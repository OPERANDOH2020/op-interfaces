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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import eu.operando.AuthenticationWrapper;
import eu.operando.UnableToGetDataException;
import eu.operando.api.AuthenticationService;
import eu.operando.api.factories.AuthenticationServiceFactory;
import eu.operando.api.model.AnalyticsReport;
import eu.operando.interfaces.oapi.factories.BigDataAnalyticsServiceFactory;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;

@Path("/bda")
@RestController
@RequestMapping(value="/bda")
@Produces({ MediaType.APPLICATION_JSON })
public class BigDataAnalyticsApi
{
	final Logger LOGGER;
	
	private static final String SERVICE_ID = "GET/bda/jobs/{job-id}/reports/latest";
	
	// Location of properties file.
	private static final String PROPERTIES_FILE_OAPI = "config.properties";
	
	private AuthenticationService authenticationDelegate;
	private BigDataAnalyticsService bigDataDelegate;

	public BigDataAnalyticsApi(){
		authenticationDelegate = AuthenticationServiceFactory.getAuthenticationService(PROPERTIES_FILE_OAPI);
		bigDataDelegate = BigDataAnalyticsServiceFactory.getBdaService();
		LOGGER = LogManager.getLogger(BigDataAnalyticsApi.class);
	}
	
	@GET
	@Path("/jobs/{job-id}/reports/latest")
	@Produces({ MediaType.APPLICATION_JSON })
	@RequestMapping(value = "/jobs/{job-id}/reports/latest", method = RequestMethod.GET)
	@ApiOperation(
		value = "", 
		notes = "provides a link to download a report", 
		response = AnalyticsReport.class
	)
	@ApiResponses(value = {
		@ApiResponse(
			code = 200, 
			message = "Successful response"
		),
		@ApiResponse(
			code = 401,
			message = "Error - The user is not authenticated with the OPERANDO system. Check that the service ticket provided by the authentication service is correctly included in the message body."
		),
		@ApiResponse(
			code = 404,
			message = "Error - The specified job could not be found."
		),
		@ApiResponse(
			code = 500,
			message = "Error - An internal error has occured."
		) 
	})
	@ApiImplicitParams({
		@ApiImplicitParam(name = "service-ticket", value = "Ticket proving that the caller is allowed to use this service", dataType = "string", required = true, paramType= "header"),
		@ApiImplicitParam(name= "job-id", value = "Identification of the job to get the status about", dataType = "string", required = true, paramType="path")
	})
	public Response getBdaReport(
			@ApiIgnore @HeaderParam("service-ticket") String serviceTicket,
			@ApiIgnore @PathParam("job-id") String jobId
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