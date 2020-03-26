<!-- SPDX-License-Identifier: CC-BY-4.0 -->
<!-- Copyright IBM Corp. 2007-2020 -->

# SpecMaps.csv

A given spec map will span multiple lines in the CSV file, each line
defining a single mapping between attributes. Furthermore, it is necessary to specify both
source and destination objects and source and destination specs. This necessity is due to
the case of Catalog mappings, which could involve mapping data to any number of secondary specs.

## Columns

1. **CS?** - indicates whether the line should be considered as a template (true) or a single object
1. **Spec Map Name** - name of the spec mapping
1. **Spec Map Type** - type of spec mapping (see below for list of valid values)
1. **Source Object Name** - name of the source catalog or source file spec (depending on Spec Map Type)
1. **Destination Object Name** - name of the destination catalog or destination spec (depending on Spec Map Type)
1. **Source Spec Name** - name of the spec containing the source attribute (will always be a spec name, even for Catalog to Destination Spec mappings)
1. **Source Attribute Path** - path (not including the spec name) of the source attribute
1. **Destination Spec Name** - name of the spec containing the destination attribute (will always be a spec name, even for File Spec to Catalog mappings)
1. **Destination Attribute Path** - path (not including the spec name) of the destination attribute

### Valid spec map types

- `FILE_CATALOG_MAP` - File Spec to Catalog mapping
- `FILE_CAT_MAP` - File Spec to Catalog Spec mapping
- `CATALOG_MKT_MAP` - Catalog to Destination Spec mapping
- `CAT_MKT_MAP` - Catalog Spec to Destination Spec mapping

----
License: [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/),
Copyright IBM Corp. 2007-2020.
