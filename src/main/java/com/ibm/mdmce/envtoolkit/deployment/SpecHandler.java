/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment;

import com.ibm.mdmce.envtoolkit.deployment.model.BasicEntity;
import com.ibm.mdmce.envtoolkit.deployment.model.Lookup;
import com.ibm.mdmce.envtoolkit.deployment.model.Spec;
import com.ibm.mdmce.envtoolkit.deployment.model.TemplateParameters;

import java.util.*;

/**
 * 
 * Marshals information from the Specs.csv and handles transformation to relevant XML(s).
 * The expected file format is defined within the Spec class.
 * 
 * @author cgrote
 * @see Spec
 */
public class SpecHandler extends BasicEntityHandler {

	public SpecHandler(String sInputFilePath, String sVersion, TemplateParameters tp, String sCmpCode, String sEncoding) {
		super(Spec.getInstance(),
				"Specs.csv",
				"SPECS.xml",
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
		
		Spec spec = (Spec) oEntity;
		Map<String, Spec.Attribute> hmAttrs = spec.getAttributes();
		
		// Let's ensure primary specs have the required stuff...
		if (spec.getType().equals("PRIMARY_SPEC") || spec.getType().equals("LKP_SPEC")) {
			String sPathPK = spec.getPrimaryKeyPath();
			if (sPathPK.equals("")) {
				err.println(". . . WARNING (" + spec.getName() + "): No primary key selected.");
				bValid = false;
			}
			Spec.Attribute attr = hmAttrs.get(sPathPK);
			if (attr != null) {
				if (! (attr.getMin() == 1 && attr.getMax() == 1) ) {
					err.println(". . . WARNING (" + spec.getName() + "): Primary key attribute does not have min / max occurrence of 1.");
					bValid = false;
				}
			}
		}
		
		// Let's ensure we at least validate the referentials:
		//  - lookup table attributes
		//  - child attributes have parents defined as GROUPING
		//  - (others?)
		for (Map.Entry<String, Spec.Attribute> entry : hmAttrs.entrySet()) {
			String sAttrPath = entry.getKey();
			Spec.Attribute attr = entry.getValue();
			// Ensure we are not using any reserved characters in the attribute's name [BF#69239]
			if ((sAttrPath.indexOf("[") > 0)
					|| (sAttrPath.indexOf("]") > 0)
					|| (sAttrPath.indexOf("{") > 0)
					|| (sAttrPath.indexOf("}") > 0)
					|| (sAttrPath.indexOf(":") > 0)
					|| (sAttrPath.indexOf("\\") > 0)
					|| (sAttrPath.indexOf("\"") > 0)
					|| (sAttrPath.indexOf("'") > 0)
					|| (sAttrPath.indexOf("#") > 0)
					|| (sAttrPath.indexOf("@") > 0)
					|| (sAttrPath.indexOf("<") > 0)
					|| (sAttrPath.indexOf(">") > 0)
					|| (sAttrPath.indexOf(",") > 0)
					|| (sAttrPath.indexOf("*") > 0)
					|| (sAttrPath.indexOf("|") > 0)) {
				err.println(". . . WARNING (" + spec.getName() + "): Attribute \"" + sAttrPath + "\" contains illegal character - cannot contain any of the following: []{}:\\\"'#@<>,*|");
			}
			// Ensure we capture unspecified lookup tables and give some better warning information [BF#73391]
			if (attr.getType().equals("LOOKUP_TABLE"))
				bValid = validateExists(attr.getLookupTable(), Lookup.class.getName(), spec.getName() + "/" + sAttrPath) && bValid;
			int iParentIdx = sAttrPath.lastIndexOf("/"); 
			while (iParentIdx != -1) {
				String sParentPath = sAttrPath.substring(0, iParentIdx);
				if (!hmAttrs.containsKey(sParentPath)) {
					err.println(". . . WARNING (" + spec.getName() + "): Parent attribute '" + sParentPath + "' not defined before child definition(s).");
					bValid = false;
				} else {
					Spec.Attribute attrParent = hmAttrs.get(sParentPath);
					if (!attrParent.getType().equals("GROUPING")) {
						err.println(". . . WARNING (" + spec.getName() + "): Parent attribute type is not set to GROUPING for parent attribute '" + sParentPath + "'.");
						bValid = false;
					}
				}
				iParentIdx = sParentPath.indexOf("/");
			}
		}
				
		return bValid;
		
	}
	
	private void addSpecFilesToImportEnvXML(StringBuffer sb, List<String> alFiles, String sSpecType) {
		if (!alFiles.isEmpty()) {
			sb.append("   <Import enable=\"true\" type=\"").append(sSpecType).append("\">\n");
			for (String file : alFiles) {
				sb.append("      <File>").append(file).append("</File>\n");
			}
			sb.append("   </Import>\n");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getImportEnvXML(String sInputFilePath) {
		
		List<String> aGenFiles = getGeneratedFilesXML();
		
		StringBuffer sb = new StringBuffer();
		
		List<String> alPrimarySpecs = new ArrayList<>();
		List<String> alSecondarySpecs = new ArrayList<>();
		List<String> alLookupSpecs = new ArrayList<>();
		List<String> alFileSpecs = new ArrayList<>();
		List<String> alDestSpecs = new ArrayList<>();
		List<String> alInputSpecs = new ArrayList<>();
		List<String> alSubSpecs = new ArrayList<>();

		for (String sGenFile : aGenFiles) {
			String sFilePath = sGenFile.replace(sInputFilePath, "");
			if (sFilePath.contains("PRIMARY_SPEC")) {
				alPrimarySpecs.add(sFilePath);
			} else if (sFilePath.contains("SECONDARY_SPEC")) {
				alSecondarySpecs.add(sFilePath);
			} else if (sFilePath.contains("LKP_SPEC")) {
				alLookupSpecs.add(sFilePath);
			} else if (sFilePath.contains("FILE_SPEC")) {
				alFileSpecs.add(sFilePath);
			} else if (sFilePath.contains("MKT_SPEC")) {
				alDestSpecs.add(sFilePath);
			} else if (sFilePath.contains("SCRIPT_INPUT_SPEC")) {
				alInputSpecs.add(sFilePath);
			} else if (sFilePath.contains("SUB_SPEC")) {
				alSubSpecs.add(sFilePath);
			}
		}

		// special way to list specs in the XML for release 5.2.1 or 5.3.2 or higher
		if (version.equals("5.2.1") || versionUsesNextGenXML()) {
			List<String> alAllSpecs = new ArrayList<>();
			alAllSpecs.addAll(alFileSpecs);
			alAllSpecs.addAll(alInputSpecs);
			alAllSpecs.addAll(alPrimarySpecs);
			alAllSpecs.addAll(alLookupSpecs);
			alAllSpecs.addAll(alSecondarySpecs);
			alAllSpecs.addAll(alDestSpecs);
			alAllSpecs.addAll(alSubSpecs);
			addSpecFilesToImportEnvXML(sb, alAllSpecs, "SPEC");
		} else {
			addSpecFilesToImportEnvXML(sb, alFileSpecs, "SPEC");
			addSpecFilesToImportEnvXML(sb, alInputSpecs, "SCRIPT_INPUT_SPEC");
			addSpecFilesToImportEnvXML(sb, alPrimarySpecs, "PRIMARY_SPEC");
			addSpecFilesToImportEnvXML(sb, alLookupSpecs, "LOOKUP_TABLE_SPEC");
			addSpecFilesToImportEnvXML(sb, alSecondarySpecs, "SECONDARY_SPEC");
			addSpecFilesToImportEnvXML(sb, alDestSpecs, "DESTINATION_SPEC");
			addSpecFilesToImportEnvXML(sb, alSubSpecs, "SUB_SPEC");
		}
		
		return sb.toString();
		
	}
	
}
