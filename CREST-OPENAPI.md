---
title: CREST Server v5.0
language_tabs:
  - shell: Shell
  - http: HTTP
  - javascript: JavaScript
  - ruby: Ruby
  - python: Python
  - php: PHP
  - java: Java
  - go: Go
toc_footers: []
includes: []
search: true
highlight_theme: darkula
headingLevel: 2

---

<!-- Generator: Widdershins v4.0.1 -->

<h1 id="crest-server">CREST Server v5.0</h1>

> Scroll down for code samples, example requests and responses. Select a language for code samples from the tabs above or the mobile navigation menu.

OpenApi3 for CREST Server

Base URLs:

* <a href="http://crest-j23.cern.ch/api-v5.0">http://crest-j23.cern.ch/api-v5.0</a>

License: <a href="http://www.apache.org/licenses/LICENSE-2.0.html">Apache 2.0</a>

# Authentication

- HTTP Authentication, scheme: bearer 

<h1 id="crest-server-admin">admin</h1>

## updateGlobalTag

<a id="opIdupdateGlobalTag"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://crest-j23.cern.ch/api-v5.0/admin/globaltags/{name} \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer {access-token}'

```

```http
PUT http://crest-j23.cern.ch/api-v5.0/admin/globaltags/{name} HTTP/1.1
Host: crest-j23.cern.ch
Content-Type: application/json
Accept: application/json

```

```javascript
const inputBody = '{
  "name": "string",
  "validity": 0,
  "description": "string",
  "release": "string",
  "insertionTime": "2019-08-24T14:15:22Z",
  "snapshotTime": "2019-08-24T14:15:22Z",
  "scenario": "string",
  "workflow": "string",
  "type": "string",
  "snapshotTimeMilli": 0,
  "insertionTimeMilli": 0
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'application/json',
  'Authorization':'Bearer {access-token}'
};

fetch('http://crest-j23.cern.ch/api-v5.0/admin/globaltags/{name}',
{
  method: 'PUT',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Content-Type' => 'application/json',
  'Accept' => 'application/json',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.put 'http://crest-j23.cern.ch/api-v5.0/admin/globaltags/{name}',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Content-Type': 'application/json',
  'Accept': 'application/json',
  'Authorization': 'Bearer {access-token}'
}

r = requests.put('http://crest-j23.cern.ch/api-v5.0/admin/globaltags/{name}', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Content-Type' => 'application/json',
    'Accept' => 'application/json',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('PUT','http://crest-j23.cern.ch/api-v5.0/admin/globaltags/{name}', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("http://crest-j23.cern.ch/api-v5.0/admin/globaltags/{name}");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("PUT");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Content-Type": []string{"application/json"},
        "Accept": []string{"application/json"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("PUT", "http://crest-j23.cern.ch/api-v5.0/admin/globaltags/{name}", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`PUT /admin/globaltags/{name}`

*Update a GlobalTag in the database.*

This method allows to update a GlobalTag.Arguments: the name has to uniquely identify a global tag.

> Body parameter

```json
{
  "name": "string",
  "validity": 0,
  "description": "string",
  "release": "string",
  "insertionTime": "2019-08-24T14:15:22Z",
  "snapshotTime": "2019-08-24T14:15:22Z",
  "scenario": "string",
  "workflow": "string",
  "type": "string",
  "snapshotTimeMilli": 0,
  "insertionTimeMilli": 0
}
```

<h3 id="updateglobaltag-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|name|path|string|true|none|
|body|body|[GlobalTagDto](#schemaglobaltagdto)|false|none|

> Example responses

> 200 Response

```json
{
  "name": "string",
  "validity": 0,
  "description": "string",
  "release": "string",
  "insertionTime": "2019-08-24T14:15:22Z",
  "snapshotTime": "2019-08-24T14:15:22Z",
  "scenario": "string",
  "workflow": "string",
  "type": "string",
  "snapshotTimeMilli": 0,
  "insertionTimeMilli": 0
}
```

<h3 id="updateglobaltag-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|successful operation|[GlobalTagDto](#schemaglobaltagdto)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth, OpenID ( Scopes: openid )
</aside>

## removeGlobalTag

<a id="opIdremoveGlobalTag"></a>

> Code samples

```shell
# You can also use wget
curl -X DELETE http://crest-j23.cern.ch/api-v5.0/admin/globaltags/{name} \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer {access-token}'

```

```http
DELETE http://crest-j23.cern.ch/api-v5.0/admin/globaltags/{name} HTTP/1.1
Host: crest-j23.cern.ch
Accept: application/json

```

```javascript

const headers = {
  'Accept':'application/json',
  'Authorization':'Bearer {access-token}'
};

fetch('http://crest-j23.cern.ch/api-v5.0/admin/globaltags/{name}',
{
  method: 'DELETE',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Accept' => 'application/json',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.delete 'http://crest-j23.cern.ch/api-v5.0/admin/globaltags/{name}',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Accept': 'application/json',
  'Authorization': 'Bearer {access-token}'
}

r = requests.delete('http://crest-j23.cern.ch/api-v5.0/admin/globaltags/{name}', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Accept' => 'application/json',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('DELETE','http://crest-j23.cern.ch/api-v5.0/admin/globaltags/{name}', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("http://crest-j23.cern.ch/api-v5.0/admin/globaltags/{name}");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("DELETE");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Accept": []string{"application/json"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("DELETE", "http://crest-j23.cern.ch/api-v5.0/admin/globaltags/{name}", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`DELETE /admin/globaltags/{name}`

*Remove a GlobalTag from the database.*

This method allows to remove a GlobalTag.Arguments: the name has to uniquely identify a global tag.

<h3 id="removeglobaltag-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|name|path|string|true|none|

> Example responses

> 404 Response

```json
{
  "timestamp": "2019-08-24T14:15:22Z",
  "code": 0,
  "error": "string",
  "type": "string",
  "message": "string",
  "id": "string"
}
```

<h3 id="removeglobaltag-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|successful operation|None|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|Not found|[HTTPResponse](#schemahttpresponse)|
|default|Default|Generic error response|[HTTPResponse](#schemahttpresponse)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth, OpenID ( Scopes: openid )
</aside>

## removeTag

<a id="opIdremoveTag"></a>

> Code samples

```shell
# You can also use wget
curl -X DELETE http://crest-j23.cern.ch/api-v5.0/admin/tags/{name} \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer {access-token}'

```

```http
DELETE http://crest-j23.cern.ch/api-v5.0/admin/tags/{name} HTTP/1.1
Host: crest-j23.cern.ch
Accept: application/json

```

```javascript

const headers = {
  'Accept':'application/json',
  'Authorization':'Bearer {access-token}'
};

fetch('http://crest-j23.cern.ch/api-v5.0/admin/tags/{name}',
{
  method: 'DELETE',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Accept' => 'application/json',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.delete 'http://crest-j23.cern.ch/api-v5.0/admin/tags/{name}',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Accept': 'application/json',
  'Authorization': 'Bearer {access-token}'
}

r = requests.delete('http://crest-j23.cern.ch/api-v5.0/admin/tags/{name}', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Accept' => 'application/json',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('DELETE','http://crest-j23.cern.ch/api-v5.0/admin/tags/{name}', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("http://crest-j23.cern.ch/api-v5.0/admin/tags/{name}");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("DELETE");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Accept": []string{"application/json"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("DELETE", "http://crest-j23.cern.ch/api-v5.0/admin/tags/{name}", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`DELETE /admin/tags/{name}`

*Remove a Tag from the database.*

This method allows to remove a Tag.Arguments: the name has to uniquely identify a tag.

<h3 id="removetag-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|name|path|string|true|none|

> Example responses

> 404 Response

```json
{
  "timestamp": "2019-08-24T14:15:22Z",
  "code": 0,
  "error": "string",
  "type": "string",
  "message": "string",
  "id": "string"
}
```

<h3 id="removetag-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|successful operation|None|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|Not found|[HTTPResponse](#schemahttpresponse)|
|default|Default|Generic error response|[HTTPResponse](#schemahttpresponse)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth, OpenID ( Scopes: openid )
</aside>

<h1 id="crest-server-folders">folders</h1>

## createFolder

<a id="opIdcreateFolder"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://crest-j23.cern.ch/api-v5.0/folders \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer {access-token}'

```

```http
POST http://crest-j23.cern.ch/api-v5.0/folders HTTP/1.1
Host: crest-j23.cern.ch
Content-Type: application/json
Accept: application/json

```

```javascript
const inputBody = '{
  "nodeFullpath": "string",
  "schemaName": "string",
  "nodeName": "string",
  "nodeDescription": "string",
  "tagPattern": "string",
  "groupRole": "string"
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'application/json',
  'Authorization':'Bearer {access-token}'
};

fetch('http://crest-j23.cern.ch/api-v5.0/folders',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Content-Type' => 'application/json',
  'Accept' => 'application/json',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.post 'http://crest-j23.cern.ch/api-v5.0/folders',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Content-Type': 'application/json',
  'Accept': 'application/json',
  'Authorization': 'Bearer {access-token}'
}

r = requests.post('http://crest-j23.cern.ch/api-v5.0/folders', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Content-Type' => 'application/json',
    'Accept' => 'application/json',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('POST','http://crest-j23.cern.ch/api-v5.0/folders', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("http://crest-j23.cern.ch/api-v5.0/folders");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("POST");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Content-Type": []string{"application/json"},
        "Accept": []string{"application/json"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("POST", "http://crest-j23.cern.ch/api-v5.0/folders", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`POST /folders`

*Create an entry for folder information.*

Folder informations go into a dedicated table.

> Body parameter

```json
{
  "nodeFullpath": "string",
  "schemaName": "string",
  "nodeName": "string",
  "nodeDescription": "string",
  "tagPattern": "string",
  "groupRole": "string"
}
```

<h3 id="createfolder-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[FolderDto](#schemafolderdto)|false|none|

> Example responses

> 201 Response

```json
{
  "nodeFullpath": "string",
  "schemaName": "string",
  "nodeName": "string",
  "nodeDescription": "string",
  "tagPattern": "string",
  "groupRole": "string"
}
```

<h3 id="createfolder-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|201|[Created](https://tools.ietf.org/html/rfc7231#section-6.3.2)|successful operation|[FolderDto](#schemafolderdto)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth, OpenID ( Scopes: openid )
</aside>

## listFolders

<a id="opIdlistFolders"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://crest-j23.cern.ch/api-v5.0/folders \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer {access-token}'

```

```http
GET http://crest-j23.cern.ch/api-v5.0/folders HTTP/1.1
Host: crest-j23.cern.ch
Accept: application/json

```

```javascript

const headers = {
  'Accept':'application/json',
  'Authorization':'Bearer {access-token}'
};

fetch('http://crest-j23.cern.ch/api-v5.0/folders',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Accept' => 'application/json',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.get 'http://crest-j23.cern.ch/api-v5.0/folders',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Accept': 'application/json',
  'Authorization': 'Bearer {access-token}'
}

r = requests.get('http://crest-j23.cern.ch/api-v5.0/folders', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Accept' => 'application/json',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('GET','http://crest-j23.cern.ch/api-v5.0/folders', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("http://crest-j23.cern.ch/api-v5.0/folders");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("GET");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Accept": []string{"application/json"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("GET", "http://crest-j23.cern.ch/api-v5.0/folders", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`GET /folders`

*Finds a FolderDto list.*

This method allows to perform search and sorting.Arguments: by=<pattern>, sort=<sortpattern>. The pattern <pattern> is in the form <param-name><operation><param-value>       <param-name> is the name of one of the fields in the dto       <operation> can be [< : >] ; for string use only [:]        <param-value> depends on the chosen parameter. A list of this criteria can be provided       using comma separated strings for <pattern>.      The pattern <sortpattern> is <field>:[DESC|ASC]

<h3 id="listfolders-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|schema|query|string|false|the schema pattern {none}|

> Example responses

> 200 Response

```json
{
  "resources": [
    {
      "nodeFullpath": "string",
      "schemaName": "string",
      "nodeName": "string",
      "nodeDescription": "string",
      "tagPattern": "string",
      "groupRole": "string"
    }
  ],
  "size": 0,
  "datatype": "string",
  "format": "string",
  "page": {
    "size": 0,
    "totalElements": 0,
    "totalPages": 0,
    "number": 0
  },
  "filter": {
    "name": "string",
    "property1": "string",
    "property2": "string"
  }
}
```

<h3 id="listfolders-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|successful operation|[FolderSetDto](#schemafoldersetdto)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth, OpenID ( Scopes: openid )
</aside>

<h1 id="crest-server-globaltagmaps">globaltagmaps</h1>

## createGlobalTagMap

<a id="opIdcreateGlobalTagMap"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://crest-j23.cern.ch/api-v5.0/globaltagmaps \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer {access-token}'

```

```http
POST http://crest-j23.cern.ch/api-v5.0/globaltagmaps HTTP/1.1
Host: crest-j23.cern.ch
Content-Type: application/json
Accept: application/json

```

```javascript
const inputBody = '{
  "globalTagName": "string",
  "record": "string",
  "label": "string",
  "tagName": "string"
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'application/json',
  'Authorization':'Bearer {access-token}'
};

fetch('http://crest-j23.cern.ch/api-v5.0/globaltagmaps',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Content-Type' => 'application/json',
  'Accept' => 'application/json',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.post 'http://crest-j23.cern.ch/api-v5.0/globaltagmaps',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Content-Type': 'application/json',
  'Accept': 'application/json',
  'Authorization': 'Bearer {access-token}'
}

r = requests.post('http://crest-j23.cern.ch/api-v5.0/globaltagmaps', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Content-Type' => 'application/json',
    'Accept' => 'application/json',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('POST','http://crest-j23.cern.ch/api-v5.0/globaltagmaps', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("http://crest-j23.cern.ch/api-v5.0/globaltagmaps");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("POST");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Content-Type": []string{"application/json"},
        "Accept": []string{"application/json"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("POST", "http://crest-j23.cern.ch/api-v5.0/globaltagmaps", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`POST /globaltagmaps`

*Create a GlobalTagMap in the database.*

This method allows to insert a GlobalTagMap.Arguments: GlobalTagMapDto should be provided in the body as a JSON file.

> Body parameter

```json
{
  "globalTagName": "string",
  "record": "string",
  "label": "string",
  "tagName": "string"
}
```

<h3 id="createglobaltagmap-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[GlobalTagMapDto](#schemaglobaltagmapdto)|false|none|

> Example responses

> 201 Response

```json
{
  "globalTagName": "string",
  "record": "string",
  "label": "string",
  "tagName": "string"
}
```

<h3 id="createglobaltagmap-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|201|[Created](https://tools.ietf.org/html/rfc7231#section-6.3.2)|successful operation|[GlobalTagMapDto](#schemaglobaltagmapdto)|
|default|Default|Generic error response|[HTTPResponse](#schemahttpresponse)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth, OpenID ( Scopes: openid )
</aside>

## findGlobalTagMap

<a id="opIdfindGlobalTagMap"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://crest-j23.cern.ch/api-v5.0/globaltagmaps/{name} \
  -H 'Accept: application/json' \
  -H 'X-Crest-MapMode: Trace' \
  -H 'Authorization: Bearer {access-token}'

```

```http
GET http://crest-j23.cern.ch/api-v5.0/globaltagmaps/{name} HTTP/1.1
Host: crest-j23.cern.ch
Accept: application/json
X-Crest-MapMode: Trace

```

```javascript

const headers = {
  'Accept':'application/json',
  'X-Crest-MapMode':'Trace',
  'Authorization':'Bearer {access-token}'
};

fetch('http://crest-j23.cern.ch/api-v5.0/globaltagmaps/{name}',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Accept' => 'application/json',
  'X-Crest-MapMode' => 'Trace',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.get 'http://crest-j23.cern.ch/api-v5.0/globaltagmaps/{name}',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Accept': 'application/json',
  'X-Crest-MapMode': 'Trace',
  'Authorization': 'Bearer {access-token}'
}

r = requests.get('http://crest-j23.cern.ch/api-v5.0/globaltagmaps/{name}', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Accept' => 'application/json',
    'X-Crest-MapMode' => 'Trace',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('GET','http://crest-j23.cern.ch/api-v5.0/globaltagmaps/{name}', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("http://crest-j23.cern.ch/api-v5.0/globaltagmaps/{name}");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("GET");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Accept": []string{"application/json"},
        "X-Crest-MapMode": []string{"Trace"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("GET", "http://crest-j23.cern.ch/api-v5.0/globaltagmaps/{name}", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`GET /globaltagmaps/{name}`

*Find GlobalTagMapDto lists.*

This method search for mappings using the global tag name.

<h3 id="findglobaltagmap-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Crest-MapMode|header|string|false|If the mode is BackTrace then it will search for global tags containing the tag <name>|
|name|path|string|true|none|

> Example responses

> 200 Response

```json
{
  "resources": [
    {
      "globalTagName": "string",
      "record": "string",
      "label": "string",
      "tagName": "string"
    }
  ],
  "size": 0,
  "datatype": "string",
  "format": "string",
  "page": {
    "size": 0,
    "totalElements": 0,
    "totalPages": 0,
    "number": 0
  },
  "filter": {
    "name": "string",
    "property1": "string",
    "property2": "string"
  }
}
```

<h3 id="findglobaltagmap-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|successful operation|[GlobalTagMapSetDto](#schemaglobaltagmapsetdto)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|Not found|[HTTPResponse](#schemahttpresponse)|
|default|Default|Generic error response|[HTTPResponse](#schemahttpresponse)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth, OpenID ( Scopes: openid )
</aside>

## deleteGlobalTagMap

<a id="opIddeleteGlobalTagMap"></a>

> Code samples

```shell
# You can also use wget
curl -X DELETE http://crest-j23.cern.ch/api-v5.0/globaltagmaps/{name}?label=none&tagname=none \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer {access-token}'

```

```http
DELETE http://crest-j23.cern.ch/api-v5.0/globaltagmaps/{name}?label=none&tagname=none HTTP/1.1
Host: crest-j23.cern.ch
Accept: application/json

```

```javascript

const headers = {
  'Accept':'application/json',
  'Authorization':'Bearer {access-token}'
};

fetch('http://crest-j23.cern.ch/api-v5.0/globaltagmaps/{name}?label=none&tagname=none',
{
  method: 'DELETE',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Accept' => 'application/json',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.delete 'http://crest-j23.cern.ch/api-v5.0/globaltagmaps/{name}',
  params: {
  'label' => 'string',
'tagname' => 'string'
}, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Accept': 'application/json',
  'Authorization': 'Bearer {access-token}'
}

r = requests.delete('http://crest-j23.cern.ch/api-v5.0/globaltagmaps/{name}', params={
  'label': 'none',  'tagname': 'none'
}, headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Accept' => 'application/json',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('DELETE','http://crest-j23.cern.ch/api-v5.0/globaltagmaps/{name}', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("http://crest-j23.cern.ch/api-v5.0/globaltagmaps/{name}?label=none&tagname=none");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("DELETE");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Accept": []string{"application/json"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("DELETE", "http://crest-j23.cern.ch/api-v5.0/globaltagmaps/{name}", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`DELETE /globaltagmaps/{name}`

*Delete GlobalTagMapDto lists.*

This method search for mappings using the global tag name and deletes all mappings.

<h3 id="deleteglobaltagmap-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|name|path|string|true|the global tag name|
|label|query|string|true|label: the generic name labelling all tags of a certain kind.|
|record|query|string|false|record: the record.|
|tagname|query|string|true|tagname: the name of the tag associated.|

> Example responses

> 200 Response

```json
{
  "resources": [
    {
      "globalTagName": "string",
      "record": "string",
      "label": "string",
      "tagName": "string"
    }
  ],
  "size": 0,
  "datatype": "string",
  "format": "string",
  "page": {
    "size": 0,
    "totalElements": 0,
    "totalPages": 0,
    "number": 0
  },
  "filter": {
    "name": "string",
    "property1": "string",
    "property2": "string"
  }
}
```

<h3 id="deleteglobaltagmap-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|successful operation|[GlobalTagMapSetDto](#schemaglobaltagmapsetdto)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|Not found|[HTTPResponse](#schemahttpresponse)|
|default|Default|Generic error response|[HTTPResponse](#schemahttpresponse)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth, OpenID ( Scopes: openid )
</aside>

<h1 id="crest-server-globaltags">globaltags</h1>

## listGlobalTags

<a id="opIdlistGlobalTags"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://crest-j23.cern.ch/api-v5.0/globaltags \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer {access-token}'

```

```http
GET http://crest-j23.cern.ch/api-v5.0/globaltags HTTP/1.1
Host: crest-j23.cern.ch
Accept: application/json

```

```javascript

const headers = {
  'Accept':'application/json',
  'Authorization':'Bearer {access-token}'
};

fetch('http://crest-j23.cern.ch/api-v5.0/globaltags',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Accept' => 'application/json',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.get 'http://crest-j23.cern.ch/api-v5.0/globaltags',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Accept': 'application/json',
  'Authorization': 'Bearer {access-token}'
}

r = requests.get('http://crest-j23.cern.ch/api-v5.0/globaltags', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Accept' => 'application/json',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('GET','http://crest-j23.cern.ch/api-v5.0/globaltags', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("http://crest-j23.cern.ch/api-v5.0/globaltags");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("GET");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Accept": []string{"application/json"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("GET", "http://crest-j23.cern.ch/api-v5.0/globaltags", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`GET /globaltags`

*Finds a GlobalTagDtos lists.*

This method allows to perform search and sorting.
Arguments: name=<pattern>, workflow, scenario, release, validity, description
page={ipage}, size={isize}, sort=<sortpattern>.

<h3 id="listglobaltags-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|name|query|string|false|the global tag name search pattern {none}|
|workflow|query|string|false|the global tag workflow search pattern {none}|
|scenario|query|string|false|the global tag scenario search pattern {none}|
|release|query|string|false|the global tag release search pattern {none}|
|validity|query|integer(int64)|false|the global tag validity low limit {x>=validity}|
|description|query|string|false|the global tag description search pattern {none}|
|page|query|integer(int32)|false|page: the page number {0}|
|size|query|integer(int32)|false|size: the page size {1000}|
|sort|query|string|false|sort: the sort pattern {name:ASC}|

> Example responses

> 200 Response

```json
{
  "resources": [
    {
      "name": "string",
      "validity": 0,
      "description": "string",
      "release": "string",
      "insertionTime": "2019-08-24T14:15:22Z",
      "snapshotTime": "2019-08-24T14:15:22Z",
      "scenario": "string",
      "workflow": "string",
      "type": "string",
      "snapshotTimeMilli": 0,
      "insertionTimeMilli": 0
    }
  ],
  "size": 0,
  "datatype": "string",
  "format": "string",
  "page": {
    "size": 0,
    "totalElements": 0,
    "totalPages": 0,
    "number": 0
  },
  "filter": {
    "name": "string",
    "property1": "string",
    "property2": "string"
  }
}
```

<h3 id="listglobaltags-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|successful operation|[GlobalTagSetDto](#schemaglobaltagsetdto)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|Not found|[HTTPResponse](#schemahttpresponse)|
|default|Default|Generic error response|[HTTPResponse](#schemahttpresponse)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth, OpenID ( Scopes: openid )
</aside>

## createGlobalTag

<a id="opIdcreateGlobalTag"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://crest-j23.cern.ch/api-v5.0/globaltags \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer {access-token}'

```

```http
POST http://crest-j23.cern.ch/api-v5.0/globaltags HTTP/1.1
Host: crest-j23.cern.ch
Content-Type: application/json
Accept: application/json

```

```javascript
const inputBody = '{
  "name": "string",
  "validity": 0,
  "description": "string",
  "release": "string",
  "insertionTime": "2019-08-24T14:15:22Z",
  "snapshotTime": "2019-08-24T14:15:22Z",
  "scenario": "string",
  "workflow": "string",
  "type": "string",
  "snapshotTimeMilli": 0,
  "insertionTimeMilli": 0
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'application/json',
  'Authorization':'Bearer {access-token}'
};

fetch('http://crest-j23.cern.ch/api-v5.0/globaltags',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Content-Type' => 'application/json',
  'Accept' => 'application/json',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.post 'http://crest-j23.cern.ch/api-v5.0/globaltags',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Content-Type': 'application/json',
  'Accept': 'application/json',
  'Authorization': 'Bearer {access-token}'
}

r = requests.post('http://crest-j23.cern.ch/api-v5.0/globaltags', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Content-Type' => 'application/json',
    'Accept' => 'application/json',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('POST','http://crest-j23.cern.ch/api-v5.0/globaltags', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("http://crest-j23.cern.ch/api-v5.0/globaltags");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("POST");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Content-Type": []string{"application/json"},
        "Accept": []string{"application/json"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("POST", "http://crest-j23.cern.ch/api-v5.0/globaltags", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`POST /globaltags`

*Create a GlobalTag in the database.*

This method allows to insert a GlobalTag.Arguments: GlobalTagDto should be provided in the body as a JSON file.

> Body parameter

```json
{
  "name": "string",
  "validity": 0,
  "description": "string",
  "release": "string",
  "insertionTime": "2019-08-24T14:15:22Z",
  "snapshotTime": "2019-08-24T14:15:22Z",
  "scenario": "string",
  "workflow": "string",
  "type": "string",
  "snapshotTimeMilli": 0,
  "insertionTimeMilli": 0
}
```

<h3 id="createglobaltag-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|force|query|string|false|force: tell the server if it should use or not the insertion time provided {default: false}|
|body|body|[GlobalTagDto](#schemaglobaltagdto)|false|none|

> Example responses

> 201 Response

```json
{
  "name": "string",
  "validity": 0,
  "description": "string",
  "release": "string",
  "insertionTime": "2019-08-24T14:15:22Z",
  "snapshotTime": "2019-08-24T14:15:22Z",
  "scenario": "string",
  "workflow": "string",
  "type": "string",
  "snapshotTimeMilli": 0,
  "insertionTimeMilli": 0
}
```

<h3 id="createglobaltag-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|201|[Created](https://tools.ietf.org/html/rfc7231#section-6.3.2)|successful operation|[GlobalTagDto](#schemaglobaltagdto)|
|default|Default|Generic error response|[HTTPResponse](#schemahttpresponse)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth, OpenID ( Scopes: openid )
</aside>

## findGlobalTag

<a id="opIdfindGlobalTag"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://crest-j23.cern.ch/api-v5.0/globaltags/{name} \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer {access-token}'

```

```http
GET http://crest-j23.cern.ch/api-v5.0/globaltags/{name} HTTP/1.1
Host: crest-j23.cern.ch
Accept: application/json

```

```javascript

const headers = {
  'Accept':'application/json',
  'Authorization':'Bearer {access-token}'
};

fetch('http://crest-j23.cern.ch/api-v5.0/globaltags/{name}',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Accept' => 'application/json',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.get 'http://crest-j23.cern.ch/api-v5.0/globaltags/{name}',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Accept': 'application/json',
  'Authorization': 'Bearer {access-token}'
}

r = requests.get('http://crest-j23.cern.ch/api-v5.0/globaltags/{name}', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Accept' => 'application/json',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('GET','http://crest-j23.cern.ch/api-v5.0/globaltags/{name}', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("http://crest-j23.cern.ch/api-v5.0/globaltags/{name}");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("GET");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Accept": []string{"application/json"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("GET", "http://crest-j23.cern.ch/api-v5.0/globaltags/{name}", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`GET /globaltags/{name}`

*Finds a GlobalTagDto by name*

This method will search for a global tag with the given name. Only one global tag should be returned.

<h3 id="findglobaltag-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|name|path|string|true|none|

> Example responses

> 200 Response

```json
{
  "resources": [
    {
      "name": "string",
      "validity": 0,
      "description": "string",
      "release": "string",
      "insertionTime": "2019-08-24T14:15:22Z",
      "snapshotTime": "2019-08-24T14:15:22Z",
      "scenario": "string",
      "workflow": "string",
      "type": "string",
      "snapshotTimeMilli": 0,
      "insertionTimeMilli": 0
    }
  ],
  "size": 0,
  "datatype": "string",
  "format": "string",
  "page": {
    "size": 0,
    "totalElements": 0,
    "totalPages": 0,
    "number": 0
  },
  "filter": {
    "name": "string",
    "property1": "string",
    "property2": "string"
  }
}
```

<h3 id="findglobaltag-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|successful operation|[GlobalTagSetDto](#schemaglobaltagsetdto)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|Not found|[HTTPResponse](#schemahttpresponse)|
|default|Default|Generic error response|[HTTPResponse](#schemahttpresponse)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth, OpenID ( Scopes: openid )
</aside>

## findGlobalTagFetchTags

<a id="opIdfindGlobalTagFetchTags"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://crest-j23.cern.ch/api-v5.0/globaltags/{name}/tags \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer {access-token}'

```

```http
GET http://crest-j23.cern.ch/api-v5.0/globaltags/{name}/tags HTTP/1.1
Host: crest-j23.cern.ch
Accept: application/json

```

```javascript

const headers = {
  'Accept':'application/json',
  'Authorization':'Bearer {access-token}'
};

fetch('http://crest-j23.cern.ch/api-v5.0/globaltags/{name}/tags',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Accept' => 'application/json',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.get 'http://crest-j23.cern.ch/api-v5.0/globaltags/{name}/tags',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Accept': 'application/json',
  'Authorization': 'Bearer {access-token}'
}

r = requests.get('http://crest-j23.cern.ch/api-v5.0/globaltags/{name}/tags', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Accept' => 'application/json',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('GET','http://crest-j23.cern.ch/api-v5.0/globaltags/{name}/tags', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("http://crest-j23.cern.ch/api-v5.0/globaltags/{name}/tags");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("GET");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Accept": []string{"application/json"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("GET", "http://crest-j23.cern.ch/api-v5.0/globaltags/{name}/tags", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`GET /globaltags/{name}/tags`

*Finds a TagDtos lists associated to the global tag name in input.*

This method allows to trace a global tag.Arguments: record=<record> filter output by record, label=<label> filter output by label

<h3 id="findglobaltagfetchtags-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|name|path|string|true|none|
|record|query|string|false|record:  the record string {}|
|label|query|string|false|label:  the label string {}|

> Example responses

> 200 Response

```json
{
  "resources": [
    {
      "name": "string",
      "timeType": "string",
      "payloadSpec": "string",
      "synchronization": "string",
      "description": "string",
      "lastValidatedTime": 0,
      "endOfValidity": 0,
      "insertionTime": "2019-08-24T14:15:22Z",
      "modificationTime": "2019-08-24T14:15:22Z"
    }
  ],
  "size": 0,
  "datatype": "string",
  "format": "string",
  "page": {
    "size": 0,
    "totalElements": 0,
    "totalPages": 0,
    "number": 0
  },
  "filter": {
    "name": "string",
    "property1": "string",
    "property2": "string"
  }
}
```

<h3 id="findglobaltagfetchtags-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|successful operation|[TagSetDto](#schematagsetdto)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|Not found|[HTTPResponse](#schemahttpresponse)|
|default|Default|Generic error response|[HTTPResponse](#schemahttpresponse)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth, OpenID ( Scopes: openid )
</aside>

<h1 id="crest-server-tags">tags</h1>

## listTags

<a id="opIdlistTags"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://crest-j23.cern.ch/api-v5.0/tags \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer {access-token}'

```

```http
GET http://crest-j23.cern.ch/api-v5.0/tags HTTP/1.1
Host: crest-j23.cern.ch
Accept: application/json

```

```javascript

const headers = {
  'Accept':'application/json',
  'Authorization':'Bearer {access-token}'
};

fetch('http://crest-j23.cern.ch/api-v5.0/tags',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Accept' => 'application/json',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.get 'http://crest-j23.cern.ch/api-v5.0/tags',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Accept': 'application/json',
  'Authorization': 'Bearer {access-token}'
}

r = requests.get('http://crest-j23.cern.ch/api-v5.0/tags', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Accept' => 'application/json',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('GET','http://crest-j23.cern.ch/api-v5.0/tags', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("http://crest-j23.cern.ch/api-v5.0/tags");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("GET");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Accept": []string{"application/json"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("GET", "http://crest-j23.cern.ch/api-v5.0/tags", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`GET /tags`

*Finds a TagDtos lists.*

This method allows to perform search and sorting.
Arguments: name=<pattern>, objectType, timeType, description
page={ipage}, size={isize}, sort=<sortpattern>.

<h3 id="listtags-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|name|query|string|false|the tag name search pattern {all}|
|timeType|query|string|false|the tag timeType {none}|
|objectType|query|string|false|the tag objectType search pattern {none}|
|description|query|string|false|the global tag description search pattern {none}|
|page|query|integer(int32)|false|page: the page number {0}|
|size|query|integer(int32)|false|size: the page size {1000}|
|sort|query|string|false|sort: the sort pattern {name:ASC}|

> Example responses

> 200 Response

```json
{
  "resources": [
    {
      "name": "string",
      "timeType": "string",
      "payloadSpec": "string",
      "synchronization": "string",
      "description": "string",
      "lastValidatedTime": 0,
      "endOfValidity": 0,
      "insertionTime": "2019-08-24T14:15:22Z",
      "modificationTime": "2019-08-24T14:15:22Z"
    }
  ],
  "size": 0,
  "datatype": "string",
  "format": "string",
  "page": {
    "size": 0,
    "totalElements": 0,
    "totalPages": 0,
    "number": 0
  },
  "filter": {
    "name": "string",
    "property1": "string",
    "property2": "string"
  }
}
```

<h3 id="listtags-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|successful operation|[TagSetDto](#schematagsetdto)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|Not found|[HTTPResponse](#schemahttpresponse)|
|default|Default|Generic error response|[HTTPResponse](#schemahttpresponse)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth, OpenID ( Scopes: openid )
</aside>

## createTag

<a id="opIdcreateTag"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://crest-j23.cern.ch/api-v5.0/tags \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer {access-token}'

```

```http
POST http://crest-j23.cern.ch/api-v5.0/tags HTTP/1.1
Host: crest-j23.cern.ch
Content-Type: application/json
Accept: application/json

```

```javascript
const inputBody = '{
  "name": "string",
  "timeType": "string",
  "payloadSpec": "string",
  "synchronization": "string",
  "description": "string",
  "lastValidatedTime": 0,
  "endOfValidity": 0,
  "insertionTime": "2019-08-24T14:15:22Z",
  "modificationTime": "2019-08-24T14:15:22Z"
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'application/json',
  'Authorization':'Bearer {access-token}'
};

fetch('http://crest-j23.cern.ch/api-v5.0/tags',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Content-Type' => 'application/json',
  'Accept' => 'application/json',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.post 'http://crest-j23.cern.ch/api-v5.0/tags',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Content-Type': 'application/json',
  'Accept': 'application/json',
  'Authorization': 'Bearer {access-token}'
}

r = requests.post('http://crest-j23.cern.ch/api-v5.0/tags', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Content-Type' => 'application/json',
    'Accept' => 'application/json',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('POST','http://crest-j23.cern.ch/api-v5.0/tags', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("http://crest-j23.cern.ch/api-v5.0/tags");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("POST");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Content-Type": []string{"application/json"},
        "Accept": []string{"application/json"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("POST", "http://crest-j23.cern.ch/api-v5.0/tags", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`POST /tags`

*Create a Tag in the database.*

This method allows to insert a Tag.Arguments: TagDto should be provided in the body as a JSON file.

> Body parameter

```json
{
  "name": "string",
  "timeType": "string",
  "payloadSpec": "string",
  "synchronization": "string",
  "description": "string",
  "lastValidatedTime": 0,
  "endOfValidity": 0,
  "insertionTime": "2019-08-24T14:15:22Z",
  "modificationTime": "2019-08-24T14:15:22Z"
}
```

<h3 id="createtag-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[TagDto](#schematagdto)|false|none|

> Example responses

> 200 Response

```json
{
  "name": "string",
  "timeType": "string",
  "payloadSpec": "string",
  "synchronization": "string",
  "description": "string",
  "lastValidatedTime": 0,
  "endOfValidity": 0,
  "insertionTime": "2019-08-24T14:15:22Z",
  "modificationTime": "2019-08-24T14:15:22Z"
}
```

<h3 id="createtag-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|successful operation|[TagDto](#schematagdto)|
|default|Default|Generic error response|[HTTPResponse](#schemahttpresponse)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth, OpenID ( Scopes: openid )
</aside>

## findTag

<a id="opIdfindTag"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://crest-j23.cern.ch/api-v5.0/tags/{name} \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer {access-token}'

```

```http
GET http://crest-j23.cern.ch/api-v5.0/tags/{name} HTTP/1.1
Host: crest-j23.cern.ch
Accept: application/json

```

```javascript

const headers = {
  'Accept':'application/json',
  'Authorization':'Bearer {access-token}'
};

fetch('http://crest-j23.cern.ch/api-v5.0/tags/{name}',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Accept' => 'application/json',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.get 'http://crest-j23.cern.ch/api-v5.0/tags/{name}',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Accept': 'application/json',
  'Authorization': 'Bearer {access-token}'
}

r = requests.get('http://crest-j23.cern.ch/api-v5.0/tags/{name}', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Accept' => 'application/json',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('GET','http://crest-j23.cern.ch/api-v5.0/tags/{name}', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("http://crest-j23.cern.ch/api-v5.0/tags/{name}");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("GET");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Accept": []string{"application/json"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("GET", "http://crest-j23.cern.ch/api-v5.0/tags/{name}", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`GET /tags/{name}`

*Finds a TagDto by name*

This method will search for a tag with the given name. Only one tag should be returned.

<h3 id="findtag-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|name|path|string|true|name: the tag name|

> Example responses

> 200 Response

```json
{
  "resources": [
    {
      "name": "string",
      "timeType": "string",
      "payloadSpec": "string",
      "synchronization": "string",
      "description": "string",
      "lastValidatedTime": 0,
      "endOfValidity": 0,
      "insertionTime": "2019-08-24T14:15:22Z",
      "modificationTime": "2019-08-24T14:15:22Z"
    }
  ],
  "size": 0,
  "datatype": "string",
  "format": "string",
  "page": {
    "size": 0,
    "totalElements": 0,
    "totalPages": 0,
    "number": 0
  },
  "filter": {
    "name": "string",
    "property1": "string",
    "property2": "string"
  }
}
```

<h3 id="findtag-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|successful operation|[TagSetDto](#schematagsetdto)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|Not found|[HTTPResponse](#schemahttpresponse)|
|default|Default|Generic error response|[HTTPResponse](#schemahttpresponse)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth, OpenID ( Scopes: openid )
</aside>

## updateTag

<a id="opIdupdateTag"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://crest-j23.cern.ch/api-v5.0/tags/{name} \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer {access-token}'

```

```http
PUT http://crest-j23.cern.ch/api-v5.0/tags/{name} HTTP/1.1
Host: crest-j23.cern.ch
Content-Type: application/json
Accept: application/json

```

```javascript
const inputBody = '{
  "name": "string",
  "property1": "string",
  "property2": "string"
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'application/json',
  'Authorization':'Bearer {access-token}'
};

fetch('http://crest-j23.cern.ch/api-v5.0/tags/{name}',
{
  method: 'PUT',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Content-Type' => 'application/json',
  'Accept' => 'application/json',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.put 'http://crest-j23.cern.ch/api-v5.0/tags/{name}',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Content-Type': 'application/json',
  'Accept': 'application/json',
  'Authorization': 'Bearer {access-token}'
}

r = requests.put('http://crest-j23.cern.ch/api-v5.0/tags/{name}', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Content-Type' => 'application/json',
    'Accept' => 'application/json',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('PUT','http://crest-j23.cern.ch/api-v5.0/tags/{name}', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("http://crest-j23.cern.ch/api-v5.0/tags/{name}");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("PUT");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Content-Type": []string{"application/json"},
        "Accept": []string{"application/json"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("PUT", "http://crest-j23.cern.ch/api-v5.0/tags/{name}", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`PUT /tags/{name}`

*Update a TagDto by name*

This method will search for a tag with the given name, and update its content for the provided body fields. Only the following fields can be updated: description, timeType, objectTime, endOfValidity, lastValidatedTime.

> Body parameter

```json
{
  "name": "string",
  "property1": "string",
  "property2": "string"
}
```

<h3 id="updatetag-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|name|path|string|true|name: the tag name|
|body|body|[GenericMap](#schemagenericmap)|false|none|

> Example responses

> 200 Response

```json
{
  "name": "string",
  "timeType": "string",
  "payloadSpec": "string",
  "synchronization": "string",
  "description": "string",
  "lastValidatedTime": 0,
  "endOfValidity": 0,
  "insertionTime": "2019-08-24T14:15:22Z",
  "modificationTime": "2019-08-24T14:15:22Z"
}
```

<h3 id="updatetag-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|successful operation|[TagDto](#schematagdto)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|Not found|[HTTPResponse](#schemahttpresponse)|
|default|Default|Generic error response|[HTTPResponse](#schemahttpresponse)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth, OpenID ( Scopes: openid )
</aside>

## findTagMeta

<a id="opIdfindTagMeta"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://crest-j23.cern.ch/api-v5.0/tags/{name}/meta \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer {access-token}'

```

```http
GET http://crest-j23.cern.ch/api-v5.0/tags/{name}/meta HTTP/1.1
Host: crest-j23.cern.ch
Accept: application/json

```

```javascript

const headers = {
  'Accept':'application/json',
  'Authorization':'Bearer {access-token}'
};

fetch('http://crest-j23.cern.ch/api-v5.0/tags/{name}/meta',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Accept' => 'application/json',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.get 'http://crest-j23.cern.ch/api-v5.0/tags/{name}/meta',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Accept': 'application/json',
  'Authorization': 'Bearer {access-token}'
}

r = requests.get('http://crest-j23.cern.ch/api-v5.0/tags/{name}/meta', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Accept' => 'application/json',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('GET','http://crest-j23.cern.ch/api-v5.0/tags/{name}/meta', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("http://crest-j23.cern.ch/api-v5.0/tags/{name}/meta");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("GET");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Accept": []string{"application/json"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("GET", "http://crest-j23.cern.ch/api-v5.0/tags/{name}/meta", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`GET /tags/{name}/meta`

*Finds a TagMetaDto by name*

This method will search for a tag metadata with the given name. Only one tag should be returned.

<h3 id="findtagmeta-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|name|path|string|true|name: the tag name|

> Example responses

> 200 Response

```json
{
  "resources": [
    {
      "tagName": "string",
      "description": "string",
      "chansize": 0,
      "colsize": 0,
      "tagInfo": "string",
      "insertionTime": "2019-08-24T14:15:22Z"
    }
  ],
  "size": 0,
  "datatype": "string",
  "format": "string",
  "page": {
    "size": 0,
    "totalElements": 0,
    "totalPages": 0,
    "number": 0
  },
  "filter": {
    "name": "string",
    "property1": "string",
    "property2": "string"
  }
}
```

<h3 id="findtagmeta-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|successful operation|[TagMetaSetDto](#schematagmetasetdto)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|Not found|[HTTPResponse](#schemahttpresponse)|
|default|Default|Generic error response|[HTTPResponse](#schemahttpresponse)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth, OpenID ( Scopes: openid )
</aside>

## createTagMeta

<a id="opIdcreateTagMeta"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://crest-j23.cern.ch/api-v5.0/tags/{name}/meta \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer {access-token}'

```

```http
POST http://crest-j23.cern.ch/api-v5.0/tags/{name}/meta HTTP/1.1
Host: crest-j23.cern.ch
Content-Type: application/json
Accept: application/json

```

```javascript
const inputBody = '{
  "tagName": "string",
  "description": "string",
  "chansize": 0,
  "colsize": 0,
  "tagInfo": "string",
  "insertionTime": "2019-08-24T14:15:22Z"
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'application/json',
  'Authorization':'Bearer {access-token}'
};

fetch('http://crest-j23.cern.ch/api-v5.0/tags/{name}/meta',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Content-Type' => 'application/json',
  'Accept' => 'application/json',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.post 'http://crest-j23.cern.ch/api-v5.0/tags/{name}/meta',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Content-Type': 'application/json',
  'Accept': 'application/json',
  'Authorization': 'Bearer {access-token}'
}

r = requests.post('http://crest-j23.cern.ch/api-v5.0/tags/{name}/meta', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Content-Type' => 'application/json',
    'Accept' => 'application/json',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('POST','http://crest-j23.cern.ch/api-v5.0/tags/{name}/meta', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("http://crest-j23.cern.ch/api-v5.0/tags/{name}/meta");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("POST");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Content-Type": []string{"application/json"},
        "Accept": []string{"application/json"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("POST", "http://crest-j23.cern.ch/api-v5.0/tags/{name}/meta", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`POST /tags/{name}/meta`

*Create a TagMeta in the database.*

This method allows to insert a TagMeta.Arguments: TagMetaDto should be provided in the body as a JSON file.

> Body parameter

```json
{
  "tagName": "string",
  "description": "string",
  "chansize": 0,
  "colsize": 0,
  "tagInfo": "string",
  "insertionTime": "2019-08-24T14:15:22Z"
}
```

<h3 id="createtagmeta-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|name|path|string|true|name: the tag name|
|body|body|[TagMetaDto](#schematagmetadto)|false|none|

> Example responses

> 200 Response

```json
{
  "tagName": "string",
  "description": "string",
  "chansize": 0,
  "colsize": 0,
  "tagInfo": "string",
  "insertionTime": "2019-08-24T14:15:22Z"
}
```

<h3 id="createtagmeta-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|successful operation|[TagMetaDto](#schematagmetadto)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|Not found|[HTTPResponse](#schemahttpresponse)|
|default|Default|Generic error response|[HTTPResponse](#schemahttpresponse)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth, OpenID ( Scopes: openid )
</aside>

## updateTagMeta

<a id="opIdupdateTagMeta"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://crest-j23.cern.ch/api-v5.0/tags/{name}/meta \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer {access-token}'

```

```http
PUT http://crest-j23.cern.ch/api-v5.0/tags/{name}/meta HTTP/1.1
Host: crest-j23.cern.ch
Content-Type: application/json
Accept: application/json

```

```javascript
const inputBody = '{
  "name": "string",
  "property1": "string",
  "property2": "string"
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'application/json',
  'Authorization':'Bearer {access-token}'
};

fetch('http://crest-j23.cern.ch/api-v5.0/tags/{name}/meta',
{
  method: 'PUT',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Content-Type' => 'application/json',
  'Accept' => 'application/json',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.put 'http://crest-j23.cern.ch/api-v5.0/tags/{name}/meta',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Content-Type': 'application/json',
  'Accept': 'application/json',
  'Authorization': 'Bearer {access-token}'
}

r = requests.put('http://crest-j23.cern.ch/api-v5.0/tags/{name}/meta', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Content-Type' => 'application/json',
    'Accept' => 'application/json',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('PUT','http://crest-j23.cern.ch/api-v5.0/tags/{name}/meta', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("http://crest-j23.cern.ch/api-v5.0/tags/{name}/meta");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("PUT");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Content-Type": []string{"application/json"},
        "Accept": []string{"application/json"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("PUT", "http://crest-j23.cern.ch/api-v5.0/tags/{name}/meta", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`PUT /tags/{name}/meta`

*Update a TagMetaDto by name*

This method will search for a tag with the given name, and update its content for the provided body fields. Only the following fields can be updated: description, timeType, objectTime, endOfValidity, lastValidatedTime.

> Body parameter

```json
{
  "name": "string",
  "property1": "string",
  "property2": "string"
}
```

<h3 id="updatetagmeta-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|name|path|string|true|name: the tag name|
|body|body|[GenericMap](#schemagenericmap)|false|none|

> Example responses

> 200 Response

```json
{
  "tagName": "string",
  "description": "string",
  "chansize": 0,
  "colsize": 0,
  "tagInfo": "string",
  "insertionTime": "2019-08-24T14:15:22Z"
}
```

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<TagMetaDto>
  <tagName>string</tagName>
  <description>string</description>
  <chansize>0</chansize>
  <colsize>0</colsize>
  <tagInfo>string</tagInfo>
  <insertionTime>2019-08-24T14:15:22Z</insertionTime>
</TagMetaDto>
```

<h3 id="updatetagmeta-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|successful operation|[TagMetaDto](#schematagmetadto)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|Not found|[HTTPResponse](#schemahttpresponse)|
|default|Default|Generic error response|[HTTPResponse](#schemahttpresponse)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth, OpenID ( Scopes: openid )
</aside>

<h1 id="crest-server-iovs">iovs</h1>

## findAllIovs

<a id="opIdfindAllIovs"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://crest-j23.cern.ch/api-v5.0/iovs?method=IOVS \
  -H 'Accept: application/json' \
  -H 'X-Crest-Query: IOVS' \
  -H 'X-Crest-Since: MS' \
  -H 'Authorization: Bearer {access-token}'

```

```http
GET http://crest-j23.cern.ch/api-v5.0/iovs?method=IOVS HTTP/1.1
Host: crest-j23.cern.ch
Accept: application/json
X-Crest-Query: IOVS
X-Crest-Since: MS

```

```javascript

const headers = {
  'Accept':'application/json',
  'X-Crest-Query':'IOVS',
  'X-Crest-Since':'MS',
  'Authorization':'Bearer {access-token}'
};

fetch('http://crest-j23.cern.ch/api-v5.0/iovs?method=IOVS',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Accept' => 'application/json',
  'X-Crest-Query' => 'IOVS',
  'X-Crest-Since' => 'MS',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.get 'http://crest-j23.cern.ch/api-v5.0/iovs',
  params: {
  'method' => 'string'
}, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Accept': 'application/json',
  'X-Crest-Query': 'IOVS',
  'X-Crest-Since': 'MS',
  'Authorization': 'Bearer {access-token}'
}

r = requests.get('http://crest-j23.cern.ch/api-v5.0/iovs', params={
  'method': 'IOVS'
}, headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Accept' => 'application/json',
    'X-Crest-Query' => 'IOVS',
    'X-Crest-Since' => 'MS',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('GET','http://crest-j23.cern.ch/api-v5.0/iovs', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("http://crest-j23.cern.ch/api-v5.0/iovs?method=IOVS");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("GET");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Accept": []string{"application/json"},
        "X-Crest-Query": []string{"IOVS"},
        "X-Crest-Since": []string{"MS"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("GET", "http://crest-j23.cern.ch/api-v5.0/iovs", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`GET /iovs`

*Finds a IovDtos lists.*

Retrieves IOVs, with parameterizable method and arguments

<h3 id="findalliovs-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|method|query|string|true|the method used will determine which query is executed|
|tagname|query|string|false|the tag name|
|snapshot|query|integer(int64)|false|snapshot: the snapshot time {0}|
|since|query|string|false|the since time as a string {0}|
|until|query|string|false|the until time as a string {INF}|
|timeformat|query|string|false|the format for since and until {number | ms | iso | run-lumi | custom (yyyyMMdd'T'HHmmssX)}|
|groupsize|query|integer(int64)|false|The group size represent the pagination type provided for GROUPS query method.|
|hash|query|string|false|the hash for searching specific IOV list for a given hash.|
|page|query|integer(int32)|false|the page number {0}|
|size|query|integer(int32)|false|the page size {10000}|
|sort|query|string|false|the sort pattern {id.since:ASC}|
|X-Crest-Query|header|string|false|The query type. The header parameter X-Crest-Query can be : iovs, ranges, at.|
|X-Crest-Since|header|string|false|The since type required in the query. It can be : ms, cool.|

#### Detailed descriptions

**method**: the method used will determine which query is executed
IOVS, RANGE and AT is a standard IOV query requiring a precise tag name
GROUPS is a group query type

**timeformat**: the format for since and until {number | ms | iso | run-lumi | custom (yyyyMMdd'T'HHmmssX)}
If timeformat is equal number, we just parse the argument as a long.

**groupsize**: The group size represent the pagination type provided for GROUPS query method.

**hash**: the hash for searching specific IOV list for a given hash.

**X-Crest-Query**: The query type. The header parameter X-Crest-Query can be : iovs, ranges, at.
The iovs represents an exclusive interval, while ranges and at include previous since.
This has an impact on how the since and until ranges are applied.

**X-Crest-Since**: The since type required in the query. It can be : ms, cool.
Since and until will be transformed in these units.
It differs from timeformat which indicates how to interpret the since and until
strings in input.

#### Enumerated Values

|Parameter|Value|
|---|---|
|method|IOVS|
|method|GROUPS|
|method|MONITOR|
|timeformat|NUMBER|
|timeformat|MS|
|timeformat|ISO|
|timeformat|RUN|
|timeformat|RUN_LUMI|
|timeformat|CUSTOM|
|X-Crest-Query|IOVS|
|X-Crest-Query|RANGES|
|X-Crest-Query|AT|
|X-Crest-Since|MS|
|X-Crest-Since|COOL|
|X-Crest-Since|NUMBER|

> Example responses

> 200 Response

```json
{
  "resources": [
    {
      "tagName": "string",
      "since": 0,
      "insertionTime": "2019-08-24T14:15:22Z",
      "payloadHash": "string"
    }
  ],
  "size": 0,
  "datatype": "string",
  "format": "string",
  "page": {
    "size": 0,
    "totalElements": 0,
    "totalPages": 0,
    "number": 0
  },
  "filter": {
    "name": "string",
    "property1": "string",
    "property2": "string"
  }
}
```

<h3 id="findalliovs-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|successful operation|[IovSetDto](#schemaiovsetdto)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|Not found|[HTTPResponse](#schemahttpresponse)|
|default|Default|Generic error response|[HTTPResponse](#schemahttpresponse)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth, OpenID ( Scopes: openid )
</aside>

## storeIovBatch

<a id="opIdstoreIovBatch"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://crest-j23.cern.ch/api-v5.0/iovs \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer {access-token}'

```

```http
POST http://crest-j23.cern.ch/api-v5.0/iovs HTTP/1.1
Host: crest-j23.cern.ch
Content-Type: application/json
Accept: application/json

```

```javascript
const inputBody = '{
  "resources": [
    {
      "tagName": "string",
      "since": 0,
      "insertionTime": "2019-08-24T14:15:22Z",
      "payloadHash": "string"
    }
  ],
  "size": 0,
  "datatype": "string",
  "format": "string",
  "page": {
    "size": 0,
    "totalElements": 0,
    "totalPages": 0,
    "number": 0
  },
  "filter": {
    "name": "string",
    "property1": "string",
    "property2": "string"
  }
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'application/json',
  'Authorization':'Bearer {access-token}'
};

fetch('http://crest-j23.cern.ch/api-v5.0/iovs',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Content-Type' => 'application/json',
  'Accept' => 'application/json',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.post 'http://crest-j23.cern.ch/api-v5.0/iovs',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Content-Type': 'application/json',
  'Accept': 'application/json',
  'Authorization': 'Bearer {access-token}'
}

r = requests.post('http://crest-j23.cern.ch/api-v5.0/iovs', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Content-Type' => 'application/json',
    'Accept' => 'application/json',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('POST','http://crest-j23.cern.ch/api-v5.0/iovs', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("http://crest-j23.cern.ch/api-v5.0/iovs");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("POST");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Content-Type": []string{"application/json"},
        "Accept": []string{"application/json"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("POST", "http://crest-j23.cern.ch/api-v5.0/iovs", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`POST /iovs`

*Create IOVs in the database, associated to a tag name.*

Insert a list of Iovs using an IovSetDto in the request body. It is mandatory
to provide an existing tag in input. The referenced payloads should already exists in the DB.

> Body parameter

```json
{
  "resources": [
    {
      "tagName": "string",
      "since": 0,
      "insertionTime": "2019-08-24T14:15:22Z",
      "payloadHash": "string"
    }
  ],
  "size": 0,
  "datatype": "string",
  "format": "string",
  "page": {
    "size": 0,
    "totalElements": 0,
    "totalPages": 0,
    "number": 0
  },
  "filter": {
    "name": "string",
    "property1": "string",
    "property2": "string"
  }
}
```

<h3 id="storeiovbatch-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[IovSetDto](#schemaiovsetdto)|false|none|

> Example responses

> 201 Response

```json
{
  "resources": [
    {
      "tagName": "string",
      "since": 0,
      "insertionTime": "2019-08-24T14:15:22Z",
      "payloadHash": "string"
    }
  ],
  "size": 0,
  "datatype": "string",
  "format": "string",
  "page": {
    "size": 0,
    "totalElements": 0,
    "totalPages": 0,
    "number": 0
  },
  "filter": {
    "name": "string",
    "property1": "string",
    "property2": "string"
  }
}
```

<h3 id="storeiovbatch-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|201|[Created](https://tools.ietf.org/html/rfc7231#section-6.3.2)|successful operation|[IovSetDto](#schemaiovsetdto)|
|default|Default|Generic error response|[HTTPResponse](#schemahttpresponse)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth, OpenID ( Scopes: openid )
</aside>

## storeIovOne

<a id="opIdstoreIovOne"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://crest-j23.cern.ch/api-v5.0/iovs \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer {access-token}'

```

```http
PUT http://crest-j23.cern.ch/api-v5.0/iovs HTTP/1.1
Host: crest-j23.cern.ch
Content-Type: application/json
Accept: application/json

```

```javascript
const inputBody = '{
  "tagName": "string",
  "since": 0,
  "insertionTime": "2019-08-24T14:15:22Z",
  "payloadHash": "string"
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'application/json',
  'Authorization':'Bearer {access-token}'
};

fetch('http://crest-j23.cern.ch/api-v5.0/iovs',
{
  method: 'PUT',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Content-Type' => 'application/json',
  'Accept' => 'application/json',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.put 'http://crest-j23.cern.ch/api-v5.0/iovs',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Content-Type': 'application/json',
  'Accept': 'application/json',
  'Authorization': 'Bearer {access-token}'
}

r = requests.put('http://crest-j23.cern.ch/api-v5.0/iovs', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Content-Type' => 'application/json',
    'Accept' => 'application/json',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('PUT','http://crest-j23.cern.ch/api-v5.0/iovs', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("http://crest-j23.cern.ch/api-v5.0/iovs");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("PUT");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Content-Type": []string{"application/json"},
        "Accept": []string{"application/json"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("PUT", "http://crest-j23.cern.ch/api-v5.0/iovs", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`PUT /iovs`

*Create a single IOV in the database, associated to a tag name.*

Insert an Iov using an IovDto in the request body. It is mandatory
to provide an existing tag in input. The referenced payloads should already exists in the DB.

> Body parameter

```json
{
  "tagName": "string",
  "since": 0,
  "insertionTime": "2019-08-24T14:15:22Z",
  "payloadHash": "string"
}
```

<h3 id="storeiovone-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[IovDto](#schemaiovdto)|false|none|

> Example responses

> 201 Response

```json
{
  "resources": [
    {
      "tagName": "string",
      "since": 0,
      "insertionTime": "2019-08-24T14:15:22Z",
      "payloadHash": "string"
    }
  ],
  "size": 0,
  "datatype": "string",
  "format": "string",
  "page": {
    "size": 0,
    "totalElements": 0,
    "totalPages": 0,
    "number": 0
  },
  "filter": {
    "name": "string",
    "property1": "string",
    "property2": "string"
  }
}
```

<h3 id="storeiovone-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|201|[Created](https://tools.ietf.org/html/rfc7231#section-6.3.2)|successful operation|[IovSetDto](#schemaiovsetdto)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad request|[HTTPResponse](#schemahttpresponse)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|Not found|[HTTPResponse](#schemahttpresponse)|
|default|Default|Generic error response|[HTTPResponse](#schemahttpresponse)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth, OpenID ( Scopes: openid )
</aside>

## selectIovPayloads

<a id="opIdselectIovPayloads"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://crest-j23.cern.ch/api-v5.0/iovs/infos?tagname=none \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer {access-token}'

```

```http
GET http://crest-j23.cern.ch/api-v5.0/iovs/infos?tagname=none HTTP/1.1
Host: crest-j23.cern.ch
Accept: application/json

```

```javascript

const headers = {
  'Accept':'application/json',
  'Authorization':'Bearer {access-token}'
};

fetch('http://crest-j23.cern.ch/api-v5.0/iovs/infos?tagname=none',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Accept' => 'application/json',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.get 'http://crest-j23.cern.ch/api-v5.0/iovs/infos',
  params: {
  'tagname' => 'string'
}, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Accept': 'application/json',
  'Authorization': 'Bearer {access-token}'
}

r = requests.get('http://crest-j23.cern.ch/api-v5.0/iovs/infos', params={
  'tagname': 'none'
}, headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Accept' => 'application/json',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('GET','http://crest-j23.cern.ch/api-v5.0/iovs/infos', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("http://crest-j23.cern.ch/api-v5.0/iovs/infos?tagname=none");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("GET");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Accept": []string{"application/json"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("GET", "http://crest-j23.cern.ch/api-v5.0/iovs/infos", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`GET /iovs/infos`

*Select iovs and payload meta info for a given tagname and in a given range.*

Retrieve a list of iovs with payload metadata associated. The arguments are:
tagname={a tag name}, since={since time as string}, until={until time as string}, snapshot={snapshot time as long}'
and timeformat={format of since/until}.

<h3 id="selectiovpayloads-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|tagname|query|string|true|the tag name|
|since|query|string|false|the since time as a string {0}|
|until|query|string|false|the until time as a string {INF}|
|timeformat|query|string|false|the format for since and until {number | ms | iso | custom (yyyyMMdd'T'HHmmssX)}|
|page|query|integer(int32)|false|the page number {0}|
|size|query|integer(int32)|false|the page size {10000}|
|sort|query|string|false|the sort pattern {id.since:ASC}|

#### Detailed descriptions

**timeformat**: the format for since and until {number | ms | iso | custom (yyyyMMdd'T'HHmmssX)}
If timeformat is equal number, we just parse the argument as a long.

> Example responses

> 200 Response

```json
{
  "resources": [
    {
      "since": 0,
      "insertionTime": "2019-08-24T14:15:22Z",
      "version": "string",
      "objectType": "string",
      "objectName": "string",
      "compressionType": "string",
      "size": 0,
      "payloadHash": "string"
    }
  ],
  "size": 0,
  "datatype": "string",
  "format": "string",
  "page": {
    "size": 0,
    "totalElements": 0,
    "totalPages": 0,
    "number": 0
  },
  "filter": {
    "name": "string",
    "property1": "string",
    "property2": "string"
  }
}
```

<h3 id="selectiovpayloads-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|successful operation|[IovPayloadSetDto](#schemaiovpayloadsetdto)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|Not found|[HTTPResponse](#schemahttpresponse)|
|default|Default|Generic error response|[HTTPResponse](#schemahttpresponse)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth, OpenID ( Scopes: openid )
</aside>

## getSizeByTag

<a id="opIdgetSizeByTag"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://crest-j23.cern.ch/api-v5.0/iovs/size?tagname=none \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer {access-token}'

```

```http
GET http://crest-j23.cern.ch/api-v5.0/iovs/size?tagname=none HTTP/1.1
Host: crest-j23.cern.ch
Accept: application/json

```

```javascript

const headers = {
  'Accept':'application/json',
  'Authorization':'Bearer {access-token}'
};

fetch('http://crest-j23.cern.ch/api-v5.0/iovs/size?tagname=none',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Accept' => 'application/json',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.get 'http://crest-j23.cern.ch/api-v5.0/iovs/size',
  params: {
  'tagname' => 'string'
}, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Accept': 'application/json',
  'Authorization': 'Bearer {access-token}'
}

r = requests.get('http://crest-j23.cern.ch/api-v5.0/iovs/size', params={
  'tagname': 'none'
}, headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Accept' => 'application/json',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('GET','http://crest-j23.cern.ch/api-v5.0/iovs/size', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("http://crest-j23.cern.ch/api-v5.0/iovs/size?tagname=none");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("GET");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Accept": []string{"application/json"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("GET", "http://crest-j23.cern.ch/api-v5.0/iovs/size", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`GET /iovs/size`

*Get the number o iovs for tags matching pattern.*

This method allows to retrieve the number of iovs in a tag (or pattern).

<h3 id="getsizebytag-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|tagname|query|string|true|the tag name, can be a pattern like MDT%|

> Example responses

> 200 Response

```json
{
  "resources": [
    {
      "tagname": "string",
      "niovs": 0
    }
  ],
  "size": 0,
  "datatype": "string",
  "format": "string",
  "page": {
    "size": 0,
    "totalElements": 0,
    "totalPages": 0,
    "number": 0
  },
  "filter": {
    "name": "string",
    "property1": "string",
    "property2": "string"
  }
}
```

<h3 id="getsizebytag-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|successful operation|[TagSummarySetDto](#schematagsummarysetdto)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|Not found|[HTTPResponse](#schemahttpresponse)|
|default|Default|Generic error response|[HTTPResponse](#schemahttpresponse)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth, OpenID ( Scopes: openid )
</aside>

<h1 id="crest-server-monitoring">monitoring</h1>

## listPayloadTagInfo

<a id="opIdlistPayloadTagInfo"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://crest-j23.cern.ch/api-v5.0/monitoring/payloads \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer {access-token}'

```

```http
GET http://crest-j23.cern.ch/api-v5.0/monitoring/payloads HTTP/1.1
Host: crest-j23.cern.ch
Accept: application/json

```

```javascript

const headers = {
  'Accept':'application/json',
  'Authorization':'Bearer {access-token}'
};

fetch('http://crest-j23.cern.ch/api-v5.0/monitoring/payloads',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Accept' => 'application/json',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.get 'http://crest-j23.cern.ch/api-v5.0/monitoring/payloads',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Accept': 'application/json',
  'Authorization': 'Bearer {access-token}'
}

r = requests.get('http://crest-j23.cern.ch/api-v5.0/monitoring/payloads', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Accept' => 'application/json',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('GET','http://crest-j23.cern.ch/api-v5.0/monitoring/payloads', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("http://crest-j23.cern.ch/api-v5.0/monitoring/payloads");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("GET");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Accept": []string{"application/json"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("GET", "http://crest-j23.cern.ch/api-v5.0/monitoring/payloads", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`GET /monitoring/payloads`

*Retrieves monitoring information on payload as a list of PayloadTagInfoDtos.*

This method allows to perform search and sorting.Arguments: tagname=<pattern>, page={ipage}, size={isize}, sort=<sortpattern>. The pattern <pattern> is in the form <param-name><operation><param-value>       <param-name> is the name of one of the fields in the dto       <operation> can be [< : >] ; for string use only [:]        <param-value> depends on the chosen parameter. A list of this criteria can be provided       using comma separated strings for <pattern>.      The pattern <sortpattern> is <field>:[DESC|ASC]

<h3 id="listpayloadtaginfo-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|tagname|query|string|false|tagname: the search pattern {none}|

> Example responses

> 200 Response

```json
{
  "resources": [
    {
      "tagname": "string",
      "niovs": 0,
      "totvolume": 0.1,
      "avgvolume": 0.1
    }
  ],
  "size": 0,
  "datatype": "string",
  "format": "string",
  "page": {
    "size": 0,
    "totalElements": 0,
    "totalPages": 0,
    "number": 0
  },
  "filter": {
    "name": "string",
    "property1": "string",
    "property2": "string"
  }
}
```

<h3 id="listpayloadtaginfo-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|successful operation|[PayloadTagInfoSetDto](#schemapayloadtaginfosetdto)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|Not found|[HTTPResponse](#schemahttpresponse)|
|default|Default|Generic error response|[HTTPResponse](#schemahttpresponse)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth, OpenID ( Scopes: openid )
</aside>

<h1 id="crest-server-payloads">payloads</h1>

## storePayloadBatch

<a id="opIdstorePayloadBatch"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://crest-j23.cern.ch/api-v5.0/payloads \
  -H 'Content-Type: multipart/form-data' \
  -H 'Accept: application/json' \
  -H 'X-Crest-PayloadFormat: FILE' \
  -H 'Authorization: Bearer {access-token}'

```

```http
POST http://crest-j23.cern.ch/api-v5.0/payloads HTTP/1.1
Host: crest-j23.cern.ch
Content-Type: multipart/form-data
Accept: application/json
X-Crest-PayloadFormat: FILE

```

```javascript
const inputBody = '{
  "tag": "string",
  "storeset": "string",
  "files": [
    "string"
  ],
  "objectType": "string",
  "compressionType": "string",
  "version": "string",
  "endtime": "string"
}';
const headers = {
  'Content-Type':'multipart/form-data',
  'Accept':'application/json',
  'X-Crest-PayloadFormat':'FILE',
  'Authorization':'Bearer {access-token}'
};

fetch('http://crest-j23.cern.ch/api-v5.0/payloads',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Content-Type' => 'multipart/form-data',
  'Accept' => 'application/json',
  'X-Crest-PayloadFormat' => 'FILE',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.post 'http://crest-j23.cern.ch/api-v5.0/payloads',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Content-Type': 'multipart/form-data',
  'Accept': 'application/json',
  'X-Crest-PayloadFormat': 'FILE',
  'Authorization': 'Bearer {access-token}'
}

r = requests.post('http://crest-j23.cern.ch/api-v5.0/payloads', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Content-Type' => 'multipart/form-data',
    'Accept' => 'application/json',
    'X-Crest-PayloadFormat' => 'FILE',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('POST','http://crest-j23.cern.ch/api-v5.0/payloads', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("http://crest-j23.cern.ch/api-v5.0/payloads");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("POST");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Content-Type": []string{"multipart/form-data"},
        "Accept": []string{"application/json"},
        "X-Crest-PayloadFormat": []string{"FILE"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("POST", "http://crest-j23.cern.ch/api-v5.0/payloads", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`POST /payloads`

*Create Payloads in the database, associated to a given iov since list and tag name.*

This method allows to insert list of Payloads and IOVs.
Payload can be contained in the HASH of the IOV (in case it is a small JSON)
or as a reference to external file (FILE).
In the first case, the files list can be null.
Arguments: tag,version,endtime,objectType,compressionType
The header parameter X-Crest-PayloadFormat can be FILE or JSON

> Body parameter

```yaml
tag: string
storeset: string
files:
  - string
objectType: string
compressionType: string
version: string
endtime: string

```

<h3 id="storepayloadbatch-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|X-Crest-PayloadFormat|header|string|false|The format of the input data. StoreSetDto entries will have either the content inline (JSON)|
|body|body|object|false|A json string that is used to construct the form data object.|
|» tag|body|string|true|The tag name|
|» storeset|body|string|true|the string representing a StoreSetDto in json|
|» files|body|[string]|false|The payload files as an array of streams|
|» objectType|body|string|false|The object type|
|» compressionType|body|string|false|The compression type|
|» version|body|string|false|The version|
|» endtime|body|string|false|The tag end time. This represents a number.|

#### Detailed descriptions

**X-Crest-PayloadFormat**: The format of the input data. StoreSetDto entries will have either the content inline (JSON)
or stored via external files (FILE).

#### Enumerated Values

|Parameter|Value|
|---|---|
|X-Crest-PayloadFormat|FILE|
|X-Crest-PayloadFormat|JSON|

> Example responses

> 201 Response

```json
{
  "resources": [
    {
      "hash": "string",
      "since": 0,
      "data": "string",
      "streamerInfo": "string"
    }
  ],
  "size": 0,
  "datatype": "string",
  "format": "string",
  "page": {
    "size": 0,
    "totalElements": 0,
    "totalPages": 0,
    "number": 0
  },
  "filter": {
    "name": "string",
    "property1": "string",
    "property2": "string"
  }
}
```

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<StoreSetDto>
  <resources>
    <hash>string</hash>
    <since>0</since>
    <data>string</data>
    <streamerInfo>string</streamerInfo>
  </resources>
  <size>0</size>
  <datatype>string</datatype>
  <format>string</format>
  <page>
    <size>0</size>
    <totalElements>0</totalElements>
    <totalPages>0</totalPages>
    <number>0</number>
  </page>
  <filter>
    <name>string</name>
    <property1>string</property1>
    <property2>string</property2>
  </filter>
</StoreSetDto>
```

<h3 id="storepayloadbatch-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|201|[Created](https://tools.ietf.org/html/rfc7231#section-6.3.2)|successful operation|[StoreSetDto](#schemastoresetdto)|
|default|Default|Generic error response|[HTTPResponse](#schemahttpresponse)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth, OpenID ( Scopes: openid )
</aside>

## listPayloads

<a id="opIdlistPayloads"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://crest-j23.cern.ch/api-v5.0/payloads \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer {access-token}'

```

```http
GET http://crest-j23.cern.ch/api-v5.0/payloads HTTP/1.1
Host: crest-j23.cern.ch
Accept: application/json

```

```javascript

const headers = {
  'Accept':'application/json',
  'Authorization':'Bearer {access-token}'
};

fetch('http://crest-j23.cern.ch/api-v5.0/payloads',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Accept' => 'application/json',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.get 'http://crest-j23.cern.ch/api-v5.0/payloads',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Accept': 'application/json',
  'Authorization': 'Bearer {access-token}'
}

r = requests.get('http://crest-j23.cern.ch/api-v5.0/payloads', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Accept' => 'application/json',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('GET','http://crest-j23.cern.ch/api-v5.0/payloads', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("http://crest-j23.cern.ch/api-v5.0/payloads");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("GET");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Accept": []string{"application/json"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("GET", "http://crest-j23.cern.ch/api-v5.0/payloads", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`GET /payloads`

*Finds Payloads metadata.*

This method allows to perform search and sorting.
Arguments: hash=<the payload hash>, minsize=<min size>, objectType=<the type>
page={ipage}, size={isize}, sort=<sortpattern>.

<h3 id="listpayloads-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|hash|query|string|false|the hash to search {none}|
|objectType|query|string|false|the objectType to search|
|minsize|query|integer(int32)|false|the minimum size to search|
|page|query|integer(int32)|false|page: the page number {0}|
|size|query|integer(int32)|false|size: the page size {1000}|
|sort|query|string|false|sort: the sort pattern {insertionTime:DESC}|

> Example responses

> 200 Response

```json
{
  "resources": [
    {
      "hash": "string",
      "version": "string",
      "objectType": "string",
      "objectName": "string",
      "compressionType": "string",
      "checkSum": "string",
      "size": 0,
      "insertionTime": "2019-08-24T14:15:22Z"
    }
  ],
  "size": 0,
  "datatype": "string",
  "format": "string",
  "page": {
    "size": 0,
    "totalElements": 0,
    "totalPages": 0,
    "number": 0
  },
  "filter": {
    "name": "string",
    "property1": "string",
    "property2": "string"
  }
}
```

<h3 id="listpayloads-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|successful operation|[PayloadSetDto](#schemapayloadsetdto)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|Not found|[HTTPResponse](#schemahttpresponse)|
|default|Default|Generic error response|[HTTPResponse](#schemahttpresponse)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth, OpenID ( Scopes: openid )
</aside>

## uploadJson

<a id="opIduploadJson"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://crest-j23.cern.ch/api-v5.0/payloads \
  -H 'Content-Type: multipart/form-data' \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer {access-token}'

```

```http
PUT http://crest-j23.cern.ch/api-v5.0/payloads HTTP/1.1
Host: crest-j23.cern.ch
Content-Type: multipart/form-data
Accept: application/json

```

```javascript
const inputBody = '{
  "tag": "string",
  "storeset": "string",
  "objectType": "string",
  "compressionType": "string",
  "version": "string",
  "endtime": "string"
}';
const headers = {
  'Content-Type':'multipart/form-data',
  'Accept':'application/json',
  'Authorization':'Bearer {access-token}'
};

fetch('http://crest-j23.cern.ch/api-v5.0/payloads',
{
  method: 'PUT',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Content-Type' => 'multipart/form-data',
  'Accept' => 'application/json',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.put 'http://crest-j23.cern.ch/api-v5.0/payloads',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Content-Type': 'multipart/form-data',
  'Accept': 'application/json',
  'Authorization': 'Bearer {access-token}'
}

r = requests.put('http://crest-j23.cern.ch/api-v5.0/payloads', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Content-Type' => 'multipart/form-data',
    'Accept' => 'application/json',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('PUT','http://crest-j23.cern.ch/api-v5.0/payloads', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("http://crest-j23.cern.ch/api-v5.0/payloads");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("PUT");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Content-Type": []string{"multipart/form-data"},
        "Accept": []string{"application/json"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("PUT", "http://crest-j23.cern.ch/api-v5.0/payloads", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`PUT /payloads`

*Upload and process large JSON data.*

> Body parameter

```yaml
tag: string
storeset: string
objectType: string
compressionType: string
version: string
endtime: string

```

<h3 id="uploadjson-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|object|true|none|
|» tag|body|string|true|The tag name|
|» storeset|body|string(binary)|true|the string representing a StoreSetDto in json|
|» objectType|body|string|false|The object type|
|» compressionType|body|string|false|The compression type|
|» version|body|string|false|The version|
|» endtime|body|string|false|The tag end time, represent a number.|

> Example responses

> 201 Response

```json
{
  "resources": [
    {
      "hash": "string",
      "since": 0,
      "data": "string",
      "streamerInfo": "string"
    }
  ],
  "size": 0,
  "datatype": "string",
  "format": "string",
  "page": {
    "size": 0,
    "totalElements": 0,
    "totalPages": 0,
    "number": 0
  },
  "filter": {
    "name": "string",
    "property1": "string",
    "property2": "string"
  }
}
```

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<StoreSetDto>
  <resources>
    <hash>string</hash>
    <since>0</since>
    <data>string</data>
    <streamerInfo>string</streamerInfo>
  </resources>
  <size>0</size>
  <datatype>string</datatype>
  <format>string</format>
  <page>
    <size>0</size>
    <totalElements>0</totalElements>
    <totalPages>0</totalPages>
    <number>0</number>
  </page>
  <filter>
    <name>string</name>
    <property1>string</property1>
    <property2>string</property2>
  </filter>
</StoreSetDto>
```

<h3 id="uploadjson-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|201|[Created](https://tools.ietf.org/html/rfc7231#section-6.3.2)|successful operation|[StoreSetDto](#schemastoresetdto)|
|default|Default|Generic error response|[HTTPResponse](#schemahttpresponse)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth, OpenID ( Scopes: openid )
</aside>

## getPayload

<a id="opIdgetPayload"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://crest-j23.cern.ch/api-v5.0/payloads/data?hash=string&format=BLOB \
  -H 'Accept: application/octet-stream' \
  -H 'Authorization: Bearer {access-token}'

```

```http
GET http://crest-j23.cern.ch/api-v5.0/payloads/data?hash=string&format=BLOB HTTP/1.1
Host: crest-j23.cern.ch
Accept: application/octet-stream

```

```javascript

const headers = {
  'Accept':'application/octet-stream',
  'Authorization':'Bearer {access-token}'
};

fetch('http://crest-j23.cern.ch/api-v5.0/payloads/data?hash=string&format=BLOB',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Accept' => 'application/octet-stream',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.get 'http://crest-j23.cern.ch/api-v5.0/payloads/data',
  params: {
  'hash' => 'string',
'format' => 'string'
}, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Accept': 'application/octet-stream',
  'Authorization': 'Bearer {access-token}'
}

r = requests.get('http://crest-j23.cern.ch/api-v5.0/payloads/data', params={
  'hash': 'string',  'format': 'BLOB'
}, headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Accept' => 'application/octet-stream',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('GET','http://crest-j23.cern.ch/api-v5.0/payloads/data', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("http://crest-j23.cern.ch/api-v5.0/payloads/data?hash=string&format=BLOB");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("GET");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Accept": []string{"application/octet-stream"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("GET", "http://crest-j23.cern.ch/api-v5.0/payloads/data", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`GET /payloads/data`

*Finds a payload resource associated to the hash.*

This method retrieves a payload resource.
Arguments: hash=<hash> the hash of the payload
Depending on the header, this method will either retrieve the data, the metadata of the payload 
or the streamerInfo alone.

<h3 id="getpayload-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|hash|query|string|true|hash:  the hash of the payload|
|format|query|string|true|The format of the output data. |

#### Detailed descriptions

**format**: The format of the output data. 
It can be : BLOB (default), META (meta data) or STREAMER (streamerInfo).

#### Enumerated Values

|Parameter|Value|
|---|---|
|format|BLOB|
|format|META|
|format|STREAMER|

> Example responses

> 200 Response

```json
{
  "hash": "string",
  "version": "string",
  "objectType": "string",
  "objectName": "string",
  "compressionType": "string",
  "checkSum": "string",
  "size": 0,
  "insertionTime": "2019-08-24T14:15:22Z"
}
```

<h3 id="getpayload-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|successful operation|[PayloadDto](#schemapayloaddto)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|Not found|[HTTPResponse](#schemahttpresponse)|
|default|Default|Generic error response|[HTTPResponse](#schemahttpresponse)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth, OpenID ( Scopes: openid )
</aside>

## updatePayload

<a id="opIdupdatePayload"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://crest-j23.cern.ch/api-v5.0/payloads/data \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer {access-token}'

```

```http
PUT http://crest-j23.cern.ch/api-v5.0/payloads/data HTTP/1.1
Host: crest-j23.cern.ch
Content-Type: application/json
Accept: application/json

```

```javascript
const inputBody = '{
  "name": "string",
  "property1": "string",
  "property2": "string"
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'application/json',
  'Authorization':'Bearer {access-token}'
};

fetch('http://crest-j23.cern.ch/api-v5.0/payloads/data',
{
  method: 'PUT',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Content-Type' => 'application/json',
  'Accept' => 'application/json',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.put 'http://crest-j23.cern.ch/api-v5.0/payloads/data',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Content-Type': 'application/json',
  'Accept': 'application/json',
  'Authorization': 'Bearer {access-token}'
}

r = requests.put('http://crest-j23.cern.ch/api-v5.0/payloads/data', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Content-Type' => 'application/json',
    'Accept' => 'application/json',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('PUT','http://crest-j23.cern.ch/api-v5.0/payloads/data', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("http://crest-j23.cern.ch/api-v5.0/payloads/data");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("PUT");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Content-Type": []string{"application/json"},
        "Accept": []string{"application/json"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("PUT", "http://crest-j23.cern.ch/api-v5.0/payloads/data", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`PUT /payloads/data`

*Update a streamerInfo in a payload*

This method will update the streamerInfo.
This is provided via a generic map in the request body containing the key 'streamerInfo'

> Body parameter

```json
{
  "name": "string",
  "property1": "string",
  "property2": "string"
}
```

<h3 id="updatepayload-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|hash|path|string|true|hash:  the hash of the payload|
|body|body|[GenericMap](#schemagenericmap)|false|none|

> Example responses

> 200 Response

```json
{
  "hash": "string",
  "version": "string",
  "objectType": "string",
  "objectName": "string",
  "compressionType": "string",
  "checkSum": "string",
  "size": 0,
  "insertionTime": "2019-08-24T14:15:22Z"
}
```

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<PayloadDto>
  <hash>string</hash>
  <version>string</version>
  <objectType>string</objectType>
  <objectName>string</objectName>
  <compressionType>string</compressionType>
  <checkSum>string</checkSum>
  <size>0</size>
  <insertionTime>2019-08-24T14:15:22Z</insertionTime>
</PayloadDto>
```

<h3 id="updatepayload-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|successful operation|[PayloadDto](#schemapayloaddto)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|Not found|[HTTPResponse](#schemahttpresponse)|
|default|Default|Generic error response|[HTTPResponse](#schemahttpresponse)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth, OpenID ( Scopes: openid )
</aside>

<h1 id="crest-server-runinfo">runinfo</h1>

## createRunInfo

<a id="opIdcreateRunInfo"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://crest-j23.cern.ch/api-v5.0/runinfo \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer {access-token}'

```

```http
POST http://crest-j23.cern.ch/api-v5.0/runinfo HTTP/1.1
Host: crest-j23.cern.ch
Content-Type: application/json
Accept: application/json

```

```javascript
const inputBody = '{
  "resources": [
    {
      "runNumber": 0,
      "lb": 0,
      "starttime": 0,
      "endtime": 0
    }
  ],
  "size": 0,
  "datatype": "string",
  "format": "string",
  "page": {
    "size": 0,
    "totalElements": 0,
    "totalPages": 0,
    "number": 0
  },
  "filter": {
    "name": "string",
    "property1": "string",
    "property2": "string"
  }
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'application/json',
  'Authorization':'Bearer {access-token}'
};

fetch('http://crest-j23.cern.ch/api-v5.0/runinfo',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Content-Type' => 'application/json',
  'Accept' => 'application/json',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.post 'http://crest-j23.cern.ch/api-v5.0/runinfo',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Content-Type': 'application/json',
  'Accept': 'application/json',
  'Authorization': 'Bearer {access-token}'
}

r = requests.post('http://crest-j23.cern.ch/api-v5.0/runinfo', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Content-Type' => 'application/json',
    'Accept' => 'application/json',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('POST','http://crest-j23.cern.ch/api-v5.0/runinfo', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("http://crest-j23.cern.ch/api-v5.0/runinfo");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("POST");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Content-Type": []string{"application/json"},
        "Accept": []string{"application/json"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("POST", "http://crest-j23.cern.ch/api-v5.0/runinfo", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`POST /runinfo`

*Create an entry for run information.*

Run informations go into a separate table.

> Body parameter

```json
{
  "resources": [
    {
      "runNumber": 0,
      "lb": 0,
      "starttime": 0,
      "endtime": 0
    }
  ],
  "size": 0,
  "datatype": "string",
  "format": "string",
  "page": {
    "size": 0,
    "totalElements": 0,
    "totalPages": 0,
    "number": 0
  },
  "filter": {
    "name": "string",
    "property1": "string",
    "property2": "string"
  }
}
```

<h3 id="createruninfo-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[RunLumiSetDto](#schemarunlumisetdto)|false|none|

> Example responses

> 200 Response

```json
{
  "resources": [
    {
      "runNumber": 0,
      "lb": 0,
      "starttime": 0,
      "endtime": 0
    }
  ],
  "size": 0,
  "datatype": "string",
  "format": "string",
  "page": {
    "size": 0,
    "totalElements": 0,
    "totalPages": 0,
    "number": 0
  },
  "filter": {
    "name": "string",
    "property1": "string",
    "property2": "string"
  }
}
```

<h3 id="createruninfo-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|successful operation|[RunLumiSetDto](#schemarunlumisetdto)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth, OpenID ( Scopes: openid )
</aside>

## updateRunInfo

<a id="opIdupdateRunInfo"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://crest-j23.cern.ch/api-v5.0/runinfo \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer {access-token}'

```

```http
PUT http://crest-j23.cern.ch/api-v5.0/runinfo HTTP/1.1
Host: crest-j23.cern.ch
Content-Type: application/json
Accept: application/json

```

```javascript
const inputBody = '{
  "runNumber": 0,
  "lb": 0,
  "starttime": 0,
  "endtime": 0
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'application/json',
  'Authorization':'Bearer {access-token}'
};

fetch('http://crest-j23.cern.ch/api-v5.0/runinfo',
{
  method: 'PUT',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Content-Type' => 'application/json',
  'Accept' => 'application/json',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.put 'http://crest-j23.cern.ch/api-v5.0/runinfo',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Content-Type': 'application/json',
  'Accept': 'application/json',
  'Authorization': 'Bearer {access-token}'
}

r = requests.put('http://crest-j23.cern.ch/api-v5.0/runinfo', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Content-Type' => 'application/json',
    'Accept' => 'application/json',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('PUT','http://crest-j23.cern.ch/api-v5.0/runinfo', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("http://crest-j23.cern.ch/api-v5.0/runinfo");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("PUT");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Content-Type": []string{"application/json"},
        "Accept": []string{"application/json"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("PUT", "http://crest-j23.cern.ch/api-v5.0/runinfo", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`PUT /runinfo`

*Update an entry for run information.*

Run informations go into a separate table. To update an entry, the run number and the lumi section must be provided.

> Body parameter

```json
{
  "runNumber": 0,
  "lb": 0,
  "starttime": 0,
  "endtime": 0
}
```

<h3 id="updateruninfo-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[RunLumiInfoDto](#schemarunlumiinfodto)|false|none|

> Example responses

> 200 Response

```json
{
  "resources": [
    {
      "runNumber": 0,
      "lb": 0,
      "starttime": 0,
      "endtime": 0
    }
  ],
  "size": 0,
  "datatype": "string",
  "format": "string",
  "page": {
    "size": 0,
    "totalElements": 0,
    "totalPages": 0,
    "number": 0
  },
  "filter": {
    "name": "string",
    "property1": "string",
    "property2": "string"
  }
}
```

<h3 id="updateruninfo-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|successful operation|[RunLumiSetDto](#schemarunlumisetdto)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth, OpenID ( Scopes: openid )
</aside>

## listRunInfo

<a id="opIdlistRunInfo"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://crest-j23.cern.ch/api-v5.0/runinfo \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer {access-token}'

```

```http
GET http://crest-j23.cern.ch/api-v5.0/runinfo HTTP/1.1
Host: crest-j23.cern.ch
Accept: application/json

```

```javascript

const headers = {
  'Accept':'application/json',
  'Authorization':'Bearer {access-token}'
};

fetch('http://crest-j23.cern.ch/api-v5.0/runinfo',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```ruby
require 'rest-client'
require 'json'

headers = {
  'Accept' => 'application/json',
  'Authorization' => 'Bearer {access-token}'
}

result = RestClient.get 'http://crest-j23.cern.ch/api-v5.0/runinfo',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Accept': 'application/json',
  'Authorization': 'Bearer {access-token}'
}

r = requests.get('http://crest-j23.cern.ch/api-v5.0/runinfo', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Accept' => 'application/json',
    'Authorization' => 'Bearer {access-token}',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('GET','http://crest-j23.cern.ch/api-v5.0/runinfo', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("http://crest-j23.cern.ch/api-v5.0/runinfo");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("GET");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Accept": []string{"application/json"},
        "Authorization": []string{"Bearer {access-token}"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("GET", "http://crest-j23.cern.ch/api-v5.0/runinfo", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`GET /runinfo`

*Finds a RunLumiInfoDto lists using parameters.*

This method allows to perform search.Arguments: from=<someformat>,to=<someformat>, format=<describe previous types>, page={ipage}, size={isize}, sort=<sortpattern>. The pattern <pattern> is in the form <param-name><operation><param-value>       <param-name> is the name of one of the fields in the dto       <operation> can be [< : >] ; for string use only [:]        <param-value> depends on the chosen parameter. A list of this criteria can be provided       using comma separated strings for <pattern>.      The pattern <sortpattern> is <field>:[DESC|ASC]

<h3 id="listruninfo-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|since|query|string|false|since: the starting time or run-lumi|
|until|query|string|false|until: the ending time or run-lumi|
|format|query|string|false|the format to digest previous arguments [iso], [number], [run-lumi].|
|mode|query|string|false|the mode for the request : [daterange] or [runrange]|
|page|query|integer(int32)|false|page: the page number {0}|
|size|query|integer(int32)|false|size: the page size {1000}|
|sort|query|string|false|sort: the sort pattern {id.runNumber:ASC}|

#### Detailed descriptions

**format**: the format to digest previous arguments [iso], [number], [run-lumi].
Time(iso) = yyyymmddhhmiss, 
Time(number) = milliseconds or Run(number) = runnumber
Run(run-lumi) = runnumber-lumisection

**mode**: the mode for the request : [daterange] or [runrange]

#### Enumerated Values

|Parameter|Value|
|---|---|
|mode|daterange|
|mode|runrange|

> Example responses

> 200 Response

```json
{
  "resources": [
    {
      "runNumber": 0,
      "lb": 0,
      "starttime": 0,
      "endtime": 0
    }
  ],
  "size": 0,
  "datatype": "string",
  "format": "string",
  "page": {
    "size": 0,
    "totalElements": 0,
    "totalPages": 0,
    "number": 0
  },
  "filter": {
    "name": "string",
    "property1": "string",
    "property2": "string"
  }
}
```

<h3 id="listruninfo-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|successful operation|[RunLumiSetDto](#schemarunlumisetdto)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
BearerAuth, OpenID ( Scopes: openid )
</aside>

# Schemas

<h2 id="tocS_HTTPResponse">HTTPResponse</h2>
<!-- backwards compatibility -->
<a id="schemahttpresponse"></a>
<a id="schema_HTTPResponse"></a>
<a id="tocShttpresponse"></a>
<a id="tocshttpresponse"></a>

```json
{
  "timestamp": "2019-08-24T14:15:22Z",
  "code": 0,
  "error": "string",
  "type": "string",
  "message": "string",
  "id": "string"
}

```

general response object that can be used for POST and PUT methods

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|timestamp|string(date-time)|false|none|none|
|code|integer|true|none|HTTP status code of the response|
|error|string|false|none|none|
|type|string|false|none|A generic string specifying the exception type.|
|message|string|true|none|none|
|id|string|false|none|path or URI of the requested or generated resource|

<h2 id="tocS_CrestBaseResponse">CrestBaseResponse</h2>
<!-- backwards compatibility -->
<a id="schemacrestbaseresponse"></a>
<a id="schema_CrestBaseResponse"></a>
<a id="tocScrestbaseresponse"></a>
<a id="tocscrestbaseresponse"></a>

```json
{
  "size": 0,
  "datatype": "string",
  "format": "string",
  "page": {
    "size": 0,
    "totalElements": 0,
    "totalPages": 0,
    "number": 0
  },
  "filter": {
    "name": "string",
    "property1": "string",
    "property2": "string"
  }
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|size|integer(int64)|true|none|none|
|datatype|string|false|none|none|
|format|string|true|none|none|
|page|[RespPage](#schemaresppage)|false|none|none|
|filter|[GenericMap](#schemagenericmap)|false|none|none|

<h2 id="tocS_RespPage">RespPage</h2>
<!-- backwards compatibility -->
<a id="schemaresppage"></a>
<a id="schema_RespPage"></a>
<a id="tocSresppage"></a>
<a id="tocsresppage"></a>

```json
{
  "size": 0,
  "totalElements": 0,
  "totalPages": 0,
  "number": 0
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|size|integer|true|none|none|
|totalElements|integer(int64)|true|none|none|
|totalPages|integer|true|none|none|
|number|integer|true|none|none|

<h2 id="tocS_GenericMap">GenericMap</h2>
<!-- backwards compatibility -->
<a id="schemagenericmap"></a>
<a id="schema_GenericMap"></a>
<a id="tocSgenericmap"></a>
<a id="tocsgenericmap"></a>

```json
{
  "name": "string",
  "property1": "string",
  "property2": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|**additionalProperties**|string|false|none|none|
|name|string|false|none|none|

<h2 id="tocS_TagDto">TagDto</h2>
<!-- backwards compatibility -->
<a id="schematagdto"></a>
<a id="schema_TagDto"></a>
<a id="tocStagdto"></a>
<a id="tocstagdto"></a>

```json
{
  "name": "string",
  "timeType": "string",
  "payloadSpec": "string",
  "synchronization": "string",
  "description": "string",
  "lastValidatedTime": 0,
  "endOfValidity": 0,
  "insertionTime": "2019-08-24T14:15:22Z",
  "modificationTime": "2019-08-24T14:15:22Z"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|name|string|false|none|none|
|timeType|string|false|none|none|
|payloadSpec|string|false|none|none|
|synchronization|string|false|none|none|
|description|string|false|none|none|
|lastValidatedTime|integer(int64)|false|none|none|
|endOfValidity|integer(int64)|false|none|none|
|insertionTime|string(date-time)|false|none|none|
|modificationTime|string(date-time)|false|none|none|

<h2 id="tocS_TagSetDto">TagSetDto</h2>
<!-- backwards compatibility -->
<a id="schematagsetdto"></a>
<a id="schema_TagSetDto"></a>
<a id="tocStagsetdto"></a>
<a id="tocstagsetdto"></a>

```json
{
  "resources": [
    {
      "name": "string",
      "timeType": "string",
      "payloadSpec": "string",
      "synchronization": "string",
      "description": "string",
      "lastValidatedTime": 0,
      "endOfValidity": 0,
      "insertionTime": "2019-08-24T14:15:22Z",
      "modificationTime": "2019-08-24T14:15:22Z"
    }
  ],
  "size": 0,
  "datatype": "string",
  "format": "string",
  "page": {
    "size": 0,
    "totalElements": 0,
    "totalPages": 0,
    "number": 0
  },
  "filter": {
    "name": "string",
    "property1": "string",
    "property2": "string"
  }
}

```

An Set containing TagDto objects.

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|resources|[[TagDto](#schematagdto)]|false|none|none|

<h2 id="tocS_TagMetaSetDto">TagMetaSetDto</h2>
<!-- backwards compatibility -->
<a id="schematagmetasetdto"></a>
<a id="schema_TagMetaSetDto"></a>
<a id="tocStagmetasetdto"></a>
<a id="tocstagmetasetdto"></a>

```json
{
  "resources": [
    {
      "tagName": "string",
      "description": "string",
      "chansize": 0,
      "colsize": 0,
      "tagInfo": "string",
      "insertionTime": "2019-08-24T14:15:22Z"
    }
  ],
  "size": 0,
  "datatype": "string",
  "format": "string",
  "page": {
    "size": 0,
    "totalElements": 0,
    "totalPages": 0,
    "number": 0
  },
  "filter": {
    "name": "string",
    "property1": "string",
    "property2": "string"
  }
}

```

An Set containing TagMetaDto objects.

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|resources|[[TagMetaDto](#schematagmetadto)]|false|none|none|

<h2 id="tocS_TagMetaDto">TagMetaDto</h2>
<!-- backwards compatibility -->
<a id="schematagmetadto"></a>
<a id="schema_TagMetaDto"></a>
<a id="tocStagmetadto"></a>
<a id="tocstagmetadto"></a>

```json
{
  "tagName": "string",
  "description": "string",
  "chansize": 0,
  "colsize": 0,
  "tagInfo": "string",
  "insertionTime": "2019-08-24T14:15:22Z"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|tagName|string|false|none|none|
|description|string|false|none|none|
|chansize|integer(int32)|false|none|none|
|colsize|integer(int32)|false|none|none|
|tagInfo|string|false|none|none|
|insertionTime|string(date-time)|false|none|none|

<h2 id="tocS_GlobalTagDto">GlobalTagDto</h2>
<!-- backwards compatibility -->
<a id="schemaglobaltagdto"></a>
<a id="schema_GlobalTagDto"></a>
<a id="tocSglobaltagdto"></a>
<a id="tocsglobaltagdto"></a>

```json
{
  "name": "string",
  "validity": 0,
  "description": "string",
  "release": "string",
  "insertionTime": "2019-08-24T14:15:22Z",
  "snapshotTime": "2019-08-24T14:15:22Z",
  "scenario": "string",
  "workflow": "string",
  "type": "string",
  "snapshotTimeMilli": 0,
  "insertionTimeMilli": 0
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|name|string|false|none|none|
|validity|integer(int64)|false|none|none|
|description|string|false|none|none|
|release|string|false|none|none|
|insertionTime|string(date-time)|false|none|none|
|snapshotTime|string(date-time)|false|none|none|
|scenario|string|false|none|none|
|workflow|string|false|none|none|
|type|string|false|none|none|
|snapshotTimeMilli|integer(int64)|false|none|none|
|insertionTimeMilli|integer(int64)|false|none|none|

<h2 id="tocS_FolderDto">FolderDto</h2>
<!-- backwards compatibility -->
<a id="schemafolderdto"></a>
<a id="schema_FolderDto"></a>
<a id="tocSfolderdto"></a>
<a id="tocsfolderdto"></a>

```json
{
  "nodeFullpath": "string",
  "schemaName": "string",
  "nodeName": "string",
  "nodeDescription": "string",
  "tagPattern": "string",
  "groupRole": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|nodeFullpath|string|false|none|none|
|schemaName|string|false|none|none|
|nodeName|string|false|none|none|
|nodeDescription|string|false|none|none|
|tagPattern|string|false|none|none|
|groupRole|string|false|none|none|

<h2 id="tocS_FolderSetDto">FolderSetDto</h2>
<!-- backwards compatibility -->
<a id="schemafoldersetdto"></a>
<a id="schema_FolderSetDto"></a>
<a id="tocSfoldersetdto"></a>
<a id="tocsfoldersetdto"></a>

```json
{
  "resources": [
    {
      "nodeFullpath": "string",
      "schemaName": "string",
      "nodeName": "string",
      "nodeDescription": "string",
      "tagPattern": "string",
      "groupRole": "string"
    }
  ],
  "size": 0,
  "datatype": "string",
  "format": "string",
  "page": {
    "size": 0,
    "totalElements": 0,
    "totalPages": 0,
    "number": 0
  },
  "filter": {
    "name": "string",
    "property1": "string",
    "property2": "string"
  }
}

```

An FolderSet containing FolderDto objects.

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|resources|[[FolderDto](#schemafolderdto)]|false|none|none|

<h2 id="tocS_GlobalTagMapSetDto">GlobalTagMapSetDto</h2>
<!-- backwards compatibility -->
<a id="schemaglobaltagmapsetdto"></a>
<a id="schema_GlobalTagMapSetDto"></a>
<a id="tocSglobaltagmapsetdto"></a>
<a id="tocsglobaltagmapsetdto"></a>

```json
{
  "resources": [
    {
      "globalTagName": "string",
      "record": "string",
      "label": "string",
      "tagName": "string"
    }
  ],
  "size": 0,
  "datatype": "string",
  "format": "string",
  "page": {
    "size": 0,
    "totalElements": 0,
    "totalPages": 0,
    "number": 0
  },
  "filter": {
    "name": "string",
    "property1": "string",
    "property2": "string"
  }
}

```

An GlobalTagMapSet containing GlobalTagMapDto objects.

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|resources|[[GlobalTagMapDto](#schemaglobaltagmapdto)]|false|none|none|

<h2 id="tocS_GlobalTagMapDto">GlobalTagMapDto</h2>
<!-- backwards compatibility -->
<a id="schemaglobaltagmapdto"></a>
<a id="schema_GlobalTagMapDto"></a>
<a id="tocSglobaltagmapdto"></a>
<a id="tocsglobaltagmapdto"></a>

```json
{
  "globalTagName": "string",
  "record": "string",
  "label": "string",
  "tagName": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|globalTagName|string|false|none|none|
|record|string|false|none|none|
|label|string|false|none|none|
|tagName|string|false|none|none|

<h2 id="tocS_GlobalTagSetDto">GlobalTagSetDto</h2>
<!-- backwards compatibility -->
<a id="schemaglobaltagsetdto"></a>
<a id="schema_GlobalTagSetDto"></a>
<a id="tocSglobaltagsetdto"></a>
<a id="tocsglobaltagsetdto"></a>

```json
{
  "resources": [
    {
      "name": "string",
      "validity": 0,
      "description": "string",
      "release": "string",
      "insertionTime": "2019-08-24T14:15:22Z",
      "snapshotTime": "2019-08-24T14:15:22Z",
      "scenario": "string",
      "workflow": "string",
      "type": "string",
      "snapshotTimeMilli": 0,
      "insertionTimeMilli": 0
    }
  ],
  "size": 0,
  "datatype": "string",
  "format": "string",
  "page": {
    "size": 0,
    "totalElements": 0,
    "totalPages": 0,
    "number": 0
  },
  "filter": {
    "name": "string",
    "property1": "string",
    "property2": "string"
  }
}

```

An GlobalTagSet containing GlobalTagDto objects.

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|resources|[[GlobalTagDto](#schemaglobaltagdto)]|false|none|none|

<h2 id="tocS_IovDto">IovDto</h2>
<!-- backwards compatibility -->
<a id="schemaiovdto"></a>
<a id="schema_IovDto"></a>
<a id="tocSiovdto"></a>
<a id="tocsiovdto"></a>

```json
{
  "tagName": "string",
  "since": 0,
  "insertionTime": "2019-08-24T14:15:22Z",
  "payloadHash": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|tagName|string|false|none|none|
|since|integer(int64)|true|none|none|
|insertionTime|string(date-time)|false|none|none|
|payloadHash|string|true|none|none|

<h2 id="tocS_IovSetDto">IovSetDto</h2>
<!-- backwards compatibility -->
<a id="schemaiovsetdto"></a>
<a id="schema_IovSetDto"></a>
<a id="tocSiovsetdto"></a>
<a id="tocsiovsetdto"></a>

```json
{
  "resources": [
    {
      "tagName": "string",
      "since": 0,
      "insertionTime": "2019-08-24T14:15:22Z",
      "payloadHash": "string"
    }
  ],
  "size": 0,
  "datatype": "string",
  "format": "string",
  "page": {
    "size": 0,
    "totalElements": 0,
    "totalPages": 0,
    "number": 0
  },
  "filter": {
    "name": "string",
    "property1": "string",
    "property2": "string"
  }
}

```

An Set containing IovDto objects.

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|resources|[[IovDto](#schemaiovdto)]|false|none|none|

<h2 id="tocS_IovPayloadSetDto">IovPayloadSetDto</h2>
<!-- backwards compatibility -->
<a id="schemaiovpayloadsetdto"></a>
<a id="schema_IovPayloadSetDto"></a>
<a id="tocSiovpayloadsetdto"></a>
<a id="tocsiovpayloadsetdto"></a>

```json
{
  "resources": [
    {
      "since": 0,
      "insertionTime": "2019-08-24T14:15:22Z",
      "version": "string",
      "objectType": "string",
      "objectName": "string",
      "compressionType": "string",
      "size": 0,
      "payloadHash": "string"
    }
  ],
  "size": 0,
  "datatype": "string",
  "format": "string",
  "page": {
    "size": 0,
    "totalElements": 0,
    "totalPages": 0,
    "number": 0
  },
  "filter": {
    "name": "string",
    "property1": "string",
    "property2": "string"
  }
}

```

An Set containing IovPayloadDto objects.

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|resources|[[IovPayloadDto](#schemaiovpayloaddto)]|false|none|none|

<h2 id="tocS_IovPayloadDto">IovPayloadDto</h2>
<!-- backwards compatibility -->
<a id="schemaiovpayloaddto"></a>
<a id="schema_IovPayloadDto"></a>
<a id="tocSiovpayloaddto"></a>
<a id="tocsiovpayloaddto"></a>

```json
{
  "since": 0,
  "insertionTime": "2019-08-24T14:15:22Z",
  "version": "string",
  "objectType": "string",
  "objectName": "string",
  "compressionType": "string",
  "size": 0,
  "payloadHash": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|since|number|false|none|none|
|insertionTime|string(date-time)|false|none|none|
|version|string|false|none|none|
|objectType|string|false|none|none|
|objectName|string|false|none|none|
|compressionType|string|false|none|none|
|size|integer(int32)|false|none|none|
|payloadHash|string|false|none|none|

<h2 id="tocS_PayloadSetDto">PayloadSetDto</h2>
<!-- backwards compatibility -->
<a id="schemapayloadsetdto"></a>
<a id="schema_PayloadSetDto"></a>
<a id="tocSpayloadsetdto"></a>
<a id="tocspayloadsetdto"></a>

```json
{
  "resources": [
    {
      "hash": "string",
      "version": "string",
      "objectType": "string",
      "objectName": "string",
      "compressionType": "string",
      "checkSum": "string",
      "size": 0,
      "insertionTime": "2019-08-24T14:15:22Z"
    }
  ],
  "size": 0,
  "datatype": "string",
  "format": "string",
  "page": {
    "size": 0,
    "totalElements": 0,
    "totalPages": 0,
    "number": 0
  },
  "filter": {
    "name": "string",
    "property1": "string",
    "property2": "string"
  }
}

```

An Set containing PayloadDto objects.

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|resources|[[PayloadDto](#schemapayloaddto)]|false|none|none|

<h2 id="tocS_PayloadDto">PayloadDto</h2>
<!-- backwards compatibility -->
<a id="schemapayloaddto"></a>
<a id="schema_PayloadDto"></a>
<a id="tocSpayloaddto"></a>
<a id="tocspayloaddto"></a>

```json
{
  "hash": "string",
  "version": "string",
  "objectType": "string",
  "objectName": "string",
  "compressionType": "string",
  "checkSum": "string",
  "size": 0,
  "insertionTime": "2019-08-24T14:15:22Z"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|hash|string|false|none|none|
|version|string|false|none|none|
|objectType|string|false|none|none|
|objectName|string|false|none|none|
|compressionType|string|false|none|none|
|checkSum|string|false|none|none|
|size|integer(int32)|false|none|none|
|insertionTime|string(date-time)|false|none|none|

<h2 id="tocS_StoreDto">StoreDto</h2>
<!-- backwards compatibility -->
<a id="schemastoredto"></a>
<a id="schema_StoreDto"></a>
<a id="tocSstoredto"></a>
<a id="tocsstoredto"></a>

```json
{
  "hash": "string",
  "since": 0,
  "data": "string",
  "streamerInfo": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|hash|string|false|none|none|
|since|integer(int64)|false|none|none|
|data|string|false|none|none|
|streamerInfo|string|false|none|none|

<h2 id="tocS_StoreSetDto">StoreSetDto</h2>
<!-- backwards compatibility -->
<a id="schemastoresetdto"></a>
<a id="schema_StoreSetDto"></a>
<a id="tocSstoresetdto"></a>
<a id="tocsstoresetdto"></a>

```json
{
  "resources": [
    {
      "hash": "string",
      "since": 0,
      "data": "string",
      "streamerInfo": "string"
    }
  ],
  "size": 0,
  "datatype": "string",
  "format": "string",
  "page": {
    "size": 0,
    "totalElements": 0,
    "totalPages": 0,
    "number": 0
  },
  "filter": {
    "name": "string",
    "property1": "string",
    "property2": "string"
  }
}

```

An Set containing StoreDto objects.

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|resources|[[StoreDto](#schemastoredto)]|false|none|none|

<h2 id="tocS_PayloadTagInfoSetDto">PayloadTagInfoSetDto</h2>
<!-- backwards compatibility -->
<a id="schemapayloadtaginfosetdto"></a>
<a id="schema_PayloadTagInfoSetDto"></a>
<a id="tocSpayloadtaginfosetdto"></a>
<a id="tocspayloadtaginfosetdto"></a>

```json
{
  "resources": [
    {
      "tagname": "string",
      "niovs": 0,
      "totvolume": 0.1,
      "avgvolume": 0.1
    }
  ],
  "size": 0,
  "datatype": "string",
  "format": "string",
  "page": {
    "size": 0,
    "totalElements": 0,
    "totalPages": 0,
    "number": 0
  },
  "filter": {
    "name": "string",
    "property1": "string",
    "property2": "string"
  }
}

```

An PayloadTagInfoSet containing PayloadTagInfoDto objects.

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|resources|[[PayloadTagInfoDto](#schemapayloadtaginfodto)]|false|none|none|

<h2 id="tocS_PayloadTagInfoDto">PayloadTagInfoDto</h2>
<!-- backwards compatibility -->
<a id="schemapayloadtaginfodto"></a>
<a id="schema_PayloadTagInfoDto"></a>
<a id="tocSpayloadtaginfodto"></a>
<a id="tocspayloadtaginfodto"></a>

```json
{
  "tagname": "string",
  "niovs": 0,
  "totvolume": 0.1,
  "avgvolume": 0.1
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|tagname|string|false|none|none|
|niovs|integer|false|none|none|
|totvolume|number(float)|false|none|none|
|avgvolume|number(float)|false|none|none|

<h2 id="tocS_TagSummarySetDto">TagSummarySetDto</h2>
<!-- backwards compatibility -->
<a id="schematagsummarysetdto"></a>
<a id="schema_TagSummarySetDto"></a>
<a id="tocStagsummarysetdto"></a>
<a id="tocstagsummarysetdto"></a>

```json
{
  "resources": [
    {
      "tagname": "string",
      "niovs": 0
    }
  ],
  "size": 0,
  "datatype": "string",
  "format": "string",
  "page": {
    "size": 0,
    "totalElements": 0,
    "totalPages": 0,
    "number": 0
  },
  "filter": {
    "name": "string",
    "property1": "string",
    "property2": "string"
  }
}

```

An Set containing TagSummaryDto objects.

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|resources|[[TagSummaryDto](#schematagsummarydto)]|false|none|none|

<h2 id="tocS_TagSummaryDto">TagSummaryDto</h2>
<!-- backwards compatibility -->
<a id="schematagsummarydto"></a>
<a id="schema_TagSummaryDto"></a>
<a id="tocStagsummarydto"></a>
<a id="tocstagsummarydto"></a>

```json
{
  "tagname": "string",
  "niovs": 0
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|tagname|string|false|none|none|
|niovs|integer(int64)|false|none|none|

<h2 id="tocS_RunLumiSetDto">RunLumiSetDto</h2>
<!-- backwards compatibility -->
<a id="schemarunlumisetdto"></a>
<a id="schema_RunLumiSetDto"></a>
<a id="tocSrunlumisetdto"></a>
<a id="tocsrunlumisetdto"></a>

```json
{
  "resources": [
    {
      "runNumber": 0,
      "lb": 0,
      "starttime": 0,
      "endtime": 0
    }
  ],
  "size": 0,
  "datatype": "string",
  "format": "string",
  "page": {
    "size": 0,
    "totalElements": 0,
    "totalPages": 0,
    "number": 0
  },
  "filter": {
    "name": "string",
    "property1": "string",
    "property2": "string"
  }
}

```

An RunLumiSet containing RunLumiInfoDto objects.

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|resources|[[RunLumiInfoDto](#schemarunlumiinfodto)]|false|none|none|

<h2 id="tocS_RunLumiInfoDto">RunLumiInfoDto</h2>
<!-- backwards compatibility -->
<a id="schemarunlumiinfodto"></a>
<a id="schema_RunLumiInfoDto"></a>
<a id="tocSrunlumiinfodto"></a>
<a id="tocsrunlumiinfodto"></a>

```json
{
  "runNumber": 0,
  "lb": 0,
  "starttime": 0,
  "endtime": 0
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|runNumber|number|false|none|none|
|lb|number|false|none|none|
|starttime|number|false|none|none|
|endtime|number|false|none|none|

