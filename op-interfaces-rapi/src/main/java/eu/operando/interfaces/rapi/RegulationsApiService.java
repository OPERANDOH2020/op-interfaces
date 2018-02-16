package eu.operando.interfaces.rapi;

import eu.operando.api.model.PrivacyRegulationInput;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2016-07-12T14:06:26.001Z")
public interface RegulationsApiService
{
	public boolean processNewRegulation(PrivacyRegulationInput regulation);
	public boolean processExistingRegulation(PrivacyRegulationInput regulation, String regId);
}
