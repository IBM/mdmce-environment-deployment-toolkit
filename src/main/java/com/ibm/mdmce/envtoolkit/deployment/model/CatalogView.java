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
 * Marshals information from the Views.csv (for catalog views only) and handles transformation to relevant XML(s).
 * The expected file format is defined within the View class.
 *
 * @see View
 */
public class CatalogView extends View {

    private static class Singleton {
        private static final CatalogView INSTANCE = new CatalogView();
    }

    /**
     * Retrieve the static definition of a CatalogView (ie. its columns and type information).
     * @return CatalogView
     */
    public static CatalogView getInstance() {
        return CatalogView.Singleton.INSTANCE;
    }

    private CatalogView() {
        super("CATALOG_VIEW", "CATALOG_VIEW");
    }

    /**
     * Construct a new instance of a Catalog View using the provided field values.
     * @param <T> expected to be CatalogView whenever used by this class
     * @param aFields from which to construct the Catalog View
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BasicEntity> T createInstance(List<String> aFields) {

        String sViewType = getFieldValue(CONTAINER_TYPE, aFields);
        if (sViewType.equals("CATALOG")) {

            String sCtgViewName = getFieldValue(VIEW_NAME, aFields);
            String sContainerName = getFieldValue(CONTAINER_NAME, aFields);
            CatalogView view = (CatalogView) BasicEntityHandler.getFromCache(sContainerName + "::" + sCtgViewName, CatalogView.class.getName(), false, false);
            if (view == null) {
                view = new CatalogView();
                view.containerType = "CATALOG";
            }
            view.viewName = sCtgViewName;
            view.containerName = sContainerName;
            view.defaultView = CSVParser.checkBoolean(getFieldValue(DEFAULT, aFields));

            String sAttrCollection = getFieldValue(ATTR_COLLECTION, aFields);
            String sTabName = getFieldValue(TAB_NAME, aFields);

            boolean bViewOnly = CSVParser.checkBoolean(getFieldValue(VIEW_ONLY, aFields));
            boolean bSingleEdit = CSVParser.checkBoolean(getFieldValue(SINGLE_EDIT, aFields));
            boolean bMultiEdit = CSVParser.checkBoolean(getFieldValue(MULTI_EDIT, aFields));
            boolean bItemList = CSVParser.checkBoolean(getFieldValue(ITEM_LIST, aFields));
            boolean bItemPopup = CSVParser.checkBoolean(getFieldValue(ITEM_POPUP, aFields));
            boolean bLocation = CSVParser.checkBoolean(getFieldValue(LOCATION, aFields));

            if (!view.attributeCollections.contains(sAttrCollection))
                view.attributeCollections.add(sAttrCollection);
            if (bSingleEdit) {
                if (!view.singleEdit.contains(sAttrCollection))
                    view.singleEdit.add(sAttrCollection);
                captureTabs(sTabName, sAttrCollection, view.singleEditTabOrder, view.singleEditTabs);
            }
            if (bMultiEdit) {
                if (!view.multiEdit.contains(sAttrCollection))
                    view.multiEdit.add(sAttrCollection);
                captureTabs(sTabName, sAttrCollection, view.multiEditTabOrder, view.multiEditTabs);
            }
            if (bItemList)
                if (!view.itemList.contains(sAttrCollection))
                    view.itemList.add(sAttrCollection);
            if (bItemPopup)
                if (!view.itemPopup.contains(sAttrCollection))
                    view.itemPopup.add(sAttrCollection);
            if (bLocation) {
                if (!view.location.contains(sAttrCollection))
                    view.location.add(sAttrCollection);
                captureTabs(sTabName, sAttrCollection, view.locationTabOrder, view.locationTabs);
            }
            if (bViewOnly && !view.viewOnly.contains(sAttrCollection))
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
            outputViewComponent("EditItem", singleEdit, singleEditTabOrder, singleEditTabs, true, outFile);
        }
        if (!multiEdit.isEmpty()) {
            outputViewComponent("BulkEdit", multiEdit, multiEditTabOrder, multiEditTabs, true, outFile);
        }
        if (!itemList.isEmpty()) {
            outputViewComponent("ItemList", itemList, singleEditTabOrder, singleEditTabs, false, outFile);
        }
        if (!itemPopup.isEmpty()) {
            outputViewComponent("ItemPopup", itemPopup, singleEditTabOrder, singleEditTabs, false, outFile);
        }
        if (!location.isEmpty()) {
            outputViewComponent("ItemLocation", location, locationTabOrder, locationTabs, false, outFile);
        }

        outFile.write("   </ContainerView>\n");
    }

}
