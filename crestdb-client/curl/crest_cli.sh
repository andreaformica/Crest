#!/bin/bash

host=${CREST_SERVER_PATH}

generate_tag_data()
{
  tag_name=$1
  tag_description=$2
  cat <<EOF
{
  "description": "${tag_description}",
   "endOfValidity": -1.0,
   "lastValidatedTime": -1.0,
   "name": "${tag_name}",
   "payloadSpec": "JSON",
   "synchronization": "any",
   "timeType": "time"
}
EOF
}

generate_gtag_data()
{
  gtag_name=$1
  gtag_description=$2
  cat <<EOF
{
  "description": "${gtag_description}",
  "validity": -1.0,
  "name": "${gtag_name}",
  "release": "v1",
  "scenario": "undefined",
  "workflow": "undefined",
  "type": "U"
}
EOF
}

generate_link_data()
{
  gtag_name=$1
  tag_name=$2
  record=$3
  label=$4
  cat <<EOF
{
  "globalTagName": "${gtag_name}",
  "tagName": "${tag_name}",
  "record": "${record}",
  "label": "${label}"
}
EOF
}

function get_file_string() {
  local fpath=$1
  local filename=$2
  pstr=`cat ${fpath}/${filename}`
  echo $pstr
}

generate_payload_data()
{
  tag_name=$1
  since=$2
  fpath=$3
  filename=$4
  pyld_data=$(get_file_string "$fpath" "$filename")
  cat <<EOF
{
  "size": 1,
  "datatype": "iovs",
  "format": "StoreSetDto",
  "page": null,
  "filter": null,
  "resources":[
  { "since" : $since, "data": "${pyld_data}", "streamerInfo": "{\"filename\": \"${filename}\"}"}
  ]
};type=application/json
EOF
}

# Function to create a tag
create_tag() {
  local name="$1"
  local description="$2"
  echo "Creating tag with name: $name and description: $description"
  # Add your code to create a tag here
  post_data=$(generate_tag_data "$name" "$description")
  echo "Use data $post_data"
  resp=`curl -X POST -H "Accept: application/json" -H "Content-Type: application/json" "${host}/tags" --data "${post_data}"`
  echo "Received response $resp"
}

# Function to create a global tag
create_gtag() {
  local name="$1"
  local description="$2"
  echo "Creating global tag with name: $name and description: $description"
  post_data=$(generate_gtag_data "$name" "$description")
  # Add your code to create a global tag here
  echo "Use data $post_data"
  resp=`curl -X POST -H "Accept: application/json" -H "Content-Type: application/json" "${host}/globaltags" --data "${post_data}"`
  echo "Received response $resp"
}

# Function to link a tag to a global tag
link_tag2gtag() {
  local tag_name="$1"
  local gtag_name="$2"
  local record="$3"
  local label="$4"
  echo "Linking tag '$tag_name' to global tag '$gtag_name'"
  # Add your code to link the tag to the global tag here
  post_data=$(generate_link_data "$gtag_name" "$tag_name" "$record" "$label")
  echo "Use data $post_data"
  resp=`curl -X POST -H "Accept: application/json" -H "Content-Type: application/json" "${host}/globaltagmaps" --data "${post_data}"`
}

# Function to store data
store_data() {
  local name="$1"
  local since="$2"
  local path="$3"
  local filename="$4"
  echo "Storing data with name: $name, since: $since, and filename: $filename"
  # Add your code to store data here
  post_data=$(generate_payload_data "$name" "$since" "$path" "$filename")
  echo "Use data $post_data"
  echo $post_data > ${name}_${since}_store.json
  resp=`curl -X PUT -H "Accept: application/json" -H "Content-Type: multipart/form-data" --form tag=$name --form endtime=0 --form version="1.0" --form storeset=@${name}_${since}_store.json --form objectType="JSON" "${host}/payloads"`
  echo "Received response $resp"
}


# Function to trace tags using a global tag name
trace_tags() {
  local name="$1"
  echo "Trace tags in global tag: $name"
  # Add your code to store data here
  resp=`curl -X GET -H "Accept: application/json" -H "Content-Type: application/json" "${host}/globaltagmaps/${name}"`
}

list_iovs() {
  local name="$1"
  echo "List IOVs in tag: $name"
  # Add your code to store data here
  resp=`curl -X GET -H "Accept: application/json" -H "Content-Type: application/json" "${host}/iovs?tagname=${name}"`
  echo $resp
}

get_data() {
  local hash="$1"
  echo "Get data with hash: $hash"
  # Add your code to store data here
  resp=`curl -X GET "${host}/payloads/${hash}"`
  echo $resp
}

# Main script
if [ "$#" -lt 1 ]; then
  echo "Usage: $0 [function] [arguments]"
  echo "Available functions:"
  echo "  create_tag [name] [description]"
  echo "  create_gtag [name] [description]"
  echo "  link_tag2gtag [tag_name] [gtag_name] [record] [label]"
  echo "  store_data [tag_name] [since] [filepath] [filename]"
  echo "  trace_tags [globaltag_name]"
  echo "  list_iovs [tag_name]"
  echo "  get_data [hash]"
  exit 1
fi

function_name="$1"
shift # Remove the function name from the argument list

case "$function_name" in
  "create_tag")
    create_tag "$@"
    ;;
  "create_gtag")
    create_gtag "$@"
    ;;
  "link_tag2gtag")
    link_tag2gtag "$@"
    ;;
  "store_data")
    store_data "$@"
    ;;
  "trace_tags")
    trace_tags "$@"
    ;;
  "list_iovs")
    list_iovs "$@"
    ;;
  "get_data")
    get_data "$@"
    ;;
  *)
    echo "Invalid function: $function_name"
    exit 1
    ;;
esac
