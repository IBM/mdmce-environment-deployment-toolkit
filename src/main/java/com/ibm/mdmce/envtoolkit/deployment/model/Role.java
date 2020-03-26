/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment.model;

import com.ibm.mdmce.envtoolkit.deployment.BasicEntityHandler;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * Processes <b>Roles.csv</b>.
 */
public class Role extends BasicEntity {

    public static final String NAME = "Role Name";
    public static final String DESCRIPTION = "Role Description";

    private String name;
    private String description;

    private static class Singleton {
        private static final Role INSTANCE = new Role();
    }

    /**
     * Retrieve the static definition of a Role (ie. its columns and type information).
     * @return Role
     */
    public static Role getInstance() {
        return Role.Singleton.INSTANCE;
    }

    private Role() {
        super("UNUSED", "Roles");
        addColumn(COUNTRY_SPECIFIC);
        addColumn(NAME);
        addColumn(DESCRIPTION);
    }

    /**
     * Construct a new instance of a Role using the provided field values.
     * @param <T> expected to be Role whenever used by this class
     * @param aFields from which to construct the Role
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BasicEntity> T createInstance(List<String> aFields) {
        Role role = new Role();
        role.name = getFieldValue(NAME, aFields);
        role.description = getFieldValue(DESCRIPTION, aFields);
        return (T) role;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUniqueId() {
        return getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputEntityXML(BasicEntityHandler handler, Writer outFile, String sOutputPath, String sCompanyCode) throws IOException {

        String sRoleName = getName();

        String sEscapedRoleName = BasicEntityHandler.escapeForFilename(sRoleName);
        String sRoleFilePath = sOutputPath + File.separator + "ROLES" + File.separator + "ROLE_" + sEscapedRoleName + ".xml";
        Writer outRole = BasicEntityHandler.getNewWriter(sRoleFilePath);

        outRole.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
        outRole.write("<Roles version=\"" + handler.getVersion() + "\">\n");
        outRole.write("   <ROLES>\n");
        outRole.write(getNodeXML("Name", sRoleName));
        outRole.write(getNodeXML("Action", "CREATE_OR_UPDATE"));
        outRole.write(getNodeXML("Description", getDescription()));

        RoleToACG rm = (RoleToACG) BasicEntityHandler.getFromCache(sRoleName, RoleToACG.class.getName(), false, false);

        if (rm != null) {
            Map<String, List<String>> hmACGs = rm.getAcgMappings();
            for (Map.Entry<String, List<String>> entry : hmACGs.entrySet()) {
                String sACG = entry.getKey();
                List<String> aPrivs = entry.getValue();
                if (sACG.equals(ACG.DEFAULT_ACG))
                    outRole.write("      <AccessControlGroup isDefault=\"true\">\n");
                else
                    outRole.write("      <AccessControlGroup name=\"" + BasicEntityHandler.escapeForXML(sACG)  + "\">\n");
                for (String priv : aPrivs) {
                    if (!priv.startsWith("PAGE_OBJ_") || sACG.equals(ACG.DEFAULT_ACG))
                        outRole.write("         <Privilege><![CDATA[" + priv + "]]></Privilege>\n");
                }
                outRole.write("      </AccessControlGroup>\n");
            }
        }

        outRole.write("      <CompanyCode>" + sCompanyCode + "</CompanyCode>\n");
        outRole.write("   </ROLES>\n");
        outRole.write("</Roles>\n");
        outRole.flush();

        handler.addGeneratedFileXML(sRoleFilePath);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputEntityCSV(Writer outFile, String sOutputPath) throws IOException {
        List<String> line = new ArrayList<>();
        line.add("");
        line.add(getName());
        line.add(getDescription());
        outputCSV(line, outFile);
    }

    /**
     * Retrieve the name of this instance of a role.
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieve the description of this instance of a role.
     * @return String
     */
    public String getDescription() {
        return description;
    }

}
