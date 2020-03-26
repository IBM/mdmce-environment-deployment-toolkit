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
 * Processes <b>AttrCollections.csv</b>.
 */
public class AttrCollection extends BasicEntity {

    public static final String NAME = "Attribute Collection Name";
    public static final String TYPE = "Type";
    public static final String DESCRIPTION = "Description";
    public static final String SPEC_NAME = "Spec Name";
    public static final String ATTRIBUTE_PATH = "Attribute Path";

    private String name;
    private String type = "GENERAL";
    private String description;

    private Set<String> localizedAttributePaths = new HashSet<>();
    private Set<String> allAttributePaths = new TreeSet<>();
    private Map<String, List<String>> specsToAttributes = new TreeMap<>();
    private Set<String> dynamicSpecs = new HashSet<>();

    private static class Singleton {
        private static final AttrCollection INSTANCE = new AttrCollection();
    }

    /**
     * Retrieve the static definition of an AttributeCollection (ie. its columns and type information).
     * @return AttributeCollection
     */
    public static AttrCollection getInstance() {
        return AttrCollection.Singleton.INSTANCE;
    }

    private AttrCollection() {
        super("ATTRIBUTE_COLS", "AttributeCollections");
        addColumn(COUNTRY_SPECIFIC);
        addColumn(NAME);
        addColumn(TYPE);
        addColumn(DESCRIPTION);
        addColumn(SPEC_NAME);
        addColumn(ATTRIBUTE_PATH);
    }

    /**
     * Construct a new instance of an Attribute Collection using the provided field values.
     * @param <T> expected to be AttributeCollection whenever used by this class
     * @param aFields from which to construct the Attribute Collection
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BasicEntity> T createInstance(List<String> aFields) {

        String sAttrColName = getFieldValue(NAME, aFields);
        AttrCollection attrCol = (AttrCollection) BasicEntityHandler.getFromCache(sAttrColName, AttrCollection.class.getName(), false, false);
        if (attrCol == null) {
            attrCol = new AttrCollection();
            attrCol.name = sAttrColName;
        }
        attrCol.type = getFieldValue(TYPE, aFields);
        boolean bFullSpec = false;
        attrCol.description = getFieldValue(DESCRIPTION, aFields);
        if (attrCol.type.equals("FULL_SPEC")) {
            attrCol.type = "GENERAL";
            bFullSpec = true;
        } else if (attrCol.type.equals("FULL_SPEC_SEARCH")) {
            attrCol.type = "SEARCH_TEMPLATE";
            bFullSpec = true;
        }
        String sSpecName = getFieldValue(SPEC_NAME, aFields);
        String sAttrPath = getFieldValue(ATTRIBUTE_PATH, aFields);
        if (bFullSpec) {
            attrCol.dynamicSpecs.add(sSpecName);
        }
        boolean localized = false;
        Spec spec = (Spec) BasicEntityHandler.getFromCache(sSpecName, Spec.class.getName(), false, false);
        if (spec != null) {
            Spec.Attribute attr = spec.getAttributes().get(sAttrPath);
            if (attr != null) {
                localized = attr.isLocalized();
            }
        }
        if (!sSpecName.equals("") && !sAttrPath.equals("")) {
            String fullPath = sSpecName + "/" + sAttrPath;
            attrCol.allAttributePaths.add(fullPath);
            if (localized) {
                attrCol.localizedAttributePaths.add(sSpecName + "/" + sAttrPath);
            }
            List<String> attrsForSpec = attrCol.specsToAttributes.get(sSpecName);
            if (attrsForSpec == null) {
                attrsForSpec = new ArrayList<>();
            }
            attrsForSpec.add(sAttrPath);
            attrCol.specsToAttributes.put(sSpecName, attrsForSpec);
        } else if (!sSpecName.equals("")) {
            attrCol.specsToAttributes.put(sSpecName, new ArrayList<>());
        }

        return (T) attrCol;

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
        outFile.write("   <ATTRIBUTE_COLS>\n");
        outFile.write(getNodeXML("Name", getName()));
        outFile.write(getNodeXML("Action", "CREATE_OR_UPDATE"));
        outFile.write(getNodeXML("Type", getType()));
        outFile.write(getNodeXML("Description", getDescription()));

        Map<String, List<String>> hmSpecsToAttrs = getSpecsToAttributes();

        if (!hmSpecsToAttrs.isEmpty()) {
            outFile.write("      <SpecList>\n");
            for (Map.Entry<String, List<String>> entry : hmSpecsToAttrs.entrySet()) {
                String sSpecName = entry.getKey();
                List<String> aAttrPaths = entry.getValue();
                outFile.write("         <Spec name=\"" + BasicEntityHandler.escapeForXML(sSpecName) + "\">\n");
                if (getDynamicSpecs().contains(sSpecName)) {
                    outFile.write("            <Dynamic>true</Dynamic>\n");
                } else {
                    for (String sAttrPath : aAttrPaths) {
                        boolean bLocalized = localizedAttributePaths.contains(sSpecName + "/" + sAttrPath);
                        outFile.write("            <Attr name=\"" + sAttrPath + "\">\n");
                        outFile.write("               <Dynamic>" + bLocalized + "</Dynamic>\n");
                        outFile.write("            </Attr>\n");
                    }
                }
                outFile.write("         </Spec>\n");
            }
            outFile.write("      </SpecList>\n");
        }
        outFile.write("   </ATTRIBUTE_COLS>\n");
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
        line.add(getDescription());
        // TODO: this is not actually going to work, we need each path on a separate line not as columns...
        line.addAll(getAllAttributePaths());
        outputCSV(line, outFile);
    }

    /**
     * Retrieve the name of this instance of an attribute collection.
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieve the type of this instance of an attribute collection.
     * @return String
     */
    public String getType() {
        return type;
    }

    /**
     * Retrieve the description of this instance of an attribute collection.
     * @return String
     */
    public String getDescription() {
        return description;
    }

    /**
     * Retrieve a listing of all attribute paths for this attribute collection.
     * @return {@code List<String>}
     */
    public List<String> getAllAttributePaths() {
        return new ArrayList<>(allAttributePaths);
    }

    /**
     * Retrieve a set of all dynamic specs for this attribute collection.
     * @return {@code Set<String>}
     */
    public Set<String> getDynamicSpecs() {
        return dynamicSpecs;
    }

    /**
     * Retrieve a mapping from spec name to a listing of all attributes for this attribute collection.
     * @return {@code Map<String, List<String>>}
     */
    public Map<String, List<String>> getSpecsToAttributes() {
        return specsToAttributes;
    }

}
