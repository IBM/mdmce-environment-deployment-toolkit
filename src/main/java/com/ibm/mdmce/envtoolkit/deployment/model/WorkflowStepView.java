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
 * Marshals information from the Views.csv (for workflow step views only) and handles transformation to relevant XML(s).
 * The expected file format is defined within the View class.
 *
 * @see View
 */
public class WorkflowStepView extends View {

    private Map<String, String> attributeCollectionToTab = new HashMap<>();
    private Map<String, List<String>> tabToAttributeCollections = new HashMap<>();
    private List<String> tabOrder = new ArrayList<>();

    private static class Singleton {
        private static final WorkflowStepView INSTANCE = new WorkflowStepView();
    }

    /**
     * Retrieve the static definition of a WorkflowStepView (ie. its columns and type information).
     * @return WorkflowStepView
     */
    public static WorkflowStepView getInstance() {
        return WorkflowStepView.Singleton.INSTANCE;
    }

    private WorkflowStepView() {
        super("UNUSED", "WflStepView");
    }

    /**
     * Construct a new instance of a Workflow Step View using the provided field values.
     * @param <T> expected to be WorkflowStepView whenever used by this class
     * @param aFields from which to construct the Workflow Step View
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BasicEntity> T createInstance(List<String> aFields) {

        String sViewType = getFieldValue(CONTAINER_TYPE, aFields);
        if (sViewType.equals("WORKFLOW")) {

            String sStepPath = getFieldValue(VIEW_NAME, aFields);
            String sContainerName = getFieldValue(CONTAINER_NAME, aFields);
            WorkflowStepView view = (WorkflowStepView) BasicEntityHandler.getFromCache(sContainerName + "::" + sStepPath, WorkflowStepView.class.getName(), false, false);
            if (view == null) {
                view = new WorkflowStepView();
                view.containerType = "WORKFLOW";
            }
            view.viewName = sStepPath;
            view.containerName = sContainerName;

            String sAttrCollection = getFieldValue(ATTR_COLLECTION, aFields);
            String sTabName = getFieldValue(TAB_NAME, aFields);

            boolean bViewOnly = CSVParser.checkBoolean(getFieldValue(VIEW_ONLY, aFields));
            boolean bSingleEdit = CSVParser.checkBoolean(getFieldValue(SINGLE_EDIT, aFields));
            boolean bMultiEdit = CSVParser.checkBoolean(getFieldValue(MULTI_EDIT, aFields));
            boolean bItemPopup = CSVParser.checkBoolean(getFieldValue(ITEM_POPUP, aFields));

            if (!view.attributeCollections.contains(sAttrCollection))
                view.attributeCollections.add(sAttrCollection);
            if (bSingleEdit)
                if (!view.singleEdit.contains(sAttrCollection))
                    view.singleEdit.add(sAttrCollection);
            if (bMultiEdit)
                if (!view.multiEdit.contains(sAttrCollection))
                    view.multiEdit.add(sAttrCollection);
            if (bItemPopup)
                if (!view.itemPopup.contains(sAttrCollection))
                    view.itemPopup.add(sAttrCollection);
            if (bViewOnly && !view.viewOnly.contains(sAttrCollection))
                view.viewOnly.add(sAttrCollection);

            if (!sTabName.equals("")) {
                view.attributeCollectionToTab.put(sAttrCollection, sTabName);
                List<String> alAttrsInTab = view.tabToAttributeCollections.getOrDefault(sTabName, null);
                if (alAttrsInTab == null) {
                    alAttrsInTab = new ArrayList<>();
                    view.tabOrder.add(sTabName);
                }
                if (!alAttrsInTab.contains(sAttrCollection))
                    alAttrsInTab.add(sAttrCollection);
                view.tabToAttributeCollections.put(sTabName, alAttrsInTab);
            }

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
        // Stub function
    }

    private boolean isAttrCollectionOnViewComponent(String sAttrCollection, String sViewComponent) {
        boolean bValid = false;
        if (sViewComponent.equals("SINGLE_EDIT"))
            bValid = singleEdit.contains(sAttrCollection);
        else if (sViewComponent.equals("MULTI_EDIT"))
            bValid = multiEdit.contains(sAttrCollection);
        return bValid;
    }

    private void outputTabsForView(Writer outFile, String sViewComponent) throws IOException {

        if (!tabToAttributeCollections.isEmpty()) {
            outFile.write("               <TabGroup>\n");
            int iNumSkipped = 0;
            for (int j = 0; j < tabOrder.size(); j++) {

                int iIndex = (j - iNumSkipped);
                String sTabName = tabOrder.get(j);

                // We need to confirm that the view type actually contains these
                // attribute collections for the tab before outputting them
                List<String> alAttrsForTab = new ArrayList<>();
                List<String> aAttrCols = tabToAttributeCollections.get(sTabName);
                for (String sAttrCol : aAttrCols) {
                    if (isAttrCollectionOnViewComponent(sAttrCol, sViewComponent))
                        alAttrsForTab.add(sAttrCol);
                }
                if (alAttrsForTab.size() > 0) {
                    outFile.write("                  <Tab name=\"" + sTabName + "\">\n");
                    outFile.write("                  <TabOrder>" + iIndex + "</TabOrder>\n");
                    for (String sAttrCol : alAttrsForTab) {
                        outFile.write("                     <AttributeCollection><![CDATA[" + sAttrCol + "]]></AttributeCollection>\n");
                    }
                    outFile.write("                  </Tab>\n");
                } else {
                    iNumSkipped++;
                }

            }
            outFile.write("               </TabGroup>\n");
        }

    }

    /**
     * Output the view components for this workflow step view.
     * @param outFile into which to write
     * @throws IOException on any error writing
     */
    public void outputViewComponent(Writer outFile) throws IOException {

        if (!tabToAttributeCollections.isEmpty() && !tabOrder.isEmpty()) {

            if (!singleEdit.isEmpty()) {
                outFile.write("            <ContainerView>\n");
                outFile.write("               <ContainerViewType>ITEM_EDIT:" + 0 + "</ContainerViewType>\n"); // ??? where does this number come from?
                outputTabsForView(outFile, "SINGLE_EDIT");
                outFile.write("            </ContainerView>\n");
            }

            if (!multiEdit.isEmpty()) {
                outFile.write("            <ContainerView>\n");
                outFile.write("               <ContainerViewType>BULK_EDIT:" + 0 + "</ContainerViewType>\n"); // ??? where does this number come from?
                outputTabsForView(outFile, "MULTI_EDIT");
                outFile.write("            </ContainerView>\n");
            }

        }

    }

    private void outputEditableAttrCols(List<String> alAttrCollections, Set<String> includeAttrCols, Writer outFile) throws IOException {
        for (String sAttrCol : alAttrCollections) {
            if (includeAttrCols.contains(sAttrCol) && !viewOnly.contains(sAttrCol)) {
                outFile.write("               <AttributeCollection><![CDATA[" + sAttrCol + "]]></AttributeCollection>\n");
            }
        }
    }

    private void outputViewableAttrCols(List<String> alAttrCollections, Set<String> includeAttrCols, Writer outFile) throws IOException {
        for (String sAttrCol : alAttrCollections) {
            if (includeAttrCols.contains(sAttrCol) && viewOnly.contains(sAttrCol)) {
                outFile.write("               <AttributeCollection><![CDATA[" + sAttrCol + "]]></AttributeCollection>\n");
            }
        }
    }

    /**
     * Output the attribute collections as XML for this workflow step view.
     * @param outFile into which to write
     * @throws IOException on any error writing
     */
    public void outputAttrColXML(Writer outFile) throws IOException {

        outFile.write("         <EditableAttributesCollections>\n");
        outFile.write("            <ItemEdit>\n");
        outputEditableAttrCols(getAttributeCollections(), singleEdit, outFile);
        outFile.write("            </ItemEdit>\n");
        outFile.write("            <BulkEdit>\n");
        outputEditableAttrCols(getAttributeCollections(), multiEdit, outFile);
        outFile.write("            </BulkEdit>\n");
        outFile.write("            <ItemPopup>\n");
        outputEditableAttrCols(getAttributeCollections(), itemPopup, outFile);
        outFile.write("            </ItemPopup>\n");
        outFile.write("         </EditableAttributesCollections>\n");

        outFile.write("         <ViewableAttributesCollections>\n");
        outFile.write("            <ItemEdit>\n");
        outputViewableAttrCols(getAttributeCollections(), singleEdit, outFile);
        outFile.write("            </ItemEdit>\n");
        outFile.write("            <BulkEdit>\n");
        outputViewableAttrCols(getAttributeCollections(), multiEdit, outFile);
        outFile.write("            </BulkEdit>\n");
        outFile.write("            <ItemPopup>\n");
        outputViewableAttrCols(getAttributeCollections(), itemPopup, outFile);
        outFile.write("            </ItemPopup>\n");
        outFile.write("         </ViewableAttributesCollections>\n");

    }

    /**
     * Retrieve the step path for this instance of a workflow step view.
     * @return String
     */
    public String getStepPath() {
        return getViewName();
    }

}
