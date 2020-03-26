/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment;

import com.ibm.mdmce.envtoolkit.deployment.model.HierarchyView;
import com.ibm.mdmce.envtoolkit.deployment.model.TemplateParameters;

import java.io.*;

/**
 * Marshals information from the Views.csv (for hierarchy views only) and handles transformation to relevant XML(s).
 * The expected file format is defined within the CatalogView class.
 *
 * @see HierarchyView
 */
public class HierarchyViewHandler extends ViewHandler {
	public HierarchyViewHandler(String sInputFilePath, String sVersion, TemplateParameters tp, String sEncoding) {
		super(HierarchyView.getInstance(),
				"Views.csv",
				"HIERARCHY_VIEW" + File.separator + "HIERARCHY_VIEW.xml",
				sInputFilePath,
				sVersion,
				tp,
				sEncoding);
		initialize(sInputFilePath, sEncoding, tp);
	}
}
