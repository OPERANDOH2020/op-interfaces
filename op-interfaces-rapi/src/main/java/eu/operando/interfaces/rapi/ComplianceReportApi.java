package eu.operando.interfaces.rapi;

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

import eu.operando.UnableToGetDataException;
import eu.operando.api.AuthenticationService;
import eu.operando.api.factories.AuthenticationServiceFactory;
import eu.operando.api.model.ComplianceReport;
import eu.operando.interfaces.rapi.factories.ComplianceReportsServiceFactory;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;

@Path("/osps/{osp-id}/compliance-report")
@Produces({ MediaType.APPLICATION_JSON })
@RestController
@RequestMapping(value = "/osps/{osp-id}/compliance-report")
public class ComplianceReportApi
{

	final Logger LOGGER;
	
	private static final String PROPERTIES_FILE_RAPI = "config.properties";
	private static final String SERVICE_ID = "GET/osps/{osp-id}/compliance-report";

	private AuthenticationService authenticationDelegate;
	private ComplianceReportsService reportDelegate;

	public ComplianceReportApi()
	{
		authenticationDelegate = AuthenticationServiceFactory.getAuthenticationService(PROPERTIES_FILE_RAPI);
		reportDelegate = ComplianceReportsServiceFactory.getComplienceReportApiService();
		LOGGER = LogManager.getLogger(ComplianceReportApi.class);
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@RequestMapping(value = "", method = RequestMethod.GET)
	@ApiOperation(value = "", response = ComplianceReport.class, notes = "Called by a regulator to obtain a compliance report relating to the specified OSP.")
	@ApiResponses(
		value = {
			@ApiResponse(
				code = 200,
				message = "The compliance report for the OSP is returned as a JSON object."),
			@ApiResponse(
				code = 401,
				message = "Error - The user is not authenticated with the OPERANDO system. Check that the service ticket provided by the authentication service is correctly included in the message body."),
			@ApiResponse(code = 404, message = "Error - The OSP could not be found."),
			@ApiResponse(code = 500, message = "Error - An internal error has occured.") })
	@ApiImplicitParams({
		@ApiImplicitParam(name = "service-ticket", value = "Ticket proving that the caller is allowed to use this service", required = true, dataType = "string", paramType = "header"),
		@ApiImplicitParam(name = "osp-id", value = "The unique identifier of an online service provider.", required = true, dataType = "string", paramType = "path")
	})
	public Response complianceReportGet(@ApiIgnore @HeaderParam("service-ticket") String serviceTicket, @ApiIgnore @PathParam("osp-id") String ospId) throws UnableToGetDataException
	{
		if (!authenticationDelegate.isAuthenticatedForService(serviceTicket, SERVICE_ID))
		{
			return Response.status(Status.UNAUTHORIZED).build();
		}
				
		return getReportAndCreateResponse(ospId);
	}

	private Response getReportAndCreateResponse(String ospId)
	{
		Response response;
		try
		{
			ComplianceReport report = reportDelegate.getComplianceReportForOsp(ospId);
			if (report != null)
			{
				response = Response.ok(report)
					.build();
			}
			else
			{
				response = Response.status(Status.NOT_FOUND).build();
			}
		}
		catch (UnableToGetDataException ex)
		{
			LOGGER.error("Error getting Compliance Report for Compliance Report Api: " + ex.toString());
			response = Response.serverError()
				.build();
		}
		return response;
	}

}
