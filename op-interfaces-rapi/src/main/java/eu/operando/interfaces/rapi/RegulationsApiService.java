package eu.operando.interfaces.rapi;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import eu.operando.interfaces.rapi.model.RegulationBody;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2016-07-12T14:06:26.001Z")
public abstract class RegulationsApiService
{
	public abstract Response regulationsPost(RegulationBody regulationBody);

	public abstract Response regulationsRegIdPut(RegulationBody regulationBody, String regId);
}
