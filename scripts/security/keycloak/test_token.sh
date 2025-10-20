if [ $1 == "user" ]; then
export DIRECT_GRANT_RESPONSE=$(curl -i --request POST https://admin.svom.eu/keycloak/realms/svom-fsc/protocol/openid-connect/token --header "Accept: application/json" --header "Content-Type: application/x-www-form-urlencoded" --data "grant_type=password&username=andrea.formica@cern.ch&password=$2&client_id=FSC_PUBLIC")
else
export DIRECT_GRANT_RESPONSE=$(curl -i --request POST https://admin.svom.eu/keycloak/realms/svom-fsc/protocol/openid-connect/token --header "Accept: application/json" --header "Content-Type: application/x-www-form-urlencoded" --data "grant_type=client_credentials&client_id=$1&client_secret=$2")
fi

echo -e "\n\nSENT RESOURCE-OWNER-PASSWORD-CREDENTIALS-REQUEST. OUTPUT IS:\n\n";
echo $DIRECT_GRANT_RESPONSE;

export ACCESS_TOKEN=$(echo $DIRECT_GRANT_RESPONSE | grep "access_token" | sed 's/.*\"access_token\":\"\([^\"]*\)\".*/\1/g');
echo -e "\n\nACCESS TOKEN IS \"$ACCESS_TOKEN\"";


#curl -X POST 'https://admin.svom.eu/keycloak/realms/svom-fsc/protocol/openid-connect/token' \
# --header 'Content-Type: application/x-www-form-urlencoded' \
# --data-urlencode 'grant_type=password' \
# --data-urlencode 'client_id=FSC_PUBLIC' \
# --data-urlencode 'username=andrea.formica@cern.ch' \
# --data-urlencode 'password=xxxxx'
