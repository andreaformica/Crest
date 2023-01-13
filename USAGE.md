#### Author: A.Formica
# Table of Contents
1. [Introduction](#introduction)
2. [Tagging](#tagging)
3. [IoV](#iov)
4. [Payload](#payload)

## Introduction
The main concepts behind *CREST* are the following:
 * Tags: defined by a name, a payload type and time type. The time type indicates what is used in IoV.
 * IoV: defined by a tag, a start time and payload hash (sha256 in general). The start time indicates the beginning of the validity of the payload.
 * Payload: defined by a hash (sha256 in general) and a content. The content is a binary blob.
 * Global Tag: a container of Tags, defined by a name.
 * Global Tag Map: a collection of Tags to Global Tag mappings. The mapping is defined by a tag name, a record and a label.

## Tagging
 * create a tag: `POST /tags`
 * get a tag: `GET /tags/{name}`
 * get all tags: `GET /tags` (paginated), use several parameters to filter the results.

## IoV
 * create an iov: `POST /iovs`, a list of iovs can be created at once.
 * get all iovs: `GET /iovs` (paginated), use several parameters to filter the results.

## Payload
 * create a payload: `POST /payloads`
 * get a payload: `GET /payloads/{hash}`

