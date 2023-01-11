#!/bin/sh

trap 'trap " " TERM; kill 0; wait; cleanup' INT TERM

# Make a temporary directory
export SPRING_TMPDIR=$(mktemp -d)
export P12KEYSTORE=${SPRING_TMPDIR}/tls.p12

cleanup () {
  if [ -d "${SPRING_TMPDIR}" ]; then
    rm -rf ${SPRING_TMPDIR}
  fi
}

make_fifo () {
  local fifo=$(mktemp -u -p ${SPRING_TMPDIR})
  mkfifo -m 600 "${fifo}"
  echo "${fifo}"
}

convert_certificate () {
  # Openshift/kubernetes provide host certificates in key and crt format.
  # Convert on the fly to PKCS12 format, in pipe so that it is used only once.
  # A dummy password is included for this operation
  TLSDIR=/run/secrets/align-mon-tls
  if [ -e ${TLSDIR}/tls.key ] && [ -e ${TLSDIR}/tls.crt ]; then
    echo "Found certificates - converting to PKCS12 format in ${P12KEYSTORE}"
    mkfifo -m 600 ${P12KEYSTORE}
    openssl pkcs12 -export -out ${P12KEYSTORE} -inkey ${TLSDIR}/tls.key -in ${TLSDIR}/tls.crt -passout pass:dummy_password &
  fi
}

print_application_properties () {
  if [ -e config/application.properties ] ; then
    cat config/application.properties
  fi
  if [ -e /run/secrets/crest-phys-cond ] ; then
    echo "crest.db.password=$(cat /run/secrets/crest-phys-cond)"
  fi
  if [ -e /run/secrets/cool_secret ] ; then
    echo "align.cool.writer=$(cat /run/secrets/cool_secret)"
  fi
  if [ -e /run/secrets/crest-keycloak-secret/client_id ]; then
    echo "crest.keycloak.resource=$(cat /run/secrets/crest-keycloak-secret/client_id)"
  fi
  if [ -e /run/secrets/crest-keycloak-secret/client_secret ]; then
    echo "crest.keycloak.secret=$(cat /run/secrets/crest-keycloak-secret/client_secret)"
  fi
  if [ -e ${P12KEYSTORE} ]; then
    echo "server.ssl.key-store-type=PKCS12"
    echo "server.ssl.key-store=file:${P12KEYSTORE}"
    echo "server.ssl.key-store-password=dummy_password"
    #echo "server.ssl.key-alias=FIXME"
    echo "server.ssl.enabled=true"
  fi
}

## -Dlogging.config=/data/logs/logback.xml
echo "Setting JAVA_OPTS from file javaopts.properties"
joptfile=./javaopts.properties
echo "use opt : "
cat $joptfile
if [ -e $joptfile ]; then
   export JAVA_OPTS=
   while read line; do JAVA_OPTS="$JAVA_OPTS -D$line"; done < $joptfile
fi

prj_dir=$crest_dir
if [ -z "$crest_dir" ]; then
   prj_dir=$PWD/build/libs
fi 

convert_certificate

app_properties=${SPRING_TMPDIR}/application.properties
mkfifo -m 600 "${app_properties}"
print_application_properties >> ${app_properties} &

echo "$USER is starting server with JAVA_OPTS : $JAVA_OPTS from user directory $PWD"
if [ x"$1" = x"" ]; then
    echo "execute command ${prj_dir}/crest.jar"
    exec java $JAVA_OPTS -jar ${prj_dir}/crest.jar --spring.config.location=optional:classpath:/,optional:classpath:/config/,file:${app_properties} 2>>/tmp/err.log
else
    sh -c "$@"
fi

cleanup

