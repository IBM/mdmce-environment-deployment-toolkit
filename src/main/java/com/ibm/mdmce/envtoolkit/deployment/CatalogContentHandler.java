/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment;

import com.ibm.mdmce.envtoolkit.deployment.model.CatalogContent;
import com.ibm.mdmce.envtoolkit.deployment.model.TemplateParameters;

import java.io.File;

/**
 * Marshals information from the CatalogContents.csv and handles transformation to relevant XML and CSV file(s).
 * The expected file format is defined within the ContainerContent class.
 * 
 * @see CatalogContent
 */
public class CatalogContentHandler extends ContainerContentHandler {
	public CatalogContentHandler(String sInputFilePath, String sVersion, TemplateParameters tp, String sOutputPath, String sEncoding) {
		super(CatalogContent.getInstance(),
				"CatalogContents.csv",
				"CATALOG_CONTENT" + File.separator + "CATALOG_CONTENT_DATA.xml",
				sInputFilePath,
				sVersion,
				tp,
				sOutputPath + File.separator + "CATALOG_CONTENT",
				sEncoding);
		initialize(sInputFilePath, sEncoding, tp);
	}
}
