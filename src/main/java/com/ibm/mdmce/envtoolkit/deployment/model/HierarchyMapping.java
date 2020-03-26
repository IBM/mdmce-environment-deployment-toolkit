/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment.model;

import com.ibm.mdmce.envtoolkit.deployment.BasicEntityHandler;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * Processes <b>HierarchyMappings.csv</b>.
 */
public class HierarchyMapping extends BasicEntity {

    public static final String SRC_HIERARCHY = "Source Hierarchy";
    public static final String DST_HIERARCHY = "Destination Hierarchy";
    public static final String SRC_CATEGORY = "Source Category Path";
    public static final String DST_CATEGORY = "Destination Category Path";

    public String sourceHierarchy;
    public String destinationHierarchy;

    public Map<String, List<String>> sourceToDestinationCategories = new HashMap<>();

    private static class Singleton {
        private static final HierarchyMapping INSTANCE = new HierarchyMapping();
    }

    /**
     * Retrieve the static definition of a HierarchyMapping (ie. its columns and type information).
     * @return HierarchyMapping
     */
    public static HierarchyMapping getInstance() {
        return HierarchyMapping.Singleton.INSTANCE;
    }

    private HierarchyMapping() {
        super("HIERARCHY_MAPS", "HierarchyMap");
        addColumn(COUNTRY_SPECIFIC);
        addColumn(SRC_HIERARCHY);
        addColumn(DST_HIERARCHY);
        addColumn(SRC_CATEGORY);
        addColumn(DST_CATEGORY);
    }

    /**
     * Construct a new instance of a Hierarchy Mapping using the provided field values.
     * @param <T> expected to be HierarchyMapping whenever used by this class
     * @param aFields from which to construct the Hierarchy Mapping
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BasicEntity> T createInstance(List<String> aFields) {
        String src = getFieldValue(SRC_HIERARCHY, aFields);
        String dst = getFieldValue(DST_HIERARCHY, aFields);
        String sMappingKey = src + "::" + dst;
        HierarchyMapping hierarchyMap = (HierarchyMapping) BasicEntityHandler.getFromCache(sMappingKey, HierarchyMapping.class.getName(), false, false);
        if (hierarchyMap == null) {
            hierarchyMap = new HierarchyMapping();
            hierarchyMap.sourceHierarchy = src;
            hierarchyMap.destinationHierarchy = dst;
        }
        String sSrcPath = getFieldValue(SRC_CATEGORY, aFields);
        String sDstPath = getFieldValue(DST_CATEGORY, aFields);
        List<String> alDestinationsForSrc = hierarchyMap.sourceToDestinationCategories.getOrDefault(sSrcPath, new ArrayList<>());
        alDestinationsForSrc.add(sDstPath);
        hierarchyMap.sourceToDestinationCategories.put(sSrcPath, alDestinationsForSrc);
        return (T) hierarchyMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUniqueId() {
        return getSourceHierarchy() + "::" + getDestinationHierarchy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputEntityXML(BasicEntityHandler handler, Writer outFile, String sOutputPath, String sCompanyCode) throws IOException {
        outFile.write("   <HIERARCHY_MAPS>\n");
        outFile.write(getNodeXML("Action", "CREATE_OR_UPDATE"));
        outFile.write(getNodeXML("SourceHierarchyTree", getSourceHierarchy()));
        outFile.write(getNodeXML("DestinationHierarchyTree", getDestinationHierarchy()));
        for (Map.Entry<String, List<String>> entry : getSourceToDestinationCategories().entrySet()) {
            String sSrcCategoryPath = entry.getKey();
            outFile.write("      <SourceHierarchy name=\"" + sSrcCategoryPath + "\">\n");
            List<String> alDestinations = entry.getValue();
            for (String sDstCategoryPath : alDestinations) {
                outFile.write("        <DestinationHierarchy>" + sDstCategoryPath + "</DestinationHierarchy>\n");
            }
            outFile.write("      </SourceHierarchy>\n");
        }
        outFile.write("   </HIERARCHY_MAPS>\n");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputEntityCSV(Writer outFile, String sOutputPath) throws IOException {
        List<String> line = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : getSourceToDestinationCategories().entrySet()) {
            String sSrcCategoryPath = entry.getKey();
            List<String> alDestinations = entry.getValue();
            for (String sDstCategoryPath : alDestinations) {
                line.add("");
                line.add(getSourceHierarchy());
                line.add(getDestinationHierarchy());
                line.add(sSrcCategoryPath);
                line.add(sDstCategoryPath);
                outputCSV(line, outFile);
            }
        }
    }

    /**
     * Retrieve the source hierarchy for this instance of a hierarchy mapping.
     * @return String
     */
    public String getSourceHierarchy() {
        return sourceHierarchy;
    }

    /**
     * Retrieve the destination hierarchy for this instance of a hierarchy mapping.
     * @return String
     */
    public String getDestinationHierarchy() {
        return destinationHierarchy;
    }

    /**
     * Retrieve the mapping from source category path to a list of its destination categories for this instance of a
     * hierarchy mapping.
     * @return String
     */
    public Map<String, List<String>> getSourceToDestinationCategories() {
        return sourceToDestinationCategories;
    }

}
