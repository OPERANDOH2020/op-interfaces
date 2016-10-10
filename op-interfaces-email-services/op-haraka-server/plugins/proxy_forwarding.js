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

var address = require("address-rfc2821").Address;
var util = require('util');
var outbound = require("./outbound");



function SwarmConnector(){
    var adapterPort  = 3000;
    var adapterHost  = "localhost";
    var util         = require("swarmcore");
    var client       = util.createClient(adapterHost, adapterPort, "BroadcastUser", "ok","BroadcastTest", "testCtor");
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
        var realEmail = userAlias.split("@");
        realEmail = "tac@rms.ro";//realEmail[0]+"_realEmail@"+realEmail[1];

        setTimeout(function(){
            callback(undefined,realEmail);
        },500);
        /*
        var swarmHandler = client.startSwarm("identity.js","getRealEmail",userAlias);
        swarmHandler.onResponse(function(swarm){
            if(swarm.error){
                callback(swarm.error);
            }else{
                callback(undefined,swarm.realEmail);
            }
        });
        */
    };
}

var edb = new SwarmConnector();


exports.register = function(){
    this.inherits("queue/discard");
    this.register_hook("rcpt","forwards");
}

exports.forwards = function (next, connection) {
    var alias = connection.transaction.rcpt_to[0].user+"@"+connection.transaction.rcpt_to[0].host;
    var self = this;

    edb.getRealEmail(alias,function(err,realEmail){
        if(!err){
            deliverToUser(realEmail)
        }else{
            var conversationUUID = alias.split("@")[0];
            edb.getConversation(conversationUUID,function(err,conversation){
                if(!err) {
                        deliverToOutsideEntity(conversation.sender, conversation.receiver);
                        //edb.removeConversation(conversationUUID)
                }else{
                    connection.relay = false;
                    self.loginfo("Deliver internally");
                    next(DISCONNECT);
                }
            });
        }
    });

    function deliverToUser(user){
        self.loginfo("Delivering to user: "+user);
        var sender = connection.transaction.mail_from.original; //e.g. facebook,twitter
        edb.registerConversation(sender,alias,function(err,conversationUUID){
            var aliasOfSender = conversationUUID+"@operando.com";
            self.loginfo("\n\n\n\n",aliasOfSender,"\n\n\n\n");
            connection.transaction.header.add("Reply-To", aliasOfSender); //the user will send the reply to this address
            connection.transaction.rcpt_to.pop();
            connection.transaction.rcpt_to.push(new address('<'+user+'>'));
            send();
        })
    }

    function deliverToOutsideEntity(outsideEntity,userProxy){
        self.loginfo("Delivering to outside entity:"+outsideEntity+" through alias: "+userProxy)
        connection.transaction.rcpt_to.pop();
        connection.transaction.rcpt_to.push(new address(outsideEntity));
        
        self.loginfo("\n\n\n",util.inspect(connection.transaction),"\n\n\n");

        connection.transaction.mail_from.original = '<'+userProxy+'>';
        connection.transaction.mail_from.user = userProxy.split('@')[0];
        connection.transaction.mail_from.host = userProxy.split('@')[1];

        send();
    }


    function send(){
        connection.relaying = true;
        next();
        //outbound.send_email(connection.transaction, next);
    }
};

