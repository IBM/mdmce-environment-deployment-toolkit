/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment;

import com.ibm.mdmce.envtoolkit.deployment.model.TemplateParameters;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Marshals information from the TemplateParameters.csv in order to build a list of the templating variables.
 *
 * @see TemplateParameters
 */
public class TemplateParameterMarshaller {

    private TemplateParameters tm = new TemplateParameters();

    /**
     * Parse a new set of template parameters.
     * @param sInputFilePath the path to the CSV file containing the parameters
     * @param sEncoding the encoding of the CSV file
     */
    public TemplateParameterMarshaller(String sInputFilePath, String sEncoding) {

        EnvironmentHandler.logger.info("Reading input from: " + sInputFilePath);

        try {
            // Read in the entities first...
            CSVParser readerCSV = new CSVParser(sInputFilePath, sEncoding);
            // Get the variable identifiers from the first line...
            List<String> aTokens = readerCSV.splitLine();
            tm = new TemplateParameters();
            tm.setTopLevelVarname(aTokens.get(0));
            for (int i = 1; i < aTokens.size(); i++) {
                tm.addTopLevelVar(aTokens.get(i));
            }
            List<String> aTemplateVars = tm.getTopLevelVars();
            aTokens = readerCSV.splitLine();
            while (aTokens != null && !aTokens.isEmpty()) {
                if (tm.getSecondLevelVarname().equals(""))
                    tm.setSecondLevelVarname(aTokens.get(0));
                for (int j = 1; j < aTokens.size(); j++) {
                    String sTempVar = aTemplateVars.get(j-1);
                    tm.addSecondLevelToTopLevel(sTempVar, aTokens.get(j));
                }
                aTokens = readerCSV.splitLine();
            }
        }
        catch (FileNotFoundException errNoFile) {
            EnvironmentHandler.logger.severe("Error: File not found! " + errNoFile.getMessage());
        }
        catch (IOException errIO) {
            EnvironmentHandler.logger.severe("Error: IO problem! " + errIO.getMessage());
        }

    }

    /**
     * Retrieve the template parameters that were parsed.
     * @return TemplateParameters
     */
    public TemplateParameters getTemplateParameters() {
        return tm;
    }

}
