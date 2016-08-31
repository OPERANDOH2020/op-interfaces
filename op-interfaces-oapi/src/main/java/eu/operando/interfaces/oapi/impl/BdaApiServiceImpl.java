package eu.operando.interfaces.oapi.impl;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import eu.operando.interfaces.oapi.ApiResponseMessage;
import eu.operando.interfaces.oapi.BdaApiService;
import eu.operando.interfaces.oapi.NotFoundException;
import eu.operando.interfaces.oapi.model.WrapperBdaRequestBody;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2016-08-31T09:45:10.086Z")
public class BdaApiServiceImpl extends BdaApiService
{
	@Override
	public Response bdaJobsJobIdReportsGet(WrapperBdaRequestBody wrapper, String jobId)
	{
		// do some magic!
		return Response.ok()
			.entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!"))
			.build();
	}
}
