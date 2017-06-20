
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

var address = require("address-rfc2821").Address;
var fs = require('fs');
var edb = new SwarmConnector();
var jwt = require('jsonwebtoken');
var cfg;
var publicKey;
var privateKey;
var plugin;



function readConfig(){
    cfg = plugin.config.get('operando.ini',readConfig);
    publicKey = fs.readFileSync(cfg.main.publicKey);
    privateKey= fs.readFileSync(cfg.main.privateKey);
    plugin.loginfo("Operando configuration: ",cfg);
}

exports.register = function(){
    this.register_hook("rcpt","forward_to_user");
    this.register_hook("data","clean_body");
    this.register_hook("data_post","forward_to_outside_entity");
    this.register_hook("data_post","finish_forward_to_user");
    plugin = this;
    readConfig();
};

exports.forward_to_user = function(next,connection){
    var alias = connection.transaction.rcpt_to[0].user+"@"+connection.transaction.rcpt_to[0].host;
    var sender = connection.transaction.mail_from.original.slice(1,connection.transaction.mail_from.original.length-1)
    connection.relaying = false;
    if(edb.connected() === false){
        next(DENYSOFT)
    }else{
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
                    "to": realEmail,
                    "from": newSender,
                    "replyTo": token+"@plusprivacy.com>"
                });
                connection.relaying = true;
            }
            next()
        })
    }
};

exports.forward_to_outside_entity = function(next,connection){
    var forward_to_user_details = connection.results.get('forward_emails');
    if(forward_to_user_details===undefined) {
        jwt.verify(connection.transaction.rcpt_to[0].user, publicKey, ['RS256'], function (err, conversation) {
            if (err) {
                next(DENIDISCONNECT)
            } else {
                plugin.loginfo('Delivering to outside entity');
                conversation = JSON.parse(new Buffer(conversation, 'base64').toString());
                var to = conversation.sender;
                var from = conversation.alias;
                connection.relaying = true;
                changeFrom(connection,from);
                changeTo(connection,to);
                removeHeaders(connection);
                addReplyTo(connection,from);
                next(OK)
            }
        })
    }else{
        next()
    }
};

exports.clean_body = function (next, connection) {
    var plugin = this;
    var forward_to_user_details = connection.results.get('forward_emails');
    if (forward_to_user_details!==undefined) {
        plugin.loginfo("Filtering the body");
        connection.transaction.add_body_filter('text/html',function(content_type,encoding,body_buffer){
	    var body = body_buffer.toString()
	    var originalFrom = connection.transaction.mail_from.user+"@"+connection.transaction.mail_from.host
            var filteredBody = body.split(originalFrom).join(forward_to_user_details.from);
            return Buffer.from(filteredBody,encoding);
        })
	    connection.transaction.add_body_filter('text/plain',function(content_type,encoding,body_buffer){
            var body = body_buffer.toString()
            var originalFrom = connection.transaction.mail_from.user+"@"+connection.transaction.mail_from.host
            var filteredBody = body.split(originalFrom).join(forward_to_user_details.from);
            return Buffer.from(filteredBody,encoding);
        })
    }
    next();
};

exports.finish_forward_to_user = function (next, connection) {
    var plugin = this;
    var forward_to_user_details = connection.results.get('forward_emails');
    if(forward_to_user_details!==undefined){
        plugin.loginfo("Relay to user");
        changeTo(connection,forward_to_user_details.to);
        changeFrom(connection,forward_to_user_details.from, true);
        removeHeaders(connection);
        addReplyTo(connection,forward_to_user_details.replyTo);
    }
    next();
};

function changeTo(connection,newTo) {
    plugin.loginfo("New to: "+newTo);
    connection.transaction.rcpt_to.pop();
    connection.transaction.header.remove('to');
    if (Array.isArray(newTo)){
        newTo.forEach(function(t){
            connection.transaction.header.add('to', t);
            connection.transaction.rcpt_to.push(new address('<' + t + '>'));
        })
    }else{
        connection.transaction.header.add('to', newTo);
        connection.transaction.rcpt_to.push(new address('<' + newTo + '>'));
    }
}

function changeFrom(connection,newFrom,displayOriginal) {
    var original = connection.transaction.mail_from.user+"@"+connection.transaction.mail_from.host;
    connection.transaction.mail_from.original = '<' + newFrom + '>';
    connection.transaction.mail_from.user = newFrom.split('@')[0];
    connection.transaction.mail_from.host = newFrom.split('@')[1];

    connection.transaction.remove_header('From');
    if(!displayOriginal) {
        plugin.loginfo("New from: "+newFrom);
        connection.transaction.add_header('From', newFrom);
    }else{
        var fromMessage = original+" via plusprivacy.com"+"' <"+newFrom+">";
        plugin.loginfo(fromMessage);
        connection.transaction.add_header('From',fromMessage );
    }
}

function addReplyTo(connection,replyTo) {
    plugin.loginfo("New Reply-To :"+replyTo);
    connection.transaction.header.remove("Reply-To");
    connection.transaction.header.add("Reply-To", "<"+replyTo+">"); //the user will send the reply to this address
}

function removeHeaders(connection){
    connection.transaction.header.remove('Received');
    connection.transaction.header.remove('X-Sender');
    connection.transaction.header.remove('DKIM-Signature');
    connection.transaction.header.remove('DomainKey-Signature');
    connection.transaction.header.remove('Message-ID');
}



