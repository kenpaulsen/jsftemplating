/*
 * The contents of this file are subject to the terms 
 * of the Common Development and Distribution License 
 * (the License).  You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at 
 * https://jsftemplating.dev.java.net/cddl1.html or
 * jsftemplating/cddl1.txt.
 * See the License for the specific language governing 
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL 
 * Header Notice in each file and include the License file 
 * at jsftemplating/cddl1.txt.  
 * If applicable, add the following below the CDDL Header, 
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information: 
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 */
package com.sun.jsftemplating.layout;

import com.sun.jsftemplating.TemplatingException;


/**
 *  <p>	This exception is thrown when a syntax error has occurred.  For i/o
 *	related exceptions, see {@link LayoutDefinitionException}.</p>
 */
public class SyntaxException extends TemplatingException {
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    public SyntaxException(String msg, Throwable ex) {
	super(msg, ex);
    }

    /**
     *
     */
    public SyntaxException() {
	super();
    }

    /**
     *
     */
    public SyntaxException(Throwable ex) {
	super(ex);
    }

    /**
     *
     */
    public SyntaxException(String msg) {
	super(msg);
    }
}
