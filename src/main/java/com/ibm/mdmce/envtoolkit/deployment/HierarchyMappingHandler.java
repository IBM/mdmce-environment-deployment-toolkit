/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment;

import com.ibm.mdmce.envtoolkit.deployment.model.BasicEntity;
import com.ibm.mdmce.envtoolkit.deployment.model.Hierarchy;
import com.ibm.mdmce.envtoolkit.deployment.model.HierarchyMapping;
import com.ibm.mdmce.envtoolkit.deployment.model.TemplateParameters;

import java.io.*;

/**
 * Marshals information from the HierarchyMappings.csv and handles transformation to relevant XML(s).
 * The expected file format is defined within the HierarchyMapping class.
 *
 * @see HierarchyMapping
 */
public class HierarchyMappingHandler extends BasicEntityHandler {
	
	public HierarchyMappingHandler(String sInputFilePath, String sVersion, TemplateParameters tp, String sEncoding) {
		super(HierarchyMapping.getInstance(),
				"HierarchyMappings.csv",
				"HIERARCHY_MAPS" + File.separator + "HIERARCHY_MAPS.xml",
				sInputFilePath,
				sVersion,
				tp,
				sEncoding);
		initialize(sInputFilePath, sEncoding, tp);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean validate(BasicEntity oEntity) {
		boolean bValid = true;
		HierarchyMapping hierarchyMap = (HierarchyMapping) oEntity;
		bValid = validateExists(hierarchyMap.getSourceHierarchy(), Hierarchy.class.getName(), hierarchyMap.getUniqueId()) && bValid;
		bValid = validateExists(hierarchyMap.getDestinationHierarchy(), Hierarchy.class.getName(), hierarchyMap.getUniqueId()) && bValid;
		return bValid;
	}

}
