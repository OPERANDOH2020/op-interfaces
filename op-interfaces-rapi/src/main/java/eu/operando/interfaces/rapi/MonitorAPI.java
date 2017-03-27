package eu.operando.interfaces.rapi;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

/**
 * Due to changes in the work-flow for handling requests to access data, the Gatekeeper is now essentially a placeholder module (which will manage
 * additional up-front validation of requests beyond checking authentication), which currently functions as a proxy to the Data Access Node.
 * 
 */
@Path("monitor")
public class MonitorAPI
{
	@GET
	public Response processGetRequest(@Context HttpHeaders headers)
	{
		System.out.print("monitor"); //GBE
		
        return Response.ok().entity("server ok").build();
	}
	
}
