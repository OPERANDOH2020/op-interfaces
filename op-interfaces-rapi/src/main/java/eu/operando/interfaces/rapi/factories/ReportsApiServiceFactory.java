package eu.operando.interfaces.rapi.factories;

import eu.operando.interfaces.rapi.ReportsApiService;
import eu.operando.interfaces.rapi.impl.ReportsApiServiceImpl;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2016-07-12T14:06:26.001Z")
public class ReportsApiServiceFactory
{

	private final static ReportsApiService service = new ReportsApiServiceImpl();

	public static ReportsApiService getReportsApi()
	{
		return service;
	}
}
