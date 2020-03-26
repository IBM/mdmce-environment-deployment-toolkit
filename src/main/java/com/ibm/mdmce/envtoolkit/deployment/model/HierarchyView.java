/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment.model;

import com.ibm.mdmce.envtoolkit.deployment.BasicEntityHandler;
import com.ibm.mdmce.envtoolkit.deployment.CSVParser;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * Marshals information from the Views.csv (for hierarchy views only) and handles transformation to relevant XML(s).
 * The expected file format is defined within the View class.
 *
 * @see View
 */
public class HierarchyView extends View {

    private static class Singleton {
        private static final HierarchyView INSTANCE = new HierarchyView();
    }

    /**
     * Retrieve the static definition of a HierarchyView (ie. its columns and type information).
     * @return HierarchyView
     */
    public static HierarchyView getInstance() {
        return HierarchyView.Singleton.INSTANCE;
    }

    private HierarchyView() {
        super("HIERARCHY_VIEW", "HIERARCHY_VIEW");
    }

    /**
     * Construct a new instance of a Hierarchy View using the provided field values.
     * @param <T> expected to be HierarchyView whenever used by this class
     * @param aFields from which to construct the Hierarchy View
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BasicEntity> T createInstance(List<String> aFields) {

        String sViewType = getFieldValue(CONTAINER_TYPE, aFields);
        if (sViewType.equals("CATEGORY_TREE")) {

            String sHierViewName = getFieldValue(VIEW_NAME, aFields);
            String sContainerName = getFieldValue(CONTAINER_NAME, aFields);
            HierarchyView view = (HierarchyView) BasicEntityHandler.getFromCache(sContainerName + "::" + sHierViewName, HierarchyView.class.getName(), false, false);
            if (view == null) {
                view = new HierarchyView();
                view.containerType = "CATEGORY_TREE";
            }
            view.viewName = sHierViewName;
            view.containerName = sContainerName;
            view.defaultView = CSVParser.checkBoolean(getFieldValue(DEFAULT, aFields));

            String sAttrCollection = getFieldValue(ATTR_COLLECTION, aFields);
            String sTabName = getFieldValue(TAB_NAME, aFields);

            boolean bViewOnly = CSVParser.checkBoolean(getFieldValue(VIEW_ONLY, aFields));
            boolean bSingleEdit = CSVParser.checkBoolean(getFieldValue(SINGLE_EDIT, aFields));
            boolean bMultiEdit = CSVParser.checkBoolean(getFieldValue(MULTI_EDIT, aFields));

            view.attributeCollections.add(sAttrCollection);
            if (bSingleEdit) {
                view.singleEdit.add(sAttrCollection);
                captureTabs(sTabName, sAttrCollection, view.singleEditTabOrder, view.singleEditTabs);
            }
            if (bMultiEdit) {
                view.multiEdit.add(sAttrCollection);
                captureTabs(sTabName, sAttrCollection, view.multiEditTabOrder, view.multiEditTabs);
            }
            if (bViewOnly)
                view.viewOnly.add(sAttrCollection);

            return (T) view;

        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputEntityXML(BasicEntityHandler handler, Writer outFile, String sOutputPath, String sCompanyCode) throws IOException {
        outFile.write("   <ContainerView name=\"" + getViewName() + "\">\n");
        outFile.write(getNodeXML("ContainerName", getContainerName()));
        outFile.write(getNodeXML("Action", "CREATE_OR_UPDATE"));
        outFile.write(getNodeXML("Default", "" + isDefaultView()));
        if (!singleEdit.isEmpty()) {
            outputViewComponent("CategoryEdit", singleEdit, singleEditTabOrder, singleEditTabs, true, outFile);
        }
        if (!multiEdit.isEmpty()) {
            outputViewComponent("CategoryBulkEdit", multiEdit, multiEditTabOrder, multiEditTabs, true, outFile);
        }
        outFile.write("   </ContainerView>\n");
    }

}
