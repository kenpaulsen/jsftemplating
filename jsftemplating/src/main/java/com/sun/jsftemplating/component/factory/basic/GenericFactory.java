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
package com.sun.jsftemplating.component.factory.basic;

import java.io.Serializable;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.sun.jsftemplating.annotation.UIComponentFactory;
import com.sun.jsftemplating.component.factory.ComponentFactoryBase;
import com.sun.jsftemplating.layout.descriptors.LayoutComponent;


/**
 *  <p>	This factory is capable of creating any UIComponent that can be created
 *	via the <code>Application.createComponent(componentType)</code>
 *	method.  It requires that the <code>componentType</code> property be
 *	set indicating what type of component should be instantiated.</p>
 *
 *  <p>	The {@link com.sun.jsftemplating.layout.descriptors.ComponentType}
 *	id for this factory is: "component".</p>
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
@UIComponentFactory("component")
public class GenericFactory extends ComponentFactoryBase {

    /**
     *	<p> This is the factory method responsible for creating the
     *	    <code>UIComponent</code>.  It requires that the "componentType"
     *	    attribute be supplied with a valid JSF ComponentType.  This may
     *	    be supplied in the page on a per-use basis, or on an instance of
     *	    this Factory via the
     *	    {@link ComponentFactoryBase#setExtraInfo(Serializable)}
     *	    method.</p>
     *
     *	@param	context	    The <code>FacesContext</code>
     *	@param	descriptor  The {@link LayoutComponent} descriptor associated
     *			    with the requested <code>UIComponent</code>.
     *	@param	parent	    The parent <code>UIComponent</code>
     *
     *	@return	The newly created <code>UIComponent</code>.
     */
    public UIComponent create(FacesContext context, LayoutComponent descriptor, UIComponent parent) {
	// Determine the componentType
	String componentType = (String) descriptor.getEvaluatedOption(context, COMPONENT_TYPE, parent);
	if (componentType == null) {
	    Serializable extraInfo = getExtraInfo();
	    if (extraInfo != null) {
		// This component allows the (default) CompnentType to be set
		// on the factory.
		componentType = extraInfo.toString();
	    } else {
		throw new IllegalArgumentException(
			"\"&gt;component&lt;\" requires a \"" + COMPONENT_TYPE
			+ "\" property to be set to the componentType of the "
			+ "component you wish to create.");
	    }
	}

	// Create the UIComponent
	UIComponent comp = createComponent(context, componentType, descriptor, parent);

	// Set all the attributes / properties
	setOptions(context, descriptor, comp);

	// Return the component
	return comp;
    }

    /**
     *	<p> This the is the property name ("componentType") that will be used
     *	    to define the componentType to be used to create the
     *	    <code>UIComponent</code>.  This much match the defined component
     *	    type in the faces-config.xml file (this is usually stored along
     *	    with the component .class files in the component's jar file).</p>
     */
    public static final String COMPONENT_TYPE	= "componentType";
}
