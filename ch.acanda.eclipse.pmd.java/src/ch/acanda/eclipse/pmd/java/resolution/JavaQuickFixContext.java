// =====================================================================
//
// Copyright (C) 2012 - 2020, Philip Graf
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// =====================================================================

package ch.acanda.eclipse.pmd.java.resolution;

import org.osgi.framework.Version;

final class JavaQuickFixContext {

    private final Version compilerCompliance;

    public JavaQuickFixContext(final Version compilerCompliance) {
        this.compilerCompliance = compilerCompliance;
    }

    public Version getCompilerCompliance() {
        return compilerCompliance;
    }

}
