<!-- SPDX-License-Identifier: CC-BY-4.0 -->
<!-- Copyright IBM Corp. 2007-2020 -->

# Users.csv

Note: within the `Roles` column, a special token `$CMP` can be used that will automatically be
replaced with the company code of the environment. Thus, for instance, the default administrative role can
generically be specified as `$CMP_admin` in this column - regardless of the company code of the environment,
the user will be assigned the default administrator role.

## Columns

1. **CS?** - indicates whether the line should be considered as template (true) or a single object
1. **Username** - unique username that will identify the user (note that the password will be hard-coded to &quot;trinitron&quot; for all users)
1. **First name** - first name of the user
1. **Last name** - last name of the user
1. **Email address** - e-mail address of the user
1. **Address** - address of the user
1. **Phone number** - telephone number of the user
1. **Fax number** - fax number of the user
1. **Enabled?** - indicates whether the user should be enabled (true) or not
1. **Roles** - comma-separated list of roles with which the user should be associated **
1. **Organization** - comma-separated list of full category paths (including hierarchy name) of the organizations in which the user should be classified
1. **LDAP Enabled?** - indicates whether the user should be authenticated via LDAP (true) or locally
1. **LDAP Entry DN** - the unique distinguished name of the user in the LDAP directory
1. **LDAP Server URL** - the URL to the LDAP directory server this user should be authenticated against

----
License: [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/),
Copyright IBM Corp. 2007-2020.
