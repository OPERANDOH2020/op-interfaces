// store_test_emails

// documentation via: haraka -c /home/ciprian/Workspace/op-email-services/op-haraka-server -h plugins/store_test_emails

// Put your plugin code here
// type: `haraka -h Plugins` for documentation on how to create a plugin

var fs = require('fs');

var tempDir = __dirname+"/test_emails";

exports.hook_queue = function(next, connection) {
    var ws = fs.createWriteStream(tempDir + '/mail.eml');
    ws.once('close', function () {
        return next(OK);
    });
    connection.transaction.message_stream.pipe(ws);
};