package eu.operando.interfaces.rapi.model;

import java.util.Objects;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

import eu.operando.api.model.PrivacyRegulationInput;
import io.swagger.annotations.ApiModelProperty;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2016-07-12T14:06:26.001Z")
@XmlRootElement
public class RegulationBody
{
	private String serviceTicket = null;
	private PrivacyRegulationInput regulation = null;

	/**
	 **/
	public RegulationBody serviceTicket(String serviceTicket)
	{
		this.serviceTicket = serviceTicket;
		return this;
	}

	@ApiModelProperty(value = "")
	@JsonProperty("service_ticket")
	public String getServiceTicket()
	{
		return serviceTicket;
	}

	public void setServiceTicket(String serviceTicket)
	{
		this.serviceTicket = serviceTicket;
	}

	/**
	 **/
	public RegulationBody regulation(PrivacyRegulationInput regulation)
	{
		this.regulation = regulation;
		return this;
	}

	@ApiModelProperty(value = "")
	@JsonProperty("regulation")
	public PrivacyRegulationInput getRegulation()
	{
		return regulation;
	}

	public void setRegulation(PrivacyRegulationInput regulation)
	{
		this.regulation = regulation;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}
		RegulationBody regulationBody = (RegulationBody) o;
		return Objects.equals(serviceTicket, regulationBody.serviceTicket) && Objects.equals(regulation, regulationBody.regulation);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(serviceTicket, regulation);
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("class RegulationBody {\n");

		sb.append("    serviceTicket: ").append(toIndentedString(serviceTicket)).append("\n");
		sb.append("    regulation: ").append(toIndentedString(regulation)).append("\n");
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Convert the given object to string with each line indented by 4 spaces
	 * (except the first line).
	 */
	private String toIndentedString(Object o)
	{
		if (o == null)
		{
			return "null";
		}
		return o.toString().replace("\n", "\n    ");
	}
}
