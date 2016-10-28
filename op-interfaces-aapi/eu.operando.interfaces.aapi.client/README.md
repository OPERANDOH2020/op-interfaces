# swagger-java-client

## Requirements

Building the API client library requires [Maven](https://maven.apache.org/) to be installed.

## Installation

To install the API client library to your local Maven repository, simply execute:

```shell
mvn install
```

To deploy it to a remote Maven repository instead, configure the settings of the repository and execute:

```shell
mvn deploy
```

Refer to the [official documentation](https://maven.apache.org/plugins/maven-deploy-plugin/usage.html) for more information.

### Maven users

Add this dependency to your project's POM:

```xml
<dependency>
    <groupId>io.swagger</groupId>
    <artifactId>swagger-java-client</artifactId>
    <version>1.0.0</version>
    <scope>compile</scope>
</dependency>
```

### Gradle users

Add this dependency to your project's build file:

```groovy
compile "io.swagger:swagger-java-client:1.0.0"
```

### Others

At first generate the JAR by executing:

    mvn package

Then manually install the following JARs:

* target/swagger-java-client-1.0.0.jar
* target/lib/*.jar

## Getting Started

Please follow the [installation](#installation) instruction and execute the following Java code:

```java

import io.swagger.client.*;
import io.swagger.client.auth.*;
import io.swagger.client.model.*;
import io.swagger.client.api.DefaultApi;

import java.io.File;
import java.util.*;

public class DefaultApiExample {

    public static void main(String[] args) {
        
        DefaultApi apiInstance = new DefaultApi();
        UserCredential userCredential = new UserCredential(); // UserCredential | Users username, password
        try {
            String result = apiInstance.aapiTicketsPost(userCredential);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling DefaultApi#aapiTicketsPost");
            e.printStackTrace();
        }
    }
}

```

## Documentation for API Endpoints

All URIs are relative to *https://localhost:8080/operando/interfaces/authentication*

Class | Method | HTTP request | Description
------------ | ------------- | ------------- | -------------
*DefaultApi* | [**aapiTicketsPost**](docs/DefaultApi.md#aapiTicketsPost) | **POST** /aapi/tickets |  This operation makes a request for a ticket granting ticket (TGT) to the AAPI, which is the session key for the application SSO session. This operation should be called the very first time for an application to be authenticated  to OPERANDOs CAS server, through a login form.
*DefaultApi* | [**aapiTicketsStValidateGet**](docs/DefaultApi.md#aapiTicketsStValidateGet) | **GET** /aapi/tickets/{st}/validate | 
*DefaultApi* | [**aapiTicketsTgtPost**](docs/DefaultApi.md#aapiTicketsTgtPost) | **POST** /aapi/tickets/{tgt} |  This operation makes a request for a service ticket (ST) to the AAPI, which is the authorization ticket for a specific protected service of OPERANDOs system. This operation should be called each time the user tried to access a protected service
*DefaultApi* | [**aapiUserRegisterPost**](docs/DefaultApi.md#aapiUserRegisterPost) | **POST** /aapi/user/register | This operation registers a user to OPERANDOs platform.
*DefaultApi* | [**userUsernameDelete**](docs/DefaultApi.md#userUsernameDelete) | **DELETE** /user/{username} | 
*DefaultApi* | [**userUsernameGet**](docs/DefaultApi.md#userUsernameGet) | **GET** /user/{username} | This operation returns the OPERANDOs registed user with given username
*DefaultApi* | [**userUsernamePut**](docs/DefaultApi.md#userUsernamePut) | **PUT** /user/{username} | 


## Documentation for Models

 - [Attribute](docs/Attribute.md)
 - [User](docs/User.md)
 - [UserCredential](docs/UserCredential.md)


## Documentation for Authorization

All endpoints do not require authorization.
Authentication schemes defined for the API:

## Recommendation

It's recommended to create an instance of `ApiClient` per thread in a multithreaded environment to avoid any potential issue.

## Author

kpatsak@gmail.com

