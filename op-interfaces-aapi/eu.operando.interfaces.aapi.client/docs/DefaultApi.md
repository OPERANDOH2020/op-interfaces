# DefaultApi

All URIs are relative to *https://localhost:8080/operando/interfaces/authentication*

Method | HTTP request | Description
------------- | ------------- | -------------
[**aapiTicketsPost**](DefaultApi.md#aapiTicketsPost) | **POST** /aapi/tickets |  This operation makes a request for a ticket granting ticket (TGT) to the AAPI, which is the session key for the application SSO session. This operation should be called the very first time for an application to be authenticated  to OPERANDOs CAS server, through a login form.
[**aapiTicketsStValidateGet**](DefaultApi.md#aapiTicketsStValidateGet) | **GET** /aapi/tickets/{st}/validate | 
[**aapiTicketsTgtPost**](DefaultApi.md#aapiTicketsTgtPost) | **POST** /aapi/tickets/{tgt} |  This operation makes a request for a service ticket (ST) to the AAPI, which is the authorization ticket for a specific protected service of OPERANDOs system. This operation should be called each time the user tried to access a protected service
[**aapiUserRegisterPost**](DefaultApi.md#aapiUserRegisterPost) | **POST** /aapi/user/register | This operation registers a user to OPERANDOs platform.
[**userUsernameDelete**](DefaultApi.md#userUsernameDelete) | **DELETE** /user/{username} | 
[**userUsernameGet**](DefaultApi.md#userUsernameGet) | **GET** /user/{username} | This operation returns the OPERANDOs registed user with given username
[**userUsernamePut**](DefaultApi.md#userUsernamePut) | **PUT** /user/{username} | 


<a name="aapiTicketsPost"></a>
# **aapiTicketsPost**
> String aapiTicketsPost(userCredential)

 This operation makes a request for a ticket granting ticket (TGT) to the AAPI, which is the session key for the application SSO session. This operation should be called the very first time for an application to be authenticated  to OPERANDOs CAS server, through a login form.

Login to AS and issue a session ticket (tgt)

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.DefaultApi;


DefaultApi apiInstance = new DefaultApi();
UserCredential userCredential = new UserCredential(); // UserCredential | Users username, password
try {
    String result = apiInstance.aapiTicketsPost(userCredential);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#aapiTicketsPost");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **userCredential** | [**UserCredential**](UserCredential.md)| Users username, password |

### Return type

**String**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a name="aapiTicketsStValidateGet"></a>
# **aapiTicketsStValidateGet**
> aapiTicketsStValidateGet(st, serviceId)



Validate the service ticket (ST)

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.DefaultApi;


DefaultApi apiInstance = new DefaultApi();
String st = "st_example"; // String | service ticket (ST)
String serviceId = "serviceId_example"; // String | service identifier
try {
    apiInstance.aapiTicketsStValidateGet(st, serviceId);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#aapiTicketsStValidateGet");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **st** | **String**| service ticket (ST) |
 **serviceId** | **String**| service identifier |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a name="aapiTicketsTgtPost"></a>
# **aapiTicketsTgtPost**
> String aapiTicketsTgtPost(tgt, serviceId)

 This operation makes a request for a service ticket (ST) to the AAPI, which is the authorization ticket for a specific protected service of OPERANDOs system. This operation should be called each time the user tried to access a protected service

Request a service ticket (ST) for the service with id serviceId

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.DefaultApi;


DefaultApi apiInstance = new DefaultApi();
String tgt = "tgt_example"; // String | Users session ticket (TGT)
String serviceId = "serviceId_example"; // String | Services endpoint
try {
    String result = apiInstance.aapiTicketsTgtPost(tgt, serviceId);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#aapiTicketsTgtPost");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **tgt** | **String**| Users session ticket (TGT) |
 **serviceId** | **String**| Services endpoint |

### Return type

**String**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a name="aapiUserRegisterPost"></a>
# **aapiUserRegisterPost**
> User aapiUserRegisterPost(user)

This operation registers a user to OPERANDOs platform.

This operation registers a user to OPERANDOs platform.

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.DefaultApi;


DefaultApi apiInstance = new DefaultApi();
User user = new User(); // User | User description
try {
    User result = apiInstance.aapiUserRegisterPost(user);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#aapiUserRegisterPost");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **user** | [**User**](User.md)| User description |

### Return type

[**User**](User.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a name="userUsernameDelete"></a>
# **userUsernameDelete**
> User userUsernameDelete(username)



Delete ASs registed user with corresponding username

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.DefaultApi;


DefaultApi apiInstance = new DefaultApi();
String username = "username_example"; // String | Users username
try {
    User result = apiInstance.userUsernameDelete(username);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#userUsernameDelete");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **String**| Users username |

### Return type

[**User**](User.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a name="userUsernameGet"></a>
# **userUsernameGet**
> User userUsernameGet(username)

This operation returns the OPERANDOs registed user with given username

This operation returns the OPERANDOs registed user with given username

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.DefaultApi;


DefaultApi apiInstance = new DefaultApi();
String username = "username_example"; // String | Users username
try {
    User result = apiInstance.userUsernameGet(username);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#userUsernameGet");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **String**| Users username |

### Return type

[**User**](User.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a name="userUsernamePut"></a>
# **userUsernamePut**
> User userUsernamePut(username, user)



Updates the content of ASs registed user with corresponding username

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.DefaultApi;


DefaultApi apiInstance = new DefaultApi();
String username = "username_example"; // String | Users username
User user = new User(); // User | Users data
try {
    User result = apiInstance.userUsernamePut(username, user);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#userUsernamePut");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **String**| Users username |
 **user** | [**User**](User.md)| Users data |

### Return type

[**User**](User.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

