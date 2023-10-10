
##curl -X POST --form tag=SB_TAG-PYLD --form since=100 --form file=@/tmp/test.txt --form endtime=0 'http://localhost:8080/crestapi/payloads/store'

generate_post_data()
{
  cat <<EOF
{
  "size": 2,
  "datatype": "PYL",
  "format": "StoreSetDto",
  "page": null,
  "filter": null,
  "resources":[
  { "since" : $since1, "data": "file:///tmp/newfile-01.txt", "streamerInfo": "{\"key\": \"value\", \"author\": \"formica\"}"},
  { "since" : $since2, "data": "file:///tmp/newfile-02.txt", "streamerInfo": "{\"key\": \"value\", \"author\": \"formica\"}"}
  ]
};type=application/json
EOF
}

function get_rndm_pyld() {
##  dd if=/dev/urandom of=/tmp/newfile.stuff bs=1m count=10
  dd if=/dev/urandom of=/tmp/newfile.stuff bs=2048 count=1
}

function get_rndm_string() {
  pstr=`dd if=/dev/urandom bs=4096 count=4000 | base64`
  echo $pstr
}

generate_string_post_data()
{
  cat <<EOF
{
  "size": 1,
  "datatype": "PYL",
  "format": "StoreSetDto",
  "page": null,
  "filter": null,
  "resources":[
  { "since" : $since1, "data": "$(get_rndm_string)", "streamerInfo": "{\"key\": \"value\", \"author\": \"formica\"}"}
  ]
};type=application/json
EOF
}


host=$1
apiname=$2
tag=$3

get_rndm_pyld

for a in {2001..2001}; do echo $a;
  b=$((a + 1000))
  echo "another blob 1 is $a" > /tmp/newfile-01.txt
  echo "another blob 2 is $b" > /tmp/newfile-02.txt
  cat /tmp/newfile.stuff >> /tmp/newfile-01.txt
  cat /tmp/newfile.stuff >> /tmp/newfile-02.txt

  since1=$a
  since2=$b
  echo $(generate_post_data)
  echo $(generate_string_post_data) > store.json

#  resp=`curl --form tag=$tag --form endtime=0 --form storeset="$(generate_post_data)"  --form "files=@/tmp/newfile-01.txt" --form "files=@/tmp/newfile-02.txt" --form objectType="JSON" --form version="1.0" "${host}/${apiname}/payloads"`
  resp=`curl -H "Accept: application/json" -H "Content-Type: multipart/form-data" -H "X-Crest-PayloadFormat: JSON" --form tag=$tag --form endtime=0 --form storeset=@store.json --form objectType="JSON" --form version="1.0" "${host}/${apiname}/payloads"`

  echo "Received response $resp"
  sleep 1
done
