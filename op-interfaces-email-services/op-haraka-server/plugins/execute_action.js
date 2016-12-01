
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
    this.register_hook("queue_outbound","forward");
    this.register_hook("send_email","deferr_message");
};

exports.deferr_message = function(next,hmailItem){
    var plugin = this;
    plugin.loginfo("Send email hook arguments:\n\n",arguments);
    if(hmailItem.notes.deferr===true) {
        hmailItem.temp_fail(new Error("Operando backend if offline"));
    }else{
        next(OK);
    }
}

exports.forward = function (next, connection) {
    var plugin = this;
    var decision = connection.results.get('decide_action');

    switch (decision.action) {
        case "relayToUser" :
        {
            plugin.loginfo("Relay to user");
            changeTo(decision.to);
            changeFrom(decision.from);
            removeHeaders();
            addReplyTo(decision.replyTo);
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
        connection.transaction.rcpt_to.push(new address('<' + newTo + '>'));
        connection.transaction.header.remove('to');
        connection.transaction.header.add('to',newTo);
    }

    function changeFrom(newFrom) {
        plugin.loginfo("New from: "+newFrom);
        connection.transaction.mail_from.original = '<' + newFrom + '>';
        connection.transaction.mail_from.user = newFrom.split('@')[0];
        connection.transaction.mail_from.host = newFrom.split('@')[1];
        connection.transaction.header.remove('from');
        connection.transaction.header.add('from',newFrom);
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
    }
};




