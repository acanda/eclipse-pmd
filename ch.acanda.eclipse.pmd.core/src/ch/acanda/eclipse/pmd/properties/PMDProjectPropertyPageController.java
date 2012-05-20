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

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * Controller for the PMD project property page.
 * 
 * @author Philip Graf
 */
final class PMDProjectPropertyPageController {
    
    private final PMDProjectPropertyPageModel model;
    private IProject project;
    
    public PMDProjectPropertyPageController() {
        model = new PMDProjectPropertyPageModel();
    }
    
    public PMDProjectPropertyPageModel getModel() {
        return model;
    }
    
    public void init(final IProject project) {
        this.project = project;
        model.init(project);
    }
    
    public void reset() {
        model.reset();
    }
    
    public void save() {
        final PMDProjectSettings settings = new PMDProjectSettings(project);
        settings.setRuleSetsConfiguration(model.getRuleSetsConfiguration());
        settings.setPMDEnabled(model.isPMDEnabled());
    }
    
    public boolean isValid() {
        final boolean isValid;
        if (model.isPMDEnabled()) {
            final String config = model.getRuleSetsConfiguration();
            if (config != null && config.length() > 0) {
                final File file = new File(config);
                isValid = file.exists() && file.canRead();
            } else {
                isValid = true;
            }
        } else {
            isValid = true;
        }
        return isValid;
    }
    
    public void browseForRuleSetsConfiguration(final Shell shell) {
        final FileDialog fileDialog = new FileDialog(shell);
        final String file = fileDialog.open();
        if (file != null) {
            model.setRuleSetsConfiguration(file);
        }
    }
    
}
