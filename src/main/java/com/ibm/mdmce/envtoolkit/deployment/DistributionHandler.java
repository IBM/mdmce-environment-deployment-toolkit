/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment;

import com.ibm.mdmce.envtoolkit.deployment.model.Distribution;
import com.ibm.mdmce.envtoolkit.deployment.model.TemplateParameters;

import java.io.*;

/**
 * Marshals information from the Distributions.csv and handles transformation to relevant XML(s).
 * The expected file format is defined within the Distribution class.
 *
 * @see Distribution
 */
public class DistributionHandler extends BasicEntityHandler {
	public DistributionHandler(String sInputFilePath, String sVersion, TemplateParameters tp, String sEncoding) {
		super(Distribution.getInstance(),
				"Distributions.csv",
				"DISTRIBUTION" + File.separator + "DISTRIBUTION.xml",
				sInputFilePath,
				sVersion,
				tp,
				sEncoding);
		initialize(sInputFilePath, sEncoding, tp);
	}
}
