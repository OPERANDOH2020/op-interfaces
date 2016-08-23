package eu.operando.interfaces.rapi.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import eu.operando.interfaces.rapi.api.factories.ReportsApiServiceFactory;
import io.swagger.annotations.ApiParam;

@Path("/reports")

@Produces({ MediaType.APPLICATION_JSON })
@io.swagger.annotations.Api(description = "the reports API")
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2016-07-12T14:06:26.001Z")
public class ReportsApi
{
	private final ReportsApiService delegate = ReportsApiServiceFactory.getReportsApi();

	@GET
	@Path("/{report-id}")
	@Produces({ MediaType.APPLICATION_JSON })
	@io.swagger.annotations.ApiOperation(
			value = "Get a report.",
			notes = "Called by a regulator to obtain a report matching the given ID. Intended (at the moment) to return a compliance report relating to an OSP. ",
			response = String.class,
			tags = { "reports" })
	@io.swagger.annotations.ApiResponses(
			value = {
					@io.swagger.annotations.ApiResponse(
							code = 200,
							message = "The request was successful. The report is returned in the response body in the requested format.",
							response = String.class),
					@io.swagger.annotations.ApiResponse(
							code = 401,
							message = "The user is not authenticated with the OPERANDO system. Check that the service ticket provided by the authentication service is correctly included in the message body.",
							response = String.class),
					@io.swagger.annotations.ApiResponse(
							code = 403,
							message = "The user is authenticated with the OPERANDO system, but is not allowed to perform the requested action.",
							response = String.class) })
	public Response reportsReportIdGet(@ApiParam(value = "the unique identifier of a report.", required = true) @PathParam("report-id") String reportId,
			@ApiParam(value = "the requested format of the report (e.g. pdf, html)", required = true) @QueryParam("format") String format,
			@Context SecurityContext securityContext) throws NotFoundException
	{
		return delegate.reportsReportIdGet(reportId, format, securityContext);
	}
}
