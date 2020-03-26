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
 * Processes <b>Lookups.csv</b>.
 */
public class Lookup extends BasicEntity {

    public static final String NAME = "Lookup Table Name";
    public static final String SPEC_NAME = "Lookup Table Spec";

    private String name;
    private String specName;
    private String displayAttribute;

    private static class Singleton {
        private static final Lookup INSTANCE = new Lookup();
    }

    /**
     * Retrieve the static definition of a LookupTable (ie. its columns and type information).
     * @return LookupTable
     */
    public static Lookup getInstance() {
        return Lookup.Singleton.INSTANCE;
    }

    private Lookup() {
        super("LOOKUP_TABLE", "LookupTables");
        addColumn(COUNTRY_SPECIFIC);
        addColumn(NAME);
        addColumn(SPEC_NAME);
    }

    /**
     * Construct a new instance of a Lookup Table using the provided field values.
     * @param <T> expected to be LookupTable whenever used by this class
     * @param aFields from which to construct the Lookup Table
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BasicEntity> T createInstance(List<String> aFields) {
        Lookup lt = new Lookup();
        lt.name = getFieldValue(NAME, aFields);
        lt.specName = getFieldValue(SPEC_NAME, aFields);
        Spec spec = (Spec) BasicEntityHandler.getFromCache(lt.specName, Spec.class.getName());
        lt.displayAttribute = spec.getPrimaryKeyPath();
        return (T) lt;
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
        outFile.write("   <LOOKUP_TABLE>\n");
        outFile.write(getNodeXML("Name", getName()));
        outFile.write(getNodeXML("Action", "CREATE_OR_UPDATE"));
        outFile.write(getNodeXML("UsesInheritance", "false"));
        outFile.write(getNodeXML("Spec", getSpecName()));
        outFile.write("      <ACG isDefault=\"true\" />\n");
        outFile.write(getNodeXML("PrimaryCategoryTree", "-1"));
        outFile.write(getNodeXML("SecondaryCategoryTrees", ""));
        outFile.write(getNodeXML("DisplayAttribute", getDisplayAttribute()));
        outFile.write("   </LOOKUP_TABLE>\n");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputEntityCSV(Writer outFile, String sOutputPath) throws IOException {
        List<String> line = new ArrayList<>();
        line.add("");
        line.add(getName());
        line.add(getSpecName());
        outputCSV(line, outFile);
    }

    /**
     * Retrieve the name of this instance of a lookup table.
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieve the name of the spec for this instance of a lookup table.
     * @return String
     */
    public String getSpecName() {
        return specName;
    }

    /**
     * Retrieve the attribute path for the display attribute for this instance of a lookup table.
     * @return String
     */
    public String getDisplayAttribute() {
        return displayAttribute;
    }

}
