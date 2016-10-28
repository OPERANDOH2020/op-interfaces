/*
 * Copyright (c) 2016 ROMSOFT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the The MIT License (MIT).
 * which accompanies this distribution, and is available at
 * http://opensource.org/licenses/MIT
 *
 * Contributors:
 *    Ciprian Tălmăcel (ROMSOFT)
 * Initially developed in the context of OPERANDO EU project
 www.operando.eu
 */


function SwarmConnector(){
    var adapterPort  = 3000;
    var adapterHost  = "localhost";
    var util         = require("swarmcore");
    var client	  = util.createClient(adapterHost, adapterPort, "BroadcastUser", "ok","BroadcastTest", "testCtor");
    var uuid         = require('node-uuid');

    this.registerConversation=function(sender,receiver,callback){
        var swarmHandler = client.startSwarm("emails.js","registerConversation",sender,receiver);
        swarmHandler.onResponse(function(swarm){
            if(swarm.error){
                callback(swarm.error);
            }else{
                callback(undefined,swarm.conversationUUID);
            }
        });
    };

    this.getConversation=function(conversationUUID,callback){
        var swarmHandler = client.startSwarm("emails.js","getConversation",conversationUUID);
        swarmHandler.onResponse(function(swarm){
            if(swarm.error){
                callback(swarm.error);
            }else{
                callback(undefined,swarm.conversation);
            }
        });
    };

    this.removeConversation=function(conversationUUID,callback){
        var swarmHandler = client.startSwarm("emails.js","removeConversation",conversationUUID);
        swarmHandler.onResponse(function(swarm){
            if(swarm.error){
                callback(swarm.error);
            }else{
                callback(undefined,swarm.result);
            }
        });
    };

    this.getRealEmail=function(userAlias,callback){
        var swarmHandler = client.startSwarm("identity.js","getRealEmail",userAlias);
	       
	swarmHandler.onResponse(function(swarm){
	    if(swarm.realEmail){
                callback(undefined,swarm.realEmail);
            }else{
                callback(swarm.error);
            }
        });
    };	
}

var edb = new SwarmConnector();

exports.register = function(){
    plugin = this;
    this.register_hook("rcpt","decideAction");
};
var plugin;


exports.decideAction = function(next,connection){
    var alias = connection.transaction.rcpt_to[0].user+"@"+connection.transaction.rcpt_to[0].host;
    plugin = this;
    var sender = connection.transaction.mail_from.original;

    connection.relaying = false;
    sender = sender.substr(1, sender.length - 2);
    if(sender==="operando@privatesky.xyz"){
        connection.results.add(plugin,
            {
                "action": "noAction"
            }
        );
        connection.relaying = true;
        next(OK);
        return;
    }

    edb.getRealEmail(alias,function(err,realEmail){
        if(realEmail) {
            edb.registerConversation(sender, alias, function (err, conversationUUID) {
                if(!err){
                    plugin.loginfo("Delivering to user");
                    connection.results.add(plugin,
                        {
                            "action": "relayToUser",
                            "to": realEmail,
                            "replyTo":conversationUUID+"@privatesky.xyz"
                        }
                    );
                    connection.relaying = true;
                    next(OK)
                }else{
		    plugin.loginfo("Could not register conversation between ",sender," and ",alias,"\nError:",err)
                    next(DENY)
                }
            })
        }
        else{
	    plugin.loginfo("Try to get conversation ", connection.transaction.rcpt_to[0].user);
            edb.getConversation(connection.transaction.rcpt_to[0].user,function(err,conversation){
                if(conversation) {
                    plugin.loginfo("Delivering to outside entity");
                    plugin.loginfo("Current conversation:"+connection.transaction.rcpt_to[0].user);
                 
                    connection.results.add(plugin,
                        {
                            "action":"relayToOutsideEntity",
                            "to":conversation['sender'],
                            "from":conversation['receiver']
                        }
                    );
                    edb.removeConversation(connection.transaction.rcpt_to[0].user,function(err,result){
                        if(err){
                            self.loginfo("Failed to remove conversation:"+connection.transaction.rcpt_to[0].user+" from conversations database");
                        }
                    });
                    connection.relaying = true;
                    next(OK)
                }else{
                    plugin.loginfo("Dropping email");
                    next(DENY);
                }
            });
        }
    });
};
