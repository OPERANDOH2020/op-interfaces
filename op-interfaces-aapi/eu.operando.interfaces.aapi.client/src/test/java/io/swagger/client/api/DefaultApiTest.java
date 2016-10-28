package io.swagger.client.api;

import io.swagger.client.ApiException;
import io.swagger.client.model.UserCredential;
import io.swagger.client.model.User;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API tests for DefaultApi
 */
public class DefaultApiTest {

    private final DefaultApi api = new DefaultApi();

    
    /**
     *  This operation makes a request for a ticket granting ticket (TGT) to the AAPI, which is the session key for the application SSO session. This operation should be called the very first time for an application to be authenticated  to OPERANDOs CAS server, through a login form.
     *
     * Login to AS and issue a session ticket (tgt)
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void aapiTicketsPostTest() throws ApiException {
        UserCredential userCredential = null;
        // String response = api.aapiTicketsPost(userCredential);

        // TODO: test validations
    }
    
    /**
     * 
     *
     * Validate the service ticket (ST)
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void aapiTicketsStValidateGetTest() throws ApiException {
        String st = null;
        String serviceId = null;
        // api.aapiTicketsStValidateGet(st, serviceId);

        // TODO: test validations
    }
    
    /**
     *  This operation makes a request for a service ticket (ST) to the AAPI, which is the authorization ticket for a specific protected service of OPERANDOs system. This operation should be called each time the user tried to access a protected service
     *
     * Request a service ticket (ST) for the service with id serviceId
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void aapiTicketsTgtPostTest() throws ApiException {
        String tgt = null;
        String serviceId = null;
        // String response = api.aapiTicketsTgtPost(tgt, serviceId);

        // TODO: test validations
    }
    
    /**
     * This operation registers a user to OPERANDOs platform.
     *
     * This operation registers a user to OPERANDOs platform.
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void aapiUserRegisterPostTest() throws ApiException {
        User user = null;
        // User response = api.aapiUserRegisterPost(user);

        // TODO: test validations
    }
    
    /**
     * 
     *
     * Delete ASs registed user with corresponding username
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void userUsernameDeleteTest() throws ApiException {
        String username = null;
        // User response = api.userUsernameDelete(username);

        // TODO: test validations
    }
    
    /**
     * This operation returns the OPERANDOs registed user with given username
     *
     * This operation returns the OPERANDOs registed user with given username
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void userUsernameGetTest() throws ApiException {
        String username = null;
        // User response = api.userUsernameGet(username);

        // TODO: test validations
    }
    
    /**
     * 
     *
     * Updates the content of ASs registed user with corresponding username
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void userUsernamePutTest() throws ApiException {
        String username = null;
        User user = null;
        // User response = api.userUsernamePut(username, user);

        // TODO: test validations
    }
    
}
