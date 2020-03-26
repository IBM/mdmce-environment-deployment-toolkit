<!-- SPDX-License-Identifier: CC-BY-4.0 -->
<!-- Copyright IBM Corp. 2007-2020 -->

# RoleToACGs.csv

The left-most column of the CSV file is a listing of all of the access control privileges and all of the screen
privileges (those starting with `PAGE_OBJ_`) available. Each subsequent column should define a single
mapping between Role and ACG using the format `RoleName,ACGName`. The cross between the
access control privileges and the `Role,ACG` column should be ticked only if the mapping specified by
the column should have the access defined by the access control privilege.

In short, the file is itself the same matrix that can be seen within the MDM-CE user interface for managing the
mappings between roles and ACGs, only the file contains **all** such mappings rather than just the mappings for a single
ACG or a single role.

NOTE: The "Default" ACG should be specified in this file (as well as anywhere else it is used) using the special
value `$DEFAULT`. This is necessary due to variations in locale setup for MDM-CE environments, which dictate that
the name of this ACG is not always simply "Default".

----
License: [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/),
Copyright IBM Corp. 2007-2020.
