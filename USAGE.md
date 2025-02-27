#### Author: A.Formica
# Table of Contents
1. [Introduction](#introduction)
2. [Deployment](#deployment)
2. [Tagging](#tagging)
3. [IoV](#iov)
4. [Payload](#payload)
5. [Global Tag](#global-tag)
6. [Global Tag Map](#global-tag-map)
7. [Examples](#examples)

## Introduction
The main concepts behind *CREST* are the following:
- Tags: defined by a name, a payload type and time type. The time type indicates what is used in IoV. 
- IoV: defined by a tag, a start time and payload hash (sha256 in general). The start time indicates the beginning of the validity of the payload. 
- Payload: defined by a hash (sha256 in general) and a content. The content is a binary blob. 
- Global Tag: a container of Tags, defined by a name. 
- Global Tag Map: a collection of Tags to Global Tag mappings. The mapping is defined by a tag name, a record and a label.

## Deployment
The list of available servers is the following:

| Server | URL                      | APID                                        | Description                                        |
| ------ |--------------------------|---------------------------------------------|----------------------------------------------------|
| Development | http://crest-j23.cern.ch:8080 | api-v5.0                                    | Development server, use ATLAS_PHYS_COND_01 @ INT8R |
| Production  | https://crest.cern.ch    | api-v4.0                                    | Production read only server, use ATLAS_PHYS_COND @ INT8R    |
| Production  | http://crest-03.cern.ch:9090 | api-v5.0                                    | Production read/write server, use ATLAS_PHYS_COND @ INT8R |
| Test        | http://atlaf-alma9-01.cern.ch:8080 | api-v5.0    | Test server, use ATLAS_PHYS_COND_01 @ INT8R |

There are haproxy in P1 to access the production servers. The haproxy is configured to use the following servers:
- _atlashap02-atcn:8081_ : access the read-write server crest-03.cern.ch
- _atlashap01-atcn:8080_ : access the read-only server crest.cern.ch

## Tagging
Here are the main operations related to tagging:

 * create a tag: `POST /tags`
 * get a tag: `GET /tags/{name}`
 * get all tags: `GET /tags` (paginated), use several parameters to filter the results.

## IoV
Here are the main operations related to IoV:

 * create an iov: `POST /iovs`, a list of iovs can be created at once.
 * get all iovs: `GET /iovs` (paginated), use several parameters to filter the results.

## Payload
Here are the main operations related to payloads:

 * create a payload: `POST /payloads`
 * get a payload: `GET /payloads/{hash}`

## Global Tag
Here are the main operations related to global tags:

 * create a global tag: `POST /globaltags`
 * get a global tag: `GET /globaltags/{name}`
 * get all global tags: `GET /globaltags` (paginated), use several parameters to filter the results.

## Global Tag Map
Here are the main operations related to global tag maps:

 * create a global tag map: `POST /globaltagmaps`

## Examples
A user needs to create a tag for a payload of type `mytype` and time type `run-lumi`. 
The user creates the tag with the following command:
```
curl -X POST "http://localhost:8080/tags" -H "accept: application/json" -H "Content-Type: application/json" -d "{ \"name\": \"mytag\", \"payloadType\": \"mytype\", \"timeType\": \"run-lumi\"}"
```
The user creates a list of payloads inside a tag, providing the iov associated with the following command:
```
curl -X POST "http://localhost:8080/payloads" -H "accept: application/json" -H "Content-Type: application/json" -d ""
```
The user creates a global tag with the following command:
```
curl -X POST "http://localhost:8080/globaltags" -H "accept: application/json" -H "Content-Type: application/json" -d "{ \"name\": \"myglobaltag\", \"tags\": [ { \"name\": \"mytag\", \"record\": \"myrecord\", \"label\": \"mylabel\" } ]}"
```
The user creates a global tag map with the following command:
```
curl -X POST "http://localhost:8080/globaltagmaps" -H "accept: application/json" -H "Content-Type: application/json" -d "{ \"globalTagName\": \"myglobaltag\", \"tagName\": \"mytag\", \"record\": \"myrecord\", \"label\": \"mylabel\"}"
```
The user gets the global tag with the following command:
```
curl -X GET "http://localhost:8080/globaltags/myglobaltag" -H "accept: application/json"
```
A user can copy a payload from another tag to a new tag with the following command:
- get the iov corresponding to the payload from the old tag. 
- insert the iov in the new tag, after changing eventually the start time (since).
