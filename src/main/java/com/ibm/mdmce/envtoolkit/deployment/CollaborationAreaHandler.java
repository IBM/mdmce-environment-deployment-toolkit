/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment;

import com.ibm.mdmce.envtoolkit.deployment.model.*;

import java.io.*;

/**
 * Marshals information from the ColAreas.csv and handles transformation to relevant XML(s).
 * The expected file format is defined within the CollaborationArea class.
 *
 * @see ColArea
 */
public class CollaborationAreaHandler extends BasicEntityHandler {

	public CollaborationAreaHandler(String sInputFilePath, String sVersion, TemplateParameters tp, String sEncoding) {
		super(ColArea.getInstance(),
				"ColAreas.csv",
				"COLLABORATION_AREA" + File.separator + "COLLABORATION_AREA.xml",
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
		
		ColArea colArea = (ColArea) oEntity;
		bValid = validateExists(colArea.getWorkflow(), Workflow.class.getName(), colArea.getName()) && bValid;
		
		if (colArea.getContainerType().equals("CATALOG"))
			bValid = validateExists(colArea.getSourceContainer(), Catalog.class.getName(), colArea.getName()) && bValid;
		else
			bValid = validateExists(colArea.getSourceContainer(), Hierarchy.class.getName(), colArea.getName()) && bValid;
		
		bValid = validateExists(colArea.getAcg(), ACG.class.getName(), colArea.getName()) && bValid;

		for (String role : colArea.getAdminRoles()) {
			bValid = validateExists(role, Role.class.getName(), colArea.getName()) && bValid;
		}
		for (String user : colArea.getAdminUsers()) {
			bValid = validateExists(user, User.class.getName(), colArea.getName()) && bValid;
		}
		
		return bValid;
		
	}

}
