/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment;

import com.ibm.mdmce.envtoolkit.deployment.model.*;

import java.io.*;

/**
 * Marshals information from the WebServices.csv and handles transformation to relevant XML(s).
 * The expected file format is defined within the WebService class.
 *
 * @see WebService
 */
public class WebServiceHandler extends BasicEntityHandler {
		
	private String inputPath;

	public WebServiceHandler(String sInputFilePath, String sVersion, TemplateParameters tp, String sEncoding) {
		super(WebService.getInstance(),
				"WebServices.csv",
				"WEBSERVICE" + File.separator + "WEBSERVICE.xml",
				sInputFilePath,
				sVersion,
				tp,
				sEncoding);
		inputPath = sInputFilePath;
		initialize(sInputFilePath, sEncoding, tp);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean validate(BasicEntity oEntity) {
		boolean bValid = true;
		WebService websvc = (WebService) oEntity;
		String sScriptPath = "/scripts/wbs/";
		String sWSDLPath = "/archives/wsdl/";
		bValid = validateExists(sScriptPath + websvc.getScript(), Script.class.getName(), websvc.getName()) && bValid;
		bValid = validateExists(sWSDLPath + websvc.getWsdl(), Script.class.getName(), websvc.getName()) && bValid;
		return bValid;
	}

	/**
	 * Retrieve the path used for input.
	 * @return String
	 */
	public String getInputPath() {
		return inputPath;
	}

}
