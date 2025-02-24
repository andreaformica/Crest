# GlobaltagsApi

All URIs are relative to *http://crest-j23.cern.ch/api-v5.0*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createGlobalTag**](GlobaltagsApi.md#createGlobalTag) | **POST** /globaltags | Create a GlobalTag in the database. |
| [**findGlobalTag**](GlobaltagsApi.md#findGlobalTag) | **GET** /globaltags/{name} | Finds a GlobalTagDto by name |
| [**findGlobalTagFetchTags**](GlobaltagsApi.md#findGlobalTagFetchTags) | **GET** /globaltags/{name}/tags | Finds a TagDtos lists associated to the global tag name in input. |
| [**listGlobalTags**](GlobaltagsApi.md#listGlobalTags) | **GET** /globaltags | Finds a GlobalTagDtos lists. |


<a name="createGlobalTag"></a>
# **createGlobalTag**
> GlobalTagDto createGlobalTag(force, GlobalTagDto)

Create a GlobalTag in the database.

    This method allows to insert a GlobalTag.Arguments: GlobalTagDto should be provided in the body as a JSON file.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **force** | **String**| force: tell the server if it should use or not the insertion time provided {default: false} | [optional] [default to false] |
| **GlobalTagDto** | [**GlobalTagDto**](../Models/GlobalTagDto.md)|  | [optional] |

### Return type

[**GlobalTagDto**](../Models/GlobalTagDto.md)

### Authorization

[OpenID](../README.md#OpenID), [BearerAuth](../README.md#BearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

<a name="findGlobalTag"></a>
# **findGlobalTag**
> GlobalTagSetDto findGlobalTag(name)

Finds a GlobalTagDto by name

    This method will search for a global tag with the given name. Only one global tag should be returned.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **name** | **String**|  | [default to null] |

### Return type

[**GlobalTagSetDto**](../Models/GlobalTagSetDto.md)

### Authorization

[OpenID](../README.md#OpenID), [BearerAuth](../README.md#BearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="findGlobalTagFetchTags"></a>
# **findGlobalTagFetchTags**
> TagSetDto findGlobalTagFetchTags(name, record, label)

Finds a TagDtos lists associated to the global tag name in input.

    This method allows to trace a global tag.Arguments: record&#x3D;&lt;record&gt; filter output by record, label&#x3D;&lt;label&gt; filter output by label

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **name** | **String**|  | [default to null] |
| **record** | **String**| record:  the record string {} | [optional] [default to none] |
| **label** | **String**| label:  the label string {} | [optional] [default to none] |

### Return type

[**TagSetDto**](../Models/TagSetDto.md)

### Authorization

[OpenID](../README.md#OpenID), [BearerAuth](../README.md#BearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="listGlobalTags"></a>
# **listGlobalTags**
> GlobalTagSetDto listGlobalTags(name, workflow, scenario, release, validity, description, page, size, sort)

Finds a GlobalTagDtos lists.

    This method allows to perform search and sorting. Arguments: name&#x3D;&lt;pattern&gt;, workflow, scenario, release, validity, description page&#x3D;{ipage}, size&#x3D;{isize}, sort&#x3D;&lt;sortpattern&gt;. 

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **name** | **String**| the global tag name search pattern {none} | [optional] [default to all] |
| **workflow** | **String**| the global tag workflow search pattern {none} | [optional] [default to null] |
| **scenario** | **String**| the global tag scenario search pattern {none} | [optional] [default to null] |
| **release** | **String**| the global tag release search pattern {none} | [optional] [default to null] |
| **validity** | **Long**| the global tag validity low limit {x&gt;&#x3D;validity} | [optional] [default to null] |
| **description** | **String**| the global tag description search pattern {none} | [optional] [default to null] |
| **page** | **Integer**| page: the page number {0} | [optional] [default to 0] |
| **size** | **Integer**| size: the page size {1000} | [optional] [default to 1000] |
| **sort** | **String**| sort: the sort pattern {name:ASC} | [optional] [default to name:ASC] |

### Return type

[**GlobalTagSetDto**](../Models/GlobalTagSetDto.md)

### Authorization

[OpenID](../README.md#OpenID), [BearerAuth](../README.md#BearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

