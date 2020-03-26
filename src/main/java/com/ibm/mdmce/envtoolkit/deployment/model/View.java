/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment.model;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * Processes <b>Views.csv</b>.
 */
public abstract class View extends BasicEntity {

    public static final String CONTAINER_NAME = "Container Name";
    public static final String CONTAINER_TYPE = "Container Type";
    public static final String VIEW_NAME = "View name / Step Path";
    public static final String ATTR_COLLECTION = "Attribute Collection";
    public static final String TAB_NAME = "Tab name";
    public static final String VIEW_ONLY = "View-only?";
    public static final String SINGLE_EDIT = "Single Edit?";
    public static final String MULTI_EDIT = "Multi-Edit?";
    public static final String ITEM_LIST = "Item List?";
    public static final String ITEM_POPUP = "Item-Popup?";
    public static final String LOCATION = "Location?";
    public static final String DEFAULT = "Default?";

    protected String containerName;
    protected String containerType;
    protected String viewName;
    protected String tabName;
    protected boolean defaultView = false;

    protected List<String> attributeCollections = new ArrayList<>();
    protected List<String> singleEditTabOrder = new ArrayList<>();
    protected List<String> multiEditTabOrder = new ArrayList<>();
    protected List<String> locationTabOrder = new ArrayList<>();

    protected Map<String, List<String>> singleEditTabs = new HashMap<>();
    protected Map<String, List<String>> multiEditTabs = new HashMap<>();
    protected Map<String, List<String>> locationTabs = new HashMap<>();

    protected Set<String> viewOnly = new TreeSet<>();
    protected Set<String> singleEdit = new TreeSet<>();
    protected Set<String> multiEdit = new TreeSet<>();
    protected Set<String> itemList = new TreeSet<>();
    protected Set<String> itemPopup = new TreeSet<>();
    protected Set<String> location = new TreeSet<>();

    /**
     * Construct a new View using the specified parameters.
     * @param objectType type of view object
     * @param rootElement root XML element for the view object
     */
    protected View(String objectType, String rootElement) {
        super(objectType, rootElement);
        addColumn(COUNTRY_SPECIFIC);
        addColumn(CONTAINER_NAME);
        addColumn(CONTAINER_TYPE);
        addColumn(VIEW_NAME);
        addColumn(ATTR_COLLECTION);
        addColumn(TAB_NAME);
        addColumn(VIEW_ONLY);
        addColumn(SINGLE_EDIT);
        addColumn(MULTI_EDIT);
        addColumn(ITEM_LIST);
        addColumn(ITEM_POPUP);
        addColumn(LOCATION);
        addColumn(DEFAULT);
    }

    /**
     * Capture the tab information for the view.
     * @param sTabName the name of the tab
     * @param sAttrCollection the name of the attribute collection
     * @param tabOrder the list of tabs, in order, to which to append
     * @param tabMap the map of from tab name to the attribute collections within that tab
     */
    protected void captureTabs(String sTabName, String sAttrCollection, List<String> tabOrder, Map<String, List<String>> tabMap) {
        if (!sTabName.equals("")) {
            List<String> existingAttrsInTab = tabMap.getOrDefault(sTabName, null);
            if (existingAttrsInTab == null) {
                existingAttrsInTab = new ArrayList<>();
                tabOrder.add(sTabName);
            }
            existingAttrsInTab.add(sAttrCollection);
            tabMap.put(sTabName, existingAttrsInTab);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUniqueId() {
        return getContainerName() + "::" + getViewName();
    }

    /**
     * Output the XML for a component of the view using the specified parameters.
     * @param sComponentName the name of the component
     * @param attrCols the attribute collections to include in the component
     * @param alOrderedTabNames the ordered list of tab names for the component
     * @param hmTabs the mapping from tab name to the list of attribute collections for the component
     * @param bEditableComponent whether the component is editable (true) or not (false)
     * @param outFile into which to write
     * @throws IOException on any error writing
     */
    protected void outputViewComponent(String sComponentName, Set<String> attrCols, List<String> alOrderedTabNames, Map<String, List<String>> hmTabs, boolean bEditableComponent, Writer outFile) throws IOException {

        outFile.write("      <" + sComponentName + ">\n");

        for (String sAttrCol : attrCols) {
            if (bEditableComponent) {
                outFile.write("         <AttributeCollection name=\"" + sAttrCol + "\">\n");
                if (viewOnly.contains(sAttrCol)) {
                    outFile.write("            <View>true</View>\n");
                    outFile.write("            <Edit>false</Edit>\n");
                } else {
                    outFile.write("            <View>false</View>\n");
                    outFile.write("            <Edit>true</Edit>\n");
                }
                outFile.write("         </AttributeCollection>\n");
            } else {
                outFile.write("         <AttributeCollection><![CDATA[" + sAttrCol + "]]></AttributeCollection>\n");
            }
        }

        if (hmTabs != null && !hmTabs.isEmpty()
                && (sComponentName.equals("EditItem") || sComponentName.equals("BulkEdit") || sComponentName.equals("ItemLocation") || sComponentName.equals("CategoryEdit") || sComponentName.equals("CategoryBulkEdit"))
                && alOrderedTabNames.size() > 0) {
            outFile.write("         <TabGroup>\n");
            for (String sTabName : alOrderedTabNames) {
                outFile.write("            <Tab name=\"" + sTabName + "\">\n");
                List<String> aAttrCols = hmTabs.get(sTabName);
                for (String sAttrCol : aAttrCols) {
                    outFile.write("               <AttributeCollection><![CDATA[" + sAttrCol + "]]></AttributeCollection>\n");
                }
                outFile.write("            </Tab>\n");
            }
            outFile.write("         </TabGroup>\n");
        }

        outFile.write("      </" + sComponentName + ">\n");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputEntityCSV(Writer outFile, String sOutputPath) throws IOException {

        List<String> line = new ArrayList<>();

        line.add("");
        // TODO: this will not actually work as all attribute collections are output on the same line and they
        //  need to be separated across multiple lines (and also include tab names)
        for (String sAttrColName : getAttributeCollections()) {
            line.add(getContainerName());
            line.add(getContainerType());
            line.add(getViewName());
            line.add(sAttrColName);
            line.add("");
            line.add("" + viewOnly.contains(sAttrColName));
            line.add("" + singleEdit.contains(sAttrColName));
            line.add("" + multiEdit.contains(sAttrColName));
            line.add("" + itemList.contains(sAttrColName));
            line.add("" + itemPopup.contains(sAttrColName));
            line.add("" + location.contains(sAttrColName));
        }

        outputCSV(line, outFile);

    }

    /**
     * Retrieve the name of the container of this instance of a catalog view.
     * @return String
     */
    public String getContainerName() {
        return containerName;
    }

    /**
     * Retrieve the type of container for this instance of a catalog view.
     * @return String
     */
    public String getContainerType() {
        return containerType;
    }

    /**
     * Retrieve the name of this instance of a catalog view.
     * @return String
     */
    public String getViewName() {
        return viewName;
    }

    /**
     * Indicates whether this is the default view (true) or not (false) for this instance of a catalog view.
     * @return boolean
     */
    public boolean isDefaultView() {
        return defaultView;
    }

    /**
     * Retrieve the list of attribute collections for this instance of a catalog view.
     * @return {@code List<String>}
     */
    public List<String> getAttributeCollections() {
        return attributeCollections;
    }

}
