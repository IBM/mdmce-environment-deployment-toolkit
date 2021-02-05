/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment.model;

import com.ibm.mdmce.envtoolkit.deployment.BasicEntityHandler;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * Processes <b>RolesToACGs.csv</b>.
 */
public class RoleToACG extends BasicEntity {

    private String role = "";
    private HashMap<String, List<String>> acgMappings = new HashMap<>();

    private static class Singleton {
        private static final RoleToACG INSTANCE = new RoleToACG();
    }

    /**
     * Retrieve the static definition of a RoleMapping (ie. its columns and type information).
     * @return RoleMapping
     */
    public static RoleToACG getInstance() {
        return RoleToACG.Singleton.INSTANCE;
    }

    private RoleToACG() {
        super("UNUSED", "Roles");
        addColumn("Access Controls (Role,ACG)");
    }

    public RoleToACG(String role) {
        this.role = role;
    }

    /**
     * Construct a new instance of a RoleMapping using the provided field values.
     * @param <T> expected to be RoleMapping whenever used by this class
     * @param aFields from which to construct the Role
     */
    @Override
    //@SuppressWarnings("unchecked")
    public <T extends BasicEntity> T createInstance(List<String> aFields) {
        // do nothing...
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUniqueId() {
        // do nothing...
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputEntityXML(BasicEntityHandler handler, Writer outFile, String sOutputPath, String sCompanyCode) throws IOException {
        // do nothing...
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputEntityCSV(Writer outFile, String sOutputPath) throws IOException {
        List<String> line = new ArrayList<>();
        line.add("");
        outputCSV(line, outFile);
    }

    /**
     * Retrieve the role for this instance of a mapping.
     * @return String
     */
    public String getRole() {
        return role;
    }

    /**
     * Retrieve a mapping from ACG name to the list of privileges that are enabled for this instance of a mapping.
     * @return {@code Map<String, List<String>>}
     */
    public Map<String, List<String>> getAcgMappings() {
        return acgMappings;
    }

}
