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
package com.sun.jsftemplating.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * <p>
 * This class provides the implementation of a simple PatternMatcher and a
 * static utility-method to create an array of SimplePatternMatchers from a
 * comma separated String.
 * </p>
 * 
 * @author Imre O&szlig;wald (ioss@emx.jevelopers.com)
 */

public class SimplePatternMatcher {

    private Pattern regexPattern = null;

    private String pattern;

    private String prepared;

    private Mode mode;

    private static enum Mode {
	SIMPLEREGEX, PREFIX, SUFFIX, EXACT
    }

    @SuppressWarnings("unchecked")
    private static final Collection<SimplePatternMatcher> EMPTY_PATTERN_COLLECTION = Collections.EMPTY_LIST;

    /**
     * Constructor.
     * 
     * @param pattern
     *                a single pattern against which this instance should match.
     * @throws PatternSyntaxException
     *                 if the pattern could not be compiled.
     */
    public SimplePatternMatcher(final String pattern) {
	
	this.pattern = pattern.trim();
	if (this.pattern.length() > 0) {
	    if (!pattern.contains("?")) {
		if (pattern.lastIndexOf('*') == 0) {
		    this.mode = Mode.SUFFIX;
		    this.prepared = pattern.substring(1);
		} else if (pattern.indexOf('*') == pattern.length() - 1) {
		    this.mode = Mode.PREFIX;
		    this.prepared = pattern.substring(0, pattern.length() - 1);
		} else if (pattern.indexOf('*') == -1) {
		    this.mode = Mode.EXACT;
		}
	    }
	    if (this.mode == null) {
		this.mode = Mode.SIMPLEREGEX;

		final String regex = this.regexify(pattern);
		// TODO: catch the possible PatternSyntaxException and provide a
		// "non-regex" Exception and message
		this.regexPattern = Pattern.compile(regex);
	    }
	}
    }

    /**
     * splits a delimiter separated <code>String</code> into a
     * <code>Collection</code> of <code>SimplePatternMatcher</code>s. If
     * the provided String is <code>null</code> or empty, an empty Collection
     * is returned. If after splitting the provided <code>String</code> a part
     * is <em>empty</em> the part will be ignored. ( So ";;;;;;" will result
     * in an empty Collection being returned. )
     * 
     * @param multiplePatterns
     *                the String to split
     * @param delimiter
     *                the delimiter to split the multiplePatterns at.
     * @return Collection of SimplePatternMatchers
     */
    public static Collection<SimplePatternMatcher> parseMultiPatternString(
	    final String multiplePatterns, final String delimiter) {
	if (multiplePatterns == null || multiplePatterns.trim().length() == 0) {
	    return EMPTY_PATTERN_COLLECTION;
	}
	final String[] patterns = multiplePatterns.split(delimiter);
	final List<SimplePatternMatcher> result = new ArrayList<SimplePatternMatcher>();
	for (String pattern : patterns) {
	    pattern = pattern.trim();
	    if (pattern.length() == 0) {
		continue;
	    }
	    final SimplePatternMatcher matcher = new SimplePatternMatcher(
		    pattern);
	    result.add(matcher);
	}
	return result;
    }

    /**
     * <p>
     * tests if the provided input matches this pattern. If the input is
     * <code>null</code> this method will return <code>false</code>
     * </p>
     * 
     * @param input
     *                the String to match against.
     * @return true if the input matches this pattern, false otherwise.
     */
    public boolean matches(final String input) {
	if (input == null) {
	    return false;
	}
	if (this.pattern.length() == 0) {
	    return true;
	}
	switch (this.mode) {
	    case PREFIX:
		return this.prepared.length() == 0
			|| input.startsWith(this.prepared);
	    case SUFFIX:
		return input.endsWith(this.prepared);
	    case EXACT:
		return input.equals(this.pattern);
	    case SIMPLEREGEX:
		return this.regexPattern.matcher(input).matches();
	    default:
		// should not happen
		throw new IllegalStateException("Unknown Mode: " + this.mode);
	}
    }

    /**
     * Default Constructor.
     */
    protected SimplePatternMatcher() {
    }

    /**
     * translates a 'simple' pattern String into a regular expression.
     * 
     * @param pattern
     *                the 'simple' pattern to translate
     * @return the translation
     */
    public static String regexify(final CharSequence pattern) {
	if(pattern == null) {
	    throw new NullPointerException("Pattern may not be null");
	}
	final StringBuilder sb = new StringBuilder();
	for (int i = 0, len = pattern.length(); i < len; i++) {
	    final char c = pattern.charAt(i);
	    switch (c) {
		case '.':
		    sb.append("\\.");
		    break;
		case '*':
		    sb.append("(.*)");
		    break;
		case '?':
		    sb.append('.');
		    break;
		default:
		    sb.append(c);
	    }
	}
	return sb.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "SimplePatternMatcher: " + this.pattern + " in mode "
		+ this.mode;
    }
}
