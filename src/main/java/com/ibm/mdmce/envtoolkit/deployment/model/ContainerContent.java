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
 * General methods re-usable by any container contents.
 *
 * @see CatalogContent
 * @see HierarchyContent
 * @see OrganizationContent
 */
public abstract class ContainerContent extends BasicEntity {

    public static final String CONTAINER_NAME = "Container Name";
    public static final String DATA_FILE_PATH = "Data File Path";
    public static final String CHARSET = "Charset";

    private String containerName;
    private String dataFilePath;
    private String filename;
    private String charset;

    private String contentType;
    private String outputPath;

    /**
     * Construct a new ContainerContent description for the specified object type.
     * @param objectType the object type for which to construct the description
     * @param contentType the type of content being handled
     * @param outputPath the output path for the content file
     */
    protected ContainerContent(String objectType, String contentType, String outputPath) {
        super(objectType, "ExportContainerEntries");
        this.contentType = contentType;
        this.outputPath = "/" + outputPath + "/";
        addColumn(CONTAINER_NAME);
        addColumn(DATA_FILE_PATH);
        addColumn(CHARSET);
    }

    /**
     * Setup an instance of ContainerContent using the provided information.
     * @param instance the instance to setup
     * @param aFields the fields from which to setup the instance
     */
    protected void setupInstance(ContainerContent instance, List<String> aFields) {
        instance.containerName = getFieldValue(CONTAINER_NAME, aFields);
        instance.dataFilePath = getFieldValue(DATA_FILE_PATH, aFields);
        instance.charset = getFieldValue(CHARSET, aFields);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUniqueId() {
        return getContainerName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputEntityXML(BasicEntityHandler handler, Writer outFile, String sOutputPath, String sCompanyCode) throws IOException {
        outFile.write("   <Container>\n");
        outFile.write(getNodeXML("ContainerType", contentType));
        outFile.write(getNodeXML("ContainerName", getContainerName()));
        outFile.write(getNodeXML("EntryDataFilePath", outputPath + getFilename()));
        outFile.write(getNodeXML("Encoding", getCharset()));
        outFile.write("   </Container>\n");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputEntityCSV(Writer outFile, String sOutputPath) throws IOException {
        List<String> line = new ArrayList<>();
        line.add(getContainerName());
        line.add(getDataFilePath());
        line.add(getCharset());
        outputCSV(line, outFile);
    }

    /**
     * Retrieve the name of the container for this instance of content.
     * @return String
     */
    public String getContainerName() {
        return containerName;
    }

    /**
     * Retrieve the path to the data file containing data for this instance of content.
     * @return String
     */
    public String getDataFilePath() {
        return dataFilePath;
    }

    /**
     * Set the filename for the data file containing data for this instance of content.
     * @param filename for the output file containing data for this instance of content
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Retrieve the filename for the data file containing data for this instance of content.
     * @return String
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Retrieve the character encoding of the data file for this instance of content.
     * @return String
     */
    public String getCharset() {
        return charset;
    }

}
