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

var env = process.env;
env.NODE_TLS_REJECT_UNAUTHORIZED = "0";

const haraka_server = spawn("haraka", ['-c','/home/ciprian/storage/Workspace/op-interfaces/op-interfaces-email-services/op-haraka-server'],{"env":env})
var util = require('util')
const mailer = require('nodemailer');
var smtpTransport = require('nodemailer-smtp-transport');

var server_ready = false;
var tests = []

haraka_server.stdout.on("data",function(data){
    data = data.toString()
    console.log(data);

    if(!server_ready){
        server_ready = data.match("Listening on 127.0.0.1:587");
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
                console.log("Test ",test," passed")
            }
            return !passed;
        })
    }

    function runTest(test,data) {
        if(!test.expectedOutput){
            console.error("Test",test," does not provide expected output")
            return false;
        }
        var passed = data.match(test.expectedOutput);
        if(passed){
            return true;
        }else{
            return false;
        }
    }
});

whenServerReadyPerformTests();

function whenServerReadyPerformTests(){
    if(server_ready){
        performTests();
    }else{
        setTimeout(whenServerReadyPerformTests,500);
    }
    function performTests() {
        testing = true;
        var transporter = mailer.createTransport(smtpTransport({host: '127.0.0.1', port: 587, direct: true}));
        test1();
        //setTimeout(test2,500);

        function test1(){

            tests.push({
                expectedOutput:"Add Reply-To eyJ1c2VyQWxpYXMiOiJ3ZWJfZGVtaWdvZEBvcGVyYW5kby5jb20iLCJzZW5kZXIiOiI8Y3Bybl90YWxtYWNlbEB5YWhvby5jb20+In0=@operando.com"
            })
            tests.push({
                expectedOutput:"Forwarding mail to alias web_demigod@operando.com towards rafael@operando.com"
            })


            var mailOptions = {
                from: ' "facebook@facebook.com',
                to: 'web_demigod@operando.com',
                subject: 'Test',
                text: 'Oh my Demigod!'
            };

            transporter.sendMail(mailOptions, function (err, info) {
                if (err) {
                    console.log(err);
                }
            })
        }

        function test2(){

            tests.push({
                expectedOutput:'Intended destination:<cprn_talmacel@yahoo.com>'
            })
            tests.push({
                expectedOutput:'Alias towards that destination:web_demigod@operando.com'
            })

            var mailOptions = {
                from: ' "rafael@operando.com',
                to: 'eyJ1c2VyQWxpYXMiOiJ3ZWJfZGVtaWdvZEBvcGVyYW5kby5jb20iLCJzZW5kZXIiOiI8Y3Bybl90YWxtYWNlbEB5YWhvby5jb20+In0=@operando.com',
                subject: 'Response',
                text: 'Yes, petty human!'
            };

            transporter.sendMail(mailOptions, function (err, info) {
                if (err) {
                    console.log(err);
                }
            })
        }
    }
}
