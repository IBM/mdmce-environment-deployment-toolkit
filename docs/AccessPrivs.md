<!-- SPDX-License-Identifier: CC-BY-4.0 -->
<!-- Copyright IBM Corp. 2007-2020 -->

# AccessPrivs.csv

## Columns:

1. **CS?** - indicates whether the line should be considered as a template (true) or a single object</li>
1. **Container Name** - name of the container against which the access privilege is being defined</li>
1. **Container Type** - type of container against which the access privilege is being defined</li>
1. **Roles (CSV)** - a comma-separated list of the roles against which to set the permissions</li>
1. **Attribute Collection** - the attribute collection against which to set the permissions</li>
1. **View-only** - indicates if the specified attribute collection should be read-only (true) or editable</li>

### Valid container types

- `CATALOG`- a catalog
- `CATEGORY_TREE` - a hierarchy, organization, category tree

----
License: [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/),
Copyright IBM Corp. 2007-2020.
