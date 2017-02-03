package eu.operando.interfaces.rapi;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import eu.operando.api.model.ComplianceReport;
import eu.operando.interfaces.rapi.factories.ComplianceReportApiServiceFactory;
import io.swagger.annotations.ApiParam;

@Path("/osps")
@Produces({ MediaType.APPLICATION_JSON })
@io.swagger.annotations.Api(description = "the compliance report API")
public class ComplianceReportApi {
	private final ComplianceReportApiService delegate = ComplianceReportApiServiceFactory.getComplienceReportApiService();
	
	@GET
	@Path("{osp-id}/compliance-report")
	@Produces({ MediaType.APPLICATION_JSON })
	@io.swagger.annotations.ApiOperation(
		value = "Get the compliance report for an OSP.",
		notes = "Called by a regulator to ensure that the platform becomes compliant with the new regulation. The regulation that was saved is returned, along with an identifer which should be used to refer to it in later communication. ",
		response = ComplianceReport.class,
		tags = { "regulations", }
	)
	@io.swagger.annotations.ApiResponses(
		value = {
			@io.swagger.annotations.ApiResponse(
				code = 200,
				message = "The compliance report for the OSP is returned as a JSON object.",
				response = ComplianceReport.class
			),
			@io.swagger.annotations.ApiResponse(
				code = 401,
				message = "The user is not authenticated with the OPERANDO system. Check that the service ticket provided by the authentication service is correctly included in the message body.",
				response = ComplianceReport.class
			),
			@io.swagger.annotations.ApiResponse(
				code = 404,
				message = "Error - The OSP could not be found.",
				response = ComplianceReport.class
			) 
		}
	)
	public Response complianceReportGet(
		@ApiParam(value = "Ticket proving that the caller is allowed to use this service", required = true) @HeaderParam("service-ticket") 
			String serviceTicket,
		@ApiParam(value = "The unique identifier of an online service provider.", required = true) @PathParam("osp-id") 
			String ospId
	){
		return delegate.ComplianceReportGet(serviceTicket, ospId);
	}
	
}
