package eu.operando.interfaces.rapi.api;

@SuppressWarnings("serial")
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2016-07-12T14:06:26.001Z")
public class ApiException extends Exception
{
	private int code;

	public ApiException(int code, String msg)
	{
		super(msg);
		this.code = code;
	}
}
