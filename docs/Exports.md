<!-- SPDX-License-Identifier: CC-BY-4.0 -->
<!-- Copyright IBM Corp. 2007-2020 -->

# Exports.csv

## Columns

1. **CS?** - indicates whether the line should be considered as a template (true) or a single object
1. **Export Name** - name of the export
1. **Container Name** - name of the container from which to export
1. **Hierarchy Name** - name of the hierarchy specified for this export
1. **Syndication Type** - type of syndication to use for the export being defined
1. **Character Set** - encoding of the file(s) the export will create (`UTF-8`, `Cp1252`, etc)
1. **Destination Spec Name** - name of the destination spec that describes the file the export will create
1. **Spec Map Name** - name of the spec map mapping the container to the destination spec (NOTE: a blank mapping will be automatically generated if this field is empty)
1. **Export Script Name** - name of the export script the export will use for generating its file(s)
1. **Approval User** - username of the user who must approve that the export can run (empty for no approval)
1. **Distribution Name** - name of the distribution to which to send the generated file(s)
1. **Distribution Group Name** - name of the distribution group to which to send the generated file(s)
1. **Selection** - name of the selection from which to retrieve the entries to export
1. **Difference Type** - type of differences to include in this export, if it is a differences export
1. **Parameters Name** - name of the parameter document that should be used when this export runs (for no parameters, leave empty)

### Valid syndication types

- `CONTENT_ALL` - all entries in the version
- `CONTENT_DIFF_VERS` - differences between two versions
- `CONTENT_DIFF_SYND` - differences since last syndication
- `IMAGES_ALL` - all binary files for entries in a version
- `IMAGES_DIFF_VERS` - updated binary files for entries between two versions
- `IMAGES_DIFF_SYND` - updated binary files for entries since last syndication

### Valid difference types

- `A` - added entries
- `D` - deleted entries
- `M` - modified entries
- `U` - unchanged entries
- `MITA` - modified attributes (unsupported?)
- `MICM` - modified category mappings (unsupported?)
- `L` - all entries (unsupported?)

----
License: [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/),
Copyright IBM Corp. 2007-2020.
