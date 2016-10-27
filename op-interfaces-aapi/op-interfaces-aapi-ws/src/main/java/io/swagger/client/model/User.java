package io.swagger.client.model;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * User
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2016-06-27T22:29:17.225Z")
public class User   {
  
  private String username = null;
  private String password = null;
  private Object requiredAttrs = null;
  private Object optionalAttrs = null;
  private Object privacySettings = null;

  
  /**
   **/
  public User username(String username) {
    this.username = username;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("username")
  public String getUsername() {
    return username;
  }
  public void setUsername(String username) {
    this.username = username;
  }


  /**
   **/
  public User password(String password) {
    this.password = password;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("password")
  public String getPassword() {
    return password;
  }
  public void setPassword(String password) {
    this.password = password;
  }


  /**
   **/
  public User requiredAttrs(Object requiredAttrs) {
    this.requiredAttrs = requiredAttrs;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("required_attrs")
  public Object getRequiredAttrs() {
    return requiredAttrs;
  }
  public void setRequiredAttrs(Object requiredAttrs) {
    this.requiredAttrs = requiredAttrs;
  }


  /**
   **/
  public User optionalAttrs(Object optionalAttrs) {
    this.optionalAttrs = optionalAttrs;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("optional_attrs")
  public Object getOptionalAttrs() {
    return optionalAttrs;
  }
  public void setOptionalAttrs(Object optionalAttrs) {
    this.optionalAttrs = optionalAttrs;
  }


  /**
   **/
  public User privacySettings(Object privacySettings) {
    this.privacySettings = privacySettings;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("privacy_settings")
  public Object getPrivacySettings() {
    return privacySettings;
  }
  public void setPrivacySettings(Object privacySettings) {
    this.privacySettings = privacySettings;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User) o;
    return Objects.equals(this.username, user.username) &&
        Objects.equals(this.password, user.password) &&
        Objects.equals(this.requiredAttrs, user.requiredAttrs) &&
        Objects.equals(this.optionalAttrs, user.optionalAttrs) &&
        Objects.equals(this.privacySettings, user.privacySettings);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username, password, requiredAttrs, optionalAttrs, privacySettings);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class User {\n");
    
    sb.append("    username: ").append(toIndentedString(username)).append("\n");
    sb.append("    password: ").append(toIndentedString(password)).append("\n");
    sb.append("    requiredAttrs: ").append(toIndentedString(requiredAttrs)).append("\n");
    sb.append("    optionalAttrs: ").append(toIndentedString(optionalAttrs)).append("\n");
    sb.append("    privacySettings: ").append(toIndentedString(privacySettings)).append("\n");
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

