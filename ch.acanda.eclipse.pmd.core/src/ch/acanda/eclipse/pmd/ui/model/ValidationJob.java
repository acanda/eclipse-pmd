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

package ch.acanda.eclipse.pmd.ui.model;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.progress.UIJob;

/**
 * Validates a {@link ViewModel} asynchronously.
 * 
 * @author Philip Graf
 */
final class ValidationJob extends Job {
    
    private final ViewModel model;
    private final String propertyName;
    
    ValidationJob(final ViewModel model, final String propertyName) {
        super("ValidationJob");
        this.model = model;
        this.propertyName = propertyName;
        setSystem(true);
        setUser(false);
    }
    
    @Override
    protected IStatus run(final IProgressMonitor monitor) {
        monitor.beginTask("Validating...", IProgressMonitor.UNKNOWN);
        try {
            final ValidationResult result = new ValidationResult();
            model.validate(propertyName, result);
            final UIJob uijob = new UIJob("ValidationJob") {
                @Override
                public IStatus runInUIThread(final IProgressMonitor monitor) {
                    model.setValidationResult(result);
                    return Status.OK_STATUS;
                }
            };
            uijob.schedule();
        } finally {
            monitor.done();
        }
        return Status.OK_STATUS;
    }
    
}
