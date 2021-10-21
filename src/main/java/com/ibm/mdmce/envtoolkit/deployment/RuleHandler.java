/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment;

import com.ibm.mdmce.envtoolkit.deployment.model.BasicEntity;
import com.ibm.mdmce.envtoolkit.deployment.model.Rule;
import com.ibm.mdmce.envtoolkit.deployment.model.TemplateParameters;

import java.io.File;

/**
 * Marshals information from the Rules.csv and handles transformation to relevant XML(s).
 * The expected file format is defined within the User class.
 *
 * @see Rule
 */
public class RuleHandler extends BasicEntityHandler {

    public RuleHandler(String sInputFilePath, String sVersion, TemplateParameters tp, String sEncoding) {
        super(Rule.getInstance(),
                "Rules.csv",
                "RULE" + File.separator + "RULE.xml",
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
        return bValid;
    }

}
