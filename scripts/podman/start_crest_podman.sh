#!/usr/bin/env bash
# --- Start a local CREST server on lxplus (podman or JAR) ---
#
# Starts CREST with H2 in-memory database — no Oracle privileges needed.
# Uses Andrea Formica's official container image (recommended).
#
# Usage:
#   ./start_local_crest.sh              # start on port 8080 (background)
#   ./start_local_crest.sh 9090         # start on custom port
#   ./start_local_crest.sh --fg         # start in foreground (see logs live)
#   ./start_local_crest.sh --stop       # stop the running container
#   ./start_local_crest.sh --logs       # tail container logs
#   ./start_local_crest.sh --status     # check if server is running
#
# After starting, use the local server:
#   source setup_crest.sh local
#   python3 create_hgtd_payload.py --create
#
# Data lives in memory only — it resets on container restart.
# This is fine for exercises and development testing.
#
set -euo pipefail

CREST_PORT="${1:-8080}"
CONTAINER_NAME="crest-local"
# Set the image version here; update as needed when new versions are released.
# You can set the environment variable CREST_CONTAINER_VERSION, if not existing a default value 6.4.0 will be used.
CONTAINER_VERSION="${CREST_CONTAINER_VERSION:-6.4.0}"
CREST_IMAGE="registry.cern.ch/crest/crest21-service:${CONTAINER_VERSION}"
# The CREST_API_VERSION is added to the path. It uses the first 2 digits of CONTAINER_VERSION, e.g. 6.4.0 -> 6.4
CREST_API_VERSION="$(echo "${CONTAINER_VERSION}" | cut -d. -f1,2)"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CONFIG_DIR="${SCRIPT_DIR}/crest_podman"
HOST_DATA_DIR="/tmp/${USER}/crest-data"

# --- Helpers ---

_wait_for_health() {
    local port="$1"
    local health_url="http://localhost:${port}/mgmt/health"
    local max_wait=90
    local waited=0

    echo "Waiting for CREST server to start (up to ${max_wait}s)..."
    while [ "${waited}" -lt "${max_wait}" ]; do
        if curl -sf --connect-timeout 2 "${health_url}" >/dev/null 2>&1; then
            echo ""
            echo "========================================================"
            echo "  CREST server is READY"
            echo "========================================================"
            echo ""
            echo "  API URL:     http://localhost:${port}/api-v${CREST_API_VERSION}"
            echo "  Health:  ${health_url}"
            echo "  Logs:    podman logs -f ${CONTAINER_NAME}"
            echo ""
            echo "To use it in your exercises (e.g.):"
            echo "  curl http://localhost:${port}/api-v${CREST_API_VERSION}/tags"
            echo ""
            echo "To stop the server:"
            echo "  $0 --stop"
            return 0
        fi

        # Check if container died
        if ! podman inspect --format '{{.State.Status}}' "${CONTAINER_NAME}" 2>/dev/null | grep -q "running"; then
            echo ""
            echo "ERROR: Container stopped unexpectedly. Check logs:"
            echo "  podman logs ${CONTAINER_NAME}"
            return 1
        fi

        sleep 3
        waited=$((waited + 3))
        printf "."
    done

    echo ""
    echo "WARNING: Server did not respond within ${max_wait}s."
    echo "It may still be starting. Check:"
    echo "  podman logs -f ${CONTAINER_NAME}"
    echo "  curl http://localhost:${port}/mgmt/health"
    return 1
}

# --- Stop mode ---
if [ "${1:-}" = "--stop" ]; then
    if podman inspect "${CONTAINER_NAME}" >/dev/null 2>&1; then
        echo "Stopping container ${CONTAINER_NAME}..."
        podman stop "${CONTAINER_NAME}" 2>/dev/null || true
        podman rm "${CONTAINER_NAME}" 2>/dev/null || true
        echo "Stopped."
    else
        echo "Container ${CONTAINER_NAME} is not running."
    fi
    exit 0
fi

# --- Logs mode ---
if [ "${1:-}" = "--logs" ]; then
    podman logs -f "${CONTAINER_NAME}"
    exit 0
fi

# --- Status mode ---
if [ "${1:-}" = "--status" ]; then
    if podman inspect "${CONTAINER_NAME}" >/dev/null 2>&1; then
        status="$(podman inspect --format '{{.State.Status}}' "${CONTAINER_NAME}")"
        echo "Container ${CONTAINER_NAME}: ${status}"
        if [ "${status}" = "running" ]; then
            echo "  API: http://localhost:${CREST_PORT}/api-v6.0"
            echo "  Health: $(curl -sf "http://localhost:${CREST_PORT}/mgmt/health" 2>/dev/null | head -c 200 || echo 'not responding')"
        fi
    else
        echo "Container ${CONTAINER_NAME} does not exist."
    fi
    exit 0
fi

# --- Foreground flag ---
FOREGROUND=false
if [ "${1:-}" = "--fg" ]; then
    FOREGROUND=true
    CREST_PORT="${2:-8080}"
fi

# --- Check if already running ---
if podman inspect "${CONTAINER_NAME}" >/dev/null 2>&1; then
    status="$(podman inspect --format '{{.State.Status}}' "${CONTAINER_NAME}")"
    if [ "${status}" = "running" ]; then
        echo "CREST server already running as container '${CONTAINER_NAME}'."
        echo "  API: http://localhost:${CREST_PORT}/api-v${CREST_API_VERSION}"
        echo ""
        echo "To stop it:  $0 --stop"
        echo "To see logs: $0 --logs"
        exit 0
    else
        echo "Removing stale container '${CONTAINER_NAME}' (status: ${status})..."
        podman rm "${CONTAINER_NAME}" 2>/dev/null || true
    fi
fi

# --- Create host data directory ---
mkdir -p "${HOST_DATA_DIR}/dump" "${HOST_DATA_DIR}/logs" "${HOST_DATA_DIR}/web"

# --- Pull image if not present ---
if ! podman image inspect "${CREST_IMAGE}" >/dev/null 2>&1; then
    echo "Pulling CREST image (first run, may take a minute)..."
    echo "  ${CREST_IMAGE}"
    # Need CERN GitLab registry login — check if already logged in
    if ! podman pull "${CREST_IMAGE}" 2>/dev/null; then
        echo ""
        echo "Pull failed. You may need to log in to the CERN registry first:"
        echo "  podman login registry.cern.ch"
        echo "  (use your CERN username and password or SSO)"
        echo ""
        echo "Then re-run this script."
        exit 1
    fi
fi

# --- Build the podman run command ---
PODMAN_ARGS=(
    "--name" "${CONTAINER_NAME}"
    "-p" "${CREST_PORT}:8080"
    "-v" "${HOST_DATA_DIR}:/home/crestsvc/data:Z"
    # Pass all config as environment variables — no config files needed.
    # This avoids EOS/FUSE mount issues entirely (docker-compose.yml uses same approach).
    "-e" "SPRING_PROFILES_ACTIVE=default,nosecurity"
    "-e" "CREST_API_NAME=/api-v${CREST_API_VERSION}"
    "-e" "CREST_SERVER_PORT=8080"
    "-e" "CREST_SERVER_SECURITY=none"
    "-e" "CREST_DUMP_DIR=/home/crestsvc/data/dump"
    "-e" "CREST_LOG_DIR=/home/crestsvc/data/logs"
    "-e" "MANAGEMENT_ENDPOINTS_WEB_BASE_PATH=/mgmt"
    "-e" "MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info,metrics"
    "-e" "MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS=always"
    # Disable Redis — not available on lxplus nodes; the default profile tries to
    # connect to localhost:6379 for caching, causing health check to report DOWN.
    "-e" "MANAGEMENT_HEALTH_REDIS_ENABLED=false"
    "-e" "SPRING_AUTOCONFIGURE_EXCLUDE=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration"
)

echo "Starting CREST server..."
echo "  Image:   ${CREST_IMAGE}"
echo "  Port:    ${CREST_PORT} -> 8080"
echo "  Config:  ${CONFIG_DIR}/"
echo "  Data:    ${HOST_DATA_DIR}/"
echo ""

if [ "${FOREGROUND}" = "true" ]; then
    echo "(running in foreground — Ctrl+C to stop)"
    exec podman run --rm "${PODMAN_ARGS[@]}" "${CREST_IMAGE}"
else
    # Background mode — use -d
    podman run -d "${PODMAN_ARGS[@]}" "${CREST_IMAGE}"
    _wait_for_health "${CREST_PORT}"
fi
