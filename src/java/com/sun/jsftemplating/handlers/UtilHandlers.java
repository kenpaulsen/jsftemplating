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
/*
 *  UtilHandlers.java
 *
 *  Created on December 2, 2004, 3:06 AM
 */
package com.sun.jsftemplating.handlers;

import com.sun.jsftemplating.annotation.Handler;
import com.sun.jsftemplating.annotation.HandlerInput;
import com.sun.jsftemplating.annotation.HandlerOutput;
import com.sun.jsftemplating.layout.descriptors.handler.HandlerContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;


/**
 *  <p>	This class contains
 *	{@link com.sun.jsftemplating.layout.descriptors.handler.Handler}
 *	methods that perform common utility-type functions.</p>
 *
 *  @author  Ken Paulsen (ken.paulsen@sun.com)
 */
public class UtilHandlers {

    /**
     *	<p> Default Constructor.</p>
     */
    public UtilHandlers() {
    }

    /**
     *	<p> This handler writes using <CODE>System.out.println</CODE>.  It
     *	    requires that <code>value</code> be supplied as a String input
     *	    parameter.</p>
     *
     *	@param	context	The HandlerContext.
     */
    @Handler(id="println",
	input={@HandlerInput(name="value", type=String.class, required=true)})
    public static void println(HandlerContext context) {
	String value = (String) context.getInputValue("value");
	System.out.println(value);
    }

    /**
     *	<p> This handler decrements a number by 1.  This handler requires
     *	    "number" to be supplied as an Integer input value.  It sets an
     *	    output value "value" to number-1.</p>
     *
     *	@param	context	The HandlerContext.
     */
    @Handler(id="dec",
	input={
	    @HandlerInput(name="number", type=Integer.class, required=true)},
	output={
	    @HandlerOutput(name="value", type=Integer.class)})
    public static void dec(HandlerContext context) {
	Integer value = (Integer) context.getInputValue("number");
	context.setOutputValue("value", new Integer(value.intValue() - 1));
    }

    /**
     *	<p> This handler increments a number by 1.  This handler requires
     *	    "number" to be supplied as an Integer input value.  It sets an
     *	    output value "value" to number+1.</p>
     *
     *	@param	context	The HandlerContext.
     */
    @Handler(id="inc",
	input={
	    @HandlerInput(name="number", type=Integer.class, required=true)},
	output={
	    @HandlerOutput(name="value", type=Integer.class)})
    public static void inc(HandlerContext context) {
	Integer value = (Integer) context.getInputValue("number");
	context.setOutputValue("value", new Integer(value.intValue() + 1));
    }

    /**
     *	<p> This handler sets a request attribute.  It requires "key" and
     *	    "value" input values to be passed in.</p>
     *
     *	@param	context	The HandlerContext.
     */
    @Handler(id="setAttribute",
	input={
	    @HandlerInput(name="key", type=String.class, required=true),
	    @HandlerInput(name="value", required=true)})
    public static void setAttribute(HandlerContext context) {
	String key = (String) context.getInputValue("key");
	Object value = context.getInputValue("value");
	context.getFacesContext().getExternalContext().
	    getRequestMap().put(key, value);
    }

    /**
     *	<p> This method returns an <code>Iterator</code> for the given
     *	    <code>List</code>.  The <code>List</code> input value key is:
     *	    "list".  The output value key for the <code>Iterator</code> is:
     *	    "iterator".</p>
     *
     *	@param	context	The HandlerContext.
     */
    @Handler(id="getIterator",
	input={
	    @HandlerInput(name="list", type=List.class, required=true)},
	output={
	    @HandlerOutput(name="iterator", type=Iterator.class)})
    public static void getIterator(HandlerContext context) {
	List list = (List) context.getInputValue("list");
	context.setOutputValue("iterator", list.iterator());
    }

    /**
     *	<p> This method returns a <code>Boolean</code> value representing
     *	    whether another value exists for the given <code>Iterator</code>.
     *	    The <code>Iterator</code> input value key is: "iterator".  The
     *	    output value key is "hasNext".</p>
     *
     *	@param	context	The HandlerContext.
     */
    @Handler(id="iteratorHasNext",
	input={
	    @HandlerInput(name="iterator", type=Iterator.class, required=true)},
	output={
	    @HandlerOutput(name="hasNext", type=Boolean.class)})
    public static void iteratorHasNext(HandlerContext context) {
	Iterator it = (Iterator) context.getInputValue("iterator");
	context.setOutputValue("hasNext", Boolean.valueOf(it.hasNext()));
    }

    /**
     *	<p> This method returns the next object in the <code>List</code> that
     *	    the given <code>Iterator</code> is iterating over.  The
     *	    <code>Iterator</code> input value key is: "iterator".  The
     *	    output value key is "next".</p>
     *
     *	@param	context	The HandlerContext.
     */
    @Handler(id="iteratorNext",
	input={
	    @HandlerInput(name="iterator", type=Iterator.class, required=true)},
	output={
	    @HandlerOutput(name="next")})
    public static void iteratorNext(HandlerContext context) {
	Iterator it = (Iterator) context.getInputValue("iterator");
	context.setOutputValue("next", it.next());
    }

    /**
     *	<p> This method creates a List.  Optionally you may supply "size" to
     *	    create a List of blank "" values of the specified size.  The
     *	    output value from this handler is "result".</p>
     *
     *	@param	context	The HandlerContext
     */
    @Handler(id="createList",
	input={
	    @HandlerInput(name="size", type=Integer.class, required=true)},
	output={
	    @HandlerOutput(name="result", type=List.class)})
    public static void createList(HandlerContext context) {
	int size = ((Integer) context.getInputValue("size")).intValue();
	List list = new ArrayList(size);
	for (int count = 0; count < size; count++) {
	    list.add("");
	}
	context.setOutputValue("result", list);
    }

    /**
     *	<p> This method returns true.  It does not take any input or provide
     *	    any output values.</p>
     *
     *	@param context	The {@link HandlerContext}
     */
    @Handler(id="returnTrue")
    public static boolean returnTrue(HandlerContext context) {
	return true;
    }

    /**
     *	<p> This method returns false.  It does not take any input or provide
     *	    any output values.</p>
     *
     *	@param context	The {@link HandlerContext}
     */
    @Handler(id="returnFalse")
    public static boolean returnFalse(HandlerContext context) {
	return false;
    }

    /**
     *	<p> This method enables you to retrieve the clientId for the given
     *	    <code>UIComponent</code>.</p>
     *
     *	@param context	The {@link HandlerContext}
     */
    @Handler(id="getClientId",
	input={
	    @HandlerInput(name="component", type=UIComponent.class, required=true)},
	output={
	    @HandlerOutput(name="clientId", type=String.class)})
    public static void getClientId(HandlerContext context) {
	UIComponent comp = (UIComponent) context.getInputValue("component");
	context.setOutputValue("clientId",
		comp.getClientId(context.getFacesContext()));
    }

    /**
     *	<p> This method enables you to retrieve the id or clientId for the given
     *	    <code>Object</code> which is expected to be a
     *	    <code>UIComponent</code> or a <code>String</code> that already
     *	    represents the clientId.</p>
     *
     *	@param context	The {@link HandlerContext}
     */
    @Handler(id="getId",
	input={
	    @HandlerInput(name="object", required=true)},
	output={
	    @HandlerOutput(name="id", type=String.class),
	    @HandlerOutput(name="clientId", type=String.class)})
    public static void getId(HandlerContext context) {
	Object obj = context.getInputValue("object");
	if (obj == null) {
	    return;
	}

	String clientId = null;
	String id = null;
	if (obj instanceof UIComponent) {
	    clientId =
		((UIComponent) obj).getClientId(context.getFacesContext());
	    id = ((UIComponent) obj).getId();
	} else {
	    clientId = obj.toString();
	    id = clientId.substring(clientId.lastIndexOf(':') + 1);
	}
	context.setOutputValue("id", id);
	context.setOutputValue("clientId", clientId);
    }
}
