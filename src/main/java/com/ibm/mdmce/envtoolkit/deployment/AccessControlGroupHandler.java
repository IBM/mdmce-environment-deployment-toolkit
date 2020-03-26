/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment;

import com.ibm.mdmce.envtoolkit.deployment.model.ACG;
import com.ibm.mdmce.envtoolkit.deployment.model.TemplateParameters;

import java.io.File;

/**
 * Marshals information from the ACGs.csv and handles transformation to relevant XML(s).
 * The expected file format is defined within the AccessControlGroup class.
 *
 * @see ACG
 */
public class AccessControlGroupHandler extends BasicEntityHandler {
    public AccessControlGroupHandler(String sInputFilePath, String sVersion, TemplateParameters tp, String sEncoding) {
        super(ACG.getInstance(),
                "ACGs.csv",
                "ACG" + File.separator + "ACG.xml",
                sInputFilePath,
                sVersion,
                tp,
                sEncoding);
        initialize(sInputFilePath, sEncoding, tp);
    }
}
