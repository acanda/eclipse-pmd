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

package ch.acanda.eclipse.pmd.ui.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Base class for a view model.
 * 
 * @author Philip Graf
 */
public abstract class ViewModel {
    public static final String DIRTY_PROPERTY = "dirty";
    
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private boolean isDirty;
    
    public void addPropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }
    
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
    }
    
    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
    
    protected <T> void setProperty(final String propertyName, final T oldValue, final T newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
        final boolean newIsDirty = updateDirty();
        propertyChangeSupport.firePropertyChange(DIRTY_PROPERTY, isDirty, isDirty = newIsDirty);
    }
    
    protected abstract boolean updateDirty();
    
    public void addDirtyChangeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(DIRTY_PROPERTY, listener);
    }
    
    public void removeDirtyChangeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(DIRTY_PROPERTY, listener);
    }
    
    protected abstract void reset();
}
