/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment.model;

import com.ibm.mdmce.envtoolkit.deployment.BasicEntityHandler;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Processes <b>LookupTableContents.csv</b>.
 */
public class LookupTableContent extends BasicEntity {

    public static final String TABLE_NAME = "Lookup Table Name";

    private String tableName;
    private String fileName;
    private List<String> attributePaths = new ArrayList<>();

    private static class Singleton {
        private static final LookupTableContent INSTANCE = new LookupTableContent();
    }

    /**
     * Retrieve the static definition of LookupTableData (ie. its columns and type information).
     * @return LookupTableData
     */
    public static LookupTableContent getInstance() {
        return LookupTableContent.Singleton.INSTANCE;
    }

    private LookupTableContent() {
        super("LOOKUP_TABLE_CONTENT", "ExportContainerEntries");
        addColumn(TABLE_NAME);
    }

    /**
     * Construct a new instance of an Access Control Group using the provided field values.
     * @param <T> expected to be AccessControlGroup whenever used by this class
     * @param aFields from which to construct the Access Control Group
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BasicEntity> T createInstance(List<String> aFields) {
        LookupTableContent lkpData = new LookupTableContent();
        lkpData.tableName = getFieldValue(TABLE_NAME, aFields);
        for (int iFs = 1; iFs < aFields.size(); iFs++) {
            String path = aFields.get(iFs);
            if (path != null && !path.equals("")) {
                lkpData.attributePaths.add(path);
            } else {
                // As soon as we hit an empty header, break out (as this will throw off indexing for retrieving data)
                break;
            }
        }
        return (T) lkpData;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUniqueId() {
        return getTableName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputEntityXML(BasicEntityHandler handler, Writer outFile, String sOutputPath, String sCompanyCode) throws IOException {
        outFile.write("   <Container>\n");
        outFile.write(getNodeXML("ContainerType", "CATALOG"));
        outFile.write(getNodeXML("ContainerName", getTableName()));
        outFile.write(getNodeXML("EntryDataFilePath", getTransformedFileName().replace(File.separator, "/")));
        outFile.write(getNodeXML("Encoding", "UTF-8"));
        outFile.write("   </Container>\n");
    }

    /**
     * Retrieve the output file for this lookup table data.
     * @return String
     */
    public String getTransformedFileName() {
        return File.separator + "LOOKUP_TABLE_CONTENT" + File.separator + BasicEntityHandler.escapeForFilename(getFileName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputEntityCSV(Writer outFile, String sOutputPath) throws IOException {
        List<String> line = new ArrayList<>();
        line.add(getTableName());
        line.add("");
        line.add("");
        outputCSV(line, outFile);
    }

    /**
     * Retrieve the table name of this instance of lookup table data.
     * @return String
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Retrieve the file name for this instance of lookup table data.
     * @return String
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Set the file name for this instance of lookup table data.
     * @param fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Retrieve the attribute paths, in order, for the data to be loaded for this instance of lookup table data.
     * @return {@code List<String>}
     */
    public List<String> getAttributePaths() {
        return attributePaths;
    }

}
