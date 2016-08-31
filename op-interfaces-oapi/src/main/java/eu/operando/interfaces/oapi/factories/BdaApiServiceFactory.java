package eu.operando.interfaces.oapi.factories;

import eu.operando.interfaces.oapi.BdaApiService;
import eu.operando.interfaces.oapi.impl.BdaApiServiceImpl;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2016-08-31T09:45:10.086Z")
public class BdaApiServiceFactory
{
	private final static BdaApiService service = new BdaApiServiceImpl();

	public static BdaApiService getBdaApi()
	{
		return service;
	}
}
