/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment;

import com.ibm.mdmce.envtoolkit.deployment.model.BasicEntity;
import com.ibm.mdmce.envtoolkit.deployment.model.CompanyAttribute;
import com.ibm.mdmce.envtoolkit.deployment.model.TemplateParameters;

import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.logging.*;

/**
 * The primary execution thread through which all of the entity handlers are invoked, the only requirements to utilise
 * are to be certain to pass the following parameters:
 * <ol>
 *   <li>The company code of the environment for which to build files.</li>
 *   <li>The filesystem directory path that gives the location of the deployment CSV files.</li>
 *   <li>The filesystem directory path to use for outputting the generated XML (i.e. temporary directory).</li>
 *   <li>Optionally, the filesystem directory path containing Doxygen documentation that has been generated for the implementation.</li>
 * </ol>
 */
public class EnvironmentHandler {

    public static final String PACKAGE_NAME = "com.ibm.mdmce.envtoolkit.deployment";
    public static final String MODEL_PKG_NAME = PACKAGE_NAME + ".model";

    private String companyCode;
    private String version;
    private String inputPath;
    private String outputPath;
    private static String _ENCODING;
    private TemplateParameters templateParameters;

    public static PrintWriter out;
    public static PrintWriter err;

    private static Map<String, BasicEntity> hmFullEntityCache = new HashMap<>();
    private static List<String> alAllLocales = new ArrayList<>();

    private static Map<String, BasicEntityHandler> hmEntityHandlers = new HashMap<>();

    public static Logger logger;
    private static void initLogger(Level lvl){
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%n");
        logger = Logger.getLogger("toolkit");
        logger.setLevel(lvl); 
        System.out.println("Logger level: "+logger.getLevel());
        for (Handler handler : logger.getParent().getHandlers()){//The logger inherits parent handler, which is 'INFO' by default
            handler.setLevel(lvl);
            System.out.println("Parent handler level: "+handler.getLevel());
        }
    }

    private static final String[] ENTITY_ORDER = {
            "CompanyAttribute",
            "Script",
            "DataSource",
            "Distribution",
            "ACG",
            "Spec",
            "Lookup",
            "LookupTableContent",
            "AttrCollection",
            "Hierarchy",
            "Organization",
            "OrganizationContent",
            "Catalog",
            "CatalogView",
            "HierarchyView",
            "Role",
            "RoleToACG",
            "AccessPriv",
            "User",
            "Setting",
            "Workflow",
            "WorkflowStepView",
            "ColArea",
            "WebService",
            "Selection",
            "SearchTemplate",
            "SpecMap",
            "Export",
            "Import",
            "Report",
            "UDL",
            "CatalogContent",
            "HierarchyContent",
            "HierarchyMapping"
    };

    protected void initEntities(String sDocumentationFilePath) {
        hmEntityHandlers.put("CompanyAttribute", new CompanyAttributeHandler(inputPath, version, templateParameters, _ENCODING));
        hmEntityHandlers.put("Script", new DocumentHandler(inputPath, sDocumentationFilePath, version, templateParameters, _ENCODING));
        hmEntityHandlers.put("DataSource", new DataSourceHandler(inputPath, version, templateParameters, _ENCODING));
        hmEntityHandlers.put("Distribution", new DistributionHandler(inputPath, version, templateParameters, _ENCODING));
        hmEntityHandlers.put("ACG", new AccessControlGroupHandler(inputPath, version, templateParameters, _ENCODING));
        hmEntityHandlers.put("Spec", new SpecHandler(inputPath, version, templateParameters, companyCode, _ENCODING));
        hmEntityHandlers.put("Lookup", new LookupTableHandler(inputPath, version, templateParameters, _ENCODING));
        hmEntityHandlers.put("LookupTableContent", new LookupTableDataHandler(inputPath, version, templateParameters, outputPath, _ENCODING));
        hmEntityHandlers.put("AttrCollection", new AttributeCollectionHandler(inputPath, version, templateParameters, _ENCODING));
        hmEntityHandlers.put("Hierarchy", new HierarchyHandler(inputPath, version, templateParameters, _ENCODING));
        hmEntityHandlers.put("Organization", new OrganizationHandler(inputPath, version, templateParameters, _ENCODING));
        hmEntityHandlers.put("OrganizationContent", new OrganizationContentHandler(inputPath, version, templateParameters, outputPath, _ENCODING));
        hmEntityHandlers.put("Catalog", new CatalogHandler(inputPath, version, templateParameters, _ENCODING));
        hmEntityHandlers.put("CatalogView", new CatalogViewHandler(inputPath, version, templateParameters, _ENCODING));
        hmEntityHandlers.put("HierarchyView", new HierarchyViewHandler(inputPath, version, templateParameters, _ENCODING));
        hmEntityHandlers.put("Role", new RoleHandler(inputPath, version, templateParameters, _ENCODING));
        hmEntityHandlers.put("RoleToACG", new RoleMappingHandler(inputPath, version, templateParameters, _ENCODING));
        hmEntityHandlers.put("AccessPriv", new AccessPrivilegeHandler(inputPath, version, templateParameters, companyCode, _ENCODING));
        hmEntityHandlers.put("User", new UserHandler(inputPath, version, templateParameters, _ENCODING));
        hmEntityHandlers.put("Setting", new SettingHandler(inputPath, version, templateParameters, _ENCODING));
        hmEntityHandlers.put("Workflow", new WorkflowHandler(inputPath, version, templateParameters, _ENCODING));
        hmEntityHandlers.put("WorkflowStepView", new WorkflowStepViewHandler(inputPath, version, templateParameters, _ENCODING));
        hmEntityHandlers.put("ColArea", new CollaborationAreaHandler(inputPath, version, templateParameters, _ENCODING));
        hmEntityHandlers.put("WebService", new WebServiceHandler(inputPath, version, templateParameters, _ENCODING));
        hmEntityHandlers.put("Selection", new SelectionHandler(inputPath, version, templateParameters, _ENCODING));
        hmEntityHandlers.put("SearchTemplate", new SearchTemplateHandler(inputPath, version, templateParameters, _ENCODING));
        hmEntityHandlers.put("SpecMap", new SpecMapHandler(inputPath, version, templateParameters, companyCode, _ENCODING));
        hmEntityHandlers.put("Export", new ExportHandler(inputPath, version, templateParameters, _ENCODING));
        hmEntityHandlers.put("Import", new ImportFeedHandler(inputPath, version, templateParameters, _ENCODING));
        hmEntityHandlers.put("Report", new ReportHandler(inputPath, version, templateParameters, _ENCODING));
        hmEntityHandlers.put("UDL", new UserDefinedLogHandler(inputPath, version, templateParameters, _ENCODING));
        hmEntityHandlers.put("CatalogContent", new CatalogContentHandler(inputPath, version, templateParameters, outputPath, _ENCODING));
        hmEntityHandlers.put("HierarchyContent", new HierarchyContentHandler(inputPath, version, templateParameters, outputPath, _ENCODING));
        hmEntityHandlers.put("HierarchyMapping", new HierarchyMappingHandler(inputPath, version, templateParameters, _ENCODING));
    }

    /**
     * Construct a new environment archive.
     * @param sCmpCode the company code for the environment
     * @param sVersion the software version for the environment
     * @param sInputPath the directory containing the input CSV files
     * @param sOutputPath the directory into which to write the generated XML files
     * @param sDocumentationFilePath (optional) directory containing Doxygen documentation
     * @param sEncoding the encoding used within the files
     */
    public EnvironmentHandler(String sCmpCode, String sVersion, String sInputPath, String sOutputPath, String sDocumentationFilePath, String sEncoding) {

        companyCode = sCmpCode;
        version = sVersion;
        inputPath = sInputPath;
        outputPath = sOutputPath;
        _ENCODING = sEncoding;

        try {
            out = new PrintWriter(new OutputStreamWriter(System.out, sEncoding), true);
            err = new PrintWriter(new OutputStreamWriter(System.err, sEncoding), true);
        } catch (UnsupportedEncodingException uee) {
            err.println("ERROR: Unable to create console outputs using encoding: " + sEncoding);
        }

        TemplateParameterMarshaller tpm = new TemplateParameterMarshaller(inputPath + File.separator + "TemplateParameters.csv", sEncoding);
        templateParameters = tpm.getTemplateParameters();

        out.println("Reading input from        : " + inputPath);
        out.println("Reading documentation from: " + sDocumentationFilePath);
        if (templateParameters.getTopLevelVarname().equals("")) {
            out.println("Not creating template objects - no template parameters defined.");
        } else {
            out.println("Creating template objects using the following variables:");
            for (String sTopLevelVar : templateParameters.getTopLevelVars()) {
                out.println(" . . . " + sTopLevelVar + ":");
                List<String> alVars = templateParameters.getSecondLevelFromTopLevel(sTopLevelVar);
                for (String sSecondVar : alVars) {
                    out.println(" . . . . . . " + sSecondVar);
                }
            }
        }

        out.println("Marshalling data...");
        initEntities(sDocumentationFilePath);

    }

    /**
     * Output the generated XML files.
     */
    public void outputEnvironmentFiles() {

        try {

            Date dToday = new Date();
            SimpleDateFormat formatCreationDate = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");

            Writer outFile = BasicEntityHandler.getNewWriter(outputPath + File.separator + "ImportEnvControl.xml");
            outFile.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            outFile.write("<ImportList CreatedDate=\"" + formatCreationDate.format(dToday) + "\" name=\"\" version=\"" + version + "\">\n");

            for (String sEntityName : ENTITY_ORDER) {

                BasicEntityHandler beh = getHandler(sEntityName);
                if (beh.hasFederatedInfo()) {
                    out.println("Writing ImportEnv file for entity " + sEntityName);
                    beh.outputEnvFile(companyCode, MODEL_PKG_NAME + "." + sEntityName, outputPath + File.separator + beh.getXmlFilePath(), "XML");
                    outFile.write(beh.getImportEnvXML(outputPath + File.separator).replace("\\", "/"));
                }

            }

            outFile.write("</ImportList>\n");
            outFile.flush();

        } catch (FileNotFoundException errNoFile) {
            err.println("Error: File not found! " + errNoFile.getMessage());
        } catch (IOException errIO) {
            err.println("Error: IO problem! " + errIO.getMessage());
        }

    }

    /**
     * Validate the provided CSV files are referentially-integral.
     */
    public void validateEnvironmentFiles() {

        for (String sEntityName : ENTITY_ORDER) {

            BasicEntityHandler beh = getHandler(sEntityName);
            out.println("Validating " + sEntityName + "s ...");
            beh.validateEntities(MODEL_PKG_NAME + "." + sEntityName);

        }

    }

    /**
     * Add the provided entity into the global cache.
     * @param sName the key by which to refer to the entity
     * @param oEntity the entity to cache
     */
    public static void addEntityToCache(String sName, BasicEntity oEntity) {
        String sEntityType = oEntity.getClass().getName();
        addEntityToCacheWithType(sName, sEntityType, oEntity);
    }

    /**
     * Add the provided entity into the global cache.
     * @param sName the key by which to refer to the entity
     * @param sEntityType the type (class name) of the entity
     * @param oEntity the entity to cache
     */
    public static void addEntityToCacheWithType(String sName, String sEntityType, BasicEntity oEntity) {
        hmFullEntityCache.put(sEntityType + "|" + sName, oEntity);
        if (sEntityType.equals(CompanyAttribute.class.getName())) {
            if (sName.indexOf("_") > 0 && !alAllLocales.contains(sName))
                alAllLocales.add(sName);
        }
    }

    /**
     * Retrieve the specified entity from the global cache.
     * @param sName of the entity to retrieve
     * @param sEntityType type (class name) of the entity
     * @param bFailIfNotFound if true, will fail if not found
     * @param bWarnIfNotFound if true, will warn if not found
     * @return BasicEntity
     */
    public static BasicEntity getEntityFromCache(String sName, String sEntityType, boolean bFailIfNotFound, boolean bWarnIfNotFound) {
        return getEntityFromCache(sName, sEntityType, bFailIfNotFound, bWarnIfNotFound, null);
    }

    /**
     * Retrieve the specified entity from the global cache.
     * @param sName of the entity to retrieve
     * @param sEntityType type (class name) of the entity
     * @param bFailIfNotFound if true, will fail if not found
     * @param bWarnIfNotFound if true, will warn if not found
     * @param sQualifier (optional) designation of the other object attempting to find this one in the cache
     * @return BasicEntity
     */
    public static BasicEntity getEntityFromCache(String sName, String sEntityType, boolean bFailIfNotFound, boolean bWarnIfNotFound, String sQualifier) {

        BasicEntity oEntity = hmFullEntityCache.get(sEntityType + "|" + sName);

        if (oEntity == null) {
            if (bFailIfNotFound) {
                if (sQualifier != null)
                    err.println(". . . ERROR (" + sQualifier + "): " + sName + " [" + sEntityType + "] not found!");
                else
                    err.println(". . . ERROR: " + sName + " [" + sEntityType + "] not found!");
                err.println(". . . Build will now exit due to failed dependencies (see above).");
                System.exit(1);
            } else if (bWarnIfNotFound) {
                if (sQualifier != null)
                    err.println(". . . WARNING (" + sQualifier + "): " + sName + " [" + sEntityType + "] not found!");
                else
                    err.println(". . . WARNING: " + sName + " [" + sEntityType + "] not found!");
            }
        }

        return oEntity;

    }

    /**
     * Retrieve the list of locales defined for the environment.
     * @return {@code List<String>}
     */
    public static List<String> getAllLocales() {
        return alAllLocales;
    }

    /**
     * Print the usage of the main method.
     */
    public static void printUsage() {
        out.println("Usage: EnvironmentHandler <companycode> <inputPath> <outputPath> [<version> <documentationPath> <encoding> <logLevel>]");
    }

    /**
     * Print the contents of the global cache.
     */
    public static void outputEnvCache() {

        out.println("===== CACHE CONTENTS =====");
        for (String sCacheKey : hmFullEntityCache.keySet()) {
            out.println(sCacheKey + ": " + hmFullEntityCache.get(sCacheKey));
        }

    }

    /**
     * Retrieve the handler for a given type of entity.
     * @param sHandlerName type of entity
     * @return BasicEntityHandler
     */
    public static BasicEntityHandler getHandler(String sHandlerName) {
        return hmEntityHandlers.get(sHandlerName);
    }

    /**
     * Run the generation.
     * @param args list of arguments: company code, input file directory, output file directory, version, documentation path, encoding
     */
    public static void main(String[] args) {

        // NOTE: These initialisations are only used for testing of the tooling,
        // they will be ignored when using the Ant build for your project (which
        // will overwrite these default values in the next few lines of code)
        String sCmpCode = "";
        String sInputFilePath = "";
        String sOutputFilePath = "";
        String sVersion = "";
        String sDocumentationPath = "";
        String sEncoding = "ISO-8859-1";
        String sLogLevel = Level.ALL.toString();
        if (args.length >= 3) {
            sCmpCode = args[0];
            sInputFilePath = args[1];
            sOutputFilePath = args[2];
            if (args.length > 3)
                sVersion = args[3];
            if (args.length > 4)
                sDocumentationPath = args[4];
            if (args.length > 5)
                sEncoding = args[5];
            if (args.length > 6)
                sLogLevel = args[6];
        } else {
            printUsage();
            System.exit(1);
        }
        initLogger(Level.parse(sLogLevel));
        EnvironmentHandler eh = new EnvironmentHandler(sCmpCode, sVersion, sInputFilePath, sOutputFilePath, sDocumentationPath, sEncoding);
        eh.validateEnvironmentFiles();
        eh.outputEnvironmentFiles();

    }

}
