package eu.operando.interfaces.rapi;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import eu.operando.OperandoCommunicationException;
import eu.operando.OperandoCommunicationException.CommunicationError;
import eu.operando.api.AuthenticationService;
import eu.operando.api.factories.AuthenticationServiceFactory;
import eu.operando.api.model.ComplianceReport;
import eu.operando.interfaces.rapi.factories.ComplianceReportApiServiceFactory;
import io.swagger.annotations.ApiParam;

@Path("/osps")
@Produces({ MediaType.APPLICATION_JSON })
@io.swagger.annotations.Api(description = "the compliance report API")
public class ComplianceReportApi {
	
	private static final String SERVICE_ID = "GET/osps/{osp-id}/compliance-report";
	
	private AuthenticationService authenticationDelegate;
	private ComplianceReportApiService reportDelegate;
	
	public ComplianceReportApi(){
		authenticationDelegate = AuthenticationServiceFactory.getAuthenticationService();
		reportDelegate = ComplianceReportApiServiceFactory.getComplienceReportApiService();
	}
	
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
				message = "Error - The user is not authenticated with the OPERANDO system. Check that the service ticket provided by the authentication service is correctly included in the message body.",
				response = ComplianceReport.class
			),
			@io.swagger.annotations.ApiResponse(
				code = 404,
				message = "Error - The OSP could not be found.",
				response = ComplianceReport.class
			),
			@io.swagger.annotations.ApiResponse(
				code = 500,
				message = "Error - An internal error has occured.",
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
		Response response;
		try{
			if(authenticationDelegate.isAuthenticatedForService(serviceTicket, SERVICE_ID)){
				ComplianceReport report = reportDelegate.getComplianceReport(ospId);
				response = Response.ok(report).build();
			} else {
				response = Response.status(Status.UNAUTHORIZED).build();
			}
		}
		catch(OperandoCommunicationException ex){
			CommunicationError error = ex.getCommunitcationError();
			if(error == CommunicationError.REQUESTED_RESOURCE_NOT_FOUND){
				response = Response.status(Status.NOT_FOUND).build();
			} 
			else {
				response = Response.serverError().build();
			}
		}
		return response;
	}
	
}
