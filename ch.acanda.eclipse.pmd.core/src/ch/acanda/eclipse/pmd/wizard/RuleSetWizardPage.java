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

package ch.acanda.eclipse.pmd.wizard;

import org.eclipse.jface.wizard.IWizardPage;

import ch.acanda.eclipse.pmd.domain.RuleSetModel;

public interface RuleSetWizardPage extends IWizardPage {
    
    RuleSetModel getRuleSet();
    
}
