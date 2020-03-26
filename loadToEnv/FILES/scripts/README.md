<!-- SPDX-License-Identifier: CC-BY-4.0 -->
<!-- Copyright IBM Corp. 2007-2020 -->

# scripts

Directory in which to place scripts, by sub-directory. The listed type is as it should be
specified in [Scripts.csv](../../../docs/Scripts.md).

| Sub-directory | Type | Used for |
| :--- | :--- | :--- |
| `catalog` | `CTG` | pre-processing, post-processing, and post-save scripts on catalogs |
| `category_tree` | `CTR` | pre-processing, post-processing, and post-save scripts on hierarchies |
| `distribution` | `DISTRIBUTION` | custom distributions |
| `entry_build` | `ENTRY_BUILD` | entry build scripts, which run before an item is rendered |
| `entry_macro` | `ENTRY_MACRO` | entry macro scripts, which can be executed via the "MACRO" button on item and category edit screens |
| `entry_preview` | `ENTRY_PREVIEW` | entry preview scripts, which can be executed via the "ACTION" button on item and category edit screens |
| `export/ctg` | `CTG_EXPORT` | the most common type of general catalog exports |
| `export/ctg_diff` | `CTG_DIFF_EXPORT` | differences exports that use MDM-CE's native differencing |
| `export/ctg_to_ctg` | `CTLG_CTLG_EXPORT` | catalog-to-catalog exports when using MDM-CE's native catalog-to-catalog export functionality |
| `export/img` | `IMG_EXPORT` | binary exports |
| `export/img_diff` | `IMG_DIFF_EXPORT` | binary differences exports when using MDM-CE's native differencing |
| `import/ctg` | `CTG_IMPORT` | the most common type of general catalog imports |
| `import/ctr` | `CTR_IMPORT` | the most common type of general hierarchy imports |
| `import/lkp` | `LKP_IMPORT` | imports into lookup tables |
| `ldap_usr_fetch` | `LDAP_USR_FETCH` | the script that defines hooks to execute on LDAP user fetch events |
| `login` | `LOGIN` | the script that defines hooks to execute on login events |
| `logout` | `LOGOUT` | the script that defines hooks to execute on logout events |
| `params` | `INPUT_PARAM` | defining a set of input values for a particular job (eg. an export) |
| `report` | `SEARCH_RESULT_REPORT` | the scripts that define Rich Search Result reports |
| `reports` | `REPORT` | the scripts that define reports |
| `sandbox` |  | scripts that are used for testing in the sandbox, but not actually deployed to an environment |
| `secure_triggers` | `SECURE_TRIGGER` | scripts that can be executed using `/utils/secure_invoker.jsp?script=<name>`, which will ensure a user has logged in before executing the script |
| `triggers` | `TRIGGER` | function libraries, most commonly |
| `trigo_app` | `TRIGO_APP` | custom tool scripts |
| `wbs` | `WBS` | webservice implementation scripts |
| `workflow` | `DOCUMENT` | workflow step scripts, using the convention `workflow/WorkflowName/StepName/StepName.wpcs` |

----
License: [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/),
Copyright IBM Corp. 2007-2020.
