# Prerequisite
  * build project /ep-commerce/extensions/cortex/system-tests to install required dependency - mvn clean install -DskipAllTests

## Import Project in IntelliJ:
1. File -> New -> Project from Existing Sources....
2. Select the /ep-commerce/extensions/cortex/ext-system-tests folder
3. Select Import project from external model option and choose Maven from the list
4. Click Next in the subsequent screens then click Finish

## Add Test Sources Root in IntelliJ:
1. Build project /ep-commerce/extensions/cortex/ext-system-tests using "mvn clean install -DskipAllTests"
2. From the Project navigation window right-click ext-system-tests/cucumber/target/com and click Mark Directory as > Test Sources Root

The implementation of statements within feature files should now be visible as groovy scripts in the directory above.
If they do not appear, restart your IDE.

## Modifying core step definitions
If the core step definitions need to be modified, do not change the groovy scripts in:
* /ext-system-tests/cucumber/target/com

as those changes only remain locally. Instead make your changes in:
* /ep-commerce/extensions/cortex/system-tests/common-test-defintions/src/main/java/com.elasticpath/cucumber.definitions

Rebuild the projects below, using "mvn clean install -DskipAllTests"
1. /ep-commerce/extensions/cortex/system-tests
2. /ep-commerce/extensions/cortex/ext-system-tests

## Adding Tests and Step Definitions:
* Create features under test/resources/features/<YOUR_FOLDER>
* Create Step Definitions under test/java/com/elasticpath/cortex/dce/<YOUR_FOLDER>

## Running against a remote Cortex instance##
  * mvn clean install -Dep.rest.baseurl="http://<REMOTE_SERVER_URL>/cortex"
  * You can optionally add -Dcucumber.options="--tags @example --glue classpath:com.elasticpath.cortex.dce" for any specific set of tests to run.
