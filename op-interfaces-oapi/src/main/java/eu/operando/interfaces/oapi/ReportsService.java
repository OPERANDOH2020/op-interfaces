package eu.operando.interfaces.oapi;

import javax.ws.rs.core.MultivaluedMap;

import eu.operando.OperandoCommunicationException;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2016-07-12T14:06:26.001Z")
public abstract class ReportsService
{
	public abstract String getReport(String reportId, String format, MultivaluedMap<String, String> optionalParameters) throws OperandoCommunicationException;
}
