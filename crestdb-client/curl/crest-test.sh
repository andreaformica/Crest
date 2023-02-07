function json_data() {
  cat <<EOF
{ "" }
EOF
}

generate_globaltag_data()
{
  cat <<EOF
{
  "description": "A new global tag for testing",
  "validity": -1.0,
  "name": "$1",
  "release": "v1",
  "snapshotTime": 0,
  "scenario": "data challenge",
  "workflow": "some wf",
  "type": "test"
}
EOF
}

generate_tagmeta_data()
{
  cat <<EOF
{
   "description": "A new tag meta for testing",
   "chansize": 1,
   "colsize": 1,
   "tagName": "$1",
   "tagInfo": "{some json}",
   "insertionTime": "2021-01-01T10:12:13+0000"
}
EOF
}

generate_tag_data()
{
  cat <<EOF
{
  "description": "A new tag for testing",
   "endOfValidity": -1.0,
   "lastValidatedTime": -1.0,
   "name": "$1",
   "payloadSpec": "JSON",
   "synchronization": "any",
   "timeType": "time"
}
EOF
}

generate_mapping_data()
{
  cat <<EOF
{
  "tagName": "$1",
   "globalTagName": "$2",
   "record": "$3",
   "label": "$4"
}
EOF
}

function get_data() {
  echo "Execute $1 : get data of type $2 from server using search $3"
  if [ ${token} == "" ]; then
     resp=`curl -X GET ${http_proxy} -H "Accept: application/json" -H "Content-Type: application/json" "${host}/${apiname}/$2?$3"`
  else
     resp=`curl -X GET ${http_proxy} -H "Authorization: Bearer ${token}" -H "Accept: application/json" -H "Content-Type: application/json" "${host}/${apiname}/$2?$3"`
  fi  
  echo "Received response "
  echo $resp | json_pp
}

function post_data() {
  echo "Using token ${token}"
  pdata=$2
  if [ "${token}" == "" ]; then
     echo "Request: curl -X POST -H \"Accept: application/json\" -H \"Content-Type: application/json\" \"${host}/${apiname}/$1\" --data \"${pdata}\""
     resp=`curl -X POST ${http_proxy} -H "X-Crest-PayloadFormat: FILE" -H "Accept: application/json" -H "Content-Type: application/json" "${host}/${apiname}/$1" --data "${pdata}"`
  else
     echo "Request: curl -X POST -H \"Authorization: Bearer ${token}\" -H \"Accept: application/json\" -H \"Content-Type: application/json\" \"${host}/${apiname}/$1\" --data \"${pdata}\""
     resp=`curl -X POST ${http_proxy} -H "Authorization: Bearer ${token}" -H "Accept: application/json" -H "Content-Type: application/json" "${host}/${apiname}/$1" --data "${pdata}"`
  fi
  echo "Received response $resp"
  echo $resp | json_pp
}

function get_rndm_pyld() {
  rndfile=$1
  echo "generate 1MB of random payload data in file $rndfile"
  dd if=/dev/urandom of=$rndfile bs=1m count=1
}

generate_multi_upload_data()
{
  cat <<EOF
{
    "size": 2,
    "datatype" : "payloads",
    "format": "StoreSetDto",
    "resources":[
    { "hash": "", "since" : $since1, "data": "file:///tmp/test-01.txt", "streamerInfo": "rnd gen" },
    { "hash": "", "since" : $since2, "data": "file:///tmp/test-02.txt", "streamerInfo": "rnd gen" }
    ]

}
EOF
}
function create_tag() {
  echo "Execute $1 : create tag $2 "
  tdata="$(generate_tag_data  $2)"
  echo "Upload ${tdata}"
  post_data "tags" "$tdata"
}
function create_tagmeta() {
  echo "Execute $1 : create tag meta $2 "
  tdata="$(generate_tagmeta_data  $2)"
  echo "Upload ${tdata}"
  post_data "tags/$2/meta" "$tdata"
}
function create_globaltag() {
  echo "Execute $1 : create globaltag $2 "
  tdata="$(generate_globaltag_data  $2)"
  echo "Upload ${tdata}"
  post_data "globaltags" "$tdata"
}

function map_tag_to_gtag() {
  echo "Execute $1 : add tag $2 to global tag $3 "
  echo "Add mapping using record $4 and label $5"
  tdata="$(generate_mapping_data $2 $3 $4 $5)"
  post_data "globaltagmaps" "$tdata"
}

function multi_upload() {
  echo "Execute $1 : add data to tag $2 using an iov interval $3 to $4"
  tag=$2
  ST=$3
  END=$4

  get_rndm_pyld "/tmp/test-01.txt"
  get_rndm_pyld "/tmp/test-02.txt"
  for a in $(seq $ST 1 $END); do
    echo "generate insert for since $a";
    b=$((a + 1000))
    echo "another blob 1 is $a" >> /tmp/test-01.txt
    echo "another blob 2 is $b" >> /tmp/test-02.txt
    since1=$a
    since2=$b
    echo $(generate_multi_upload_data $tag)
    echo "Using token ${token}"
    sset=$(generate_multi_upload_data)
    echo "Using tag ${tag}, storeset ${sset}"
    if [ "${token}" == "" ]; then
      resp=`curl -X POST ${http_proxy} --form "tag=${tag}" --form compressionType="none" --form version="v1" --form endtime=0 --form "storeset=${sset}"  --form "files=@/tmp/test-01.txt" --form "files=@/tmp/test-02.txt"  "${host}/${apiname}/payloads"`
    else
      resp=`curl -X POST ${http_proxy} -H "Authorization: Bearer ${token}" --form "tag=${tag}" --form endtime=0 --form "iovsetupload=${iovset}"  --form "files=@/tmp/test-01.txt" --form "files=@/tmp/test-02.txt"  "${host}/${apiname}/payloads/batch"`
    fi
      echo "Post returned : $resp"
      #sleep 1
  done
}

###
# Main script
host=$1
apiname=$2
echo "Use host = $host apiname $apiname"
echo "Execute $3"

#### this section should be uncommented for AUTH with keycloak
##token="${ACCESS_TOKEN}"
token=

## you can set http_proxy if you need a socks proxy
export http_proxy="--socks5 localhost:3129"
http_proxy=

if [ "$host" == "help" ]; then
  echo "$0 <host> <apiname> <command>"
  echo "Use commands: get_data, create_tag, multi_upload.. "
  echo "get_data: <type> <search pattern>"
  echo "multi_upload: <tag> <iov-start> <iov-stop>"
  echo "create_tag: <tag>"
  echo "create_tagmeta: <tag>"
  echo "create_globaltag: <gtag>"
  echo "map_tag_to_gtag: <tag> <gtag> <record> <label>"
elif [[ "x$3" == "x" ]]; then
  echo "use arg help to get help."
else
  $3 "${@:3}"
fi
