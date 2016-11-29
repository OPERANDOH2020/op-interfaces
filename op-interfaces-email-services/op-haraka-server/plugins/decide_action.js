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
    var adapterPort    = 3000;
    var adapterHost    = "localhost";
    var util           = require("swarmcore");
    var client	       = util.createClient(adapterHost, adapterPort, "BroadcastUser", "ok","BroadcastTest", "testCtor");
    var uuid           = require('node-uuid');
    var self           = this;
    var gotConnection  = false;
    var outbound       = require("./outbound");

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
        self.gotConnection = false;
    });

    client.addListener('open',function(){
        self.gotConnection = true;
        plugin.loginfo("Swarm connection was opened");
        outbound.load_queue();
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

var path = require('path');
var uniq = 0;
var MAX_UNIQ = 100000;
var my_hostname = require('os').hostname().replace(/\\/, '\\057').replace(/:/, '\\072');
var platformDOT = ((['win32','win64'].indexOf( os.platform() ) !== -1) ? '' : '__tmp__') + '.';

function generateQueueLocation(){
    /*
        The filename of the stored email must match the pattern in "./outbound.js" to be able to be queued
     */
    var queue_path =process.env.HARAKA+"/queue/";
    var fname = new Date().getTime()+'_0_' + process.pid + "_" + _next_uniq() + '.' + my_hostname;

    return path.join(queue_path,platformDOT+fname);

    function _next_uniq(){
        if (uniq >= MAX_UNIQ) {
            uniq = 0;
        }
        return ++uniq;
    }
}


exports.decideAction = function(next,connection){
    var alias = connection.transaction.rcpt_to[0].user+"@"+connection.transaction.rcpt_to[0].host;
    plugin = this;
    var sender = connection.transaction.mail_from.original;

    connection.relaying = false;
    sender = sender.substr(1, sender.length - 2);

    if(!edb.connected()){
        connection.results.add(plugin,
            {
                "action": "storeEmail",
                "location":generateQueueLocation()
            }
        );
        connection.relaying = false;
        next(OK)
    }
    else {
        edb.getRealEmail(alias, function (err, realEmail) {
            if (realEmail) {
                edb.registerConversation(alias, sender, function (err, conversationUUID) {
                    if (!err) {
                        plugin.loginfo("Delivering to user");
                        connection.results.add(plugin,
                            {
                                "action": "relayToUser",
                                "to": realEmail,
                                "from": sender.replace("@", "_") + "@" + cfg.main.host,
                                "replyTo": conversationUUID + "@" + cfg.main.host
                            }
                        );
                        connection.relaying = true;
                        next(OK)
                    } else {
                        plugin.loginfo("Could not register conversation between ", sender, " and ", alias, "\nError:", err);
                        next(DENY)
                    }
                })
            }
            else {
                plugin.loginfo("Try to get conversation ", connection.transaction.rcpt_to[0].user);
                edb.getConversation(connection.transaction.rcpt_to[0].user, function (err, conversation) {
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
                        edb.removeConversation(connection.transaction.rcpt_to[0].user, function (err, result) {
                            if (err) {
                                self.loginfo("Failed to remove conversation:" + connection.transaction.rcpt_to[0].user + " from conversations database");
                            }
                        });
                        connection.relaying = true;
                        next(OK)
                    } else {
                        plugin.loginfo("Dropping email");
                        next(DENY);
                    }
                });
            }
        });
    }
};
