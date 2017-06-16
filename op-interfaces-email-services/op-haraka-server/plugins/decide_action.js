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
var jwt = require('jsonwebtoken');
var fs = require('fs');
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
    publicKey = fs.readFileSync(cfg.main.publicKey);
    privateKey= fs.readFileSync(cfg.main.privateKey);
    plugin.loginfo("Operando configuration: ",cfg);
}

exports.decideAction = function(next,connection){
    var alias = connection.transaction.rcpt_to[0].user+"@"+connection.transaction.rcpt_to[0].host;
    var sender = connection.transaction.mail_from.original.slice(1,connection.transaction.mail_from.original.length-2)
    plugin = this
    connection.relaying = false;

    if(edb.connected() === false){
        next(DENYSOFT);
    }else {
        plugin.loginfo("Check whether "+alias.toLowerCase()+" is an alias");
        edb.getRealEmail(alias.toLowerCase(), function (err, realEmail) {
            if (realEmail) {
                plugin.loginfo("Delivering to user");
                var conversation = Buffer.from(JSON.stringify({
                    "alias": alias,
                    "sender": sender
                })).toString('base64');
                var token = jwt.sign(conversation, privateKey, {algorithm: "RS256"});
                var newSender = sender.split("@").join("_at_") + "_via_plusprivacy@plusprivacy.com";
                connection.results.add(plugin, {
                    "action": "relayToUser",
                    "to": realEmail,
                    "from": newSender,
                    "replyTo": token+"@plusprivacy.com>"
                });
                connection.relaying = true;
                next(OK)
            }
            else{
                jwt.verify(connection.transaction.rcpt_to[0].user, publicKey, ['RS256'], function (err, conversation) {
                    if (err) {
                        next(DENYDISCONNECT)
                    } else {
                        plugin.loginfo("Delivering to outside entity");
                        conversation = JSON.parse(new Buffer(conversation, 'base64').toString())
                        connection.results.add(plugin, {
                            "action": "relayToOutsideEntity",
                            "to": conversation['sender'],
                            "from": conversation['alias']
                        });
                        connection.relaying = true;
                        next(OK)
                    }
                })
            }
        });
    }
};
