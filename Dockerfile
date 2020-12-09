# CrestDB
#
# VERSION       CrestDB-1.0

# use the centos base image provided by dotCloud
# FROM openjdk:8u121-jdk
# FROM anapsix/alpine-java
# FROM openjdk:8u212-jre-alpine3.9
#FROM openjdk:15-jdk-alpine
FROM adoptopenjdk/openjdk11:alpine-jre
MAINTAINER Andrea Formica

ENV USR crest
ENV CREST_GID 208

ENV crest_version 1.0-SNAPSHOT
ENV crest_dir /home/${USR}/crest
ENV data_dir /home/${USR}/data
#ENV data_dir /data
ENV gradle_version 6.7
ENV TZ GMT

RUN addgroup -g $CREST_GID $USR \
    && adduser -S -u $CREST_GID -G $USR -h /home/$USR $USR

RUN  mkdir -p ${crest_dir} \
  && mkdir -p ${data_dir}/web \
  && mkdir -p ${data_dir}/dump \
  && mkdir -p ${data_dir}/logs

## This works if using an externally generated war, in the local directory
ADD crestdb-web/build/libs/crest.war ${crest_dir}/crest.war
ADD web ${data_dir}/web
ADD logback.xml.crest ${data_dir}/logback.xml

### we export only 1 directories....
VOLUME "${data_dir}"
EXPOSE 8080

# copy the entrypoint
COPY ./entrypoint.sh /home/${USR}
COPY ./create-properties.sh /home/${USR}

RUN chown -R $USR:$CREST_GID /home/${USR}

### we set the user and the workdir....
USER ${USR}
WORKDIR /home/${USR}

ENTRYPOINT  [ "./entrypoint.sh" ]
