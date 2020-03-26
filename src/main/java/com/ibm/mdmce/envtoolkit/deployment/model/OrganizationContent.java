/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment.model;

import java.util.List;

/**
 * Processes <b>OrganizationContents.csv</b>.
 */
public class OrganizationContent extends ContainerContent {

    private static class Singleton {
        private static final OrganizationContent INSTANCE = new OrganizationContent();
    }

    /**
     * Retrieve the static definition of an AccessControlGroup (ie. its columns and type information).
     * @return AccessControlGroup
     */
    public static OrganizationContent getInstance() {
        return OrganizationContent.Singleton.INSTANCE;
    }

    private OrganizationContent() {
        super("ORG_HIERARCHY_CONTENT", "ORGANIZATION_TREE", "ORG_HIERARCHY_CONTENT");
    }

    /**
     * Construct a new instance of Container Content using the provided field values.
     * @param <T> expected to be ContainerContent whenever used by this class
     * @param aFields from which to construct the Container Content
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BasicEntity> T createInstance(List<String> aFields) {
        OrganizationContent containerData = new OrganizationContent();
        setupInstance(containerData, aFields);
        return (T) containerData;
    }

}
