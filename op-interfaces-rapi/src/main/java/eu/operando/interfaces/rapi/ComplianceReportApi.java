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

import eu.operando.UnableToGetDataException;
import eu.operando.api.AuthenticationService;
import eu.operando.api.factories.AuthenticationServiceFactory;
import eu.operando.api.model.ComplianceReport;
import eu.operando.interfaces.rapi.factories.ComplianceReportsServiceFactory;
import io.swagger.annotations.ApiParam;

@Path("/osps/{osp-id}/compliance-report")
@Produces({ MediaType.APPLICATION_JSON })
@io.swagger.annotations.Api(description = "the compliance report API")
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
	@io.swagger.annotations.ApiOperation(value = "Get the compliance report for an OSP.", response = ComplianceReport.class)
	@io.swagger.annotations.ApiResponses(
		value = {
			@io.swagger.annotations.ApiResponse(
				code = 200,
				message = "The compliance report for the OSP is returned as a JSON object.",
				response = ComplianceReport.class),
			@io.swagger.annotations.ApiResponse(
				code = 401,
				message = "Error - The user is not authenticated with the OPERANDO system. Check that the service ticket provided by the authentication service is correctly included in the message body.",
				response = ComplianceReport.class),
			@io.swagger.annotations.ApiResponse(code = 404, message = "Error - The OSP could not be found.", response = ComplianceReport.class),
			@io.swagger.annotations.ApiResponse(code = 500, message = "Error - An internal error has occured.", response = ComplianceReport.class) })
	public Response complianceReportGet(
			@ApiParam(value = "Ticket proving that the caller is allowed to use this service", required = true) @HeaderParam("service-ticket") String serviceTicket,
			@ApiParam(value = "The unique identifier of an online service provider.", required = true) @PathParam("osp-id") String ospId)
	{
		Response response;
		try
		{
			if (authenticationDelegate.isAuthenticatedForService(serviceTicket, SERVICE_ID))
			{
				response = getReportAndCreateResponse(ospId);
			}
			else
			{
				response = Response.status(Status.UNAUTHORIZED)
					.build();
			}
		}
		catch (UnableToGetDataException e)
		{
			LOGGER.error("Error authenticating Service Ticket for Compliance Report Api: " + e.toString());
			response = Response.serverError()
					.build();
		}

		return response;
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
