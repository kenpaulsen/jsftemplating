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
package com.sun.jsftemplating.layout.template;

import java.io.IOException;

import com.sun.jsftemplating.layout.SyntaxException;
import com.sun.jsftemplating.layout.descriptors.LayoutComponent;
import com.sun.jsftemplating.layout.descriptors.LayoutElement;
import com.sun.jsftemplating.layout.descriptors.LayoutStaticText;
import com.sun.jsftemplating.util.LayoutElementUtil;
import com.sun.jsftemplating.util.Util;


/**
 *  <p> Since many contexts share common functionality (i.e. processing static
 *	text elements), it makes sense to have a base {@link ProcessingContext}
 *	which may be specialized as needed.</p>
 */
public class BaseProcessingContext implements ProcessingContext {

    /**
     *  <p> This is called when a component tag is found
     *	    (&lt;tagname ...).</p>
     */
    public void beginComponent(ProcessingContextEnvironment env, String content) throws IOException {
	// We have a UIComponent tag... first get the parser
	// Skip white Space
	TemplateReader reader = env.getReader();
	TemplateParser parser = reader.getTemplateParser();
	parser.skipCommentsAndWhiteSpace(TemplateParser.SIMPLE_WHITE_SPACE);

// tagStack.push(content);
	// Create the LayoutComponent
	LayoutElement parent = env.getParent();
	LayoutComponent child = reader.createLayoutComponent(
		parent, env.isNested(), content);
	parent.addChildLayoutElement(child);

	// See if this is a single tag or if there is a closing tag
	boolean single = false;
	int ch = parser.nextChar();
	if (ch == '/') {
	    // Single Tag
	    ch = parser.nextChar();  // Throw away '>'
	    single = true;
	    reader.popTag();	    // Don't look for ending tag
	}
	if (ch != '>') {
	    throw new SyntaxException(
		"Expected '>' found '" + (char) ch + "'.");
	}

	if (single) {
	    // This is also the end of the component in this case...
	    endComponent(env, content);
	} else {
	    // Process child LayoutElements (recurse)
	    reader.process(
		TemplateReader.LAYOUT_COMPONENT_CONTEXT, child, true);
	}
    }

    /**
     *  <p> This is called when an end component tag is found (&lt;/tagname&gt;
     *	    or &lt;tagname ... /&gt;).  Because it may be called for either of
     *	    the above syntaxes, the caller of this method is responsible for
     *	    maintaining the parser position, this method (or its subclasses)
     *	    should not effect the parser position.</p>
     */
    public void endComponent(ProcessingContextEnvironment env, String content) throws IOException {
    }

    /**
     *  <p> This is called when a special tag is found (&lt;!tagname ...).</p>
     */
    public void beginSpecial(ProcessingContextEnvironment env, String content) throws IOException {
	CustomParserCommand command =
	    env.getReader().getCustomParserCommand(content);
	if (command == null) {
	    // If there is no Custom command for this, use the default...
	    command = TemplateReader.EVENT_PARSER_COMMAND;
	}
	command.process(this, env, content);
    }

    /**
     *  <p> This is called when a special end tag is found (&lt;/tagname ... or
     *	    &lt;!tagname ... /&gt;).</p>
     */
    public void endSpecial(ProcessingContextEnvironment env, String content) throws IOException {
    }

    /**
     *  <p> This is called when static text is found (").</p>
     */
    public void staticText(ProcessingContextEnvironment env, String content) throws IOException {
	LayoutElement parent = env.getParent();

	// Create a LayoutStaticText
	LayoutComponent child = new LayoutStaticText(
	    parent,
	    LayoutElementUtil.getGeneratedId(
		"txt", env.getReader().getNextIdNumber()),
	    content);
	child.addOption("value", content);
	child.setNested(env.isNested());

	parent.addChildLayoutElement(child);
    }

    /**
     *  <p> This is called when escaped static text is found (').  The
     *	    difference between this and staticText is that HTML is expected to
     *	    be escaped so the browser does not parse it.</p>
     */
    public void escapedStaticText(ProcessingContextEnvironment env, String content) throws IOException {
	staticText(env, Util.htmlEscape(content));
    }

    /**
     *  <p> This method is invoked when nothing else matches.</p>
     *
     *  <p> This implementation reads a character and does nothing with it.</p>
     */
    public void handleDefault(ProcessingContextEnvironment env, String content) throws IOException {
	env.getReader().getTemplateParser().nextChar();
    }

    /**
     *	<p>  This is a static reference to the "staticText"
     *	    {@link ComponentType}.</p>
    public static final ComponentType STATIC_TEXT = 
	LayoutDefinitionManager.getGlobalComponentType(null, "staticText");
     */
}
