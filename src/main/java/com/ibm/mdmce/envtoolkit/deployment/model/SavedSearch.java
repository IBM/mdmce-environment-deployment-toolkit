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
 * Processes <b>Searches.csv</b>.
 */
public class SavedSearch extends Search {

    private static class Singleton {
        private static final SavedSearch INSTANCE = new SavedSearch();
    }

    /**
     * Retrieve the static definition of a Search (ie. its columns and type information).
     * @return SavedSearch
     */
    public static SavedSearch getInstance() {
        return SavedSearch.Singleton.INSTANCE;
    }

    protected SavedSearch() { super("SAVED_SEARCHES", "SavedSearches"); }

    /**
     * Construct a new instance of a Search Template using the provided field values.
     * @param <T> expected to be SearchTemplate whenever used by this class
     * @param aFields from which to construct the Search Template
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BasicEntity> T createInstance(List<String> aFields) {
        if ("QUERY".equals(getFieldValue(SEARCH_TYPE, aFields))) {
            Search s = (Search) BasicEntityHandler.getFromCache(getFieldValue(SEARCH_NAME, aFields) + "::" + getFieldValue(USERNAME, aFields), SavedSearch.class.getName(), false, false);
            if (s == null) {
                s = new SavedSearch();
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
        outFile.write("   <SAVED_SEARCHES>\n");
        outFile.write(getNodeXML("Name", getSearchName()));
        outFile.write(getNodeXML("Action", "CREATE_OR_UPDATE"));
        outFile.write(getNodeXML("ContainerType", getContainerType()));
        outFile.write(getNodeXML("ContainerName", getContainerName()));
        outFile.write(getNodeXML("Type", getSearchType()));
        outFile.write(getNodeXML("IsDefault", isDefaut()?"t":"f"));
        outFile.write(getNodeXML("SavedSearchUser", getUserName()));
        outputSSTblobXML(handler, outFile, sOutputPath, sCompanyCode);
        outFile.write(getNodeXML("Description", getDescription()));
        outFile.write("   </SAVED_SEARCHES>\n");
    }
}
