import requests
import json

host = "http://example.com"  # Replace with your CREST_SERVER_PATH

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

def create_tag(name, description):
    print(f"Creating tag with name: {name} and description: {description}")
    post_data = generate_tag_data(name, description)
    print(f"Use data {post_data}")
    response = requests.post(f"{host}/tags", headers={"Accept": "application/json", "Content-Type": "application/json"}, data=post_data)
    print(f"Received response {response.text}")

def create_gtag(name, description):
    print(f"Creating global tag with name: {name} and description: {description}")
    post_data = generate_gtag_data(name, description)
    print(f"Use data {post_data}")
    response = requests.post(f"{host}/globaltags", headers={"Accept": "application/json", "Content-Type": "application/json"}, data=post_data)
    print(f"Received response {response.text}")

def link_tag2gtag(tag_name, gtag_name, record, label):
    print(f"Linking tag '{tag_name}' to global tag '{gtag_name}'")
    post_data = generate_link_data(gtag_name, tag_name, record, label)
    print(f"Use data {post_data}")
    response = requests.post(f"{host}/globaltagmaps", headers={"Accept": "application/json", "Content-Type": "application/json"}, data=post_data)

def store_data(name, since, path, filename):
    print(f"Storing data with name: {name}, since: {since}, and filename: {filename}")
    post_data = generate_payload_data(name, since, path, filename)
    print(f"Use data {post_data}")
    with open(f"{name}_{since}_store.json", "w") as file:
        file.write(post_data)
    response = requests.put(f"{host}/payloads", headers={"Accept": "application/json", "Content-Type": "multipart/form-data"}, data={"tag": name, "endtime": 0, "version": "1.0"}, files={"storeset": (f"{name}_{since}_store.json", open(f"{name}_{since}_store.json", "rb")), "objectType": "JSON"})
    print(f"Received response {response.text}")

def trace_tags(name):
    print(f"Trace tags in global tag: {name}")
    response = requests.get(f"{host}/globaltagmaps/{name}", headers={"Accept": "application/json", "Content-Type": "application/json"})
    print(response.text)

def list_iovs(name):
    print(f"List IOVs in tag: {name}")
    response = requests.get(f"{host}/iovs?tagname={name}", headers={"Accept": "application/json", "Content-Type": "application/json"})
    print(response.text)

def get_data(hash):
    print(f"Get data with hash: {hash}")
    response = requests.get(f"{host}/payloads/{hash}")
    print(response.text)

# Main script
import sys

if len(sys.argv) < 2:
    print("Usage: python script.py [function] [arguments]")
    print("Available functions:")
    print("  create_tag [name] [description]")
    print("  create_gtag [name] [description]")
    print("  link_tag2gtag [tag_name] [gtag_name] [record] [label]")
    print("  store_data [tag_name] [since] [filepath] [filename]")
    print("  trace_tags [globaltag_name]")
    print("  list_iovs [tag_name]")
    print("  get_data [hash]")
    sys.exit(1)

function_name = sys.argv[1]
arguments = sys.argv[2:]

if function_name == "create_tag":
    create_tag(*arguments)
elif function_name == "create_gtag":
    create_gtag(*arguments)
elif function_name == "link_tag2gtag":
    link_tag2gtag(*arguments)
elif function_name == "store_data":
    store_data(*arguments)
elif function_name == "trace_tags":
    trace_tags(*arguments)
elif function_name == "list_iovs":
    list_iovs(*arguments)
elif function_name == "get_data":
    get_data(*arguments)
else:
    print(f"Invalid function: {function_name}")
    sys.exit(1)
