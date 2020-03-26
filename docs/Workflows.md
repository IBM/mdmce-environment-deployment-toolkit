<!-- SPDX-License-Identifier: CC-BY-4.0 -->
<!-- Copyright IBM Corp. 2007-2020 -->

# Workflows.csv

A given workflow will span multiple lines in the CSV file, each line defining a
single step that should be included in the workflow. The processing will expect
that all lines relevant to one workflow appear next to each other in the file
(ie. that the file is sorted by Workflow Name).

## Columns

1. **CS?** - indicates whether the line should be considered as a template (true) or a single object
1. **Workflow Name** - name of the workflow
1. **Workflow Description** - description of the workflow
1. **ACG** - access control group with which to associate the workflow
1. **Container Type** - type of container for which the workflow should allow entries
1. **Step Name** - name of the workflow step
1. **Step Description** - description of the workflow step (NOTE: this should not contain any newlines or the GUI editor for workflows will fail to operate)
1. **Step Type** - type of the workflow step (see below for list of valid values)
1. **Exit Value to Next Steps** - mappings from exit value to next steps, where each mapping is a separate line (CTRL-Enter within Excel) and is defined as `ExitValue=NextStepName`
1. **Performers (Roles)** - comma-separated listing of the roles that have access to perform this workflow step
1. **Performers (Users)** - comma-separated listing of the users that have access to perform this workflow step
1. **Required Attribute Collections** - comma-separated listing of the attribute collections for which a value must be provided before an entry can leave this workflow step
1. **TimeOut Duration (seconds)** - amount of time (in seconds) after which this workflow step should time-out
1. **Allow import?** - indicates whether it should be possible to import directly into this workflow step (true) or not
1. **Allow recategorize?** - indicates whether it should be possible to recategorize entries in this workflow step (true) or not
1. **Reserve to Edit?** - indicates whether it should be necessary to reserve an entry in order to edit it (true) or not
1. **Emails on entry** - comma-separated list of e-mail addresses that should receive a notification when entries enter this workflow step
1. **Emails on timeout** - comma-separated list of e-mail addresses that should receive a notification when entries time-out in this workflow step
1. **Include script?** - indicates whether a script should be attached to this step (true) or not

### Valid step types

- `INITIAL` - initial (default) step
- `FAILURE` - failure (default) step
- `SUCCESS` - success (default) step
- `FIXIT` - fixit (default) step
- `AND_APPROVAL` - approval step where all must approve
- `OR_APPROVAL` - approval step where at least 1 must approve
- `MODIFY` - modification step expecting user interaction
- `DISPATCH` - dispatch step
- `MERGE` - merge step
- `GENERAL` - the "catch-all" step
- `AUTOMATED` - automated (scripted) step
- `WAIT` - wait step
- `MAKE_UNIQUE` - enforce uniqueness step
- `PARTIAL_UNDO` - partial undo step (revert those attributes in the specified attribute groups to their values in the source container)
- `CONDENSER` - condenser step
- `NESTED_WORKFLOW` - nested workflow step

### Valid view types

- `ItemEdit` - item single edit screen
- `BulkEdit` - item multi-edit screen
- `ItemPopup` - item popup screen

----
License: [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/),
Copyright IBM Corp. 2007-2020.
