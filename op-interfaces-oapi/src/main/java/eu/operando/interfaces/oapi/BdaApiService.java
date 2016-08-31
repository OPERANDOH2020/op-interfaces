package eu.operando.interfaces.oapi;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import eu.operando.interfaces.oapi.model.WrapperBdaRequestBody;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2016-08-31T09:45:10.086Z")
public abstract class BdaApiService
{
	public abstract Response bdaJobsJobIdReportsGet(WrapperBdaRequestBody wrapper, String jobId);
}
