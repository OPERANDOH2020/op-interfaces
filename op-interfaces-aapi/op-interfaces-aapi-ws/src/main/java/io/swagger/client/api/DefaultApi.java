package io.swagger.client.api;

import com.sun.jersey.api.client.GenericType;

import io.swagger.client.ApiException;
import io.swagger.client.ApiClient;
import io.swagger.client.Configuration;
import io.swagger.client.model.*;
import io.swagger.client.Pair;

import io.swagger.client.model.UserCredential;
import io.swagger.client.model.User;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2016-06-27T22:29:17.225Z")
public class DefaultApi {
  private ApiClient apiClient;

  public DefaultApi() {
    this(Configuration.getDefaultApiClient());
  }

  public DefaultApi(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  public ApiClient getApiClient() {
    return apiClient;
  }

  public void setApiClient(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  /**
   *  This operation makes a request for a ticket granting ticket (TGT) to the AAPI, which is the session key for the application SSO session. This operation should be called the very first time for an application to be authenticated  to OPERANDOs CAS server, through a login form.
   * Login to AS and issue a session ticket (tgt)
   * @param userCredential Users username, password (required)
   * @return String
   * @throws ApiException if fails to make API call
   */
  public String aapiTicketsPost(UserCredential userCredential) throws ApiException {
    Object localVarPostBody = userCredential;
    
    // verify the required parameter 'userCredential' is set
    if (userCredential == null) {
      throw new ApiException(400, "Missing the required parameter 'userCredential' when calling aapiTicketsPost");
    }
    
    // create path and map variables
    String localVarPath = "/aapi/tickets".replaceAll("\\{format\\}","json");

    // query params
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();


    
    
    final String[] localVarAccepts = {
      
    };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);

    final String[] localVarContentTypes = {
      
    };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] {  };

    GenericType<String> localVarReturnType = new GenericType<String>() {};
    return apiClient.invokeAPI(localVarPath, "POST", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
      }
  /**
   * 
   * Validate the service ticket (ST)
   * @param st service ticket (ST) (required)
   * @param serviceId service identifier (required)
   * @throws ApiException if fails to make API call
   */
  public void aapiTicketsStValidateGet(String st, String serviceId) throws ApiException {
    Object localVarPostBody = null;
    
    // verify the required parameter 'st' is set
    if (st == null) {
      throw new ApiException(400, "Missing the required parameter 'st' when calling aapiTicketsStValidateGet");
    }
    
    // verify the required parameter 'serviceId' is set
    if (serviceId == null) {
      throw new ApiException(400, "Missing the required parameter 'serviceId' when calling aapiTicketsStValidateGet");
    }
    
    // create path and map variables
    String localVarPath = "/aapi/tickets/{st}/validate".replaceAll("\\{format\\}","json")
      .replaceAll("\\{" + "st" + "\\}", apiClient.escapeString(st.toString()));

    // query params
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();

    localVarQueryParams.addAll(apiClient.parameterToPairs("", "serviceId", serviceId));

    
    
    final String[] localVarAccepts = {
      
    };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);

    final String[] localVarContentTypes = {
      
    };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] {  };


    apiClient.invokeAPI(localVarPath, "GET", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, null);
  }
  /**
   *  This operation makes a request for a service ticket (ST) to the AAPI, which is the authorization ticket for a specific protected service of OPERANDOs system. This operation should be called each time the user tried to access a protected service
   * Request a service ticket (ST) for the service with id serviceId
   * @param tgt Users session ticket (TGT) (required)
   * @param serviceId Services endpoint (required)
   * @return String
   * @throws ApiException if fails to make API call
   */
  public String aapiTicketsTgtPost(String tgt, String serviceId) throws ApiException {
    Object localVarPostBody = serviceId;
    
    // verify the required parameter 'tgt' is set
    if (tgt == null) {
      throw new ApiException(400, "Missing the required parameter 'tgt' when calling aapiTicketsTgtPost");
    }
    
    // verify the required parameter 'serviceId' is set
    if (serviceId == null) {
      throw new ApiException(400, "Missing the required parameter 'serviceId' when calling aapiTicketsTgtPost");
    }
    
    // create path and map variables
    String localVarPath = "/aapi/tickets/{tgt}".replaceAll("\\{format\\}","json")
      .replaceAll("\\{" + "tgt" + "\\}", apiClient.escapeString(tgt.toString()));

    // query params
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();


    
    
    final String[] localVarAccepts = {
      
    };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);

    final String[] localVarContentTypes = {
      
    };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] {  };

    GenericType<String> localVarReturnType = new GenericType<String>() {};
    return apiClient.invokeAPI(localVarPath, "POST", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
      }
  /**
   * This operation registers a user to OPERANDOs platform.
   * This operation registers a user to OPERANDOs platform.
   * @param user User description (required)
   * @return User
   * @throws ApiException if fails to make API call
   */
  public User aapiUserRegisterPost(User user) throws ApiException {
    Object localVarPostBody = user;
    
    // verify the required parameter 'user' is set
    if (user == null) {
      throw new ApiException(400, "Missing the required parameter 'user' when calling aapiUserRegisterPost");
    }
    
    // create path and map variables
    String localVarPath = "/aapi/user/register".replaceAll("\\{format\\}","json");

    // query params
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();


    
    
    final String[] localVarAccepts = {
      
    };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);

    final String[] localVarContentTypes = {
      
    };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] {  };

    GenericType<User> localVarReturnType = new GenericType<User>() {};
    return apiClient.invokeAPI(localVarPath, "POST", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
      }
  /**
   * 
   * Delete ASs registed user with corresponding username
   * @param username Users username (required)
   * @return User
   * @throws ApiException if fails to make API call
   */
  public User userUsernameDelete(String username) throws ApiException {
    Object localVarPostBody = null;
    
    // verify the required parameter 'username' is set
    if (username == null) {
      throw new ApiException(400, "Missing the required parameter 'username' when calling userUsernameDelete");
    }
    
    // create path and map variables
    String localVarPath = "/user/{username}".replaceAll("\\{format\\}","json")
      .replaceAll("\\{" + "username" + "\\}", apiClient.escapeString(username.toString()));

    // query params
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();


    
    
    final String[] localVarAccepts = {
      
    };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);

    final String[] localVarContentTypes = {
      
    };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] {  };

    GenericType<User> localVarReturnType = new GenericType<User>() {};
    return apiClient.invokeAPI(localVarPath, "DELETE", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
      }
  /**
   * This operation returns the OPERANDOs registed user with given username
   * This operation returns the OPERANDOs registed user with given username
   * @param username Users username (required)
   * @return User
   * @throws ApiException if fails to make API call
   */
  public User userUsernameGet(String username) throws ApiException {
    Object localVarPostBody = null;
    
    // verify the required parameter 'username' is set
    if (username == null) {
      throw new ApiException(400, "Missing the required parameter 'username' when calling userUsernameGet");
    }
    
    // create path and map variables
    String localVarPath = "/user/{username}".replaceAll("\\{format\\}","json")
      .replaceAll("\\{" + "username" + "\\}", apiClient.escapeString(username.toString()));

    // query params
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();


    
    
    final String[] localVarAccepts = {
      
    };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);

    final String[] localVarContentTypes = {
      
    };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] {  };

    GenericType<User> localVarReturnType = new GenericType<User>() {};
    return apiClient.invokeAPI(localVarPath, "GET", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
      }
  /**
   * 
   * Updates the content of ASs registed user with corresponding username
   * @param username Users username (required)
   * @param user Users data (required)
   * @return User
   * @throws ApiException if fails to make API call
   */
  public User userUsernamePut(String username, User user) throws ApiException {
    Object localVarPostBody = user;
    
    // verify the required parameter 'username' is set
    if (username == null) {
      throw new ApiException(400, "Missing the required parameter 'username' when calling userUsernamePut");
    }
    
    // verify the required parameter 'user' is set
    if (user == null) {
      throw new ApiException(400, "Missing the required parameter 'user' when calling userUsernamePut");
    }
    
    // create path and map variables
    String localVarPath = "/user/{username}".replaceAll("\\{format\\}","json")
      .replaceAll("\\{" + "username" + "\\}", apiClient.escapeString(username.toString()));

    // query params
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();


    
    
    final String[] localVarAccepts = {
      
    };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);

    final String[] localVarContentTypes = {
      
    };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] {  };

    GenericType<User> localVarReturnType = new GenericType<User>() {};
    return apiClient.invokeAPI(localVarPath, "PUT", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
      }
}
