This is a template that contains empty Import/Export and Liquibase configuration. To use...

1. Copy to the data directory and rename (e.g. rename data/data-template to data/rel-1.x-data)

2. Replace all instances of @new-data-dir with the new directory name (e.g. with rel-1.x-data)

3. Add an <include> statement to /src/main/data/liquibase-changelog.xml before test-data.

   <include file="rel-1.x-data/liquibase-changelog.xml" relativeToChangelogFile="true" />

4. Add a default qualifier to config/filtering.properties:

   importexport.rel-1.x-data.qualifier=

5. Override the qualifier in environments/local/filtering.properties (and other environments as needed)

   importexport.rel-1.x-data.qualifier=${timestamp.qualifier}


