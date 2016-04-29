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


var outbound = require("./outbound");

var fs = require('fs');
var util = require('util');
var address = require("address-rfc2821").Address;

var aliasToUser;

loadAliases();
function loadAliases(){
    fs.readFile("/home/ciprian/Workspace/op-email-services/op-haraka-server/plugins/aliases.json",function(err,result){
        if(err){
            throw err;
        }
        try {
            var userToAliases = JSON.parse(result.toString());
            aliasToUser = {};
            for(var user in userToAliases){
                userToAliases[user].forEach(function(alias){
                    aliasToUser[alias] = user;
                })
            }
        }
        catch(err){
            throw new Error("Aliases file must be in JSON format\n" +
                "The format should be " +
                "{user1:[alias11,alias12,..., alias1N]," +
                "user2:[alias21,alias22,..., alias2N]}");
        }
    })
}


exports.register = function(){
    var plugin = this;
    //plugin.register_hook('rcpt','addReplyToField');
    //plugin.register_hook('rcpt','forwardToRealUser');
    plugin.register_hook('rcpt','deliverToIntendedDestination');

}

exports.deliverToIntendedDestination = function (next, connection, params) {
    //this.loginfo("\n\n\ndeliverToIntendedDestination\n\n\n\n",util.inspect(connection.transaction));

    try{
        var maybeIsAliasOfDestination =connection.transaction.rcpt_to[0].user;
        maybeIsAliasOfDestination = new Buffer(maybeIsAliasOfDestination,'base64');
        maybeIsAliasOfDestination = JSON.parse(maybeIsAliasOfDestination);
        this.loginfo("\n\n\n\nMaybe is an alias\n\n",maybeIsAliasOfDestination);
        if(maybeIsAliasOfDestination['isAnAlias'] === true){
            connection.transaction.rcpt_to.pop();
            connection.transaction.rcpt_to.push(new address(maybeIsAliasOfDestination['sender']));


            var userAlias = maybeIsAliasOfDestination['userAlias'];
            connection.transaction.mail_from.original = '<'+userAlias+'>';
            connection.transaction.mail_from.user = userAlias.split('@')[0];
            connection.transaction.mail_from.host = userAlias.split('@')[1];

            if(maybeIsAliasOfDestination['sender'].split("@")[1]!=="operando.com"){
                connection.transaction.results.conn.relaying = true;
            }


            //FOR TESTS SWITCH RECIPIENT HOST TO operando.com because we will not route them just yet
            if(maybeIsAliasOfDestination['sender'].split("@")[1]!=="operando.com"){
                var newRecipient= new address("<"+maybeIsAliasOfDestination['sender'].split("@")[1].slice(0,-1)+"@operando.com>");
                connection.transaction.results.conn.relaying = false;
                connection.transaction.rcpt_to.pop();
                connection.transaction.rcpt_to.push(new address(newRecipient));
            }

        }
        this.loginfo("\n\n\ndeliveredToIntendedDestination\n\n\n\n",util.inspect(connection));

        if(connection.transaction.results.conn.relaying === true){
            outbound.send_email(connection.transaction, next);
        }
        next();
    }
    catch(e){
        this.loginfo(util.inspect(e));
        //the JSON parse thing threw an error...
        next();
    }
}

exports.addReplyToField = function (next, connection, params) {
    this.loginfo("\n\n\n\naddReplyToField\n\n\n",util.inspect(connection.transaction));
    var recipientAlias = params[0].user+"@"+params[0].host;
    if(!aliasToUser[recipientAlias]){
        next();
    }
    var aliasOfSender = new Buffer(JSON.stringify({
            'userAlias':recipientAlias,
            'sender':connection.transaction.mail_from.original,
            'isAnAlias':true
        })).toString('base64')+"@operando.com";
    connection.transaction.header.add("Reply-To",aliasOfSender);
    next();
}

exports.forwardToRealUser = function (next, connection, params) {
    this.loginfo("\n\n\nforwardToRealUser\n\n\n\n",util.inspect(connection.transaction));
    var recipientAlias = params[0].user+"@"+params[0].host;
    if(!aliasToUser[recipientAlias]){
        next();
    }
    var toAddress   = aliasToUser[recipientAlias]
    var userAddress = new address("<"+toAddress+">");
    this.loginfo("\n\n\n\n\n\nForwarding mail to alias "+recipientAlias+" towards "+toAddress);
    connection.transaction.rcpt_to.pop();
    connection.transaction.rcpt_to.push(userAddress);
    next();
}






