# GlobaltagmapsApi

All URIs are relative to *http://crest-j23.cern.ch/api-v5.0*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createGlobalTagMap**](GlobaltagmapsApi.md#createGlobalTagMap) | **POST** /globaltagmaps | Create a GlobalTagMap in the database. |
| [**deleteGlobalTagMap**](GlobaltagmapsApi.md#deleteGlobalTagMap) | **DELETE** /globaltagmaps/{name} | Delete GlobalTagMapDto lists. |
| [**findGlobalTagMap**](GlobaltagmapsApi.md#findGlobalTagMap) | **GET** /globaltagmaps/{name} | Find GlobalTagMapDto lists. |


<a name="createGlobalTagMap"></a>
# **createGlobalTagMap**
> GlobalTagMapDto createGlobalTagMap(GlobalTagMapDto)

Create a GlobalTagMap in the database.

    This method allows to insert a GlobalTagMap.Arguments: GlobalTagMapDto should be provided in the body as a JSON file.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **GlobalTagMapDto** | [**GlobalTagMapDto**](../Models/GlobalTagMapDto.md)|  | [optional] |

### Return type

[**GlobalTagMapDto**](../Models/GlobalTagMapDto.md)

### Authorization

[OpenID](../README.md#OpenID), [BearerAuth](../README.md#BearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

<a name="deleteGlobalTagMap"></a>
# **deleteGlobalTagMap**
> GlobalTagMapSetDto deleteGlobalTagMap(name, label, tagname, record)

Delete GlobalTagMapDto lists.

    This method search for mappings using the global tag name and deletes all mappings.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **name** | **String**| the global tag name | [default to null] |
| **label** | **String**| label: the generic name labelling all tags of a certain kind. | [default to none] |
| **tagname** | **String**| tagname: the name of the tag associated. | [default to none] |
| **record** | **String**| record: the record. | [optional] [default to null] |

### Return type

[**GlobalTagMapSetDto**](../Models/GlobalTagMapSetDto.md)

### Authorization

[OpenID](../README.md#OpenID), [BearerAuth](../README.md#BearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="findGlobalTagMap"></a>
# **findGlobalTagMap**
> GlobalTagMapSetDto findGlobalTagMap(name, X-Crest-MapMode)

Find GlobalTagMapDto lists.

    This method search for mappings using the global tag name.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **name** | **String**|  | [default to null] |
| **X-Crest-MapMode** | **String**| If the mode is BackTrace then it will search for global tags containing the tag &lt;name&gt; | [optional] [default to Trace] |

### Return type

[**GlobalTagMapSetDto**](../Models/GlobalTagMapSetDto.md)

### Authorization

[OpenID](../README.md#OpenID), [BearerAuth](../README.md#BearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

