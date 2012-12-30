// =====================================================================
//
// Copyright (C) 2012, Philip Graf
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// =====================================================================

package ch.acanda.eclipse.pmd.marker.resolution.java.optimization;

import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;

import ch.acanda.eclipse.pmd.marker.resolution.ASTQuickFixTestCase;

/**
 * Unit plug-in test for {@link MethodArgumentCouldBeFinalQuickFix}.
 * 
 * @author Philip Graf
 */
public class MethodArgumentCouldBeFinalQuickFixTest extends ASTQuickFixTestCase<MethodArgumentCouldBeFinalQuickFix> {
    
    public MethodArgumentCouldBeFinalQuickFixTest(final TestParameters parameters) {
        super(parameters);
    }

    @Parameters
    public static Collection<Object[]> getTestData() {
        return createTestData(MethodArgumentCouldBeFinalQuickFixTest.class.getResourceAsStream("MethodArgumentCouldBeFinal.xml"));
    }

}
