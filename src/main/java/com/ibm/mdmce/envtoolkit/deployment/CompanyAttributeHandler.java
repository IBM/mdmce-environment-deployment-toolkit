/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment;

import com.ibm.mdmce.envtoolkit.deployment.model.CompanyAttribute;
import com.ibm.mdmce.envtoolkit.deployment.model.TemplateParameters;

import java.io.File;

/**
 * Marshals information from the CompanyAttributes.csv and handles transformation to relevant XML(s).
 * The expected file format is defined within the CompanyAttribute class.
 *
 * @see CompanyAttribute
 */
public class CompanyAttributeHandler extends BasicEntityHandler {
    public CompanyAttributeHandler(String sInputFilePath, String sVersion, TemplateParameters tp, String sEncoding) {
        super(CompanyAttribute.getInstance(),
                "CompanyAttributes.csv",
                "COMPANY_ATTRIBUTES" + File.separator + "COMPANY_ATTRIBUTES.xml",
                sInputFilePath,
                sVersion,
                tp,
                sEncoding);
        initialize(sInputFilePath, sEncoding, tp);
    }
}
