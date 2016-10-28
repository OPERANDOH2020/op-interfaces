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
package eu.operando.interfaces.aapi.ticket.controller.exceptions;

public class TicketingException extends RuntimeException {

	private static final long serialVersionUID = 571525856112017970L;
	
	private String errCode;
	private String errMsg;
	private Exception errCause;
	
	public TicketingException(String errCode, String errMsg, Exception errCause) {
		this.errCode = errCode;
		this.errMsg = errMsg;
		this.errCause = errCause;
	}
	
	public String getErrCode() {
		return errCode;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public Exception getErrCause() {
		return errCause;
	}
}
