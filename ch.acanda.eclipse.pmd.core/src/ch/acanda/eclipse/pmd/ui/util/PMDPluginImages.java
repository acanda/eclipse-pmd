// =====================================================================
//
// Copyright (C) 2012, Philip Graf
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// =====================================================================

package ch.acanda.eclipse.pmd.ui.util;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.graphics.Image;

import ch.acanda.eclipse.pmd.PMDPlugin;

/**
 * Manages the resources of the PMD plug-in's images.
 * 
 * @author Philip Graf
 */
public class PMDPluginImages {
    
    public static final ImageDescriptor QUICKFIX_ADD = PMDPlugin.getImageDescriptor("icons/quickfix_add.gif");
    public static final ImageDescriptor QUICKFIX_REMOVE = PMDPlugin.getImageDescriptor("icons/quickfix_remove.gif");
    public static final ImageDescriptor QUICKFIX_CHANGE = PMDPlugin.getImageDescriptor("icons/quickfix_change.gif");
    
    private static final ResourceManager manager = new LocalResourceManager(JFaceResources.getResources());
    
    public static Image get(final ImageDescriptor descriptor) {
        return (Image) manager.get(descriptor);
    }
    
    public static void dispose() {
        manager.dispose();
    }
    
}
