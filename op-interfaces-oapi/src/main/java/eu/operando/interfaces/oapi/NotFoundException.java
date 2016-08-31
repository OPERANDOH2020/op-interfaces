package eu.operando.interfaces.oapi;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2016-08-31T09:45:10.086Z")
public class NotFoundException extends ApiException
{
	private int code;

	public NotFoundException(int code, String msg)
	{
		super(code, msg);
		this.code = code;
	}
}
