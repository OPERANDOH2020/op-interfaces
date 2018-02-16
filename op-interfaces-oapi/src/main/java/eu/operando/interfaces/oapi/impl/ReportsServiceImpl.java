package eu.operando.interfaces.oapi.impl;

import javax.ws.rs.core.MultivaluedMap;

import eu.operando.OperandoCommunicationException;
import eu.operando.interfaces.oapi.ReportsService;
import eu.operando.moduleclients.ClientReportGenerator;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2016-07-12T14:06:26.001Z")
public class ReportsServiceImpl extends ReportsService
{
	private ClientReportGenerator clientReportGenerator;

	public ReportsServiceImpl(ClientReportGenerator clientReportGenerator)
	{
		this.clientReportGenerator = clientReportGenerator;
	}

	@Override
	public String getReport(String reportId, String format, MultivaluedMap<String, String> parametersOptional) throws OperandoCommunicationException
	{
		return clientReportGenerator.getReport(reportId, format, parametersOptional);
	}
}
