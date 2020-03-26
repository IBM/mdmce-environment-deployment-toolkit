/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment;

import com.ibm.mdmce.envtoolkit.deployment.model.TemplateParameters;
import com.ibm.mdmce.envtoolkit.deployment.model.HierarchyContent;

import java.io.File;

/**
 * Marshals information from the HierarchyContents.csv and handles transformation to relevant XML and CSV file(s).
 * The expected file format is defined within the ContainerContent class.
 *
 * @see HierarchyContent
 */
public class HierarchyContentHandler extends ContainerContentHandler {
	public HierarchyContentHandler(String sInputFilePath, String sVersion, TemplateParameters tp, String sOutputPath, String sEncoding) {
		super(HierarchyContent.getInstance(),
				"HierarchyContents.csv",
				"HIERARCHY_CONTENT" + File.separator + "HIERARCHY_CONTENT_DATA.xml",
				sInputFilePath,
				sVersion,
				tp,
				sOutputPath + File.separator + "HIERARCHY_CONTENT",
				sEncoding);
		initialize(sInputFilePath, sEncoding, tp);
	}
}
