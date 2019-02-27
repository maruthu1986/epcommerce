# Commerce-extension cortex system-tests
To run the test suite run `mvn clean install`.
As a precondition you might want to rebuild the webapp package if you have changes to other related github projects you want to integration test.
In order to do that run `mvn clean install` from the root folder of system-tests which is `cortex`.
## Project structure
```
system-tests
|- cucumber // cucumber based test suite
|- tests-data // test data for filling a test database
```

## Review results of test suite execution
* Cucumber results are published to: `cucumber/target/cucumber-html-report/index.html`
