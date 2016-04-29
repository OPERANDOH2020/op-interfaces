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



const spawn = require('child_process').spawn;
const haraka_server = spawn("haraka", ['-c','/home/ciprian/Workspace/op-email-services/op-haraka-server'])


const mailer = require('nodemailer');
var smtpTransport = require('nodemailer-smtp-transport');

var server_ready = false;
var tests = []

haraka_server.stdout.on("data",function(data){
    data = data.toString();
    console.log(data);



    if(!server_ready){
        server_ready = data.match("Listening on 127.0.0.1:2525");
        if(server_ready){
            console.log("Server ready");
        }
    }

    if(!server_ready){
        return;
    }

    if(tests.length>0) {
        tests = tests.filter(function(test){
            //if test passes remove it from the tests
            var passed = runTest(test,data);
            if(passed){
                console.log("\n\n\nTest ",test," passed\n\n\n")
            }
            return !passed;
        })
    }

    function runTest(test,data) {
        if(!test.expectedOutput){
            console.error("\n\n\nTest",test," does not provide expected output\n\n\n")
            return false;
        }
        var passed = data.match(test.expectedOutput);
        if(passed){
            return true;
        }else{
            return false;
        }
    }
})



function ifServerReadyPerformTests(){
    if(server_ready){
        performTests();
    }else{
        setTimeout(ifServerReadyPerformTests,500);
    }
}

setTimeout(ifServerReadyPerformTests,500);

function performTests() {
    testing = true;
    var transporter = mailer.createTransport(smtpTransport({host: '127.0.0.1', port: 2525, direct: true}))

    function test1(){

        tests.push({
            expectedOutput:"User with alias web_demigod@operando.com is rafael@operando.com"
        })

        var mailOptions = {
            from: ' "Facebook" <facebook@facebook.com>',
            to: 'web_demigod@operando.com',
            subject: 'Hello',
            text: 'Oh my Demigod!'
        };

        transporter.sendMail(mailOptions, function (err, info) {
            if (err) {
                console.log(err);
            } else {
                console.log("Message sent:", info.response);
            }
        })
    }

    test1();
}