# Documentation for CREST Server

<a name="documentation-for-api-endpoints"></a>
## Documentation for API Endpoints

All URIs are relative to *http://crest-j23.cern.ch/api-v5.0*

| Class | Method | HTTP request | Description |
|------------ | ------------- | ------------- | -------------|
| *AdminApi* | [**removeGlobalTag**](Apis/AdminApi.md#removeglobaltag) | **DELETE** /admin/globaltags/{name} | Remove a GlobalTag from the database. |
*AdminApi* | [**removeTag**](Apis/AdminApi.md#removetag) | **DELETE** /admin/tags/{name} | Remove a Tag from the database. |
*AdminApi* | [**updateGlobalTag**](Apis/AdminApi.md#updateglobaltag) | **PUT** /admin/globaltags/{name} | Update a GlobalTag in the database. |
| *FoldersApi* | [**createFolder**](Apis/FoldersApi.md#createfolder) | **POST** /folders | Create an entry for folder information. |
*FoldersApi* | [**listFolders**](Apis/FoldersApi.md#listfolders) | **GET** /folders | Finds a FolderDto list. |
| *GlobaltagmapsApi* | [**createGlobalTagMap**](Apis/GlobaltagmapsApi.md#createglobaltagmap) | **POST** /globaltagmaps | Create a GlobalTagMap in the database. |
*GlobaltagmapsApi* | [**deleteGlobalTagMap**](Apis/GlobaltagmapsApi.md#deleteglobaltagmap) | **DELETE** /globaltagmaps/{name} | Delete GlobalTagMapDto lists. |
*GlobaltagmapsApi* | [**findGlobalTagMap**](Apis/GlobaltagmapsApi.md#findglobaltagmap) | **GET** /globaltagmaps/{name} | Find GlobalTagMapDto lists. |
| *GlobaltagsApi* | [**createGlobalTag**](Apis/GlobaltagsApi.md#createglobaltag) | **POST** /globaltags | Create a GlobalTag in the database. |
*GlobaltagsApi* | [**findGlobalTag**](Apis/GlobaltagsApi.md#findglobaltag) | **GET** /globaltags/{name} | Finds a GlobalTagDto by name |
*GlobaltagsApi* | [**findGlobalTagFetchTags**](Apis/GlobaltagsApi.md#findglobaltagfetchtags) | **GET** /globaltags/{name}/tags | Finds a TagDtos lists associated to the global tag name in input. |
*GlobaltagsApi* | [**listGlobalTags**](Apis/GlobaltagsApi.md#listglobaltags) | **GET** /globaltags | Finds a GlobalTagDtos lists. |
| *IovsApi* | [**findAllIovs**](Apis/IovsApi.md#findalliovs) | **GET** /iovs | Finds a IovDtos lists. |
*IovsApi* | [**getSizeByTag**](Apis/IovsApi.md#getsizebytag) | **GET** /iovs/size | Get the number o iovs for tags matching pattern. |
*IovsApi* | [**selectIovPayloads**](Apis/IovsApi.md#selectiovpayloads) | **GET** /iovs/infos | Select iovs and payload meta info for a given tagname and in a given range. |
*IovsApi* | [**storeIovBatch**](Apis/IovsApi.md#storeiovbatch) | **POST** /iovs | Create IOVs in the database, associated to a tag name. |
*IovsApi* | [**storeIovOne**](Apis/IovsApi.md#storeiovone) | **PUT** /iovs | Create a single IOV in the database, associated to a tag name. |
| *MonitoringApi* | [**listPayloadTagInfo**](Apis/MonitoringApi.md#listpayloadtaginfo) | **GET** /monitoring/payloads | Retrieves monitoring information on payload as a list of PayloadTagInfoDtos. |
| *PayloadsApi* | [**getPayload**](Apis/PayloadsApi.md#getpayload) | **GET** /payloads/data | Finds a payload resource associated to the hash. |
*PayloadsApi* | [**listPayloads**](Apis/PayloadsApi.md#listpayloads) | **GET** /payloads | Finds Payloads metadata. |
*PayloadsApi* | [**storePayloadBatch**](Apis/PayloadsApi.md#storepayloadbatch) | **POST** /payloads | Create Payloads in the database, associated to a given iov since list and tag name. |
*PayloadsApi* | [**updatePayload**](Apis/PayloadsApi.md#updatepayload) | **PUT** /payloads/data | Update a streamerInfo in a payload |
*PayloadsApi* | [**uploadJson**](Apis/PayloadsApi.md#uploadjson) | **PUT** /payloads | Upload and process large JSON data. |
| *RuninfoApi* | [**createRunInfo**](Apis/RuninfoApi.md#createruninfo) | **POST** /runinfo | Create an entry for run information. |
*RuninfoApi* | [**listRunInfo**](Apis/RuninfoApi.md#listruninfo) | **GET** /runinfo | Finds a RunLumiInfoDto lists using parameters. |
*RuninfoApi* | [**updateRunInfo**](Apis/RuninfoApi.md#updateruninfo) | **PUT** /runinfo | Update an entry for run information. |
| *TagsApi* | [**createTag**](Apis/TagsApi.md#createtag) | **POST** /tags | Create a Tag in the database. |
*TagsApi* | [**createTagMeta**](Apis/TagsApi.md#createtagmeta) | **POST** /tags/{name}/meta | Create a TagMeta in the database. |
*TagsApi* | [**findTag**](Apis/TagsApi.md#findtag) | **GET** /tags/{name} | Finds a TagDto by name |
*TagsApi* | [**findTagMeta**](Apis/TagsApi.md#findtagmeta) | **GET** /tags/{name}/meta | Finds a TagMetaDto by name |
*TagsApi* | [**listTags**](Apis/TagsApi.md#listtags) | **GET** /tags | Finds a TagDtos lists. |
*TagsApi* | [**updateTag**](Apis/TagsApi.md#updatetag) | **PUT** /tags/{name} | Update a TagDto by name |
*TagsApi* | [**updateTagMeta**](Apis/TagsApi.md#updatetagmeta) | **PUT** /tags/{name}/meta | Update a TagMetaDto by name |


<a name="documentation-for-models"></a>
## Documentation for Models

 - [CrestBaseResponse](./Models/CrestBaseResponse.md)
 - [FolderDto](./Models/FolderDto.md)
 - [FolderSetDto](./Models/FolderSetDto.md)
 - [GenericMap](./Models/GenericMap.md)
 - [GlobalTagDto](./Models/GlobalTagDto.md)
 - [GlobalTagMapDto](./Models/GlobalTagMapDto.md)
 - [GlobalTagMapSetDto](./Models/GlobalTagMapSetDto.md)
 - [GlobalTagSetDto](./Models/GlobalTagSetDto.md)
 - [HTTPResponse](./Models/HTTPResponse.md)
 - [IovDto](./Models/IovDto.md)
 - [IovPayloadDto](./Models/IovPayloadDto.md)
 - [IovPayloadSetDto](./Models/IovPayloadSetDto.md)
 - [IovSetDto](./Models/IovSetDto.md)
 - [PayloadDto](./Models/PayloadDto.md)
 - [PayloadSetDto](./Models/PayloadSetDto.md)
 - [PayloadTagInfoDto](./Models/PayloadTagInfoDto.md)
 - [PayloadTagInfoSetDto](./Models/PayloadTagInfoSetDto.md)
 - [RespPage](./Models/RespPage.md)
 - [RunLumiInfoDto](./Models/RunLumiInfoDto.md)
 - [RunLumiSetDto](./Models/RunLumiSetDto.md)
 - [StoreDto](./Models/StoreDto.md)
 - [StoreSetDto](./Models/StoreSetDto.md)
 - [TagDto](./Models/TagDto.md)
 - [TagMetaDto](./Models/TagMetaDto.md)
 - [TagMetaSetDto](./Models/TagMetaSetDto.md)
 - [TagSetDto](./Models/TagSetDto.md)
 - [TagSummaryDto](./Models/TagSummaryDto.md)
 - [TagSummarySetDto](./Models/TagSummarySetDto.md)


<a name="documentation-for-authorization"></a>
## Documentation for Authorization

<a name="BearerAuth"></a>
### BearerAuth

- **Type**: HTTP Bearer Token authentication

<a name="OpenID"></a>
### OpenID


