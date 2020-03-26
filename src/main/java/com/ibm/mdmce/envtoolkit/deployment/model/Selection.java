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
import java.util.List;

/**
 * Processes <b>Selections.csv</b>.
 */
public class Selection extends BasicEntity {

    public static final String NAME = "Selection Name";
    public static final String TYPE = "Type";
    public static final String CATALOG = "Catalog";
    public static final String HIERARCHY = "Hierarchy";
    public static final String ACG = "ACG";
    public static final String CATEGORIES = "Categories";
    public static final String RULE = "Rule";

    private String name;
    private String type = "S";
    private String catalog;
    private String hierarchy;
    private String acg = com.ibm.mdmce.envtoolkit.deployment.model.ACG.DEFAULT_ACG;
    private List<String> categoryPaths = new ArrayList<>();
    private String rule;

    private static class Singleton {
        private static final Selection INSTANCE = new Selection();
    }

    /**
     * Retrieve the static definition of a Selection (ie. its columns and type information).
     * @return Selection
     */
    public static Selection getInstance() {
        return Selection.Singleton.INSTANCE;
    }

    private Selection() {
        super("SELECTION", "Selections");
        addColumn(COUNTRY_SPECIFIC);
        addColumn(NAME);
        addColumn(TYPE);
        addColumn(CATALOG);
        addColumn(HIERARCHY);
        addColumn(ACG);
        addColumn(CATEGORIES);
        addColumn(RULE);
    }

    /**
     * Construct a new instance of a Selection using the provided field values.
     * @param <T> expected to be Selection whenever used by this class
     * @param aFields from which to construct the Selection
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BasicEntity> T createInstance(List<String> aFields) {
        Selection sel = new Selection();
        sel.name = getFieldValue(NAME, aFields);
        sel.type = getFieldValue(TYPE, aFields);
        sel.catalog = getFieldValue(CATALOG, aFields);
        sel.hierarchy = getFieldValue(HIERARCHY, aFields);
        sel.acg = getFieldValue(ACG, aFields);
        sel.categoryPaths = CSVParser.checkList(getFieldValue(CATEGORIES, aFields), ",");
        sel.rule = getFieldValue(RULE, aFields);
        return (T) sel;
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

        String sSelectionType = getType();

        outFile.write("   <SELECTION>\n");
        outFile.write(getNodeXML("SelectionType", sSelectionType));
        outFile.write(getNodeXML("Name", getName()));
        outFile.write(getNodeXML("Action", "CREATE_OR_UPDATE"));
        if (getAcg().equals(com.ibm.mdmce.envtoolkit.deployment.model.ACG.DEFAULT_ACG))
            outFile.write("      <Acg isDefault=\"true\"/>\n");
        else
            outFile.write(getNodeXML("Acg", getAcg()));
        outFile.write(getNodeXML("Catalog", getCatalog()));
        outFile.write(getNodeXML("CatalogVersion", "999999999"));
        outFile.write(getNodeXML("Hierarchy", getHierarchy()));

        if (sSelectionType.equals("S")) {

            outFile.write("      <CategoryPaths>\n");
            for (String path : getCategoryPaths()) {
                outFile.write("         <CategoryPath><![CDATA[" + path + "]]></CategoryPath>\n");
            }
            outFile.write("      </CategoryPaths>\n");

            // TODO: Support static item selections
            outFile.write("      <Items />\n");
            // TODO: Support including unassigned items
            outFile.write("      <IncludeUnassigned>false</IncludeUnassigned>\n");
        } else {
            outFile.write(getNodeXML("RuleString", getRule()));
        }

        outFile.write("   </SELECTION>\n");

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
        line.add(getCatalog());
        line.add(getHierarchy());
        line.add(getAcg());
        line.add(escapeForCSV(String.join(",", getCategoryPaths())));
        line.add(getRule());
        outputCSV(line, outFile);
    }

    /**
     * Retrieve the name of this instance of a selection.
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieve the type of this instance of a selection.
     * @return String
     */
    public String getType() {
        return type;
    }

    /**
     * Retrieve the catalog for this instance of a selection.
     * @return String
     */
    public String getCatalog() {
        return catalog;
    }

    /**
     * Retrieve the hierarchy for this instance of a selection.
     * @return String
     */
    public String getHierarchy() {
        return hierarchy;
    }

    /**
     * Retrieve the access control group for this instance of a selection.
     * @return String
     */
    public String getAcg() {
        return acg;
    }

    /**
     * Retrieve the list of category paths for this instance of a selection.
     * @return String
     */
    public List<String> getCategoryPaths() {
        return categoryPaths;
    }

    /**
     * Retrieve the rule or WQL query for this instance of a selection.
     * @return String
     */
    public String getRule() {
        return rule;
    }

}
