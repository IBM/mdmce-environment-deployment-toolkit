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
 * Processes <b>Searches.csv</b>.
 */
public class SearchTemplate extends Search {

    private static class Singleton {
        private static final SearchTemplate INSTANCE = new SearchTemplate();
    }

    /**
     * Retrieve the static definition of a SearchTemplate (ie. its columns and type information).
     * @return SearchTemplate
     */
    public static SearchTemplate getInstance() {  return SearchTemplate.Singleton.INSTANCE; }

    protected SearchTemplate() { super("SEARCH_TEMPLATES", "SearchTemplates"); }

    /**
     * Construct a new instance of a Search Template using the provided field values.
     * @param <T> expected to be SearchTemplate whenever used by this class
     * @param aFields from which to construct the Search Template
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BasicEntity> T createInstance(List<String> aFields) {
        if ("TEMPLATE".equals(getFieldValue(SEARCH_TYPE, aFields))) {
            Search s = (Search) BasicEntityHandler.getFromCache(getFieldValue(SEARCH_NAME, aFields) + "::" + getFieldValue(USERNAME, aFields), SearchTemplate.class.getName(), false, false);
            if (s == null) {
                s = new SearchTemplate();
            }
            fillInstance(s, aFields);
            return (T) s;
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputEntityXML(BasicEntityHandler handler, Writer outFile, String sOutputPath, String sCompanyCode) throws IOException {
        outFile.write("   <SEARCH_TEMPLATES>\n");
        outFile.write(getNodeXML("Name", getSearchName()));
        outFile.write(getNodeXML("Action", "CREATE_OR_UPDATE"));
        if (isNewUI(handler.getVersion())) {
            outFile.write(getNodeXML("ContainerType", getContainerType()));
            outFile.write(getNodeXML("ContainerName", getContainerName()));
            outFile.write(getNodeXML("Type", getSearchType()));
            outFile.write(getNodeXML("IsDefault", isDefaut()?"t":"f"));
            outFile.write(getNodeXML("IsShared", isShared()?"t":"f"));
            outFile.write(getNodeXML("IsNewUI", "YES"));
            outFile.write(getNodeXML("TemplateUser", getUserName()));
            outputSSTblobXML(handler, outFile, sOutputPath, sCompanyCode);
            outFile.write(getNodeXML("Description", getDescription()));
        } else {
            outFile.write(getNodeXML("ContainerName", getContainerName()));
            outFile.write(getNodeXML("StepName", getStepName()));
            outFile.write(getNodeXML("IsNewUI", "NO"));
        }
        outFile.write("   </SEARCH_TEMPLATES>\n");
    }
}
