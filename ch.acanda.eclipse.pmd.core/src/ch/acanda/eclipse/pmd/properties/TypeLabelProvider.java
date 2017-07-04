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

package ch.acanda.eclipse.pmd.properties;

import ch.acanda.eclipse.pmd.properties.PMDPropertyPageViewModel.RuleSetViewModel;

/**
 * Provides the label for the type column.
 *
 * @author Philip Graf
 */
final class TypeLabelProvider extends RuleSetConfigurationLabelProvider {

    protected TypeLabelProvider(final PMDPropertyPageViewModel model) {
        super(model);
    }

    @Override
    protected String getText(final RuleSetViewModel ruleSet) {
        return ruleSet.getType();
    }

}