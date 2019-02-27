To create a deployment package for QA box, run

`mvn clean install -P with-deployment-package`.

To create a perf deployment package, without Batch and CM WARs, run

`mvn clean install -P with-deployment-package -Dexclude.batch.and.cm.wars`.

NOTE: If you are on Windows and Cygwin, escape `!`

To create a demo package, run

`mvn clean install -P with-demo-package`