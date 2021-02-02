/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment;

import com.ibm.mdmce.envtoolkit.deployment.model.*;

import java.io.*;
import java.util.*;

/**
 * Marshals information from the Imports.csv and handles transformation to relevant XML(s).
 * The expected file format is defined within the ImportFeed class.
 *
 * @see Import
 */
public class ImportFeedHandler extends BasicEntityHandler {

	private boolean specMapGenerated = false;

	public ImportFeedHandler(String sInputFilePath, String sVersion, TemplateParameters tp, String sEncoding) {
		super(Import.getInstance(),
				"Imports.csv",
				"FEEDS" + File.separator + "FEEDS.xml",
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
			Import instance = entity.createInstance(aReplacedTokens);
			addToCache(instance.getUniqueId(), instance);
			if (instance.getType().equals("ITM") && instance.getSpecMap().equals("")) {
				// auto-generate a dummy spec mapping...
				instance.setSpecMap(instance.getFileSpec() + " to " + instance.getCatalog());
				SpecMap specMap = (SpecMap) getFromCache(instance.getSpecMap(), SpecMap.class.getName(), false, false);
				if (specMap == null) {
					out.println(" . . . generating default spec map: " + instance.getSpecMap());
					specMap = new SpecMap(instance.getSpecMap(), "FILE_CATALOG_MAP", instance.getFileSpec(), instance.getCatalog());
					EnvironmentHandler.getHandler("SpecMap").addToCache(specMap.getName(), specMap);
					specMapGenerated = true;
				}
			}
			if (!instance.getParamsPath().equals("")) {
				Script docParams = (Script) getFromCache(instance.getParamsPath(), Script.class.getName(), false, false);
				if (docParams == null) {
					out.println(" . . . generating default parameters: " + instance.getParamsPath());
					docParams = new Script("INPUT_PARAM", instance.getParamsName(), instance.getInputSpec(), "/params/None", instance.getParamsPath());
					EnvironmentHandler.getHandler("Script").addToCache(docParams.getPathRemote(), docParams);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean validate(BasicEntity oEntity) {
		
		boolean bValid = true;
		
		Import feed = (Import) oEntity;
		String sScriptPath = "/scripts/import/";
		if (feed.getType().equals("ITM"))
			sScriptPath += "ctg/";
		else if (feed.getType().equals("CTR"))
			sScriptPath += "ctr/";
		
		if (!feed.getType().equals("IMG")) {
			Catalog ctg = (Catalog) getFromCache(feed.getCatalog(), Catalog.class.getName(), false, false);
			if (ctg == null && !feed.getType().equals("CTR") && !feed.isCollaborationArea())
				bValid = validateExists(feed.getCatalog(), Lookup.class.getName(), feed.getName()) && bValid;
			if (feed.getType().equals("CTR") || !feed.getHierarchy().equals(""))
				bValid = validateExists(feed.getHierarchy(), Hierarchy.class.getName(), feed.getName()) && bValid;
			if (!feed.getType().equals("CTR")) {
				bValid = validateExists(feed.getFileSpec(), Spec.class.getName(), feed.getName()) && bValid;
				bValid = validateExists(feed.getSpecMap(), SpecMap.class.getName(), feed.getName()) && bValid;
			}
			if (specMapGenerated)
				bValid = validateExists(sScriptPath + feed.getScript(), Script.class.getName(), feed.getName()) && bValid;
		}		
		
		if (!feed.getApprovalUser().equals(""))
			bValid = validateExists(feed.getApprovalUser(), User.class.getName(), feed.getName()) && bValid;
		bValid = validateExists(feed.getDataSource(), DataSource.class.getName(), feed.getName()) && bValid;
		bValid = validateExists(feed.getAcg(), ACG.class.getName(), feed.getName()) && bValid;
		
		if (feed.isCollaborationArea()) {
			bValid = validateExists(feed.getCatalog(), ColArea.class.getName(), feed.getName()) && bValid;
			ColArea colArea = (ColArea) getFromCache(feed.getCatalog(), ColArea.class.getName(), false, false);
			if (colArea != null) {
				Workflow wfl = (Workflow) getFromCache(colArea.getWorkflow(), Workflow.class.getName());
				if (wfl != null) {
					Workflow.WorkflowStep wflStep = wfl.getSteps().get(feed.getWorkflowStep());
					bValid = (wflStep != null) && bValid;
					if (wflStep == null)
						err.println("WARNING (" + feed.getName() + "): Workflow step does not exist - " + feed.getWorkflowStep());
				}
			}
		}
		
		// Spec map ???
		
		if (!feed.getParamsPath().equals("params/None"))
			bValid = validateExists(feed.getParamsPath(), Script.class.getName(), feed.getName()) && bValid;
		
		return bValid;
		
	}
	
}
