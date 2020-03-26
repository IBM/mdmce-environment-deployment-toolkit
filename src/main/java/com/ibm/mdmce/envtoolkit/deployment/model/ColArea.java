/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment.model;

import com.ibm.mdmce.envtoolkit.deployment.BasicEntityHandler;
import com.ibm.mdmce.envtoolkit.deployment.CSVParser;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Processes <b>ColAreas.csv</b>.
 */
public class ColArea extends BasicEntity {

    public static final String NAME = "Collaboration Area Name";
    public static final String WORKFLOW = "Workflow";
    public static final String SRC_CONTAINER = "Source Container";
    public static final String CONTAINER_TYPE = "Container Type";
    public static final String ACG = "ACG";
    public static final String ADMIN_ROLES = "Admin Roles";
    public static final String ADMIN_USERS = "Admin Users";

    private String name;
    private String workflow;
    private String sourceContainer;
    private String containerType;
    private String acg = com.ibm.mdmce.envtoolkit.deployment.model.ACG.DEFAULT_ACG;

    private List<String> adminRoles = new ArrayList<>();
    private List<String> adminUsers = new ArrayList<>();

    private static class Singleton {
        private static final ColArea INSTANCE = new ColArea();
    }

    /**
     * Retrieve the static definition of a CollaborationArea (ie. its columns and type information).
     * @return CollaborationArea
     */
    public static ColArea getInstance() {
        return ColArea.Singleton.INSTANCE;
    }

    private ColArea() {
        super("COLLABORATION_AREA", "CollaborationAreas");
        addColumn(COUNTRY_SPECIFIC);
        addColumn(NAME);
        addColumn(WORKFLOW);
        addColumn(SRC_CONTAINER);
        addColumn(CONTAINER_TYPE);
        addColumn(ACG);
        addColumn(ADMIN_ROLES);
        addColumn(ADMIN_USERS);
    }

    /**
     * Construct a new instance of an Access Control Group using the provided field values.
     * @param <T> expected to be AccessControlGroup whenever used by this class
     * @param aFields from which to construct the Access Control Group
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BasicEntity> T createInstance(List<String> aFields) {
        ColArea colArea = new ColArea();
        colArea.name = getFieldValue(NAME, aFields);
        colArea.workflow = getFieldValue(WORKFLOW, aFields);
        colArea.sourceContainer = getFieldValue(SRC_CONTAINER, aFields);
        colArea.containerType = getFieldValue(CONTAINER_TYPE, aFields);
        colArea.acg = getFieldValue(ACG, aFields);
        colArea.adminRoles = CSVParser.checkList(getFieldValue(ADMIN_ROLES, aFields), ",");
        colArea.adminUsers = CSVParser.checkList(getFieldValue(ADMIN_USERS, aFields), ",");
        return (T) colArea;
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
        // TODO: Support outputting deleteWhenEmpty colareas
        outFile.write("   <COLLABORATION_AREA Deletewhenempty=\"false\">\n");
        outFile.write(getNodeXML("Name", getName()));
        outFile.write(getNodeXML("Action", "CREATE_OR_UPDATE"));
        // TODO: Support descriptions on colareas
        outFile.write(getNodeXML("Description", ""));
        outFile.write(getNodeXML("Workflow", getWorkflow()));
        outFile.write(getNodeXML("ContainerType", getContainerType()));
        outFile.write(getNodeXML("Container", getSourceContainer()));
        if (getAcg().equals(com.ibm.mdmce.envtoolkit.deployment.model.ACG.DEFAULT_ACG))
            outFile.write("      <AccessControlGroup isDefault=\"true\"/>\n");
        else
            outFile.write(getNodeXML("AccessControlGroup", getAcg()));
        outFile.write("      <Administrators>\n");
        for (String sRoleName : getAdminRoles()) {
            outFile.write("         <Role>" + sRoleName + "</Role>\n");
        }
        for (String sUserName : getAdminUsers()) {
            outFile.write("         <User>" + sUserName + "</User>\n");
        }
        outFile.write("      </Administrators>\n");
        // TODO: Support timeouts on colareas (?)
        outFile.write("      <Timeout>\n");
        outFile.write("         <Duration Unit=\"Milliseconds\">0</Duration>\n");
        outFile.write("      </Timeout>\n");
        outFile.write("   </COLLABORATION_AREA>\n");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputEntityCSV(Writer outFile, String sOutputPath) throws IOException {
        List<String> line = new ArrayList<>();
        line.add("");
        line.add(getName());
        line.add(getWorkflow());
        line.add(getSourceContainer());
        line.add(getContainerType());
        line.add(getAcg());
        line.add(escapeForCSV(String.join(",", getAdminRoles())));
        line.add(escapeForCSV(String.join(",", getAdminUsers())));
        outputCSV(line, outFile);
    }

    /**
     * Retrieve the name of this instance of a collaboration area.
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieve the workflow for this instance of a collaboration area.
     * @return String
     */
    public String getWorkflow() {
        return workflow;
    }

    /**
     * Retrieve the source container for this instance of a collaboration area.
     * @return String
     */
    public String getSourceContainer() {
        return sourceContainer;
    }

    /**
     * Retrieve the container type for this instance of a collaboration area.
     * @return String
     */
    public String getContainerType() {
        return containerType;
    }

    /**
     * Retrieve the access control group for this instance of a collaboration area.
     * @return String
     */
    public String getAcg() {
        return acg;
    }

    /**
     * Retrieve the roles that can administer this instance of a collaboration area.
     * @return {@code List<String>}
     */
    public List<String> getAdminRoles() {
        return adminRoles;
    }

    /**
     * Retrieve the users that can administer this instance of a collaboration area.
     * @return {@code List<String>}
     */
    public List<String> getAdminUsers() {
        return adminUsers;
    }

}
