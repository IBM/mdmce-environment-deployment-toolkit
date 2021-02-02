/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment.model;

import com.ibm.mdmce.envtoolkit.deployment.BasicEntityHandler;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Processes <b>Exports.csv</b>.
 */
public class Export extends BasicEntity {

    public static final String NAME = "Export Name";
    public static final String CONTAINER_NAME = "Container Name";
    public static final String HIERARCHY_NAME = "Hierarchy Name";
    public static final String TYPE = "Syndication Type";
    public static final String CHARSET = "Character Set";
    public static final String DESTINATION_SPEC = "Destination Spec Name";
    public static final String SPEC_MAP = "Spec Map Name";
    public static final String SCRIPT_NAME = "Export Script Name";
    public static final String APPROVAL_USER = "Approval User";
    public static final String DISTRIBUTION_NAME = "Distribution Name";
    public static final String DISTRIBUTION_GROUP = "Distribution Group Name";
    public static final String SELECTION = "Selection";
    public static final String DIFF_TYPE = "Difference Type";
    public static final String PARAMS_NAME = "Parameters Name";

    private String name;
    private String containerName;
    private String type;
    private String charset;
    private String destinationSpec;
    private String specMap;
    private String scriptName;
    private String approvalUser;
    private String distributionName;
    private String distributionGroup;
    private String selection;
    private String diffType;
    private String paramsName;
    private String paramsPath="";
    private String hierarchyName;
    private String inputSpec;

    private static class Singleton {
        private static final Export INSTANCE = new Export();
    }

    /**
     * Retrieve the static definition of an Export (ie. its columns and type information).
     * @return Export
     */
    public static Export getInstance() {
        return Export.Singleton.INSTANCE;
    }

    private Export() {
        super("EXPORTS", "Syndication");
        addColumn(COUNTRY_SPECIFIC);
        addColumn(NAME);
        addColumn(CONTAINER_NAME);
        addColumn(HIERARCHY_NAME);
        addColumn(TYPE);
        addColumn(CHARSET);
        addColumn(DESTINATION_SPEC);
        addColumn(SPEC_MAP);
        addColumn(SCRIPT_NAME);
        addColumn(APPROVAL_USER);
        addColumn(DISTRIBUTION_NAME);
        addColumn(DISTRIBUTION_GROUP);
        addColumn(SELECTION);
        addColumn(DIFF_TYPE);
        addColumn(PARAMS_NAME);
    }

    /**
     * Construct a new instance of an Export using the provided field values.
     * @param <T> expected to be Export whenever used by this class
     * @param aFields from which to construct the Export
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BasicEntity> T createInstance(List<String> aFields) {
        Export export = new Export();
        export.name = getFieldValue(NAME, aFields);
        export.containerName = getFieldValue(CONTAINER_NAME, aFields);
        export.type = getFieldValue(TYPE, aFields);
        export.charset = getFieldValue(CHARSET, aFields);
        export.destinationSpec = getFieldValue(DESTINATION_SPEC, aFields);
        export.specMap = getFieldValue(SPEC_MAP, aFields);
        export.scriptName = getFieldValue(SCRIPT_NAME, aFields);
        export.approvalUser = getFieldValue(APPROVAL_USER, aFields);
        export.distributionName = getFieldValue(DISTRIBUTION_NAME, aFields);
        export.distributionGroup = getFieldValue(DISTRIBUTION_GROUP, aFields);
        export.selection = getFieldValue(SELECTION, aFields);
        export.diffType = getFieldValue(DIFF_TYPE, aFields);
        export.paramsName = getFieldValue(PARAMS_NAME, aFields);
        String sScriptPath = "/scripts/export/ctg/";
        String sScriptName = sScriptPath + export.scriptName;
        Script docScript = (Script) BasicEntityHandler.getFromCache(sScriptName, Script.class.getName(), false, false);
        if (docScript != null) {
            export.inputSpec = docScript.getInputSpec();
            if (!export.inputSpec.equals(""))
                export.paramsPath = "/params/" + export.inputSpec + "/" + export.paramsName;
        }
        export.hierarchyName = getFieldValue(HIERARCHY_NAME, aFields);
        return (T) export;
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
        outFile.write("   <EXPORTS name=\"" + getName() + "\">\n");
        outFile.write(getNodeXML("Action", "CREATE_OR_UPDATE"));
        outFile.write(getNodeXML("DestinationSpec", getDestinationSpec()));
        outFile.write(getNodeXML("CatalogName", getContainerName()));
        outFile.write(getNodeXML("CatalogVersion", "Latest Version"));
        // TODO: Support exports with other than entire catalog
        outFile.write(getNodeXML("CatalogGroupItems", "Entire catalog"));
        outFile.write(getNodeXML("CatalogGroupItemsType", "E"));
        outFile.write(getNodeXML("HierarchyName", getHierarchyName()));
        outFile.write(getNodeXML("SyndicationType", getType()));
        outFile.write(getNodeXML("CharsetName", getCharset()));
        outFile.write(getNodeXML("SpecMapping", getSpecMap()));
        outFile.write(getNodeXML("CatalogExportScriptPath", "scripts/export/ctg/" + getScriptName()));
        // This should apparently be the name of the script rather than the spec -- at least in 5.3.2
        outFile.write(getNodeXML("FileType", getScriptName()));
        if (!getParamsPath().equals("")) {
            outFile.write(getNodeXML("ParamsDocPath", getParamsPath()));
        }
        outFile.write("   </EXPORTS>\n");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputEntityCSV(Writer outFile, String sOutputPath) throws IOException {
        List<String> line = new ArrayList<>();
        line.add("");
        line.add(getName());
        line.add(getContainerName());
        line.add(getHierarchyName());
        line.add(getType());
        line.add(getCharset());
        line.add(getDestinationSpec());
        line.add(getSpecMap());
        line.add(getScriptName());
        line.add(getApprovalUser());
        line.add(getDistributionName());
        line.add(getDistributionGroup());
        line.add(getSelection());
        line.add(getDiffType());
        line.add(getParamsName());
        outputCSV(line, outFile);
    }

    /**
     * Retrieve the name of this instance of an export.
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieve the container name for this instance of an export.
     * @return String
     */
    public String getContainerName() {
        return containerName;
    }

    /**
     * Retrieve the type of this instance of an export.
     * @return String
     */
    public String getType() {
        return type;
    }

    /**
     * Retrieve the character set for this instance of an export.
     * @return String
     */
    public String getCharset() {
        return charset;
    }

    /**
     * Retrieve the destination spec for this instance of an export.
     * @return String
     */
    public String getDestinationSpec() {
        return destinationSpec;
    }

    /**
     * Retrieve the spec map for this instance of an export.
     * @return String
     */
    public String getSpecMap() {
        return specMap;
    }

    /**
     * Set the spec map for this instance of an export.
     * @param specMap to set
     */
    public void setSpecMap(String specMap) {
        this.specMap = specMap;
    }

    /**
     * Retrieve the script name for this instance of an export.
     * @return String
     */
    public String getScriptName() {
        return scriptName;
    }

    /**
     * Retrieve the approval user for this instance of an export.
     * @return String
     */
    public String getApprovalUser() {
        return approvalUser;
    }

    /**
     * Retrieve the distribution name for this instance of an export.
     * @return String
     */
    public String getDistributionName() {
        return distributionName;
    }

    /**
     * Retrieve the distribution group for this instance of an export.
     * @return String
     */
    public String getDistributionGroup() {
        return distributionGroup;
    }

    /**
     * Retrieve the selection for this instance of an export.
     * @return String
     */
    public String getSelection() {
        return selection;
    }

    /**
     * Retrieve the difference type for this instance of an export.
     * @return String
     */
    public String getDiffType() {
        return diffType;
    }

    /**
     * Retrieve the parameters name for this instance of an export.
     * @return String
     */
    public String getParamsName() {
        return paramsName;
    }

    /**
     * Retrieve the parameters path for this instance of an export.
     * @return String
     */
    public String getParamsPath() {
        return paramsPath;
    }

    /**
     * Retrieve the hierarchy name for this instance of an export.
     * @return String
     */
    public String getHierarchyName() {
        return hierarchyName;
    }

    /**
     * Retrieve the input spec for the parameters of this instance of an export.
     * @return String
     */
    public String getInputSpec() {
        return inputSpec;
    }
}
