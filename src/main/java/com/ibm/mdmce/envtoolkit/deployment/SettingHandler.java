/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment;

import com.ibm.mdmce.envtoolkit.deployment.model.BasicEntity;
import com.ibm.mdmce.envtoolkit.deployment.model.Setting;
import com.ibm.mdmce.envtoolkit.deployment.model.User;
import com.ibm.mdmce.envtoolkit.deployment.model.TemplateParameters;

import java.io.File;

/**
 * Marshals information from the Settings.csv and handles transformation to relevant XML(s).
 * The expected file format is defined within the Setting class.
 *
 * @see Setting
 */
public class SettingHandler extends BasicEntityHandler {

    public SettingHandler(String sInputFilePath, String sVersion, TemplateParameters tp, String sEncoding) {
        super(Setting.getInstance(),
                "Settings.csv",//Name of the input csv file
                "MY_SETTINGS" + File.separator + "MY_SETTINGS.xml",//Path and name of the output xml file
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
        Setting setting = (Setting) oEntity;
        String userName = setting.getUsername();
        bValid = validateExists(userName, User.class.getName(), setting.getUniqueId()) && bValid;
        return bValid;
    }

}
