/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment;

import java.io.*;
import java.util.*;

import com.ibm.mdmce.envtoolkit.deployment.model.*;

/**
 * Marshals information from the Exports.csv and handles transformation to relevant XML(s).
 * The expected file format is defined within the Export class.
 *
 * @see Export
 */
public class ExportHandler extends BasicEntityHandler {

	public ExportHandler(String sInputFilePath, String sVersion, TemplateParameters tp, String sEncoding) {
		super(Export.getInstance(),
				"Exports.csv",
				"EXPORTS" + File.separator + "EXPORTS.xml",
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
			Export instance = entity.createInstance(aReplacedTokens);
			addToCache(instance.getUniqueId(), instance);
			if (instance.getSpecMap().equals("")) {
				// auto-generate a dummy spec mapping...
				instance.setSpecMap(instance.getContainerName() + " to " + instance.getDestinationSpec());
				SpecMap specMap = (SpecMap) getFromCache(instance.getSpecMap(), SpecMap.class.getName(), false, false);
				if (specMap == null) {
					out.println(" . . . generating default spec map: " + instance.getSpecMap());
					specMap = new SpecMap(instance.getSpecMap(), "CATALOG_MKT_MAP", instance.getContainerName(), instance.getDestinationSpec());
					EnvironmentHandler.getHandler("SpecMap").addToCache(specMap.getName(), specMap);
				}
			}
			if (!instance.getParamsPath().equals("")) {
				Script docParams = (Script) getFromCache(instance.getParamsName(), Script.class.getName(), false, false);
				if (docParams == null) {
					out.println(" . . . generating default parameters: " + instance.getParamsPath());
					docParams = new Script("INPUT_PARAM",
							instance.getParamsName(),
							instance.getInputSpec(),
							instance.getParamsPath(),
							"/params/None");
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
		
		Export export = (Export) oEntity;
		Catalog ctg = (Catalog) BasicEntityHandler.getFromCache(export.getContainerName(), Catalog.class.getName(), false, false);
		if (ctg == null)
			bValid = validateExists(export.getContainerName(), Lookup.class.getName(), export.getName()) && bValid;
		
		bValid = validateExists(export.getDestinationSpec(), Spec.class.getName(), export.getName()) && bValid;
		bValid = validateExists("/scripts/export/ctg/" + export.getScriptName(), Script.class.getName(), export.getName()) && bValid;
		if (!export.getApprovalUser().equals(""))
			bValid = validateExists(export.getApprovalUser(), User.class.getName(), export.getName()) && bValid;
		if (!export.getDistributionName().equals(""))
			bValid = validateExists(export.getDistributionName(), Distribution.class.getName(), export.getName()) && bValid;
		bValid = validateExists(export.getHierarchyName(), Hierarchy.class.getName(), export.getName()) && bValid;

		bValid = validateExists(export.getSpecMap(), SpecMap.class.getName(), export.getName()) && bValid;
		// TODO: validate Distribution group
		
		if (!export.getSelection().equals(""))
			bValid = validateExists(export.getSelection(), Selection.class.getName(), export.getName()) && bValid;
		
		if (!export.getParamsPath().equals(""))
			bValid = validateExists(export.getParamsPath(), Script.class.getName(), export.getName()) && bValid;
		
		return bValid;
		
	}
	
}
