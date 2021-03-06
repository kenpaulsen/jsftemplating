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
 *  MetaDataHandlers.java
 *
 *  Created on December 2, 2004, 3:06 AM
 */
package com.sun.jsftemplating.handlers;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.sun.jsftemplating.annotation.Handler;
import com.sun.jsftemplating.annotation.HandlerInput;
import com.sun.jsftemplating.annotation.HandlerOutput;
import com.sun.jsftemplating.layout.LayoutDefinitionManager;
import com.sun.jsftemplating.layout.ViewRootUtil;
import com.sun.jsftemplating.layout.descriptors.ComponentType;
import com.sun.jsftemplating.layout.descriptors.LayoutComponent;
import com.sun.jsftemplating.layout.descriptors.LayoutDefinition;
import com.sun.jsftemplating.layout.descriptors.handler.HandlerContext;
import com.sun.jsftemplating.layout.descriptors.handler.HandlerDefinition;


/**
 *  <p>	This class contains
 *	{@link com.sun.jsftemplating.layout.descriptors.handler.Handler}
 *	methods that perform common utility-type functions.</p>
 *
 *  @author  Ken Paulsen (ken.paulsen@sun.com)
 */
public class MetaDataHandlers {

    /**
     *	<p> Default Constructor.</p>
     */
    public MetaDataHandlers() {
    }

    /**
     *	<p> This handler provides information about all known (global)
     *	    {@link com.sun.jsftemplating.layout.descriptors.handler.Handler}s.
     *	    It allows an input value ("id") to be passed in, this is optional.
     *	    If the value is supplied, it will return information about that
     *	    handler only.  If not supplied, it will return information about
     *	    all handlers.  Output is passed via output values "info", "ids",
     *	    and "handler".  "info" is always returned and contains a
     *	    <code>String</code> of information.  "ids" is always returned and
     *	    contains a <code>Set</code> of global {@link HandlerDefinition}
     *	    ids that may be passed into this method.  "handler" is returned
     *	    only if an id was specified and will contain the requested
     *	    {@link HandlerDefinition}.</p>
     */
    @Handler(id="getGlobalHandlerInformation",
	input={
	    @HandlerInput(name="id", type=String.class, required=false)
	},
	output={
	    @HandlerOutput(name="info", type=String.class),
	    @HandlerOutput(name="ids", type=Set.class),
	    @HandlerOutput(name="value", type=HandlerDefinition.class)
	})
    public static void getGlobalHandlerInformation(HandlerContext context) {
	// Get the known global HandlerDefinitions
	Map<String, HandlerDefinition> defs =
	    new TreeMap(LayoutDefinitionManager.getGlobalHandlerDefinitions());

	// Provide a Set of ids
	context.setOutputValue("ids", defs.keySet());

	// If a single HandlerDefinition was requested, provide it
	// Produce a String of information also
	String key = (String) context.getInputValue("id");
	if (key != null) {
	    context.setOutputValue("value", defs.get(key));
	    context.setOutputValue("info", defs.get(key).toString());
	} else {
	    Iterator<HandlerDefinition> it = defs.values().iterator();
	    StringBuffer buf = new StringBuffer("");
	    while (it.hasNext()) {
		buf.append(it.next().toString());
	    }
	    context.setOutputValue("info", buf.toString());
	}
    }

    /**
     *	<p> This handler provides information about all known (global)
     *	    {@link ComponentType}s.  It allows an input value ("id") to be
     *	    passed in, this is optional.  If the value is supplied, it will
     *	    return information about that {@link ComponentType} only.  If not
     *	    supplied, it will return information about all
     *	    {@link ComponentType}s.  Output is passed via output values
     *	    "info", "ids", and "value".  "info" is always returned and
     *	    contains a <code>String</code> of information.  "ids" is always
     *	    returned and contains a <code>Set</code> of global
     *	    {@link ComponentType} ids that may be passed into this method.
     *	    "value" is returned only if an id was specified and will contain
     *	    the requested {@link ComponentType}.</p>
     */
    @Handler(id="getGlobalComponentTypeInformation",
	input={
	    @HandlerInput(name="id", type=String.class, required=false)
	},
	output={
	    @HandlerOutput(name="info", type=String.class),
	    @HandlerOutput(name="ids", type=Set.class),
	    @HandlerOutput(name="value", type=ComponentType.class)
	})
    public static void getGlobalComponentTypeInformation(HandlerContext context) {
	// Get the known global HandlerDefinitions
	Map<String, ComponentType> defs = new TreeMap(
		LayoutDefinitionManager.getGlobalComponentTypes(
		    context.getFacesContext()));

	// Provide a Set of ids
	context.setOutputValue("ids", defs.keySet());

	// If a single HandlerDefinition was requested, provide it
	// Produce a String of information also
	String key = (String) context.getInputValue("id");
	if (key != null) {
	    // Return info for 1 ComponentType, return the type itself too
	    context.setOutputValue("value", defs.get(key));
	    context.setOutputValue("info", defs.get(key).toString());
	} else {
	    // Return info for ALL ComponentTypes
	    Iterator<ComponentType> it = defs.values().iterator();
	    StringBuffer buf = new StringBuffer("");
	    while (it.hasNext()) {
		buf.append(it.next().toString());
	    }
	    context.setOutputValue("info", buf.toString());
	}
    }

    /**
     *	<p> This handler finds the (closest) requested
     *	    {@link LayoutComponent} for the given <code>viewId</code> /
     *	    <code>clientId</code>.  If the <code>viewId</code> is not supplied,
     *	    the current <code>UIViewRoot</code> will be used.  The
     *	    {@link LayoutComponent} is returned via the <code>component</code>
     *	    output parameter.  If an exact match is not found, it will return
     *	    the last {@link LayoutComponent} found while searching the tree --
     *	    this should be the last {@link LayoutComponent} in the hierarchy
     *	    of the specified component.</p>
     *
     *	<p> This is not an easy process since JSF components may not all be
     *	    <code>NamingContainer</code>s, so the clientId is not sufficient to
     *	    find it.  This is unfortunate, but we we have to deal with it.</p>
     */
    @Handler(id="getLayoutComponent",
	input={
	    @HandlerInput(name="viewId", type=String.class, required=false),
	    @HandlerInput(name="clientId", type=String.class, required=true)},
	output={
	    @HandlerOutput(name="component", type=LayoutComponent.class)})
    public static void getLayoutComponent(HandlerContext ctx) {
	// First get the clientId that we are going to attempt to walk.
	String viewId = (String) ctx.getInputValue("viewId");
	String clientId = (String) ctx.getInputValue("clientId");

	// Next, find it
	LayoutComponent result = LayoutDefinitionManager.getLayoutComponent(
		ctx.getFacesContext(), viewId, clientId);

	// Set the result
	ctx.setOutputValue("component", result);
    }

    /**
     *	<p> This handler provides a way to get a {@link LayoutDefinition}.</p>
     */
    @Handler(id="getLayoutDefinition",
	input={
	    @HandlerInput(name="viewId", type=String.class, required=false)},
	output={
	    @HandlerOutput(name="layoutDefinition", type=LayoutDefinition.class)})
    public static void getLayoutDefinition(HandlerContext ctx) {
	// First get the viewId...
	String viewId = (String) ctx.getInputValue("viewId");

	// Find the LayoutDefinition
	LayoutDefinition def = ViewRootUtil.getLayoutDefinition(viewId);

	// Set the result
	ctx.setOutputValue("layoutDefinition", def);
    }

    /**
     *	<p> This handler returns true if jsft is running in debug mode.</p>
     */
    @Handler(id="isDebug",
	output={
	    @HandlerOutput(name="value", type=Boolean.class)})
    public static void isDebug(HandlerContext ctx) {
	// Set the result
	ctx.setOutputValue("value",
	    LayoutDefinitionManager.isDebug(ctx.getFacesContext()));
    }
}
