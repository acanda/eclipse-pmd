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

/**
 * eclipse-pmd up to version 0.7 used to store its settings in the Eclipse preferences store. Since version 0.8 the
 * settings are stored in files in the respective project so they can be shared. This package contains the classes that
 * can read the version 0.7 settings and convert them to version 0.8 settings. They are all marked as 'deprecated' as
 * they are not intended to be used fo anything else.
 * 
 * @author Philip Graf
 */
package ch.acanda.eclipse.pmd.v07tov08;