/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment.model;

import com.ibm.mdmce.envtoolkit.deployment.BasicEntityHandler;
import com.ibm.mdmce.envtoolkit.deployment.CSVParser;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * Processes <b>Workflows.csv</b>.
 */
public class Workflow extends BasicEntity {

    public static final String NAME = "Workflow Name";
    public static final String DESCRIPTION = "Workflow Description";
    public static final String ACG = "ACG";
    public static final String CONTAINER_TYPE = "Container Type";
    public static final String STEP_NAME = "Step Name";
    public static final String STEP_DESC = "Step Description";
    public static final String STEP_TYPE = "Step Type";
    public static final String EXIT_VALUES = "Exit Value to Next Steps";
    public static final String PERFORMER_ROLES = "Performers (Roles)";
    public static final String PERFORMER_USERS = "Performers (Users)";
    public static final String REQ_ATTR_COLLECTIONS = "Required Attribute Collections";
    public static final String TIMEOUT = "TimeOut Duration (seconds)";
    public static final String ALLOW_IMPORT = "Allow import?";
    public static final String ALLOW_RECAT = "Allow recategorize?";
    public static final String RESERVE_TO_EDIT = "Reserve to Edit?";
    public static final String EMAILS_ON_ENTRY = "Emails on entry";
    public static final String EMAILS_ON_TIMEOUT = "Emails on timeout";
    public static final String INCLUDE_SCRIPT = "Include script?";

    private static final String[] DEFAULT_STEPS = { "INITIAL", "SUCCESS", "FAILURE" };
    private static final String[] SUBVIEW_TYPES = { "ItemEdit", "BulkEdit", "ItemPopup" };

    private String name;
    private String description;
    private String acg = com.ibm.mdmce.envtoolkit.deployment.model.ACG.DEFAULT_ACG;
    private String containerType = "CATALOG";

    public Map<String, WorkflowStep> steps = new HashMap<>();

    private static class Singleton {
        private static final Workflow INSTANCE = new Workflow();
    }

    /**
     * Retrieve the static definition of a Workflow (ie. its columns and type information).
     * @return Workflow
     */
    public static Workflow getInstance() {
        return Workflow.Singleton.INSTANCE;
    }

    private Workflow() {
        super("WORKFLOW", "Workflows");
        addColumn(COUNTRY_SPECIFIC);
        addColumn(NAME);
        addColumn(DESCRIPTION);
        addColumn(ACG);
        addColumn(CONTAINER_TYPE);
        addColumn(STEP_NAME);
        addColumn(STEP_DESC);
        addColumn(STEP_TYPE);
        addColumn(EXIT_VALUES);
        addColumn(PERFORMER_ROLES);
        addColumn(PERFORMER_USERS);
        addColumn(REQ_ATTR_COLLECTIONS);
        addColumn(TIMEOUT);
        addColumn(ALLOW_IMPORT);
        addColumn(ALLOW_RECAT);
        addColumn(RESERVE_TO_EDIT);
        addColumn(EMAILS_ON_ENTRY);
        addColumn(EMAILS_ON_TIMEOUT);
        addColumn(INCLUDE_SCRIPT);
    }

    /**
     * Construct a new instance of a Workflow using the provided field values.
     * @param <T> expected to be Workflow whenever used by this class
     * @param aFields from which to construct the Workflow
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BasicEntity> T createInstance(List<String> aFields) {

        String sWflName = getFieldValue(NAME, aFields);
        Workflow wfl = (Workflow) BasicEntityHandler.getFromCache(sWflName, Workflow.class.getName(), false, false);
        if (wfl == null) {
            wfl = new Workflow();
            wfl.name = sWflName;
            wfl.description = getFieldValue(DESCRIPTION, aFields);
            wfl.addDefaultSteps();
        }
        wfl.acg = getFieldValue(ACG, aFields);
        wfl.containerType = getFieldValue(CONTAINER_TYPE, aFields);

        WorkflowStep wflStep = new WorkflowStep();
        String sStepName = getFieldValue(STEP_NAME, aFields);
        wflStep.name = sStepName;
        wflStep.type = getFieldValue(STEP_TYPE, aFields);
        wflStep.description = getFieldValue(STEP_DESC, aFields);
        wflStep.timeout = CSVParser.checkInteger(getFieldValue(TIMEOUT, aFields), 0);
        wflStep.allowImport = CSVParser.checkBoolean(getFieldValue(ALLOW_IMPORT, aFields));
        wflStep.allowRecategorization = CSVParser.checkBoolean(getFieldValue(ALLOW_RECAT, aFields));
        wflStep.reserveToEdit = CSVParser.checkBoolean(getFieldValue(RESERVE_TO_EDIT, aFields));
        wflStep.includeScript = CSVParser.checkBoolean(getFieldValue(INCLUDE_SCRIPT, aFields));

        String sExitValues = getFieldValue(EXIT_VALUES, aFields);
        String[] aExitMappings = sExitValues.split("\n");
        for (String sExitMapping : aExitMappings) {
            String[] aMapping = sExitMapping.split("=");
            if (aMapping.length == 2)
                wfl.addStepMapping(wflStep, aMapping[0], aMapping[1]);
        }

        wflStep.performerRoles = CSVParser.checkList(getFieldValue(PERFORMER_ROLES, aFields), ",");
        wflStep.performerUsers = CSVParser.checkList(getFieldValue(PERFORMER_USERS, aFields), ",");
        wflStep.requiredAttributeCollections = CSVParser.checkList(getFieldValue(REQ_ATTR_COLLECTIONS, aFields), ",");
        wflStep.emailsOnEntry = CSVParser.checkList(getFieldValue(EMAILS_ON_ENTRY, aFields), ",");
        wflStep.emailsOnTimeout = CSVParser.checkList(getFieldValue(EMAILS_ON_TIMEOUT, aFields), ",");

        wfl.steps.put(sStepName, wflStep);

        return (T) wfl;

    }

    private void addStepMapping(WorkflowStep wflStep, String sExitValue, String sNextStep) {
        List<String> alNextSteps = wflStep.getExitValueToNextSteps().getOrDefault(sExitValue, new ArrayList<>());
        alNextSteps.add(sNextStep);
        wflStep.getExitValueToNextSteps().put(sExitValue, alNextSteps);
    }

    private void addDefaultSteps() {
        for (String stepName : DEFAULT_STEPS) {
            WorkflowStep wflStep = new WorkflowStep();
            wflStep.name = stepName;
            wflStep.type = stepName;
            wflStep.getExitValueToNextSteps().put(stepName, new ArrayList<>());
            steps.put(stepName, wflStep);
        }
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
        String sWflName = getName();
        outFile.write("   <WORKFLOW>\n");
        outFile.write(getNodeXML("Name", sWflName));
        outFile.write(getNodeXML("Action", "CREATE_OR_UPDATE"));
        outFile.write(getNodeXML("Desc", getDescription()));
        if (getAcg().equals(com.ibm.mdmce.envtoolkit.deployment.model.ACG.DEFAULT_ACG))
            outFile.write("      <ACG isDefault=\"true\"/>\n");
        else
            outFile.write(getNodeXML("ACG", getAcg()));
        outFile.write(getNodeXML("ContainerType", getContainerType()));
        Script doc = (Script) BasicEntityHandler.getFromCache("/workflow/gui/" + sWflName + ".html", Script.class.getName(), false, false);
        if (doc != null)
            outFile.write(getNodeXML("GUIDocStorePath", "/workflow/gui/" + sWflName + ".html"));
        else
            outFile.write(getNodeXML("GUIDocStorePath", ""));
        for (WorkflowStep step : getSteps().values()) {
            outputStepXML(step, sWflName, outFile);
        }
        outFile.write("   </WORKFLOW>\n");
    }

    private void outputStepXML(WorkflowStep wflStep, String sWflName, Writer outWfl) throws IOException {

        String sWflStepName = wflStep.getName();

        WorkflowStepView wflView = (WorkflowStepView) BasicEntityHandler.getFromCache(sWflName + "::" + sWflStepName, WorkflowStepView.class.getName(), false, false);

        outWfl.write("      <Step>\n");
        outWfl.write(getNodeXML("StepName", sWflStepName));
        outWfl.write(getNodeXML("StepType", wflStep.getType()));
        outWfl.write(getNodeXML("StepDesc", wflStep.getDescription()));
        outWfl.write(getNodeXML("AllowEntries", (wflStep.allowsImport() ? "true" : "false") ));
        outWfl.write(getNodeXML("AllowRe-categorization", (wflStep.allowsRecategorization() ? "true" : "false") ));
        outWfl.write(getNodeXML("ReserveToEdit", (wflStep.isReserveToEdit() ? "true" : "false") ));
        outWfl.write(getNodeXML("TimeoutType", "DURATION"));
        outWfl.write(getNodeXML("Timeout", "" + (wflStep.getTimeout() * 1000)));
        // TODO: EntryNotifications
        outWfl.write(getNodeXML("EntryNotifications", ""));
        // TODO: TimeoutNotifications
        outWfl.write(getNodeXML("TimeoutNotifications", ""));

        outWfl.write("         <RequiredAttributesCollections>\n");
        for (String sSubViewType : SUBVIEW_TYPES) {
            outWfl.write("            <"+sSubViewType+">\n");
            for (String sAttrColName : wflStep.getRequiredAttributeCollections()) {
                outWfl.write("               <AttributeCollection><![CDATA[" + sAttrColName + "]]></AttributeCollection>\n");
            }
            outWfl.write("            </"+sSubViewType+">\n");
        }
        outWfl.write("         </RequiredAttributesCollections>\n");

        if (wflView != null)
            wflView.outputAttrColXML(outWfl);

        outWfl.write("         <ContainerViews>\n");
        if (wflView != null)
            wflView.outputViewComponent(outWfl);
        outWfl.write("         </ContainerViews>\n");

        outWfl.write("         <Performers>\n");
        for (String role : wflStep.getPerformerRoles()) {
            outWfl.write("             <Roles><![CDATA[" + role + "]]></Roles>\n");
        }
        for (String user : wflStep.getPerformerUsers()) {
            outWfl.write("             <Users><![CDATA[" + user + "]]></Users>\n");
        }
        outWfl.write("         </Performers>\n");
        if (wflStep.includesScript())
            outWfl.write("        <ScriptPath>scripts/workflow/" + sWflName + "/" + wflStep.getName() + "</ScriptPath>\n");

        outWfl.write("         <ExitValues>\n");
        for (String sExitValue : wflStep.getExitValueToNextSteps().keySet()) {
            outWfl.write("            <ExitValue><![CDATA[" + sExitValue + "]]></ExitValue>\n");
        }
        outWfl.write("         </ExitValues>\n");
        if (!wflStep.getName().equals("SUCCESS") && !wflStep.getName().equals("FAILURE")) {
            outWfl.write("         <StepMaps>\n");
            for (Map.Entry<String, List<String>> entry : wflStep.getExitValueToNextSteps().entrySet()) {
                String sExitValue = entry.getKey();
                List<String> alNextSteps = entry.getValue();
                for (String next : alNextSteps) {
                    outWfl.write("            <NextStep ExitValue=\"" + sExitValue + "\"><![CDATA[" + next + "]]></NextStep>\n");
                }
            }
            outWfl.write("         </StepMaps>\n");
        }

        outWfl.write("      </Step>\n");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputEntityCSV(Writer outFile, String sOutputPath) throws IOException {

        for (Map.Entry<String, WorkflowStep> entry : getSteps().entrySet()) {
            List<String> line = new ArrayList<>();
            String sStepName = entry.getKey();
            WorkflowStep step = entry.getValue();
            line.add("");
            line.add(getName());
            line.add(getDescription());
            line.add(getAcg());
            line.add(getContainerType());
            line.add(sStepName);
            line.add(step.getDescription());
            line.add(step.getType());
            line.add("???"); // TODO: exit values to next steps
            line.add(escapeForCSV(String.join(",", step.getPerformerRoles())));
            line.add(escapeForCSV(String.join(",", step.getPerformerUsers())));
            line.add(escapeForCSV(String.join(",", step.getRequiredAttributeCollections())));
            line.add("" + step.getTimeout());
            line.add( (step.allowsImport() ? "yes" : "no") );
            line.add( (step.allowsRecategorization() ? "yes" : "no") );
            line.add( (step.isReserveToEdit() ? "yes" : "no") );
            line.add(escapeForCSV(String.join(",", step.getEmailsOnEntry())));
            line.add(escapeForCSV(String.join(",", step.getEmailsOnTimeout())));
            line.add( (step.includesScript() ? "yes" : "no") );
            outputCSV(line, outFile);
        }

    }

    /**
     * Retrieve the name of this instance of a workflow.
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieve the description of this instance of a workflow.
     * @return String
     */
    public String getDescription() {
        return description;
    }

    /**
     * Retrieve the access control group for this instance of a workflow.
     * @return String
     */
    public String getAcg() {
        return acg;
    }

    /**
     * Retrieve the container type allowed for this instance of a workflow.
     * @return String
     */
    public String getContainerType() {
        return containerType;
    }

    /**
     * Retrieve the mapping from step name to step details for this instance of a workflow.
     * @return {@code Map<String, WorkflowStep>}
     */
    public Map<String, WorkflowStep> getSteps() {
        return steps;
    }

    /**
     * Internal class to represent the details of each row in the Workflow CSV.
     */
    public static class WorkflowStep {

        private String name;
        private String description = "";
        private String type;
        private int timeout = 0;
        private boolean allowImport = false;
        private boolean allowRecategorization = false;
        private boolean reserveToEdit = false;
        private boolean includeScript = false;

        private Map<String, List<String>> exitValueToNextSteps = new HashMap<>();
        private List<String> performerRoles = new ArrayList<>();
        private List<String> performerUsers = new ArrayList<>();
        private List<String> emailsOnEntry = new ArrayList<>();
        private List<String> emailsOnTimeout = new ArrayList<>();

        private List<String> requiredAttributeCollections = new ArrayList<>();

        /**
         * Retrieve the name of this instance of a workflow step.
         * @return String
         */
        public String getName() {
            return name;
        }

        /**
         * Retrieve the description of this instance of a workflow step.
         * @return String
         */
        public String getDescription() {
            return description;
        }

        /**
         * Retrieve the type of this instance of a workflow step.
         * @return String
         */
        public String getType() {
            return type;
        }

        /**
         * Retrieve the timeout for this instance of a workflow step (or 0 if no timeout is defined).
         * @return int
         */
        public int getTimeout() {
            return timeout;
        }

        /**
         * Indicates whether this instance of a workflow step allows import directly into it (true) or not (false).
         * @return boolean
         */
        public boolean allowsImport() {
            return allowImport;
        }

        /**
         * Indicates whether this instance of a workflow step allows recategorization of the items within it (true)
         * or not (false).
         * @return boolean
         */
        public boolean allowsRecategorization() {
            return allowRecategorization;
        }

        /**
         * Indicates whether this instance of a workflow step needs items to be reserved before they can be edited
         * (true) or not (false).
         * @return boolean
         */
        public boolean isReserveToEdit() {
            return reserveToEdit;
        }

        /**
         * Indicates whether this instance of a workflow step includes a script (true) or not (false).
         * @return boolean
         */
        public boolean includesScript() {
            return includeScript;
        }

        /**
         * Retrieves the mapping from exit value to the next steps for that exit value for this instance of a workflow
         * step.
         * @return {@code Map<String, List<String>>}
         */
        public Map<String, List<String>> getExitValueToNextSteps() {
            return exitValueToNextSteps;
        }

        /**
         * Retrieves the roles that are allowed to perform this instance of a workflow step.
         * @return {@code List<String>}
         */
        public List<String> getPerformerRoles() {
            return performerRoles;
        }

        /**
         * Retrieves the users that are allowed to perform this instance of a workflow step.
         * @return String[]
         */
        public List<String> getPerformerUsers() {
            return performerUsers;
        }

        /**
         * Retrieves the emails that are notified on any items entering this instance of a workflow step.
         * @return {@code List<String>}
         */
        public List<String> getEmailsOnEntry() {
            return emailsOnEntry;
        }

        /**
         * Retrieves the emails that are notified on any items timing out in this instance of a workflow step.
         * @return {@code List<String>}
         */
        public List<String> getEmailsOnTimeout() {
            return emailsOnTimeout;
        }

        /**
         * Retrieves the required attribute collections for this instance of a workflow step.
         * @return {@code List<String>}
         */
        public List<String> getRequiredAttributeCollections() {
            return requiredAttributeCollections;
        }

    }

}
