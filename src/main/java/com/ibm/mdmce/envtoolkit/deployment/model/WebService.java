/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment.model;

import com.ibm.mdmce.envtoolkit.deployment.BasicEntityHandler;
import com.ibm.mdmce.envtoolkit.deployment.CSVParser;
import com.ibm.mdmce.envtoolkit.deployment.EnvironmentHandler;
import com.ibm.mdmce.envtoolkit.deployment.WebServiceHandler;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Processes <b>WebServices.csv</b>.
 */
public class WebService extends BasicEntity {

    public static final String NAME = "WebServices Name";
    public static final String DESCRIPTION = "WebService Description";
    public static final String PROTOCOL = "Protocol";
    public static final String STYLE = "Style";
    public static final String WSDL = "WSDL File";
    public static final String SCRIPT = "Implementation Script";
    public static final String STORE_REQUEST = "Store Requests?";
    public static final String STORE_RESPONSE = "Store Responses?";
    public static final String DEPLOY = "Deploy?";

    private String name;
    private String description;
    private String protocol;
    private String style;
    private String wsdl = "U";
    private String script;
    private boolean storeRequest = false;
    private boolean storeResponse = false;
    private boolean deploy = true;

    private static class Singleton {
        private static final WebService INSTANCE = new WebService();
    }

    /**
     * Retrieve the static definition of a WebService (ie. its columns and type information).
     * @return WebService
     */
    public static WebService getInstance() {
        return WebService.Singleton.INSTANCE;
    }

    private WebService() {
        super("WEBSERVICE", "WebServices");
        addColumn(COUNTRY_SPECIFIC);
        addColumn(NAME);
        addColumn(DESCRIPTION);
        addColumn(PROTOCOL);
        addColumn(STYLE);
        addColumn(WSDL);
        addColumn(SCRIPT);
        addColumn(STORE_REQUEST);
        addColumn(STORE_RESPONSE);
        addColumn(DEPLOY);
    }

    /**
     * Construct a new instance of a WebService using the provided field values.
     * @param <T> expected to be WebService whenever used by this class
     * @param aFields from which to construct the WebService
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BasicEntity> T createInstance(List<String> aFields) {
        WebService websvc = new WebService();
        websvc.name = getFieldValue(NAME, aFields);
        websvc.description = getFieldValue(DESCRIPTION, aFields);
        websvc.protocol = getFieldValue(PROTOCOL, aFields);
        websvc.style = getFieldValue(STYLE, aFields);
        websvc.wsdl = getFieldValue(WSDL, aFields);
        websvc.script = getFieldValue(SCRIPT, aFields);
        websvc.storeRequest = CSVParser.checkBoolean(getFieldValue(STORE_REQUEST, aFields));
        websvc.storeResponse = CSVParser.checkBoolean(getFieldValue(STORE_RESPONSE, aFields));
        websvc.deploy = CSVParser.checkBoolean(getFieldValue(DEPLOY, aFields));
        return (T) websvc;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUniqueId() {
        return getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputEntityXML(BasicEntityHandler handler, Writer outFile, String sOutputPath, String sCompanyCode) throws IOException {

        String sInputPath = "";
        if (handler instanceof WebServiceHandler) {
            sInputPath = ((WebServiceHandler) handler).getInputPath();
        }
        File fInputFile = new File(sInputPath);
        File fEnvDir = fInputFile.getParentFile().getParentFile();
        String sFilePath = fEnvDir.getAbsolutePath() + File.separator;

        String sPathScriptRemote = "/scripts/wbs/" + getScript();
        String sPathWSDLRemote = "/archives/wsdl/" + getWsdl();
        Script docScript = (Script) BasicEntityHandler.getFromCache(sPathScriptRemote, Script.class.getName(), false, false);
        Script docWSDL = (Script) BasicEntityHandler.getFromCache(sPathWSDLRemote, Script.class.getName(), false, false);

        if (docScript == null || docWSDL == null) {
            EnvironmentHandler.logger.warning(". . . WARNING: Could not find specified WebService script or WSDL file.");
        } else {

            String sPathScriptLocal = sFilePath + docScript.getPathLocal().replace("/", File.separator);
            String sPathWSDLLocal = sFilePath + docWSDL.getPathLocal().replace("/", File.separator);

            BufferedReader readerScript = new BufferedReader(new FileReader(sPathScriptLocal));
            BufferedReader readerWSDL = new BufferedReader(new FileReader(sPathWSDLLocal));

            StringBuilder sbScript = new StringBuilder(1024);
            sbScript.append("\n");
            StringBuilder sbWSDL = new StringBuilder(1024);

            String sLine = readerScript.readLine();
            while (sLine != null) {
                sbScript.append(sLine).append("\n");
                sLine = readerScript.readLine();
            }
            sLine = readerWSDL.readLine();
            while (sLine != null) {
                sbWSDL.append(sLine).append("\n");
                sLine = readerWSDL.readLine();
            }
            
            readerScript.close();
            readerWSDL.close();

            outFile.write("   <WEBSERVICE>\n");
            outFile.write(getNodeXML("Name", getName()));
            outFile.write(getNodeXML("Action", "CREATE_OR_UPDATE"));
            outFile.write(getNodeXML("Description", getDescription()));
            outFile.write(getNodeXML("Protocol", getProtocol()));
            outFile.write(getNodeXML("Style", getStyle()));
            outFile.write(getNodeXML("WSDLPath", "archives/wsdl/" + getWsdl()));
            outFile.write(getNodeXML("WSDL", sbWSDL.toString()));
            outFile.write(getNodeXML("ImplementationScriptPath", "scripts/wbs/" + getScript()));
            outFile.write(getNodeXML("ImplementationScript", sbScript.toString()));
            outFile.write(getNodeXML("StoreRequests", "" + storesRequests()));
            outFile.write(getNodeXML("StoreResponses", "" + storesResponses()));
            outFile.write(getNodeXML("Deployed", "" + isDeployed()));
            outFile.write("   </WEBSERVICE>\n");

        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputEntityCSV(Writer outFile, String sOutputPath) throws IOException {
        List<String> line = new ArrayList<>();
        line.add("");
        line.add(getName());
        line.add(getDescription());
        line.add(getProtocol());
        line.add(getStyle());
        line.add(getWsdl());
        line.add(getScript());
        line.add("" + storesRequests());
        line.add("" + storesResponses());
        line.add("" + isDeployed());
        outputCSV(line, outFile);
    }

    /**
     * Retrieve the name of this instance of a web service.
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieve the description of this instance of a web service.
     * @return String
     */
    public String getDescription() {
        return description;
    }

    /**
     * Retrieve the protocol for this instance of a web service.
     * @return String
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * Retrieve the style for this instance of a web service.
     * @return String
     */
    public String getStyle() {
        return style;
    }

    /**
     * Retrieve the WSDL for this instance of a web service.
     * @return String
     */
    public String getWsdl() {
        return wsdl;
    }

    /**
     * Retrieve the script for this instance of a web service.
     * @return String
     */
    public String getScript() {
        return script;
    }

    /**
     * Indicates whether this instance of a web service stores requests (true) or not (false).
     * @return boolean
     */
    public boolean storesRequests() {
        return storeRequest;
    }

    /**
     * Indicates whether this instance of a web service stores responses (true) or not (false).
     * @return boolean
     */
    public boolean storesResponses() {
        return storeResponse;
    }

    /**
     * Indicates whether this instance of a web service is deployed (true) or not (false).
     * @return boolean
     */
    public boolean isDeployed() {
        return deploy;
    }

}
