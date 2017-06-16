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
    var client	       = util.createClient(adapterHost, adapterPort, "emailServer", "haraka","BroadcastTest", "emailLoginCtor");
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
var jwt = require('jsonwebtoken')
var fs = require('fs')
var cfg;
var publicKey;
var privateKey;
var plugin;

exports.register = function(){
    plugin = this;
    readConfig();
    this.register_hook("rcpt","decideAction");
};

function readConfig(){
    cfg = plugin.config.get('operando.ini',readConfig);
    publicKey = fs.readFileSync(cfg.publicKey);
    privateKey= fs.readFileSync(cfg.privateKey);
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
        plugin.loginfo("Check whether "+alias.toLowerCase()+" is an alias");
        edb.getRealEmail(alias.toLowerCase(), function (err, realEmail) {
            if (realEmail) {
                plugin.loginfo("Delivering to user");
                var conversation = Buffer.from(JSON.stringify({
                    "alias":alias,
                    "sender":sender
                })).toString('base64');
                var token = jwt.sign(conversation,privateKey,{algorithm:"RS256"});
                var newSender = sender.split("@").join("_at_")+"_via_plusprivacy@plusprivacy.com";
                connection.results.add(plugin, {
                        "action": "relayToUser",
                        "to": realEmail,
                        "from":newSender,
                        "replyId": token
                    });
                connection.relaying = true;
                next(OK)
            }
            else {
                jwt.verify(connection.transaction.header.get("X-REPLY-ID"),publicKey,['RS256'],function(err,conversation){
                    if(err){
                        next(DENYDISCONNECT)
                    }else{
                        plugin.loginfo("Delivering to outside entity");
                        conversation = JSON.parse(new Buffer(conversation,'base64').toString())
                        connection.results.add(plugin, {
                            "action": "relayToOutsideEntity",
                            "to": conversation['alias'],
                            "from": conversation['sender']
                        });
                        connection.relaying = true;
                        next(OK)
                    }
                })
            }
        });
    }
};
