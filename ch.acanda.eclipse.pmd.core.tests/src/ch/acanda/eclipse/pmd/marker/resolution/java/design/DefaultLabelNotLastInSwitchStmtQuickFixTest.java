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

package ch.acanda.eclipse.pmd.marker.resolution.java.design;

import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;

import ch.acanda.eclipse.pmd.marker.resolution.ASTQuickFixTestCase;
import ch.acanda.eclipse.pmd.marker.resolution.QuickFixTestData.TestParameters;

/**
 * Unit plug-in test for {@link DefaultLabelNotLastInSwitchStmtQuickFix}.
 * 
 * @author Philip Graf
 */
public class DefaultLabelNotLastInSwitchStmtQuickFixTest extends ASTQuickFixTestCase<DefaultLabelNotLastInSwitchStmtQuickFix> {
    
    public DefaultLabelNotLastInSwitchStmtQuickFixTest(final TestParameters parameters) {
        super(parameters);
    }
    
    @Parameters
    public static Collection<Object[]> getTestData() {
        return createTestData(DefaultLabelNotLastInSwitchStmtQuickFixTest.class.getResourceAsStream("DefaultLabelNotLastInSwitchStmt.xml"));
    }
    
}
