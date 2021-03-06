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
 * Copyright 2007 Sun Microsystems, Inc. All rights reserved.
 */
package com.sun.jsftemplating.component;

import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;

import com.sun.jsftemplating.layout.LayoutDefinitionManager;
import com.sun.jsftemplating.layout.descriptors.LayoutComponent;
import com.sun.jsftemplating.layout.descriptors.LayoutDefinition;
import com.sun.jsftemplating.layout.descriptors.LayoutElement;


/**
 *  <p>	This abstract class provides base functionality for components that
 *	work in conjunction with the
 *	{@link com.sun.jsftemplating.renderer.TemplateRenderer} and would like
 *	to provide <code>UIInput</code> functionality.  It provides a default
 *	implementation of the {@link TemplateComponent} interface.</p>
 *
 *  @see    com.sun.jsftemplating.renderer.TemplateRenderer
 *  @see    com.sun.jsftemplating.component.TemplateComponent
 *  @see    com.sun.jsftemplating.component.TemplateComponentHelper
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
public abstract class TemplateOutputComponentBase extends UIOutput implements TemplateComponent {

    /**
     *	<p> This method will find the request child <code>UIComponent</code>
     *	    by id.  If it is not found, it will attempt to create it if it can
     *	    find a {@link LayoutElement} describing it.</p>
     *
     *	@param	context	    The <code>FacesContext</code>.
     *	@param	id	    The <code>UIComponent</code> id to find.
     *
     *	@return	The requested <code>UIComponent</code>.
     */
    public UIComponent getChild(FacesContext context, String id) {
	return getHelper().getChild(this, context, id);
    }


    /**
     *	<p> This method will find the request child <code>UIComponent</code> by
     *	    id (the id is obtained from the given {@link LayoutComponent}).  If
     *	    it is not found, it will attempt to create it from the supplied
     *	    {@link LayoutElement}.</p>
     *
     *	@param	context	    The <code>FacesContext</code>.
     *	@param	descriptor  The {@link LayoutElement} describing the <code>UIComponent</code>
     *
     *	@return	The requested <code>UIComponent</code>.
     */
    public UIComponent getChild(FacesContext context, LayoutComponent descriptor) {
	return getHelper().getChild(this, context, descriptor);
    }

    /**
     *	<p> This method returns the {@link LayoutDefinition} associated with
     *	    this component.</p>
     *
     *	@param	context	The <code>FacesContext</code>.
     *
     *	@return	{@link LayoutDefinition} associated with this component.
     */
    public LayoutDefinition getLayoutDefinition(FacesContext context) {
	return getHelper().getLayoutDefinition(context);
    }

    /**
     *	<p> This method saves the state for this component.  It relies on the
     *	    superclass to save its own sate, this method will invoke
     *	    super.saveState().</p>
     *
     *	@param	context	The <code>FacesContext</code>.
     *
     *	@return The serialized state.
     */
    public Object saveState(FacesContext context) {
	return getHelper().saveState(context, super.saveState(context));
    }

    /**
     *	<p> This method restores the state for this component.  It will invoke
     *	    the superclass to restore its state.</p>
     *
     *	@param	context	The <code>FacesContext</code>.
     *	@param	state	The serialized state.
     */
    public void restoreState(FacesContext context, Object state) {
	super.restoreState(context, getHelper().restoreState(context, state));
    }

    /**
     *	<p> This method returns the {@link LayoutDefinition} key for this
     *	    component.</p>
     *
     *	@return	The key to use in the {@link LayoutDefinitionManager}.
     */
    public String getLayoutDefinitionKey() {
	return getHelper().getLayoutDefinitionKey();
    }


    /**
     *	<p> This method sets the {@link LayoutDefinition} key for this
     *	    component.</p>
     *
     *	@param	key The key to use in the {@link LayoutDefinitionManager}.
     */
    public void setLayoutDefinitionKey(String key) {
	getHelper().setLayoutDefinitionKey(key);
    }

    public <V> V getPropertyValue(V field, String attributeName, V defaultValue) {
        return getHelper().getAttributeValue(this, field, attributeName, defaultValue);
    }

    /**
     *	<p> This method retrieves the {@link TemplateComponentHelper} used by
     *	    this class to help implement the {@link TemplateComponent}
     *	    interface.</p>
     *
     *	@return	The {@link TemplateComponentHelper} for this component.
     */
    protected TemplateComponentHelper getHelper() {
	if (_helper == null) {
	    _helper = new TemplateComponentHelper();
	}
	return _helper;
    }

    /**
     *	<p> Our <code>TemplateComponentHelper</code>.  We initialize it on
     *	    access b/c we want to ensure it exists, if it is serialized it
     *	    won't exist if we init it here or in the constructor.</p>
     */
    private transient TemplateComponentHelper _helper = null;
}
