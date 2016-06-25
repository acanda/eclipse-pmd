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

package ch.acanda.eclipse.pmd.java.resolution;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator2;
import org.osgi.framework.Version;

import ch.acanda.eclipse.pmd.marker.WrappingPMDMarker;

import com.google.common.collect.ImmutableList;

/**
 * Creates resolutions for a Java PMD marker.
 *
 * @author Philip Graf
 */
public class PMDMarkerResolutionGenerator implements IMarkerResolutionGenerator2 {

    private final JavaQuickFixGenerator quickFixGenerator = new JavaQuickFixGenerator();

    @Override
    public boolean hasResolutions(final IMarker marker) {
        final JavaQuickFixContext context = new JavaQuickFixContext(getCompilerCompliance(marker));
        return quickFixGenerator.hasQuickFixes(new WrappingPMDMarker(marker), context);
    }

    @Override
    public IMarkerResolution[] getResolutions(final IMarker marker) {
        final JavaQuickFixContext context = new JavaQuickFixContext(getCompilerCompliance(marker));
        final ImmutableList<IMarkerResolution> quickFixes = quickFixGenerator.getQuickFixes(new WrappingPMDMarker(marker), context);
        return quickFixes.toArray(new IMarkerResolution[quickFixes.size()]);
    }

    private Version getCompilerCompliance(final IMarker marker) {
        final IJavaProject project = JavaCore.create(marker.getResource().getProject());
        final String compilerCompliance = project.getOption(JavaCore.COMPILER_COMPLIANCE, true);
        return new Version(compilerCompliance);
    }

}
