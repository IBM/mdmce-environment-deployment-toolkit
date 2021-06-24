/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment;

import com.ibm.mdmce.envtoolkit.deployment.model.*;

import java.io.*;

/**
 * Marshals information from the Searches.csv and handles transformation to relevant XML(s).
 * The expected file format is defined within the SearchTemplate class.
 *
 * @see Search
 */
public class SearchTemplateHandler extends SearchHandler {

	public SearchTemplateHandler(String sInputFilePath, String sVersion, TemplateParameters tp, String sEncoding) {
		super(SearchTemplate.getInstance(),
				"Searches.csv",
				"SEARCH_TEMPLATES" + File.separator + "SEARCH_TEMPLATES.xml",
				sInputFilePath,
				sVersion,
				tp,
				sEncoding);
	}
}
