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

/*
    Aliases will provide an interface through which the email server checks the aliases of the users
    Ideally the aliases will be stored in a mysql database
    UUID-s will uniquely identify each conversation so that a user and an external entity can exchange messages through an user alias
 */

var fs = require('fs');

exports.init = function(){}

var uuid = require('node-uuid');

exports.getRealUser = function(alias,callback){
    /*
        This function gets an alias and return the real user that has that alias or an error if the alias has no user
     */
    persistence.findById("alias",alias,function(err,aliasUserAssociation){
        if(err){
            callback(err);
            return;
        }

        if(aliasUserAssociation===null){
            callback(new Error("Unknown alias"));
            return;
        }
        callback(undefined,aliasUserAssociation.real_address)
    })
};
