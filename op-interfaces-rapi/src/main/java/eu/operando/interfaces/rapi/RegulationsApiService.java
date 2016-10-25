package eu.operando.interfaces.rapi;

import javax.ws.rs.core.Response;

import eu.operando.api.model.PrivacyRegulationInput;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2016-07-12T14:06:26.001Z")
public interface RegulationsApiService
{
	public Response processNewRegulation(String serviceTicket, PrivacyRegulationInput regulation);
	public Response processExistingRegulation(String serviceTicket, PrivacyRegulationInput regulation, String regId);
}
