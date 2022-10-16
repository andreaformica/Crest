# CrestDB
#
# 2-stage docker to build a minimal JVM
# Inspired from:
#   https://blog.gilliard.lol/2018/11/05/alpine-jdk11-images.html
ARG jvm_location=/opt/java-11-openjdk-minimal

# First stage build: make minimal JVM
FROM alpine:3.13 as jlink
MAINTAINER Andrea Formica

ARG jvm_location

# Use the alpine native JVM built for the musl libc (project "portola")
# (Seems to be officially supported by alpine)
RUN apk add --no-cache openjdk11-jdk openjdk11-jmods

# Run the "jlink" tool provided by openjdk to create a minimalist JVM
RUN /usr/lib/jvm/java-11-openjdk/bin/jlink \
  --verbose \
  --add-modules \
     java.base,java.sql,java.naming,java.desktop,java.management,java.security.jgss,java.instrument,jdk.unsupported,java.net.http,jdk.crypto.cryptoki,jdk.crypto.ec \
  --compress 2 --strip-debug --no-header-files --no-man-pages \
  --output "${jvm_location}"


# Second stage build: ship minimal JVM, copy application into it
FROM alpine:3.13

ARG jvm_location

ENV USR crest
ENV CREST_GID 208

ENV crest_version 1.0-SNAPSHOT
ENV crest_dir /home/${USR}/crest
ENV data_dir /home/${USR}/data
#ENV data_dir /data
ENV gradle_version 6.7
ENV TZ GMT
ENV JAVA_HOME="${jvm_location}"
ENV PATH="$PATH:$JAVA_HOME/bin"

RUN apk add --no-cache libjpeg openssl

RUN addgroup -g $CREST_GID $USR \
    && adduser -S -u $CREST_GID -G $USR -h /home/$USR $USR

RUN  mkdir -p ${crest_dir} \
  && mkdir -p ${data_dir}/web \
  && mkdir -p ${data_dir}/dump \
  && mkdir -p ${data_dir}/logs \
  && chown -R ${USR}:${USR} /home/${USR}

# Copy the minimal JVM from previous step
COPY --from=jlink "${jvm_location}" "${jvm_location}"

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
