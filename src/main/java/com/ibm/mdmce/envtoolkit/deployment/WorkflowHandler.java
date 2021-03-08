/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment;

import com.ibm.mdmce.envtoolkit.deployment.model.*;

import java.io.*;
import java.util.*;

/**
 * Marshals information from the Workflows.csv and handles transformation to relevant XML(s).
 * The expected file format is defined within the Workflow class.
 *
 * @see Workflow
 */
public class WorkflowHandler extends BasicEntityHandler {
	
	public WorkflowHandler(String sInputFilePath, String sVersion, TemplateParameters tp, String sEncoding) {
		super(Workflow.getInstance(),
				"Workflows.csv",
				"WORKFLOW" + File.separator + "WORKFLOW.xml",
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

		Workflow wfl = (Workflow) oEntity;
		bValid = validateExists(wfl.getAcg(), ACG.class.getName(), wfl.getName()) && bValid;

		for (Map.Entry<String, Workflow.WorkflowStep> entry : wfl.getSteps().entrySet()) {

			String sStepName = entry.getKey();
			Workflow.WorkflowStep wflStep = entry.getValue();

			if (wflStep.includesScript())
				bValid = validateExists("/scripts/workflow/" + wfl.getName() + "/" + wflStep.getName(), Script.class.getName(), wfl.getName() + "/" + wflStep.getName()) && bValid;

			if (wflStep.getType().equals("NESTED_WORKFLOW")) {
				if (wflStep.getPerformerUsers().size() > 0) {
					EnvironmentHandler.logger.warning(". . . WARNING (" + wfl.getName() + "): Nested workflow (" + sStepName + ") cannot have any user performers defined.");
					bValid = false;
				}
				if (wflStep.getPerformerRoles().size() > 0) {
					EnvironmentHandler.logger.warning(". . . WARNING (" + wfl.getName() + "): Nested workflow (" + sStepName + ") cannot have any role performers defined.");
					bValid = false;
				}
				if (wflStep.getRequiredAttributeCollections().size() > 0) {
					EnvironmentHandler.logger.warning(". . . WARNING (" + wfl.getName() + "): Nested workflow (" + sStepName + ") cannot have any required attribute collections defined.");
					bValid = false;
				}
			} else {
				for (String role : wflStep.getPerformerRoles()) {
					bValid = validateExists(role, Role.class.getName(), wfl.getName() + "/" + wflStep.getName()) && bValid;
				}
				for (String user : wflStep.getPerformerUsers()) {
					bValid = validateExists(user, User.class.getName(), wfl.getName() + "/" + wflStep.getName()) && bValid;
				}
				for (String sAttrCol : wflStep.getRequiredAttributeCollections()) {
					bValid = validateExists(sAttrCol, AttrCollection.class.getName(), wfl.getName() + "/" + wflStep.getName()) && bValid;
				}
			}

			for (Map.Entry<String, List<String>> entryExitValues : wflStep.getExitValueToNextSteps().entrySet()) {
				String sExitValue = entryExitValues.getKey();
				List<String> alNextSteps = entryExitValues.getValue();
				if (wflStep.getType().equals("NESTED_WORKFLOW")) {
					if (!sExitValue.equals("SUCCESS") && !sExitValue.equals("FAILURE") && !sExitValue.equals("TIMEOUT")) {
						EnvironmentHandler.logger.warning(". . . WARNING (" + wfl.getName() + "): Nested workflow (" + sStepName + ") is using an exit value other than SUCCESS, FAILURE, or TIMEOUT: " + sExitValue + ".");
						bValid = false;
					}
				}
				for (String sNextStep : alNextSteps) {
					if (!wfl.getSteps().containsKey(sNextStep)) {
						EnvironmentHandler.logger.warning(". . . WARNING (" + wfl.getName() + "): Mapped next step (" + sNextStep + ") from " + sStepName + "::" + sExitValue + " not found.");
						bValid = false;
					}
				}
			}
						
		}
		
		return bValid;
		
	}

}
