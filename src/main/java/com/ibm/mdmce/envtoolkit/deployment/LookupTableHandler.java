/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment;

import com.ibm.mdmce.envtoolkit.deployment.model.BasicEntity;
import com.ibm.mdmce.envtoolkit.deployment.model.Lookup;
import com.ibm.mdmce.envtoolkit.deployment.model.Spec;
import com.ibm.mdmce.envtoolkit.deployment.model.TemplateParameters;

import java.io.File;

/**
 * Marshals information from the Lookups.csv and handles transformation to relevant XML(s).
 * The expected file format is defined within the LookupTable class.
 *
 * @see Lookup
 */
public class LookupTableHandler extends BasicEntityHandler {
	
	public LookupTableHandler(String sInputFilePath, String sVersion, TemplateParameters tp, String sEncoding) {
		super(Lookup.getInstance(),
				"Lookups.csv",
				"LOOKUP_TABLE" + File.separator + "LOOKUP_TABLE.xml",
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
		Lookup lkp = (Lookup) oEntity;
		bValid = validateExists(lkp.getSpecName(), Spec.class.getName(), lkp.getName()) && bValid;
		return bValid;
	}
	
}
