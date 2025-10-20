# CREST VS COOL Payload Checker
# Requires the 2 CURL commands to return the same IOV from both system

cool_curl="http://atlas-coolr-api.web.cern.ch/api/payloads?schema=ATLAS_COOLOFL_TILE&node=/TILE/OFL02/CALIB/CIS/LIN&tag=TileOfl02CalibCisLin-RUN2-HLT-UPD1-00&since=1888943796649984&until=1917896171192319"
crest_curl="http://crest-undertow-api.web.cern.ch:80/api-v4.0/payloads/d9c6411b58e89619e0ffd55324e1a4c3dacd29a285f51ca12c49f933332821b2?format=BLOB"

# Get files from both systems
curl -s -o cool.json $cool_curl
curl -s -o crest.json $crest_curl

# Check if the files are the same
cat cool.json | jq -r '.data_array[] | "\(.CHANNEL_ID) \(.TileCalibBlob)"' > cool.txt
cat crest.json | jq -r '.data| to_entries| .[]| "\(.key) \(.value[0])"' > crest.txt

cat cool.txt | sort -k 1 > cool-sorted.txt
cat crest.txt | sort -k 1 > crest-sorted.txt

echo "Comparing CREST and COOL content"
diff cool-sorted.txt crest-sorted.txt
if [ $? -ne 0 ]; then
    echo "CREST and COOL content are different"
else
    echo "CREST and COOL content are the same"
fi
echo "clean up (only json files are kept)"
rm cool.txt crest.txt cool-sorted.txt crest-sorted.txt 
