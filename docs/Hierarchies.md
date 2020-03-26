<!-- SPDX-License-Identifier: CC-BY-4.0 -->
<!-- Copyright IBM Corp. 2007-2020 -->

# Hierarchies.csv

## Columns

1. **CS?** - indicates whether the line should be considered as a template (true) or a single object
1. **Hierarchy Name** - name of the hierarchy
1. **Spec** - name of the primary spec upon which this hierarchy should be based
1. **Inherit?** - indicates whether this hierarchy should support inheritance (true) or not
1. **Display Attribute** - full attribute path (including spec name) of the display attribute for this hierarchy
1. **Path Attribute** - full attribute path (including spec name) of the path attribute for this hierarchy
1. **ACG** - name of the access control group that should be associated with this hierarchy
1. **Org?** - `[DEPRECATED]` refer to [Organizations.csv](Organizations.md) instead 
1. **Scripts** - comma-separated list of the scripts that should be associated with the hierarchy.  Each script takes the form `ScriptType|ScriptName`
1. **Items on Leaves Only?** - indicates whether this hierarchy should allow items to be classified only under its leaf nodes (true) or under any node

### Valid script types

- `SPEC_ID` - (unknown)
- `SEQUENCE_START` - (internal? probably not wise to change)
- `SCRIPT_NAME` - post-processing script
- `PRE_SCRIPT_NAME` - pre-processing script
- `POST_SAVE_SCRIPT_NAME` - post-save script
- `ENTRY_BUILD_SCRIPT` - entry build script
- `DISPLAY_ATTRIBUTE` - (overlaps with "Display Attribute" column of CSV)
- `CORE_ATTRIBUTE_GROUP` - (internal? - probably not wise to change)
- `USER_DEFINED_CORE_ATTRIBUTE_GROUP` - the name of the attribute collection that should always be retrieved and saved to the database for entries in the catalogue
- `SCRIPT_RESTRICT_LOCALES` - (unknown)
- `PERSISTENCE_UDL` - (unknown)

----
License: [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/),
Copyright IBM Corp. 2007-2020.
