// forward_to_aliases

// documentation via: haraka -c /home/ciprian/Workspace/op-email-services/op-haraka-server -h plugins/forward_to_aliases

// Put your plugin code here
// type: `haraka -h Plugins` for documentation on how to create a plugin

var fs = require('fs');
var util = require('util');
var address = require("address-rfc2821").Address;

var userToAliases;
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
    plugin.register_hook('deliverToIntendedDestination','deliverToIntendedDestination');
    plugin.register_hook('addReplyToField','addReplyToField');
    plugin.register_hook('forwardToRealUser','forwardToRealUser');
}

exports.deliverToIntendedDestination = function (next, connection, params) {
    this.loginfo("\n\n\n\n\n\n\n",util.inspect(connection));
    try{
        var maybeIsAliasOfDestination =connection.transaction.rcpt_to[0].split("@")[0];
        maybeIsAliasOfDestination = JSON.parse(maybeIsAliasOfDestination.toString('utf8'));
        if(maybeIsAliasOfDestination['isAnAlias'] === true){
            connection.transaction.rcpt_to.pop();
            connection.transaction.rcpt_to.push(maybeIsAliasOfDestination['sender']);
            var userAlias = maybeIsAliasOfDestination['userAlias'];
            connection.transaction.mail_from.original = '<'+userAlias+'>';
            connection.transaction.mail_from.user = userAlias.split('@')[0];
            connection.transaction.mail_from.user = userAlias.split('@')[1];
            next();
        }
    }
    catch(e){
        next();
    }
}

exports.addReplyToField = function (next, connection, params) {
    this.loginfo("\n\n\n\n\n\n\n",util.inspect(connection));
    this.loginfo("\n\n\n\n",util.inspect(connection.transaction.rcpt_to));
    var recipientAlias = params[0].user+"@"+params[0].host;
    if(!aliasToUser[recipientAlias]){
        next();
    }
    var aliasOfSender = JSON.stringify({
            'userAlias':recipientAlias,
            'sender':connection.transaction.mail_from.original,
            'isAnAlias':true
        }).toString('base64')+"@operando.com";
    connection.transaction.header.add(this,{"Reply_To":aliasOfSender});
    next();
}

exports.forwardToRealUser = function (next, connection, params) {
    this.loginfo("\n\n\n\n\n\n\n",util.inspect(params));
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






