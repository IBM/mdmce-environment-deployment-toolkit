/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment;

import com.ibm.mdmce.envtoolkit.deployment.model.BasicEntity;
import com.ibm.mdmce.envtoolkit.deployment.model.Role;
import com.ibm.mdmce.envtoolkit.deployment.model.User;
import com.ibm.mdmce.envtoolkit.deployment.model.TemplateParameters;

import java.io.File;

/**
 * Marshals information from the Users.csv and handles transformation to relevant XML(s).
 * The expected file format is defined within the User class.
 *
 * @see User
 */
public class UserHandler extends BasicEntityHandler {

    public UserHandler(String sInputFilePath, String sVersion, TemplateParameters tp, String sEncoding) {
        super(User.getInstance(),
                "Users.csv",
                "USERS" + File.separator + "USERS.xml",
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
        User user = (User) oEntity;
        for (String role : user.getRoles()) {
            bValid = validateExists(role, Role.class.getName(), user.getUniqueId()) && bValid;
        }
        return bValid;
    }

}
