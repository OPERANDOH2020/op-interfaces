/*******************************************************************************
 *  # Copyright (c) 2016 {UPRC}.
 *  # All rights reserved. This program and the accompanying materials
 *  # are made available under the terms of the The MIT License (MIT).
 *  # which accompanies this distribution, and is available at
 *  # http://opensource.org/licenses/MIT
 *
 *  # Contributors:
 *  #    {Constantinos Patsakis} {UPRC}
 *  #    {Stamatis Glykos} {UPRC}
 *  #    {Constantinos Alexandris} {UPRC}
 *  # Initially developed in the context of OPERANDO EU project www.operando.eu 
 *******************************************************************************/
'use strict';

(function () {
    var configure, highlightBlock;

    configure = hljs.configure;
    // "extending" hljs.configure method
    hljs.configure = function _configure (options) {
        var size = options.highlightSizeThreshold;

        // added highlightSizeThreshold option to set maximum size
        // of processed string. Set to null if not a number
        hljs.highlightSizeThreshold = size === +size ? size : null;

        configure.call(this, options);
    };

    highlightBlock = hljs.highlightBlock;

    // "extending" hljs.highlightBlock method
    hljs.highlightBlock = function _highlightBlock (el) {
        var innerHTML = el.innerHTML;
        var size = hljs.highlightSizeThreshold;

        // check if highlightSizeThreshold is not set or element innerHTML
        // is less than set option highlightSizeThreshold
        if (size == null || size > innerHTML.length) {
            // proceed with hljs.highlightBlock
            highlightBlock.call(hljs, el);
        }
    };

})();

