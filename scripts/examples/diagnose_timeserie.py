#!/usr/bin/env python3
"""
Diagnostic script to check TimeSerieData stored in the database.
"""

import json
import requests
import sys

CREST_URL = "http://localhost:8080/api-v6.0"

def check_tag(tag_name):
    """Check if tag exists and get its details."""
    print(f"\n[CHECK TAG] Checking tag: {tag_name}")

    response = requests.get(f"{CREST_URL}/tags/{tag_name}")
    if response.status_code == 200:
        tag_data = response.json()
        print(f"  ✓ Tag found:")
        print(f"    - Name: {tag_data.get('name')}")
        print(f"    - Object Type: {tag_data.get('objectType')}")
        print(f"    - Payload Spec: {tag_data.get('payloadSpec')}")
        print(f"    - Tag ID: {tag_data.get('id')}")
        return tag_data
    else:
        print(f"  ✗ Tag not found (HTTP {response.status_code})")
        return None

def check_tag_meta(tag_name):
    """Check if tag meta exists."""
    print(f"\n[CHECK TAG META] Checking meta for tag: {tag_name}")

    response = requests.get(f"{CREST_URL}/tags/{tag_name}/meta")
    if response.status_code == 200:
        meta_data = response.json()
        print(f"  ✓ Tag meta found:")
        print(f"    - Tag Name: {meta_data.get('tagName')}")
        print(f"    - Channel Size: {meta_data.get('chansize')}")
        print(f"    - Col Size: {meta_data.get('colsize')}")
        tag_info = meta_data.get('tagInfo')
        if tag_info:
            if isinstance(tag_info, str):
                try:
                    tag_info = json.loads(tag_info)
                except:
                    pass
            print(f"    - Tag Info: {json.dumps(tag_info, indent=6)}")
        return meta_data
    else:
        print(f"  ✗ Tag meta not found (HTTP {response.status_code})")
        return None

def check_iovs_detailed(tag_name):
    """Check IOVs with different query parameters."""
    print(f"\n[CHECK IOVs] Checking IOVs for tag: {tag_name}")

    # Try different parameters
    params_list = [
        {"tagname": tag_name},
        {"tagname": tag_name, "method": "IOVS"},
        {"tagname": tag_name, "method": "IOVS", "page": "0", "size": "100"},
    ]

    for params in params_list:
        print(f"\n  Query with params: {params}")
        try:
            response = requests.get(f"{CREST_URL}/iovs", params=params)
            if response.status_code == 200:
                data = response.json()
                size = data.get('size', 0)
                total = data.get('page', {}).get('totalElements', 0) if data.get('page') else 0
                resources = data.get('resources', [])
                print(f"    - Size: {size}, Total Elements: {total}")
                print(f"    - Resources count: {len(resources)}")
                if resources:
                    print(f"    - First resource: {json.dumps(resources[0], indent=6)}")
            else:
                print(f"    - Failed (HTTP {response.status_code})")
        except Exception as e:
            print(f"    - Error: {str(e)}")

def main():
    if len(sys.argv) < 2:
        print("Usage: python diagnose_timeserie.py <tag_name>")
        sys.exit(1)

    tag_name = sys.argv[1]

    print("=" * 80)
    print(f"TimeSerieData Diagnostic for tag: {tag_name}")
    print("=" * 80)

    tag_data = check_tag(tag_name)
    meta_data = check_tag_meta(tag_name)
    check_iovs_detailed(tag_name)

    print("\n" + "=" * 80)
    print("Diagnostic complete")
    print("=" * 80)

if __name__ == "__main__":
    main()

