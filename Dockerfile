# CrestDB
FROM registry.cern.ch/docker.io/eclipse-temurin:23-alpine
LABEL maintainer="Andrea Formica"

ARG USER_ID=208
ARG GROUP_ID=208

# ===== Environment Variables =====
ENV USR=crestsvc \
    TZ=GMT

# Predefine paths (easier to modify)
ENV HOME=/home/${USR} \
    crest_dir=${HOME}/crest \
    data_dir=${HOME}/data \
    config_dir=${HOME}/config

# ===== User & Group Setup =====
# Create group and user in a single layer
RUN addgroup -g $GROUP_ID crest && \
    adduser -u $USER_ID -G crest -h $HOME -D $USR && \
    # Create all required directories in one RUN
    mkdir -p ${crest_dir} ${config_dir} \
             ${data_dir}/web ${data_dir}/dump ${data_dir}/logs && \
    chown -R $USR:$GROUP_ID $HOME

# ===== Application Setup =====
# Add jar and entrypoint in separate layers (better caching)
ADD build/libs/crest.jar ${crest_dir}/crest.jar
COPY entrypoint.sh $HOME/

# ===== Permissions & Runtime Config =====
RUN chmod +x $HOME/entrypoint.sh && \
    chown $USR:$GROUP_ID $HOME/entrypoint.sh

### we export only 1 directories....
VOLUME "${data_dir}"
EXPOSE 8080
### we set the user and the workdir....
USER ${USR}
WORKDIR /home/${USR}

ENTRYPOINT  [ "./entrypoint.sh" ]
