# PayloadsApi

All URIs are relative to *http://crest-j23.cern.ch/api-v5.0*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**getPayload**](PayloadsApi.md#getPayload) | **GET** /payloads/data | Finds a payload resource associated to the hash. |
| [**listPayloads**](PayloadsApi.md#listPayloads) | **GET** /payloads | Finds Payloads metadata. |
| [**storePayloadBatch**](PayloadsApi.md#storePayloadBatch) | **POST** /payloads | Create Payloads in the database, associated to a given iov since list and tag name. |
| [**updatePayload**](PayloadsApi.md#updatePayload) | **PUT** /payloads/data | Update a streamerInfo in a payload |
| [**uploadJson**](PayloadsApi.md#uploadJson) | **PUT** /payloads | Upload and process large JSON data. |


<a name="getPayload"></a>
# **getPayload**
> String getPayload(hash, format)

Finds a payload resource associated to the hash.

    This method retrieves a payload resource. Arguments: hash&#x3D;&lt;hash&gt; the hash of the payload Depending on the header, this method will either retrieve the data, the metadata of the payload  or the streamerInfo alone. 

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **hash** | **String**| hash:  the hash of the payload | [default to null] |
| **format** | **String**| The format of the output data.  It can be : BLOB (default), META (meta data) or STREAMER (streamerInfo).  | [default to BLOB] [enum: BLOB, META, STREAMER] |

### Return type

**String**

### Authorization

[OpenID](../README.md#OpenID), [BearerAuth](../README.md#BearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/octet-stream, application/json

<a name="listPayloads"></a>
# **listPayloads**
> PayloadSetDto listPayloads(hash, objectType, minsize, page, size, sort)

Finds Payloads metadata.

    This method allows to perform search and sorting. Arguments: hash&#x3D;&lt;the payload hash&gt;, minsize&#x3D;&lt;min size&gt;, objectType&#x3D;&lt;the type&gt; page&#x3D;{ipage}, size&#x3D;{isize}, sort&#x3D;&lt;sortpattern&gt;. 

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **hash** | **String**| the hash to search {none} | [optional] [default to null] |
| **objectType** | **String**| the objectType to search | [optional] [default to null] |
| **minsize** | **Integer**| the minimum size to search | [optional] [default to null] |
| **page** | **Integer**| page: the page number {0} | [optional] [default to 0] |
| **size** | **Integer**| size: the page size {1000} | [optional] [default to 1000] |
| **sort** | **String**| sort: the sort pattern {insertionTime:DESC} | [optional] [default to insertionTime:DESC] |

### Return type

[**PayloadSetDto**](../Models/PayloadSetDto.md)

### Authorization

[OpenID](../README.md#OpenID), [BearerAuth](../README.md#BearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="storePayloadBatch"></a>
# **storePayloadBatch**
> StoreSetDto storePayloadBatch(tag, storeset, X-Crest-PayloadFormat, files, objectType, compressionType, version, endtime)

Create Payloads in the database, associated to a given iov since list and tag name.

    This method allows to insert list of Payloads and IOVs. Payload can be contained in the HASH of the IOV (in case it is a small JSON) or as a reference to external file (FILE). In the first case, the files list can be null. Arguments: tag,version,endtime,objectType,compressionType The header parameter X-Crest-PayloadFormat can be FILE or JSON 

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **tag** | **String**| The tag name | [default to null] |
| **storeset** | **String**| the string representing a StoreSetDto in json | [default to null] |
| **X-Crest-PayloadFormat** | **String**| The format of the input data. StoreSetDto entries will have either the content inline (JSON) or stored via external files (FILE).  | [optional] [default to FILE] [enum: FILE, JSON] |
| **files** | **List**| The payload files as an array of streams | [optional] [default to null] |
| **objectType** | **String**| The object type | [optional] [default to null] |
| **compressionType** | **String**| The compression type | [optional] [default to null] |
| **version** | **String**| The version | [optional] [default to null] |
| **endtime** | **String**| The tag end time. This represents a number. | [optional] [default to null] |

### Return type

[**StoreSetDto**](../Models/StoreSetDto.md)

### Authorization

[OpenID](../README.md#OpenID), [BearerAuth](../README.md#BearerAuth)

### HTTP request headers

- **Content-Type**: multipart/form-data
- **Accept**: application/json, application/xml

<a name="updatePayload"></a>
# **updatePayload**
> PayloadDto updatePayload(hash, GenericMap)

Update a streamerInfo in a payload

    This method will update the streamerInfo. This is provided via a generic map in the request body containing the key &#39;streamerInfo&#39; 

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **hash** | **String**| hash:  the hash of the payload | [default to null] |
| **GenericMap** | [**GenericMap**](../Models/GenericMap.md)|  | [optional] |

### Return type

[**PayloadDto**](../Models/PayloadDto.md)

### Authorization

[OpenID](../README.md#OpenID), [BearerAuth](../README.md#BearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json, application/xml

<a name="uploadJson"></a>
# **uploadJson**
> StoreSetDto uploadJson(tag, storeset, objectType, compressionType, version, endtime)

Upload and process large JSON data.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **tag** | **String**| The tag name | [default to null] |
| **storeset** | **File**| the string representing a StoreSetDto in json | [default to null] |
| **objectType** | **String**| The object type | [optional] [default to null] |
| **compressionType** | **String**| The compression type | [optional] [default to null] |
| **version** | **String**| The version | [optional] [default to null] |
| **endtime** | **String**| The tag end time, represent a number. | [optional] [default to null] |

### Return type

[**StoreSetDto**](../Models/StoreSetDto.md)

### Authorization

[OpenID](../README.md#OpenID), [BearerAuth](../README.md#BearerAuth)

### HTTP request headers

- **Content-Type**: multipart/form-data
- **Accept**: application/json, application/xml

