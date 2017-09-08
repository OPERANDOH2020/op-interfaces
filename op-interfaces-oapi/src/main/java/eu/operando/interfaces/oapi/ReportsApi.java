package eu.operando.interfaces.oapi;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import eu.operando.interfaces.oapi.factories.ReportsApiServiceFactory;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;

@Path("/reports")
@RestController
@RequestMapping(value = "/reports")
@Produces({ MediaType.APPLICATION_JSON })
public class ReportsApi
{
	private final ReportsApiService delegate = ReportsApiServiceFactory.getReportsApi();

	@GET
	@Path("/{report-id}")
	@Produces({ MediaType.APPLICATION_JSON })
	@RequestMapping(value= "/{report-id}", method = RequestMethod.GET)
	@ApiOperation(value = "", notes = "Called by a regulator to obtain a report matching the given ID. Intended (at the moment) to return a compliance report relating to an OSP.", response = String.class)
	@ApiResponses(
		value = {
			@ApiResponse(
				code = 200,
				message = "The request was successful. The report is returned in the response body in the requested format."),
			@ApiResponse(
				code = 401,
				message = "The user is not authenticated with the OPERANDO system. Check that the service ticket provided by the authentication service is correctly included in the message body."),
			@ApiResponse(
				code = 403,
				message = "The user is authenticated with the OPERANDO system, but is not allowed to perform the requested action.")
			})
	@ApiImplicitParams({
		@ApiImplicitParam(name = "service-ticket", value = "Ticket proving that the caller is allowed to use this service", required = true, dataType = "string", paramType = "header"),
		@ApiImplicitParam(name = "report-id", value = "The unique identifier of a report", required = true, dataType = "string", paramType = "path"),
		@ApiImplicitParam(name = "format", value = "The requested format of the report (e.g. pdf, html)", required = true, dataType = "string", paramType = "query"),
	})
public Response reportsReportIdGet(
			@ApiIgnore @HeaderParam("service-ticket") String serviceTicket,
			@ApiIgnore @PathParam("report-id")String reportId,
			@ApiIgnore @QueryParam("format") String format, 
			@ApiIgnore @Context UriInfo uriInfo)
	{
		MultivaluedMap<String, String> optionalParameters = uriInfo.getQueryParameters();
		return delegate.reportsGetReport(serviceTicket, reportId, format, optionalParameters);
	}
}
