/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment.model;

import com.ibm.mdmce.envtoolkit.deployment.BasicEntityHandler;

import java.io.*;
import java.util.*;

/**
 * Processes <b>SpecMaps.csv</b>.
 */
public class SpecMap extends BasicEntity {

    public static final String NAME = "Spec Map Name";
    public static final String TYPE = "Spec Map Type";
    public static final String SRC_OBJ = "Source Object Name";
    public static final String DST_OBJ = "Destination Object Name";
    public static final String SRC_SPEC = "Source Spec Name";
    public static final String SRC_ATTR_PATH = "Source Attribute Path";
    public static final String DST_SPEC = "Destination Spec Name";
    public static final String DST_ATTR_PATH = "Destination Attribute Path";

    private String name;
    private String type;
    private String sourceObject;
    private String destinationObject;

    private Map<String, List<String>> sourceToDestinationPaths = new HashMap<>();

    private static class Singleton {
        private static final SpecMap INSTANCE = new SpecMap();
    }

    /**
     * Retrieve the static definition of a SpecMap (ie. its columns and type information).
     * @return SpecMap
     */
    public static SpecMap getInstance() {
        return SpecMap.Singleton.INSTANCE;
    }

    private SpecMap() {
        super("UNUSED", "ThisFileIsNotUsed");
        addColumn(COUNTRY_SPECIFIC);
        addColumn(NAME);
        addColumn(TYPE);
        addColumn(SRC_OBJ);
        addColumn(DST_OBJ);
        addColumn(SRC_SPEC);
        addColumn(SRC_ATTR_PATH);
        addColumn(DST_SPEC);
        addColumn(DST_ATTR_PATH);
    }

    /**
     * Construct a new spec map using the provided parameters.
     * @param name of the spec map
     * @param type of the spec map
     * @param sourceObject for the spec map
     * @param destinationObject for the spec map
     */
    public SpecMap(String name, String type, String sourceObject, String destinationObject) {
        this();
        this.name = name;
        this.type = type;
        this.sourceObject = sourceObject;
        this.destinationObject = destinationObject;
    }

    /**
     * Construct a new instance of a SpecMap using the provided field values.
     * @param <T> expected to be SpecMap whenever used by this class
     * @param aFields from which to construct the SpecMap
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BasicEntity> T createInstance(List<String> aFields) {
        String sSpecMapName = getFieldValue(NAME, aFields);
        SpecMap specMap = (SpecMap) BasicEntityHandler.getFromCache(sSpecMapName, SpecMap.class.getName(), false, false);
        if (specMap == null) {
            specMap = new SpecMap();
            specMap.name = sSpecMapName;
        }
        specMap.type = getFieldValue(TYPE, aFields);
        specMap.sourceObject = getFieldValue(SRC_OBJ, aFields);
        specMap.destinationObject = getFieldValue(DST_OBJ, aFields);
        String sSrcSpecName = getFieldValue(SRC_SPEC, aFields);
        String sDstSpecName = getFieldValue(DST_SPEC, aFields);
        String sSrcPath = sSrcSpecName + "/" + getFieldValue(SRC_ATTR_PATH, aFields);
        String sDstPath = sDstSpecName + "/" + getFieldValue(DST_ATTR_PATH, aFields);
        List<String> alDstAttrsForSrc = specMap.sourceToDestinationPaths.getOrDefault(sSrcPath, new ArrayList<>());
        alDstAttrsForSrc.add(sDstPath);
        specMap.sourceToDestinationPaths.put(sSrcPath, alDstAttrsForSrc);
        return (T) specMap;
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

        String sSpecMapName = getName();

        String sEscapedFilename = BasicEntityHandler.escapeForFilename(sSpecMapName);
        String sSpecMapFilePath = sOutputPath + File.separator + sEscapedFilename + ".xml";
        Writer outSpecMap = BasicEntityHandler.getNewWriter(sSpecMapFilePath);

        outSpecMap.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
        outSpecMap.write("<TrigoXML version=\"" + handler.getVersion() + "\">\n");
        outSpecMap.write("   <Header>\n");
        outSpecMap.write(getNodeXML("Name", getName()));
        outSpecMap.write(getNodeXML("Type", getType()));

        // use 'Create_or_Update' option for releases >= 5.3.2
        if (handler.versionUsesNextGenXML()) {
            outSpecMap.write("      <Action>Create_or_Update</Action>\n");
        } else {
            outSpecMap.write("      <Action>Create</Action>\n");
        }
        outSpecMap.write(getNodeXML("CompanyName", sCompanyCode));
        outSpecMap.write(getNodeXML("SrcSpecName", getSourceObject()));
        outSpecMap.write(getNodeXML("DstSpecName", getDestinationObject()));
        outSpecMap.write("   </Header>\n");

        Map<String, List<String>> hmSrcToDsts = getSourceToDestinationPaths();
        if (hmSrcToDsts.size() > 0) {

            outSpecMap.write("   <Maps>\n");

            for (Map.Entry<String, List<String>> entry : hmSrcToDsts.entrySet()) {
                String sSrcPath = entry.getKey();
                List<String> alDstPaths = entry.getValue();
                for (String sDstPath : alDstPaths) {
                    outSpecMap.write("      <Map>\n");
                    outSpecMap.write("         <SrcPath><![CDATA[" + sSrcPath + "]]></SrcPath>\n");
                    outSpecMap.write("         <DstPath><![CDATA[" + sDstPath + "]]></DstPath>\n");
                    outSpecMap.write("      </Map>\n");
                }
            }

            outSpecMap.write("   </Maps>\n");

        }

        outSpecMap.write("</TrigoXML>\n");
        outSpecMap.flush();

        Writer osw = new OutputStreamWriter(new FileOutputStream(sOutputPath + File.separator + "MAPS.xml", true), "UTF-8");
        String sCorrectedPath = sSpecMapFilePath.replace(File.separator, "/");
        sCorrectedPath = sCorrectedPath.substring(sCorrectedPath.indexOf("/"));
        osw.write(sCorrectedPath + "\n");
        osw.flush();
        osw.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputEntityCSV(Writer outFile, String sOutputPath) throws IOException {
        List<String> line = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : getSourceToDestinationPaths().entrySet()) {
            String sSourcePath = entry.getKey();
            String sSourceSpec = sSourcePath.substring(0, sSourcePath.indexOf("/"));
            String sSourceAttr = sSourcePath.substring(sSourcePath.indexOf("/") + 1);
            List<String> destinationPaths = entry.getValue();
            for (String sDestinationPath : destinationPaths) {
                String sDestinationSpec = sDestinationPath.substring(0, sDestinationPath.indexOf("/"));
                String sDestinationAttr = sDestinationPath.substring(sDestinationPath.indexOf("/") + 1);
                line.add("");
                line.add(getName());
                line.add(getType());
                line.add(getSourceObject());
                line.add(getDestinationObject());
                line.add(sSourceSpec);
                line.add(sSourceAttr);
                line.add(sDestinationSpec);
                line.add(sDestinationAttr);
                outputCSV(line, outFile);
            }
        }
    }

    /**
     * Retrieve the name of this instance of a spec map.
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieve the type of this instance of a spec map.
     * @return String
     */
    public String getType() {
        return type;
    }

    /**
     * Retrieve the source object for this instance of a spec map.
     * @return String
     */
    public String getSourceObject() {
        return sourceObject;
    }

    /**
     * Retrieve the destination object for this instance of a spec map.
     * @return String
     */
    public String getDestinationObject() {
        return destinationObject;
    }

    /**
     * Retrieve the mapping from source attribute to the list of destination paths for this instance of a spec map.
     * @return {@code Map<String, List<String>>}
     */
    public Map<String, List<String>> getSourceToDestinationPaths() {
        return sourceToDestinationPaths;
    }

}
