package eu.operando.interfaces.oapi;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2016-08-31T09:45:10.086Z")
public class ApiException extends Exception
{
	private int code;

	public ApiException(int code, String msg)
	{
		super(msg);
		this.code = code;
	}
}
