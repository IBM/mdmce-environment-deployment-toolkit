/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment;

import com.ibm.mdmce.envtoolkit.deployment.model.*;

import java.io.*;

/**
 * Marshals information from the Views.csv (for catalog views only) and handles transformation to relevant XML(s).
 * The expected file format is defined within the CatalogView class.
 *
 * @see CatalogView
 */
public class CatalogViewHandler extends ViewHandler {
	public CatalogViewHandler(String sInputFilePath, String sVersion, TemplateParameters tp, String sEncoding) {
		super(CatalogView.getInstance(),
				"Views.csv",
				"CATALOG_VIEW" + File.separator + "CATALOG_VIEW.xml",
				sInputFilePath,
				sVersion,
				tp,
				sEncoding);
		initialize(sInputFilePath, sEncoding, tp);
	}
}
