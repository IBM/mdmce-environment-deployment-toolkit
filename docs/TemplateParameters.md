<!-- SPDX-License-Identifier: CC-BY-4.0 -->
<!-- Copyright IBM Corp. 2007-2020 -->

# TemplateParameters.csv

This file allows templating parameters to be defined, that can be used to generate various
target objects from a single input line in the CSV files. Any files that have a `CS?` column
are able to use these variable replacements, and they will only be applied to any lines that
have that column ticked.

The expected definition of these parameters in `TemplateParameters.csv` is as follows:

- The first column of the CSV should contain up to 2 rows of data.
- The first cell of the CSV should contain the variable name of the highest-level varying factor.
- The first column, second row of the CSV should contain the sub-variable name for those parameters that vary within the factor above.
- Each column after the first column should have a replacement value for the highest-level varying factor in its first row.
- Each subsequent row should contain a replacement value for the sub-variable factor.

This format allows:

- A single varying element (by filling in only the first row, and as many columns as there are replacements for that variable).
- Two varying elements, where the replacement values for the sub-varying element can differ depending on the higher-level varying element (as per format description above).
- Two varying elements, where the replacement values for the sub-varying element can be the same across all higher-level varying elements (by copy/pasting an entire column and then just replacing the first row with a different highest-level value).

----
License: [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/),
Copyright IBM Corp. 2007-2020.
