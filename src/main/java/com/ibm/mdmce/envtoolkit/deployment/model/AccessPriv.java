/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment.model;

import com.ibm.mdmce.envtoolkit.deployment.BasicEntityHandler;
import com.ibm.mdmce.envtoolkit.deployment.CSVParser;
import com.ibm.mdmce.envtoolkit.deployment.EnvironmentHandler;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * Processes <b>AccessPrivs.csv</b>.
 */
public class AccessPriv extends BasicEntity {

    public static final String CONTAINER_NAME = "Container Name";
    public static final String CONTAINER_TYPE = "Container Type";
    public static final String ROLES = "Roles (CSV)";
    public static final String ATTRIBUTE_COLLECTION = "Attribute Collection";
    public static final String VIEW_ONLY = "View-only";

    public static final String ALL_ROLES = "All Roles";

    private String containerName;
    private String containerType;
    private List<String> roles = new ArrayList<>();
    private Map<String, Boolean> attributeCollectionToReadOnly = new TreeMap<>();

    private static class Singleton {
        private static final AccessPriv INSTANCE = new AccessPriv();
    }

    /**
     * Retrieve the static definition of an AccessPrivilege (ie. its columns and type information).
     * @return AccessPrivilege
     */
    public static AccessPriv getInstance() {
        return AccessPriv.Singleton.INSTANCE;
    }

    private AccessPriv() {
        super("CONTAINER_ACCESSPRV", "ContainerAccessPrivileges");
        addColumn(COUNTRY_SPECIFIC);
        addColumn(CONTAINER_NAME);
        addColumn(CONTAINER_TYPE);
        addColumn(ROLES);
        addColumn(ATTRIBUTE_COLLECTION);
        addColumn(VIEW_ONLY);
    }

    /**
     * Construct a new instance of an Access Privilege using the provided field values.
     * @param <T> expected to be AccessPrivilege whenever used by this class
     * @param aFields from which to construct the Access Privilege
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BasicEntity> T createInstance(List<String> aFields) {
        AccessPriv ap = new AccessPriv();
        ap.containerName = getFieldValue(CONTAINER_NAME, aFields);
        ap.containerType = getFieldValue(CONTAINER_TYPE, aFields);
        String sRoles = getFieldValue(ROLES, aFields);
        ap.roles = Arrays.asList(sRoles.split(","));
        String sAttrCollectionName = getFieldValue(ATTRIBUTE_COLLECTION, aFields);
        if (ap.roles.isEmpty()) {
            ap.roles = new ArrayList<>();
            ap.roles.add(ALL_ROLES); // default
        }
        // try to retrieve from cache
        // (with one of the roles since they point to the same object if configured for several roles)
        String sRole = ap.roles.get(0); // just one role needed to retrieve the object
        AccessPriv known = (AccessPriv) EnvironmentHandler.getEntityFromCache(getUniqueIdFromRole(sRole), AccessPriv.class.getName(), false, false);
        if (known != null) {
            ap = known;
        }
        if (sAttrCollectionName != null && !sAttrCollectionName.equals("")) {
            ap.attributeCollectionToReadOnly.put(sAttrCollectionName, CSVParser.checkBoolean(getFieldValue(VIEW_ONLY, aFields)));
        }
        return (T) ap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUniqueId() {
        return null;
    }

    /**
     * Retrieve the unique identification of this access privilege for the specified role.
     * @param sRoleName name of the role
     * @return String
     */
    public String getUniqueIdFromRole(String sRoleName) {
        return getContainerName() + "::" + getContainerType() + "::" + sRoleName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputEntityXML(BasicEntityHandler handler, Writer outFile, String sOutputPath, String sCompanyCode) throws IOException {

        for (String role : getRoles()) {
            outFile.write("   <CONTAINER_ACCESSPRV>\n");
            outFile.write(getNodeXML("Name", getContainerName()));
            outFile.write(getNodeXML("Action", "CREATE_OR_UPDATE"));
            outFile.write(getNodeXML("Type", getContainerType()));
            outFile.write(getNodeXML("Role", role));
            outFile.write(getNodeXML("CompanyCode", sCompanyCode));
            for (Map.Entry<String, Boolean> entry : getAttributeCollectionToReadOnly().entrySet()) {
                String sAttrCollectionName = entry.getKey();
                boolean isReadOnly = entry.getValue();
                outFile.write("      <AttrCollection name=\"" + sAttrCollectionName + "\">\n");
                outFile.write("         <View>" + (isReadOnly ? "true" : "false") + "</View>\n");
                outFile.write("         <Edit>" + (isReadOnly ? "false" : "true") + "</Edit>\n");
                outFile.write("      </AttrCollection>\n");
            }
            outFile.write("   </CONTAINER_ACCESSPRV>\n");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputEntityCSV(Writer outFile, String sOutputPath) throws IOException {
        List<String> line = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : getAttributeCollectionToReadOnly().entrySet()) {
            String sAttrCollectionName = entry.getKey();
            boolean isReadOnly = entry.getValue();
            line.add("");
            line.add(getContainerName());
            line.add(getContainerType());
            line.add(escapeForCSV(String.join(",", getRoles())));
            line.add(sAttrCollectionName);
            line.add( (isReadOnly ? "x" : "") );
            outputCSV(line, outFile);
        }
    }

    /**
     * Retrieve the name of the container for this instance of an access privilege.
     * @return String
     */
    public String getContainerName() {
        return containerName;
    }

    /**
     * Retrieve the type of container for this instance of an access privilege.
     * @return String
     */
    public String getContainerType() {
        return containerType;
    }

    /**
     * Retrieve the list of roles that apply to this instance of an access privilege.
     * @return {@code List<String>}
     */
    public List<String> getRoles() {
        return roles;
    }

    /**
     * Retrieve the mapping from attribute collection name to read-only (true) or editable (false) for this instance of
     * an access privilege.
     * @return {@code Map<String, Boolean>}
     */
    public Map<String, Boolean> getAttributeCollectionToReadOnly() {
        return attributeCollectionToReadOnly;
    }

}
