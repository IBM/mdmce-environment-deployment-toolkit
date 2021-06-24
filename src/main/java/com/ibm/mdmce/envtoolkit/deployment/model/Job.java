/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment.model;

import com.ibm.mdmce.envtoolkit.deployment.BasicEntityHandler;
import com.ibm.mdmce.envtoolkit.deployment.CSVParser;
import com.ibm.mdmce.envtoolkit.deployment.EnvironmentHandler;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Processes <b>Jobs.csv</b>.
 */
public class Job extends BasicEntity {

    public static final String JOB_DESCRIPTION = "Job Description";
    public static final String CREATED_BY = "Created By";
    public static final String EXECUTABLE = "Executable";
    public static final String RECURRING = "Recurring";
    public static final String SCHEDULE_DESCRIPTION = "Schedule Description";
    public static final String SCHEDULE_TYPE = "Schedule Type";
    public static final String NEXT_RUNNING_TIME = "Next Running Time";
    public static final String TIME_WHEN_RAN = "Time When Ran";
    public static final String ENABLED = "Enabled";
    public static final String USER = "User";

    private String jobDescription;
    private String createdBy;
    private String executable;
    private String recurring;
    private List<Map<String, String>> schedules = new ArrayList<>();

    private static class Singleton { private static final Job INSTANCE = new Job(); }

    /**
     * Retrieve the static definition of a Job (ie. its columns and type information).
     * @return Job
     */
    public static Job getInstance() {
        return Job.Singleton.INSTANCE;
    }

    private Job() {
        super("JOBS", "Jobs");
        addColumn(COUNTRY_SPECIFIC);
        addColumn(JOB_DESCRIPTION);
        addColumn(CREATED_BY);
        addColumn(EXECUTABLE);
        addColumn(RECURRING);
        addColumn(SCHEDULE_DESCRIPTION);
        addColumn(SCHEDULE_TYPE);
        addColumn(NEXT_RUNNING_TIME);
        addColumn(TIME_WHEN_RAN);
        addColumn(ENABLED);
        addColumn(USER);
    }

    /**
     * Construct a new instance of a Job using the provided field values.
     * @param <T> expected to be Job whenever used by this class
     * @param aFields from which to construct the Role
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BasicEntity> T createInstance(List<String> aFields) {
        Job j = (Job) BasicEntityHandler.getFromCache(getFieldValue(JOB_DESCRIPTION, aFields), Job.class.getName(), false, false);
        if (j == null) {
            j = new Job();
            j.jobDescription = getFieldValue(JOB_DESCRIPTION, aFields);
            j.createdBy = getFieldValue(CREATED_BY, aFields);
            j.executable = getFieldValue(EXECUTABLE, aFields);
            j.recurring = getFieldValue(RECURRING, aFields);
        }
        Map<String, String> sch = new HashMap<>();
        sch.put(SCHEDULE_DESCRIPTION, getFieldValue(SCHEDULE_DESCRIPTION, aFields));
        sch.put(SCHEDULE_TYPE, getFieldValue(SCHEDULE_TYPE, aFields));

        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        String dateNrt = getFieldValue(NEXT_RUNNING_TIME, aFields);
        String dateTwr = getFieldValue(TIME_WHEN_RAN, aFields);
        try {
            sch.put(NEXT_RUNNING_TIME, ""+df.parse(dateNrt));
        } catch (ParseException e) {
            EnvironmentHandler.logger.warning(". . . WARNING (Problem with parsing Next Running Time. Date format dd.MM.yyyy HH:mm:ss)");
        }
        try {
            sch.put(TIME_WHEN_RAN, ""+df.parse(dateTwr));
        } catch (ParseException e) {
            EnvironmentHandler.logger.warning(". . . WARNING (Problem with parsing Time When Ran. Date format dd.MM.yyyy HH:mm:ss)");
        }

        boolean enbl = CSVParser.checkBoolean(getFieldValue(ENABLED, aFields));
        if (enbl) {
            sch.put(ENABLED, "E");
        } else {
            sch.put(ENABLED, "D");
        }
        sch.put(USER, getFieldValue(USER, aFields));
        j.schedules.add(sch);
        return (T) j;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUniqueId() {
        return getJobDescription();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputEntityXML(BasicEntityHandler handler, Writer outFile, String sOutputPath, String sCompanyCode) throws IOException {
        outFile.write("   <JOBS>\n");
        outFile.write(getNodeXML("Description", getJobDescription()));
        outFile.write(getNodeXML("CreatedBy", getCreatedBy()));
        outFile.write(getNodeXML("Action", "CREATE_OR_UPDATE"));
        outFile.write(getNodeXML("Executable", getExecutable()));
        outFile.write(getNodeXML("Recurring", getRecurring()));
        outFile.write("      <SchedulesList>\n");
        for (Map<String, String> sch : getSchedules()) {
            outFile.write("         <Schedule>\n");
            outFile.write("      " + getNodeXML("Description", sch.get(SCHEDULE_DESCRIPTION)));
            outFile.write("      " + getNodeXML("ScheduleType", sch.get(SCHEDULE_TYPE)));
            outFile.write("      " + getNodeXML("NextRunningTime", sch.get(NEXT_RUNNING_TIME)));
            outFile.write("      " + getNodeXML("TimeWhenRan", sch.get(TIME_WHEN_RAN)));
            outFile.write("      " + getNodeXML("Enabled", sch.get(ENABLED)));
            outFile.write("      " + getNodeXML("User", sch.get(USER)));
            outFile.write("         </Schedule>\n");
        }
        outFile.write("      </SchedulesList>\n");
        outFile.write("   </JOBS>\n");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputEntityCSV(Writer outFile, String sOutputPath) throws IOException {
        List<String> line = new ArrayList<>();
        line.add("");
        line.add(getJobDescription());
        line.add(getCreatedBy());
        line.add(getExecutable());
        line.add(getRecurring());
        for (Map<String, String> sch : getSchedules()) {
            List<String> outLine = new ArrayList<>();
            outLine.addAll(line);
            outLine.add(sch.get(SCHEDULE_DESCRIPTION));
            outLine.add(sch.get(SCHEDULE_TYPE));
            outLine.add(sch.get(NEXT_RUNNING_TIME));
            outLine.add(sch.get(TIME_WHEN_RAN));
            outLine.add(sch.get(ENABLED));
            outLine.add(sch.get(USER));
            outputCSV(outLine, outFile);
        }
    }

    /**
     * Retrieve the Job Description of this instance of a job.
     * @return String
     */
    public String getJobDescription() {
        return jobDescription;
    }

    /**
     * Retrieve the Created By of this instance of a job.
     * @return String
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Retrieve the Executable By of this instance of a job.
     * @return String
     */
    public String getExecutable() {
        return executable;
    }

    /**
     * Retrieve the Executable By of this instance of a job.
     * @return String
     */
    public String getRecurring() {
        return recurring;
    }

    /**
     * Retrieve the Executable By of this instance of a job.
     * @return List<Map<String, String>>
     */
    public List<Map<String, String>> getSchedules() {
        return schedules;
    }
}
