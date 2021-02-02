/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment;

import com.ibm.mdmce.envtoolkit.deployment.model.*;

import java.io.File;
import java.util.*;

/**
 * Marshals information from the Catalogs.csv and handles transformation to relevant XML(s).
 * The expected file format is defined within the Catalog class.
 * 
 * @see Catalog
 */
public class CatalogHandler extends BasicEntityHandler {
	
	public CatalogHandler(String sInputFilePath, String sVersion, TemplateParameters tp, String sEncoding) {
		super(Catalog.getInstance(),
				"Catalogs.csv",
				"CATALOG" + File.separator + "CATALOG.xml",
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
		
		Catalog ctg = (Catalog) oEntity;
		bValid = validateExists(ctg.getSpecName(), Spec.class.getName(), ctg.getName()) && bValid;
		bValid = validateExists(ctg.getPrimaryHierarchy(), Hierarchy.class.getName(), ctg.getName()) && bValid;
		bValid = validateExists(ctg.getAcg(), ACG.class.getName(), ctg.getName()) && bValid;
		if (!ctg.getUserDefinedCoreAttrGroup().equals(""))
			bValid = validateExists(ctg.getUserDefinedCoreAttrGroup(), AttrCollection.class.getName(), ctg.getName()) && bValid;

		for (String sHierarchyName : ctg.getSecondaryHierarchies()) {
			if (!sHierarchyName.equals(""))
				bValid = validateExists(sHierarchyName, Hierarchy.class.getName(), ctg.getName()) && bValid;
		}
		
		Spec spec = (Spec) getFromCache(ctg.getSpecName(), Spec.class.getName(), false, false);
		if (spec != null) {
			
			String sDisplayAttr = ctg.getDisplayAttribute().replace(ctg.getSpecName() + "/", "");
			Spec.Attribute attr = spec.getAttributes().get(sDisplayAttr);
			bValid = (attr != null) && bValid;
			if (attr == null) {
				err.println("WARNING (" + ctg.getName() + "): Unable to find attribute - " + ctg.getSpecName() + "/" + sDisplayAttr);
				bValid = false;
			} else if (!attr.isIndexed()) {
				err.println(". . . WARNING (" + ctg.getName() + "): Display attribute (" + ctg.getSpecName() + "/" + sDisplayAttr + ") is not indexed.");
				bValid = false;
			}

			for (Map.Entry<String, String> entry : ctg.getLinkSpecPathToDestinationCatalog().entrySet()) {
				//String sLinkAttrPath = entry.getKey().replace(ctg.getSpecName() + "/", "");//RS 20151201 if attribute name ends with spec name, it is removed. ex : spec/attributeofspec/ --> /attributeof/ 
				//as we just want to remove the spec name at the beginning of the string, this is better:				
				String sLinkAttrPath = entry.getKey();
				if(sLinkAttrPath.startsWith(ctg.getSpecName() + "/")){
					sLinkAttrPath = sLinkAttrPath.substring((ctg.getSpecName() + "/").length());
				}				
				String sDestinationCtg = entry.getValue();
				attr = spec.getAttributes().get(sLinkAttrPath);
				bValid = (attr != null) && bValid;
				if (attr == null)
					err.println("WARNING (" + ctg.getName() + "): Unable to find attribute - " + ctg.getSpecName() + "/" + sLinkAttrPath);
				bValid = validateExists(sDestinationCtg, Catalog.class.getName(), ctg.getName()) && bValid;
			}

		}

		for (Map.Entry<String, String> entry : ctg.getScriptTypeToName().entrySet()) {
			String sScriptType = entry.getKey();
			String sScriptName = entry.getValue();
			if (sScriptType.equals("ENTRY_BUILD_SCRIPT")) {
				bValid = validateExists("/scripts/entry_build/" + sScriptName, Script.class.getName(), ctg.getName()) && bValid;
			} else if (sScriptType.contains("SCRIPT_NAME")) {
				bValid = validateExists("/scripts/catalog/" + sScriptName, Script.class.getName(), ctg.getName()) && bValid;
			}
		}

		for (Map.Entry<String, Map<String, List<String>>> entry : ctg.getLocationHierarchyToAttributeCollections().entrySet()) {
			String sHierarchyName = entry.getKey();
			Map<String, List<String>> attrColMap = entry.getValue();
			bValid = validateExists(sHierarchyName, Hierarchy.class.getName(), ctg.getName()) && bValid;
			if ( !(ctg.getPrimaryHierarchy().equals(sHierarchyName) || ctg.getSecondaryHierarchies().contains(sHierarchyName)) ) {
				err.println("WARNING (" + ctg.getName() + "): Location hierarchy (" + sHierarchyName + ") not associated as a primary or secondary hierarchy to the catalogue (" + ctg.getName() + ")");
				bValid = false;
			}
			for (Map.Entry<String, List<String>> specEntry : attrColMap.entrySet()) {
				String sSpecName = specEntry.getKey();
				List<String> attrCollections = specEntry.getValue();
				bValid = validateExists(sSpecName, Spec.class.getName(), ctg.getName()) && bValid;
				for (String attrCollection : attrCollections) {
					bValid = validateExists(attrCollection, AttrCollection.class.getName(), ctg.getName()) && bValid;
				}
			}
		}

		return bValid;
		
	}

}
