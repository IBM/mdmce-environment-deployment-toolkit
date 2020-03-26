<!-- SPDX-License-Identifier: CC-BY-4.0 -->
<!-- Copyright IBM Corp. 2007-2020 -->

# HierarchyMappings.csv

The forward-slash path delimiter is currently required on the MDM-CE side, and as such there is
currently no option to select another path delimiter. You must ensure that your path attribute in your hierarchy
will not contain a forward-slash if you want to make use of hierarchy mapping through this toolkit or the environment
export / import in general.

## Columns

1. **CS?** - indicates whether the line should be considered as a template (true) or a single object
1. **Source Hierarchy** - name of the hierarchy from which mapping should originate
1. **Destination Hierarchy** - name of the hierarchy to which mapping should be automated
1. **Source Category Path** - path of the category from which mapping should originate, where the path delimiter is a single forward-slash ('/')
1. **Destination Category Path** - path of the category to which mapping should be automated, where the path delimiter is a single forward-slash ('/')

----
License: [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/),
Copyright IBM Corp. 2007-2020.
