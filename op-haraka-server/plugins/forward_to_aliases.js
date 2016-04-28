// forward_to_aliases

// documentation via: haraka -c /home/ciprian/Workspace/op-email-services/op-haraka-server -h plugins/forward_to_aliases

// Put your plugin code here
// type: `haraka -h Plugins` for documentation on how to create a plugin
var fs = require('fs');
var util = require('util');


var userToAliases;
var aliasToUser;

loadAliases();

exports.hook_rcpt = function (next, connection, params) {

    var recipientAlias = params[0].user+"@"+params[0].host;
    var self = this;

    this.loginfo("\n\n\n\nTHIS\n\n\n\n",util.inspect(this));
    this.loginfo("\n\n\n\nTHIS\n\n\n\n",util.inspect(connection))

    connection.transaction.rcpt_to.add({user:aliasToUser[recipientAlias].split("@")[0]});
    connection.transaction.rcpt_to.add({host : "operando.com"});
    connection.transaction.rcpt_to.add({original: "<"+recipientAlias+">"});



    if(userToAliases){
        displayUserOfAlias(recipientAlias);
    }else{
        setTimeout(function(){
            displayUserOfAlias(recipientAlias);
        },100)
    }

    function displayUserOfAlias(alias){
        self.loginfo("\n\n\n\n\n\n\n\nUser with alias ",alias," is ",aliasToUser[alias]);
        next();
    }
}

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


