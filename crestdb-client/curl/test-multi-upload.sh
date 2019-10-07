
##curl -X POST --form tag=SB_TAG-PYLD --form since=100 --form file=@/tmp/test.txt --form endtime=0 'http://localhost:8080/crestapi/payloads/store'

generate_post_data()
{
  cat <<EOF
{
  "niovs": 2,
  "format": "PYL",
  "iovsList":[
  { "since" : $since1, "payload": "file:///tmp/test-01.txt"},
  { "since" : $since2, "payload": "file:///tmp/test-02.txt"}
  ]
}
EOF
}
host=$1
tag=$2
apiname="api"
for a in {3001..4000}; do echo $a;
  b=$((a + 1000))
  echo "another blob 1 is $a" > /tmp/test-01.txt
  echo "another blob 2 is $b" > /tmp/test-02.txt
  since1=$a
  since2=$b
  echo $(generate_post_data)
  curl --form tag="${tag}" --form endtime=0 --form iovsetupload="$(generate_post_data)"  --form "files=@/tmp/test-01.txt" --form "files=@/tmp/test-02.txt"  "${host}/${apiname}/payloads/uploadbatch"
  #sleep 1
done
