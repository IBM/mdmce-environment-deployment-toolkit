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
 * Processes <b>ACGs.csv</b>.
 */
public class ACG extends BasicEntity {

    /**
     * The value to specify in any CSV to make use of the default Access Control Group.
     */
    public static final String DEFAULT_ACG = "$DEFAULT";

    public static final String NAME = "ACG Name";
    public static final String DESCRIPTION = "ACG Description";

    private String name;
    private String description;

    private static class Singleton {
        private static final ACG INSTANCE = new ACG();
    }

    /**
     * Retrieve the static definition of an AccessControlGroup (ie. its columns and type information).
     * @return AccessControlGroup
     */
    public static ACG getInstance() {
        return Singleton.INSTANCE;
    }

    private ACG() {
        super("ACG", "AccessControlGroups");
        addColumn(COUNTRY_SPECIFIC);
        addColumn(NAME);
        addColumn(DESCRIPTION);
    }

    /**
     * Construct a new instance of an Access Control Group using the provided field values.
     * @param <T> expected to be AccessControlGroup whenever used by this class
     * @param aFields from which to construct the Access Control Group
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BasicEntity> T createInstance(List<String> aFields) {
        ACG acg = new ACG();
        acg.name = getFieldValue(NAME, aFields);
        acg.description = getFieldValue(DESCRIPTION, aFields);
        return (T) acg;
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
        outFile.write("   <ACG>\n");
        outFile.write(getNodeXML("Name", getName()));
        outFile.write(getNodeXML("Action", "CREATE_OR_UPDATE"));
        outFile.write(getNodeXML("Description", getDescription()));
        outFile.write("   </ACG>\n");
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
     * Retrieve the name of this instance of an Access Control Group.
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieve the description of this instance of an Access Control Group.
     * @return String
     */
    public String getDescription() {
        return description;
    }

}
