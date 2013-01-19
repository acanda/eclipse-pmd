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

package ch.acanda.eclipse.pmd.ui.model;

/**
 * @author Philip Graf
 */
public class ValidationProblem {
    
    private final String propertyName;
    private final Severity severity;
    private final String message;
    
    public enum Severity {
        ERROR, WARNING
    };
    
    public ValidationProblem(final String propertyName, final Severity severity, final String message) {
        assert propertyName != null && severity != null && message != null;
        this.propertyName = propertyName;
        this.severity = severity;
        this.message = message;
    }
    
    public String getPropertyName() {
        return propertyName;
    }
    
    public Severity getSeverity() {
        return severity;
    }
    
    public String getMessage() {
        return message;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (message == null ? 0 : message.hashCode());
        result = prime * result + (propertyName == null ? 0 : propertyName.hashCode());
        result = prime * result + (severity == null ? 0 : severity.hashCode());
        return result;
    }
    
    @Override
    @SuppressWarnings("PMD.NPathComplexity")
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ValidationProblem other = (ValidationProblem) obj;
        if (message == null) {
            if (other.message != null) {
                return false;
            }
        } else if (!message.equals(other.message)) {
            return false;
        }
        if (propertyName == null) {
            if (other.propertyName != null) {
                return false;
            }
        } else if (!propertyName.equals(other.propertyName)) {
            return false;
        }
        if (severity != other.severity) {
            return false;
        }
        return true;
    }
    
}
