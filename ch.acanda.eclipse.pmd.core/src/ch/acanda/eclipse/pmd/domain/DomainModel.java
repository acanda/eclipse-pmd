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

package ch.acanda.eclipse.pmd.domain;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * @author Philip Graf
 */
public class DomainModel {

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

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
    }

    protected <T> void addPropertyElement(final String propertyName, final T addedValue) {
        if (addedValue != null) {
            propertyChangeSupport.firePropertyChange(new AddElementPropertyChangeEvent(this, propertyName, addedValue));
        }
    }

    protected <T> void removePropertyElement(final String propertyName, final T removedValue) {
        if (removedValue != null) {
            propertyChangeSupport.firePropertyChange(new RemoveElementPropertyChangeEvent(this, propertyName, removedValue));
        }
    }

    public static final class AddElementPropertyChangeEvent extends PropertyChangeEvent {

        private static final long serialVersionUID = 0L;

        AddElementPropertyChangeEvent(final Object source, final String propertyName, final Object addedValue) {
            super(source, propertyName, null, addedValue);
        }

        /**
         * @return The added element. Never returns {@code null}.
         */
        public Object getAddedElement() {
            return getNewValue();
        }

    }

    public static final class RemoveElementPropertyChangeEvent extends PropertyChangeEvent {

        private static final long serialVersionUID = 0L;

        RemoveElementPropertyChangeEvent(final Object source, final String propertyName, final Object removedValue) {
            super(source, propertyName, removedValue, null);
        }

        /**
         * @return The removed element. Never returns {@code null}.
         */
        public Object getRemovedElement() {
            return getOldValue();
        }

    }

}
