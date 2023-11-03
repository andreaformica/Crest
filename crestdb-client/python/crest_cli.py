import requests
import json
import argparse

DEFAULT_HOST = "http://crest-undertow-api.web.cern.ch/api-v4.0"
mproxy = {}
host = DEFAULT_HOST

def generate_tag_data(tag_name, tag_description):
    data = {
        "description": tag_description,
        "endOfValidity": -1.0,
        "lastValidatedTime": -1.0,
        "name": tag_name,
        "payloadSpec": "JSON",
        "synchronization": "any",
        "timeType": "time"
    }
    return json.dumps(data)

def generate_gtag_data(gtag_name, gtag_description):
    data = {
        "description": gtag_description,
        "validity": -1.0,
        "name": gtag_name,
        "release": "v1",
        "scenario": "undefined",
        "workflow": "undefined",
        "type": "U"
    }
    return json.dumps(data)

def generate_link_data(gtag_name, tag_name, record, label):
    data = {
        "globalTagName": gtag_name,
        "tagName": tag_name,
        "record": record,
        "label": label
    }
    return json.dumps(data)

def generate_payload_data(tag_name, since, fpath, filename):
    with open(f"{fpath}/{filename}", "r") as file:
        pyld_data = file.read()
    data = {
        "size": 1,
        "datatype": "iovs",
        "format": "StoreSetDto",
        "page": None,
        "filter": None,
        "resources": [
            {"since": since, "data": pyld_data, "streamerInfo": {"filename": filename}}
        ]
    }
    return json.dumps(data)

def create_tag(args, host, mproxy):
    name = args.name
    description = args.description
    print(f"Creating tag with name: {name} and description: {description}")
    post_data = generate_tag_data(name, description)
    print(f"Use data {post_data}")
    response = requests.post(f"{host}/tags", headers={"Accept": "application/json", "Content-Type": "application/json"}, data=post_data, proxies=mproxy)
    print(f"Received response {response.text}")

def create_gtag(args, host, mproxy):
    name = args.name
    description = args.description
    print(f"Creating global tag with name: {name} and description: {description}")
    post_data = generate_gtag_data(name, description)
    print(f"Use data {post_data}")
    response = requests.post(f"{host}/globaltags", headers={"Accept": "application/json", "Content-Type": "application/json"}, data=post_data, proxies=mproxy)
    print(f"Received response {response.text}")

def link_tag2gtag(args, host, mproxy):
    tag_name = args.tag_name
    gtag_name = args.gtag_name
    record = args.record
    label = args.label
    print(f"Linking tag '{tag_name}' to global tag '{gtag_name}'")
    post_data = generate_link_data(gtag_name, tag_name, record, label)
    print(f"Use data {post_data}")
    response = requests.post(f"{host}/globaltagmaps", headers={"Accept": "application/json", "Content-Type": "application/json"}, data=post_data, proxies=mproxy)
    print(f"Received response {response.text}")

def store_data(args, host, mproxy):
    name = args.name
    since = args.since
    path = args.path
    filename = args.filename
    print(f"Storing data with name: {name}, since: {since}, and filename: {filename}")
    post_data = generate_payload_data(name, since, path, filename)
    print(f"Use data {post_data}")
    with open(f"{name}_{since}_store.json", "w") as file:
        file.write(post_data)
    response = requests.put(f"{host}/payloads", headers={"Accept": "application/json", "Content-Type": "multipart/form-data"}, data={"tag": name, "endtime": 0, "version": "1.0"}, files={"storeset": (f"{name}_{since}_store.json", open(f"{name}_{since}_store.json", "rb")), "objectType": "JSON"}, proxies=mproxy)
    print(f"Received response {response.text}")

def trace_tags(args, host, mproxy):
    name = args.name
    print(f"Trace tags in global tag: {name}")
    response = requests.get(f"{host}/globaltagmaps/{name}", headers={"Accept": "application/json", "Content-Type": "application/json"}, proxies=mproxy)
    print(f"Output: {response.text}")

def list_globaltags(args, host, mproxy):
    name = args.name
    print(f"List GlobalTags: {name} using proxies {mproxy}")
    response = requests.get(f"{host}/globaltags?name={name}", headers={"Accept": "application/json", "Content-Type": "application/json"}, proxies=mproxy)
    print(f"Output: {response.text}")

def list_tags(args, host, mproxy):
    name = args.name
    print(f"List Tags: {name}")
    response = requests.get(f"{host}/tags?name={name}", headers={"Accept": "application/json", "Content-Type": "application/json"}, proxies=mproxy)
    print(f"Output: {response.text}")

def list_iovs(args, host, mproxy):
    name = args.name
    print(f"List IOVs in tag: {name}")
    response = requests.get(f"{host}/iovs?tagname={name}", headers={"Accept": "application/json", "Content-Type": "application/json"}, proxies=mproxy)
    print(f"Output: {response.text}")

def get_data(args, host, mproxy):
    hash = args.hash
    print(f"Get data with hash: {hash}")
    response = requests.get(f"{host}/payloads/{hash}", proxies=mproxy)
    print(f"Output: {response.text}")

def main():
    parser = argparse.ArgumentParser(description="Perform various functions with different arguments.")

    subparsers = parser.add_subparsers(dest="command")
    # Define the --host argument
    parser.add_argument("--host", default=DEFAULT_HOST, help="Base URL of the REST API")
    # Define the --proxy argument
    parser.add_argument("--proxy", help="Proxy URL for making requests: use socks5://<host>:<port> for SOCKS5 proxy")

    create_tag_parser = subparsers.add_parser("create_tag")
    create_tag_parser.add_argument("name", help="Name of the tag")
    create_tag_parser.add_argument("description", help="Description of the tag")

    create_gtag_parser = subparsers.add_parser("create_gtag")
    create_gtag_parser.add_argument("name", help="Name of the global tag")
    create_gtag_parser.add_argument("description", help="Description of the global tag")

    link_tag2gtag_parser = subparsers.add_parser("link_tag2gtag")
    link_tag2gtag_parser.add_argument("tag_name", help="Name of the tag")
    link_tag2gtag_parser.add_argument("gtag_name", help="Name of the global tag")
    link_tag2gtag_parser.add_argument("record", help="Record")
    link_tag2gtag_parser.add_argument("label", help="Label")

    store_data_parser = subparsers.add_parser("store_data")
    store_data_parser.add_argument("name", help="Name of the data")
    store_data_parser.add_argument("since", type=int, help="Since value")
    store_data_parser.add_argument("path", help="File path")
    store_data_parser.add_argument("filename", help="File name")

    trace_tags_parser = subparsers.add_parser("trace_tags")
    trace_tags_parser.add_argument("name", help="Name of the global tag")

    list_globaltags_parser = subparsers.add_parser("list_globaltags")
    list_globaltags_parser.add_argument("name", help="Name of the global tag")
    list_tags_parser = subparsers.add_parser("list_tags")
    list_tags_parser.add_argument("name", help="Name of the tag")
    list_iovs_parser = subparsers.add_parser("list_iovs")
    list_iovs_parser.add_argument("name", help="Name of the tag")

    get_data_parser = subparsers.add_parser("get_data")
    get_data_parser.add_argument("hash", help="Data hash")

    args = parser.parse_args()

    # Extract the host and proxy values
    host = args.host
    mproxy = {"http": args.proxy} if args.proxy else None
    print(f"Using host: {host}")
    print(f"Using proxy: {mproxy}")

    if args.command == "create_tag":
        create_tag(args, host, mproxy)
    elif args.command == "create_gtag":
        create_gtag(args, host, mproxy)
    elif args.command == "link_tag2gtag":
        link_tag2gtag(args, host, mproxy)
    elif args.command == "store_data":
        store_data(args, host, mproxy)
    elif args.command == "trace_tags":
        trace_tags(args, host, mproxy)
    elif args.command == "list_globaltags":
        list_globaltags(args, host, mproxy)
    elif args.command == "list_tags":
        list_tags(args, host, mproxy)
    elif args.command == "list_iovs":
        list_iovs(args, host, mproxy)
    elif args.command == "get_data":
        get_data(args, host, mproxy)
    else:
        print(f"Invalid function: {args.command}")

if __name__ == "__main__":
    main()
