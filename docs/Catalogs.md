<!-- SPDX-License-Identifier: CC-BY-4.0 -->
<!-- Copyright IBM Corp. 2007-2020 -->

# Catalogs.csv

## Columns

1. **CS?** - indicates whether the line should be considered as a template (true) or a single object
1. **Catalog Name** - name of the catalog being defined
1. **Spec** - name of the primary spec to be used for defining the catalog
1. **Primary Hierarchy** - name of the hierarchy to be associated to the catalog as the primary hierarchy
1. **Secondary Hierarchies** - comma-separated list of the secondary hierarchies that should be associated to the catalog
1. **Inherit?** - indicates whether the catalog should use inheritance (true) or not
1. **Display Attribute** - full spec path of the display attribute to be used for the catalog
1. **ACG** - name of the access control group to be associated to the catalog
1. **Links** - comma-separated list of the links that should be defined for the catalog.  Each link takes the form
    `FullSpecPathToLinkingAttribute|DestinationCatalogName`.
    Optionaly you can specify a display attribute instead of the primary key with `FullSpecPathToLinkingAttribute|DestinationCatalogName|FullSpecPathToDestinationCatalogAttribute`
1. **Locations** - location hierarchy and details specified in the format:
    `HierarchyName=SecondarySpecName|InheritanceAttributeCollectionList`, where the `InheritanceAttributeCollectionList`
    is itself a comma-separated list of attribute collection names. Separate location hierarchies will need to be defined
    on a new line within the cell (CTRL-Enter within Excel).
1. **Scripts** - comma-separated list of the scripts that should be associated with the catalog.  Each script takes the
    form `ScriptType|ScriptName`
1. **User Defined Attributes** - comma-separated list of user defined attributes to be added to the catalog.  Each attribute takes the
    form `AttributeName|AttributeValue`. This can be used to set the domain parameters: [DOMAIN_ENTITY_FOR_ITEM, DOMAIN_ENTITY_ATTRIBUTE](https://www.ibm.com/support/knowledgecenter/SSADN3_12.0.0/dev_soln/labels/con_customizinglabels.html)

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
