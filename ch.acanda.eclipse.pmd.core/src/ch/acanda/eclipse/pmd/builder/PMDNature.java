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

package ch.acanda.eclipse.pmd.builder;

import java.util.ArrayList;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

import ch.acanda.eclipse.pmd.marker.MarkerUtil;

import com.google.common.collect.Lists;

/**
 * Project nature of projects that have PMD enabled.
 * 
 * @author Philip Graf
 */
public class PMDNature implements IProjectNature {
    
    public static final String ID = "ch.acanda.eclipse.pmd.builder.PMDNature";
    
    private IProject project;
    
    @Override
    public void configure() throws CoreException {
        final IProjectDescription desc = project.getDescription();
        final ICommand[] commands = desc.getBuildSpec();
        
        for (int i = 0; i < commands.length; ++i) {
            if (commands[i].getBuilderName().equals(PMDBuilder.ID)) {
                return;
            }
        }
        
        final ICommand[] newCommands = new ICommand[commands.length + 1];
        System.arraycopy(commands, 0, newCommands, 0, commands.length);
        final ICommand command = desc.newCommand();
        command.setBuilderName(PMDBuilder.ID);
        newCommands[newCommands.length - 1] = command;
        desc.setBuildSpec(newCommands);
        project.setDescription(desc, null);
    }
    
    @Override
    public void deconfigure() throws CoreException {
        final IProjectDescription description = getProject().getDescription();
        final ICommand[] commands = description.getBuildSpec();
        for (int i = 0; i < commands.length; ++i) {
            if (commands[i].getBuilderName().equals(PMDBuilder.ID)) {
                final ICommand[] newCommands = new ICommand[commands.length - 1];
                System.arraycopy(commands, 0, newCommands, 0, i);
                System.arraycopy(commands, i + 1, newCommands, i,
                        commands.length - i - 1);
                description.setBuildSpec(newCommands);
                project.setDescription(description, null);
                return;
            }
        }
    }
    
    @Override
    public IProject getProject() {
        return project;
    }
    
    @Override
    public void setProject(final IProject project) {
        this.project = project;
    }
    
    /**
     * Adds the PMD nature to a project.
     */
    public static void addTo(final IProject project) throws CoreException {
        if (!project.hasNature(ID)) {
            final IProjectDescription description = project.getDescription();
            final ArrayList<String> natureIds = Lists.newArrayList(description.getNatureIds());
            natureIds.add(ID);
            description.setNatureIds(natureIds.toArray(new String[natureIds.size()]));
            project.setDescription(description, null);
            MarkerUtil.removeAllMarkers(project);
        }
    }
    
    /**
     * Removes the PMD nature from a project.
     */
    public static void removeFrom(final IProject project) throws CoreException {
        if (project.hasNature(ID)) {
            final IProjectDescription description = project.getDescription();
            final ArrayList<String> natureIds = Lists.newArrayList(description.getNatureIds());
            natureIds.remove(ID);
            description.setNatureIds(natureIds.toArray(new String[natureIds.size()]));
            project.setDescription(description, null);
            MarkerUtil.removeAllMarkers(project);
        }
    }
    
}
