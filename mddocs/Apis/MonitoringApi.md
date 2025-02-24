# MonitoringApi

All URIs are relative to *http://crest-j23.cern.ch/api-v5.0*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**listPayloadTagInfo**](MonitoringApi.md#listPayloadTagInfo) | **GET** /monitoring/payloads | Retrieves monitoring information on payload as a list of PayloadTagInfoDtos. |


<a name="listPayloadTagInfo"></a>
# **listPayloadTagInfo**
> PayloadTagInfoSetDto listPayloadTagInfo(tagname)

Retrieves monitoring information on payload as a list of PayloadTagInfoDtos.

    This method allows to perform search and sorting.Arguments: tagname&#x3D;&lt;pattern&gt;, page&#x3D;{ipage}, size&#x3D;{isize}, sort&#x3D;&lt;sortpattern&gt;. The pattern &lt;pattern&gt; is in the form &lt;param-name&gt;&lt;operation&gt;&lt;param-value&gt;       &lt;param-name&gt; is the name of one of the fields in the dto       &lt;operation&gt; can be [&lt; : &gt;] ; for string use only [:]        &lt;param-value&gt; depends on the chosen parameter. A list of this criteria can be provided       using comma separated strings for &lt;pattern&gt;.      The pattern &lt;sortpattern&gt; is &lt;field&gt;:[DESC|ASC]

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **tagname** | **String**| tagname: the search pattern {none} | [optional] [default to none] |

### Return type

[**PayloadTagInfoSetDto**](../Models/PayloadTagInfoSetDto.md)

### Authorization

[OpenID](../README.md#OpenID), [BearerAuth](../README.md#BearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

