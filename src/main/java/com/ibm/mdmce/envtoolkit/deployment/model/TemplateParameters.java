/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment.model;

import java.util.*;

/**
 * Holds the parameters used to apply variability to a defined structure to create multiple instances from a single
 * template-like definition.
 */
public class TemplateParameters {

    private String topLevelVarname;
    private String secondLevelVarname;

    private List<String> topLevelVars;
    private Map<String, List<String>> topLevelToSecondLevel;

    /**
     * Construct a new set of templating parameters.
     */
    public TemplateParameters() {
        topLevelVarname = "";
        secondLevelVarname = "";
        topLevelVars = new ArrayList<>();
        topLevelToSecondLevel = new TreeMap<>();
    }

    /**
     * Retrieve the top-level variable name for the templating.
     * @return String
     */
    public String getTopLevelVarname() {
        return topLevelVarname;
    }

    /**
     * Set the top-level variable name for the templating.
     * @param topLevelVarname top-level variable name
     */
    public void setTopLevelVarname(String topLevelVarname) {
        this.topLevelVarname = topLevelVarname;
    }

    /**
     * Retrieve the secondary-level variable name for the templating.
     * @return String
     */
    public String getSecondLevelVarname() {
        return secondLevelVarname;
    }

    /**
     * Set the secondary-level variable name for the templating.
     * @param secondLevelVarname secondary-level variable name
     */
    public void setSecondLevelVarname(String secondLevelVarname) {
        this.secondLevelVarname = secondLevelVarname;
    }

    /**
     * Retrieve the top-level variable values.
     * @return {@code List<String>}
     */
    public List<String> getTopLevelVars() {
        return topLevelVars;
    }

    /**
     * Add a top-level variable value to the list of values for the top-level variable.
     * @param topLevelVar value to add
     */
    public void addTopLevelVar(String topLevelVar) {
        this.topLevelVars.add(topLevelVar);
    }

    /**
     * Retrieve the list of secondary-level variable values for the provided top-level variable value.
     * @param top value of the top-level variable for which to retrieve secondary-level values
     * @return {@code List<String>}
     */
    public List<String> getSecondLevelFromTopLevel(String top) {
        return topLevelToSecondLevel.getOrDefault(top, Collections.emptyList());
    }

    /**
     * Add a secondary-level value for the provided top-level value.
     * @param top value of the top-level variable for which to add a secondary-level value
     * @param second value of the secondary-level to add
     */
    public void addSecondLevelToTopLevel(String top, String second) {
        if (!topLevelToSecondLevel.containsKey(top)) {
            topLevelToSecondLevel.put(top, new ArrayList<>());
        }
        topLevelToSecondLevel.get(top).add(second);
    }

}
