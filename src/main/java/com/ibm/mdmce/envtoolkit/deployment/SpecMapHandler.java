/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment;

import com.ibm.mdmce.envtoolkit.deployment.model.BasicEntity;
import com.ibm.mdmce.envtoolkit.deployment.model.Spec;
import com.ibm.mdmce.envtoolkit.deployment.model.SpecMap;
import com.ibm.mdmce.envtoolkit.deployment.model.TemplateParameters;

import java.io.*;
import java.util.*;

/**
 * At the moment, no support exists for these through CSV files - they exist as an object only to support 
 * automatic generation of dummy SpecMaps internally by the tooling.
 *
 * @see SpecMap
 */
public class SpecMapHandler extends BasicEntityHandler {
	
	public SpecMapHandler(String sInputFilePath, String sVersion, TemplateParameters tp, String sCmpCode, String sEncoding) {
		super(SpecMap.getInstance(),
				"SpecMaps.csv",
				"MAPS" + File.separator + "MAPS_EMPTY.xml",
				sInputFilePath,
				sVersion,
				tp,
				sEncoding);
		// Ensure we always output spec maps
		federated = true;
		initialize(sInputFilePath, sEncoding, tp);
	}
	
	private boolean validateAttributeExists(SpecMap specMap, String sFullAttrPath) {
		
		boolean bAttrExists = true;
		
		int iFirstSlash = sFullAttrPath.indexOf("/");
		String sSpecName = sFullAttrPath.substring(0, iFirstSlash);
		String sAttrPath = sFullAttrPath.substring(iFirstSlash + 1);
		
		Spec spec = (Spec) getFromCache(sSpecName, Spec.class.getName(), false, true, specMap.getName());
		
		if (spec != null) {
			Set<String> attributes = spec.getAttributes().keySet();
			if (!attributes.contains(sAttrPath)) {
				bAttrExists = false;
				EnvironmentHandler.logger.warning(". . . WARNING (" + specMap.getName() + "): Attribute \"" + sAttrPath + "\" does not exist in the spec indicated ['" + spec.getName() + "'].");
			}
		}
		
		return bAttrExists;
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean validate(BasicEntity oEntity) {
		boolean bValid = true;
		SpecMap specMap = (SpecMap)oEntity;
		// Ensure the source and destination attributes both exist in their respective specs...
		for (Map.Entry<String, List<String>> entry : specMap.getSourceToDestinationPaths().entrySet()) {
			String sSrcPath = entry.getKey();
			bValid = validateAttributeExists(specMap, sSrcPath) && bValid;
			List<String> alDstPaths = entry.getValue();
			for (String sDstPath : alDstPaths) {
				bValid = validateAttributeExists(specMap, sDstPath) && bValid;
			}
		}
		return bValid;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getImportEnvXML(String sInputFilePath) {
		return "   <Import enable=\"true\" type=\"MAPS\">\n" +
				"      <File>MAPS/MAPS.xml</File>\n" +
				"   </Import>\n";
	}
	
}
