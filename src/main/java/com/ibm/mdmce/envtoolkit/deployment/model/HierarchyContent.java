/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment.model;

import java.util.List;

/**
 * Processes <b>CatalogContents.csv</b>.
 */
public class HierarchyContent extends ContainerContent {

    private static class Singleton {
        private static final HierarchyContent INSTANCE = new HierarchyContent();
    }

    /**
     * Retrieve the static definition of HierarchyContent (ie. its columns and type information).
     * @return HierarchyContent
     */
    public static HierarchyContent getInstance() {
        return HierarchyContent.Singleton.INSTANCE;
    }

    private HierarchyContent() {
        super("HIERARCHY_CONTENT", "CATEGORY_TREE", "HIERARCHY_CONTENT");
    }

    /**
     * Construct a new instance of Container Content using the provided field values.
     * @param <T> expected to be ContainerContent whenever used by this class
     * @param aFields from which to construct the Container Content
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BasicEntity> T createInstance(List<String> aFields) {
        HierarchyContent containerData = new HierarchyContent();
        setupInstance(containerData, aFields);
        return (T) containerData;
    }

}
