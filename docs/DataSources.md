<!-- SPDX-License-Identifier: CC-BY-4.0 -->
<!-- Copyright IBM Corp. 2007-2020 -->

# DataSources.csv

## Columns

1. **CS?** - indicates whether the line should be considered as a template (true) or a single object
1. **Data source name** - name of the data source being defined
1. **Data source type** - type of data source being defined
1. **Server address** - address of the server for the data source (if Type is `PULL_FTP`)
1. **Server port** - port of the server for the data source (if Type is `PULL_FTP`)
1. **Login username** - username to login with for the data source (if Type is `PULL_FTP`)
1. **Login password** - password for the username to login with for the data source (if Type is `PULL_FTP`)
1. **Directory** - directory from which to retrieve a file (if Type is `PULL_FTP`)
1. **Filename** - name of the file to retrieve for the data source (if Type is `PULL_FTP`)
1. **DocStore Path** - document store path for the file to retrieve for the data source (if Type is `DOC_STORE`)

### Valid data source types

- `PULL_FTP` - to retrieve data via FTP
- `PUSH_WWW` - to have a user upload data via HTTP
- `DOC_STORE` - to retrieve data from the document store
- `STAGING_AREA` - to retrieve data from the staging area (?); don't know how this can be defined currently - UNTESTED

----
License: [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/),
Copyright IBM Corp. 2007-2020.
