package eu.operando.interfaces.oapi;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import eu.operando.interfaces.oapi.factories.BigDataAnalyticsApiServiceFactory;
import eu.operando.interfaces.oapi.model.WrapperBdaRequestBody;
import io.swagger.annotations.ApiParam;

@Path("/bda")

@Produces({ MediaType.APPLICATION_JSON })
@io.swagger.annotations.Api(description = "the bda API")
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2016-08-31T09:45:10.086Z")
public class BigDataAnalyticsApi
{

	private final BigDataAnalyticsApiService delegate = BigDataAnalyticsApiServiceFactory.getBdaApi();

	@GET
	@Path("/jobs/{job-id}/reports")
	@Produces({ MediaType.APPLICATION_JSON })
	@io.swagger.annotations.ApiOperation(value = "", notes = "provides a link to download a report", response = void.class, tags = {})
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Successful response", response = void.class),

			@io.swagger.annotations.ApiResponse(
					code = 401,
					message = "The user is not authenticated with the OPERANDO system. Check that the service ticket provided by the authentication service is correctly included in the message body.",
					response = void.class),

			@io.swagger.annotations.ApiResponse(
					code = 403,
					message = "The user is authenticated with the OPERANDO system, but is not allowed to perform the requested action.",
					response = void.class) })
	public Response getBdaReport(@ApiParam(value = "Ticket proving that the caller is allowed to use this service", required = true) @HeaderParam("service-ticket") String serviceTicket,
			@ApiParam(value = "Identification of the job to get the status about", required = true) @PathParam("job-id") String jobId,
			@ApiParam(value = "Identification of the requesting end user", required = true) WrapperBdaRequestBody wrapper)
	{
		return delegate.getBdaReport(serviceTicket, wrapper, jobId);
	}
}