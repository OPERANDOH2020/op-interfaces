package eu.operando.interfaces.oapi;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import eu.operando.interfaces.oapi.factories.BdaApiServiceFactory;
import eu.operando.interfaces.oapi.model.WrapperBdaRequestBody;
import io.swagger.annotations.ApiParam;

@Path("/bda")

@Produces({ "application/json" })
@io.swagger.annotations.Api(description = "the bda API")
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2016-08-31T09:45:10.086Z")
public class BdaApi
{
	private final BdaApiService delegate = BdaApiServiceFactory.getBdaApi();

	@GET
	@Path("/jobs/{job_id}/reports")

	@Produces({ "application/json" })
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
	public Response bdaJobsJobIdReportsGet(@ApiParam(value = "Identification of the requesting end user", required = true) WrapperBdaRequestBody wrapper,
			@ApiParam(value = "Identification of the job to get the status about", required = true) @QueryParam("jobId") String jobId)
	{
		return delegate.bdaJobsJobIdReportsGet(wrapper, jobId);
	}
}
