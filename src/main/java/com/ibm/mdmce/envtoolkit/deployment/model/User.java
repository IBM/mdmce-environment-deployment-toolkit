/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment.model;

import com.ibm.mdmce.envtoolkit.deployment.BasicEntityHandler;
import com.ibm.mdmce.envtoolkit.deployment.CSVParser;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Processes <b>Users.csv</b>.
 */
public class User extends BasicEntity {

    public static final String USERNAME = "Username";
    public static final String FIRSTNAME = "First name";
    public static final String LASTNAME = "Last name";
    public static final String EMAIL_ADDRESS = "Email address";
    public static final String ADDRESS = "Address";
    public static final String PHONE_NUMBER = "Phone number";
    public static final String FAX_NUMBER = "Fax number";
    public static final String ENABLED = "Enabled?";
    public static final String ROLES = "Roles";
    public static final String ORGANIZATIONS = "Organizations";
    public static final String LDAP_ENABLED = "LDAP Enabled?";
    public static final String LDAP_ENTRY_DN = "LDAP Entry DN";
    public static final String LDAP_SERVER_URL = "LDAP Server URL";
    public static final String PASSWORD = "Password";

    private String username;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String address;
    private String phoneNumber;
    private String faxNumber;
    private boolean enabled = false;
    private boolean ldapEnabled = false;
    private String ldapEntryDN;
    private String ldapServerURL;
    private String password;

    private List<String> roles = new ArrayList<>();
    private List<String> organizations = new ArrayList<>();

    private static class Singleton {
        private static final User INSTANCE = new User();
    }

    /**
     * Retrieve the static definition of an User (ie. its columns and type information).
     * @return User
     */
    public static User getInstance() {
        return User.Singleton.INSTANCE;
    }

    private User() {
        super("USERS", "Users");
        addColumn(COUNTRY_SPECIFIC);
        addColumn(USERNAME);
        addColumn(FIRSTNAME);
        addColumn(LASTNAME);
        addColumn(EMAIL_ADDRESS);
        addColumn(ADDRESS);
        addColumn(PHONE_NUMBER);
        addColumn(FAX_NUMBER);
        addColumn(ENABLED);
        addColumn(ROLES);
        addColumn(ORGANIZATIONS);
        addColumn(LDAP_ENABLED);
        addColumn(LDAP_ENTRY_DN);
        addColumn(LDAP_SERVER_URL);
        addColumn(PASSWORD);
    }

    /**
     * Construct a new instance of an Access Control Group using the provided field values.
     * @param <T> expected to be AccessControlGroup whenever used by this class
     * @param aFields from which to construct the Access Control Group
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BasicEntity> T createInstance(List<String> aFields) {

        User user = new User();

        user.username = getFieldValue(USERNAME, aFields);
        user.firstName = getFieldValue(FIRSTNAME, aFields);
        user.lastName = getFieldValue(LASTNAME, aFields);
        user.emailAddress = getFieldValue(EMAIL_ADDRESS, aFields);
        user.address = getFieldValue(ADDRESS, aFields);
        user.phoneNumber = getFieldValue(PHONE_NUMBER, aFields);
        user.faxNumber = getFieldValue(FAX_NUMBER, aFields);
        user.enabled = CSVParser.checkBoolean(getFieldValue(ENABLED, aFields));
        user.ldapEnabled = CSVParser.checkBoolean(getFieldValue(LDAP_ENABLED, aFields));
        user.ldapEntryDN = getFieldValue(LDAP_ENTRY_DN, aFields);
        user.ldapServerURL = getFieldValue(LDAP_SERVER_URL, aFields);
        user.password = getFieldValue(PASSWORD, aFields);

        String sRoles = getFieldValue(ROLES, aFields);
        String[] aRoles = sRoles.split(",");
        user.roles.addAll(Arrays.asList(aRoles));

        String sOrgs = getFieldValue(ORGANIZATIONS, aFields);
        String[] aOrgs = sOrgs.split(",");
        user.organizations.addAll(Arrays.asList(aOrgs));

        return (T) user;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUniqueId() {
        return getUsername();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputEntityXML(BasicEntityHandler handler, Writer outFile, String sOutputPath, String sCompanyCode) throws IOException {

        outFile.write("   <USERS>\n");
        outFile.write(getNodeXML("Username", getUsername()));
        outFile.write(getNodeXML("FirstName", getFirstName()));
        outFile.write(getNodeXML("LastName", getLastName()));
        outFile.write(getNodeXML("Action", "CREATE_OR_UPDATE"));
        outFile.write(getNodeXML("Email", getEmailAddress()));
        outFile.write(getNodeXML("Fax", getFaxNumber()));
        outFile.write(getNodeXML("Phone", getPhoneNumber()));
        outFile.write(getNodeXML("Address", getAddress()));
        outFile.write(getNodeXML("Active", "" + isEnabled()));
        outFile.write(getNodeXML("LdapEnabled", "" + isLdapEnabled()));
        // NOTE: Empty password hard-coded to "trinitron" in encrypted form
        outFile.write(getNodeXML("Password",getPassword()));
        outFile.write(getNodeXML("LdapEntryDn", getLdapEntryDN()));
        outFile.write(getNodeXML("LdapServerUrl", getLdapServerURL()));

        if (getRoles().isEmpty()) {
            outFile.write(getNodeXML("Role", ""));
        } else {
            for (String role : getRoles()) {
                outFile.write(getNodeXML("Role", role.replace("$CMP", sCompanyCode)));
            }
        }

        if (getOrganizations().isEmpty()) {
            outFile.write(getNodeXML("OrganizationHierarchy", "##DefaultOrganizationHierarchy##/##DefaultOrganization##"));
        } else {
            for (String org : getOrganizations()) {
                outFile.write(getNodeXML("OrganizationHierarchy", org));
            }
        }

        outFile.write(getNodeXML("CompanyCode", sCompanyCode));
        outFile.write("   </USERS>\n");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputEntityCSV(Writer outFile, String sOutputPath) throws IOException {
        List<String> line = new ArrayList<>();
        line.add("");
        line.add(getUsername());
        line.add(getFirstName());
        line.add(getLastName());
        line.add(getEmailAddress());
        line.add(getAddress());
        line.add(getPhoneNumber());
        line.add(getFaxNumber());
        line.add("" + isEnabled());
        line.add(escapeForCSV(String.join(",", getRoles())));
        line.add(escapeForCSV(String.join(",", getOrganizations())));
        line.add("" + isLdapEnabled());
        line.add(getLdapEntryDN());
        line.add(getLdapServerURL());
        line.add(getPassword());
        outputCSV(line, outFile);
    }

    /**
     * Retrieve the username of this instance of a user.
     * @return String
     */
    public String getUsername() {
        return username;
    }

    /**
     * Retrieve the first name of this instance of a user.
     * @return String
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Retrieve the last name of this instance of a user.
     * @return String
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Retrieve the email address of this instance of a user.
     * @return String
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Retrieve the fax number of this instance of a user.
     * @return String
     */
    public String getFaxNumber() {
        return faxNumber;
    }

    /**
     * Retrieve the phone number of this instance of a user.
     * @return String
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Retrieve the address of this instance of a user.
     * @return String
     */
    public String getAddress() {
        return address;
    }

    /**
     * Indicates whether this instance of a user is enabled (true) or not (false).
     * @return boolean
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Indicates whether this instance of a user is LDAP-enabled (true) or not (false).
     * @return boolean
     */
    public boolean isLdapEnabled() {
        return ldapEnabled;
    }

    /**
     * Retrieve the LDAP distinguished name of this instance of a user.
     * @return String
     */
    public String getLdapEntryDN() {
        return ldapEntryDN;
    }

    /**
     * Retrieve the LDAP server URL for this instance of a user.
     * @return String
     */
    public String getLdapServerURL() {
        return ldapServerURL;
    }

    /**
     * Retrieve the roles for this instance of a user.
     * @return {@code List<String>}
     */
    public List<String> getRoles() {
        return roles == null ? Collections.emptyList() : roles;
    }

    /**
     * Retrieve the organizations for this instance of a user.
     * @return {@code List<String>}
     */
    public List<String> getOrganizations() {
        return organizations == null ? Collections.emptyList() : organizations;
    }

    /**
     * Retrieve the password for this user.
     * @return String
     */
    public String getPassword() {
        return password == null ||  (password != null && password.isEmpty()) ? "447e4ec3f5804e78d7f952eb359a71e5" : password;
    }

}
