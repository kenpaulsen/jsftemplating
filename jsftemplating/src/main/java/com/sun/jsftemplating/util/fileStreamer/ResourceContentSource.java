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
package com.sun.jsftemplating.util.fileStreamer;

import com.sun.jsftemplating.layout.LayoutDefinitionManager;
import com.sun.jsftemplating.util.FileUtil;
import com.sun.jsftemplating.util.LogUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;



/**
 *  <p>	This class implements {@link ContentSource}.  It retreives it content
 *	by looking in the "docroot" of the application, and if not found, it
 *	will check the classpath.</p>
 */
public class ResourceContentSource implements ContentSource {

    /**
     *	<p> This method returns a unique string used to identify this
     *	    {@link ContentSource}.  This string must be specified in order to
     *	    select the appropriate {@link ContentSource} when using the
     *	    {@link FileStreamer}.</p>
     */
    public String getId() {
	return ID;
    }

    /**
     *  <p> This method is responsible for generating the content and returning
     *	    an <code>InputStream</code> for that content.  It is also
     *	    responsible for setting any attribute values in the
     *	    {@link Context}, such as {@link Context#EXTENSION} or
     *	    {@link Context#CONTENT_TYPE}.</p>
     */
    public InputStream getInputStream(Context ctx) throws IOException {
	// See if we already have it.
	InputStream in = (InputStream) ctx.getAttribute("inputStream");
	if (in != null) {
	    return in;
	}

	// Get the path...
	String path = getResourcePath(ctx);

	// Find the file...
	URL url = FileUtil.searchForFile(path, null);
	if (url == null) {
	    return null;
	}

	// Set the extension so it can be mapped to a MIME type
	int index = path.lastIndexOf('.');
	if (index > 0) {
	    ctx.setAttribute(Context.EXTENSION, path.substring(index + 1));
	}

	// Open the InputStream
	in = url.openStream();
	ctx.setAttribute("inputStream", in);

	// Return an InputStream to the file
	return in;
    }

    /**
     *	<p> This method returns the path of the resource that was
     *	    requested.</p>
     */
    public String getResourcePath(Context ctx) {
	// Check the ctx for the path...
	String path = (String) ctx.getAttribute(Context.FILE_PATH + "norm");
	if (path != null) {
	    return path;
	}

	// Not yet calculated, calculate it...
	String origPath = (String) ctx.getAttribute(Context.FILE_PATH);

	// Store for next time... and return
	path = normalize(origPath);
	ctx.setAttribute(Context.FILE_PATH + "norm", path);
	return path;
    }

    /**
     *	<p> This method attempts to normalize the paths that we are using for
     *	    comparison purposes and to ensure illegal paths are prevented for
     *	    security reasons.</p>
     */
    public static String normalize(String origPath) {
	String path = origPath;

	// Normalize it...
	if ((path != null) && (path.length() > 0)) {
	    path = path.replace('\\', '/');
	    // Remove leading '/' chars
	    while ((path.length() > 0) && (path.charAt(0) == '/')) {
		path = path.substring(1);
	    }
	    // Replace all double "//" with "/"
	    while (path.indexOf("//") != -1) {
		path = path.replace("//", "/");
	    }
	    for (int idx = path.indexOf("../"); idx != -1; idx = path.indexOf("../")) {
		if (idx == 0) {
		    // Make sure we're not trying to go before the context root
		    LogUtil.info("JSFT0010", origPath);
		    throw new IllegalArgumentException(
			"Invalid Resource Path: '" + origPath + "'");
		}
		if (path.charAt(idx-1) != '/') {
		    // Not a "../" match...
		    continue;
		}
		// Create new path after evaluating ".."
		int prevPathIdx = path.lastIndexOf('/', idx-2) + 1;
		path = path.substring(0, prevPathIdx)	// before x/../
		    + path.substring(idx + 3);		// after  x/../
		while ((path.length() > 0) && (path.charAt(0) == '/')) {
		    // Remove leading '/' chars
		    path = path.substring(1);
		}
	    }
	    // We check for "../" so ".." at the end of a path could occur,
	    // which is fine, unless it is also at the beginning...
	    if (path.equals("..")) {
		path = null;
	    }

	    // Last ensure path does not end in a '/'
	    if (path.endsWith("/")) {
		path = path.substring(0, path.length()-1);
	    }
	}

	return path;
    }

    /**
     *	<p> This method may be used to clean up any temporary resources.  It
     *	    will be invoked after the <code>InputStream</code> has been
     *	    completely read.</p>
     */
    public void cleanUp(Context ctx) {
	// Get the File information
	InputStream is = (InputStream) ctx.getAttribute("inputStream");

	// Close the InputStream
	if (is != null) {
	    try {
		is.close();
	    } catch (Exception ex) {
		// Ignore...
	    }
	}
	ctx.removeAttribute("inputStream");
    }

    /**
     *	<p> This method is responsible for returning the last modified date of
     *	    the content, or -1 if not applicable.  This information will be
     *	    used for caching.</p>
     */
    public long getLastModified(Context context) {
	FacesContext fc = FacesContext.getCurrentInstance();
	if (LayoutDefinitionManager.isDebug(fc)) {
	    // When in debug mode, don't cache resources... otherwise always
	    // allow browser to always cache them
	    if (fc != null) {
		// Check to see if this resource exists in the FileSystem, if
		// it doesn't we will let the browser cache it b/c it can't be
		// changed.
		String path = getResourcePath(context);
		path = FileUtil.getRealPath(
		    fc.getExternalContext().getContext(), path);
		if (path != null) {
		    long time = new File(path).lastModified();
		    if (time > 0) {
			// Send the timestamp so it will only be cached by
			// the browser if it hasn't changed
			return time;
		    }
		}
	    }
	}

	// This will enable caching on all files served through this code path
	return DEFAULT_MODIFIED_DATE;
    }

    /**
     *	This is the default "Last-Modified" Date.  Its value is the
     *	<code>Long</code> representing the initialization time of this class.
     */
    protected static final long DEFAULT_MODIFIED_DATE = (new Date()).getTime();

    /**
     *	<p>This is the ID for this {@link ContentSource}.</p>
     */
    public static final String ID = "resourceCS";
}
