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
 * Processes <b>CompanyAttributes.csv</b>.
 */
public class CompanyAttribute extends BasicEntity {

    public static final String TYPE = "Type";
    public static final String LANGUAGE_OR_CODE = "Language / Code";
    public static final String COUNTRY_OR_SYMBOL = "Country / Symbol";

    private String language;
    private String country;
    private String code;
    private String symbol;
    private String type;

    private static class Singleton {
        private static final CompanyAttribute INSTANCE = new CompanyAttribute();
    }

    /**
     * Retrieve the static definition of an CompanyAttribute (ie. its columns and type information).
     * @return CompanyAttribute
     */
    public static CompanyAttribute getInstance() {
        return CompanyAttribute.Singleton.INSTANCE;
    }

    private CompanyAttribute() {
        super("COMPANY_ATTRIBUTES", "CompanyAttributes");
        addColumn(TYPE);
        addColumn(LANGUAGE_OR_CODE);
        addColumn(COUNTRY_OR_SYMBOL);
    }

    /**
     * Construct a new instance of a Company Attribute using the provided field values.
     * @param <T> expected to be CompanyAttribute whenever used by this class
     * @param aFields from which to construct the Company Attribute
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BasicEntity> T createInstance(List<String> aFields) {
        CompanyAttribute ca = new CompanyAttribute();
        ca.type = getFieldValue(TYPE, aFields);
        if (ca.type.equals("LOCALE")) {
            ca.language = getFieldValue(LANGUAGE_OR_CODE, aFields);
            ca.country = getFieldValue(COUNTRY_OR_SYMBOL, aFields);
            ca.code = ca.language + "_" + ca.country;
        } else if (ca.type.equals("CURRENCY")) {
            ca.code = getFieldValue(LANGUAGE_OR_CODE, aFields);
            ca.symbol = getFieldValue(COUNTRY_OR_SYMBOL, aFields);
        }
        return (T) ca;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUniqueId() {
        return getCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputEntityXML(BasicEntityHandler handler, Writer outFile, String sOutputPath, String sCompanyCode) throws IOException {
        String sType = getType();
        if (sType.equals("CURRENCY")) {
            outFile.write("   <COMPANY_ATTRIBUTES type=\"Currency\">\n");
            outFile.write(getNodeXML("Code", getCode()));
            outFile.write(getNodeXML("Symbol", getSymbol()));
            outFile.write("   </COMPANY_ATTRIBUTES>\n");
        } else if (sType.equals("LOCALE")) {
            outFile.write("   <COMPANY_ATTRIBUTES>\n");
            outFile.write(getNodeXML("Value", getCode()));
            outFile.write(getNodeXML("Language", getLanguage()));
            outFile.write(getNodeXML("Country", getCountry()));
            outFile.write("   </COMPANY_ATTRIBUTES>\n");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputEntityCSV(Writer outFile, String sOutputPath) throws IOException {
        List<String> line = new ArrayList<>();
        String sType = getType();
        line.add(sType);
        line.add(sType.equals("LOCALE") ? getLanguage() : getCode());
        line.add(sType.equals("LOCALE") ? getCountry() : getSymbol());
        outputCSV(line, outFile);
    }

    /**
     * Retrieve the type of this instance of a company attribute ({@literal LOCALE} or {@literal CURRENCY}.
     * @return String
     */
    public String getType() {
        return type;
    }

    /**
     * Retrieve the language of this instance of a company attribute (if it is of type {@literal LOCALE}).
     * @return String (or null if not a {@literal LOCALE})
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Retrieve the code of this instance of a company attribute.
     * @return String
     */
    public String getCode() {
        return code;
    }

    /**
     * Retrieve the country of this instance of a company attribute (if it is of type {@literal LOCALE}).
     * @return String
     */
    public String getCountry() {
        return country;
    }

    /**
     * Retrieve the symbol of this instance of a company attribute (if it is of type {@literal CURRENCY}).
     * @return String
     */
    public String getSymbol() {
        return symbol;
    }

}
