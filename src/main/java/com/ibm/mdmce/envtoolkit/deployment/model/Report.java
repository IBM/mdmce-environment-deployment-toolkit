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
 * Processes <b>Reports.csv</b>.
 */
public class Report extends BasicEntity {

    public static final String NAME = "Report Name";
    public static final String SCRIPT = "Script Name";
    public static final String DISTRIBUTION = "Distribution Name";

    private String name;
    private String script;
    private String distribution;

    private static class Singleton {
        private static final Report INSTANCE = new Report();
    }

    /**
     * Retrieve the static definition of a Report (ie. its columns and type information).
     * @return Report
     */
    public static Report getInstance() {
        return Report.Singleton.INSTANCE;
    }

    private Report() {
        super("REPORTS", "Reports");
        addColumn(COUNTRY_SPECIFIC);
        addColumn(NAME);
        addColumn(SCRIPT);
        addColumn(DISTRIBUTION);
    }

    /**
     * Construct a new instance of a Report using the provided field values.
     * @param <T> expected to be Report whenever used by this class
     * @param aFields from which to construct the Report
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BasicEntity> T createInstance(List<String> aFields) {
        Report report = new Report();
        report.name = getFieldValue(NAME, aFields);
        report.script = getFieldValue(SCRIPT, aFields);
        report.distribution = getFieldValue(DISTRIBUTION, aFields);
        return (T) report;
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
        outFile.write("   <REPORTS>\n");
        outFile.write(getNodeXML("Name", getName()));
        outFile.write(getNodeXML("Action", "CREATE_OR_UPDATE"));
        outFile.write(getNodeXML("Type", getScript()));
        if (getDistribution().equals(Distribution.DEFAULT_DISTRIBUTION))
            outFile.write(getNodeXML("DistributionName", "Default"));
        else
            outFile.write(getNodeXML("DistributionName", getDistribution()));
        outFile.write("   </REPORTS>\n");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputEntityCSV(Writer outFile, String sOutputPath) throws IOException {
        List<String> line = new ArrayList<>();
        line.add("");
        line.add(getName());
        line.add(getScript());
        line.add(getDistribution());
        outputCSV(line, outFile);
    }

    /**
     * Retrieve the name of this instance of a report.
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieve the script for this instance of a report.
     * @return String
     */
    public String getScript() {
        return script;
    }

    /**
     * Retrieve the distribution for this instance of a report.
     * @return String
     */
    public String getDistribution() {
        return distribution;
    }

}
