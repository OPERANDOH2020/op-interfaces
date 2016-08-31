package eu.operando.interfaces.rapi.factories;

import eu.operando.interfaces.rapi.RegulationsApiService;
import eu.operando.interfaces.rapi.impl.RegulationsApiServiceImpl;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2016-07-12T14:06:26.001Z")
public class RegulationsApiServiceFactory
{
	private final static RegulationsApiService service = new RegulationsApiServiceImpl();

	public static RegulationsApiService getRegulationsApi()
	{
		return service;
	}
}
