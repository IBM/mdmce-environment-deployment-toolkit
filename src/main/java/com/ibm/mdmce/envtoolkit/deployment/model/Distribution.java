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
 * Processes <b>Distributions.csv</b>.
 */
public class Distribution extends BasicEntity {

    public static final String DEFAULT_DISTRIBUTION = "$DEFAULT";

    public static final String NAME = "Distribution Name";
    public static final String TYPE = "Type";
    public static final String EMAIL = "Email";
    public static final String HOSTNAME = "Hostname";
    public static final String USER_ID = "User ID";
    public static final String PASSWORD = "Password";
    public static final String PATH = "Path";
    public static final String FROM = "From";
    public static final String TO = "To";
    public static final String SUBJECT = "Subject";
    public static final String LOCAL_PATH = "Local Path";

    private String name;
    private String type;
    private String email;
    private String hostname;
    private String userId;
    private String password;
    private String path;
    private String from;
    private String to;
    private String subject;
    private String localPath;

    private static class Singleton {
        private static final Distribution INSTANCE = new Distribution();
    }

    /**
     * Retrieve the static definition of a Distribution (ie. its columns and type information).
     * @return Distribution
     */
    public static Distribution getInstance() {
        return Distribution.Singleton.INSTANCE;
    }

    private Distribution() {
        super("DISTRIBUTION", "Distributions");
        addColumn(COUNTRY_SPECIFIC);
        addColumn(NAME);
        addColumn(TYPE);
        addColumn(EMAIL);
        addColumn(HOSTNAME);
        addColumn(USER_ID);
        addColumn(PASSWORD);
        addColumn(PATH);
        addColumn(FROM);
        addColumn(TO);
        addColumn(SUBJECT);
        addColumn(LOCAL_PATH);
    }

    /**
     * Construct a new instance of a Distribution using the provided field values.
     * @param <T> expected to be Distribution whenever used by this class
     * @param aFields from which to construct the Distribution
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BasicEntity> T createInstance(List<String> aFields) {
        Distribution dist = new Distribution();
        dist.name = getFieldValue(NAME, aFields);
        dist.type = getFieldValue(TYPE, aFields);
        dist.email = getFieldValue(EMAIL, aFields);
        dist.hostname = getFieldValue(HOSTNAME, aFields);
        dist.userId = getFieldValue(USER_ID, aFields);
        dist.password = getFieldValue(PASSWORD, aFields);
        dist.path = getFieldValue(PATH, aFields);
        dist.from = getFieldValue(FROM, aFields);
        dist.to = getFieldValue(TO, aFields);
        dist.subject = getFieldValue(SUBJECT, aFields);
        dist.localPath = getFieldValue(LOCAL_PATH, aFields);
        return (T) dist;
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
        outFile.write("   <DISTRIBUTION>\n");
        outFile.write(getNodeXML("Name", getName()));
        outFile.write(getNodeXML("Action", "CREATE_OR_UPDATE"));
        outFile.write(getNodeXML("DistributionType", getType()));
        // TODO: Handle the other "DestinationType" property
        outFile.write(getNodeXML("DestinationType", ""));
        outFile.write(getNodeXML("Email", getEmail()));
        outFile.write(getNodeXML("HostName", getHostname()));
        outFile.write(getNodeXML("UserId", getUserId()));
        outFile.write(getNodeXML("Password", getPassword()));
        outFile.write(getNodeXML("Path", getPath()));
        outFile.write(getNodeXML("From", getFrom()));
        outFile.write(getNodeXML("To", getTo()));
        outFile.write(getNodeXML("Subject", getSubject()));
        outFile.write(getNodeXML("LocalPath", getLocalPath()));
        outFile.write("   </DISTRIBUTION>\n");
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
        line.add(getEmail());
        line.add(getHostname());
        line.add(getUserId());
        line.add(getPassword());
        line.add(getPath());
        line.add(getFrom());
        line.add(getTo());
        line.add(getSubject());
        line.add(getLocalPath());
        outputCSV(line, outFile);
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getEmail() {
        return email;
    }

    public String getHostname() {
        return hostname;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getPath() {
        return path;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getSubject() {
        return subject;
    }

    public String getLocalPath() {
        return localPath;
    }

}
