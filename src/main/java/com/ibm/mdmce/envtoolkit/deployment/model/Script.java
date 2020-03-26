/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment.model;

import com.ibm.mdmce.envtoolkit.deployment.BasicEntityHandler;
import com.ibm.mdmce.envtoolkit.deployment.CSVParser;
import com.ibm.mdmce.envtoolkit.deployment.DocumentHandler;

import java.io.*;
import java.util.*;

/**
 * Processes <b>Scripts.csv</b>.
 */
public class Script extends BasicEntity {

    public static final String PATH = "Script path (relative to \"loadToEnv\")";
    public static final String NAME = "Script name";
    public static final String TYPE = "Script type";
    public static final String FILE_DEST_SPEC = "File spec / Destination spec name (imports / exports)";
    public static final String INPUT_SPEC = "Input spec name (if any)";
    public static final String CHARSET = "Character set";
    public static final String ASP_JSP_LIKE = "ASP / JSP-like";
    public static final String CONTAINER_NAME = "Container name (ctr / ctg / workflow scripts)";
    public static final String CONTAINER_TYPE = "Container type (entry build / preview / macro scripts)";

    private static final Map<String, String> SCRIPT_TYPE_TO_PATH = createScriptTypeToPath();
    private static Map<String, String> createScriptTypeToPath() {
        Map<String, String> map = new HashMap<>();
        map.put("AGGREGATE_UPDATE", "/scripts/aggregate_update/");
        map.put("CTG_DIFF_EXPORT", "/scripts/export/ctg_diff/");
        map.put("CTG_EXPORT", "/scripts/export/ctg/");
        map.put("CTG_IMPORT", "/scripts/import/ctg/");
        map.put("ENTRY_MACRO", "/scripts/entry_macro/");
        map.put("CTG", "/scripts/catalog/");
        map.put("CATALOG", "/scripts/catalog/");
        map.put("CTG_MACRO", "/scripts/entry_macro/");
        map.put("ENTRY_PREVIEW", "/scripts/entry_preview/");
        map.put("CATALOG_PREVIEW", "/scripts/catalog_preview/");
        map.put("CATEGORY_PREVIEW", "/scripts/category_preview/");
        map.put("CTR", "/scripts/category_tree/");
        map.put("CATEGORY_TREE", "/scripts/category_tree/");
        map.put("CTLG_CTLG_EXPORT", "/scripts/export/ctg_to_ctg/");
        map.put("CTR_IMPORT", "/scripts/import/ctr/");
        map.put("DISTRIBUTION", "/scripts/distribution/");
        map.put("ENTRY_BUILD", "/scripts/entry_build/");
        map.put("IMG_DIFF_EXPORT", "/scripts/export/img_diff/");
        map.put("IMG_EXPORT", "/scripts/export/img/");
        map.put("LKP_IMPORT", "/scripts/import/lkp/");
        map.put("PO_EXPORT", "/scripts/export/po/");
        map.put("PO_IMPORT", "/scripts/import/po/");
        map.put("PO_STATUS_REQUEST", "/scripts/import/po_status_request/");
        map.put("PO_STATUS_UPDATE_IMPORT", "/scripts/import/po_status_update/");
        map.put("REPORT", "/scripts/reports/");
        map.put("SEARCH_RESULT_REPORT", "/scripts/report/");
        map.put("SECURE_TRIGGER", "/scripts/secure_triggers/");
        map.put("TRIGGER", "/scripts/triggers/");
        map.put("WIDGETS", "/scripts/widgets/");
        map.put("WORKFLOW", "/scripts/workflow/");
        map.put("TRIGO_APP", "/scripts/trigo_app/");
        map.put("QMSG_PROCESSOR", "/scripts/qmsg_processor/");
        map.put("ENTITY_SYNCHRONIZATION", "/scripts/entity_synchronization/");
        map.put("WBS", "/scripts/wbs/");
        map.put("LOGIN", "/scripts/login/");
        map.put("LOGOUT", "/scripts/logout/");
        map.put("LDAP_USR_FETCH", "/scripts/ldap_usr_fetch/");
        map.put("INPUT_PARAM", "/params/");
        map.put("DOCUMENT", "");
        return Collections.unmodifiableMap(map);
    }

    private String pathLocal;
    private String pathRemote;
    private String name;
    private String type;
    private String fileDestSpec;
    private String inputSpec;
    private String charset = "Cp1252";
    private boolean aspJspLike = false;
    private String containerName = "";
    private String containerType = "";
    private boolean script = true;
    private boolean documentation = false;

    private static class Singleton {
        private static final Script INSTANCE = new Script();
    }

    /**
     * Retrieve the static definition of a Document (ie. its columns and type information).
     * @return Document
     */
    public static Script getInstance() {
        return Script.Singleton.INSTANCE;
    }

    private Script() {
        super("DOC_STORE", "DocListXML");
        addColumn(PATH);
        addColumn(NAME);
        addColumn(TYPE);
        addColumn(FILE_DEST_SPEC);
        addColumn(INPUT_SPEC);
        addColumn(CHARSET);
        addColumn(ASP_JSP_LIKE);
        addColumn(CONTAINER_NAME);
        addColumn(CONTAINER_TYPE);
    }

    /**
     * Construct a new document that represents documentation (not a script).
     * @param sDocPath path to the documentation
     */
    public Script(String sDocPath) {
        this();
        pathLocal = sDocPath;
        name = sDocPath;
        pathRemote = sDocPath;
        documentation = true;
        script = false;
    }

    /**
     * Construct a new document from the provided parameters.
     * @param type of the document
     * @param name for the document
     * @param inputSpec for the document
     * @param pathLocal for the document
     * @param pathRemote for the document
     */
    public Script(String type,
                  String name,
                  String inputSpec,
                  String pathLocal,
                  String pathRemote) {
        this();
        this.type = type;
        this.name = name;
        this.inputSpec = inputSpec;
        this.script = false;
        this.pathLocal = pathLocal;
        this.pathRemote = pathRemote;
    }

    /**
     * Construct a new instance of a Document using the provided field values.
     * @param <T> expected to be Document whenever used by this class
     * @param aFields from which to construct the Document
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BasicEntity> T createInstance(List<String> aFields) {
        Script doc = new Script();
        doc.pathLocal = getFieldValue(PATH, aFields);
        doc.name = getFieldValue(NAME, aFields);
        doc.type = getFieldValue(TYPE, aFields);
        doc.fileDestSpec = getFieldValue(FILE_DEST_SPEC, aFields);
        doc.inputSpec = getFieldValue(INPUT_SPEC, aFields);
        doc.charset = getFieldValue(CHARSET, aFields);
        doc.aspJspLike = CSVParser.checkBoolean(getFieldValue(ASP_JSP_LIKE, aFields));
        doc.containerName = getFieldValue(CONTAINER_NAME, aFields);
        doc.containerType = getFieldValue(CONTAINER_TYPE, aFields);
        doc.script = ( !doc.type.equals("DOCUMENT") && !doc.type.equals("INPUT_PARAM") );
        if (doc.type.equals("INPUT_PARAM")) {
            doc.pathRemote = "/params/" + doc.inputSpec + "/" + doc.name;
        } else if (doc.type.equals("WORKFLOW")) {
            doc.pathRemote = SCRIPT_TYPE_TO_PATH.get(doc.type) + doc.containerName + "/" + doc.name;
        } else {
            doc.pathRemote = SCRIPT_TYPE_TO_PATH.get(doc.type) + doc.name;
        }
        return (T) doc;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUniqueId() {
        return getPathRemote();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputEntityXML(BasicEntityHandler handler, Writer outFile, String sOutputPath, String sCompanyCode) throws IOException {
        String inputDirectory = "";
        if (handler instanceof DocumentHandler) {
            DocumentHandler documentHandler = (DocumentHandler) handler;
            inputDirectory = documentHandler.getInputDirectory();
        }
        if (isScript()) {
            outputDocNodeXML(inputDirectory, outFile, "script");
        }
        outputDocNodeXML(inputDirectory, outFile, "any");
    }

    private void outputDocNodeXML(String inputDirectory, Writer outFile, String sDocType) throws IOException {

        outFile.write("   <Doc type=\"" + sDocType + "\">\n");
        outFile.write("      <LocalPath>" + getPathLocal() + "</LocalPath>\n");
        outFile.write("      <Action>CREATE_OR_UPDATE</Action>\n");
        if (isDocumentation()) {
            outFile.write("      <StorePath>/public_html/" + getPathRemote() + "</StorePath>\n");
        } else {
            outFile.write("      <StorePath>" + getPathRemote() + "</StorePath>\n");
        }

        StringBuilder sAttribs = new StringBuilder();

        if (getType().equals("INPUT_PARAM")) {
            sAttribs.append("         <Attrib name=\"").append(SCRIPT_TYPE_TO_PATH.get("INPUT_PARAM")).append(getInputSpec()).append("/").append(getName()).append("\" value=\"\"/>\n");
            System.out.println(" . . . Attempting to read parameters from: " + inputDirectory + File.separator + getPathLocal());
            File fileParams = new File(inputDirectory + File.separator + getPathLocal());
            if (!fileParams.exists()) {
                String sParamsPath = fileParams.getPath();
                System.err.println(" . . . WARNING: Unable to find parameters specified, will create empty default: " + sParamsPath);
                File fileParamsPath = new File(sParamsPath.substring(0, sParamsPath.lastIndexOf(File.separator)));
                fileParamsPath.mkdirs();
                fileParams.createNewFile();
            } else {
                BufferedReader rdrParams = new BufferedReader(new InputStreamReader(new FileInputStream(inputDirectory + File.separator + getPathLocal()), getCharset()));
                String sLine = "";
                while ( (sLine = rdrParams.readLine()) != null) {
                    String[] aParamTokens = sLine.split("\\Q|\\E");
                    String sAttrPath = aParamTokens[0];
                    String sAttrValue = aParamTokens[1];
                    sAttribs.append("         <Attrib name=\"").append(sAttrPath).append("\" value=\"").append(sAttrValue).append("\"/>\n");
                }
            }
        } else if (isAspJspLike()) {
            sAttribs.append("         <Attrib name=\"jsplike\" value=\"true\"/>\n" + "         <Attrib name=\"ATTR_JSPLIKE\" value=\"true\"/>\n");
        } else if (!getType().equals("DOCUMENT")){
            sAttribs.append("         <Attrib name=\"ATTR_JSPLIKE\" value=\"false\"/>\n");
        }
        if (!getFileDestSpec().equals("")) {
            sAttribs.append("         <Attrib name=\"ATTR_SPEC_NAME\" value=\"").append(getFileDestSpec()).append("\"/>\n");
        }
        if (!getInputSpec().equals("")) {
            sAttribs.append("         <Attrib name=\"ATTR_SCRIPT_INPUT_SPEC_NAME\" value=\"").append(getInputSpec()).append("\"/>\n");
        }
        if (!getCharset().equals("")) {
            sAttribs.append("         <Attrib name=\"CHARSET\" value=\"").append(getCharset()).append("\"/>\n");
        }
        if (!getType().equals("DOCUMENT")) {
            sAttribs.append("         <Attrib name=\"COMPRESSED\" value=\"true\"/>\n");
            sAttribs.append("         <Attrib name=\"GENERATED\" value=\"\"/>\n");
        }

        // Handle object-related scripts...
        if (getType().equals("CTG")) {
            if (getContainerName().equals("$ALL")) {
                sAttribs.append("         <Attrib name=\"CATALOG_ID\" value=\"-2\"/>\n");
            } else {
                sAttribs.append("         <Attrib name=\"CATALOG_NAME\" value=\"").append(getContainerName()).append("\"/>\n");
            }
        } else if (getType().equals("CTR")) {
            if (getContainerName().equals("$ALL")) {
                sAttribs.append("         <Attrib name=\"CATEGORY_TREE_ID\" value=\"-2\"/>\n");
            } else {
                sAttribs.append("         <Attrib name=\"CATEGORY_TREE_NAME\" value=\"").append(getContainerName()).append("\"/>\n");
            }
        } else if (getType().startsWith("ENTRY_")) {
            sAttribs.append("         <Attrib name=\"CONTAINER_TYPE\" value=\"").append(getContainerType()).append("\"/>\n");
            if (getContainerName().equals("$ALL")) {
                sAttribs.append("         <Attrib name=\"CONTAINER_ID\" value=\"-2\"/>\n");
            } else {
                sAttribs.append("         <Attrib name=\"CONTAINER_NAME\" value=\"").append(getContainerName()).append("\"/>\n");
            }
        }

        if (!sAttribs.toString().equals("")) {
            outFile.write("      <Attribs>\n");
            outFile.write(sAttribs.toString());
            outFile.write("      </Attribs>\n");
        }
        outFile.write("   </Doc>\n");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputEntityCSV(Writer outFile, String sOutputPath) throws IOException {
        List<String> line = new ArrayList<>();
        line.add(getPathLocal());
        line.add(getName());
        line.add(getType());
        line.add(getFileDestSpec());
        line.add(getInputSpec());
        line.add(getCharset());
        line.add("" + isAspJspLike());
        line.add(getContainerName());
        line.add(getContainerType());
        outputCSV(line, outFile);
    }

    public String getPathLocal() {
        return pathLocal;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getFileDestSpec() {
        return fileDestSpec;
    }

    public String getInputSpec() {
        return inputSpec;
    }

    public String getCharset() {
        return charset;
    }

    public boolean isAspJspLike() {
        return aspJspLike;
    }

    public String getContainerName() {
        return containerName;
    }

    public String getContainerType() {
        return containerType;
    }

    public String getPathRemote() {
        return pathRemote;
    }

    public boolean isScript() {
        return script;
    }

    public boolean isDocumentation() {
        return documentation;
    }

}
