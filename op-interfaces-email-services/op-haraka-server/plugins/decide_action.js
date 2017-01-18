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
    var gotConnection  = false;
    var adapterPort    = 3000;
    var adapterHost    = "localhost";
    var util           = require("swarmcore");
    var client	       = util.createClient(adapterHost, adapterPort, "BroadcastUser", "ok","BroadcastTest", "testCtor");
    var uuid           = require('node-uuid');
    var self           = this;

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
                callback(undefined,{
                    "receiver":swarm.conversation.receiver,
                    "sender"  :swarm.conversation.sender
                });
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

    client.addListener("close",function(){
        plugin.loginfo("Swarm connection was closed");
        gotConnection = false;
    });

    client.addListener('open',function(){
        gotConnection = true;
        plugin.loginfo("Swarm connection was opened");
    });

    this.connected = function(){
        return gotConnection;
    }
}

var edb = new SwarmConnector();

var cfg;
var plugin;

exports.register = function(){
    plugin = this;
    readConfig();
    this.register_hook("rcpt","decideAction");
};

function readConfig(){
    cfg = plugin.config.get('operando.ini',readConfig);
    plugin.loginfo("Operando configuration: ",cfg);
}



exports.decideAction = function(next,connection){
    var alias = connection.transaction.rcpt_to[0].user+"@"+connection.transaction.rcpt_to[0].host;
    plugin = this;
    var sender = connection.transaction.mail_from.original;

    connection.relaying = false;
    sender = sender.substr(1, sender.length - 2);

    if(edb.connected() === false){
        next(DENYSOFT);
    }
    else {
        edb.getRealEmail(alias.toLowerCase(), function (err, realEmail) {
            if (realEmail) {
                edb.registerConversation(alias, sender, function (err, conversationUUID) {
                    if (!err) {
                        plugin.loginfo("Delivering to user");

                        var newSender = sender;
                        if(newSender.split("@")[1].toLowerCase()!==cfg.main.host){
                            newSender = newSender.replace("@", "_") + "@" + cfg.main.host;
                        }

                        connection.results.add(plugin,
                            {
                                "action": "relayToUser",
                                "to": realEmail,
                                "from":newSender,
                                "replyTo": conversationUUID + "@" + cfg.main.host
                            }
                        );
                        connection.relaying = true;
                        next(OK)
                    } else {
                        plugin.loginfo("Could not register conversation between ", sender, " and ", alias, "\nError:", err);
                        next(DENYSOFT);
                    }
                })
            }
            else {
                plugin.loginfo("Try to get conversation ", connection.transaction.rcpt_to[0].user);
                edb.getConversation(connection.transaction.rcpt_to[0].user.toLowerCase(), function (err, conversation) {
                    if (conversation) {
                        plugin.loginfo("Delivering to outside entity");
                        plugin.loginfo("Current conversation:" + connection.transaction.rcpt_to[0].user);

                        connection.results.add(plugin,
                            {
                                "action": "relayToOutsideEntity",
                                "to": conversation['receiver'],
                                "from": conversation['sender']
                            }
                        );
                        /*
                        edb.removeConversation(connection.transaction.rcpt_to[0].user, function (err, result) {
                            if (err) {
                                self.loginfo("Failed to remove conversation:" + connection.transaction.rcpt_to[0].user + " from conversations database");
                            }
                        });*/
                        connection.relaying = true;
                        next(OK)
                    } else {
                        plugin.loginfo("Dropping email");
                        next(DENYDISCONNECT);
                    }
                });
            }
        });
    }
};
