/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment.model;

import java.util.List;

/**
 * Processes <b>CatalogContents.csv</b>.
 */
public class CatalogContent extends ContainerContent {

    private static class Singleton {
        private static final CatalogContent INSTANCE = new CatalogContent();
    }

    /**
     * Retrieve the static definition of an AccessControlGroup (ie. its columns and type information).
     * @return AccessControlGroup
     */
    public static CatalogContent getInstance() {
        return CatalogContent.Singleton.INSTANCE;
    }

    private CatalogContent() {
        super("CATALOG_CONTENT", "CATALOG", "CATALOG_CONTENT");
    }

    /**
     * Construct a new instance of Container Content using the provided field values.
     * @param <T> expected to be ContainerContent whenever used by this class
     * @param aFields from which to construct the Container Content
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BasicEntity> T createInstance(List<String> aFields) {
        CatalogContent containerData = new CatalogContent();
        setupInstance(containerData, aFields);
        return (T) containerData;
    }

}
