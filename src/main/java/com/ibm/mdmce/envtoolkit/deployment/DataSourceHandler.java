/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment;

import com.ibm.mdmce.envtoolkit.deployment.model.DataSource;
import com.ibm.mdmce.envtoolkit.deployment.model.TemplateParameters;

import java.io.*;

/**
 * Marshals information from the DataSources.csv and handles transformation to relevant XML(s).
 * The expected file format is defined within the DataSource class.
 *
 * @see DataSource
 */
public class DataSourceHandler extends BasicEntityHandler {
	public DataSourceHandler(String sInputFilePath, String sVersion, TemplateParameters tp, String sEncoding) {
		super(DataSource.getInstance(),
				"DataSources.csv",
				"DATASOURCE" + File.separator + "DATASOURCE.xml",
				sInputFilePath,
				sVersion,
				tp,
				sEncoding);
		initialize(sInputFilePath, sEncoding, tp);
	}
}
