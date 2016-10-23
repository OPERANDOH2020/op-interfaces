# LogApi

All URIs are relative to *http://localhost:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**lodDB**](LogApi.md#lodDB) | **POST** /operando/core/logdb | Inserts received data to the database.


<a name="lodDB"></a>
# **lodDB**
> String lodDB(request)

Inserts received data to the database.

Inserts received data to the database by using Log4j.

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.LogApi;


LogApi apiInstance = new LogApi();
LogRequest request = new LogRequest(); // LogRequest | The request data in JSON format to be inserted in the database by using Log4j
try {
    String result = apiInstance.lodDB(request);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling LogApi#lodDB");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **request** | [**LogRequest**](LogRequest.md)| The request data in JSON format to be inserted in the database by using Log4j |

### Return type

**String**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

