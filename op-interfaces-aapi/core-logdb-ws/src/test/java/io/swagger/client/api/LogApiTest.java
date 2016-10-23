package io.swagger.client.api;

import io.swagger.client.ApiException;
import io.swagger.client.model.LogRequest;
import io.swagger.client.model.Error;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API tests for LogApi
 */
public class LogApiTest {

    private final LogApi api = new LogApi();

    
    /**
     * Inserts received data to the database.
     *
     * Inserts received data to the database by using Log4j.
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void lodDBTest() throws ApiException {
        LogRequest request = null;
        // String response = api.lodDB(request);

        // TODO: test validations
    }
    
}
