# FoldersApi

All URIs are relative to *http://crest-j23.cern.ch/api-v5.0*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createFolder**](FoldersApi.md#createFolder) | **POST** /folders | Create an entry for folder information. |
| [**listFolders**](FoldersApi.md#listFolders) | **GET** /folders | Finds a FolderDto list. |


<a name="createFolder"></a>
# **createFolder**
> FolderDto createFolder(FolderDto)

Create an entry for folder information.

    Folder informations go into a dedicated table.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **FolderDto** | [**FolderDto**](../Models/FolderDto.md)|  | [optional] |

### Return type

[**FolderDto**](../Models/FolderDto.md)

### Authorization

[OpenID](../README.md#OpenID), [BearerAuth](../README.md#BearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

<a name="listFolders"></a>
# **listFolders**
> FolderSetDto listFolders(schema)

Finds a FolderDto list.

    This method allows to perform search and sorting.Arguments: by&#x3D;&lt;pattern&gt;, sort&#x3D;&lt;sortpattern&gt;. The pattern &lt;pattern&gt; is in the form &lt;param-name&gt;&lt;operation&gt;&lt;param-value&gt;       &lt;param-name&gt; is the name of one of the fields in the dto       &lt;operation&gt; can be [&lt; : &gt;] ; for string use only [:]        &lt;param-value&gt; depends on the chosen parameter. A list of this criteria can be provided       using comma separated strings for &lt;pattern&gt;.      The pattern &lt;sortpattern&gt; is &lt;field&gt;:[DESC|ASC]

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **schema** | **String**| the schema pattern {none} | [optional] [default to none] |

### Return type

[**FolderSetDto**](../Models/FolderSetDto.md)

### Authorization

[OpenID](../README.md#OpenID), [BearerAuth](../README.md#BearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

