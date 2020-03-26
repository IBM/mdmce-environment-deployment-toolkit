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
 * Processes <b>SearchTemplates.csv</b>.
 */
public class SearchTemplate extends BasicEntity {

    public static final String NAME = "Search Template (Attribute Collection) Name";
    public static final String CONTAINER_NAME = "Container Name";
    public static final String STEP_NAME = "Step Name";
    public static final String NEW_UI = "New UI?";

    private String name;
    private String containerName;
    private String stepName;
    private boolean newUI = false;

    private static class Singleton {
        private static final SearchTemplate INSTANCE = new SearchTemplate();
    }

    /**
     * Retrieve the static definition of a SearchTemplate (ie. its columns and type information).
     * @return SearchTemplate
     */
    public static SearchTemplate getInstance() {
        return SearchTemplate.Singleton.INSTANCE;
    }

    private SearchTemplate() {
        super("SEARCH_TEMPLATES", "SearchTemplates");
        addColumn(COUNTRY_SPECIFIC);
        addColumn(NAME);
        addColumn(CONTAINER_NAME);
        addColumn(STEP_NAME);
        addColumn(NEW_UI);
    }

    /**
     * Construct a new instance of a Search Template using the provided field values.
     * @param <T> expected to be SearchTemplate whenever used by this class
     * @param aFields from which to construct the Search Template
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BasicEntity> T createInstance(List<String> aFields) {
        SearchTemplate st = new SearchTemplate();
        st.name = getFieldValue(NAME, aFields);
        st.containerName = getFieldValue(CONTAINER_NAME, aFields);
        st.stepName = getFieldValue(STEP_NAME, aFields);
        st.newUI = CSVParser.checkBoolean(getFieldValue(NEW_UI, aFields));
        return (T) st;
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
        outFile.write("   <SEARCH_TEMPLATES>\n");
        outFile.write(getNodeXML("Name", getName()));
        outFile.write(getNodeXML("Action", "CREATE_OR_UPDATE"));
        outFile.write(getNodeXML("ContainerName", getContainerName()));
        outFile.write(getNodeXML("StepName", getStepName()));
        if (handler.getVersion().startsWith("9")) {
            String sNewUI = "NO";
            if (isNewUI()) {
                sNewUI = "YES";
            }
            outFile.write(getNodeXML("IsNewUI", sNewUI));
        }
        outFile.write("   </SEARCH_TEMPLATES>\n");
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
        line.add(getStepName());
        line.add("" + isNewUI());
        outputCSV(line, outFile);
    }

    /**
     * Retrieve the name of this instance of a search template.
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieve the container name for this instance of a search template.
     * @return String
     */
    public String getContainerName() {
        return containerName;
    }

    /**
     * Retrieve the step name for this instance of a search template.
     * @return String
     */
    public String getStepName() {
        return stepName;
    }

    /**
     * Indicates whether this instance of a search template uses the new UI (true) or not (false).
     * @return boolean
     */
    public boolean isNewUI() {
        return newUI;
    }

}
