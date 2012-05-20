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

package ch.acanda.eclipse.pmd;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Philip Graf
 */
public final class PMDPlugin extends AbstractUIPlugin {
    
    public static final String ID = "ch.acanda.eclipse.pmd";
    
    private static PMDPlugin plugin;
    
    @Override
    public void start(final BundleContext context) throws Exception {
        plugin = this;
        super.start(context);
    }
    
    @Override
    public void stop(final BundleContext context) throws Exception {
        super.stop(context);
        plugin = null;
    }
    
    public static PMDPlugin getDefault() {
        return plugin;
    }
    
    /**
     * Logs an error message to the platform, i.e. it will be visible in the Error Log view and distributed to the log
     * listeners.
     * 
     * @return An error status containing the error message.
     */
    public IStatus error(final String message) {
        return log(IStatus.ERROR, message, null);
    }
    
    /**
     * Logs an error message and a {@code Throwable} to the platform, i.e. it will be visible in the Error Log view and
     * distributed to the log listeners.
     * 
     * @return An error status containing the error message and throwable.
     */
    public IStatus error(final String message, final Throwable throwable) {
        return log(IStatus.ERROR, message, throwable);
    }
    
    /**
     * Logs a warning message and a {@code Throwable} to the platform, i.e. it will be visible in the Error Log view and
     * distributed to the log listeners.
     * 
     * @return A warning status containing the error message and throwable.
     */
    public IStatus warn(final String message, final Throwable throwable) {
        return log(IStatus.WARNING, message, throwable);
    }
    
    private IStatus log(final int severity, final String message, final Throwable throwable) {
        final IStatus status = new Status(severity, ID, message, throwable);
        getLog().log(status);
        return status;
    }
    
}
