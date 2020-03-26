/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment;

import com.ibm.mdmce.envtoolkit.deployment.model.*;

import java.io.*;
import java.util.*;

/**
 * Marshals information from the Organizations.csv and handles transformation to relevant XML(s).
 * The expected file format is defined within the Organization class.
 * 
 * @see Organization
 */
public class OrganizationHandler extends BasicEntityHandler {

	public OrganizationHandler(String sInputFilePath, String sVersion, TemplateParameters tp, String sEncoding) {
		super(Organization.getInstance(),
				"Organizations.csv",
				"ORG_HIERARCHY" + File.separator + "ORG_HIERARCHY.xml",
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
	protected <T extends BasicEntity> void addLineToCache(List<List<String>> alReplacedTokens) {
		for (List<String> aReplacedTokens : alReplacedTokens) {
			Organization instance = entity.createInstance(aReplacedTokens);
			addToCache(instance.getUniqueId(), instance);
			EnvironmentHandler.addEntityToCacheWithType(instance.getUniqueId(), Hierarchy.class.getName(), instance);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean validate(BasicEntity oEntity) {
		
		boolean bValid = true;
		
		Organization org = (Organization) oEntity;
		bValid = validateExists(org.getSpecName(), Spec.class.getName(), org.getName()) && bValid;
		bValid = validateExists(org.getAcg(), ACG.class.getName(), org.getName()) && bValid;
		if (!org.getUserDefinedCoreAttrGroup().equals(""))
			bValid = validateExists(org.getUserDefinedCoreAttrGroup(), AttrCollection.class.getName(), org.getName()) && bValid;
				
		Spec spec = (Spec) getFromCache(org.getSpecName(), Spec.class.getName(), false, false);
		if (spec != null) {
			
			String sDisplayAttr = org.getDisplayAttribute().replace(org.getSpecName() + "/", "");
			Spec.Attribute attr = spec.getAttributes().get(sDisplayAttr);
			bValid = (attr != null) && bValid;
			if (attr == null) {
				err.println(". . . WARNING (" + org.getName() + "): Unable to find attribute - " + org.getSpecName() + "/" + sDisplayAttr);
			} else if (!attr.isIndexed()) {
				err.println(". . . WARNING (" + org.getName() + "): Display attribute (" + org.getSpecName() + "/" + sDisplayAttr + ") is not indexed.");
			}
			
			String sPathAttr = org.getPathAttribute().replace(org.getSpecName() + "/", "");
			attr = spec.getAttributes().get(sPathAttr);
			bValid = (attr != null) && bValid;
			if (attr == null) {
				err.println(". . . WARNING (" + org.getName() + "): Unable to find attribute - " + org.getSpecName() + "/" + sPathAttr);
			} else if (!attr.isIndexed() || attr.getMin() != 1) {
				err.println(". . . WARNING (" + org.getName() + "): Path attribute (" + org.getSpecName() + "/" + sDisplayAttr + ") is not indexed or not set as mandatory (minimum occurrence of 1).");
			}
			
		}

		for (Map.Entry<String, String> entry : org.getScriptTypeToName().entrySet()) {
			String sScriptType = entry.getKey();
			String sScriptName = entry.getValue();
			if (sScriptType.equals("ENTRY_BUILD_SCRIPT")) {
				bValid = validateExists("/scripts/entry_build/" + sScriptName, Script.class.getName(), org.getName()) && bValid;
			} else if (sScriptType.contains("SCRIPT_NAME")) {
				bValid = validateExists("/scripts/category_tree/" + sScriptName, Script.class.getName(), org.getName()) && bValid;
			}
		}

		return bValid;
		
	}
	
}
