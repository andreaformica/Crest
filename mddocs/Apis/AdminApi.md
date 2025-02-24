# AdminApi

All URIs are relative to *http://crest-j23.cern.ch/api-v5.0*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**removeGlobalTag**](AdminApi.md#removeGlobalTag) | **DELETE** /admin/globaltags/{name} | Remove a GlobalTag from the database. |
| [**removeTag**](AdminApi.md#removeTag) | **DELETE** /admin/tags/{name} | Remove a Tag from the database. |
| [**updateGlobalTag**](AdminApi.md#updateGlobalTag) | **PUT** /admin/globaltags/{name} | Update a GlobalTag in the database. |


<a name="removeGlobalTag"></a>
# **removeGlobalTag**
> removeGlobalTag(name)

Remove a GlobalTag from the database.

    This method allows to remove a GlobalTag.Arguments: the name has to uniquely identify a global tag.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **name** | **String**|  | [default to null] |

### Return type

null (empty response body)

### Authorization

[OpenID](../README.md#OpenID), [BearerAuth](../README.md#BearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="removeTag"></a>
# **removeTag**
> removeTag(name)

Remove a Tag from the database.

    This method allows to remove a Tag.Arguments: the name has to uniquely identify a tag.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **name** | **String**|  | [default to null] |

### Return type

null (empty response body)

### Authorization

[OpenID](../README.md#OpenID), [BearerAuth](../README.md#BearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="updateGlobalTag"></a>
# **updateGlobalTag**
> GlobalTagDto updateGlobalTag(name, GlobalTagDto)

Update a GlobalTag in the database.

    This method allows to update a GlobalTag.Arguments: the name has to uniquely identify a global tag.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **name** | **String**|  | [default to null] |
| **GlobalTagDto** | [**GlobalTagDto**](../Models/GlobalTagDto.md)|  | [optional] |

### Return type

[**GlobalTagDto**](../Models/GlobalTagDto.md)

### Authorization

[OpenID](../README.md#OpenID), [BearerAuth](../README.md#BearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

