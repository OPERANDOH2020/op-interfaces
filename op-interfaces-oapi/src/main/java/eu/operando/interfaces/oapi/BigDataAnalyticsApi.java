package eu.operando.interfaces.oapi;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.operando.AuthenticationWrapper;
import eu.operando.UnableToGetDataException;
import eu.operando.api.AuthenticationService;
import eu.operando.api.factories.AuthenticationServiceFactory;
import eu.operando.api.model.AnalyticsReport;
import eu.operando.interfaces.oapi.factories.BigDataAnalyticsApiServiceFactory;
import io.swagger.annotations.ApiParam;

@Path("/bda")

@Produces({ MediaType.APPLICATION_JSON })
@io.swagger.annotations.Api(description = "the bda API")
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2016-08-31T09:45:10.086Z")
public class BigDataAnalyticsApi
{
	final Logger LOGGER;
	
	private static final String SERVICE_ID = "GET/bda/jobs/{job-id}/reports/latest";
	
	// Location of properties file.
	private static final String PROPERTIES_FILE_OAPI = "config.properties";
	
	private AuthenticationService authenticationDelegate;
	private BigDataAnalyticsApiService bigDataDelegate;

	public BigDataAnalyticsApi(){
		authenticationDelegate = AuthenticationServiceFactory.getAuthenticationService(PROPERTIES_FILE_OAPI);
		bigDataDelegate = BigDataAnalyticsApiServiceFactory.getBdaApi();
		LOGGER = LogManager.getLogger(BigDataAnalyticsApi.class);
	}
	
	@GET
	@Path("/jobs/{job-id}/reports/latest")
	@Produces({ MediaType.APPLICATION_JSON })
	@io.swagger.annotations.ApiOperation(
		value = "", 
		notes = "", 
		response = AnalyticsReport.class, 
		tags = { "big data analytics" }
	)
	@io.swagger.annotations.ApiResponses(value = {
		@io.swagger.annotations.ApiResponse(
			code = 200, 
			message = "Successful response", 
			response = AnalyticsReport.class
		),
		@io.swagger.annotations.ApiResponse(
			code = 401,
			message = "Error - The user is not authenticated with the OPERANDO system. Check that the service ticket provided by the authentication service is correctly included in the message body.",
			response = AnalyticsReport.class
		),
		@io.swagger.annotations.ApiResponse(
			code = 404,
			message = "Error - The specified job could not be found.",
			response = AnalyticsReport.class
		),
		@io.swagger.annotations.ApiResponse(
			code = 500,
			message = "Error - An internal error has occured.",
			response = AnalyticsReport.class
		) 
	})
	public Response getBdaReport(
		@ApiParam(value = "Ticket proving that the caller is allowed to use this service", required = true) @HeaderParam("service-ticket") 
			String serviceTicket,
		@ApiParam(value = "Identification of the job to get the status about", required = true) @PathParam("job-id") 
			String jobId
	){
		Response response;
		try{
			AuthenticationWrapper wrapper = authenticationDelegate.requestAuthenticationDetails(serviceTicket, SERVICE_ID);
			if(wrapper.isTicketValid()){
				AnalyticsReport report = bigDataDelegate.getBdaReport(jobId, wrapper.getIdOspUser());
				if(report == null){
					response = Response.status(Status.NOT_FOUND).build();
				} 
				else {
					response = Response.ok(report).build();
				}
			}
			else {
				response = Response.status(Status.UNAUTHORIZED).build();
			}
		}
		catch(UnableToGetDataException ex){
			LOGGER.error("Error for Big Data Analytics Api: " + ex.toString());
			response = Response.serverError().build();
		}
		return response;
	}
}