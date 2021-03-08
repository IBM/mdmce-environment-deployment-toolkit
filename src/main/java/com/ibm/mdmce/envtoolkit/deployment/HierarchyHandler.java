/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment;

import com.ibm.mdmce.envtoolkit.deployment.model.*;

import java.io.File;
import java.util.*;

/**
 * Marshals information from the Hierarchies.csv and handles transformation to relevant XML(s).
 * The expected file format is defined within the Hierarchy class.
 *
 * @see Hierarchy
 */
public class HierarchyHandler extends BasicEntityHandler {

	public HierarchyHandler(String sInputFilePath, String sVersion, TemplateParameters tp, String sEncoding) {
		super(Hierarchy.getInstance(),
				"Hierarchies.csv",
				"HIERARCHY" + File.separator + "HIERARCHY.xml",
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
		
		Hierarchy hierarchy = (Hierarchy) oEntity;
		bValid = validateExists(hierarchy.getSpecName(), Spec.class.getName(), hierarchy.getName()) && bValid;
		bValid = validateExists(hierarchy.getAcg(), ACG.class.getName(), hierarchy.getName()) && bValid;
		if (!hierarchy.getUserDefinedCoreAttrGroup().equals(""))
			bValid = validateExists(hierarchy.getUserDefinedCoreAttrGroup(), AttrCollection.class.getName(), hierarchy.getName()) && bValid;
				
		Spec spec = (Spec) getFromCache(hierarchy.getSpecName(), Spec.class.getName(), false, false);
		if (spec != null) {
			
			String sDisplayAttr = hierarchy.getDisplayAttribute().replace(hierarchy.getSpecName() + "/", "");
			Spec.Attribute attr = spec.getAttributes().get(sDisplayAttr);
			bValid = (attr != null) && bValid;
			if (attr == null) {
				EnvironmentHandler.logger.warning(". . . WARNING (" + hierarchy.getName() + "): Unable to find attribute - " + hierarchy.getSpecName() + "/" + sDisplayAttr);
			} else if (!attr.isIndexed()) {
				EnvironmentHandler.logger.warning(". . . WARNING (" + hierarchy.getName() + "): Display attribute (" + hierarchy.getSpecName() + "/" + sDisplayAttr + ") is not indexed.");
			}
			
			String sPathAttr = hierarchy.getPathAttribute().replace(hierarchy.getSpecName() + "/", "");
			attr = spec.getAttributes().get(sPathAttr);
			bValid = (attr != null) && bValid;
			if (attr == null) {
				EnvironmentHandler.logger.warning(". . . WARNING (" + hierarchy.getName() + "): Unable to find attribute - " + hierarchy.getSpecName() + "/" + sPathAttr);
			} else if (!attr.isIndexed() || attr.getMin() != 1) {
				EnvironmentHandler.logger.warning(". . . WARNING (" + hierarchy.getName() + "): Path attribute (" + hierarchy.getSpecName() + "/" + sDisplayAttr + ") is not indexed or not set as mandatory (minimum occurrence of 1).");
			}
			
		}

		for (Map.Entry<String, String> entry : hierarchy.getScriptTypeToName().entrySet()) {
			String sScriptType = entry.getKey();
			String sScriptName = entry.getValue();
			if (sScriptType.equals("ENTRY_BUILD_SCRIPT")) {
				bValid = validateExists("/scripts/entry_build/" + sScriptName, Script.class.getName(), hierarchy.getName()) && bValid;
			} else if (sScriptType.contains("SCRIPT_NAME")) {
				bValid = validateExists("/scripts/category_tree/" + sScriptName, Script.class.getName(), hierarchy.getName()) && bValid;
			}
		}
		
		return bValid;
		
	}

}
