package eu.operando.interfaces.oapi;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2016-07-12T14:06:26.001Z")
public abstract class ReportsService
{
	public abstract Response reportsGetReport(String serviceTicket, String reportId, String format, MultivaluedMap<String, String> optionalParameters);
}
