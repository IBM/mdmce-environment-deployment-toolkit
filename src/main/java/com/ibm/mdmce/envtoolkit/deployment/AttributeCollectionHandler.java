/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment;

import java.io.File;
import java.util.*;

import com.ibm.mdmce.envtoolkit.deployment.model.AttrCollection;
import com.ibm.mdmce.envtoolkit.deployment.model.BasicEntity;
import com.ibm.mdmce.envtoolkit.deployment.model.Spec;
import com.ibm.mdmce.envtoolkit.deployment.model.TemplateParameters;

/**
 * Marshals information from the AttrCollections.csv and handles transformation to relevant XML(s).
 * The expected file format is defined within the AttributeCollection class.
 *
 * @see AttrCollection
 */
public class AttributeCollectionHandler extends BasicEntityHandler {
	
	public AttributeCollectionHandler(String sInputFilePath, String sVersion, TemplateParameters tp, String sEncoding) {
		super(AttrCollection.getInstance(),
				"AttrCollections.csv",
				"ATTRIBUTE_COLS" + File.separator + "ATTRIBUTE_COLS.xml",
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
		
		AttrCollection attrCol = (AttrCollection) oEntity;

		for (Map.Entry<String, List<String>> entry : attrCol.getSpecsToAttributes().entrySet()) {
			String sSpecName = entry.getKey();
			List<String> aAttrs = entry.getValue();
			bValid = validateExists(sSpecName, Spec.class.getName(), attrCol.getName()) && bValid;
			if (!attrCol.getDynamicSpecs().contains(sSpecName)) {
				Spec spec = (Spec)getFromCache(sSpecName, Spec.class.getName(), false, false);
				if (spec != null) {
					for (String sAttr : aAttrs) {
						Spec.Attribute attr = spec.getAttributes().get(sAttr);
						if (attr == null) {
							bValid = false;
							EnvironmentHandler.logger.warning(". . . WARNING (" + attrCol.getName() + "): Unable to find attribute - " + sSpecName + "/" + sAttr);
						}
					}
				}
			}
		}

		return bValid;
		
	}

}
