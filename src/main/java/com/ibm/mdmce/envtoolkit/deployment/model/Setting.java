/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment.model;

import com.ibm.mdmce.envtoolkit.deployment.BasicEntityHandler;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Processes <b>Settings.csv</b>.
 */
public class Setting extends BasicEntity {

    public static final String USERNAME = "Username";
    public static final String SETTINGNAME = "Setting";
    public static final String SETTINGINSTANCE = "Instance";
    public static final String SETTINGVALUE = "Value";
    
    private String username;
    private String settingName;
    private String settingInstance;
    private String settingValue;

    private static class Singleton {
        private static final Setting INSTANCE = new Setting();
    }

    /**
     * Retrieve the static definition of an Setting (ie. its columns and type information).
     * @return Setting
     */
    public static Setting getInstance() {
        return Setting.Singleton.INSTANCE;
    }

    private Setting() {
        super("SETTINGS", "Settings");
        addColumn(COUNTRY_SPECIFIC);
        addColumn(USERNAME);
        addColumn(SETTINGNAME);
        addColumn(SETTINGINSTANCE);
        addColumn(SETTINGVALUE);
    }

    /**
     * Construct a new instance of a Setting using the provided field values.
     * @param <T> expected to be Setting whenever used by this class
     * @param aFields from which to construct the Setting
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BasicEntity> T createInstance(List<String> aFields) {

        Setting setting = new Setting();

        setting.username = getFieldValue(USERNAME, aFields);
        setting.settingName = getFieldValue(SETTINGNAME, aFields);
        setting.settingInstance = getFieldValue(SETTINGINSTANCE, aFields);
        setting.settingValue = getFieldValue(SETTINGVALUE, aFields);
        
        return (T) setting;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUniqueId() {
        return getUsername() + "::" + getSettingName() + "::" + getSettingInstance();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputEntityXML(BasicEntityHandler handler, Writer outFile, String sOutputPath, String sCompanyCode) throws IOException {
        outFile.write("<MY_SETTINGS>\n");
		outFile.write("     <SETTING>\n");        
        outFile.write(getNodeXML("Username", getUsername()));
        outFile.write(getNodeXML("Setting", getSettingName()));
        if(getSettingInstance().equals("")){
			outFile.write(getNodeXML("Instance", "DEFAULT_INSTANCE"));
		}else{
			outFile.write(getNodeXML("Instance", getSettingInstance()));	
		}
        outFile.write(getNodeXML("Value", getSettingValue()));
        outFile.write(getNodeXML("UserSettable","-"));
        outFile.write("     </SETTING>\n");
		outFile.write("</MY_SETTINGS>\n");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputEntityCSV(Writer outFile, String sOutputPath) throws IOException {
        List<String> line = new ArrayList<>();
        line.add("");
        line.add(getUsername());
        line.add(getSettingName());
        line.add(getSettingInstance());
        line.add(getSettingValue());
        outputCSV(line, outFile);
    }

    /**
     * Retrieve the username of this instance of a setting.
     * @return String
     */
    public String getUsername() {
        return username;
    }

    /**
     * Retrieve the setting name of this instance of a setting.
     * @return String
     */
    public String getSettingName() {
        return settingName;
    }

    /**
     * Retrieve the instance of this instance of a setting.
     * @return String
     */
    public String getSettingInstance() {
        return settingInstance;
    }

    /**
     * Retrieve the value of this instance of a setting.
     * @return String
     */
    public String getSettingValue() {
        return settingValue;
    }
}
