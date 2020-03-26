<!-- SPDX-License-Identifier: CC-BY-4.0 -->
<!-- Copyright IBM Corp. 2007-2020 -->

# Specs.csv

A given spec will span multiple lines in the CSV file, each line
defining a single attribute that should be included in the spec. The processing will expect
that all lines relevant to one spec appear next to each other in the file
(ie. that the file is sorted by Spec Name). Furthermore, it **is** necessary to explicitly
list any `GROUPING` attributes on their own line in the CSV, and these must appear prior to any
child attributes within the `GROUPING` (ie. the file should in fact be sorted first by Spec Name,
and then by Attribute Path).

Also note:

- All specs will be marked as localised using all locales in the [CompanyAttributes.csv](CompanyAttributes.md) (for display names).
- Only if there is actually a localised attribute, however, will localised data be capable of being persisted against the spec.
- Each localised attribute will be localised to all locales in the [CompanyAttributes.csv](CompanyAttributes.md).
- All localized attributes will additionally be given the "Cascade" property, so any string enumeration rules or the like will be cascaded to all localized nodes.
- The order of the nodes in the spec will be specified by the order they appear in the CSV file.
- The order of the DisplayName columns (left-to-right) should match the ordering of the locales in the [CompanyAttributes.csv](CompanyAttributes.md) file.

## Columns

1. **CS?** - indicates whether the line should be considered as a template (true) or a single object
1. **Spec Name** - name of the spec
1. **Spec Type** - type of spec (see below for list of valid values); if the type is `FILE_SPEC`, use the convention `FILE_SPEC|FileType|IgnoreHeaderLines|CharacterDelimiter`. `IgnoreHeaderLines` should be numeric, and only used for flat files (not XML), and `CharacterDelimiter` should only be used if `FileType` is `D`.
1. **Attribute Path** - path (not including the spec name) of an attribute in this spec (or the name of a subspec to attach the entire subspec)
1. **Attribute Type** - type of the attribute being specified within this spec (or the literal `SUB_SPEC` if the attribute path is the name of a subspec)
1. **PK?** - indicates whether this attribute should be the primary key (true) of the spec
1. **Idx?** - indicates whether this attribute should be indexed (true) within the spec
1. **Lcl?** - indicates whether this attribute should be localised (true) or not
1. **Lnk?** - indicates whether the specified attribute should be a linking attribute (true) or not; NOTE: if Attribute Type is `LOOKUP_TABLE`, this should specify the name of a lookup table
1. **Min** - minimum occurrences required for the specified attribute of this spec (0 for optional, 1 for mandatory)
1. **Max** - maximum occurrences required for the specified attribute of this spec (must be &gt;= 1)
1. **Edit?** - indicates whether the specified attribute should be editable (true) or not
1. **NP?** - indicates whether the specified attribute should be a non-persistent attribute (true) or not; NOTE: if it is a non-persistent attribute, the rule should appear in the `RULE` column without any prefix
1. **Default** - default value for the specified attribute of this spec
1. **Length** - maximum length allowed for the specified attribute of this spec
1. **Hidden?** - indicates whether the specified attribute should be hidden / non-viewable (true) or not
1. **RULE** - a list of the rules that should apply to the specified attribute, separated by the characters `~=~`.  Each rule takes the form: `RuleType|ActualScriptCode`
1. **DisplayName (1)** - the display name for the specified attribute in the locale given by the first row of [CompanyAttributes.csv](CompanyAttributes.md)
1. **DisplayName (*n*)** - the display name for the specified attribute in the locale given by the <i>n</i>th row of the [CompanyAttributes.csv](CompanyAttributes.md)

### Valid attribute types

- `BINARY`
- `CATEGORY`
- `CURRENCY`
- `DATE`
- `TIMEZONE`
- `FLAG`
- `GROUPING`
- `IMAGE`
- `IMAGE_URL`
- `INTEGER`
- `LOOKUP_TABLE`
- `NUMBER`
- `NUMBER_ENUMERATION`
- `PASSWORD`
- `RELATIONSHIP`
- `SEQUENCE`
- `STRING`
- `STRING_ENUMERATION`
- `THUMBNAIL_IMAGE`
- `THUMBNAIL_IMAGE_URL`
- `URL`
- `LONG_TEXT`
- `EXTERNAL_CONTENT_REFERENCE`

### Valid spec types

- `FILE_SPEC` - file spec describing incoming files (NOTE: currently there is no way to specify the type of file (Excel, CSV, etc))
- `PRIMARY_SPEC` - primary spec describing catalogue or hierarchy entries
- `SECONDARY_SPEC` - secondary spec describing either standalone or item secondary specs
- `MKT_SPEC` - destination spec describing outgoing files
- `LKP_SPEC` - lookup spec describing lookup table records (NOTE: grouping nodes cannot exist in a lookup spec, but localised nodes can)
- `SCRIPT_INPUT_SPEC` - script input spec describing input parameters to a script
- `SUB_SPEC` - for use in other specs

### Valid file types for `FILE_SPEC`

- `D` - character delimited (specify delimiter after last '|' following `FILE_SPEC`)
- `T` - tab delimited
- `C` - comma separated values (CSV)
- `F` - fixed width
- `X` - XML
- `E` - Excel

### Valid rule types

- `CATEGORY_DELIMITER` - (unknown)
- `DURATION` - (unknown)
- `ENCODING` - (unknown)
- `MAX_EXCLUSIVE` - (unknown - related to regular expressions?)
- `MAX_INCLUSIVE` - (unknown - related to regular expressions?)
- `MINLENGTH` - (unknown - minimum length of an attribute?)
- `MIN_EXCLUSIVE` - (unknown - related to regular expressions?)
- `MIN_INCLUSIVE` - (unknown - related to regular expressions?)
- `NUMBER_ENUMERATION` - allows definition of a list of numbers to validate against
- `PATTERN` - definition of a regular expression to validate against (?)
- `PERIOD` - (unknown)
- `PRECISION` - defines how many decimal places should be stored (?)
- `SCALE` - (unknown)
- `WHITESPACE` - (unknown)
- `UNIQUE` - defines whether an attribute must be unique across all entries in a single container
- `SEQUENCE_NAME` - (unknown - probably not wise to change)
- `SEQUENCE_INCREMENT` - the value by which a sequence should be incremented
- `SEQUENCE_START` - the value at which a sequence should start
- `GROUP` - (unknown)
- `DATE_FORMAT` - the format to utilise on a date attribute
- `LOCALE_NODE` - (unknown)
- `HELP_URL` - defines the URL that will popup as help for the attribute
- `OCCURRENCES_TO_DISPLAY` - the number of occurrences to display by default
- `CASCADE` - applies rules of a localised node to each child locale node (this is enabled on all localised nodes by the tooling)
- `CANDIDATE_FOR_DISPLAY` - (unknown)
- `VALIDATION_RULE` - validation rule script (must set implicit "res" variable to true or false)
- `VALUE_RULE` - value rule script (applies value set to implicit "res" variable as value of the attribute)
- `STRING_ENUMERATION_RULE` - string enumeration rule script (must set "res" implicit variable to an array)

----
License: [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/),
Copyright IBM Corp. 2007-2020.
