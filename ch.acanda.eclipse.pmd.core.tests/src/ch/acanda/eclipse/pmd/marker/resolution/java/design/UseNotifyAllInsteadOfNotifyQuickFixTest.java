// =====================================================================
//
// Copyright (C) 2012 - 2013, Philip Graf
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

/**
 * Unit plug-in test for {@link UseNotifyAllInsteadOfNotifyQuickFix}.
 * 
 * @author Philip Graf
 */
public class UseNotifyAllInsteadOfNotifyQuickFixTest extends ASTQuickFixTestCase<UseNotifyAllInsteadOfNotifyQuickFix> {
    
    public UseNotifyAllInsteadOfNotifyQuickFixTest(final TestParameters parameters) {
        super(parameters);
    }
    
    @Parameters
    public static Collection<Object[]> getTestData() {
        return createTestData(UseNotifyAllInsteadOfNotifyQuickFixTest.class.getResourceAsStream("UseNotifyAllInsteadOfNotify.xml"));
    }
    
}
