<!-- SPDX-License-Identifier: CC-BY-4.0 -->
<!-- Copyright IBM Corp. 2007-2020 -->

# Imports.csv

## Columns

1. **CS?** - indicates whether the line should be considered as a template (true) or a single object
1. **Import Name** - name of the import feed
1. **Catalog Name** - name of the catalog into which to import (if Type is `ITM`)
1. **Hierarchy Name** - name of the hierarchy into which to import (if Type is `CTR`)
1. **Import Type** - type of import feed
1. **Import Semantic** - primary operational task of the import
1. **Character Set** - encoding of the file(s) the export will create (`UTF-8`, `Cp1252`, etc)
1. **File Spec Name** - name of the file spec that describes the file the import feed will process (if Type is `ITM`)
1. **Spec Map Name** - name of the spec map mapping the file spec to the catalog or catalog spec (if Type is `ITM`) (NOTE: a blank mapping will be automatically generated if this field is empty)
1. **Data Source Name** - name of the data source from which this import feed will process data
1. **Import Script Name** - name of the import script the import feed will use for processing the data
1. **ACG** - name of the access control group to associate with the import feed
1. **Is collaboration area?** - indicates whether this import is attached to a collaboration area (true) or not
1. **Workflow Step Path** - path of the workflow step into which the import feed should load data (if imports into collaboration area)
1. **Approval User** - username of the user who must approve that the import feed can run (empty for no approval)
1. **Parameters Name** - name of the parameter document that should be used when this import feed runs (for no parameters, leave empty)

### Valid import types

- `ITM` - item import
- `ICM` - item to category mapping import
- `CTR` - hierarchy import
- `IMG` - binary import

### Valid import semantics

- `U` - for Updates
- `R` - for Replace (`ITM`-only)
- `D` - for Delete (`ITM`-only)

----
License: [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/),
Copyright IBM Corp. 2007-2020.
