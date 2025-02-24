# TagsApi

All URIs are relative to *http://crest-j23.cern.ch/api-v5.0*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createTag**](TagsApi.md#createTag) | **POST** /tags | Create a Tag in the database. |
| [**createTagMeta**](TagsApi.md#createTagMeta) | **POST** /tags/{name}/meta | Create a TagMeta in the database. |
| [**findTag**](TagsApi.md#findTag) | **GET** /tags/{name} | Finds a TagDto by name |
| [**findTagMeta**](TagsApi.md#findTagMeta) | **GET** /tags/{name}/meta | Finds a TagMetaDto by name |
| [**listTags**](TagsApi.md#listTags) | **GET** /tags | Finds a TagDtos lists. |
| [**updateTag**](TagsApi.md#updateTag) | **PUT** /tags/{name} | Update a TagDto by name |
| [**updateTagMeta**](TagsApi.md#updateTagMeta) | **PUT** /tags/{name}/meta | Update a TagMetaDto by name |


<a name="createTag"></a>
# **createTag**
> TagDto createTag(TagDto)

Create a Tag in the database.

    This method allows to insert a Tag.Arguments: TagDto should be provided in the body as a JSON file.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **TagDto** | [**TagDto**](../Models/TagDto.md)|  | [optional] |

### Return type

[**TagDto**](../Models/TagDto.md)

### Authorization

[OpenID](../README.md#OpenID), [BearerAuth](../README.md#BearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

<a name="createTagMeta"></a>
# **createTagMeta**
> TagMetaDto createTagMeta(name, TagMetaDto)

Create a TagMeta in the database.

    This method allows to insert a TagMeta.Arguments: TagMetaDto should be provided in the body as a JSON file.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **name** | **String**| name: the tag name | [default to null] |
| **TagMetaDto** | [**TagMetaDto**](../Models/TagMetaDto.md)|  | [optional] |

### Return type

[**TagMetaDto**](../Models/TagMetaDto.md)

### Authorization

[OpenID](../README.md#OpenID), [BearerAuth](../README.md#BearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

<a name="findTag"></a>
# **findTag**
> TagSetDto findTag(name)

Finds a TagDto by name

    This method will search for a tag with the given name. Only one tag should be returned.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **name** | **String**| name: the tag name | [default to null] |

### Return type

[**TagSetDto**](../Models/TagSetDto.md)

### Authorization

[OpenID](../README.md#OpenID), [BearerAuth](../README.md#BearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="findTagMeta"></a>
# **findTagMeta**
> TagMetaSetDto findTagMeta(name)

Finds a TagMetaDto by name

    This method will search for a tag metadata with the given name. Only one tag should be returned.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **name** | **String**| name: the tag name | [default to null] |

### Return type

[**TagMetaSetDto**](../Models/TagMetaSetDto.md)

### Authorization

[OpenID](../README.md#OpenID), [BearerAuth](../README.md#BearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="listTags"></a>
# **listTags**
> TagSetDto listTags(name, timeType, objectType, description, page, size, sort)

Finds a TagDtos lists.

    This method allows to perform search and sorting. Arguments: name&#x3D;&lt;pattern&gt;, objectType, timeType, description page&#x3D;{ipage}, size&#x3D;{isize}, sort&#x3D;&lt;sortpattern&gt;. 

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **name** | **String**| the tag name search pattern {all} | [optional] [default to all] |
| **timeType** | **String**| the tag timeType {none} | [optional] [default to null] |
| **objectType** | **String**| the tag objectType search pattern {none} | [optional] [default to null] |
| **description** | **String**| the global tag description search pattern {none} | [optional] [default to null] |
| **page** | **Integer**| page: the page number {0} | [optional] [default to 0] |
| **size** | **Integer**| size: the page size {1000} | [optional] [default to 1000] |
| **sort** | **String**| sort: the sort pattern {name:ASC} | [optional] [default to name:ASC] |

### Return type

[**TagSetDto**](../Models/TagSetDto.md)

### Authorization

[OpenID](../README.md#OpenID), [BearerAuth](../README.md#BearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="updateTag"></a>
# **updateTag**
> TagDto updateTag(name, GenericMap)

Update a TagDto by name

    This method will search for a tag with the given name, and update its content for the provided body fields. Only the following fields can be updated: description, timeType, objectTime, endOfValidity, lastValidatedTime.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **name** | **String**| name: the tag name | [default to null] |
| **GenericMap** | [**GenericMap**](../Models/GenericMap.md)|  | [optional] |

### Return type

[**TagDto**](../Models/TagDto.md)

### Authorization

[OpenID](../README.md#OpenID), [BearerAuth](../README.md#BearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

<a name="updateTagMeta"></a>
# **updateTagMeta**
> TagMetaDto updateTagMeta(name, GenericMap)

Update a TagMetaDto by name

    This method will search for a tag with the given name, and update its content for the provided body fields. Only the following fields can be updated: description, timeType, objectTime, endOfValidity, lastValidatedTime.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **name** | **String**| name: the tag name | [default to null] |
| **GenericMap** | [**GenericMap**](../Models/GenericMap.md)|  | [optional] |

### Return type

[**TagMetaDto**](../Models/TagMetaDto.md)

### Authorization

[OpenID](../README.md#OpenID), [BearerAuth](../README.md#BearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json, application/xml

