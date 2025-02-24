# IovsApi

All URIs are relative to *http://crest-j23.cern.ch/api-v5.0*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**findAllIovs**](IovsApi.md#findAllIovs) | **GET** /iovs | Finds a IovDtos lists. |
| [**getSizeByTag**](IovsApi.md#getSizeByTag) | **GET** /iovs/size | Get the number o iovs for tags matching pattern. |
| [**selectIovPayloads**](IovsApi.md#selectIovPayloads) | **GET** /iovs/infos | Select iovs and payload meta info for a given tagname and in a given range. |
| [**storeIovBatch**](IovsApi.md#storeIovBatch) | **POST** /iovs | Create IOVs in the database, associated to a tag name. |
| [**storeIovOne**](IovsApi.md#storeIovOne) | **PUT** /iovs | Create a single IOV in the database, associated to a tag name. |


<a name="findAllIovs"></a>
# **findAllIovs**
> IovSetDto findAllIovs(method, tagname, snapshot, since, until, timeformat, groupsize, hash, page, size, sort, X-Crest-Query, X-Crest-Since)

Finds a IovDtos lists.

    Retrieves IOVs, with parameterizable method and arguments 

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **method** | **String**| the method used will determine which query is executed IOVS, RANGE and AT is a standard IOV query requiring a precise tag name GROUPS is a group query type  | [default to IOVS] [enum: IOVS, GROUPS, MONITOR] |
| **tagname** | **String**| the tag name | [optional] [default to none] |
| **snapshot** | **Long**| snapshot: the snapshot time {0} | [optional] [default to 0] |
| **since** | **String**| the since time as a string {0} | [optional] [default to 0] |
| **until** | **String**| the until time as a string {INF} | [optional] [default to INF] |
| **timeformat** | **String**| the format for since and until {number | ms | iso | run-lumi | custom (yyyyMMdd&#39;T&#39;HHmmssX)} If timeformat is equal number, we just parse the argument as a long.  | [optional] [default to NUMBER] [enum: NUMBER, MS, ISO, RUN, RUN_LUMI, CUSTOM] |
| **groupsize** | **Long**| The group size represent the pagination type provided for GROUPS query method.  | [optional] [default to null] |
| **hash** | **String**| the hash for searching specific IOV list for a given hash.  | [optional] [default to null] |
| **page** | **Integer**| the page number {0} | [optional] [default to 0] |
| **size** | **Integer**| the page size {10000} | [optional] [default to 10000] |
| **sort** | **String**| the sort pattern {id.since:ASC} | [optional] [default to id.since:ASC] |
| **X-Crest-Query** | **String**| The query type. The header parameter X-Crest-Query can be : iovs, ranges, at. The iovs represents an exclusive interval, while ranges and at include previous since. This has an impact on how the since and until ranges are applied.  | [optional] [default to IOVS] [enum: IOVS, RANGES, AT] |
| **X-Crest-Since** | **String**| The since type required in the query. It can be : ms, cool. Since and until will be transformed in these units. It differs from timeformat which indicates how to interpret the since and until strings in input.  | [optional] [default to NUMBER] [enum: MS, COOL, NUMBER] |

### Return type

[**IovSetDto**](../Models/IovSetDto.md)

### Authorization

[OpenID](../README.md#OpenID), [BearerAuth](../README.md#BearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="getSizeByTag"></a>
# **getSizeByTag**
> TagSummarySetDto getSizeByTag(tagname)

Get the number o iovs for tags matching pattern.

    This method allows to retrieve the number of iovs in a tag (or pattern). 

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **tagname** | **String**| the tag name, can be a pattern like MDT% | [default to none] |

### Return type

[**TagSummarySetDto**](../Models/TagSummarySetDto.md)

### Authorization

[OpenID](../README.md#OpenID), [BearerAuth](../README.md#BearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="selectIovPayloads"></a>
# **selectIovPayloads**
> IovPayloadSetDto selectIovPayloads(tagname, since, until, timeformat, page, size, sort)

Select iovs and payload meta info for a given tagname and in a given range.

    Retrieve a list of iovs with payload metadata associated. The arguments are: tagname&#x3D;{a tag name}, since&#x3D;{since time as string}, until&#x3D;{until time as string}, snapshot&#x3D;{snapshot time as long}&#39; and timeformat&#x3D;{format of since/until}. 

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **tagname** | **String**| the tag name | [default to none] |
| **since** | **String**| the since time as a string {0} | [optional] [default to 0] |
| **until** | **String**| the until time as a string {INF} | [optional] [default to INF] |
| **timeformat** | **String**| the format for since and until {number | ms | iso | custom (yyyyMMdd&#39;T&#39;HHmmssX)} If timeformat is equal number, we just parse the argument as a long.  | [optional] [default to number] |
| **page** | **Integer**| the page number {0} | [optional] [default to 0] |
| **size** | **Integer**| the page size {10000} | [optional] [default to 10000] |
| **sort** | **String**| the sort pattern {id.since:ASC} | [optional] [default to id.since:ASC] |

### Return type

[**IovPayloadSetDto**](../Models/IovPayloadSetDto.md)

### Authorization

[OpenID](../README.md#OpenID), [BearerAuth](../README.md#BearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="storeIovBatch"></a>
# **storeIovBatch**
> IovSetDto storeIovBatch(IovSetDto)

Create IOVs in the database, associated to a tag name.

    Insert a list of Iovs using an IovSetDto in the request body. It is mandatory to provide an existing tag in input. The referenced payloads should already exists in the DB. 

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **IovSetDto** | [**IovSetDto**](../Models/IovSetDto.md)|  | [optional] |

### Return type

[**IovSetDto**](../Models/IovSetDto.md)

### Authorization

[OpenID](../README.md#OpenID), [BearerAuth](../README.md#BearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

<a name="storeIovOne"></a>
# **storeIovOne**
> IovSetDto storeIovOne(IovDto)

Create a single IOV in the database, associated to a tag name.

    Insert an Iov using an IovDto in the request body. It is mandatory to provide an existing tag in input. The referenced payloads should already exists in the DB. 

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **IovDto** | [**IovDto**](../Models/IovDto.md)|  | [optional] |

### Return type

[**IovSetDto**](../Models/IovSetDto.md)

### Authorization

[OpenID](../README.md#OpenID), [BearerAuth](../README.md#BearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

