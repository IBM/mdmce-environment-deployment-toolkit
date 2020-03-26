<!-- SPDX-License-Identifier: CC-BY-4.0 -->
<!-- Copyright IBM Corp. 2007-2020 -->

# Views.csv

The definition of a single view (even a single tab) may span multiple lines - in
such cases each line of the same view should appear alongside all other lines for
the view (same for tabs).

## Columns

1. **CS?** - indicates whether the line should be considered as a template (true) or a single object
1. **Container Name** - name of the container against which the view should be defined; when "Container Type" is `WORKFLOW`, should be the name of the workflow
1. **Container Type** - type of container
1. **View name / Step Path** - name of the view being defined; or when "Container Type" is `WORKFLOW` should be the step path of the workflow step
1. **Attribute Collection** - name of an attribute collection to include in the view
1. **Tab name** - name of the tab on which the attribute collection should appear within the view
1. **View-only?** - indicates whether the attribute collection should be read-only (true) or not within the view
1. **Single Edit?** - indicates whether the attribute collection should be included in the single edit screens (true) or not for the view
1. **Multi-Edit?** - indicates whether the attribute collection should be included in the multi-edit screens (true) or not for the view
1. **Item List?** - indicates whether the attribute collection should be included in the item list screens (true) or not for the view (NOTE: Unused for "Container Type" `WORKFLOW`)
1. **Item-Popup?** - indicates whether the attribute collection should be included in the item pop-up screens (true) or not for the view
1. **Location?** - indicates whether the attribute collection should be included in the item location screens (true) or not for the view
1. **Default?** - indicates whether the view should be set as the default view (true) or not (should be marked or unmarked for all rows defining the view)

### Valid container types

- `CATALOG` - view for a catalog
- `CATEGORY_TREE` - view for a hierarchy / organization / category tree
- `WORKFLOW` - view for a workflow step

----
License: [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/),
Copyright IBM Corp. 2007-2020.
