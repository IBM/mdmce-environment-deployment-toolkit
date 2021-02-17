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
 * Processes <b>Catalogs.csv</b>.
 */
public class Catalog extends BasicEntity {

    public static final String CATALOG_NAME = "Catalog Name";
    public static final String SPEC = "Spec";
    public static final String PRIMARY_HIERARCHY = "Primary Hierarchy";
    public static final String SECONDARY_HIERARCHIES = "Secondary Hierarchies";
    public static final String INHERIT = "Inherit?";
    public static final String DISPLAY_ATTRIBUTE = "Display Attribute";
    public static final String ACG = "ACG";
    public static final String LINKS = "Links";
    public static final String LOCATIONS = "Locations";
    public static final String SCRIPTS = "Scripts";
    public static final String USER_DEFINED_ATTRIBUTES = "User Defined Attributes";//RS20210217 added to support domain attributes

    private String name;
    private String specName;
    private String primaryHierarchy;
    private List<String> secondaryHierarchies = new ArrayList<>();
    private boolean inheritance = false;
    private String displayAttribute;
    private String acg;
    private Map<String, String> linkSpecPathToDestinationCatalog = new TreeMap<>();
    private Map<String, String> linkSpecPathToDestinationAttribute = new TreeMap<>();//RS20210217 support destination display attribute
    private Map<String, Map<String, List<String>>> locationHierarchyToAttributeCollections = new TreeMap<>();
    private Map<String, String> scriptTypeToName = new TreeMap<>();
    private String userDefinedCoreAttrGroup = "";
    private Map<String, String> userDefinedAttributeNameToValue = new TreeMap<>();//RS20210217 added to support domain attributes

    private static class Singleton {
        private static final Catalog INSTANCE = new Catalog();
    }

    /**
     * Retrieve the static definition of a Catalog (ie. its columns and type information).
     * @return Catalog
     */
    public static Catalog getInstance() {
        return Catalog.Singleton.INSTANCE;
    }

    private Catalog() {
        super("CATALOG", "Catalogs");
        addColumn(COUNTRY_SPECIFIC);
        addColumn(CATALOG_NAME);
        addColumn(SPEC);
        addColumn(PRIMARY_HIERARCHY);
        addColumn(SECONDARY_HIERARCHIES);
        addColumn(INHERIT);
        addColumn(DISPLAY_ATTRIBUTE);
        addColumn(ACG);
        addColumn(LINKS);
        addColumn(LOCATIONS);
        addColumn(SCRIPTS);
        addColumn(USER_DEFINED_ATTRIBUTES);
    }

    /**
     * Construct a new instance of a Catalog using the provided field values.
     * @param <T> expected to be Catalog whenever used by this class
     * @param aFields from which to construct the Catalog
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BasicEntity> T createInstance(List<String> aFields) {

        Catalog ctg = new Catalog();

        ctg.name = getFieldValue(CATALOG_NAME, aFields);
        ctg.specName = getFieldValue(SPEC, aFields);
        ctg.primaryHierarchy = getFieldValue(PRIMARY_HIERARCHY, aFields);
        String sSecondaryHierarchies = getFieldValue(SECONDARY_HIERARCHIES, aFields);
        ctg.secondaryHierarchies.addAll(Arrays.asList(sSecondaryHierarchies.split(",")));
        ctg.secondaryHierarchies.removeAll(Arrays.asList(""));//RS20210217 Remove empty strings
        ctg.inheritance = CSVParser.checkBoolean(getFieldValue(INHERIT, aFields));
        ctg.displayAttribute = getFieldValue(DISPLAY_ATTRIBUTE, aFields);
        ctg.acg = getFieldValue(ACG, aFields);

        String sLinks = getFieldValue(LINKS, aFields);
        for (String link : sLinks.split(",")) {
            String[] aLinkTokens = link.split("\\Q|\\E");
            if (aLinkTokens.length == 2) {
                ctg.linkSpecPathToDestinationCatalog.put(aLinkTokens[0], aLinkTokens[1]);
            }else if (aLinkTokens.length == 3) {//RS20210227 Since v11 we can specify a target attribute (instead of pk), linkAttribPath|Dest Catalog|Dest attribute
                ctg.linkSpecPathToDestinationCatalog.put(aLinkTokens[0], aLinkTokens[1]);
                ctg.linkSpecPathToDestinationAttribute.put(aLinkTokens[0], aLinkTokens[2]);
            }
        }

        // HierarchyName=SecondarySpecName|Inheritance,Attribute,Collection,List
        // (and could be multiple entries like this, each on its own line within the cell)
        String sLocations = getFieldValue(LOCATIONS, aFields);
        for (String location : sLocations.split("\n")) {
            String[] aLocationTokens = location.split("\\Q=\\E");
            if (aLocationTokens.length == 2) {
                String sHierarchyName = aLocationTokens[0];
                String sSpecAndCollections = aLocationTokens[1];
                String[] aSpecTokens = sSpecAndCollections.split("\\Q|\\E");
                if (aSpecTokens.length == 2) {
                    String sSecondarySpecName = aSpecTokens[0];
                    String sCollections = aSpecTokens[1];
                    String[] aCollections = sCollections.split(",");
                    Map<String, List<String>> specToCollections = new TreeMap<>();
                    specToCollections.put(sSecondarySpecName, Arrays.asList(aCollections));
                    ctg.locationHierarchyToAttributeCollections.put(sHierarchyName, specToCollections);
                }
            }
        }

        String sScripts = getFieldValue(SCRIPTS, aFields);
        for (String script : sScripts.split(",")) {
            String[] aScriptTokens = script.split("\\Q|\\E");
            if (aScriptTokens.length == 2) {
                if (aScriptTokens[0].equals("USER_DEFINED_CORE_ATTRIBUTE_GROUP")) {
                    ctg.userDefinedCoreAttrGroup = aScriptTokens[1];
                } else {
                    ctg.scriptTypeToName.put(aScriptTokens[0], aScriptTokens[1]);
                }
            }
        }

        String sUserDefinedAttributes = getFieldValue(USER_DEFINED_ATTRIBUTES, aFields);
        for (String userDefinedAttribute : sUserDefinedAttributes.split(",")) {
            String[] aUserDefinedAttributeToken = userDefinedAttribute.split("\\Q|\\E");
            if (aUserDefinedAttributeToken.length == 2) {
                ctg.userDefinedAttributeNameToValue.put(aUserDefinedAttributeToken[0], aUserDefinedAttributeToken[1]);
            }
        }

        return (T) ctg;

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

        outFile.write("   <CATALOG>\n");
        outFile.write(getNodeXML("Name", getName()));
        outFile.write(getNodeXML("Action", "CREATE_OR_UPDATE"));
        outFile.write(getNodeXML("UsesInheritance", "" + isInheritance()));
        outFile.write(getNodeXML("Spec", getSpecName()));
        if (getAcg().equals(com.ibm.mdmce.envtoolkit.deployment.model.ACG.DEFAULT_ACG)) {
            outFile.write("      <AccessControlGroup isDefault=\"true\"/>\n");
            outFile.write("      <ACG isDefault=\"true\"/>\n");
        } else {
            outFile.write(getNodeXML("AccessControlGroup", getAcg()));
            outFile.write(getNodeXML("ACG", getAcg()));
        }
        outFile.write(getNodeXML("PrimaryCategoryTree", getPrimaryHierarchy()));

        if (getSecondaryHierarchies().isEmpty()) {
            outFile.write(getNodeXML("SecondaryCategoryTrees", ""));
        } else {
            outFile.write("      <SecondaryCategoryTrees>\n");
            for (String secondaryHierarchy : getSecondaryHierarchies()) {
                outFile.write("         <SecondaryCategoryTree>" + secondaryHierarchy + "</SecondaryCategoryTree>\n");
            }
            outFile.write("      </SecondaryCategoryTrees>\n");
        }

        outFile.write(getNodeXML("DisplayAttribute", getDisplayAttribute()));
        outFile.write(getNodeXML("AttributeGroup", getUserDefinedCoreAttrGroup()));

        Map<String, String> hmUserDefinedAttributeNameToValue = getuserDefinedAttributeNameToValue();
		if (hmUserDefinedAttributeNameToValue.size() > 0) {//RS20210217 added to support domain attributes
			outFile.write("      <UserDefinedAttributes>\n");			
            for (Map.Entry<String, String> entry : hmUserDefinedAttributeNameToValue.entrySet()) {
                String sAttributeName = entry.getKey();
                String sAttributeValue = entry.getValue();
				outFile.write("         <UserDefinedAttribute>\n");
				outFile.write("            <UserDefinedAttributeName><![CDATA[" + sAttributeName + "]]></UserDefinedAttributeName>\n");
				outFile.write("            <UserDefinedAttributeValue><![CDATA[" + sAttributeValue + "]]></UserDefinedAttributeValue>\n");
				outFile.write("         </UserDefinedAttribute>\n");
			}
			outFile.write("      </UserDefinedAttributes>\n");
		} else {
			outFile.write(getNodeXML("UserDefinedAttributes", ""));
		}	


        Map<String, String> hmLinkAttrToCtgs = getLinkSpecPathToDestinationCatalog();
        Map<String, String> hmLinkAttrToAttrs = getLinkSpecPathToDestinationAttribute();
        if (hmLinkAttrToCtgs.size() > 0) {
            outFile.write("      <LinkAttributes>\n");
        } else {
            outFile.write(getNodeXML("LinkAttributes", ""));
        }
        for (Map.Entry<String, String> entry : hmLinkAttrToCtgs.entrySet()) {
            String sLinkAttr = entry.getKey();
            String sDstCtgName = entry.getValue();
            outFile.write("         <LinkAttribute>\n");
            outFile.write("            <LinkSourceAttribute><![CDATA[" + sLinkAttr + "]]></LinkSourceAttribute>\n");
            Catalog ctgLinked = (Catalog) BasicEntityHandler.getFromCache(sDstCtgName, Catalog.class.getName(), true, false);
            Spec specCtgLinked = (Spec) BasicEntityHandler.getFromCache(ctgLinked.getSpecName(), Spec.class.getName(), true, false);    
            String sDstAttrName = (String)hmLinkAttrToAttrs.get(sLinkAttr);//RS20210217 Support target attribute
			if (sDstAttrName==null || sDstAttrName.equals("")){
                outFile.write("            <LinkDestinationAttribute><![CDATA[" + specCtgLinked.getPrimaryKeyPath() + "]]></LinkDestinationAttribute>\n");
            }else{
				outFile.write("            <LinkDestinationAttribute><![CDATA[" + sDstAttrName + "]]></LinkDestinationAttribute>\n");
			}            
            outFile.write("            <LinkDstCatalog><![CDATA[" + sDstCtgName + "]]></LinkDstCatalog>\n");
            outFile.write("         </LinkAttribute>\n");
        }
        if (hmLinkAttrToCtgs.size() > 0)
            outFile.write("      </LinkAttributes>\n");

        Map<String, String> hmScripts = getScriptTypeToName();
        for (Map.Entry<String, String> entry : hmScripts.entrySet()) {
            String sScriptType = entry.getKey();
            String sScriptName = entry.getValue();
            String sTagXML = "";
            switch (sScriptType) {
                case "PRE_SCRIPT_NAME":
                    sTagXML = "PreProcessingScript";
                    break;
                case "ENTRY_BUILD_SCRIPT":
                    sTagXML = "EntryBuildScript";
                    break;
                case "POST_SAVE_SCRIPT_NAME":
                    sTagXML = "PostSaveScript";
                    break;
                case "SCRIPT_NAME":
                    sTagXML = "PostProcessingScript";
                    break;
            }
            outFile.write("      <" + sTagXML + "><![CDATA[" + sScriptName + "]]></" + sTagXML + ">\n");
        }

        Map<String, Map<String, List<String>>> hmLocations = getLocationHierarchyToAttributeCollections();
        if (hmLocations.size() > 0) {
            outFile.write("      <LocationAttributes>\n");
        } else {
            outFile.write(getNodeXML("LocationAttributes", ""));
        }
        for (Map.Entry<String, Map<String, List<String>>> entry : hmLocations.entrySet()) {
            String sLocationKey = entry.getKey();
            Map<String, List<String>> hmSpecToCollections = entry.getValue();
            for (Map.Entry<String, List<String>> specEntry : hmSpecToCollections.entrySet()) {
                String locationSpecName = specEntry.getKey();
                List<String> collections = specEntry.getValue();
                outFile.write("         <LocationAttribute>\n");
                outFile.write("            <LocationTree><![CDATA[" + sLocationKey + "]]></LocationTree>\n");
                outFile.write("            <LocationAttrSpec><![CDATA[" + locationSpecName + "]]></LocationAttrSpec>\n");
                outFile.write("            <LocationInhAttrGroups>\n");
                for (String collection : collections) {
                    outFile.write("               <LocationInhAttrGroup><![CDATA[" + collection + "]]></LocationInhAttrGroup>\n");
                }
                outFile.write("            </LocationInhAttrGroups>\n");
                outFile.write("         </LocationAttribute>\n");
            }
        }
        if (hmLocations.size() > 0)
            outFile.write("      </LocationAttributes>\n");

        outFile.write("   </CATALOG>\n");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputEntityCSV(Writer outFile, String sOutputPath) throws IOException {

        List<String> line = new ArrayList<>();

        // TODO: Handle output of user-defined core attribute collection in CSV

        StringBuilder sbLinks = new StringBuilder();
        for (Map.Entry<String, String> entry : getLinkSpecPathToDestinationCatalog().entrySet()) {
            String sLinkAttr = entry.getKey();
            String sDestCtg = entry.getValue();
            sbLinks.append(",").append(sLinkAttr).append("|").append(sDestCtg);
            String sDestAttr = (String)getLinkSpecPathToDestinationAttribute().get(sLinkAttr);//RS20210217 Support target attribute
			if (sDestAttr !=null && ! sDestAttr.equals("")){
                sbLinks.append("|").append(sDestAttr);
            }
        }
        String sLinks = sbLinks.toString();
        if (!sLinks.equals(""))
            sLinks = sLinks.substring(1);

        StringBuilder sbLocations = new StringBuilder();
        for (Map.Entry<String, Map<String, List<String>>> entry : getLocationHierarchyToAttributeCollections().entrySet()) {
            String sLocationHierarchy = entry.getKey();
            Map<String, List<String>> hmSpecsToCollections = entry.getValue();
            for (Map.Entry<String, List<String>> specEntry : hmSpecsToCollections.entrySet()) {
                String locationSpecName = specEntry.getKey();
                List<String> collections = specEntry.getValue();
                sbLocations.append("\n").append(sLocationHierarchy).append("=").append(locationSpecName).append(String.join(",", collections));
            }
        }
        String sLocations = sbLocations.toString();
        if (!sLocations.equals(""))
            sLocations = sLocations.substring(1);

        StringBuilder sbScripts = new StringBuilder();
        for (Map.Entry<String, String> entry : getScriptTypeToName().entrySet()) {
            String sScriptType = entry.getKey();
            String sScriptName = entry.getValue();
            sbScripts.append(",").append(sScriptType).append("|").append(sScriptName);
        }
        String sScripts = sbScripts.toString();
        if (!sScripts.equals(""))
            sScripts = sScripts.substring(1);

        StringBuilder sbUserDefinedAttributes = new StringBuilder();
        for (Map.Entry<String, String> entry : getuserDefinedAttributeNameToValue().entrySet()) {
            String sUserDefinedAttributeName = entry.getKey();
            String sUserDefinedAttributeValue = entry.getValue();
            sbUserDefinedAttributes.append(",").append(sUserDefinedAttributeName).append("|").append(sUserDefinedAttributeValue);
        }
        String sUserDefinedAttributes = sbUserDefinedAttributes.toString();
        if (!sUserDefinedAttributes.equals(""))
            sUserDefinedAttributes = sUserDefinedAttributes.substring(1);

        line.add("");
        line.add(getSpecName());
        line.add(getPrimaryHierarchy());
        line.add(escapeForCSV(String.join(",", getSecondaryHierarchies())));
        line.add("" + isInheritance());
        line.add(getDisplayAttribute());
        line.add(getAcg());
        line.add(escapeForCSV(sLinks));
        line.add(escapeForCSV(sLocations));
        line.add(escapeForCSV(sScripts));
        line.add(escapeForCSV(sUserDefinedAttributes));

        outputCSV(line, outFile);

    }

    /**
     * Retrieve the name of this instance of a catalog.
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieve the name of the spec for this instance of a catalog.
     * @return String
     */
    public String getSpecName() {
        return specName;
    }

    /**
     * Retrieve the name of the primary hierarchy for this instance of a catalog.
     * @return String
     */
    public String getPrimaryHierarchy() {
        return primaryHierarchy;
    }

    /**
     * Retrieve the names of the secondary hierarchies for this instance of a catalog.
     * @return {@code List<String>}
     */
    public List<String> getSecondaryHierarchies() {
        return secondaryHierarchies;
    }

    /**
     * Indicates whether this instance of a catalog uses inheritance (true) or not (false).
     * @return boolean
     */
    public boolean isInheritance() {
        return inheritance;
    }

    /**
     * Retrieve the display attribute for this instance of a catalog.
     * @return String
     */
    public String getDisplayAttribute() {
        return displayAttribute;
    }

    /**
     * Retrieve the access control group for this instance of a catalog.
     * @return String
     */
    public String getAcg() {
        return acg;
    }

    /**
     * Retrieve a mapping from user Defined Attribute Name to it's value for this instance of a catalog.
     * @return {@code Map<String, String>}
     */
    public Map<String, String> getuserDefinedAttributeNameToValue() {
        return userDefinedAttributeNameToValue;
    }

    /**
     * Retrieve a mapping from the linking attribute spec path to the destination catalog for this instance of a
     * catalog.
     * @return {@code Map<String, String>}
     */
    public Map<String, String> getLinkSpecPathToDestinationCatalog() {
        return linkSpecPathToDestinationCatalog;
    }

    /**
     * Retrieve a mapping from the linking attribute spec path to the destination attribute for this instance of a
     * catalog.
     * @return {@code Map<String, String>}
     */
    public Map<String, String> getLinkSpecPathToDestinationAttribute() {
        return linkSpecPathToDestinationAttribute;
    }
    

    /**
     * Retrieve a mapping from the location hierarchy to its secondary specs and their attribute groups for this
     * instance of a catalog.
     * @return {@code Map<String, Map<String, List<String>>>}
     */
    public Map<String, Map<String, List<String>>> getLocationHierarchyToAttributeCollections() {
        return locationHierarchyToAttributeCollections;
    }

    /**
     * Retrieve a mapping from the script type to the script name for this instance of a catalog.
     * @return {@code Map<String, String>}
     */
    public Map<String, String> getScriptTypeToName() {
        return scriptTypeToName;
    }

    /**
     * Retrieve the user-defined core attribute group for this instance of a catalog (if any, otherwise an empty string).
     * @return String
     */
    public String getUserDefinedCoreAttrGroup() {
        return userDefinedCoreAttrGroup;
    }

}
