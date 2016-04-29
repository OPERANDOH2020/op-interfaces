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

var fs = require('fs');
var util = require('util');
var tempDir = __dirname+"/test_emails";

exports.hook_queue = function(next, connection) {
    this.loginfo("\n\n\n\n",util.inspect(connection.transaction));
    var ws = fs.createWriteStream(tempDir + '/mail.eml');
    ws.once('close', function () {
        return next(OK);
    });
    connection.transaction.message_stream.pipe(ws);
};