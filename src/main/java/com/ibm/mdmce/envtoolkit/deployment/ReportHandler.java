/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment;

import com.ibm.mdmce.envtoolkit.deployment.model.*;

import java.io.*;

/**
 * Marshals information from the Reports.csv and handles transformation to relevant XML(s).
 * The expected file format is defined within the Report class.
 *
 * @see Report
 */
public class ReportHandler extends BasicEntityHandler {

	public ReportHandler(String sInputFilePath, String sVersion, TemplateParameters tp, String sEncoding) {
		super(Report.getInstance(),
				"Reports.csv",
				"REPORTS" + File.separator + "REPORTS.xml",
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
		Report report = (Report) oEntity;
		bValid = validateExists("/scripts/reports/" + report.getScript(), Script.class.getName(), report.getName()) && bValid;
		bValid = validateExists(report.getDistribution(), Distribution.class.getName(), report.getName()) && bValid;
		return bValid;
	}

}
