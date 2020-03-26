/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment.model;

import com.ibm.mdmce.envtoolkit.deployment.BasicEntityHandler;
import com.ibm.mdmce.envtoolkit.deployment.CSVParser;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * Processes <b>Organizations.csv</b>.
 */
public class Organization extends BasicEntity {

    public static final String NAME = "Organization Name";
    public static final String SPEC_NAME = "Spec";
    public static final String DISPLAY_ATTR = "Display Attribute";
    public static final String PATH_ATTR = "Path Attribute";
    public static final String ACG = "ACG";
    public static final String SCRIPTS = "Scripts";
    public static final String LEAVES_ONLY = "Users on Leaves Only?";

    private String name;
    private String specName;
    private String displayAttribute;
    private String pathAttribute;
    private String acg;
    private String userDefinedCoreAttrGroup = "";
    private boolean leavesOnly = false;

    private Map<String, String> scriptTypeToName = new HashMap<>();

    private static class Singleton {
        private static final Organization INSTANCE = new Organization();
    }

    /**
     * Retrieve the static definition of an Organization (ie. its columns and type information).
     * @return Organization
     */
    public static Organization getInstance() {
        return Organization.Singleton.INSTANCE;
    }

    private Organization() {
        super("ORG_HIERARCHY", "OrganizationHierarchies");
        addColumn(COUNTRY_SPECIFIC);
        addColumn(NAME);
        addColumn(SPEC_NAME);
        addColumn(DISPLAY_ATTR);
        addColumn(PATH_ATTR);
        addColumn(ACG);
        addColumn(SCRIPTS);
        addColumn(LEAVES_ONLY);
    }

    /**
     * Construct a new instance of an Organization using the provided field values.
     * @param <T> expected to be Organization whenever used by this class
     * @param aFields from which to construct the Organization
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BasicEntity> T createInstance(List<String> aFields) {
        Organization org = new Organization();
        org.name = getFieldValue(NAME, aFields);
        org.specName = getFieldValue(SPEC_NAME, aFields);
        org.displayAttribute = getFieldValue(DISPLAY_ATTR, aFields);
        org.pathAttribute = getFieldValue(PATH_ATTR, aFields);
        org.acg = getFieldValue(ACG, aFields);
        String sScripts = getFieldValue(SCRIPTS, aFields);
        for (String script : sScripts.split(",")) {
            String[] aScriptTokens = script.split("\\Q|\\E");
            if (aScriptTokens.length == 2) {
                if (aScriptTokens[0].equals("USER_DEFINED_CORE_ATTRIBUTE_GROUP")) {
                    org.userDefinedCoreAttrGroup = aScriptTokens[1];
                } else {
                    org.scriptTypeToName.put(aScriptTokens[0], aScriptTokens[1]);
                }
            }
        }
        org.leavesOnly = CSVParser.checkBoolean(getFieldValue(LEAVES_ONLY, aFields));
        return (T) org;
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

        outFile.write("   <ORG_HIERARCHY>\n");

        outFile.write(getNodeXML("Name", getName()));
        outFile.write(getNodeXML("Action", "CREATE_OR_UPDATE"));
        outFile.write(getNodeXML("Spec", getSpecName()));
        if (getAcg().equals(com.ibm.mdmce.envtoolkit.deployment.model.ACG.DEFAULT_ACG))
            outFile.write("      <AccessControlGroup isDefault=\"true\"/>\n");
        else
            outFile.write(getNodeXML("AccessControlGroup", getAcg()));
        outFile.write(getNodeXML("PathAttribute", getPathAttribute()));
        outFile.write(getNodeXML("DisplayAttribute", getDisplayAttribute()));
        outFile.write(getNodeXML("UserDefinedAttributes", ""));
        outFile.write(getNodeXML("AttributeGroup", getUserDefinedCoreAttrGroup()));
        outFile.write(getNodeXML("ItemsOnlyOnLeaves", "" + isLeavesOnly()));

        Map<String, String> hmScripts = getScriptTypeToName();
        for (Map.Entry<String, String> entry : hmScripts.entrySet()) {
            String sScriptType = entry.getKey();
            String sScriptName = entry.getValue();
            String sTagXML = "";
            switch (sScriptType) {
                case "PRE_SCRIPT_NAME":
                    sTagXML = "PreProcessingScript";
                    break;
                case "ENTRY_BUILD_SCRIPT":
                    sTagXML = "EntryBuildScript";
                    break;
                case "POST_SAVE_SCRIPT_NAME":
                    sTagXML = "PostSaveScript";
                    break;
                case "SCRIPT_NAME":
                    sTagXML = "PostProcessingScript";
                    break;
            }
            outFile.write("      <" + sTagXML + "><![CDATA[" + sScriptName + "]]></" + sTagXML + ">\n");
        }

        outFile.write("   </ORG_HIERARCHY>\n");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputEntityCSV(Writer outFile, String sOutputPath) throws IOException {

        List<String> line = new ArrayList<>();

        // TODO: Handle output of user-defined core attribute collection in CSV

        StringBuilder sbScripts = new StringBuilder();
        for (Map.Entry<String, String> entry : getScriptTypeToName().entrySet()) {
            String sScriptType = entry.getKey();
            String sScriptName = entry.getValue();
            sbScripts.append(",").append(sScriptType).append("|").append(sScriptName);
        }
        String sScripts = sbScripts.toString();
        if (!sScripts.equals(""))
            sScripts = sScripts.substring(1);

        line.add("");
        line.add(getName());
        line.add(getSpecName());
        line.add(getDisplayAttribute());
        line.add(getPathAttribute());
        line.add(getAcg());
        line.add(sScripts);
        line.add("" + isLeavesOnly());

        outputCSV(line, outFile);

    }

    /**
     * Retrieve the name of this instance of an organization.
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieve the spec name of this instance of an organization.
     * @return String
     */
    public String getSpecName() {
        return specName;
    }

    /**
     * Retrieve the display attribute of this instance of an organization.
     * @return String
     */
    public String getDisplayAttribute() {
        return displayAttribute;
    }

    /**
     * Retrieve the path attribute of this instance of an organization.
     * @return String
     */
    public String getPathAttribute() {
        return pathAttribute;
    }

    /**
     * Retrieve the access control group of this instance of an organization.
     * @return String
     */
    public String getAcg() {
        return acg;
    }

    /**
     * Retrieve the user-defined core attribute group for this instance of an organization.
     * @return String
     */
    public String getUserDefinedCoreAttrGroup() {
        return userDefinedCoreAttrGroup;
    }

    /**
     * Indicates whether this instance of an organization only allows users on leaves (true) or not (false).
     * @return boolean
     */
    public boolean isLeavesOnly() {
        return leavesOnly;
    }

    /**
     * Retrieve a mapping from script type to script name for this instance of an organization.
     * @return {@code Map<String, String>}
     */
    public Map<String, String> getScriptTypeToName() {
        return scriptTypeToName;
    }

}
