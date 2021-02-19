/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment;

import com.ibm.mdmce.envtoolkit.deployment.model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Generic abstract class that defines the parameters every entity handler must define in order to work with the deployment tool.
 * Also, provides various static helper functions for parsing data / caching information / comparing things from the CSV files.
 */
public abstract class BasicEntityHandler {

    //public static PrintWriter out = EnvironmentHandler.out;
    //public static PrintWriter err = EnvironmentHandler.err;

    private static final Pattern NAMING_CHAR_WHITELIST = Pattern.compile("[^a-zA-Z0-9_.]");

    protected String csvFilePath;
    protected String xmlFilePath;
    protected BasicEntity entity;
    protected String version;
    protected boolean federated = true;

    protected List<String> alFileListXML;

    protected List<String> alOrderedEntityNames;

    /**
     * Construct a new handler.
     */
    protected BasicEntityHandler() {
        alFileListXML = new ArrayList<>();
        alOrderedEntityNames = new ArrayList<>();
        //out = EnvironmentHandler.out;
        //err = EnvironmentHandler.err;
    }

    /**
     * Construct a new handler for the provided entity.
     * @param entity the entity for which to construct a handler
     * @param <T> the type of the entity for which to construct a handler
     */
    protected <T extends BasicEntity> BasicEntityHandler(T entity) {
        this();
        this.entity = entity;
    }

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
    public <T extends BasicEntity> BasicEntityHandler(T entity,
                                                      String csvFilePath,
                                                      String xmlFilePath,
                                                      String sInputFilePath,
                                                      String version,
                                                      TemplateParameters tp,
                                                      String sEncoding) {

        this(entity);
        this.csvFilePath = csvFilePath;
        this.xmlFilePath = xmlFilePath;
        this.version = version;

    }

    /**
     * Initialize this handler using the specified parameters.
     * @param sInputFilePath the location of the CSV file to process
     * @param sEncoding the encoding of the CSV file
     * @param tp the template parameters to apply to the contents of the CSV file
     */
    protected void initialize(String sInputFilePath, String sEncoding, TemplateParameters tp) {
        sInputFilePath = sInputFilePath + File.separator + csvFilePath;
        EnvironmentHandler.logger.info("Reading input from: " + sInputFilePath);
        try {
            // Read in the entities first...
            CSVParser readerCSV = new CSVParser(sInputFilePath, sEncoding, entity.getColumnCount());
            List<String> aTokens = readerCSV.splitLine(); // throw away the first line...
            aTokens = readerCSV.splitLine();
            if (aTokens == null || aTokens.isEmpty())
                federated = false;
            while (aTokens != null && !aTokens.isEmpty()) {
                List<List<String>> alReplacedTokens = replaceTemplateParameters(aTokens, tp, entity.getIndexOfColumn(ACG.COUNTRY_SPECIFIC));
                addLineToCache(alReplacedTokens);
                aTokens = readerCSV.splitLine();
            }
        } catch (FileNotFoundException errNoFile) {
            EnvironmentHandler.logger.severe("Error: File not found! " + errNoFile.getMessage());
            federated = false;
        } catch (IOException errIO) {
            EnvironmentHandler.logger.severe("Error: IO problem! " + errIO.getMessage());
        }
    }

    /**
     * Add the provided line, with its tokens already replaced, to the global cache.
     * @param alReplacedTokens the line of input, with tokens already replaced
     * @param <T> the type of entity being processed
     */
    protected <T extends BasicEntity> void addLineToCache(List<List<String>> alReplacedTokens) {
        for (List<String> aReplacedTokens : alReplacedTokens) {
            T instance = entity.createInstance(aReplacedTokens);
            addToCache(instance.getUniqueId(), instance);
        }
    }

    /**
     * Add the provided entity to the global cache.
     * @param sName of the entity
     * @param oObject the entity
     */
    public void addToCache(String sName, BasicEntity oObject) {
        EnvironmentHandler.addEntityToCache(sName, oObject);
        addEntityName(sName);
    }

    /**
     * Add the provided entity into the global cache, using the specified type.
     * @param sName the key by which to refer to the entity
     * @param sEntityType the type (class name) of the entity
     * @param oEntity the entity to cache
     */
    public void addToCacheWithType(String sName, String sEntityType, BasicEntity oEntity) {
        EnvironmentHandler.addEntityToCacheWithType(sName, sEntityType, oEntity);
        addEntityName(sName);
    }

    /**
     * Retrieve the specified entity from the global cache.
     * @param sName of the entity
     * @param sClassName of the entity
     * @return BasicEntity
     */
    public static BasicEntity getFromCache(String sName, String sClassName) {
        return getFromCache(sName, sClassName, true, false);
    }

    /**
     * Retrieve the specified entity from the global cache.
     * @param sName of the entity
     * @param sClassName of the entity
     * @param bFailIfNotFound if true, fail if not found
     * @param bWarnIfNotFound if true, warn if not found
     * @return BasicEntity
     */
    public static BasicEntity getFromCache(String sName, String sClassName, boolean bFailIfNotFound, boolean bWarnIfNotFound) {
        return getFromCache(sName, sClassName, bFailIfNotFound, bWarnIfNotFound, null);
    }

    /**
     * Retrieve the specified entity from the global cache.
     * @param sName of the entity
     * @param sClassName of the entity
     * @param bFailIfNotFound if true, fail if not found
     * @param bWarnIfNotFound if true, warn if not found
     * @param sQualifier (optional) the unique identity of the object requesting this one from the cache
     * @return BasicEntity
     */
    public static BasicEntity getFromCache(String sName, String sClassName, boolean bFailIfNotFound, boolean bWarnIfNotFound, String sQualifier) {
        return EnvironmentHandler.getEntityFromCache(sName, sClassName, bFailIfNotFound, bWarnIfNotFound, sQualifier);
    }

    /**
     * Indicates whether the specified entity exists in the global cache.
     * @param sObjectName of the entity
     * @param sClassName of the entity
     * @return boolean - true if it exists in the cache, otherwise false
     */
    public static boolean validateExists(String sObjectName, String sClassName) {
        return validateExists(sObjectName, sClassName, null);
    }

    /**
     * Indicates whether the specified entity exists in the global cache.
     * @param sObjectName of the entity
     * @param sClassName of the entity
     * @param sQualifier (optional) the unique identity of the object checking for the existence of this one
     * @return boolean - true if it exists in the cache, otherwise false
     */
    public static boolean validateExists(String sObjectName, String sClassName, String sQualifier) {
        boolean bExists;
        if (sObjectName.equals(ACG.DEFAULT_ACG))
            bExists = true;
        else if (sClassName.equals(User.class.getName()) && sObjectName.equals("Admin"))
            bExists = true;
        else
            bExists = (null != getFromCache(sObjectName, sClassName, false, true, sQualifier));
        return bExists;
    }

    /**
     * Add the specified XML filename as one that is generated.
     * @param sFilename of the XML file
     */
    public void addGeneratedFileXML(String sFilename) {
        alFileListXML.add(sFilename);
    }

    /**
     * Retrieve the list of generated XML files.
     * @return {@code List<String>}
     */
    public List<String> getGeneratedFilesXML() {
        return alFileListXML;
    }

    /**
     * Add the specified entity name to the list of entities handled.
     * @param sName of the entity
     */
    public void addEntityName(String sName) {
        if (!alOrderedEntityNames.contains(sName))
            alOrderedEntityNames.add(sName);
    }

    /**
     * Retrieve the list of entity names, in the order they were handled.
     * @return {@code List<String>}
     */
    public List<String> getOrderedEntityNames() {
        return alOrderedEntityNames;
    }

    /**
     * Indicates whether the specified version uses the next-generation XML (true) or not (false).
     * @return boolean
     */
    public boolean versionUsesNextGenXML() {
        return version.equals("5.3.2")
                || (Integer.parseInt(version.substring(0, version.indexOf("."))) > 5);
    }

    /**
     * Validates the provided entity is referentially-integral. By default this will always be true: this method must
     * be overridden to implement specific validation functionality by any sub-class that requires such checks.
     * @param oEntity the entity to validate
     * @return boolean - true if it is referentially-integral, otherwise false
     */
    public boolean validate(BasicEntity oEntity) {
        return true;
    }

    /**
     * Output the CSV representation of the provided entity.
     * @param oEntity entity to translate to CSV
     * @param outFile the file into which to write the CSV representation
     * @param sOutputPath ?
     * @throws IOException on any error writing
     */
    protected void outputEntityCSV(BasicEntity oEntity, Writer outFile, String sOutputPath) throws IOException {
        EnvironmentHandler.logger.finer(". . . outputting CSV for " + oEntity.getUniqueId());
        oEntity.outputEntityCSV(outFile, sOutputPath);
    }

    /**
     * Retrieve the XML structure required to control the import of a set of entities.
     * @param sInputFilePath the path to the XML file to include in the import
     * @return String - the XML snippet for controlling the import of that file
     */
    public String getImportEnvXML(String sInputFilePath) {
        List<String> aGenFiles = getGeneratedFilesXML();
        return "   <Import enable=\"true\" type=\"" + entity.getObjectType() + "\">\n" +
                "      <File>" + aGenFiles.get(0).replace(sInputFilePath, "") + "</File>\n" +
                "   </Import>\n";
    }

    /**
     * Replace any invalid characters for a filename with an underscore.
     * @param s the string in which to do the replacement
     * @return String
     */
    public static String escapeForFilename(String s) {
        Matcher m = NAMING_CHAR_WHITELIST.matcher(s);
        return m.replaceAll("_");
    }

    /**
     * Replace the provided variable name with the provided replacement in all of the provided fields.
     * @param aFields the fields in which to apply the replacement
     * @param sVarName the variable name to replace
     * @param sReplacement the value with which to replace the variable name
     * @return {@code List<String>} - with all of the replacements made
     */
    private static List<String> replaceFields(List<String> aFields, String sVarName, String sReplacement) {
        List<String> alReplaced = new ArrayList<>();
        for (String aField : aFields) {
            alReplaced.add(aField.replace(sVarName, sReplacement));
        }
        return alReplaced;
    }

    /**
     * Replace all of the values in the provided fields with the provided template parameters.
     * @param aFields the fields in which to apply the replacements
     * @param tm the template parameters that define the variables and their replacements
     * @param iTemplateParamIndex ?
     * @return {@code List<List<String>>}
     */
    public static List<List<String>> replaceTemplateParameters(List<String> aFields, TemplateParameters tm, int iTemplateParamIndex) {

        String sTopLevelVarName = tm.getTopLevelVarname();
        String sSecondLvlVarName = tm.getSecondLevelVarname();

        String sLineCSV = String.join(",", aFields);
        List<List<String>> alReplacedTemplates = new ArrayList<>();

        // Only go through the replacement routines if there is in fact any template parameter to be filled in...
        if (iTemplateParamIndex == -1 || CSVParser.checkBoolean(aFields.get(iTemplateParamIndex))) {
            if (sTopLevelVarName.equals("")) {
                EnvironmentHandler.logger.warning(". . . WARNING: No template variables have been defined - skipping object.");
                alReplacedTemplates.add(aFields);
            } else {

                Map<String, List<String>> hmSecondReplacedLines = new TreeMap<>();

                List<String> aTopLevelParams = tm.getTopLevelVars();
                for (String topLevelParam : aTopLevelParams) {
                    List<String> alSecondLvlParams = tm.getSecondLevelFromTopLevel(topLevelParam);
                    Map<String, List<String>> hmReplacedLines = new TreeMap<>();
                    // First we'll replace the top-levels (if there is anything to be replaced)
                    if (sLineCSV.indexOf(sTopLevelVarName) > 0) {
                        String sReplacedLine = sLineCSV.replace(sTopLevelVarName, topLevelParam);
                        List<String> aReplacedFields = replaceFields(aFields, sTopLevelVarName, topLevelParam);
                        hmReplacedLines.put(sReplacedLine, aReplacedFields);
                    } else {
                        hmReplacedLines.put(sLineCSV, aFields);
                    }
                    // Second, we'll replace the second-levels (within what's already been replaced by the top-levels)
                    for (String sReplacedLine : hmReplacedLines.keySet()) {
                        List<String> aReplacedFields = hmReplacedLines.get(sReplacedLine);
                        if (sReplacedLine.indexOf(sSecondLvlVarName) > 0) {
                            for (String sSecondLvlParam : alSecondLvlParams) {
                                String sSecondReplacedLine = sReplacedLine.replace(sSecondLvlVarName, sSecondLvlParam);
                                List<String> aSecondReplacedFields = replaceFields(aReplacedFields, sSecondLvlVarName, sSecondLvlParam);
                                hmSecondReplacedLines.put(sSecondReplacedLine, aSecondReplacedFields);
                            }
                        } else {
                            hmSecondReplacedLines.put(sReplacedLine, aReplacedFields);
                        }
                    }
                }

                for (String sSecondReplacement : hmSecondReplacedLines.keySet()) {
                    List<String> aSecondRepFields = hmSecondReplacedLines.get(sSecondReplacement);
                    alReplacedTemplates.add(aSecondRepFields);
                }

            }
        } else {
            // Just return the fields as they are if there is no replacement to be done
            alReplacedTemplates.add(aFields);
        }

        return alReplacedTemplates;

    }

    /**
     * Escape the provided string for proper XML representation.
     * @param sArg the string to escape
     * @return String
     */
    public static String escapeForXML(String sArg) {
        String s = sArg.replace("&", "&amp;");
        s = s.replace("<", "&lt;");
        s = s.replace(">", "&gt;");
        return s;
    }

    /**
     * Validate all of the entities of the type provided.
     * @param sClassName the type of entity to validate
     * @return boolean - true if all of the entities of this type are valid, otherwise false
     */
    public boolean validateEntities(String sClassName) {

        boolean bValid = true;

        List<String> aEntityNames = getOrderedEntityNames();
        for (String sEntityName : aEntityNames) {
            BasicEntity oEntity = getFromCache(sEntityName, sClassName);
            bValid = validate(oEntity) && bValid;
        }

        return bValid;

    }

    /**
     * Output the entities of the specified type.
     * @param sCompanyCode the company code of the environment
     * @param sClassName the type of entities to output
     * @param outFile the file into which to generate the output
     * @param sOutputFilePath the location of the file into which the output should be generated
     * @param sOutputType the type of output to generate ({@literal XML} or {@literal CSV})
     * @throws IOException on any error writing
     */
    public void outputEntities(String sCompanyCode, String sClassName, Writer outFile, String sOutputFilePath, String sOutputType) throws IOException {

        List<String> aEntityNames = getOrderedEntityNames();
        String sOutputDir = sOutputFilePath.substring(0, sOutputFilePath.lastIndexOf(File.separator));

        if (sOutputType.equals("XML")) {
            outFile.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            if (BasicEntity.containsData(version)) {
                outFile.write("<" + entity.getRootElement() + " version=\"" + version + "\">\n");
            } else {
                outFile.write("<" + entity.getRootElement() + ">\n");
            }
            for (String sEntityName : aEntityNames) {
                BasicEntity oEntity = getFromCache(sEntityName, sClassName);
                EnvironmentHandler.logger.finer(". . . outputting XML for " + oEntity.getUniqueId());
                oEntity.outputEntityXML(this, outFile, sOutputDir, sCompanyCode);
            }
            outFile.write("</" + entity.getRootElement() + ">\n");
        } else if (sOutputType.equals("CSV")) {
            entity.outputHeaderCSV(outFile);
            for (String sEntityName : aEntityNames) {
                BasicEntity oEntity = getFromCache(sEntityName, sClassName);
                outputEntityCSV(oEntity, outFile, sOutputDir);
            }
        }

    }

    /**
     * Create a new writer for the file at the specified location, creating its directory location if needed.
     * @param sFilePath the path to the file into which to create the writer
     * @return Writer
     */
    public static Writer getNewWriter(String sFilePath) {
        OutputStreamWriter osw = null;
        try {
            String sDirectoryPath = sFilePath.substring(0, sFilePath.lastIndexOf(File.separator));
            File fPath = new File(sDirectoryPath);
            fPath.mkdirs();
            osw = new OutputStreamWriter(new FileOutputStream(sFilePath, false), StandardCharsets.UTF_8);
        } catch (Exception e) {
            EnvironmentHandler.logger.severe("ERROR: Unable to get new writer: " + e.getMessage());
        }
        return osw;
    }

    /**
     * Output the environment file for the specified parameters.
     * @param sCompanyCode the company code for the environment
     * @param sClassName the type of entities to output
     * @param sOutputFilePath the location of the file into which to output the entities
     * @param sOutputType the type of output ({@literal XML} or {@literal CSV})
     */
    public void outputEnvFile(String sCompanyCode, String sClassName, String sOutputFilePath, String sOutputType) {

        try {
            Writer writer = getNewWriter(sOutputFilePath);
            EnvironmentHandler.logger.fine("Outputting " + sOutputType + " to : " + sOutputFilePath);
            outputEntities(sCompanyCode, sClassName, writer, sOutputFilePath, sOutputType);
            writer.flush();
            addGeneratedFileXML(sOutputFilePath);
        } catch (FileNotFoundException errNoFile) {
            EnvironmentHandler.logger.severe("Error: File not found! " + errNoFile.getMessage());
        } catch (IOException errIO) {
            EnvironmentHandler.logger.severe("Error: IO problem! " + errIO.getMessage());
        }

    }

    /**
     * Retrieve the path to the CSV file that this handler processes.
     * @return String
     */
    public String getCsvFilePath() {
        return csvFilePath;
    }

    /**
     * Retrieve the path to the XML file that this handler processes.
     * @return String
     */
    public String getXmlFilePath() {
        return xmlFilePath;
    }

    /**
     * Indicates whether information was actually parsed for the types this handler processes.
     * @return boolean - true if there is information held, otherwise false
     */
    public boolean hasFederatedInfo() {
        return federated;
    }

    /**
     * Retrieve the version of the software being handled.
     * @return String
     */
    public String getVersion() {
        return version;
    }

}
