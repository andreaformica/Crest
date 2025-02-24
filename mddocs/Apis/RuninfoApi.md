# RuninfoApi

All URIs are relative to *http://crest-j23.cern.ch/api-v5.0*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createRunInfo**](RuninfoApi.md#createRunInfo) | **POST** /runinfo | Create an entry for run information. |
| [**listRunInfo**](RuninfoApi.md#listRunInfo) | **GET** /runinfo | Finds a RunLumiInfoDto lists using parameters. |
| [**updateRunInfo**](RuninfoApi.md#updateRunInfo) | **PUT** /runinfo | Update an entry for run information. |


<a name="createRunInfo"></a>
# **createRunInfo**
> RunLumiSetDto createRunInfo(RunLumiSetDto)

Create an entry for run information.

    Run informations go into a separate table.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **RunLumiSetDto** | [**RunLumiSetDto**](../Models/RunLumiSetDto.md)|  | [optional] |

### Return type

[**RunLumiSetDto**](../Models/RunLumiSetDto.md)

### Authorization

[OpenID](../README.md#OpenID), [BearerAuth](../README.md#BearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

<a name="listRunInfo"></a>
# **listRunInfo**
> RunLumiSetDto listRunInfo(since, until, format, mode, page, size, sort)

Finds a RunLumiInfoDto lists using parameters.

    This method allows to perform search.Arguments: from&#x3D;&lt;someformat&gt;,to&#x3D;&lt;someformat&gt;, format&#x3D;&lt;describe previous types&gt;, page&#x3D;{ipage}, size&#x3D;{isize}, sort&#x3D;&lt;sortpattern&gt;. The pattern &lt;pattern&gt; is in the form &lt;param-name&gt;&lt;operation&gt;&lt;param-value&gt;       &lt;param-name&gt; is the name of one of the fields in the dto       &lt;operation&gt; can be [&lt; : &gt;] ; for string use only [:]        &lt;param-value&gt; depends on the chosen parameter. A list of this criteria can be provided       using comma separated strings for &lt;pattern&gt;.      The pattern &lt;sortpattern&gt; is &lt;field&gt;:[DESC|ASC]

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **since** | **String**| since: the starting time or run-lumi | [optional] [default to none] |
| **until** | **String**| until: the ending time or run-lumi | [optional] [default to none] |
| **format** | **String**| the format to digest previous arguments [iso], [number], [run-lumi]. Time(iso) &#x3D; yyyymmddhhmiss,  Time(number) &#x3D; milliseconds or Run(number) &#x3D; runnumber Run(run-lumi) &#x3D; runnumber-lumisection  | [optional] [default to number] |
| **mode** | **String**| the mode for the request : [daterange] or [runrange]  | [optional] [default to runrange] [enum: daterange, runrange] |
| **page** | **Integer**| page: the page number {0} | [optional] [default to 0] |
| **size** | **Integer**| size: the page size {1000} | [optional] [default to 1000] |
| **sort** | **String**| sort: the sort pattern {id.runNumber:ASC} | [optional] [default to id.runNumber:ASC] |

### Return type

[**RunLumiSetDto**](../Models/RunLumiSetDto.md)

### Authorization

[OpenID](../README.md#OpenID), [BearerAuth](../README.md#BearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="updateRunInfo"></a>
# **updateRunInfo**
> RunLumiSetDto updateRunInfo(RunLumiInfoDto)

Update an entry for run information.

    Run informations go into a separate table. To update an entry, the run number and the lumi section must be provided.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **RunLumiInfoDto** | [**RunLumiInfoDto**](../Models/RunLumiInfoDto.md)|  | [optional] |

### Return type

[**RunLumiSetDto**](../Models/RunLumiSetDto.md)

### Authorization

[OpenID](../README.md#OpenID), [BearerAuth](../README.md#BearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

