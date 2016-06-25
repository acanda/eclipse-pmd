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

package ch.acanda.eclipse.pmd.properties;

import org.eclipse.swt.graphics.Image;

import ch.acanda.eclipse.pmd.properties.PMDPropertyPageViewModel.RuleSetViewModel;

/**
 * Provides the label and tool tip for the location column.
 *
 * @author Philip Graf
 */
final class LocationLabelProvider extends RuleSetConfigurationLabelProvider {

    protected LocationLabelProvider(final PMDPropertyPageViewModel model) {
        super(model);
    }

    @Override
    protected String getText(final RuleSetViewModel ruleSet) {
        return ruleSet.getLocation();
    }

    @Override
    public String getToolTipText(final Object element) {
        final RuleSetViewModel ruleSet = toRuleSet(element);
        if (ruleSet.isLocationValid()) {
            return ruleSet.getResolvedPath();
        }
        return getErrorMessage(ruleSet);
    }

    @Override
    public Image getToolTipImage(final Object element) {
        return getImage(toRuleSet(element));
    }

}