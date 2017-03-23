/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.operando.interfaces.aapi.utils;

import io.swagger.client.model.LogRequest;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author alexandris
 */
public class Log {
    
    public void logMe(LogRequest.LogLevelEnum logLevel, String logTitle, String logDescription, String logPriority, String requesterId){
        
        ExecutorService executor = Executors.newFixedThreadPool(1);
	   
        // execute the Runnable
        
        try {
            Runnable remoteLog = new RemoteLog(logLevel, logTitle, logDescription, logPriority, requesterId);
            Runnable localLog = new LocalLog(logLevel, logTitle, logDescription, logPriority, requesterId);
            executor.execute(remoteLog);
            executor.execute(localLog);
            executor.shutdown();
            executor.awaitTermination(2, TimeUnit.SECONDS);
        } catch (Exception e) {
        }
        
        
            
    }
}
