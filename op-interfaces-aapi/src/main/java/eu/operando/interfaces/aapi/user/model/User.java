/*******************************************************************************
 *  # Copyright (c) 2016 {UPRC}.
 *  # All rights reserved. This program and the accompanying materials
 *  # are made available under the terms of the The MIT License (MIT).
 *  # which accompanies this distribution, and is available at
 *  # http://opensource.org/licenses/MIT
 *
 *  # Contributors:
 *  #    {Constantinos Patsakis} {UPRC}
 *  #    {Stamatis Glykos} {UPRC}
 *  #    {Constantinos Alexandris} {UPRC}
 *  # Initially developed in the context of OPERANDO EU project www.operando.eu 
 *******************************************************************************/
package eu.operando.interfaces.aapi.user.model;

public class User {
	private String username;
	private String password;
	private Attribute[] requiredAttrs;
	private Attribute[] optionalAttrs;
	private PrivacySetting[] privacySettings;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Attribute[] getRequiredAttrs() {
		return requiredAttrs;
	}
	public void setRequiredAttrs(Attribute[] requiredAttrs) {
		this.requiredAttrs = requiredAttrs;
	}
	public Attribute[] getOptionalAttrs() {
		return optionalAttrs;
	}
	public void setOptionalAttrs(Attribute[] optionalAttrs) {
		this.optionalAttrs = optionalAttrs;
	}
	public PrivacySetting[] getPrivacySettings() {
		return privacySettings;
	}
	public void setPrivacySettings(PrivacySetting[] privacySettings) {
		this.privacySettings = privacySettings;
	}
	
	
}
