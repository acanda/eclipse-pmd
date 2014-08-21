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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit tests for {@link ProjectPropertyTester}.
 *
 * @author Philip Graf
 */
public class ProjectPropertyTesterTest {

    private static String javaVersion;

    @BeforeClass
    public static void beforeClass() {
        javaVersion = System.getProperty("java.version");
    }

    @AfterClass
    public static void afterClass() {
        System.setProperty("java.version", javaVersion);
    }

    @Test
    public void javaVersion6LessThan7() {
        System.setProperty("java.version", "1.6.0_23");
        final ProjectPropertyTester tester = new ProjectPropertyTester();
        final boolean result = tester.test(null, "javaVersionLessThan", new String[] { "1.7" }, null);
        assertTrue("Version 1.6.0_23 should be less than 1.7", result);
    }

    @Test
    public void javaVersion7LessThan7() {
        System.setProperty("java.version", "1.7.0_45");
        final ProjectPropertyTester tester = new ProjectPropertyTester();
        final boolean result = tester.test(null, "javaVersionLessThan", new String[] { "1.7" }, null);
        assertFalse("Version 1.7.0_45 should not be less than 1.7", result);
    }

    @Test
    public void javaVersionLessThanExactMatch() {
        System.setProperty("java.version", "1.7.0_45");
        final ProjectPropertyTester tester = new ProjectPropertyTester();
        final boolean result = tester.test(null, "javaVersionLessThan", new String[] { "1.7.0_45" }, null);
        assertFalse("Version 1.7.0_45 should not be less than 1.7.0_45", result);
    }

    @Test
    public void java8EarlyAccess() {
        System.setProperty("java.version", "1.8.0-ea");
        final ProjectPropertyTester tester = new ProjectPropertyTester();
        final boolean result = tester.test(null, "javaVersionLessThan", new String[] { "1.7.0_51" }, null);
        assertFalse("Version 1.7.0_51 should not be less than 1.8.0-ea", result);
    }

    @Test
    public void invalidJVMVersion() {
        System.setProperty("java.version", "x.y.z");
        final ProjectPropertyTester tester = new ProjectPropertyTester();
        final boolean result = tester.test(null, "javaVersionLessThan", new String[] { "1.7.0_51" }, null);
        assertFalse("The tester should return false as the JVM version is invalid", result);
    }

    @Test
    public void invalidArgumentVersion() {
        System.setProperty("java.version", "1.7.0_51");
        final ProjectPropertyTester tester = new ProjectPropertyTester();
        final boolean result = tester.test(null, "javaVersionLessThan", new String[] { "x.y.z" }, null);
        assertFalse("The tester should return false as the argument version is invalid", result);
    }
}
