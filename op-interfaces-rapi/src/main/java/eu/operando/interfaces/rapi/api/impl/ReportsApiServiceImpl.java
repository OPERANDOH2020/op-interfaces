package eu.operando.interfaces.rapi.api.impl;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import eu.operando.interfaces.rapi.api.ApiResponseMessage;
import eu.operando.interfaces.rapi.api.NotFoundException;
import eu.operando.interfaces.rapi.api.ReportsApiService;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2016-07-12T14:06:26.001Z")
public class ReportsApiServiceImpl extends ReportsApiService
{
	@Override
	public Response reportsReportIdGet(String reportId, String format, SecurityContext securityContext) throws NotFoundException
	{
		// do some magic!
		return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
	}
}
