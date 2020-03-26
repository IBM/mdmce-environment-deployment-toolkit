/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment;

import com.ibm.mdmce.envtoolkit.deployment.model.*;

import java.util.List;

/**
 * Marshals information from Views.csv and handles transformation to relevant XML and CSV file(s).
 * The expected file format is defined within the View class.
 *
 * @see View
 */
public abstract class ViewHandler extends BasicEntityHandler {

    /**
     * Construct a new handler for the provided parameters.
     * @param entity the entity for which to construct a handler
     * @param csvFilePath the path to the CSV file that this handler processes
     * @param xmlFilePath the path to the XML file that this handler processes
     * @param sInputFilePath the location of the CSV file to translate
     * @param version the version of the software for which to translate
     * @param tp the template parameters to apply to the contents of the CSV file
     * @param sEncoding the encoding of the CSV file
     * @param <T> the type of entity for which to construct a handler
     */
    public <T extends BasicEntity> ViewHandler(T entity,
                                               String csvFilePath,
                                               String xmlFilePath,
                                               String sInputFilePath,
                                               String version,
                                               TemplateParameters tp,
                                               String sEncoding) {
        super(entity, csvFilePath, xmlFilePath, sInputFilePath, version, tp, sEncoding);
        initialize(sInputFilePath, sEncoding, tp);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected <T extends BasicEntity> void addLineToCache(List<List<String>> alReplacedTokens) {
        for (List<String> aReplacedTokens : alReplacedTokens) {
            View instance = entity.createInstance(aReplacedTokens);
            if (instance != null) {
                addToCache(instance.getUniqueId(), instance);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validate(BasicEntity oEntity) {
        boolean bValid = true;
        View view = (View) oEntity;
        if (view.getContainerType().equals("CATALOG"))
            bValid = validateExists(view.getContainerName(), Catalog.class.getName(), view.getViewName()) && bValid;
        else
            bValid = validateExists(view.getContainerName(), Hierarchy.class.getName(), view.getViewName()) && bValid;
        for (int i = 0; i < view.getAttributeCollections().size(); i++) {
            bValid = validateExists(view.getAttributeCollections().get(i), AttrCollection.class.getName(), view.getViewName()) && bValid;
        }
        return bValid;
    }

}
