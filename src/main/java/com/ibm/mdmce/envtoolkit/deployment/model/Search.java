/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment.model;

import com.ibm.mdmce.envtoolkit.deployment.BasicEntityHandler;
import com.ibm.mdmce.envtoolkit.deployment.CSVParser;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * Processes <b>Searches.csv</b>.
 */
public abstract class Search extends BasicEntity {

    public static final String SEARCH_NAME = "Search Name";
    public static final String SEARCH_TYPE = "Search Type";
    public static final String CONTAINER_NAME = "Container Name";
    public static final String CONTAINER_TYPE = "Container Type";
    public static final String IS_DEFAULT = "Is Default";
    public static final String IS_SHARED = "Is Shared";
    public static final String USERNAME = "Username";
    public static final String DESCRIPTION = "Description";
    public static final String SEARCH_SCOPE = "Search Scope";
    public static final String SORT_TYPE = "Sort Type";
    public static final String SORT_ATTRIBUTE_PATH = "Sort Attribute Path";
    public static final String CATEGORY_RESTRICTION = "Category Restriction";
    public static final String SAVE_PARAMETERS = "Save Parameters";
    public static final String STEP_NAME = "Step Name";
    public static final String RESERVER_BY = "Reserved by";
    public static final String CATEGORIES_FILTER = "Categories Filter";
    public static final String ATTRIBUTE_PATH = "Attribute Path";
    public static final String NEGATE = "Negate";
    public static final String SEARCH_OPERATOR = "Search operator";
    public static final String VALUE = "Value";
    public static final String SECOND_VALUE = "Second value";
    public static final String LOGICAL_OPERATOR = "Logical operator";

    public static final List<String> containerTypeList = Arrays.asList(new String[] {"CATALOG", "HIERARCHY", "COL_AREA"});
    public static final List<String> resrvedByList = Arrays.asList(new String[] {"RESERVED_BY_ME", "RESERVED_BY_OTHERS", "AVAILABLE"});
    public static final List<String> searchTypeList = Arrays.asList(new String[] {"TEMPLATE", "QUERY"});
    public static final List<String> sortTypeList = Arrays.asList(new String[] {"ASCENDING", "DESCENDING"});
    public static final List<String> categoryRestrictionList = Arrays.asList(new String[] {"ANY", "ALL"});
    public static final List<String> searchOperatorList = Arrays.asList(new String[] {"CONTAINS", "ENDS_WITH", "IS_EMPTY", "BEGINS_WITH_MATCH_CASE", "ENDS_WITH_MATCH_CASE", "EQUAL", "EQUAL_MATCH_CASE", "CONTAINS_MATCH_CASE", "GREATER_THAN_OR_EQUAL", "LESS_THAN_OR_EQUAL", "GREATER_THAN", "LESS_THAN", "BETWEEN"});
    public static final List<String> logicalOperatorList = Arrays.asList(new String[] {"AND", "OR"});

    private String searchName;
    private String searchType;
    private String containerType;
    private String containerName;
    private boolean isDefaut = false;
    private boolean isShared = false;
    private String username;
    private String description;
    private String searchScope = "ENTIRE_CATALOG";
    private String sortType = "ASCENDING";
    private String sortAttributePath;
    private String categoryRestriction = "ANY";
    private boolean saveParameters = false;
    private String stepName;
    private String reservedBy;
    private List<String> categoriesFilter = new ArrayList<>();
    private List<Map<String, String>> attributes = new ArrayList<>();

    protected Search(String objectType, String rootElement) {
        super(objectType, rootElement);
        addColumn(COUNTRY_SPECIFIC);
        addColumn(SEARCH_NAME);
        addColumn(SEARCH_TYPE);
        addColumn(CONTAINER_NAME);
        addColumn(CONTAINER_TYPE);
        addColumn(IS_DEFAULT);
        addColumn(IS_SHARED);
        addColumn(USERNAME);
        addColumn(DESCRIPTION);
        addColumn(SEARCH_SCOPE);
        addColumn(SORT_TYPE);
        addColumn(SORT_ATTRIBUTE_PATH);
        addColumn(CATEGORY_RESTRICTION);
        addColumn(SAVE_PARAMETERS);
        addColumn(STEP_NAME);
        addColumn(RESERVER_BY);
        addColumn(CATEGORIES_FILTER);
        addColumn(ATTRIBUTE_PATH);
        addColumn(NEGATE);
        addColumn(SEARCH_OPERATOR);
        addColumn(VALUE);
        addColumn(SECOND_VALUE);
        addColumn(LOGICAL_OPERATOR);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUniqueId() { return getSearchName() + "::" + getUserName(); }

    @Override
    public void outputEntityCSV(Writer outFile, String sOutputPath) throws IOException {
        List<String> line = new ArrayList<>();
        line.add("");
        line.add(getSearchName());
        line.add(getSearchType());
        line.add(getContainerName());
        line.add(getContainerType());
        line.add(isDefaut()?"x":"");
        line.add(isShared()?"x":"");
        line.add(getUserName());
        line.add(getDescription());
        line.add(getSearchScope());
        line.add(getSortType());
        line.add(getSortAttributePath());
        line.add(getCategoryRestriction());
        line.add(""+getSaveParameters());
        line.add(getStepName());
        line.add(getReservedBy());
        String catFilter = "";
        String catDelim = "";
        for (String cat : categoriesFilter) {
            if (!catFilter.isEmpty()) {
                catDelim = ",";
            }
            catFilter = catFilter + catDelim + cat;
        }
        line.add(catFilter);
        for (Map<String, String> attr : getAttributes()) {
            List<String> outLine = new ArrayList<>();
            outLine.addAll(line);
            outLine.add(attr.get(ATTRIBUTE_PATH));
            outLine.add(attr.get(NEGATE));
            outLine.add(attr.get(SEARCH_OPERATOR));
            outLine.add(attr.get(VALUE));
            outLine.add(attr.get(SECOND_VALUE));
            outLine.add(attr.get(LOGICAL_OPERATOR));
            outputCSV(outLine, outFile);
        }
    }

    public void fillInstance(Search s, List<String> aFields) {
        if (s != null) {
            if (s.searchName == null || (s.searchName != null && s.searchName.isEmpty())) {
                s.searchName = getFieldValue(SEARCH_NAME, aFields);
                s.searchType = getFieldValue(SEARCH_TYPE, aFields);
                s.containerName = getFieldValue(CONTAINER_NAME, aFields);
                s.containerType = getFieldValue(CONTAINER_TYPE, aFields);
                s.isDefaut = CSVParser.checkBoolean(getFieldValue(IS_DEFAULT, aFields));
                s.isShared = CSVParser.checkBoolean(getFieldValue(IS_SHARED, aFields));
                s.username = getFieldValue(USERNAME, aFields);
                s.description = getFieldValue(DESCRIPTION, aFields);
                String ss = getFieldValue(SEARCH_SCOPE, aFields);
                if (ss != null && !ss.isEmpty()) {
                    s.searchScope = ss;
                }
                String st = getFieldValue(SORT_TYPE, aFields);
                if (st != null && !st.isEmpty()) {
                    s.sortType = st;
                }
                s.sortAttributePath = getFieldValue(SORT_ATTRIBUTE_PATH, aFields);
                String cr = getFieldValue(CATEGORY_RESTRICTION, aFields);
                if (cr != null && !cr.isEmpty()) {
                    s.categoryRestriction = cr;
                }
                s.saveParameters = CSVParser.checkBoolean(getFieldValue(SAVE_PARAMETERS, aFields));
                s.stepName = getFieldValue(STEP_NAME, aFields);
                s.reservedBy = getFieldValue(RESERVER_BY, aFields);

                String catFilter = getFieldValue(CATEGORIES_FILTER, aFields).trim();
                if (!catFilter.isEmpty()) {
                    s.categoriesFilter.addAll(Arrays.asList(getFieldValue(CATEGORIES_FILTER, aFields).split(",")));
                    s.categoriesFilter.removeAll(Arrays.asList(""));
                }
            }
            Map<String, String> attr = new HashMap<>();
            attr.put(ATTRIBUTE_PATH, getFieldValue(ATTRIBUTE_PATH, aFields));
            boolean negate = CSVParser.checkBoolean(getFieldValue(NEGATE, aFields));
            if (negate) {
                attr.put(NEGATE, "true");
            } else {
                attr.put(NEGATE, "false");
            }
            attr.put(SEARCH_OPERATOR, getFieldValue(SEARCH_OPERATOR, aFields));
            attr.put(VALUE, getFieldValue(VALUE, aFields));
            attr.put(SECOND_VALUE, getFieldValue(SECOND_VALUE, aFields));
            attr.put(LOGICAL_OPERATOR, getFieldValue(LOGICAL_OPERATOR, aFields));
            s.attributes.add(attr);

        }
    }

    public void outputSSTblobXML(BasicEntityHandler handler, Writer outFile, String sOutputPath, String sCompanyCode) throws IOException {
        outFile.write("<SST_blob>" + BasicEntityHandler.escapeForXML("<?xml version=\"1.0\" encoding=\"UTF-8\"?>") + "\n");
        outFile.write(BasicEntityHandler.escapeForXML("<search_template>") + "\n");
        if (getAttributes().size()>0) {
            outFile.write("    " + BasicEntityHandler.escapeForXML("<attributes>") + "\n");
            for (Map<String, String> attr : getAttributes()) {
                outFile.write("        " + BasicEntityHandler.escapeForXML("<attribute>") + "\n");
                outFile.write("            " + BasicEntityHandler.escapeForXML("<attribute_path>"));
                outFile.write(attr.get(ATTRIBUTE_PATH));
                outFile.write(BasicEntityHandler.escapeForXML("</attribute_path>") + "\n");
                outFile.write("            " + BasicEntityHandler.escapeForXML("<negate>"));
                outFile.write(attr.get(NEGATE));
                outFile.write(BasicEntityHandler.escapeForXML("</negate>") + "\n");
                outFile.write("            " + BasicEntityHandler.escapeForXML("<search_operator>"));
                outFile.write(attr.get(SEARCH_OPERATOR));
                outFile.write(BasicEntityHandler.escapeForXML("</search_operator>") + "\n");
                outFile.write("            " + BasicEntityHandler.escapeForXML("<value>"));
                outFile.write(attr.get(VALUE));
                outFile.write(BasicEntityHandler.escapeForXML("</value>") + "\n");
                outFile.write("            " + BasicEntityHandler.escapeForXML("<second_value>"));
                outFile.write(attr.get(SECOND_VALUE));
                outFile.write(BasicEntityHandler.escapeForXML("</second_value>") + "\n");
                outFile.write("            " + BasicEntityHandler.escapeForXML("<logical_operator>"));
                outFile.write(attr.get(LOGICAL_OPERATOR));
                outFile.write(BasicEntityHandler.escapeForXML("</logical_operator>") + "\n");
                outFile.write("        " + BasicEntityHandler.escapeForXML("</attribute>") + "\n");
            }
            outFile.write("    " + BasicEntityHandler.escapeForXML("</attributes>") + "\n");
        }
        outFile.write("    " + BasicEntityHandler.escapeForXML("<description>"));
        outFile.write(getDescription());
        outFile.write(BasicEntityHandler.escapeForXML("</description>") + "\n");
        if (getCategoriesFilter().size() > 0) {
            outFile.write("    " + BasicEntityHandler.escapeForXML("<categories>") + "\n");
            for (String cat: getCategoriesFilter()) {
                outFile.write("        " + BasicEntityHandler.escapeForXML("<category_path>") + cat + BasicEntityHandler.escapeForXML("</category_path>") + "\n");
                outFile.write(cat);
                outFile.write(BasicEntityHandler.escapeForXML("</category_path>") + "\n");
            }
            outFile.write("    " + BasicEntityHandler.escapeForXML("</categories>") + "\n");
        } else {
            outFile.write("    " + BasicEntityHandler.escapeForXML("<categories/>") + "\n");
        }
        outFile.write("    " + BasicEntityHandler.escapeForXML("<options>") + "\n");
        outFile.write("        " + BasicEntityHandler.escapeForXML("<search_scope>"));
        outFile.write(getSearchScope());
        outFile.write(BasicEntityHandler.escapeForXML("</search_scope>") + "\n");
        outFile.write("        " + BasicEntityHandler.escapeForXML("<sort_type>"));
        outFile.write(getSortType());
        outFile.write(BasicEntityHandler.escapeForXML("</sort_type>") + "\n");
        outFile.write("        " + BasicEntityHandler.escapeForXML("<sort_attribute_path>"));
        outFile.write(getSortAttributePath());
        outFile.write(BasicEntityHandler.escapeForXML("</sort_attribute_path>") + "\n");
        outFile.write("        " + BasicEntityHandler.escapeForXML("<reserved_by>"));
        outFile.write(getReservedBy());
        outFile.write(BasicEntityHandler.escapeForXML("</reserved_by>") + "\n");
        outFile.write("    " + BasicEntityHandler.escapeForXML("</options>") + "\n");
        if (getStepName() != null && !getStepName().isEmpty()) {
            outFile.write("    " + BasicEntityHandler.escapeForXML("<collab_area_name>"));
            outFile.write(getContainerName());
            outFile.write(BasicEntityHandler.escapeForXML("</collab_area_name>") + "\n");
            outFile.write("    " + BasicEntityHandler.escapeForXML("<step_name>"));
            outFile.write(getStepName());
            outFile.write(BasicEntityHandler.escapeForXML("</step_name>") + "\n");
        } else {
            outFile.write("    " + BasicEntityHandler.escapeForXML("<collab_area_name/>") + "\n");
            outFile.write("    " + BasicEntityHandler.escapeForXML("<step_name/>") + "\n");
        }
        outFile.write("    " + BasicEntityHandler.escapeForXML("<category_restriction>"));
        outFile.write(getCategoryRestriction());
        outFile.write(BasicEntityHandler.escapeForXML("</category_restriction>") + "\n");
        outFile.write("    " + BasicEntityHandler.escapeForXML("<save_parameters>"));
        outFile.write(""+getSaveParameters());
        outFile.write(BasicEntityHandler.escapeForXML("</save_parameters>") + "\n");
        outFile.write(BasicEntityHandler.escapeForXML("</search_template>") + "\n");
        outFile.write("</SST_blob>\n");
    }


    /**
     * Retrieve the name of this instance of a search.
     * @return String
     */
    public String getSearchName() {
        return searchName;
    }

    /**
     * Retrieve the type of this instance of a search.
     * @return String
     */
    public String getSearchType() {
        return searchType;
    }

    /**
     * Retrieve the container name for this instance of a search.
     * @return String
     */
    public String getContainerName() {
        return containerName;
    }

    /**
     * Retrieve the container type for this instance of a search.
     * @return String
     */
    public String getContainerType() {
        return containerType;
    }

    /**
     * Retrieve the default for this instance of a search.
     * @return boolean
     */
    public boolean isDefaut() {
        return isDefaut;
    }

    /**
     * Retrieve the shared for this instance of a search.
     * @return boolean
     */
    public boolean isShared() {
        return isShared;
    }

    /**
     * Retrieve the user name for this instance of a search.
     * @return String
     */
    public String getUserName() {
        return username;
    }

    /**
     * Retrieve the description for this instance of a search.
     * @return String
     */
    public String getDescription() {
        return description;
    }

    /**
     * Retrieve the Search Scope for this instance of a search.
     * @return String
     */
    public String getSearchScope() {
        return searchScope;
    }

    /**
     * Retrieve the Sort Type for this instance of a search.
     * @return String
     */
    public String getSortType() {
        return sortType;
    }

    /**
     * Retrieve the Sort Attribute Path for this instance of a search.
     * @return String
     */
    public String getSortAttributePath() {
        return sortAttributePath;
    }

    /**
     * Retrieve the Category Restriction for this instance of a search.
     * @return String
     */
    public String getCategoryRestriction() {
        return categoryRestriction;
    }

    /**
     * Retrieve the Save Parameters for this instance of a search.
     * @return boolean
     */
    public boolean getSaveParameters() {
        return saveParameters;
    }

    /**
     * Retrieve the Step Name for this instance of a search.
     * @return String
     */
    public String getStepName() {
        return stepName;
    }

    /**
     * Retrieve the Reserved By for this instance of a search.
     * @return String
     */
    public String getReservedBy() {
        return reservedBy;
    }

    /**
     * Retrieve the Categories Filter for this instance of a search.
     * @return List<String>
     */
    public List<String> getCategoriesFilter() {
        return categoriesFilter;
    }

    /**
     * Retrieve the list of Attribute Path, Negate, Search Operator, Value, Second Value, Logical Operator for this instance of a search.
     * @return List<Map<String, String>>
     */
    public List<Map<String, String>> getAttributes() {
        return attributes;
    }

    /**
     * Retrieve the Is New UI for this instance of a search.
     * @return boolean
     */
    public static boolean isNewUI(String version) {
        if (version != null && !version.isEmpty()) {
            version = version.replaceAll("^\\D*?(\\d+).*$", "$1");
            if (!version.isEmpty()) {
                int ver = Integer.valueOf(version);
                if (ver < 9) {
                    return false;
                }
            }
        }
        return true;
    }
}
