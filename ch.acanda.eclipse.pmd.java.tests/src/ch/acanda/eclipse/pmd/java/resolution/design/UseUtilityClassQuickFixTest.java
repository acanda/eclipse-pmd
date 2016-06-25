// =====================================================================
//
// Copyright (C) 2012 - 2016, Philip Graf
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// =====================================================================

package ch.acanda.eclipse.pmd.java.resolution.design;

import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;

import ch.acanda.eclipse.pmd.java.resolution.QuickFixTestData.TestParameters;
import ch.acanda.eclipse.pmd.java.resolution.TextEditQuickFixTestCase;

/**
 * Unit plug-in test for {@link UseUtilityClassQuickFix}.
 *
 * @author Philip Graf
 */
public class UseUtilityClassQuickFixTest extends TextEditQuickFixTestCase<UseUtilityClassQuickFix> {

    public UseUtilityClassQuickFixTest(final TestParameters parameters) {
        super(parameters);
    }

    @Parameters
    public static Collection<Object[]> getTestData() {
        return createTestData(UseUtilityClassQuickFixTest.class.getResourceAsStream("UseUtilityClass.xml"));
    }

}
