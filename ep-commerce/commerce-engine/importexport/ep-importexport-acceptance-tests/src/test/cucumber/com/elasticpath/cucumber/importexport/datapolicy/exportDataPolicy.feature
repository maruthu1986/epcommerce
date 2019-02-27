# language: en
@exportDataPolicy
Feature: Export data policies
  In order to archive data policies for backup or data migration,
  As Operations,
  I want to export data policies to the file system.

Scenario: Export Data Policies
  Given the existing data policies of
    | guid  | dataPolicyName | description   | referenceKey  | retentionPeriod | policyState | retentionType      | segments | dataPoints |
    | 12345 | Policy 1       | Description 1 | DP12345	     | 100	           | DRAFT       | FROM_CREATION_DATE | US,CA,FR | 345;NAME1;CUSTOMER;ADDRESS LINE 1;Cust Address Line 1;true |
    | 12346 | Policy 2       | Description 2 | DP12346	     | 120	           | ACTIVE      | FROM_LAST_UPDATE   | US,CA,FR |            |
    | 12347 | Policy 3       | Description 3 | DP12347	     | 120	           | DISABLED    | FROM_CREATION_DATE | US,CA    | 345;NAME1;CUSTOMER;ADDRESS LINE 1;Cust Address Line 1;true,454;NAME2;CUSTOMER;NAME;Cust Name;false |
  When exporting data policies with the importexport tool
   And the exported data policies data is parsed
  Then the exported data policies records should include [12345] with policy state of [DRAFT]
   And the exported data policies records should include [12346] with policy state of [ACTIVE]
   And the exported data policies records should include [12347] with policy state of [DISABLED]
   And the exported manifest file should have an entry for data policies

Scenario: Export Data Policies with Data Points
  Given the existing data policies of
    | guid  | dataPolicyName | description   | referenceKey  | retentionPeriod | policyState | retentionType      | segments | dataPoints |
    | 45678 | Policy 1       | Description 1 | DP12345	     | 100	           | DRAFT       | FROM_CREATION_DATE | US,CA,FR | 345;NAME1;CUSTOMER;ADDRESS LINE 1;Cust Address Line 1;true |
    | 75333 | Policy 2       | Description 2 | DP12346	     | 120	           | ACTIVE      | FROM_LAST_UPDATE   | US,CA,FR |            |
    | 90743 | Policy 3       | Description 3 | DP12347	     | 120	           | DISABLED    | FROM_CREATION_DATE | US,CA    | 345;NAME1;CUSTOMER;ADDRESS LINE 1;Cust Address Line 1;true,454;NAME2;CUSTOMER;NAME;Cust Name;false |
   When exporting data policies with the importexport tool
    And the exported data policies data is parsed
   Then the exported data policy records should include [45678] with data points [345]
    And the exported data policy records should include [75333] with no data points
    And the exported data policy records should include [90743] with data points [345,454]
    And the exported manifest file should have an entry for data policies
