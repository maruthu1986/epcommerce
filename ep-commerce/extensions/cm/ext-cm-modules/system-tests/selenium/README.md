# cm-selenium-test
**Before Running the Tests**

Ensure the CM you are testing against has the Test ID mode turned on.
In ext-cm-webapp-runner, run the following:
```
mvn clean tomcat7:run-war -Dorg.eclipse.rap.rwt.enableUITests=true
```

**Setting up commerce/ep-commerce/extensions/cm/ext-cm-modules/system-tests Project in IntelliJ**

1. Create Project from Existing Sources using the path above.
2. Select Import project from external model -> Choose maven
3. Select checkboxes for:
    * Search for projects recursively
    * Import Maven projects automatically
4. Click next until Finish.
5. Download the Cucumber.java and Cucumber.groovy plugins in IntelliJ
6. Go to File > Settings > Editor > File type > Cucumber Scenario
    * add *.feature to the Cucumber Scenario file type.

**Building Project**

Build the following projects using this command: mvn clean install -DskipAllTests
* commerce/ep-commerce/extensions/cm/ext-cm-modules/system-tests
* commerce/ep-commerce/extensions/cm/ext-cm-modules/system-tests/selenium

**Running Tests:**

mvn clean install -Dcucumber.options="--tags @smoketest"

Sanity Tests: @sanity

*Maven Options:*
* -Dcucumber.options="--tags @smoketest" - You can replace the tag to your own tag. 
* -Dfailsafe.fork.count="1" - This is the number of parallel tests at the same time. Default is 1 and can be changed to other values depending on number of TestsIT classes.
* -Premote -Dremote.web.driver.url="<REMOTE DRIVER IP>" - The "remote" triggers tests to be executed using remote VM. The "remote.web.driver.url" specifies the URL of the remote VM. e.g. "http://10.10.2.113:4444/wd/hub"
    * Note: You have to have selenium grid setup in order to use this feature. Please refer to official documentation on Selenium Grid.  

**Running subset of tests:**
* You can run a subset of tests by right clicking and run any one of TestsIT classes under /selenium/src/test/java/com/elasticpath/cucumber/
* You can create your own local runner class to run your own tagged tests. E.g. RunLocalTestsIT.java which runs your own tagged tests @local 
    * Do not commit the local runner class and tags as they are only for your local testing purpose. 

*Updating Browser Driver Versions*
* You can download the latest browser driver from web. e.g. chromedriver.
* Update the RepositoryMap.xml for the driver version.
* Has value can be found locally if you run following in bash command locally.
```
openssl sha1 <filename>
```
* Example: https://github.com/Ardesco/Selenium-Maven-Template/blob/master/src/test/resources/RepositoryMap.xml