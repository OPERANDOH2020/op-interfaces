/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.operando.interfaces.aapi.utils;

import io.swagger.client.ApiClient;
import io.swagger.client.api.LogApi;
import io.swagger.client.model.LogRequest;
import org.apache.log4j.Logger;


/**
 *
 * @author alexandris
 */
public class LocalLog implements Runnable{
    private static final Logger LOGGER = Logger.getLogger( LocalLog.class.getName() );
    
    String logTitle;
    String logDescription;
    String logPriority;
    String requesterId;
    LogRequest.LogDataTypeEnum logLevel;
    
    LocalLog(LogRequest.LogDataTypeEnum logLevel, String logTitle, String logDescription, String logPriority, String requesterId){
        this.logLevel = logLevel;
        this.logTitle = logTitle;
        this.logDescription = logDescription;
        this.logPriority= logPriority;
        this.requesterId = requesterId;
    }
    
    @Override
    public void run() {
        LOGGER.error("logLevel:"+logLevel+" logTitle:"+logTitle+" logDescription:"+logDescription+" logPriority:"+logPriority+" requesterId:"+requesterId);
        
    }
}



