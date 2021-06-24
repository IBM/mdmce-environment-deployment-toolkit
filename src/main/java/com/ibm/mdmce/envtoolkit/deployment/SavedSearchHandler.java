/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment;

import com.ibm.mdmce.envtoolkit.deployment.model.SavedSearch;
import com.ibm.mdmce.envtoolkit.deployment.model.SearchTemplate;
import com.ibm.mdmce.envtoolkit.deployment.model.TemplateParameters;

import java.io.File;

/**
 * Marshals information from the Searches.csv and handles transformation to relevant XML(s).
 * The expected file format is defined within the SearchTemplate class.
 *
 * @see SavedSearch
 */
public class SavedSearchHandler extends SearchHandler {

	public SavedSearchHandler(String sInputFilePath, String sVersion, TemplateParameters tp, String sEncoding) {
		super(SavedSearch.getInstance(),
				"Searches.csv",
				"SAVED_SEARCHES" + File.separator + "SAVED_SEARCHES.xml",
				sInputFilePath,
				sVersion,
				tp,
				sEncoding);
	}
}
