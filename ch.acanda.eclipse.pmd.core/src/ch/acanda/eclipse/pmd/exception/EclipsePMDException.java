// =====================================================================
//
// Copyright (C) 2012 - 2017, Philip Graf
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// =====================================================================

package ch.acanda.eclipse.pmd.exception;

/**
 * {@code EclipsePMDException} is the superclass of all exceptions that can be thrown by the eclipse-pmd plugin.
 * 
 * @author Philip Graf
 */
public class EclipsePMDException extends RuntimeException {
    
    private static final long serialVersionUID = 4312111836815540119L;
    
    public EclipsePMDException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
}
