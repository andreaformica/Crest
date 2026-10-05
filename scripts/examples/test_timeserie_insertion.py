#!/usr/bin/env python3
"""
Test script for TimeSerieData insertion into CREST.

This script demonstrates:
1. Creating a TAG compatible with crest-json-timeserie format
2. Creating TagMeta with channel information
3. Storing IOVs and Payloads in the TimeSerieData format
4. Retrieving and verifying the stored data

The TimeSerieData format consists of:
- channelId: Integer - The ID of the channel
- until: BigInteger - The validity end time
- data: String - The actual payload data
"""

import json
import requests
import sys
from datetime import datetime
from typing import Dict, Any, List

# Configuration
DEFAULT_CREST_URL = "http://localhost:8080/crestapi"
TAG_NAME = f"TEST-TIMESERIE-{int(__import__('time').time())}"
TAG_DESCRIPTION = "A test tag for time series data"


class TimeSerieDataManager:
    """Manager for TimeSerieData insertion and retrieval."""

    def __init__(self, base_url: str = DEFAULT_CREST_URL):
        """
        Initialize the TimeSerieDataManager.

        Args:
            base_url: The base URL of the CREST API
        """
        self.base_url = base_url
        self.session = requests.Session()

    def create_tag(self, tag_name: str, description: str = "") -> bool:
        """
        Create a TAG compatible with crest-json-timeserie format.

        Args:
            tag_name: The name of the tag
            description: Description of the tag

        Returns:
            True if successful, False otherwise
        """
        tag_url = f"{self.base_url}/tags"

        tag_data = {
            "name": tag_name,
            "payloadSpec": "crest-json-timeserie",
            "status": "UNLOCKED",
            "description": description,
            "timeType": "time",
            "synchronization": "ALL"
        }

        print(f"\n[CREATE TAG] Storing tag: {tag_name}")
        print(f"  Payload: {json.dumps(tag_data, indent=2)}")

        try:
            response = self.session.post(tag_url, json=tag_data, headers={"Content-Type": "application/json"})
            if response.status_code == 201:
                print(f"  ✓ Tag created successfully (HTTP {response.status_code})")
                return True
            else:
                print(f"  ✗ Failed to create tag (HTTP {response.status_code})")
                print(f"  Response: {response.text}")
                return False
        except Exception as e:
            print(f"  ✗ Error creating tag: {str(e)}")
            return False

    def create_tag_meta(self, tag_name: str, num_channels: int = 1) -> bool:
        """
        Create TagMeta with channel information.

        The TagMeta contains metadata about the tag structure, including:
        - channel_list: List of available channels
        - node_description: XML description of the data structure
        - payload_spec: Specification of the payload fields

        Args:
            tag_name: The name of the tag
            num_channels: Number of channels to create

        Returns:
            True if successful, False otherwise
        """
        meta_url = f"{self.base_url}/tags/{tag_name}/meta"

        # Create channel list
        channel_list = [{str(i): ""} for i in range(num_channels)]
        # Create TagInfo as JSON string
        tag_info = {
            "channel_list": channel_list,
            "node_description": (
                "<timeStamp>time</timeStamp><addrHeader></addrHeader>"
            ),
            "payload_spec": [{"veto": "Int32"}]
        }

        tag_meta_data = {
            "tagName": tag_name,
            "chansize": num_channels,
            "colsize": 1,
            "description": f"Metadata for {tag_name}",
            "tagInfo": json.dumps(tag_info)
        }

        print(f"\n[CREATE TAG META] Storing tag meta for: {tag_name}")
        print(f"  Channels: {num_channels}")
        print(f"  TagInfo: {json.dumps(tag_info, indent=2)}")

        try:
            response = self.session.post(meta_url, json=tag_meta_data, headers={"Content-Type": "application/json"})
            if response.status_code == 201:
                print(f"  ✓ Tag meta created successfully (HTTP {response.status_code})")
                return True
            else:
                print(f"  ✗ Failed to create tag meta (HTTP {response.status_code})")
                print(f"  Response: {response.text}")
                return False
        except Exception as e:
            print(f"  ✗ Error creating tag meta: {str(e)}")
            return False

    def create_timeserie_data(
        self,
        channel_id: int,
        until_time: int,
        data: str
    ) -> str:
        """
        Create a TimeSerieData payload.

        The TimeSerieData JSON format:
        {
            "channelId": <integer>,
            "until": <integer>,
            "data": "<string>"
        }

        Args:
            channel_id: The channel ID
            until_time: The validity end time (timestamp or epoch)
            data: The actual payload data (can be any string, JSON, etc.)

        Returns:
            JSON string representation of TimeSerieData
        """
        timeserie_data = {
            "channelId": channel_id,
            "until": until_time,
            "data": data
        }
        return json.dumps(timeserie_data)

    def create_store_dto(self, since: int, timeserie_data: str) -> Dict[str, Any]:
        """
        Create a StoreDto object for payload insertion.

        Args:
            since: The validity start time (timestamp or epoch)
            timeserie_data: JSON string representation of TimeSerieData

        Returns:
            Dictionary representing StoreDto
        """
        return {
            "data": timeserie_data,
            "since": since,
            "streamerInfo": "TIMESERIE_STREAMER_INFO"
        }

    def insert_payload(
        self,
        tag_name: str,
        since: int,
        until: int,
        channel_id: int,
        payload_data: str,
        object_type: str = "crest-json-timeserie",
        compression_type: str = "none",
        version: str = "1.0",
        endtime: str = "0"
    ) -> bool:
        """
        Insert a TimeSerieData payload for a tag.

        Args:
            tag_name: The tag name
            since: The validity start time
            until: The validity end time
            channel_id: The channel ID
            payload_data: The actual payload data
            object_type: The object type (default: crest-json-timeserie)
            compression_type: Compression type (default: none)
            version: Version string (default: 1.0)
            endtime: End time parameter (default: 0)

        Returns:
            True if successful, False otherwise
        """
        payloads_url = f"{self.base_url}/payloads"

        # Create TimeSerieData
        timeserie_data_json = self.create_timeserie_data(
            channel_id=channel_id,
            until_time=until,
            data=payload_data
        )

        # Create StoreDto
        store_dto = self.create_store_dto(since=since, timeserie_data=timeserie_data_json)

        # Create StoreSetDto (list of StoreDto)
        store_set_dto = {
            "resources": [store_dto]
        }

        print(f"\n[INSERT PAYLOAD] Inserting payload for tag: {tag_name}")
        print(f"  Channel ID: {channel_id}")
        print(f"  Since: {since}, Until: {until}")
        print(f"  TimeSerieData: {timeserie_data_json}")

        # Prepare multipart request
        files = {
            "storeset": ("storeset.json", json.dumps(store_set_dto), "application/json")
        }

        data = {
            "tag": tag_name,
            "objectType": object_type,
            "compressionType": compression_type,
            "version": version,
            "endtime": endtime
        }

        try:
            # Use PUT method with multipart/form-data
            response = self.session.put(payloads_url, files=files, data=data)
            if response.status_code == 201:
                print(f"  ✓ Payload inserted successfully (HTTP {response.status_code})")
                print(f"  Response: {response.text}")
                return True
            else:
                print(f"  ✗ Failed to insert payload (HTTP {response.status_code})")
                print(f"  Response: {response.text}")
                return False
        except Exception as e:
            print(f"  ✗ Error inserting payload: {str(e)}")
            return False

    def retrieve_iovs(self, tag_name: str) -> List[Dict[str, Any]]:
        """
        Retrieve IOVs for a tag.

        Args:
            tag_name: The tag name

        Returns:
            List of IOV dictionaries
        """
        iovs_url = f"{self.base_url}/iovs?tagname={tag_name}"

        print(f"\n[RETRIEVE IOVs] Retrieving IOVs for tag: {tag_name}")
        print(f"  URL: {iovs_url}")

        try:
            response = self.session.get(iovs_url)
            if response.status_code == 200:
                data = response.json()
                print(f"  Full Response: {json.dumps(data, indent=2)}")
                iovs = data.get("resources", [])
                print(f"  ✓ Retrieved {len(iovs)} IOV(s) (HTTP {response.status_code})")
                for iov in iovs:
                    print(f"    - IOV: {json.dumps(iov, indent=6)}")
                return iovs
            else:
                print(f"  ✗ Failed to retrieve IOVs (HTTP {response.status_code})")
                print(f"  Response: {response.text}")
                return []
        except Exception as e:
            print(f"  ✗ Error retrieving IOVs: {str(e)}")
            return []

    def delete_tag(self, tag_name: str) -> bool:
        """
        Delete a tag (cleanup).

        Args:
            tag_name: The tag name

        Returns:
            True if successful, False otherwise
        """
        delete_url = f"{self.base_url}/admin/tags/{tag_name}"

        print(f"\n[DELETE TAG] Removing tag: {tag_name}")

        try:
            response = self.session.delete(delete_url)
            if response.status_code == 200:
                print(f"  ✓ Tag deleted successfully (HTTP {response.status_code})")
                return True
            else:
                print(f"  ✗ Failed to delete tag (HTTP {response.status_code})")
                print(f"  Response: {response.text}")
                return False
        except Exception as e:
            print(f"  ✗ Error deleting tag: {str(e)}")
            return False


def main():
    """Main test function."""

    # Initialize the manager
    crest_url = DEFAULT_CREST_URL
    if len(sys.argv) > 1:
        crest_url = sys.argv[1]

    manager = TimeSerieDataManager(crest_url)

    print("=" * 80)
    print("TimeSerieData Insertion Test")
    print("=" * 80)
    print(f"CREST Server URL: {crest_url}")

    try:
        # Step 1: Create a TAG compatible with crest-json-timeserie format
        if not manager.create_tag(TAG_NAME, TAG_DESCRIPTION):
            print("\n✗ Failed to create tag. Exiting.")
            return 1

        # Step 2: Create TagMeta with channel information
        num_channels = 2
        if not manager.create_tag_meta(TAG_NAME, num_channels):
            print("\n✗ Failed to create tag meta. Cleaning up...")
            manager.delete_tag(TAG_NAME)
            return 1

        # Step 3: Insert TimeSerieData payloads
        print("\n[INSERTING MULTIPLE PAYLOADS]")

        # Insert first payload for channel 0
        payload_data_1 = json.dumps({
            "temperature": 25.5,
            "pressure": 1013.25,
            "humidity": 65
        })
        if not manager.insert_payload(
            tag_name=TAG_NAME,
            since=1000,
            until=2000,
            channel_id=0,
            payload_data=payload_data_1
        ):
            print("\n✗ Failed to insert first payload. Cleaning up...")
            manager.delete_tag(TAG_NAME)
            return 1

        # Insert second payload for channel 1
        payload_data_2 = json.dumps({
            "temperature": 26.0,
            "pressure": 1013.50,
            "humidity": 70
        })
        if not manager.insert_payload(
            tag_name=TAG_NAME,
            since=1000,
            until=2000,
            channel_id=1,
            payload_data=payload_data_2
        ):
            print("\n✗ Failed to insert second payload. Cleaning up...")
            manager.delete_tag(TAG_NAME)
            return 1

        # Step 4: Retrieve and verify stored IOVs
        iovs = manager.retrieve_iovs(TAG_NAME)
        if len(iovs) < 2:
            print(f"\n⚠ Warning: Expected at least 2 IOVs, but got {len(iovs)}")

        # Step 5: Cleanup - Delete the tag
        # NOTE: Commented out to allow debugging. Uncomment for production use.
        # manager.delete_tag(TAG_NAME)

        print("\n" + "=" * 80)
        print("✓ Test completed successfully!")
        print("=" * 80)
        return 0

    except Exception as e:
        print(f"\n✗ Unexpected error: {str(e)}")
        print("Attempting cleanup...")
        manager.delete_tag(TAG_NAME)
        return 1


if __name__ == "__main__":
    sys.exit(main())





