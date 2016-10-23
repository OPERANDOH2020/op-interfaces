
# LogRequest

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**requesterType** | [**RequesterTypeEnum**](#RequesterTypeEnum) | Source type from which comes the request. |  [optional]
**requesterId** | **String** | Id of the requester (e.g the id of an OSP). |  [optional]
**logPriority** | [**LogPriorityEnum**](#LogPriorityEnum) | Priority level of the data to be logged. |  [optional]
**logDataType** | [**LogDataTypeEnum**](#LogDataTypeEnum) | Type of the data to be logged. |  [optional]
**title** | **String** | Subject of the event to be logged. |  [optional]
**description** | **String** | Description of the event to be logged. |  [optional]
**keywords** | **List&lt;String&gt;** | Array of keywords to facilitate search |  [optional]


<a name="RequesterTypeEnum"></a>
## Enum: RequesterTypeEnum
Name | Value
---- | -----
PROCESS | &quot;PROCESS&quot;
MODULE | &quot;MODULE&quot;


<a name="LogPriorityEnum"></a>
## Enum: LogPriorityEnum
Name | Value
---- | -----
LOW | &quot;LOW&quot;
NORMAL | &quot;NORMAL&quot;
HIGH | &quot;HIGH&quot;
CRITICAL | &quot;CRITICAL&quot;


<a name="LogDataTypeEnum"></a>
## Enum: LogDataTypeEnum
Name | Value
---- | -----
INFO | &quot;INFO&quot;
WARN | &quot;WARN&quot;
ERROS | &quot;ERROS&quot;
FATAL | &quot;FATAL&quot;



