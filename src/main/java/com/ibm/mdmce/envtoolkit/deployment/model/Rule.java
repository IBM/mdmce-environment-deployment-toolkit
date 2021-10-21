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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Processes <b>Rules.csv</b>.
 */
public class Rule extends BasicEntity {
    public static final String NAME = "Name";
    public static final String RULETYPE = "Rule Type";
    public static final String APPLICABLETO = "Applicable To";
    public static final String JSON = "JSON";

    private String name;
    private String ruleType;
    private String applicableTo;
    private String ruleJSON;


    private static class Singleton {
        private static final Rule INSTANCE = new Rule();
    }

    /**
     * Retrieve the static definition of an User (ie. its columns and type information).
     * @return User
     */
    public static Rule getInstance() {
        return Rule.Singleton.INSTANCE;
    }

    private Rule() {
        super("RULE", "Rules");
        addColumn(COUNTRY_SPECIFIC);
        addColumn(NAME);
        addColumn(RULETYPE);
        addColumn(APPLICABLETO);
        addColumn(JSON);
    }

    /**
     * Construct a new instance of an Access Control Group using the provided field values.
     * @param <T> expected to be AccessControlGroup whenever used by this class
     * @param aFields from which to construct the Access Control Group
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BasicEntity> T createInstance(List<String> aFields) {

        Rule rule = new Rule();

        rule.name = getFieldValue(NAME, aFields);
        rule.ruleType = getFieldValue(RULETYPE, aFields);
        rule.applicableTo = getFieldValue(APPLICABLETO, aFields);
        rule.ruleJSON = getFieldValue(JSON, aFields);

        return (T) rule;

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

        outFile.write("   <RULE>\n");
        outFile.write(getNodeXML("action", "CREATE_OR_UPDATE"));
        outFile.write(getNodeXML("name", getName()));
        outFile.write(getNodeXML("ruleType", getRuleType()));
        outFile.write(getNodeXML("applicableTo", getApplicableTo()));
        outFile.write(getNodeXML("ruleJSON", getRuleJSON()));
        outFile.write("   </RULE>\n");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputEntityCSV(Writer outFile, String sOutputPath) throws IOException {
        List<String> line = new ArrayList<>();
        line.add("");
        line.add(getName());
        line.add(getRuleType());
        line.add(getApplicableTo());
        line.add(getRuleJSON());
        outputCSV(line, outFile);
    }

    /**
     * Retrieve the username of this instance of a user.
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieve rule type.
     * @return String
     */
    public String getRuleType() {
        return ruleType;
    }

    /**
     * Retrieve attribute Applicable To of a rule.
     * @return String
     */
    public String getApplicableTo() {
        return applicableTo;
    }

    /**
     * Retrieve the JSON of a rule.
     * @return String
     */
    public String getRuleJSON() {
        return ruleJSON;
    }

}
