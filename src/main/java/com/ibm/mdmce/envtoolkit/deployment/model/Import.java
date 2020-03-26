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
 * Processes <b>Imports.csv</b>.
 */
public class Import extends BasicEntity {

    public static final String NAME = "Import Name";
    public static final String CATALOG = "Catalog Name";
    public static final String HIERARCHY = "Hierarchy Name";
    public static final String TYPE = "Import Type";
    public static final String SEMANTIC = "Import Semantic";
    public static final String CHARSET = "Character Set";
    public static final String FILE_SPEC = "File Spec Name";
    public static final String SPEC_MAP = "Spec Map Name";
    public static final String DATA_SOURCE = "Data Source Name";
    public static final String SCRIPT = "Import Script Name";
    public static final String ACG = "ACG";
    public static final String IS_COLLAB = "Is collaboration area?";
    public static final String WFL_STEP = "Workflow Step Path";
    public static final String APPROVAL_USER = "Approval User";
    public static final String PARAMS_NAME = "Parameters Name";

    private String name;
    private String catalog;
    private String hierarchy;
    private String type;
    private String semantic = "U";
    private String charset;
    private String fileSpec;
    private String specMap;
    private String dataSource;
    private String script;
    private String scriptPathPrefix;
    private String acg = com.ibm.mdmce.envtoolkit.deployment.model.ACG.DEFAULT_ACG;
    private boolean collaborationArea = false;
    private String workflowStep;
    private String approvalUser;
    private String paramsName;
    private String inputSpec;
    private String paramsPath = "params/None";

    private static class Singleton {
        private static final Import INSTANCE = new Import();
    }

    /**
     * Retrieve the static definition of an ImportFeed (ie. its columns and type information).
     * @return ImportFeed
     */
    public static Import getInstance() {
        return Import.Singleton.INSTANCE;
    }

    private Import() {
        super("FEEDS", "Feeds");
        addColumn(COUNTRY_SPECIFIC);
        addColumn(NAME);
        addColumn(CATALOG);
        addColumn(HIERARCHY);
        addColumn(TYPE);
        addColumn(SEMANTIC);
        addColumn(CHARSET);
        addColumn(FILE_SPEC);
        addColumn(SPEC_MAP);
        addColumn(DATA_SOURCE);
        addColumn(SCRIPT);
        addColumn(ACG);
        addColumn(IS_COLLAB);
        addColumn(WFL_STEP);
        addColumn(APPROVAL_USER);
        addColumn(PARAMS_NAME);
    }

    /**
     * Construct a new instance of an Import Feed using the provided field values.
     * @param <T> expected to be ImportFeed whenever used by this class
     * @param aFields from which to construct the Import Feed
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BasicEntity> T createInstance(List<String> aFields) {
        Import feed = new Import();
        feed.name = getFieldValue(NAME, aFields);
        feed.catalog = getFieldValue(CATALOG, aFields);
        feed.hierarchy = getFieldValue(HIERARCHY, aFields);
        feed.type = getFieldValue(TYPE, aFields);
        feed.semantic = getFieldValue(SEMANTIC, aFields);
        feed.charset = getFieldValue(CHARSET, aFields);
        feed.fileSpec = getFieldValue(FILE_SPEC, aFields);
        feed.specMap = getFieldValue(SPEC_MAP, aFields);
        feed.dataSource = getFieldValue(DATA_SOURCE, aFields);
        feed.script = getFieldValue(SCRIPT, aFields);
        if (feed.type.equals("ITM"))
            feed.scriptPathPrefix = "/scripts/import/ctg/";
        else if (feed.type.equals("CTR"))
            feed.scriptPathPrefix = "/scripts/import/ctr/";
        feed.acg = getFieldValue(ACG, aFields);
        feed.collaborationArea = CSVParser.checkBoolean(getFieldValue(IS_COLLAB, aFields));
        feed.workflowStep = getFieldValue(WFL_STEP, aFields);
        feed.approvalUser = getFieldValue(APPROVAL_USER, aFields);
        feed.paramsName = getFieldValue(PARAMS_NAME, aFields);
        String sScriptName = feed.scriptPathPrefix + feed.script;
        Script docScript = (Script) BasicEntityHandler.getFromCache(sScriptName, Script.class.getName(), false, false);
        if (docScript != null) {
            feed.inputSpec = docScript.getInputSpec();
            if (!feed.inputSpec.equals(""))
                feed.paramsPath = "/params/" + docScript.getInputSpec() + "/" + feed.paramsName;
        }
        if (feed.paramsPath.equals(""))
            feed.paramsPath = "params/None";
        return (T) feed;
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

        String sFeedType = getType();

        outFile.write("   <FEEDS>\n");
        if (sFeedType.equals("IMG")) {
            // TODO: Support binary feed options
            outFile.write(getNodeXML("ZIPKeepPathFeed", "false"));
            outFile.write(getNodeXML("ZIPDocStorePath", "/tmp/"));
            outFile.write(getNodeXML("ZIPFileAction", "N"));
        } else {
            outFile.write(getNodeXML("DocStorePath", getParamsPath()));
        }
        outFile.write(getNodeXML("Name", getName()));
        outFile.write(getNodeXML("Action", "CREATE_OR_UPDATE"));
        if (isCollaborationArea()) {
            outFile.write(getNodeXML("CollabArea", "true"));
            outFile.write(getNodeXML("WorkFlowStepPath", getWorkflowStep()));
        }
        if (sFeedType.equals("CTR")) {
            outFile.write(getNodeXML("CatTreeName", getHierarchy()));
        } else if (sFeedType.equals("ITM")) {
            outFile.write(getNodeXML("SpecName", getFileSpec()));
            outFile.write(getNodeXML("CatalogName", getCatalog()));
            outFile.write(getNodeXML("SpecMap", getSpecMap()));
        }
        outFile.write(getNodeXML("FeedType", sFeedType));
        outFile.write(getNodeXML("Semantic", getSemantic()));
        outFile.write(getNodeXML("CharSet", getCharset()));
        if (!sFeedType.equals("IMG")) {
            outFile.write(getNodeXML("ScriptPath", getScriptPathPrefix() + getScript()));
        }
        if (getAcg().equals(com.ibm.mdmce.envtoolkit.deployment.model.ACG.DEFAULT_ACG))
            outFile.write("      <AccessControl isDefault=\"true\"/>\n");
        else
            outFile.write(getNodeXML("AccessControl", getAcg()));
        outFile.write(getNodeXML("DataSource", getDataSource()));
        outFile.write("   </FEEDS>\n");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputEntityCSV(Writer outFile, String sOutputPath) throws IOException {
        List<String> line = new ArrayList<>();
        line.add("");
        line.add(getName());
        line.add(getCatalog());
        line.add(getHierarchy());
        line.add(getType());
        line.add(getSemantic());
        line.add(getCharset());
        line.add(getFileSpec());
        line.add(getSpecMap());
        line.add(getDataSource());
        line.add(getScript());
        line.add(getAcg());
        line.add("" + isCollaborationArea());
        line.add(getWorkflowStep());
        line.add(getApprovalUser());
        line.add(getParamsName());
        outputCSV(line, outFile);
    }

    /**
     * Retrieve the name of this instance of an import feed.
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieve the catalog for this instance of an import feed.
     * @return String
     */
    public String getCatalog() {
        return catalog;
    }

    /**
     * Retrieve the hierarchy for this instance of an import feed.
     * @return String
     */
    public String getHierarchy() {
        return hierarchy;
    }

    /**
     * Retrieve the type of this instance of an import feed.
     * @return String
     */
    public String getType() {
        return type;
    }

    /**
     * Retrieve the semantic for this instance of an import feed.
     * @return String
     */
    public String getSemantic() {
        return semantic;
    }

    /**
     * Retrieve the character encoding for this instance of an import feed.
     * @return String
     */
    public String getCharset() {
        return charset;
    }

    /**
     * Retrieve the file spec for this instance of an import feed.
     * @return String
     */
    public String getFileSpec() {
        return fileSpec;
    }

    /**
     * Retrieve the spec map for this instance of an import feed.
     * @return String
     */
    public String getSpecMap() {
        return specMap;
    }

    /**
     * Set the spec map for this instance of an import feed.
     * @param specMap to set
     */
    public void setSpecMap(String specMap) {
        this.specMap = specMap;
    }

    /**
     * Retrieve the data source for this instance of an import feed.
     * @return String
     */
    public String getDataSource() {
        return dataSource;
    }

    /**
     * Retrieve the location for the script for this instance of an import feed.
     * @return String
     */
    public String getScriptPathPrefix() {
        return scriptPathPrefix;
    }

    /**
     * Retrieve the name of the script for this instance of an import feed.
     * @return String
     */
    public String getScript() {
        return script;
    }

    /**
     * Retrieve the access control group for this instance of an import feed.
     * @return String
     */
    public String getAcg() {
        return acg;
    }

    /**
     * Indicates whether this instance of an import feed uses a collaboration area (true) or not (false).
     * @return boolean
     */
    public boolean isCollaborationArea() {
        return collaborationArea;
    }

    /**
     * Retrieve the workflow step path for this instance of an import feed.
     * @return String
     */
    public String getWorkflowStep() {
        return workflowStep;
    }

    /**
     * Retrieve the approval user for this instance of an import feed.
     * @return String
     */
    public String getApprovalUser() {
        return approvalUser;
    }

    /**
     * Retrieve the name of the parameters for this instance of an import feed.
     * @return String
     */
    public String getParamsName() {
        return paramsName;
    }

    /**
     * Retrieve the input spec for the parameters for this instance of an import feed.
     * @return String
     */
    public String getInputSpec() {
        return inputSpec;
    }

    /**
     * Retrieve the full path to the parameters for this instance of an import feed.
     * @return String
     */
    public String getParamsPath() {
        return paramsPath;
    }

}
