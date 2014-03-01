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

import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;

import ch.acanda.eclipse.pmd.exception.EclipsePMDException;
import ch.acanda.eclipse.pmd.marker.PMDMarker;

/**
 * Creates resolutions for a Java PMD marker.
 *
 * @author Philip Graf
 */
public class PMDMarkerResolutionGenerator implements IMarkerResolutionGenerator {

    @Override
    public IMarkerResolution[] getResolutions(final IMarker marker) {
        final IMarkerResolution[] resolutions;
        final PMDMarker pmdMarker = new PMDMarker(marker);
        final String ruleId = pmdMarker.getRuleId();
        if (ruleId != null) {
            resolutions = loadQuickFix(pmdMarker);
        } else {
            resolutions = createDefaultResolutions(pmdMarker);
        }
        return resolutions;
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
            resolutions = new IMarkerResolution[] { quickFix, new SuppressWarningsQuickFix(marker) };

        } catch (final ClassNotFoundException e) {
            // the quick fix class does not exist
            resolutions = createDefaultResolutions(marker);

        } catch (final SecurityException | ReflectiveOperationException e) {
            // the quick fix class does exist but it is not correctly implemented.
            throw new EclipsePMDException("Quick fix class " + className + " is not correctly implemented", e);
        }
        return resolutions;
    }

    private IMarkerResolution[] createDefaultResolutions(final PMDMarker marker) {
        return new IMarkerResolution[] { new SuppressWarningsQuickFix(marker) };
    }
}
