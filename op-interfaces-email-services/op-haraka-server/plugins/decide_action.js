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


/*
 function SwarmConnector(){

 //THIS IS A MOCKUP FOR TESTS

 var uuid         = require('node-uuid');
 var activeConversations = {};
 var aliases = {
 "gmail@privatesky.xyz":"ciprian.talmacel15@gmail.com",
 "yahoo@privatesky.xyz":"cprn_talmacel@yahoo.com",
 "rms@privatesky.xyz":"tac@rms.ro"
 }

 this.registerConversation=function(sender,receiver,callback){
 setTimeout(function(){
 try{
 var id = uuid.v1();
 activeConversations[id] = {
 "sender":sender,
 "receiver":receiver
 }
 callback(undefined,id);
 }catch(e){
 callback(e);
 }
 },500);
 };

 this.getConversation=function(conversationUUID,callback){
 setTimeout(function(){
 if(activeConversations[conversationUUID]){
 callback(undefined,activeConversations[conversationUUID]);
 }
 else{
 callback(new Error("Conversation: "+conversationUUID+" is unknown"));
 }
 },500);
 };

 this.removeConversation=function(conversationUUID,callback){
 setTimeout(function(){
 try{
 delete activeConversations[conversationUUID];
 callback();
 }catch(e){
 callback(e);
 }
 },500);
 };
 this.getRealEmail=function(userAlias,callback){
 setTimeout(function(){
 if(aliases[userAlias]){
 callback(undefined,aliases[userAlias]);
 }
 else{
 callback(new Error("Alias: "+userAlias+" is unknown"));
 }
 },500);
 };

 this.getConversations = function(){
 return activeConversations;
 }
 }
 */


function SwarmConnector() {
    var adapterPort = 3000;
    var adapterHost = "localhost";
    var util = require("swarmcore");
    var client = util.createClient(adapterHost, adapterPort, "BroadcastUser", "ok", "BroadcastTest", "testCtor");
    var uuid = require('node-uuid');

    this.registerConversation = function (sender, receiver, callback) {
        var swarmHandler = client.startSwarm("emails.js", "registerConversation", sender, receiver);
        swarmHandler.onResponse(function (swarm) {
            if (swarm.error) {
                callback(swarm.error);
            } else {
                callback(undefined, swarm.conversationUUID);
            }

        });
    };

    this.getConversation = function (conversationUUID, callback) {
        var swarmHandler = client.startSwarm("emails.js", "getConversation", conversationUUID);
        swarmHandler.onResponse(function (swarm) {
            if (swarm.error) {
                callback(swarm.error);
            } else {
                callback(undefined, swarm.conversation);
            }
        });
    };

    this.removeConversation = function (conversationUUID, callback) {
        var swarmHandler = client.startSwarm("emails.js", "removeConversation", conversationUUID);
        swarmHandler.onResponse(function (swarm) {
            if (swarm.error) {
                callback(swarm.error);
            } else {
                callback(undefined, swarm.result);
            }
        });
    };

    this.getRealEmail = function (userAlias, callback) {
        var swarmHandler = client.startSwarm("identity.js", "getRealEmail", userAlias);
        swarmHandler.onResponse(function (swarm) {
            if (swarm.error) {
                callback(swarm.error);
            } else {
                callback(undefined, swarm.result);
            }
        });
    };

    this.getRealEmail = function (userAlias, callback) {
        var swarmHandler = client.startSwarm("identity.js", "getRealEmail", userAlias);
        swarmHandler.onResponse(function (swarm) {
            if (swarm.error) {
                callback(swarm.error);
            } else {
                callback(undefined, swarm.realEmail);
            }
        });
    };
}


var edb = new SwarmConnector();

exports.register = function () {
    this.register_hook("rcpt", "decideAction");
};

exports.decideAction = function (next, connection) {
    var alias = connection.transaction.rcpt_to[0].user + "@" + connection.transaction.rcpt_to[0].host;
    var plugin = this;
    edb.getRealEmail(alias, function (err, realEmail) {
        if (!err) {
            var sender = connection.transaction.mail_from.original;
            edb.registerConversation(sender.substr(1, sender.length - 2), alias, function (err, conversationUUID) {
                if (!err) {
                    plugin.loginfo("Delivering to user");
                    connection.results.add(plugin,
                        {
                            "action": "relayToUser",
                            "to": realEmail,
                            "replyTo": conversationUUID + "@privatesky.xyz"
                        }
                    );
                    next(OK)
                } else {
                    next(DENY)
                }
            })
        }
        else {
            edb.getConversation(connection.transaction.rcpt_to[0].user, function (err, conversation) {
                if (!err) {
                    plugin.loginfo("Delivering to outside entity");
                    plugin.loginfo("Current conversation:" + connection.transaction.rcpt_to[0].user);
                    plugin.loginfo("Active conversations:\n", JSON.stringify(edb.getConversations(), null, 4));
                    connection.results.add(plugin,
                        {
                            "action": "relayToOutsideEntity",
                            "to": conversation['sender'],
                            "from": conversation['receiver']
                        }
                    );
                    edb.removeConversation(connection.transaction.rcpt_to[0].user, function (err, result) {
                        if (err) {
                            self.loginfo("Failed to remove conversation:" + connection.transaction.rcpt_to[0].user + " from conversations database");
                        }
                    });
                    next(OK)
                } else {
                    plugin.loginfo("Dropping email");
                    next(DENY);
                }
            });
        }
    });
};




