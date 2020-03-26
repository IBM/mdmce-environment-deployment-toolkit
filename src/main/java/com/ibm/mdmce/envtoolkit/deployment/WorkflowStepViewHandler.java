/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment;

import com.ibm.mdmce.envtoolkit.deployment.model.*;

import java.io.*;

/**
 * Marshals information from the Views.csv (for workflow step views only) and handles transformation to relevant XML(s).
 * The expected file format is defined within the View class.
 *
 * @see View
 */
public class WorkflowStepViewHandler extends ViewHandler {

	public WorkflowStepViewHandler(String sInputFilePath, String sVersion, TemplateParameters tp, String sEncoding) {
		super(WorkflowStepView.getInstance(),
				"Views.csv",
				"tmp" + File.separator + "WFL_STEP_VIEWS_EMPTY.xml",
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
	public boolean validate(BasicEntity oEntity) {
		boolean bValid = true;
		WorkflowStepView wflView = (WorkflowStepView) oEntity;
		for (String sAttrCol : wflView.getAttributeCollections()) {
			bValid = validateExists(sAttrCol, AttrCollection.class.getName(), wflView.getContainerName() + "/" + wflView.getStepPath()) && bValid;
		}
		return bValid;
	}

}
