/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.operando.interfaces.aapi.utils;

import io.swagger.client.ApiClient;
import io.swagger.client.api.LogApi;
import io.swagger.client.model.LogRequest;


/**
 *
 * @author alexandris
 */
public class RemoteLog implements Runnable{
    private static final String LOG_ENDPOINT = "http://server02tecnalia.westeurope.cloudapp.azure.com:8090";
    
    String logTitle;
    String logDescription;
    String logPriority;
    String requesterId;
    LogRequest.LogDataTypeEnum logLevel;
    
    RemoteLog(LogRequest.LogDataTypeEnum logLevel, String logTitle, String logDescription, String logPriority, String requesterId){
        this.logLevel = logLevel;
        this.logTitle = logTitle;
        this.logDescription = logDescription;
        this.logPriority= logPriority;
        this.requesterId = requesterId;
    }
    
    @Override
    public void run() {
        ApiClient logClient =  new ApiClient();        
        logClient.setBasePath(LOG_ENDPOINT);
        LogApi logapi = new LogApi();
        logapi.setApiClient(logClient);  
        try {
            logapi.lodDB(new LogRequest().logPriority(LogRequest.LogPriorityEnum.LOW).description(this.logDescription).requesterId(this.requesterId)
             .title(this.logTitle).requesterType(LogRequest.RequesterTypeEnum.MODULE).logDataType(logLevel));
        } catch (Exception e) {
            System.out.println("error while writing at logdb");
        }
        
        
        
    }
}



