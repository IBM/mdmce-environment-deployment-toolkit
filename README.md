<!-- SPDX-License-Identifier: CC-BY-4.0 -->
<!-- Copyright IBM Corp. 2007-2020 -->

# mdmce-environment-deployment-toolkit

Toolkit for automating the configuration of IBM Master Data Management Collaborative Edition (MDM-CE) environments
using a model-driven, revision-controllable approach.

## How it works

1. Define your environment in a series of CSV files.
1. Run an Apache Ant process that automatically translates these into a loadable archive.
1. Load the archive to your MDM-CE environment.

## 1. Define your environment

A set of templates are provided in this repository under the `loadToEnv/FILES/Deployment` directory. Fill them
in with the details of your environment according to the specific instructions linked below.

- [AccessPrivs.csv](docs/AccessPrivs.md) to define access privileges
- [ACGs.csv](docs/ACGs.md) to define access control groups
- [AttrCollections.csv](docs/AttrCollections.md) to define attribute collections
- [Catalogs.csv](docs/Catalogs.md) to define catalogs
- [CatalogContents.csv](docs/CatalogContents.md) to define data to load to catalogs
- [ColAreas.csv](docs/ColAreas.md) to define collaboration areas
- [CompanyAttributes.csv](docs/CompanyAttributes.md) to define the locales and currencies for the company
- [DataSources.csv](docs/DataSources.md) to define data sources
- [Distributions.csv](docs/Distributions.md) to define distributions
- [Exports.csv](docs/Exports.md) to define exports
- [Hierarchies.csv](docs/Hierarchies.md) to define hierarchies
- [HierarchyContents.csv](docs/HierarchyContents.md) to define data to load to hierarchies
- [HierarchyMappings.csv](docs/HierarchyMappings.md) to define hierarchy mappings
- [Imports.csv](docs/Imports.md) to define import feeds
- [Lookups.csv](docs/Lookups.md) to define lookup tables
- [LookupTableContents.csv](docs/LookupTableContents.md) to define data to load to lookup tables
- [Organizations.csv](docs/Organizations.md) to define organization hierarchies
- [OrganizationContents.csv](docs/OrganizationContents.md) to define data to load to organization hierarchies
- [Reports.csv](docs/Reports.md) to define reports
- [Roles.csv](docs/Roles.md) to define roles
- [RoleToACGs.csv](docs/RoleToACGs.md) to define the privileges enabled between roles and access control groups
- [Scripts.csv](docs/Scripts.md) to define scripts or other documents to load to the document store
- [SearchTemplates.csv](docs/SearchTemplates.md) to define search templates
- [Selections.csv](docs/Selections.md) to define selections
- [Settingss.csv](docs/Settings.md) to define user settings
- [Specs.csv](docs/Specs.md) to define specs
- [SpecMaps.csv](docs/SpecMaps.md) to define spec mappings
- [UDLs.csv](docs/UDLs.md) to define user defined logs
- [Users.csv](docs/Users.md) to define users
- [Views.csv](docs/Views.md) to define views for catalogs, hierarchies and workflow steps
- [WebServices.csv](docs/WebServices.md) to define web services
- [Workflows.csv](docs/Workflows.md) to define workflows

Note that many of these also support ["templating" by using up to 2 levels of variables](docs/TemplateParameters.md).

## 2. Build the archive

Once your environment is defined in the CSV files, you can build a loadable archive by simply running
Apache Ant against the provided `build.xml` file in this repository.

```shell script
$ ant
```

By default, this will prompt you to input:

- the company code for your target environment
- the version of the MDM-CE software (currently use `9.0.0` for anything beyond version `9.0.0`)
- the location of your CSV files (this assumes you follow the same directory layout as provided under
    `loadToEnv` in this repository, and should include up to `.../loadToEnv/FILES`)

Once you have provided those inputs (when prompted) the translation should then take place and an
archive will be created with the name `<companyCode>-<version>-<date>.zip`.

As part of the translation, the process will also attempt to validate what you have defined, and
should catch eg. any referential mistakes you may have made across different CSV files.

### Automating your environment build

Since the default Ant build relies on user input, it cannot as easily be used for fully automating the build of your
archive. The simplest way to change this is to modify the `build.xml` file to replace the variables with hard-coded
values specific to your environment.

Replace every occurrence of the following:

- `${CMP_CODE}` with your environment's company code
- `${VERSION}` with the version of your environment's MDM-CE software
- `${INPUT_DIR}` with the path to your input files (up to and including `loadToEnv/FILES`)

Finally, comment out the following lines in the XML to disable the request for input:

```xml
<!-- <input message="Company code: " addproperty="CMP_CODE" /> -->
<!-- <input message="Please specify the version of the environment: " addproperty="VERSION" validargs="5.2.1,5.3.0,5.3.1,5.3.2,6.0.0,6.5.0,9.0.0" /> -->
<!-- <input message="Input files (path to loadToEnv/FILES): " addproperty="INPUT_DIR" /> -->
```

## 3. Load the archive

1. Log in to MDM-CE environment.
1. Choose the “Import Environment” option from the “System Administrator” menu.
1. Use the "Browse..." button to select the .zip archive from your local machine.
1. Click the "Upload" button to load the archive.

You should then be provided a link to review the status of the loading and processing,
as well as a "Debug Report" button to display detailed information about the load process.

You may want to review the log, specifically for any lines including "error" or "fail" to
identify any loading or processing problems.

----
License: [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/),
Copyright IBM Corp. 2007-2020.
