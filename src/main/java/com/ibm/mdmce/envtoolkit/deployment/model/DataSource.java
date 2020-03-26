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
 * Processes <b>DataSources.csv</b>.
 */
public class DataSource extends BasicEntity {

    public static final String NAME = "Data source name";
    public static final String TYPE = "Data source type";
    public static final String SERVER_ADDRESS = "Server address";
    public static final String SERVER_PORT = "Server port";
    public static final String LOGIN_USERNAME = "Login username";
    public static final String LOGIN_PASSWORD = "Login password";
    public static final String DIRECTORY = "Directory";
    public static final String FILENAME = "Filename";
    public static final String DOCSTORE_PATH = "DocStore Path";

    private String name;
    private String type;
    private String serverAddress;
    private String serverPort;
    private String loginUsername;
    private String loginPassword;
    private String directory;
    private String filename;
    private String docStorePath;

    private static class Singleton {
        private static final DataSource INSTANCE = new DataSource();
    }

    /**
     * Retrieve the static definition of a DataSource (ie. its columns and type information).
     * @return DataSource
     */
    public static DataSource getInstance() {
        return DataSource.Singleton.INSTANCE;
    }

    private DataSource() {
        super("DATASOURCE", "DataSources");
        addColumn(COUNTRY_SPECIFIC);
        addColumn(NAME);
        addColumn(TYPE);
        addColumn(SERVER_ADDRESS);
        addColumn(SERVER_PORT);
        addColumn(LOGIN_USERNAME);
        addColumn(LOGIN_PASSWORD);
        addColumn(DIRECTORY);
        addColumn(FILENAME);
        addColumn(DOCSTORE_PATH);
    }

    /**
     * Construct a new instance of an Data Source using the provided field values.
     * @param <T> expected to be DataSource whenever used by this class
     * @param aFields from which to construct the Data Source
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BasicEntity> T createInstance(List<String> aFields) {
        DataSource ds = new DataSource();
        ds.name = getFieldValue(NAME, aFields);
        ds.type = getFieldValue(TYPE, aFields);
        ds.serverAddress = getFieldValue(SERVER_ADDRESS, aFields);
        ds.serverPort = getFieldValue(SERVER_PORT, aFields);
        ds.loginUsername = getFieldValue(LOGIN_USERNAME, aFields);
        ds.loginPassword = getFieldValue(LOGIN_PASSWORD, aFields);
        ds.directory = getFieldValue(DIRECTORY, aFields);
        ds.filename = getFieldValue(FILENAME, aFields);
        ds.docStorePath = getFieldValue(DOCSTORE_PATH, aFields);
        return (T) ds;
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
        outFile.write("   <DATASOURCE>\n");
        outFile.write(getNodeXML("SourceType", getType()));
        outFile.write(getNodeXML("Name", getName()));
        outFile.write(getNodeXML("Action", "CREATE_OR_UPDATE"));
        if (getType().equals("PULL_FTP")) {
            outFile.write(getNodeXML("FtpAddress", getServerAddress()));
            outFile.write(getNodeXML("FtpPort", getServerPort()));
            outFile.write(getNodeXML("UserName", getLoginUsername()));
            outFile.write(getNodeXML("PassWord", getLoginPassword()));
            outFile.write(getNodeXML("fileName", getFilename()));
            outFile.write(getNodeXML("Directory", getDirectory()));
        } else if (getType().equals("DOC_STORE")) {
            outFile.write(getNodeXML("DocStorePath", getDocStorePath()));
        }
        outFile.write("   </DATASOURCE>\n");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputEntityCSV(Writer outFile, String sOutputPath) throws IOException {
        List<String> line = new ArrayList<>();
        line.add("");
        line.add(getName());
        line.add(getType());
        line.add(getServerAddress());
        line.add(getServerPort());
        line.add(getLoginUsername());
        line.add(getLoginPassword());
        line.add(getDirectory());
        line.add(getFilename());
        line.add(getDocStorePath());
        outputCSV(line, outFile);
    }

    /**
     * Retrieve the name of this instance of a data source.
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieve the type of this instance of a data source.
     * @return String
     */
    public String getType() {
        return type;
    }

    /**
     * Retrieve the server address of this instance of a data source.
     * @return String
     */
    public String getServerAddress() {
        return serverAddress;
    }

    /**
     * Retrieve the server port of this instance of a data source.
     * @return String
     */
    public String getServerPort() {
        return serverPort;
    }

    /**
     * Retrieve the login username of this instance of a data source.
     * @return String
     */
    public String getLoginUsername() {
        return loginUsername;
    }

    /**
     * Retrieve the login password of this instance of a data source.
     * @return String
     */
    public String getLoginPassword() {
        return loginPassword;
    }

    /**
     * Retrieve the directory of this instance of a data source.
     * @return String
     */
    public String getDirectory() {
        return directory;
    }

    /**
     * Retrieve the filename of this instance of a data source.
     * @return String
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Retrieve the document store path of this instance of a data source.
     * @return String
     */
    public String getDocStorePath() {
        return docStorePath;
    }

}
