/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment;

import com.ibm.mdmce.envtoolkit.deployment.model.OrganizationContent;
import com.ibm.mdmce.envtoolkit.deployment.model.TemplateParameters;

import java.io.File;

/**
 * Marshals information from the OrganizationContents.csv and handles transformation to relevant XML and CSV file(s).
 * The expected file format is defined within the ContainerContent class.
 *
 * @see OrganizationContent
 */
public class OrganizationContentHandler extends ContainerContentHandler {
	public OrganizationContentHandler(String sInputFilePath, String sVersion, TemplateParameters tp, String sOutputPath, String sEncoding) {
		super(OrganizationContent.getInstance(),
				"OrganizationContents.csv",
				"ORG_HIERARCHY_CONTENT" + File.separator + "ORG_HIERARCHY_CONTENT_DATA.xml",
				sInputFilePath,
				sVersion,
				tp,
				sOutputPath + File.separator + "ORG_HIERARCHY_CONTENT",
				sEncoding);
		initialize(sInputFilePath, sEncoding, tp);
	}
}
