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

package ch.acanda.eclipse.pmd.ui.model;


/**
 * @author Philip Graf
 */
public final class ValidationUtil {
    
    private ValidationUtil() {
        // hide constructor of utility class
    }

    /**
     * Adds a validation error to the validation result if {@code value} is {@code null} or a blank string.
     * 
     * @return {@code true} if a validation error was added to the validation result.
     */
    public static boolean errorIfBlank(final String propertyName, final String value, final String message, final ValidationResult result) {
        if (value == null || value.trim().length() == 0) {
            result.add(new ValidationProblem(propertyName, ValidationProblem.Severity.ERROR, message));
            return true;
        }
        return false;
    }
    
}
