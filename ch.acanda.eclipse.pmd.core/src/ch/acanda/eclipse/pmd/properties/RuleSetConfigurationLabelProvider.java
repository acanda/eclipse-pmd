// =====================================================================
//
// Copyright (C) 2012 - 2015, Philip Graf
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// =====================================================================

package ch.acanda.eclipse.pmd.properties;

import java.text.MessageFormat;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import ch.acanda.eclipse.pmd.properties.PMDPropertyPageViewModel.RuleSetViewModel;

/**
 * Base class for the table's column label providers.
 *
 * @author Philip Graf
 */
abstract class RuleSetConfigurationLabelProvider extends ColumnLabelProvider {

    private final PMDPropertyPageViewModel model;

    protected RuleSetConfigurationLabelProvider(final PMDPropertyPageViewModel model) {
        this.model = model;
    }

    @Override
    public String getText(final Object element) {
        return getText(toRuleSet(element));
    }

    protected abstract String getText(RuleSetViewModel ruleSet);

    protected RuleSetViewModel toRuleSet(final Object element) {
        return (RuleSetViewModel) element;
    }

    protected Image getImage(final RuleSetViewModel ruleSet) {
        if (!ruleSet.isLocationValid()) {
            final String key = isActive(ruleSet) ? ISharedImages.IMG_OBJS_ERROR_TSK : ISharedImages.IMG_OBJS_WARN_TSK;
            return PlatformUI.getWorkbench().getSharedImages().getImage(key);
        }
        return null;
    }

    protected boolean isActive(final RuleSetViewModel ruleSet) {
        return model.getActiveRuleSets().contains(ruleSet);
    }

    protected String getErrorMessage(final RuleSetViewModel ruleSet) {
        final String template = "The file {0} does not exist.";
        return MessageFormat.format(template, ruleSet.getLocationToolTip());
    }

}