**Cortex Cucumber Tests**

Cucumber tests can be run with either OAuth2 (the default) or header authentication.  They can also be run using the built-in apps/H2 database (the default) or using a remote Cortex instance.

**Running with default built-in apps/H2 database**

To run Cucumber tests using the default built-in apps/H2 database in OAuth2, under cortex/system-tests run:
  ```java
  mvn clean install
   ```
  * You can optionally specify the authentication type since OAuth2 is the default type: 
  ```java
  mvn clean install -Dep.rest.authtype="OAuth2"
  ```

To run specific tagged Cucumber tests (ex. `@Addresses`) using the default built-in apps/H2 database in OAuth2, under cortex/system-tests run:
  ```java
  mvn clean install -Dcucumber.options="--tags @Addresses"
  ```
    
To run Cucumber tests using the default built-in apps/H2 database in header authentication run:
  ```java
  mvn clean install -Dep.rest.authtype="headerAuth"
  ```
  * Many out-of-the-box tests do not support header authentication.  Use the following command to run the out-of-the-box header authentication friendly tests only...
  
  To run specific tagged Cucumber tests using the default built-in apps/H2 database in header authentication run:
   ```java
 mvn clean install -Dep.rest.authtype="headerAuth" -Dcucumber.options="--tags @HeaderAuth"
   ```
    
To run Cucumber tests locally using in `Command Line tool` with ignoring the active profile 'setup-local-integration-test' run:
  ```java
  mvn clean install -Dep.rest.baseurl="http://localhost:9080/cortex" -Dep.jms.url="tcp://localhost:61616" -P \!setup-local-integration-test
  ```

To run Cucumber tests locally using in an `IntelliJ`, need to generate the properties file first and then can run from the `CucumberRunnerIT`:
  ```java
  mvn clean install -Dep.rest.baseurl="http://localhost:9080/cortex" -Dep.jms.url="tcp://localhost:61616" -P \!setup-local-integration-test
  ```

**Running against a remote Cortex instance**

Under cortex/system-tests/cucumber, add the following to ignore the active profile 'setup-local-integration-test' that starts the built-in apps/H2 database and specify where Cortex is:
  
  *For Mac / Linux users:* 
  ```java
  -P \!setup-local-integration-test -Dep.rest.baseurl="http://[resource_name]:[port]/cortex"
  ```
  *For Windows users:* 
  ```java
  -P !setup-local-integration-test -Dep.rest.baseurl="http://[resource_name]:[port]/cortex"
  ```
  
  * Ex. `mvn clean install -P \!setup-local-integration-test -Dep.rest.baseurl="http://localhost:9080/cortex"`
  You can optionally add `-Dcucumber.options="--tags @Addresses --glue classpath:com.elasticpath.cortex.dce"` for any specific set of tests to run.