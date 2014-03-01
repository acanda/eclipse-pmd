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

package ch.acanda.eclipse.pmd.java.resolution;

import java.util.Arrays;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;
import org.osgi.framework.Version;

import ch.acanda.eclipse.pmd.exception.EclipsePMDException;
import ch.acanda.eclipse.pmd.marker.PMDMarker;

/**
 * Creates resolutions for a Java PMD marker.
 *
 * @author Philip Graf
 */
public class PMDMarkerResolutionGenerator implements IMarkerResolutionGenerator {

    private static final Version JAVA_5 = new Version(1, 5, 0);
    private static final Version JAVA_8 = new Version(1, 8, 0);

    @Override
    public IMarkerResolution[] getResolutions(final IMarker marker) {
        IMarkerResolution[] resolutions = new IMarkerResolution[0];
        final Version compilerCompliance = getCompilerCompliance(marker);
        if (compilerCompliance.compareTo(JAVA_8) < 0) {
            final PMDMarker pmdMarker = new PMDMarker(marker);
            final String ruleId = pmdMarker.getRuleId();
            if (ruleId != null) {
                resolutions = loadQuickFix(pmdMarker);
            }
            resolutions = addDefaultResolutions(resolutions, pmdMarker, compilerCompliance);
        }
        return resolutions;
    }

    private Version getCompilerCompliance(final IMarker marker) {
        final IJavaProject project = JavaCore.create(marker.getResource().getProject());
        final String compilerCompliance = project.getOption(JavaCore.COMPILER_COMPLIANCE, true);
        return new Version(compilerCompliance);
    }

    private IMarkerResolution[] addDefaultResolutions(final IMarkerResolution[] resolutions, final PMDMarker marker,
            final Version compilerCompliance) {
        if (compilerCompliance.compareTo(JAVA_5) < 0) {
            return resolutions;
        }
        final IMarkerResolution[] extendedResolutions = Arrays.copyOf(resolutions, resolutions.length + 1);
        extendedResolutions[resolutions.length] = new SuppressWarningsQuickFix(marker);
        return extendedResolutions;
    }

    /**
     * Cleans the rule id by removing the "java." prefix and characters not suitable for a package or class name.
     */
    private String clean(final String ruleId) {
        return ruleId.substring(5).replace(" ", "");
    }

    private IMarkerResolution[] loadQuickFix(final PMDMarker marker) {
        IMarkerResolution[] resolutions;
        final String ruleId = marker.getRuleId();
        final String className = PMDMarkerResolutionGenerator.class.getPackage().getName() + "." + clean(ruleId) + "QuickFix";
        try {
            @SuppressWarnings("unchecked")
            final Class<? extends IMarkerResolution> quickFixClass = (Class<? extends IMarkerResolution>) Class.forName(className);
            final IMarkerResolution quickFix = quickFixClass.getConstructor(PMDMarker.class).newInstance(marker);
            resolutions = new IMarkerResolution[] { quickFix };

        } catch (final ClassNotFoundException e) {
            // the quick fix class does not exist
            resolutions = new IMarkerResolution[0];

        } catch (final SecurityException | ReflectiveOperationException e) {
            throw new EclipsePMDException("Quick fix class " + className + " is not correctly implemented", e);
        }
        return resolutions;
    }

}
