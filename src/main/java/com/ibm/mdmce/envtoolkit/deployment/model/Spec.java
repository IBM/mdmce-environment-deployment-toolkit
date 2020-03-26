/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment.model;

import com.ibm.mdmce.envtoolkit.deployment.BasicEntityHandler;
import com.ibm.mdmce.envtoolkit.deployment.CSVParser;
import com.ibm.mdmce.envtoolkit.deployment.EnvironmentHandler;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * Processes <b>Specs.csv</b>.
 */
public class Spec extends BasicEntity {

    public static final String NAME = "Spec Name";
    public static final String TYPE = "Spec Type";
    public static final String ATTR_PATH = "Attribute Path";
    public static final String ATTR_TYPE = "Attribute Type";
    public static final String PRIMARY_KEY = "PK?";
    public static final String INDEXED = "Idx?";
    public static final String LOCALIZED = "Lcl?";
    public static final String LINK = "Lnk?";
    public static final String MIN_OCCUR = "Min";
    public static final String MAX_OCCUR = "Max";
    public static final String EDITABLE = "Edit?";
    public static final String NON_PERSISTED = "NP?";
    public static final String DEFAULT_VALUE = "Default";
    public static final String LENGTH = "Length";
    public static final String HIDDEN = "Hidden?";
    public static final String RULE = "RULE";

    private String name;
    private String type;
    private String primaryKeyPath;

    private Map<String, Attribute> hmAttributes = new TreeMap<>();
    private List<String> attributes = new ArrayList<>();

    private static class Singleton {
        private static final Spec INSTANCE = new Spec();
    }

    /**
     * Retrieve the static definition of a Catalog (ie. its columns and type information).
     * @return Catalog
     */
    public static Spec getInstance() {
        return Spec.Singleton.INSTANCE;
    }

    private Spec() {
        super("UNUSED", "LookupTables");
        addColumn(COUNTRY_SPECIFIC);
        addColumn(NAME);
        addColumn(TYPE);
        addColumn(ATTR_PATH);
        addColumn(ATTR_TYPE);
        addColumn(PRIMARY_KEY);
        addColumn(INDEXED);
        addColumn(LOCALIZED);
        addColumn(LINK);
        addColumn(MIN_OCCUR);
        addColumn(MAX_OCCUR);
        addColumn(EDITABLE);
        addColumn(NON_PERSISTED);
        addColumn(DEFAULT_VALUE);
        addColumn(LENGTH);
        addColumn(HIDDEN);
        addColumn(RULE);
        for (String locale : EnvironmentHandler.getAllLocales()) {
            addColumn(locale + " DisplayName");
        }
    }

    /**
     * Construct a new instance of a Spec using the provided field values.
     * @param <T> expected to be Spec whenever used by this class
     * @param aFields from which to construct the Spec
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BasicEntity> T createInstance(List<String> aFields) {

        String sSpecName = getFieldValue(NAME, aFields);
        Spec spec = (Spec) BasicEntityHandler.getFromCache(sSpecName, Spec.class.getName(), false, false);
        if (spec == null) {
            spec = new Spec();
            spec.name = sSpecName;
        }
        spec.type = getFieldValue(TYPE, aFields);

        Attribute attr = new Attribute();
        attr.path = getFieldValue(ATTR_PATH, aFields);
        attr.type = getFieldValue(ATTR_TYPE, aFields);
        attr.primaryKey = CSVParser.checkBoolean(getFieldValue(PRIMARY_KEY, aFields));
        if (attr.primaryKey) {
            spec.primaryKeyPath = sSpecName + "/" + attr.path;
        }
        attr.indexed = CSVParser.checkBoolean(getFieldValue(INDEXED, aFields));
        attr.localized = CSVParser.checkBoolean(getFieldValue(LOCALIZED, aFields));
        String sLink = getFieldValue(LINK, aFields);
        attr.link = CSVParser.checkBoolean(sLink);
        attr.min = CSVParser.checkInteger(getFieldValue(MIN_OCCUR, aFields), attr.min);
        attr.max = CSVParser.checkInteger(getFieldValue(MAX_OCCUR, aFields), attr.max);
        attr.editable = CSVParser.checkBoolean(getFieldValue(EDITABLE, aFields));
        attr.nonPersisted = CSVParser.checkBoolean(getFieldValue(NON_PERSISTED, aFields));
        attr.defaultValue = getFieldValue(DEFAULT_VALUE, aFields);
        attr.length = CSVParser.checkInteger(getFieldValue(LENGTH, aFields), attr.length);
        attr.hidden = CSVParser.checkBoolean(getFieldValue(HIDDEN, aFields));
        attr.rule = getFieldValue(RULE, aFields);

        if (attr.type.equals("LOOKUP_TABLE")) {
            attr.lookupTable = sLink;
            attr.link = false;
        } else if (attr.type.endsWith("_ENUMERATION") && !attr.rule.contains("|")) {
            attr.enumValues = Arrays.asList(attr.rule.split(","));
            attr.rule = "";
        }

        int iStart = getColumns().indexOf(RULE) + 1;
        List<String> allLocales = EnvironmentHandler.getAllLocales();
        int iEnd = iStart + allLocales.size();
        for (int i = iStart, j = 0; i < iEnd; i++, j++) {
            String sDisplayName = aFields.get(i);
            attr.localeToDisplayName.put(allLocales.get(j), sDisplayName);
        }

        Attribute oAlreadySetBefore = spec.hmAttributes.put(attr.path, attr);
        if (oAlreadySetBefore != null) {
            System.err.println(". . . WARNING (" + spec.name + "): Attribute \"" + attr.path + "\" has already been defined before!");
        } else {
            spec.attributes.add(attr.path);
        }

        return (T) spec;

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

        String sSpecName = getName();
        String sSpecType = getType();

        String sSpecTypeForOutput = sSpecType;
        if (sSpecTypeForOutput.startsWith("FILE_SPEC")) {
            sSpecTypeForOutput = "FILE_SPEC";
        } else if (sSpecTypeForOutput.startsWith("MKT_SPEC")) {
            sSpecTypeForOutput = "MKT_SPEC";
        }

        String sEscapedFilename = BasicEntityHandler.escapeForFilename(sSpecName);
        String sSpecFilePath = sOutputPath + File.separator + "SPECS" + File.separator + sSpecTypeForOutput + File.separator + sSpecTypeForOutput + "_" + sEscapedFilename + "_SPEC.xml";
        Writer outSpec = BasicEntityHandler.getNewWriter(sSpecFilePath);

        outSpec.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
        outSpec.write("<TrigoXML version=\"" + handler.getVersion() + "\">\n");
        outSpec.write("   <Header>\n");
        outSpec.write("      <Type>Spec</Type>\n");
        // in order to enable updates of specs the action mode must be 'Create_or_Update' for version 5.3.2 or higher
        if (handler.versionUsesNextGenXML()) {
            outSpec.write("      <Action>Create_or_Update</Action>\n");
        } else {
            outSpec.write("      <Action>Create</Action>\n");
        }
        outSpec.write("      <CompanyName>" + sCompanyCode + "</CompanyName>\n");
        outSpec.write("      <SpecName>" + BasicEntityHandler.escapeForXML(sSpecName) + "</SpecName>\n");
        outSpec.write("      <Attribs>\n");
        outSpec.write("         <Attrib>\n");
        outSpec.write("            <Name>TYPE</Name>\n");
        outSpec.write("            <Value>" + sSpecTypeForOutput + "</Value>\n");
        outSpec.write("         </Attrib>\n");

        if (sSpecType.startsWith("FILE_SPEC")) {

            String[] aTypeDetailTokens = sSpecType.split("\\Q|\\E");
            String sFileType = "C";
            if (aTypeDetailTokens.length > 1) {
                sFileType = aTypeDetailTokens[1];
            } else {
                System.err.println(". . . WARNING (" + sSpecName + "): No file spec information provided - assuming CSV with no header.");
            }
            String sIgnoreHeader = "0";
            if (aTypeDetailTokens.length > 2) {
                sIgnoreHeader = aTypeDetailTokens[2];
            }
            String sCharacter = "";
            if (aTypeDetailTokens.length > 3) {
                if (aTypeDetailTokens.length > 4) {
                    sCharacter = "|";
                } else {
                    sCharacter = aTypeDetailTokens[3];
                }
            }

            outSpec.write("         <Attrib>\n");
            outSpec.write("            <Name>IGNORE_HEADER</Name>\n");
            outSpec.write("            <Value>" + sIgnoreHeader + "</Value>\n");
            outSpec.write("         </Attrib>\n");
            outSpec.write("         <Attrib>\n");
            outSpec.write("            <Name>FILE_TYPE</Name>\n");
            outSpec.write("            <Value>" + sFileType + "</Value>\n");
            outSpec.write("         </Attrib>\n");
            if (!sCharacter.equals("")) {
                outSpec.write("         <Attrib>\n");
                outSpec.write("            <Name>DELIMITER</Name>\n");
                outSpec.write("            <Value>" + sCharacter + "</Value>\n");
                outSpec.write("         </Attrib>\n");
            }

        } else if (sSpecType.startsWith("MKT_SPEC")) {

        } else {
            outSpec.write("         <Attrib>\n");
            outSpec.write("            <Name>LOCALIZED</Name>\n");
            outSpec.write("            <Value>yes</Value>\n");
            outSpec.write("         </Attrib>\n");

            for (String locale : EnvironmentHandler.getAllLocales()) {
                outSpec.write("         <Attrib>\n");
                outSpec.write("             <Name>LOCALE_IDENTIFIER</Name>\n");
                outSpec.write("             <Value>" + locale + "</Value>\n");
                outSpec.write("          </Attrib>\n");
            }

            if (sSpecType.equals("PRIMARY_SPEC") || sSpecType.equals("LKP_SPEC")) {
                outSpec.write("         <Attrib>\n");
                outSpec.write("            <Name>LINK_TYPE</Name>\n");
                outSpec.write("            <Value>MASTER</Value>\n");
                outSpec.write("         </Attrib>\n");
                outSpec.write("         <Attrib>\n");
                outSpec.write("            <Name>PRIMARY_KEY</Name>\n");
                outSpec.write("            <Value>" + getPrimaryKeyPath() + "</Value>\n");
                outSpec.write("         </Attrib>\n");
            }
        }
        outSpec.write("      </Attribs>\n");
        outSpec.write("   </Header>\n");
        outSpec.write("   <Nodes>\n");

        for (String sAttrPath : attributes) {
            Attribute attr = hmAttributes.get(sAttrPath);
            outputAttributeXML(handler, attr, outSpec);
        }

        outSpec.write("   </Nodes>\n");
        outSpec.write("</TrigoXML>\n");
        outSpec.flush();

        handler.addGeneratedFileXML(sSpecFilePath);

    }

    private void outputSubSpecAttributeXML(String sSubSpecName, Writer outSpec) throws IOException {

        Spec specSubSpec = (Spec) BasicEntityHandler.getFromCache(sSubSpecName, Spec.class.getName(), false, false);

        for (String sAttrPath : specSubSpec.attributes) {

            Attribute attr = specSubSpec.hmAttributes.get(sAttrPath);
            boolean bGrouping = (containsData(attr.type) && (attr.type.equals("GROUPING")));

            outSpec.write("      <Node>\n");
            outSpec.write("         <Name>" + sAttrPath + "</Name>\n");
            if (bGrouping) {
                outSpec.write("         <Type>TYPE</Type>\n");
            } else {
                outSpec.write("         <Type>ATTRIBUTE</Type>\n");
            }
            outSpec.write("         <SharedNodeSpec>" + specSubSpec.getName() + "</SharedNodeSpec>\n");
            outSpec.write("      </Node>\n");

        }

    }

    private void outputAttributeXML(BasicEntityHandler handler, Attribute attr, Writer outSpec) throws IOException {
        outputAttributeXML(handler, attr, null, outSpec);
    }

    private void outputAttributeXML(BasicEntityHandler handler, Attribute attr, String sLocaleString, Writer outSpec) throws IOException {

        boolean bLocalized = attr.localized;
        boolean bGrouping = containsData(attr.type) && (attr.type.equals("GROUPING"));
        String sAttrPath = sLocaleString == null ? attr.path : attr.path + "/" + sLocaleString;

        if (containsData(attr.type) && attr.type.equals("SUB_SPEC")) {
            outputSubSpecAttributeXML(attr.path, outSpec);
        } else {

            outSpec.write("      <Node>\n");
            outSpec.write("         <Name>" + sAttrPath + "</Name>\n");
            if (bLocalized || bGrouping) {
                outSpec.write("         <Type>TYPE</Type>\n");
            } else {
                outSpec.write("         <Type>ATTRIBUTE</Type>\n");
            }
            outSpec.write("         <Attribs>\n");
            if (containsData(attr.type)) {
                outSpec.write("            <Attrib>\n");
                outSpec.write("               <Name>TYPE</Name>\n");
                outSpec.write("               <Value><![CDATA[" + attr.type + "]]></Value>\n");
                outSpec.write("            </Attrib>\n");
            }
            if (containsData(attr.languageCode)) {
                outSpec.write("            <Attrib>\n");
                outSpec.write("               <Name>LANGUAGE_CODE</Name>\n");
                outSpec.write("               <Value><![CDATA[" + attr.languageCode + "]]></Value>\n");
                outSpec.write("            </Attrib>\n");
            }
            if (containsData(attr.countryCode)) {
                outSpec.write("            <Attrib>\n");
                outSpec.write("               <Name>COUNTRY_CODE</Name>\n");
                outSpec.write("               <Value><![CDATA[" + attr.countryCode + "]]></Value>\n");
                outSpec.write("            </Attrib>\n");
            }
            outSpec.write("            <Attrib>\n");
            outSpec.write("               <Name>MAX_OCCURRENCE</Name>\n");
            outSpec.write("               <Value><![CDATA[" + attr.max + "]]></Value>\n");
            outSpec.write("            </Attrib>\n");
            if (attr.length != null) {
                outSpec.write("            <Attrib>\n");
                outSpec.write("               <Name>MAXLENGTH</Name>\n");
                outSpec.write("               <Value><![CDATA[" + attr.length + "]]></Value>\n");
                outSpec.write("            </Attrib>\n");
            }
            outSpec.write("            <Attrib>\n");
            outSpec.write("               <Name>MIN_OCCURRENCE</Name>\n");
            outSpec.write("               <Value><![CDATA[" + attr.min + "]]></Value>\n");
            outSpec.write("            </Attrib>\n");
            outSpec.write("            <Attrib>\n");
            outSpec.write("               <Name>LOCALIZED</Name>\n");
            outSpec.write("               <Value><![CDATA[" + (attr.localized ? "yes" : "no") + "]]></Value>\n");
            outSpec.write("            </Attrib>\n");
            if (bLocalized) {
                outSpec.write("            <Attrib>\n");
                outSpec.write("               <Name>CASCADE</Name>\n");
                outSpec.write("               <Value><![CDATA[yes]]></Value>\n");
                outSpec.write("            </Attrib>\n");
            }
            if (containsData(attr.lookupTable)) {
                outSpec.write("            <Attrib>\n");
                outSpec.write("               <Name>LOOKUP_TABLE</Name>\n");
                outSpec.write("               <Value><![CDATA[" + attr.lookupTable + "]]></Value>\n");
                outSpec.write("            </Attrib>\n");
            }
            outSpec.write("            <Attrib>\n");
            outSpec.write("               <Name>LINK_TO_CATALOG</Name>\n");
            outSpec.write("               <Value><![CDATA[" + (attr.link ? "yes" : "no") + "]]></Value>\n");
            outSpec.write("            </Attrib>\n");
            if (!handler.getVersion().startsWith("5.2")) {
                outSpec.write("            <Attrib>\n");
                outSpec.write("               <Name>INDEXED</Name>\n");
                outSpec.write("               <Value><![CDATA[" + (attr.indexed ? "yes" : "no") + "]]></Value>\n");
                outSpec.write("            </Attrib>\n");
            }
            outSpec.write("            <Attrib>\n");
            outSpec.write("               <Name>EDITABLE</Name>\n");
            outSpec.write("               <Value><![CDATA[" + (attr.editable ? "yes" : "no") + "]]></Value>\n");
            outSpec.write("            </Attrib>\n");
            outSpec.write("            <Attrib>\n");
            outSpec.write("               <Name>NON_PERSISTED</Name>\n");
            outSpec.write("               <Value><![CDATA[" + (attr.nonPersisted ? "yes" : "no") + "]]></Value>\n");
            outSpec.write("            </Attrib>\n");
            if (attr.nonPersisted && !(attr.rule.contains("~=~"))) {
                outSpec.write("            <Attrib>\n");
                outSpec.write("               <Name>NON_PERSISTED_ATTRIBUTE_RULE</Name>\n");
                outSpec.write("               <Value><![CDATA[" + attr.rule + "]]></Value>\n");
                outSpec.write("            </Attrib>\n");
                attr.rule = "";
            }
            if (containsData(attr.defaultValue)) {
                outSpec.write("            <Attrib>\n");
                outSpec.write("               <Name>DEFAULT_VALUE</Name>\n");
                outSpec.write("               <Value><![CDATA[" + attr.defaultValue + "]]></Value>\n");
                outSpec.write("            </Attrib>\n");
            }
            outSpec.write("            <Attrib>\n");
            outSpec.write("               <Name>HIDDEN</Name>\n");
            outSpec.write("               <Value><![CDATA[" + (attr.hidden ? "yes" : "no") + "]]></Value>\n");
            outSpec.write("            </Attrib>\n");
            for (String enumValue : attr.enumValues) {
                outSpec.write("            <Attrib>\n");
                outSpec.write("               <Name>" + attr.type + "</Name>\n");
                outSpec.write("               <Value><![CDATA[" + enumValue + "]]></Value>\n");
                outSpec.write("            </Attrib>\n");
            }
            for (String locale : EnvironmentHandler.getAllLocales()) {
                String sDisplayName = attr.localeToDisplayName.get(locale);
                if (containsData(sDisplayName)) {
                    outSpec.write("            <Attrib>\n");
                    outSpec.write("               <Name>" + locale + "_display_name</Name>\n");
                    outSpec.write("               <Value><![CDATA[" + sDisplayName + "]]></Value>\n");
                    outSpec.write("            </Attrib>\n");
                }
            }
            if (containsData(attr.rule) && attr.rule.contains("|")) {
                // First we need to split up the rules (there could be more then one)... [BF#69354]
                String[] aRules = attr.rule.split("\\Q~=~\\E");
                for (int i = 0; i < aRules.length; i++) {
                    // Then we can look at each rule and output it individually...
                    String sRuleType = aRules[i].substring(0, aRules[i].indexOf("|"));
                    String sRule = aRules[i].substring(aRules[i].indexOf("|") + 1);
                    outSpec.write("            <Attrib>\n");
                    outSpec.write("               <Name>" + sRuleType + "</Name>\n");
                    outSpec.write("               <Value><![CDATA[" + sRule + "]]></Value>\n");
                    outSpec.write("            </Attrib>\n");
                }
            }

            outSpec.write("         </Attribs>\n");
            outSpec.write("      </Node>\n");
            if (bLocalized) {
                Attribute attrLocalized = new Attribute(attr);
                attrLocalized.localized = false;
                for (String locale : EnvironmentHandler.getAllLocales()) {
                    String[] aLocaleTokens = locale.split("_");
                    attrLocalized.languageCode = aLocaleTokens[0];
                    attrLocalized.countryCode = aLocaleTokens[1];
                    outputAttributeXML(handler, attrLocalized, locale, outSpec);
                }
            }

        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputEntityCSV(Writer outFile, String sOutputPath) throws IOException {

        List<String> line = new ArrayList<>();

        for (Map.Entry<String, Attribute> entry : hmAttributes.entrySet()) {
            String sAttrPath = entry.getKey();
            Attribute attr = entry.getValue();
            line.add("");
            line.add(getName());
            line.add(getType());
            line.add(attr.path);
            line.add(attr.type);
            line.add("" + attr.primaryKey);
            line.add("" + attr.indexed);
            line.add("" + attr.localized);
            line.add("" + attr.link);
            line.add("" + attr.min);
            line.add("" + attr.max);
            line.add("" + attr.editable);
            line.add("" + attr.nonPersisted);
            line.add(attr.defaultValue);
            line.add("" + attr.length);
            line.add("" + attr.hidden);
            // Handle output of rules and extensions in CSV
            for (String locale : EnvironmentHandler.getAllLocales()) {
                line.add(attr.localeToDisplayName.get(locale));
            }
        }

        outputCSV(line, outFile);

    }

    /**
     * Retrieve the name of this instance of a spec.
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieve the type of this instance of a spec.
     * @return String
     */
    public String getType() {
        return type;
    }

    /**
     * Retrieve the primary key attribute path of this instance of a spec.
     * @return String
     */
    public String getPrimaryKeyPath() {
        return primaryKeyPath;
    }

    /**
     * Retrieve the mapping of attribute paths to attributes for this instance of a spec.
     * @return {@code Map<String, Attribute>}
     */
    public Map<String, Attribute> getAttributes() {
        return hmAttributes;
    }

    /**
     * Internal class to represent the details of each row in the Spec CSV.
     */
    public static class Attribute {

        private String path = "";
        private String type = "";
        private boolean primaryKey = false;
        private boolean indexed = false;
        private boolean localized = false;
        private boolean link = false;
        private int min = 0;
        private int max = 1;
        private boolean editable = true;
        private boolean nonPersisted = false;
        private String defaultValue = "";
        private Integer length;
        private boolean hidden = false;
        private String rule = "";
        private String lookupTable = "";
        private List<String> enumValues;

        private String countryCode = "";
        private String languageCode = "";

        private Map<String, String> localeToDisplayName;

        /**
         * Construct an empty attribute.
         */
        public Attribute() {
            enumValues = new ArrayList<>();
            localeToDisplayName = new TreeMap<>();
        }

        /**
         * Construct a new attribute based on the provided attribute.
         * @param attr attribute on which to base the new attribute
         */
        public Attribute(Attribute attr) {
            this();
            this.path = attr.path;
            this.type = attr.type;
            this.primaryKey = attr.primaryKey;
            this.indexed = attr.indexed;
            this.localized = attr.localized;
            this.link = attr.link;
            this.min = attr.min;
            this.max = attr.max;
            this.editable = attr.editable;
            this.nonPersisted = attr.nonPersisted;
            this.defaultValue = attr.defaultValue;
            this.length = attr.length;
            this.hidden = attr.hidden;
            this.localeToDisplayName = attr.localeToDisplayName;
        }

        /**
         * Retrieve the type of this instance of an attribute.
         * @return String
         */
        public String getType() {
            return type;
        }

        /**
         * Retrieve the minimum number of occurrences expected for the values of this instance of an attribute.
         * @return int
         */
        public int getMin() {
            return min;
        }

        /**
         * Retrieve the maximum number of occurrences expected for the values of this instance of an attribute.
         * @return int
         */
        public int getMax() {
            return max;
        }

        /**
         * Indicates whether this instance of an attribute is indexed (true) or not (false).
         * @return boolean
         */
        public boolean isIndexed() {
            return indexed;
        }

        /**
         * Indicates whether this instance of an attribute is localized (true) or not (false).
         * @return boolean
         */
        public boolean isLocalized() {
            return localized;
        }

        /**
         * Retrieve the name of the lookup table this instance of an attribute references, or null if none is referenced.
         * @return String
         */
        public String getLookupTable() {
            return lookupTable;
        }

    }

}
