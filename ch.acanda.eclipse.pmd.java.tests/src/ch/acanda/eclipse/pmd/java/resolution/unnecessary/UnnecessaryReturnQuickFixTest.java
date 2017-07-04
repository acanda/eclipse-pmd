// =====================================================================
//
// Copyright (C) 2012 - 2017, Philip Graf
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// =====================================================================

package ch.acanda.eclipse.pmd.java.resolution.unnecessary;

import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;

import ch.acanda.eclipse.pmd.java.resolution.ASTQuickFixTestCase;
import ch.acanda.eclipse.pmd.java.resolution.QuickFixTestData.TestParameters;

/**
 * Unit plug-in test for {@link UnnecessaryReturnQuickFix}.
 * 
 * @author Philip Graf
 */
public class UnnecessaryReturnQuickFixTest extends ASTQuickFixTestCase<UnnecessaryReturnQuickFix> {
    
    public UnnecessaryReturnQuickFixTest(final TestParameters parameters) {
        super(parameters);
    }

    @Parameters
    public static Collection<Object[]> getTestData() {
        return createTestData(UnnecessaryReturnQuickFixTest.class.getResourceAsStream("UnnecessaryReturn.xml"));
    }

}
