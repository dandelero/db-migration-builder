# DB Migration Builder
DB Migration Builder is a simple library to build database migration scripts; if you are unfamiliar with database 
migrations check out [this excellent article](https://dzone.com/articles/what-is-database-migration).

# Why another library?
Database Migrations as a concept is nothing new, but it does present significant benefits to developers, DBAs, 
Release Managers, Operations staff, and others. However, most tools try to do too much without really respecting 
segregation of duties, technology approval processes, security concerns, and auditing to name a few. 

DB Migration Builder was built to provide the opportunity for use of database migrations in large organisations that 
require tools to undergo strict approval processes before they are approved for use. 
It can be quite a lengthy and arduous journey to get new tools approved in large organisations, and approval usually
comes with restrictions in use of functionality.

Further, Organisations need a tool to produce migration scripts rather than to execute and manage them. The use of
one tool to run manage a database state and migration scripts locks organisations into the tool and can result in staff
not entirely thinking through the consequences of their actions.

Organisations need a flexible tool to produce migrations, not necessarily execute them!

## Goals
DB Migration Builder was developed to address a gap in the understanding of existing tool providers, its goals are:
1. construct migration scripts for individual delta (change) scripts created by users
1. be flexible and extensible in the versioning structure (because each organisation is different!)
1. let users customise and extend functionality through a simple API
1. allow users to retain flexibility in control of the database 

# Concepts and Terminology
To use DB Migration Builder it is necessary to agree on the lingo.

### Delta Scripts
[Delta Scripts](https://github.com/dandelero/db-migration-builder/blob/master/db-migration-api/src/main/java/org/dandelero/dbmigrations/api/delta/DeltaScript.java) 
(also known as "change scripts") are SQL files created by users to transition a database resources (e.g. table, records, 
view) from one state to another. Developers write these SQL files just like they would do for any other tool!

### Modules
A [Module](https://github.com/dandelero/db-migration-builder/blob/master/db-migration-api/src/main/java/org/dandelero/dbmigrations/api/module/Module.java)
is a logical aggregation of related resources or a functional area of an application that is independently versioned.
For example, in a large CMS, you may have packages for "revenue", "opportunities", "contacts" etc, and each release
of the application may not necessarily result in changes to all these packages. Moreover, release 2.1 may have changes
just for the "revenue" module but 2.2 may include changes to "revenue" and "contacts". 
In this case it makes sense to version each package independently and allow each to be maintained separately.

Note that modules are optional.

### Version
A [Version](https://github.com/dandelero/db-migration-builder/blob/master/db-migration-api/src/main/java/org/dandelero/dbmigrations/api/version/Version.java)
is used to identify a certain point in the lifecycle of the application (or database scripts in this case). 

The following should make it clear what each of these terms represents:
```
- scripts
  - revenue                                 <-- Module: "revenue"
    - 1.0                                   <-- Version
      - 0001-create-projections-table.sql   <-- Delta script #1
      - 0002-projects-triggers.sql          <-- Delta script #2
    - 1.2
      - 0001-add-audit-trigger.sql
      - 0002-insert-new-admin.sql
  - contacts                                <-- Module: "contacts"
    - 0.0.1
      - 0001-create-customer-table.sql
      - 0002-add-dummy-customer.sql
    - 0.1
      - 0001-add-audit-triggers.sql
      - 0002-create-view-on-customer.sql
```

For the case where no modules are necessary (most systems) you will have the following layout:
```
- scripts
  - 1.0                                     <-- Version
    - 0001-create-projections-table.sql     <-- Delta script #1
    - 0002-projects-triggers.sql            <-- Delta script #2
  - 1.2
    - 0001-add-audit-trigger.sql
    - 0002-insert-new-admin.sql
```

# How do I start?
TODO: how to set a scheme
How to setup a config yaml if using a different scheme
How to lay out scripts (modules vs no modules)
Schemes provided by default

# Using DB Migrations Builder
The most common way to use DB Migration Builder is via a plugin, and we have [maven](https://github.com/dandelero/db-migration-builder-maven-plugin)
and [gradle](https://github.com/dandelero/db-migration-builder-gradle-plugin) plugins available. Check out the respective
pages for details on how to use DB Migration Builder via those plugins.

Take a look at our [sample projects](https://github.com/dandelero/db-migration-examples) on how to set up your directory 
structure.

If you prefer to use DB Migrations Builder manually or via a [ant](https://ant.apache.org/antlibs/proper.html) script read on.

1. Download the [latest build](TODO:https://github.com/dandelero/path-to-distribution): TODO: - set URL
1. Unzip the file into your preferred location, we'll refer to this as `DBMIG_HOME`
1. Run `$DBMIG_HOME/db-migration.sh` with the relevant parameters

The following parameters are available:
```$bash
Usage: db-migration.sh
    [-d <database>] the type of database to generate migrations for
    [-i <input directory>] the path to the input directory containing the modules/versions
    [-o <output directory>] the path to the output directory to write to
    [-v <version>] [optional] the version to be processed
    [-k <version scheme>] [optional] the scheme by which the versions abide
    [-m <module1, module2, ..., moduleN>] [optional] the names of the modules to be processed as a CSV string
```
### database
At the time of this writing the databases that come with out-of-the-box support are `mysql` and `mssql`, but you can 
very easily add support for other databases. Read the section on Customizing DB Migrations Builder [TODO - provide link]
to find out how to add support for other databases, but don't forget to [share your work](https://github.com/dandelero/db-migration-builder/pull/new/master) 
so we can add it into the core build for everyone to use!

### input directory
The path to the input directory that contains either the modules or the versions to be processed.

### output directory
The path to the output directory to write the generated migration scripts to.

### version
Optional - the version to generate migration scripts for. If omitted the latest version in the input directory 
(or module) will have migration scripts generated for.

Note that the version *must* match the name of the directory (i.e. version!) that you wish to generate scripts for.

### version scheme
Optional - the name of the versioning scheme used, and this *must* match the in either the [default configuration](https://github.com/dandelero/db-migration-builder/blob/master/db-migration-client/src/main/resources/conf/default-config.yaml)
or in your config file override. [TODO: link to customizing section]

The schemes supported in the bundled default configuration are `default-standard` and `default-semver1`, with the default
being `default-standard`; refer to the section on [version schemes](http://TODO:seturl) if you wish to build your own
scheme.

# Customizing
The application comes bundled with [default configuration](https://github.com/dandelero/db-migration-builder/blob/master/db-migration-client/src/main/resources/conf/default-config.yaml), 
but if you would like to customize the configuration you can do so by creating your very own `$DBMIG_HOME/conf/config.yaml` file

Th
TODO: how to setup config.yaml?

Add support for a custom database?

TODO: set up custom version scheme?

Templates?

# I love DB Migration Builder
If you use DB Migration Builder we'd love to hear from you and give your company/team a plug on our page! Any information
such as company name, team size, etc would help us shape this product.

# Contributing
This is open-source software and you are free to use it in any way you like under the Apache 2 License. 

## Technical contribution
If you'd like to add functionality, extend behaviour or fix a bug feel free to
[raise an issue](https://github.com/dandelero/db-migration-builder/issues/new) or 
[pull request](https://github.com/dandelero/db-migration-builder/pull/new/master).

# Financial contribution
We believe good software engineers write useful software to help out other engineers, but we do appreciate there are 
many good people out there that feel they'd like to make a financial contribution towards tools and products they use.

In this spirit we happily divert *all* financial donations/contributions towards charities and organisations that help 
children with life-long illnesses such as [Fragile X Syndrome](https://www.fragilex.org.au/). Yes, you read correctly, 
**ALL financial donations**. We even provide you with a copy of the proof/receipt from the recipient organisation.

Helping sick kids is one of our goals so if you really love DB Migration Builder and would like to [make a donation](http://todo) 
our little buddies that need a hand in life would really appreciate it. 

Whether it's small enough to provide a small toy or large enough to help provide therapy (physiotherapy, speech, chiropractic, 
occupational therapy, etc) you'll help a little friend have a better chance in life.

# Roadmap
Take a look at [our list](https://github.com/dandelero/db-migration-builder/issues) to see what we have on our roadmap.
If you'd like to see something added feel free to [raise a feature request](http://TODO-button-url)

TODO: GENERAL tasks
- Raise issue to add support for other databases {hsql, sybase, oracle, etc}
TODO: commit SQL to database for setup, perhaps in a resources folder?
