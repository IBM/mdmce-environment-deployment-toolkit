<!-- SPDX-License-Identifier: CC-BY-4.0 -->
<!-- Copyright IBM Corp. 2007-2020 -->

# Scripts.csv

Does not currently fully support input parameters and value sets; hope to resolve in the future.

## Columns

1. **Script path (relative to "loadToEnv")** - path to this script, relative to the `FILES/loadToEnv` directory of the filesystem on which the deployment files all reside
1. **Script name** - name of the script (or value set, if Type is `INPUT_PARAM`) as it should be saved within MDM-CE
1. **Script type** - type of script being defined
1. **File spec / Destination spec name (imports / exports)** - name of the file spec (if Type is `CTG_IMPORT`) or destination spec (if Type is `CTG_EXPORT`) for this script
1. **Input spec name (if any)** - name of the input spec for this script (if any; required for Type `REPORT`); name of the script input spec for this value set (if Type is `INPUT_PARAM`)
1. **Character set** - encoding of the script file (`UTF-8`, `Cp1252`, etc)
1. **ASP / JSP-like** - indicates whether the script being defined is an ASP/JSP-like script (true) or not
1. **Container name (ctr / ctg / workflow scripts)** - name of the container this script should be available for (if Type is `CTG` or `CTR`); or `$ALL` for all containers of the specified type; for Type `WORKFLOW` should give the workflow name; or a comma separated list of `AttributePath|Value` in case of a value set (Type is `INPUT_PARAM`)
1. **Container type (entry build / preview / macro scripts)** - type of container (`CATALOG` or `CATEGORY_TREE`) for which this script is accessible (if Type is `ENTRY_PREVIEW`, `ENTRY_MACRO`, or `ENTRY_BUILD`)

### Valid script types

- `AGGREGATE_UPDATE` - MassUpdate
- `CTG_DIFF_EXPORT` - Difference export
- `CTG_EXPORT` - catalogue export script
- `CTG_IMPORT` - catalogue import script
- `ENTRY_MACRO` - entry macro script
- `CTG` - catalogue script; for pre-processing, post-processing or post-save
- `CATALOG` - (same as above)
- `CTG_MACRO` - Macro script (use `ENTRY_MACRO` instead)
- `ENTRY_PREVIEW` - entry preview script
- `CATALOG_PREVIEW` - Catalog preview (use `ENTRY_PREVIEW` instead)
- `CTR` - hierarchy script; for pre-processing, post-processing, or post-save
- `CATEGORY_TREE` - (same as above)
- `CTLG_CTLG_EXPORT` - (unknown)
- `CTR_IMPORT` - category tree (hierarchy) import script
- `DISTRIBUTION` - Custom distribution script
- `ENTRY_BUILD` - entry build script
- `IMG_DIFF_EXPORT` - (unknown)
- `IMG_EXPORT` - (unknown)
- `LKP_IMPORT` - (unknown)
- `PO_EXPORT` - (unknown)
- `PO_IMPORT` - (unknown)
- `PO_STATUS_REQUEST` - (unknown)
- `PO_STATUS_UPDATE_IMPORT` - (unknown)
- `REPORT` - report script
- `SECURE_TRIGGER` - secure trigger script
- `TRIGGER` - trigger script (insecure)
- `WIDGETS` - (unknown)
- `WORKFLOW` - workflow script
- `TRIGO_APP` - custom tool script
- `QMSG_PROCESSOR` - (unknown)
- `ENTITY_SYNCHRONIZATION` - (unknown)
- `WBS` - WebServices Implementation script
- `LOGIN` - hook-in during login events (NOTE: may need to name script exactly `Login.wpcs`)
- `LOGOUT` - hook-in during logout events (NOTE: may need to name script exactly `Logout.wpcs`)
- `LDAP_USR_FETCH` - hook-in during LDAP fetch requests (NOTE: may need to name script exactly `LDAPUserDataFetch.wpcs`)
- `SEARCH_RESULT_REPORT` - the new Rich Search Result Report Script added in 5.3.2
- `DOCUMENT` - a general document that should not be interpreted as a script
- `INPUT_PARAM` - input parameter "script" (document)

----
License: [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/),
Copyright IBM Corp. 2007-2020.
