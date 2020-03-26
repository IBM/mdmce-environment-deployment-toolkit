<!-- SPDX-License-Identifier: CC-BY-4.0 -->
<!-- Copyright IBM Corp. 2007-2020 -->

# AttrCollections.csv

A given attribute collection can have multiple lines in the CSV file, each line
indicating a single attribute that should be included in the collection. Processing will expect
that all lines relevant to one attribute collection appear next to each other in the file
(ie. that the file is sorted by Attribute Collection Name). Also of note is that localized fields
should *not* be listed one-by-one by their full attribute path (including the locale identifier),
but should simply be listed as the parent node that is localised.  The tooling will automatically
recognise localised attributes and handle them as needed.

## Columns

1. **CS?** - indicates whether the line should be considered as a template (true) or a single object
1. **Attribute Collection Name** - name of the attribute collection being defined
1. **Type** - type of attribute collection being defined
1. **Description** - description of the attribute collection being defined
1. **Spec Name** - name of the spec that contains the attribute being defined for the collection
1. **Attribute Path** - full path to the attribute (excluding the spec name) that should be included in the collection

### Valid types

- `FULL_SPEC` - for a dynamic attribute collection containing the entire spec
- `GENERAL` - normal attribute collection
- `FULL_SPEC_SEARCH` - for a dynamic attribute collection containing the entire spec, to be used as a SEARCH_TEMPLATE attribute collection
- `SEARCH_TEMPLATE` - for the definition of all attributes that can be searched under a search template with the same name as the attribute collection

----
License: [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/),
Copyright IBM Corp. 2007-2020.
