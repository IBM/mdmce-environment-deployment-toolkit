/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment;

import com.ibm.mdmce.envtoolkit.deployment.model.*;

import java.io.*;

/**
 * Marshals information from the UDLs.csv and handles transformation to relevant XML(s).
 * The expected file format is defined within the UserDefinedLog class.
 *
 * @see UDL
 */
public class UserDefinedLogHandler extends BasicEntityHandler {

	public UserDefinedLogHandler(String sInputFilePath, String sVersion, TemplateParameters tp, String sEncoding) {
		super(UDL.getInstance(),
				"UDLs.csv",
				"UDL" + File.separator + "UDL.xml",
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
		UDL udl = (UDL) oEntity;
		if (udl.getContainerType().equals("CATALOG")) {
			bValid = validateExists(udl.getContainerName(), Catalog.class.getName(), udl.getName()) && bValid;
		} else {
			bValid = validateExists(udl.getContainerName(), Hierarchy.class.getName(), udl.getName()) && bValid;
		}
		return bValid;
	}

}
