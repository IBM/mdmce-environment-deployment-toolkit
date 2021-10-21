/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment;

import com.ibm.mdmce.envtoolkit.deployment.model.*;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Marshals information from the Roles.csv and handles transformation to relevant XML(s).
 * The expected file format is defined within the Role class.
 *
 * @see Job
 */
public class JobHandler extends BasicEntityHandler {

	public JobHandler(String sInputFilePath, String sVersion, TemplateParameters tp, String sEncoding) {
		super(Job.getInstance(),
				"Jobs.csv",
				"JOB" + File.separator + "JOB.xml",
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
		Job j = (Job) oEntity;
		bValid = validateExists(j.getCreatedBy(), User.class.getName(), j.getJobDescription()) && bValid;
		for (Map<String, String> sch : j.getSchedules()) {
			bValid = validateExists(sch.get(Job.USER), User.class.getName(), j.getJobDescription()) && bValid;
		}
		return bValid;
	}
	
}
