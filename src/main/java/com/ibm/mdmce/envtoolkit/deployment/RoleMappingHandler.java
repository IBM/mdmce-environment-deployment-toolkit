/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment;

import com.ibm.mdmce.envtoolkit.deployment.model.RoleToACG;
import com.ibm.mdmce.envtoolkit.deployment.model.TemplateParameters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Marshals information from the RolesToACGs.csv and handles transformation to relevant XML(s).
 * The expected file format is defined within the RoleMapping class.
 *
 * @see RoleToACG
 */
public class RoleMappingHandler extends BasicEntityHandler {

	public RoleMappingHandler(String sInputFilePath, String sVersion, TemplateParameters tp, String sEncoding) {

		super(RoleToACG.getInstance());

		this.csvFilePath = "RoleToACGs.csv";
		this.xmlFilePath = "tmp" + File.separator + "ROLES_EMPTY.xml";
		this.version = sVersion;

		sInputFilePath = sInputFilePath + File.separator + csvFilePath;

		out.println("Reading input from: " + sInputFilePath);
		
		try {
			// Read in the entities first...
			CSVParser readerCSV = new CSVParser(sInputFilePath, sEncoding);
			List<String> aTokens = readerCSV.splitLine();
			// First line (header) will give us all the information about roles + ACGs
			List<List<String>> headerColumnsToPrivs = new ArrayList<>();
			headerColumnsToPrivs.add(new ArrayList<>());
			
			List<List<String>> alReplacedHeaderTokens = replaceTemplateParameters(aTokens, tp, -1);

			for (List<String> aReplacedTokens : alReplacedHeaderTokens) {
				for (int j = 1; j < aReplacedTokens.size(); j++) {
					if (!aReplacedTokens.get(j).equals("")) {
						if (j >= headerColumnsToPrivs.size()) {
							List<String> alPossibilitiesForCol = new ArrayList<>();
							headerColumnsToPrivs.add(alPossibilitiesForCol);
						}
						List<String> alPossibilitiesForCol = headerColumnsToPrivs.get(j);
						alPossibilitiesForCol.add(aReplacedTokens.get(j));
						String[] aRoleAndACG = aReplacedTokens.get(j).split(",");
						String sRoleName = aRoleAndACG[0];
						RoleToACG rm = (RoleToACG) getFromCache(sRoleName, RoleToACG.class.getName(), false, false);
						if (rm == null) {
							// If there is not yet any role mapping, create one and add it to the cache
							rm = new RoleToACG(sRoleName);
							addToCache(sRoleName, rm);
						}
					}
				}
			}
			
			aTokens = readerCSV.splitLine();
			while (aTokens != null && aTokens.size() > 0) {
				String sPrivName = aTokens.get(0);
				for (int i = 1; i < aTokens.size(); i++) {
					boolean bTicked = CSVParser.checkBoolean(aTokens.get(i));
					if (bTicked) {
						List<String> alRoleToACGPossibilities = headerColumnsToPrivs.get(i);
						for (String sRoleToACG : alRoleToACGPossibilities) {
							String[] aRoleAndACG = sRoleToACG.split(",");
							String sRoleName = aRoleAndACG[0];
							String sACGName = aRoleAndACG[1];
							RoleToACG rm = (RoleToACG) getFromCache(sRoleName, RoleToACG.class.getName(), true, false);
							if (!rm.getAcgMappings().containsKey(sACGName))
								rm.getAcgMappings().put(sACGName, new ArrayList<>());
							rm.getAcgMappings().get(sACGName).add(sPrivName);
						}
					}
				}
				aTokens = readerCSV.splitLine();
			}

		} catch (FileNotFoundException errNoFile) {
			err.println("Error: File not found! " + errNoFile.getMessage());
			federated = false;
		} catch (IOException errIO) {
			err.println("Error: IO problem! " + errIO.getMessage());
		}
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getImportEnvXML(String sInputFilePath) {
		return "";
	}
	
}
