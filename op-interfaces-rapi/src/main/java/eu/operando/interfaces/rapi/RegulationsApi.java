package eu.operando.interfaces.rapi;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import eu.operando.api.model.DtoPrivacyRegulation;
import eu.operando.api.model.PrivacyRegulation;
import eu.operando.api.model.PrivacyRegulationInput;
import eu.operando.interfaces.rapi.factories.RegulationsApiServiceFactory;
import io.swagger.annotations.ApiParam;

@Path("/regulations")

@Produces({ MediaType.APPLICATION_JSON })
@io.swagger.annotations.Api(description = "the regulations API")
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2016-07-12T14:06:26.001Z")
public class RegulationsApi
{
	private final RegulationsApiService delegate = RegulationsApiServiceFactory.getRegulationsApi();

	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@io.swagger.annotations.ApiOperation(
		value = "Add a new regulation to the system.",
		notes = "Called by a regulator to ensure that the platform becomes compliant with the new regulation. The regulation that was saved is returned, along with an identifer which should be used to refer to it in later communication. ",
		response = PrivacyRegulation.class,
		tags = { "regulations", })
	@io.swagger.annotations.ApiResponses(
		value = {
			@io.swagger.annotations.ApiResponse(
				code = 201,
				message = "The regulation was successfully created, and the platform has been made compliant with it.",
				response = PrivacyRegulation.class),
			@io.swagger.annotations.ApiResponse(
				code = 401,
				message = "The user is not authenticated with the OPERANDO system. Check that the service ticket provided by the authentication service is correctly included in the message body.",
				response = PrivacyRegulation.class),
			@io.swagger.annotations.ApiResponse(
				code = 403,
				message = "The user is authenticated with the OPERANDO system, but is not allowed to perform the requested action.",
				response = PrivacyRegulation.class) })
	public Response regulationsPost(@ApiParam(value = "Ticket proving that the caller is allowed to use this service", required = true) @HeaderParam("service-ticket") String serviceTicket,
			@ApiParam(value = "", required = true) PrivacyRegulationInput regulation)
	{
		return delegate.processNewRegulation(serviceTicket, regulation);
	}

	@PUT
	@Path("/{reg-id}")
	@Produces({ MediaType.APPLICATION_JSON })
	@io.swagger.annotations.ApiOperation(
		value = "Update an exisiting regulation.",
		notes = "Called by a regulator to ensure that the platform becomes compliant with the new terms of the regulation. The regulation that was saved is returned, along with an identifer which should be used to refer to it in later communication. ",
		response = DtoPrivacyRegulation.class,
		tags = { "regulations" })
	@io.swagger.annotations.ApiResponses(
		value = {
			@io.swagger.annotations.ApiResponse(
				code = 204,
				message = "The regulation was successfully updated, and the platform has been made compliant with it.",
				response = DtoPrivacyRegulation.class),
			@io.swagger.annotations.ApiResponse(
				code = 401,
				message = "The user is not authenticated with the OPERANDO system. Check that the service ticket provided by the authentication service is correctly included in the message body.",
				response = DtoPrivacyRegulation.class),
			@io.swagger.annotations.ApiResponse(
				code = 403,
				message = "The user is authenticated with the OPERANDO system, but is not allowed to perform the requested action.",
				response = DtoPrivacyRegulation.class) })
	public Response regulationsRegIdPut(@ApiParam(value = "Ticket proving that the caller is allowed to use this service", required = true) @HeaderParam("service-ticket") String serviceTicket,
			@ApiParam(value = "", required = true) PrivacyRegulationInput regulation,
			@ApiParam(value = "the unique identifier of a regulation.", required = true) @PathParam("reg-id") String regId)
	{
		return delegate.processExistingRegulation(serviceTicket, regulation, regId);
	}
}
