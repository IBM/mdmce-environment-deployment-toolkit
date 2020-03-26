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
 * Base class for any entities that will be represented.
 */
public abstract class BasicEntity {

    /**
     * The name of the "Country Specific" column.
     */
    public static final String COUNTRY_SPECIFIC = "CS?";

    private String objectType;
    private String rootElement;

    private List<String> columns;

    protected BasicEntity() {
        columns = new ArrayList<>();
    }

    /**
     * Construct a new entity descriptor (static).
     * @param objectType the object type
     * @param rootElement the root element for the XML for the object type
     */
    public BasicEntity(String objectType, String rootElement) {
        this();
        this.objectType = objectType;
        this.rootElement = rootElement;
    }

    /**
     * Retrieve the object type.
     * @return String
     */
    public String getObjectType() {
        return objectType;
    }

    /**
     * Retrieve the root XML element's name.
     * @return String
     */
    public String getRootElement() {
        return rootElement;
    }

    /**
     * Retrieve the columns for this entity type.
     * @return {@code List<String>}
     */
    public List<String> getColumns() {
        return columns;
    }

    /**
     * Retrieve the index of the column with the specified name.
     * @param name of the column for which to retrieve its index
     * @return int
     */
    public int getIndexOfColumn(String name) {
        return columns.indexOf(name);
    }

    /**
     * Add a column to the description of this entity type.
     * @param name of the column
     */
    public void addColumn(String name) {
        columns.add(name);
    }

    /**
     * Retrieve the total number of columns for this entity type.
     * @return int
     */
    public int getColumnCount() {
        return columns.size();
    }

    /**
     * Construct a new instance of an entity using the provided field values.
     * @param <T> the type of entity to create
     * @param aFields from which to construct the entity instance
     * @return T - a new instance of the entity
     */
    public abstract <T extends BasicEntity> T createInstance(List<String> aFields);

    /**
     * Retrieve the unique identity of an individual instance of this entity.
     * @return String
     */
    public abstract String getUniqueId();

    /**
     * Output the XML for an individual instance of this entity.
     * @param handler the handler for this entity type
     * @param outFile into which to write
     * @param sOutputPath (unknown?)
     * @param sCompanyCode the company code of the environment
     * @throws IOException on any error writing
     */
    public abstract void outputEntityXML(BasicEntityHandler handler, Writer outFile, String sOutputPath, String sCompanyCode) throws IOException;

    /**
     * Output the CSV for an individual instance of this entity.
     * @param outFile into which to write
     * @param sOutputPath (unknown?)
     * @throws IOException on any error writing
     */
    public abstract void outputEntityCSV(Writer outFile, String sOutputPath) throws IOException;

    /**
     * Indicates whether the provided string contains any data (true) or not (false).
     * @param s to check
     * @return boolean
     */
    public static boolean containsData(String s) {
        return (s != null && !s.equals(""));
    }

    /**
     * Outputs the header for a CSV file to the provided output.
     * @param outFile into which to write the header
     * @throws IOException on any error writing
     */
    public void outputHeaderCSV(Writer outFile) throws IOException {
        outputCSV(getColumns(), outFile);
    }

    /**
     * Retrieve the value of the specified field from the provided list of fields.
     * @param fieldName name of the field to retrieve
     * @param aFields list of fields from which to retrieve the value
     * @return String
     */
    protected String getFieldValue(String fieldName, List<String> aFields) {
        return aFields.get(getIndexOfColumn(fieldName));
    }

    /**
     * Build the XML to represent the provided element and its data.
     * @param sElement name of the XML element
     * @param sData data for the XML element
     * @return String
     */
    protected static String getNodeXML(String sElement, String sData) {
        String sRetVal;
        if (containsData(sData))
            sRetVal = "      <" + sElement + "><![CDATA[" + sData + "]]></" + sElement + ">\n";
        else
            sRetVal = "      <" + sElement + " />\n";
        return sRetVal;
    }

    /**
     * Output the provided list of tokens as a row of CSV data to the provided writer.
     * @param aTokens the row of CSV fields
     * @param outFile the output into which to write
     * @throws IOException on any error writing
     */
    protected static void outputCSV(List<String> aTokens, Writer outFile) throws IOException {
        if (aTokens.size() > 0)
            outFile.write(escapeForCSV(aTokens.get(0)));
        for (String token : aTokens) {
            outFile.write("," + escapeForCSV(token));
        }
        outFile.write("\n");
    }

    /**
     * Escape the provided string for use within a CSV file, as handled by Microsoft Excel.
     * @param sArg the string to escape
     * @return String
     */
    public static String escapeForCSV(String sArg) {
        StringBuilder sb = new StringBuilder();
        if (sArg != null) {
            if ((sArg.contains(","))
                    || (sArg.contains("\n"))
                    || (sArg.contains("\r"))
                    || (sArg.contains("\""))) {
                sb.append("\"");
                sb.append(sArg.replace("\"", "\"\""));
                sb.append("\"");
            } else {
                sb.append(sArg);
            }
        }
        return sb.toString();
    }

}
