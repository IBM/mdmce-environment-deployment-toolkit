/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment;

import com.ibm.mdmce.envtoolkit.deployment.model.*;

import java.io.*;

/**
 * Marshals information from the SearchTemplates.csv and handles transformation to relevant XML(s).
 * The expected file format is defined within the SearchTemplate class.
 *
 * @see SearchTemplate
 */
public class SearchTemplateHandler extends BasicEntityHandler {

	public SearchTemplateHandler(String sInputFilePath, String sVersion, TemplateParameters tp, String sEncoding) {
		super(SearchTemplate.getInstance(),
				"SearchTemplates.csv",
				"SEARCH_TEMPLATES" + File.separator + "SEARCH_TEMPLATES.xml",
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
		
		SearchTemplate st = (SearchTemplate) oEntity;
		String sSearchTemplateName = st.getName();
		String sContainerName = st.getContainerName();
		String sStepName = st.getStepName();
		
		bValid = validateExists(sSearchTemplateName, AttrCollection.class.getName(), sSearchTemplateName) && bValid;
		
		boolean bCatalogExists = (null != BasicEntityHandler.getFromCache(sContainerName, Catalog.class.getName(), false, false) );
		boolean bHierarchyExists = (null != getFromCache(sContainerName, Hierarchy.class.getName(), false, false) );
		
		ColArea colArea = null;
		if (!bCatalogExists && !bHierarchyExists) {
			colArea = (ColArea) BasicEntityHandler.getFromCache(sContainerName, ColArea.class.getName(), false, true, sSearchTemplateName);
			if (colArea == null) {
				bValid = false;
				System.err.println(" . . . WARNING (" + sSearchTemplateName + "): No container found with the name '" + sContainerName + "'.");
			}
		}
		
		if (!sStepName.equals("") && colArea != null) {
			Workflow wfl = (Workflow) BasicEntityHandler.getFromCache(colArea.getWorkflow(), Workflow.class.getName(), false, true, sContainerName);
			if (!wfl.getSteps().containsKey(sStepName)) {
				bValid = false;
				System.err.println(" . . . WARNING (" + sSearchTemplateName + "): No workflow step with the name '" + sStepName + "' was found in workflow '" + wfl.getName() + "' for collaboration area '" + sContainerName + "'.");
			}
		}
		
		return bValid;
		
	}
	
}
