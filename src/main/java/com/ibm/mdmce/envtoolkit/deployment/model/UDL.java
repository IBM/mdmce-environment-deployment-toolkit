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
 * Processes <b>UDLs.csv</b>.
 */
public class UDL extends BasicEntity {

    public static final String NAME = "UDL Name";
    public static final String DESCRIPTION = "UDL Description";
    public static final String CONTAINER_NAME = "Container Name";
    public static final String CONTAINER_TYPE = "Container Type";
    public static final String IS_RUNNING_LOG = "Running Log?";

    public String name;
    public String description;
    public String containerName;
    public String containerType;
    public boolean runningLog = false;

    private static class Singleton {
        private static final UDL INSTANCE = new UDL();
    }

    /**
     * Retrieve the static definition of an UserDefinedLog (ie. its columns and type information).
     * @return UserDefinedLog
     */
    public static UDL getInstance() {
        return UDL.Singleton.INSTANCE;
    }

    private UDL() {
        super("UDL", "UserDefinedLogs");
        addColumn(COUNTRY_SPECIFIC);
        addColumn(NAME);
        addColumn(DESCRIPTION);
        addColumn(CONTAINER_NAME);
        addColumn(CONTAINER_TYPE);
        addColumn(IS_RUNNING_LOG);
    }

    /**
     * Construct a new instance of an User Defined Log using the provided field values.
     * @param <T> expected to be UserDefinedLog whenever used by this class
     * @param aFields from which to construct the User Defined Log
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BasicEntity> T createInstance(List<String> aFields) {
        UDL udl = new UDL();
        udl.name = getFieldValue(NAME, aFields);
        udl.description = getFieldValue(DESCRIPTION, aFields);
        udl.containerName = getFieldValue(CONTAINER_NAME, aFields);
        udl.containerType = getFieldValue(CONTAINER_TYPE, aFields);
        udl.runningLog = CSVParser.checkBoolean(getFieldValue(IS_RUNNING_LOG, aFields));
        return (T) udl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUniqueId() {
        return getContainerName() + "::" + getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputEntityXML(BasicEntityHandler handler, Writer outFile, String sOutputPath, String sCompanyCode) throws IOException {
        outFile.write("   <UDL>\n");
        outFile.write(getNodeXML("Name", getName()));
        outFile.write(getNodeXML("Action", "CREATE_OR_UPDATE"));
        outFile.write(getNodeXML("Description", getDescription()));
        outFile.write(getNodeXML("ContainerType", getContainerType()));
        outFile.write(getNodeXML("ContainerName", getContainerName()));
        outFile.write(getNodeXML("RunningLog", "" + isRunningLog()));
        outFile.write("   </UDL>\n");
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
        line.add(getContainerName());
        line.add(getContainerType());
        line.add("" + isRunningLog());
        outputCSV(line, outFile);
    }

    /**
     * Retrieve the name of this instance of a user defined log.
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieve the description of this instance of a user defined log.
     * @return String
     */
    public String getDescription() {
        return description;
    }

    /**
     * Retrieve the container name for this instance of a user defined log.
     * @return String
     */
    public String getContainerName() {
        return containerName;
    }

    /**
     * Retrieve the container type for this instance of a user defined log.
     * @return String
     */
    public String getContainerType() {
        return containerType;
    }

    /**
     * Indicates whether this instance of a user defined log is a running log (true) or not (false).
     * @return boolean
     */
    public boolean isRunningLog() {
        return runningLog;
    }

}
