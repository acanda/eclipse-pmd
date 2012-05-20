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

package ch.acanda.eclipse.pmd.properties;

import java.util.Objects;

import org.eclipse.core.resources.IProject;

import ch.acanda.eclipse.pmd.ui.model.ViewModel;

/**
 * @author Philip Graf
 */
final class PMDProjectPropertyPageModel extends ViewModel {
    
    private PMDProjectSettings settings;
    private boolean isPMDEnabled;
    private String ruleSetsConfiguration;
    
    public void init(final IProject project) {
        settings = new PMDProjectSettings(project);
        reset();
    }
    
    public boolean isPMDEnabled() {
        return isPMDEnabled;
    }
    
    public void setPMDEnabled(final boolean isPMDEnabled) {
        setProperty("PMDEnabled", this.isPMDEnabled, this.isPMDEnabled = isPMDEnabled);
    }
    
    public String getRuleSetsConfiguration() {
        return ruleSetsConfiguration;
    }
    
    public void setRuleSetsConfiguration(final String ruleSetsConfiguration) {
        setProperty("ruleSetsConfiguration", this.ruleSetsConfiguration, this.ruleSetsConfiguration = ruleSetsConfiguration);
    }
    
    @Override
    protected boolean updateDirty() {
        boolean isClean = Objects.equals(settings.getRuleSetsConfiguration(), getRuleSetsConfiguration());
        isClean = isClean && settings.isPMDEnabled() == isPMDEnabled();
        return !isClean;
    }
    
    @Override
    protected void reset() {
        setRuleSetsConfiguration(settings.getRuleSetsConfiguration());
        setPMDEnabled(settings.isPMDEnabled());
    }
    
}
