package io.swagger.client.model;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * Attribute
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2016-06-27T22:29:17.225Z")
public class Attribute   {
  
  private String attrName = null;
  private String attrValue = null;

  
  /**
   **/
  public Attribute attrName(String attrName) {
    this.attrName = attrName;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("attr_name")
  public String getAttrName() {
    return attrName;
  }
  public void setAttrName(String attrName) {
    this.attrName = attrName;
  }


  /**
   **/
  public Attribute attrValue(String attrValue) {
    this.attrValue = attrValue;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("attr_value")
  public String getAttrValue() {
    return attrValue;
  }
  public void setAttrValue(String attrValue) {
    this.attrValue = attrValue;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Attribute attribute = (Attribute) o;
    return Objects.equals(this.attrName, attribute.attrName) &&
        Objects.equals(this.attrValue, attribute.attrValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(attrName, attrValue);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Attribute {\n");
    
    sb.append("    attrName: ").append(toIndentedString(attrName)).append("\n");
    sb.append("    attrValue: ").append(toIndentedString(attrValue)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

