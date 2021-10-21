/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment;

import com.ibm.mdmce.envtoolkit.deployment.model.*;

import java.io.File;
import java.util.*;

/**
 * Marshals information from the Searches.csv and handles transformation to relevant XML(s).
 * The expected file format is defined within the SearchTemplate class.
 *
 * @see Search
 */
public class SearchHandler extends BasicEntityHandler {

	/**
	 * Construct a new handler for the provided parameters.
	 * @param entity the entity for which to construct a handler
	 * @param csvFilePath the path to the CSV file that this handler processes
	 * @param xmlFilePath the path to the XML file that this handler processes
	 * @param sInputFilePath the location of the CSV file to translate
	 * @param version the version of the software for which to translate
	 * @param tp the template parameters to apply to the contents of the CSV file
	 * @param sEncoding the encoding of the CSV file
	 * @param <T> the type of entity for which to construct a handler
	 */
	public <T extends BasicEntity> SearchHandler(T entity,
											   String csvFilePath,
											   String xmlFilePath,
											   String sInputFilePath,
											   String version,
											   TemplateParameters tp,
											   String sEncoding) {
		super(entity, csvFilePath, xmlFilePath, sInputFilePath, version, tp, sEncoding);
		initialize(sInputFilePath, sEncoding, tp);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected <T extends BasicEntity> void addLineToCache(List<List<String>> alReplacedTokens) {
		for (List<String> aReplacedTokens : alReplacedTokens) {
			Search instance = entity.createInstance(aReplacedTokens);
			if (instance != null) {
				addToCache(instance.getUniqueId(), instance);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean validate(BasicEntity oEntity) {
		boolean bValid = true;
		boolean bValidCtg = true;
		boolean bValidHier = true;
		boolean bValidColArea = true;
		boolean bValidStepName = true;
		boolean bValidUserName = false;
		String wflName = "";
		Search s = (Search) oEntity;

		if (!Search.searchTypeList.contains(s.getSearchType())) {
			bValid = false;
			EnvironmentHandler.logger.warning(". . . WARNING (" + s.getSearchName() + "): Search Type (" + s.getSearchType() + ") should be TEMPLATE or QUERY.");
		}


		//if (!Search.containerTypeList.contains(s.getContainerType())) {


		if ("CATALOG".equals(s.getContainerType())) {
			bValidCtg = validateExists(s.getContainerName(), Catalog.class.getName(), s.getSearchName());
		} else if ("HIERARCHY".equals(s.getContainerType())) {
			bValidHier = validateExists(s.getContainerName(), Hierarchy.class.getName(), s.getSearchName());
		} else if ("COL_AREA".equals(s.getContainerType())) {
			ColArea colArea = (ColArea) getFromCache(s.getContainerName(), ColArea.class.getName(), false, true, s.getSearchName());
			bValidColArea = (null != colArea);
			if (bValidColArea && s.getStepName() != null) {
				Workflow wfl = (Workflow)getFromCache(colArea.getWorkflow(), Workflow.class.getName(), false, true, s.getSearchName());
				wflName = wfl.getName();
				if (!wfl.getSteps().containsKey(s.getStepName())) {
					bValidStepName = false;
				}
			}
			if (!Search.resrvedByList.contains(s.getReservedBy())) {
				EnvironmentHandler.logger.warning(". . . WARNING (" + s.getSearchName() + "): Reserved By ("+s.getReservedBy()+") should be RESERVED_BY_ME, RESERVED_BY_OTHERS or AVAILABLE");
			}
		} else {
			bValid = false;
			EnvironmentHandler.logger.warning(". . . WARNING (" + s.getSearchName() + "): Container Type (" + s.getContainerType() + ") should be CATALOG, HIERARCHY or COL_AREA.");
		}

		if (bValidColArea && !bValidStepName) {
			EnvironmentHandler.logger.warning(". . . WARNING (" + s.getSearchName() + "): Workflow (" + wflName + ") Step Name (" + s.getStepName() + ") for collaboration area (" + s.getContainerName() + ") not found.");
		}
		if (!"Admin".equals(s.getUserName())) {
			bValid = validateExists(s.getUserName(), User.class.getName(), s.getSearchName());
		}

		for (Map<String, String> attr : s.getAttributes()) {
			if (!checkAttrExist(attr.get(Search.ATTRIBUTE_PATH))) {
				bValid = false;
				EnvironmentHandler.logger.warning(". . . WARNING (" + s.getSearchName() + "): Unable to find Attribute Path - " + attr.get(Search.ATTRIBUTE_PATH));
			}

			String searchOperator = attr.get(Search.SEARCH_OPERATOR);
			if (!Search.searchOperatorList.contains(searchOperator)) {
				EnvironmentHandler.logger.warning(". . . WARNING (" + s.getSearchName() + "): Search Operator ("+searchOperator+") doesn't exist.");
			}

			String logicalOperator = attr.get(Search.LOGICAL_OPERATOR);
			if (!Search.logicalOperatorList.contains(logicalOperator)) {
				EnvironmentHandler.logger.warning(". . . WARNING (" + s.getSearchName() + "): Search Operator ("+logicalOperator+") doesn't exist.");
			}
		}

		if (!Search.sortTypeList.contains(s.getSortType())) {
			EnvironmentHandler.logger.warning(". . . WARNING (" + s.getSearchName() + "): Sort Type ("+s.getSortType()+") doesn't exist.");
		}

		if (!Search.categoryRestrictionList.contains(s.getCategoryRestriction())) {
			EnvironmentHandler.logger.warning(". . . WARNING (" + s.getSearchName() + "): Category Restriction ("+s.getCategoryRestriction()+") doesn't exist.");
		}

		if (s.getSortAttributePath() != null && !s.getSortAttributePath().isEmpty() && !checkAttrExist(s.getSortAttributePath())) {
			bValid = false;
			EnvironmentHandler.logger.warning(". . . WARNING (" + s.getSearchName() + "): Unable to find Sort Attribute Path - " + s.getSortAttributePath());
		}
		return bValid;
		
	}
	public static boolean checkAttrExist(String attrPath) {
		boolean bValidAttrPath = false;
		if (attrPath !=null && !attrPath.isEmpty()) {
			String specName = attrPath.replaceFirst("/.+", "");
			String attrName = attrPath.replaceFirst(".+?/", "");
			Spec spec = (Spec)getFromCache(specName, Spec.class.getName(), false, false);
			if (spec != null) {
				Spec.Attribute specAttr = spec.getAttributes().get(attrName);
				if (specAttr != null) {
					bValidAttrPath = true;
				}
			}
		}
		return bValidAttrPath;
	}
	
}
