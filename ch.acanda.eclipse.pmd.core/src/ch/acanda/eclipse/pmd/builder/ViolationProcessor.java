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

package ch.acanda.eclipse.pmd.builder;

import java.io.IOException;
import java.nio.charset.Charset;

import net.sourceforge.pmd.RuleViolation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

import ch.acanda.eclipse.pmd.marker.MarkerUtil;

import com.google.common.collect.Iterables;
import com.google.common.io.Files;

/**
 * Processes the rule violations found by a PMD analysis.
 *
 * @author Philip Graf
 */
public class ViolationProcessor {

    public void annotate(final IFile file, final Iterable<RuleViolation> violations) throws CoreException, IOException {
        MarkerUtil.removeAllMarkers(file);
        if (!Iterables.isEmpty(violations)) {
            final String content = Files.toString(file.getRawLocation().toFile(), Charset.forName(file.getCharset()));
            for (final RuleViolation violation : violations) {
                MarkerUtil.addMarker(file, content, violation);
            }
        }
    }

}
