prog=$1
# set url: http://localhost:8090
url=$2

######## functions


generate_post_data()
{
  cat <<EOF
{
  "hash" : "somehash",
  "version" : "my-obj-v0",
  "objectType": "test-obj",
  "data" : "ZW5jb2RlZGI2NA==",
  "streamerInfo" : "ZW5jb2RlZHN0cmVhbWVy",
  "size" : 10
}
EOF
}

generate_multipost_data()
{
  cat <<EOF
"
{
  \"size\": 2,
  \"datatype\": \"PYL\",
  \"format\": \"IovSetDto\",
  \"resources\":[
  { \"since\" : $since1, \"payloadHash\": \"file:///tmp/newfile-01.txt\"},
  { \"since\" : $since2, \"payloadHash\": \"file:///tmp/newfile-02.txt\"}
  ]
}"
EOF
}

function get_rndm_pyld() {
  dd if=/dev/urandom of=/tmp/newfile.stuff bs=1m count=5
}

function upload_multi_payload() {

  prog=$1
  url=$2
  tag=$3

  get_rndm_pyld

  for a in {1001..1010}; do echo $a;
    b=$((a + 1000))
    echo "another blob 1 is $a" > /tmp/newfile-01.txt
    echo "another blob 2 is $b" > /tmp/newfile-02.txt
    cat /tmp/newfile.stuff >> /tmp/newfile-01.txt
    cat /tmp/newfile.stuff >> /tmp/newfile-02.txt

    since1=$a
    since2=$b
    echo $(generate_multipost_data)
    resp=`$prog --form tag=$tag --form endtime=0 --form iovsetupload="$(generate_multipost_data)" --form "files=@/tmp/newfile-01.txt" --form "files=@/tmp/newfile-02.txt"  --host $url -ct application/json -ac application/json uploadPayloadBatchWithIovMultiForm`
    echo "Received response $resp"
    sleep 1
  done
}

########

## Create 2 tagd
$prog --host $url -ct application/json -ac application/json createTag \
  name==LAR-TEST-02 timeType==time description=="test for LAR" endOfValidity==0 lastValidatedTime==0 payloadSpec==json synchronization==all

$prog --host $url -ct application/json -ac application/json createTag \
  name==MDT-TEST-02 timeType==time description=="test for MDT" endOfValidity==0 lastValidatedTime==0 payloadSpec==json synchronization==all

## Create a global tag
$prog --host $url -ct application/json -ac application/json createGlobalTag \
  name==GT-TEST-01 validity==0 description==test-global-tag release==v1.0 snapshotTimeMilli==0 scenario==all workflow==blk type==A

## Map the 2 tags to the global tag
$prog --host $url -ct application/json -ac application/json createGlobalTagMap \
  globalTagName==GT-TEST-01 tagName==LAR-TEST-02 record==good label==LAR
$prog --host $url -ct application/json -ac application/json createGlobalTagMap \
  globalTagName==GT-TEST-01 tagName==MDT-TEST-02 record==good label==MDT

## Find mappings for the global tag
$prog --host $url -ct application/json -ac application/json findGlobalTagFetchTags name=GT-TEST-01

upload_multi_payload $prog $url MDT-TEST-02

$prog --host $url -ct application/json -ac application/json findAllIovs by=tagname:MDT-TEST-02
$prog --host $url -ct application/json -ac application/json selectIovs tagname=MDT-TEST-02