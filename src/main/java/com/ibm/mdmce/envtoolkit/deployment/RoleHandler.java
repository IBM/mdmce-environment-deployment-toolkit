/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment;

import com.ibm.mdmce.envtoolkit.deployment.model.Role;
import com.ibm.mdmce.envtoolkit.deployment.model.TemplateParameters;

import java.util.*;

/**
 * Marshals information from the Roles.csv and handles transformation to relevant XML(s).
 * The expected file format is defined within the Role class.
 *
 * @see Role
 */
public class RoleHandler extends BasicEntityHandler {

	public RoleHandler(String sInputFilePath, String sVersion, TemplateParameters tp, String sEncoding) {
		super(Role.getInstance(),
				"Roles.csv",
				"ROLES.xml",
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
	public String getImportEnvXML(String sInputFilePath) {
		
		List<String> aGenFiles = getGeneratedFilesXML();
		StringBuilder sb = new StringBuilder();
		
		sb.append("   <Import enable=\"true\" type=\"ROLES\">\n");
		for (String genFile : aGenFiles) {
			String sRelativePath = genFile.replace(sInputFilePath, "");
			if (!sRelativePath.equals("ROLES.xml"))
				sb.append("      <File>").append(sRelativePath).append("</File>\n");
		}
		sb.append("   </Import>\n");
		
		return sb.toString();
		
	}
	
}
