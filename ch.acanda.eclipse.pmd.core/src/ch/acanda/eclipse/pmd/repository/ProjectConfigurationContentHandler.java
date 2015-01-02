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

package ch.acanda.eclipse.pmd.repository;

import static ch.acanda.eclipse.pmd.repository.ProjectModelSerializer.ATTRIBUTE_NAME_NAME;
import static ch.acanda.eclipse.pmd.repository.ProjectModelSerializer.ATTRIBUTE_NAME_REF;
import static ch.acanda.eclipse.pmd.repository.ProjectModelSerializer.ATTRIBUTE_NAME_REFCONTEXT;
import static ch.acanda.eclipse.pmd.repository.ProjectModelSerializer.ATTRIBUTE_VALUE_FILESYSTEM;
import static ch.acanda.eclipse.pmd.repository.ProjectModelSerializer.ATTRIBUTE_VALUE_PROJECT;
import static ch.acanda.eclipse.pmd.repository.ProjectModelSerializer.ATTRIBUTE_VALUE_REMOTE;
import static ch.acanda.eclipse.pmd.repository.ProjectModelSerializer.ATTRIBUTE_VALUE_WORKSPACE;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import ch.acanda.eclipse.pmd.domain.Location;
import ch.acanda.eclipse.pmd.domain.LocationContext;
import ch.acanda.eclipse.pmd.domain.ProjectModel;
import ch.acanda.eclipse.pmd.domain.RuleSetModel;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * @see ProjectModelSerializer
 *
 * @author Philip Graf
 */
final class ProjectConfigurationContentHandler extends DefaultHandler {

    private boolean isPMDEnabled;
    private final Builder<RuleSetModel> builder = ImmutableSet.builder();

    public ProjectModel getProjectModel(final String projectName) {
        final ProjectModel model = new ProjectModel(projectName);
        model.setPMDEnabled(isPMDEnabled);
        model.setRuleSets(builder.build());
        return model;
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes)
            throws SAXException {
        switch (localName) {
            case ProjectModelSerializer.TAG_NAME_ANALYSIS:
                isPMDEnabled = isPMDEnabled(attributes);
                break;

            case ProjectModelSerializer.TAG_NAME_RULESET:
                builder.add(createRuleSet(attributes));
                break;

            default:
                break;
        }
    }

    private boolean isPMDEnabled(final Attributes attributes) {
        return Boolean.valueOf(attributes.getValue(ProjectModelSerializer.ATTRIBUTE_NAME_ENABLED));
    }

    private RuleSetModel createRuleSet(final Attributes attributes) {
        final LocationContext context = getContext(attributes.getValue(ATTRIBUTE_NAME_REFCONTEXT));
        final Location location = new Location(attributes.getValue(ATTRIBUTE_NAME_REF), context);
        return new RuleSetModel(attributes.getValue(ATTRIBUTE_NAME_NAME), location);
    }

    private LocationContext getContext(final String refcontext) {
        final LocationContext context;
        switch (refcontext) {
            case ATTRIBUTE_VALUE_PROJECT:
                context = LocationContext.PROJECT;
                break;

            case ATTRIBUTE_VALUE_WORKSPACE:
                context = LocationContext.WORKSPACE;
                break;

            case ATTRIBUTE_VALUE_FILESYSTEM:
                context = LocationContext.FILESYSTEM;
                break;

            case ATTRIBUTE_VALUE_REMOTE:
                context = LocationContext.REMOTE;
                break;

            default:
                throw new IllegalArgumentException(refcontext + " is not a valid value for the attribute 'refcontext'.");

        }
        return context;
    }

}
