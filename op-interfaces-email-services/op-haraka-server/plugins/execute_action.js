
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
var fs = require('fs');
exports.register = function(){
    this.register_hook("data","clean_body");
    this.register_hook("data_post","forward");
};


exports.clean_body = function (next, connection) {
    /*
	Replace anything that refferences the real user.
    */
    var plugin = this;
    var decision = connection.results.get('decide_action');
    if (decision.action === 'relayToOutsideEntity') {
        plugin.loginfo("Filtering the body");
        connection.transaction.add_body_filter('text/html',function(content_type,encoding,body_buffer){
	    var body = body_buffer.toString()
	    var originalFrom = connection.transaction.mail_from.user+"@"+connection.transaction.mail_from.host
            var filteredBody = body.split(originalFrom).join(decision.from);
            return Buffer.from(filteredBody,encoding);
        })
	connection.transaction.add_body_filter('text/plain',function(content_type,encoding,body_buffer){
            var body = body_buffer.toString()
            var originalFrom = connection.transaction.mail_from.user+"@"+connection.transaction.mail_from.host
            var filteredBody = body.split(originalFrom).join(decision.from);
            return Buffer.from(filteredBody,encoding);
        })
    }
    next();
};

exports.forward = function (next, connection) {
    var plugin = this;
    var decision = connection.results.get('decide_action');
    switch (decision.action) {
        case "relayToUser" :
        {
            plugin.loginfo("Relay to user");
            changeTo(decision.to);
            changeFrom(decision.from,true);
            removeHeaders();
            addReplyTo("replies@plusprivacy.com");
            connection.transaction.header.add("X-REPLY-ID",decision.replyId);
            break;
        }
        case "relayToOutsideEntity" :
        {
            plugin.loginfo("Relay to outside entity");
            changeFrom(decision.from);
            addReplyTo(decision.from);
            changeTo(decision.to);
            removeHeaders();
            break;
        }
    }
    next();

    function changeTo(newTo) {
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

    function changeFrom(newFrom,displayOriginal) {
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

    function addReplyTo(replyTo) {
        plugin.loginfo("New Reply-To :"+replyTo);
        connection.transaction.header.remove("Reply-To");
        connection.transaction.header.add("Reply-To", replyTo); //the user will send the reply to this address
    }

    function removeHeaders(){
        connection.transaction.header.remove('Received');
        connection.transaction.header.remove('X-Sender');
        connection.transaction.header.remove('DKIM-Signature');
        connection.transaction.header.remove('DomainKey-Signature');
        connection.transaction.header.remove('Message-ID');
        connection.transaction.header.remove("X-REPLY-ID");
    }
};




