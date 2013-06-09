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

package ch.acanda.eclipse.pmd.marker.resolution;

import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;

import ch.acanda.eclipse.pmd.exception.EclipsePMDException;
import ch.acanda.eclipse.pmd.marker.PMDMarker;
import ch.acanda.eclipse.pmd.marker.resolution.java.SuppressWarningsQuickFix;

/**
 * Creates resolutions for a PMD marker.
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
            resolutions = createDefaultResolutions(isJavaMarker(marker), pmdMarker);
        }
        return resolutions;
    }
    
    /**
     * Cleans the rule id by removing characters not suitable for a package or class name.
     */
    private String clean(final String ruleId) {
        return ruleId.replace(" ", "");
    }
    
    private IMarkerResolution[] loadQuickFix(final PMDMarker marker) {
        IMarkerResolution[] resolutions;
        final String ruleId = marker.getRuleId();
        final String className = PMDMarkerResolutionGenerator.class.getPackage().getName() + "." + clean(ruleId) + "QuickFix";
        try {
            @SuppressWarnings("unchecked")
            final Class<? extends IMarkerResolution> quickFixClass = (Class<? extends IMarkerResolution>) Class.forName(className);
            final IMarkerResolution quickFix = quickFixClass.getConstructor(PMDMarker.class).newInstance(marker);
            if (isJavaQuickFix(quickFixClass)) {
                resolutions = new IMarkerResolution[] { quickFix, new SuppressWarningsQuickFix(marker) };
            } else {
                resolutions = new IMarkerResolution[] { quickFix };
            }
            
        } catch (final ClassNotFoundException e) {
            // the quick fix class does not exist
            resolutions = createDefaultResolutions(isJavaMarker(ruleId), marker);
            
        } catch (final SecurityException | ReflectiveOperationException e) {
            // the quick fix class does exist but it is not correctly implemented.
            throw new EclipsePMDException("Quick fix class " + className + " is not correctly implemented", e);
        }
        return resolutions;
    }
    
    private boolean isJavaMarker(final String ruleId) {
        return ruleId.startsWith("java.");
    }
    
    private boolean isJavaMarker(final IMarker marker) {
        return "java".equals(marker.getResource().getFileExtension());
    }
    
    private boolean isJavaQuickFix(final Class<? extends IMarkerResolution> quickFixClass) {
        return quickFixClass.getPackage().getName().startsWith(SuppressWarningsQuickFix.class.getPackage().getName());
    }
    
    private IMarkerResolution[] createDefaultResolutions(final boolean isJavaMarker, final PMDMarker marker) {
        if (isJavaMarker) {
            return new IMarkerResolution[] { new SuppressWarningsQuickFix(marker) };
        }
        return new IMarkerResolution[0];
    }
}
