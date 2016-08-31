package eu.operando.interfaces.oapi.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

/**
 * RequestHeader
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2016-08-31T09:45:10.086Z")
public class WrapperBdaRequestBody
{
	private String requestId = null;

	private String requesterId = null;

	private String requesterComponentId = null;

	private String proxyId = null;

	private Date requestDateTime = null;

	public WrapperBdaRequestBody requestId(String requestId)
	{
		this.requestId = requestId;
		return this;
	}

	/**
	 * Get requestId
	 * 
	 * @return requestId
	 **/
	@ApiModelProperty(required = true, value = "")
	public String getRequestId()
	{
		return requestId;
	}

	public void setRequestId(String requestId)
	{
		this.requestId = requestId;
	}

	public WrapperBdaRequestBody requesterId(String requesterId)
	{
		this.requesterId = requesterId;
		return this;
	}

	/**
	 * Get requesterId
	 * 
	 * @return requesterId
	 **/
	@ApiModelProperty(required = true, value = "")
	public String getRequesterId()
	{
		return requesterId;
	}

	public void setRequesterId(String requesterId)
	{
		this.requesterId = requesterId;
	}

	public WrapperBdaRequestBody requesterComponentId(String requesterComponentId)
	{
		this.requesterComponentId = requesterComponentId;
		return this;
	}

	/**
	 * Get requesterComponentId
	 * 
	 * @return requesterComponentId
	 **/
	@ApiModelProperty(value = "")
	public String getRequesterComponentId()
	{
		return requesterComponentId;
	}

	public void setRequesterComponentId(String requesterComponentId)
	{
		this.requesterComponentId = requesterComponentId;
	}

	public WrapperBdaRequestBody proxyId(String proxyId)
	{
		this.proxyId = proxyId;
		return this;
	}

	/**
	 * Get proxyId
	 * 
	 * @return proxyId
	 **/
	@ApiModelProperty(value = "")
	public String getProxyId()
	{
		return proxyId;
	}

	public void setProxyId(String proxyId)
	{
		this.proxyId = proxyId;
	}

	public WrapperBdaRequestBody requestDateTime(Date requestDateTime)
	{
		this.requestDateTime = requestDateTime;
		return this;
	}

	/**
	 * Get requestDateTime
	 * 
	 * @return requestDateTime
	 **/
	@ApiModelProperty(value = "")
	public Date getRequestDateTime()
	{
		return requestDateTime;
	}

	public void setRequestDateTime(Date requestDateTime)
	{
		this.requestDateTime = requestDateTime;
	}

	@Override
	public boolean equals(java.lang.Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null
			|| getClass() != o.getClass())
		{
			return false;
		}
		WrapperBdaRequestBody requestHeader = (WrapperBdaRequestBody) o;
		return Objects.equals(this.requestId, requestHeader.requestId)
			&& Objects.equals(this.requesterId, requestHeader.requesterId)
			&& Objects.equals(this.requesterComponentId, requestHeader.requesterComponentId)
			&& Objects.equals(this.proxyId, requestHeader.proxyId)
			&& Objects.equals(this.requestDateTime, requestHeader.requestDateTime);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(requestId, requesterId, requesterComponentId, proxyId, requestDateTime);
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("class RequestHeader {\n");

		sb.append("    requestId: ")
			.append(toIndentedString(requestId))
			.append("\n");
		sb.append("    requesterId: ")
			.append(toIndentedString(requesterId))
			.append("\n");
		sb.append("    requesterComponentId: ")
			.append(toIndentedString(requesterComponentId))
			.append("\n");
		sb.append("    proxyId: ")
			.append(toIndentedString(proxyId))
			.append("\n");
		sb.append("    requestDateTime: ")
			.append(toIndentedString(requestDateTime))
			.append("\n");
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Convert the given object to string with each line indented by 4 spaces (except the first line).
	 */
	private String toIndentedString(java.lang.Object o)
	{
		if (o == null)
		{
			return "null";
		}
		return o.toString()
			.replace("\n", "\n    ");
	}
}
