<!-- SPDX-License-Identifier: CC-BY-4.0 -->
<!-- Copyright IBM Corp. 2007-2020 -->

# Searches.csv

A search used for templates with type `SEARCH_TEMPLATE` and for the saved searches `SAVED_SEARCH`

## Columns

1. **CS?** - indicates whether the line should be considered as a template (true) or a single object
1. **Search Name** - name of the search (Search Template or Saved Search)
1. **Search Type** - type of the searches TEMPLATE or QUERY. TEMPLATE for Search Templates, QUERY for Saved Searches.
1. **Container Name** - Name of the container for which this search will be applied. 
   Can be Catalog, Hierarchy or Collaboration Area.
1. **Container Type** - Type of the container for which this search will be applied. 
   Can be CATALOG, HIERARCHY or COL_AREA.
1. **Is Default** - (optional) x - true, empty - false. Is this Search the default. By default setting to false.
1. **Is Shared** - (optional) x - true, empty - false. Is this Search Shared (Only for search templates). 
   By default setting to false.
1. **Username** - User for the searches
1. **Description** - (optional) Description
1. **Search Scope** - Search Scope. By default setting to ENTIRE_CATALOG
1. **Sort Type** - (optional) ASCENDING/DESCENDING. By default setting to ASCENDING
1. **Sort Attribute Path** - (optional) Full Path  for the Sort Attribute
1. **Category Restriction** - (optional) Restriction used for the Category Filter 
   (ANY - Entry can be in ANY categories, ALL - Entry must be in ALL categories). By default setting to ANY
1. **Save Parameters** - (optional) x - true, empty - false. Save the value of the search. 
   By default setting to false.
1. **Step Name** - (optional) the name of the step for which this search will be applied (if the container is a Collaboration Area)
1. **Reserved by** - (optional) RESERVED_BY_ME/RESERVED_BY_OTHERS/AVAILABLE (if the container is a Collaboration Area)
1. **Categories Filter** - (optional) Comma-separated list of the categories used for the filter
1. **Attribute Path** - (optional) Full Attribute Path
1. **Negate** - (optional) x - true, empty - false. Operator NOT 
1. **Search operator** - (optional) Search option. 
   CONTAINS
   BEGINS_WITH
   ENDS_WITH
   IS_EMPTY
   BEGINS_WITH_MATCH_CASE
   ENDS_WITH_MATCH_CASE
   EQUAL
   EQUAL_MATCH_CASE
   CONTAINS_MATCH_CASE
   GREATER_THAN_OR_EQUAL
   LESS_THAN_OR_EQUAL
   GREATER_THAN
   LESS_THAN
   BETWEEN
1. **Value** - (optional) The value for the search (if Save parameters is true)
1. **Second value** - (optional) For the search operator BETWEEN
1. **Logical operator** - (optional) Logical operator (AND / OR)

----
License: [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/),
Copyright IBM Corp. 2007-2020.