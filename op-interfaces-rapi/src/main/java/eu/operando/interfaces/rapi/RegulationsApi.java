package eu.operando.interfaces.rapi;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import eu.operando.api.model.DtoPrivacyRegulation;
import eu.operando.api.model.PrivacyRegulation;
import eu.operando.api.model.PrivacyRegulationInput;
import eu.operando.interfaces.rapi.factories.RegulationsApiServiceFactory;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;

@Path("/regulations")
@RestController
@RequestMapping(value = "/regulations")
@Produces({ MediaType.APPLICATION_JSON })
public class RegulationsApi
{
	private final RegulationsApiService delegate = RegulationsApiServiceFactory.getRegulationsApi();

	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@RequestMapping(value = "", method = RequestMethod.POST)
	@ApiOperation(
		value = "",
		notes = "Called by a regulator to ensure that the platform becomes compliant with the new regulation. The regulation that was saved is returned, along with an identifer which should be used to refer to it in later communication.",
		response = PrivacyRegulation.class)
	@ApiResponses(
		value = {
			@ApiResponse(
				code = 201,
				message = "The regulation was successfully created, and the platform has been made compliant with it."),
			@ApiResponse(
				code = 401,
				message = "The user is not authenticated with the OPERANDO system. Check that the service ticket provided by the authentication service is correctly included in the message body."),
			@ApiResponse(
				code = 403,
				message = "The user is authenticated with the OPERANDO system, but is not allowed to perform the requested action.") })
	@ApiImplicitParams({
		@ApiImplicitParam(name = "service-ticket", value = "Ticket proving that the caller is allowed to use this service", required = true, dataType = "string", paramType = "header"),
		@ApiImplicitParam(name = "regulation", value = "", required = true, dataType = "PrivacyRegulationInput", paramType = "body")
	})
	public Response regulationsPost(
			@ApiIgnore @HeaderParam("service-ticket") String serviceTicket,
			@ApiIgnore PrivacyRegulationInput regulation)
	{
		return delegate.processNewRegulation(serviceTicket, regulation);
	}

	@PUT
	@Path("/{reg-id}")
	@Produces({ MediaType.APPLICATION_JSON })
	@RequestMapping(value = "{reg-id}", method = RequestMethod.PUT)
	@ApiOperation(
		value = "",
		notes = "Called by a regulator to ensure that the platform becomes compliant with the new terms of the regulation. The regulation that was saved is returned, along with an identifer which should be used to refer to it in later communication.",
		response = DtoPrivacyRegulation.class)
	@ApiResponses(
		value = {
			@ApiResponse(
				code = 204,
				message = "The regulation was successfully updated, and the platform has been made compliant with it."),
			@ApiResponse(
				code = 401,
				message = "The user is not authenticated with the OPERANDO system. Check that the service ticket provided by the authentication service is correctly included in the message body."),
			@ApiResponse(
				code = 403,
				message = "The user is authenticated with the OPERANDO system, but is not allowed to perform the requested action.") })
	@ApiImplicitParams({
		@ApiImplicitParam(name = "service-ticket", value = "Ticket proving that the caller is allowed to use this service", dataType = "string", paramType = "header", required = true),
		@ApiImplicitParam(name = "regulation", value = "", required = true, dataType = "PrivacyRegulationInput", paramType = "body"), 
		@ApiImplicitParam(name = "reg-id", value = "The unique identifier of a regulation", required = true, dataType = "string", paramType = "path")  
	})
	public Response regulationsRegIdPut(
			@ApiIgnore @HeaderParam("service-Ticket") String serviceTicket,
			@ApiIgnore PrivacyRegulationInput regulation,
			@ApiIgnore @PathParam("reg-Id") String regId)
	{
		return delegate.processExistingRegulation(serviceTicket, regulation, regId);
	}
}
