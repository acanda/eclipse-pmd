// =====================================================================
//
// Copyright (C) 2012 - 2014, Philip Graf
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// =====================================================================

package ch.acanda.eclipse.pmd.unsupported;

import java.util.regex.Pattern;

import org.eclipse.core.expressions.PropertyTester;
import org.osgi.framework.Version;

/**
 * This property tester provides the information about the version of the current JVM. It is used to enable the
 * replacement for the PMD property page when the JVM requirements aren't fulfilled.
 * 
 * @author Philip Graf
 */
public class ProjectPropertyTester extends PropertyTester {
    
    /**
     * A pattern matching the Java version separators. This is used to convert a Java version into an OSGi version.
     * Therefore the pattern does not have to include {@code '.'}.
     */
    private static final Pattern JAVA_VERSION_SEPARATORS = Pattern.compile("[_-]");
    
    public boolean test(final Object receiver, final String property, final Object[] args, final Object expectedValue) {
        if ("javaVersionLessThan".equals(property)) {
            return isJavaVersionLessThan(args[0].toString());
        }
        throw new UnsupportedOperationException(getClass().getSimpleName() + " does not support the property " + property);
    }
    
    /**
     * Returns {@code true} if the version of the currently used JVM is lower than the provided argument. If either of
     * the versions cannot be parsed this method returns {@code false}.
     * 
     * @param version The version string against the version of the JVM is tested. The format of the version string
     *            should follow the Java version format, e.g. 1.7.0_45.
     */
    private boolean isJavaVersionLessThan(final String version) {
        try {
            final Version actual = new Version(convert(System.getProperty("java.version", "0")));
            final Version upperBound = new Version(convert(version));
            return actual.compareTo(upperBound) < 0;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * Converts a Java version string (e.g. 1.7.0_45 and 1.8.0-ea) to an OSGi version string (e.g. 1.7.0.45 and 1.8.0.ea
     * respectively).
     */
    private String convert(final String javaVersion) {
        return JAVA_VERSION_SEPARATORS.matcher(javaVersion).replaceAll(".");
    }
    
}
