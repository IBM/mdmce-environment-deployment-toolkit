/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment;

import com.ibm.mdmce.envtoolkit.deployment.model.*;

import java.io.*;

/**
 * Marshals information from the Selections.csv and handles transformation to relevant XML(s).
 * The expected file format is defined within the Selection class.
 *
 * @see Selection
 */
public class SelectionHandler extends BasicEntityHandler {

	public SelectionHandler(String sInputFilePath, String sVersion, TemplateParameters tp, String sEncoding) {
		super(Selection.getInstance(),
				"Selections.csv",
				"SELECTION" + File.separator + "SELECTION.xml",
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
		Selection sel = (Selection) oEntity;
		bValid = validateExists(sel.getCatalog(), Catalog.class.getName(), sel.getName()) && bValid;
		bValid = validateExists(sel.getHierarchy(), Hierarchy.class.getName(), sel.getName()) && bValid;
		bValid = validateExists(sel.getAcg(), ACG.class.getName(), sel.getName()) && bValid;
		return bValid;
	}
	
}
