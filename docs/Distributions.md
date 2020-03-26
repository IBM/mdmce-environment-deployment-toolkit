<!-- SPDX-License-Identifier: CC-BY-4.0 -->
<!-- Copyright IBM Corp. 2007-2020 -->

# Distributions.csv

## Columns

- **CS?** - indicates whether the line should be considered as a template (true) or a single object
- **Distribution Name** - name of the distribution being defined
- **Type** - type of distribution being defined
- **Email** - e-mail address for the distribution (if Type is `EMAIL`)
- **Hostname** - hostname for the distribution (if Type is `FTP`); or the full URL (if Type is `POST`)
- **User ID** - username to login with for the distribution (if Type is `FTP`); or the content type ("text/plain", "text/xml", "application/x-tar") (if Type is `POST`)
- **Password** - password for the username to login with for the distribution (if Type is `FTP`)
- **Path** - directory into which to place a file (if Type is `FTP`)
- **From** - unknown (this is likely legacy from the `ARIBA_CATALOG_UPLOAD` type, which is probably no longer supported?)
- **To** - unknown (this is likely legacy from the `ARIBA_CATALOG_UPLOAD` type, which is probably no longer fully supported?)
- **Subject** - unknown (this is likely legacy from the `ARIBA_CATALOG_UPLOAD` type, which is probably no longer fully supported?)
- **Local Path** - unknown (possibly the path to or name of the distribution script if Type is `CUSTOM`)

### Valid distribution types

- `EMAIL` - for sending results via e-mail
- `FTP` - for sending results via FTP
- `POST` - for posting results via HTTP
- `CUSTOM` - for using a custom-written Distribution Script

----
License: [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/),
Copyright IBM Corp. 2007-2020.
