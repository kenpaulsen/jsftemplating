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
package com.sun.jsftemplating.layout.facelets;

import com.sun.jsftemplating.annotation.FormatDefinition;
import com.sun.jsftemplating.layout.LayoutDefinitionException;
import com.sun.jsftemplating.layout.LayoutDefinitionManager;
import com.sun.jsftemplating.layout.descriptors.LayoutDefinition;
import com.sun.jsftemplating.layout.template.TemplateParser;
import com.sun.jsftemplating.util.FileUtil;

import java.io.IOException;
import java.net.URL;

import javax.faces.context.FacesContext;


/**
 *
 * @author Jason Lee
 * @author Ken Paulsen
 */
@FormatDefinition
public class FaceletsLayoutDefinitionManager extends LayoutDefinitionManager {

    /**
     *	Default Constructor.
     */
    public FaceletsLayoutDefinitionManager() {
        super();
    }

    @Override
    public boolean accepts(String key) {
        boolean accept = false;
	URL url = null;
	try {
	    url = FileUtil.searchForFile(key, defaultSuffix);
	} catch (IOException ex) {
	    // Ignore this b/c we're just trying to detect if we're the right
	    // LDM... if we're here, probably we're not.
	}
        if (url == null) {
            return false;
        }

        // Eventually, we may want this check to be configurable via a
	// context-param...
	if (url.getPath().contains(".xhtml")) {
	    accept = true;
        } else {
            // Use the TemplateParser to help us read the file to see if it is a
            // valid XML-format file
            TemplateParser parser = new TemplateParser(url);
            accept = true;
            try {
                parser.open();
                parser.readUntil("=\"http://java.sun.com/jsf/facelets\"", true);
            } catch (Exception ex) {
                // Didn't work...
		accept = false;
            } finally {
                parser.close();
            }
        }

        return accept;
    }

    /**
     *  <p> This method is responsible for finding the requested
     *      {@link LayoutDefinition} for the given <code>key</code>.</p>
     *
     *  @param  key     Key identifying the desired {@link LayoutDefinition}.
     *
     *  @return         The requested {@link LayoutDefinition}.
     */
    public LayoutDefinition getLayoutDefinition(String key) throws LayoutDefinitionException {
	// Make sure we found the url
	URL url = null;
	try {
	    url = FileUtil.searchForFile(key, defaultSuffix);
	} catch (IOException ex) {
	    throw new LayoutDefinitionException(
		    "Unable to locate '" + key + "'", ex);
	}
	if (url == null) {
	    throw new LayoutDefinitionException(
		    "Unable to locate '" + key + "'");
	}

	// Read the template file
	LayoutDefinition ld = null;
	try {
	    ld  = new FaceletsLayoutDefinitionReader(key, url).read();
	} catch (IOException ex) {
	    throw new LayoutDefinitionException(
		"Unable to process '" + url.toString() + "'.", ex);
	}

        // Dispatch "initPage" handlers
        ld.dispatchInitPageHandlers(FacesContext.getCurrentInstance(), ld);

        // Return the LayoutDefinition
        return ld;
    }

    /**
     *  <p> This method returns an instance of this LayoutDefinitionManager.
     *      The object returned is a singleton (only 1 instance will be
     *      created per application).</p>
     *
     *  @return <code>FaceletsLayoutDefinitionManager</code> instance
     */
    public static LayoutDefinitionManager getInstance() {
	return getInstance(FacesContext.getCurrentInstance());
    }

    /**
     *	<p> This method provides access to the application-scoped instance
     *	    of the <code>FaceletsLayoutDefinitionManager</code>.</p>
     *
     *	@param	ctx The <code>FacesContext</code> (may be null).
     */
    public static LayoutDefinitionManager getInstance(FacesContext ctx) {
	if (ctx == null) {
	    ctx = FacesContext.getCurrentInstance();
	}
	FaceletsLayoutDefinitionManager instance = null;
	if (ctx != null) {
	    instance = (FaceletsLayoutDefinitionManager)
		ctx.getExternalContext().getApplicationMap().get(FLDM_INSTANCE);
	}
	if (instance == null) {
	    instance = new FaceletsLayoutDefinitionManager();
	    if (ctx != null) {
		ctx.getExternalContext().getApplicationMap().put(
			FLDM_INSTANCE, instance);
	    }
	}
        return instance;
    }

    /**
     *	<p> Application scope key for an instance of this class.</p>
     */
    private static final String FLDM_INSTANCE = "__jsft_FaceletsLDM";

    private static final String defaultSuffix = ".xhtml";
}
