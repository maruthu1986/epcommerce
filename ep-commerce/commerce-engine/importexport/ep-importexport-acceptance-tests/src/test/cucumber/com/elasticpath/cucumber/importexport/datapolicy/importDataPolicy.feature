# language: en
@importDataPolicy
Feature: Import Data Policy
  In order to migrate data policies from an archive,
  As Operations,
  I want to import data policies from the file system.

Scenario: Import Data Policies with policy states
    And the data policy import data has been emptied out
    And the data policies to import of
      | guid  | dataPolicyName | description   | referenceKey  | retentionPeriod | policyState | retentionType      | segments | dataPoints |
      | 12345 | Policy 1       | Description 1 | DP12345	   | 100	         | DRAFT       | FROM_CREATION_DATE | US,CA,FR | 345;NAME1;CUSTOMER;ADDRESS LINE 1;Cust Address Line 1;true |
      | 12346 | Policy 2       | Description 2 | DP12346	   | 120	         | ACTIVE      | FROM_LAST_UPDATE   | US,CA,FR |            |
      | 12347 | Policy 3       | Description 3 | DP12347	   | 120	         | DISABLED    | FROM_CREATION_DATE | US,CA    | 345;NAME1;CUSTOMER;ADDRESS LINE 1;Cust Address Line 1;true,454;NAME2;CUSTOMER;NAME;Cust Name;false |
  When importing data policies with the importexport tool
  Then the data policy [12345] should have a policy state of [DRAFT]
   And the data policy [12346] should have a policy state of [ACTIVE]
   And the data policy [12347] should have a policy state of [DISABLED]

Scenario: Import Data Policies with Data Points
  Given the data policy import data has been emptied out
  And the data policies to import of
    | guid  | dataPolicyName | description   | referenceKey  | retentionPeriod | policyState | retentionType      | segments | dataPoints |
    | 12345 | Policy 1       | Description 1 | DP12345	     | 100	           | DRAFT       | FROM_CREATION_DATE | US,CA,FR | 345;NAME1;CUSTOMER;ADDRESS LINE 1;Cust Address Line 1;true |
    | 12346 | Policy 2       | Description 2 | DP12346	     | 120	           | ACTIVE      | FROM_LAST_UPDATE   | US,CA,FR |            |
    | 12347 | Policy 3       | Description 3 | DP12347	     | 120	           | DISABLED    | FROM_CREATION_DATE | US,CA    | 345;NAME1;CUSTOMER;ADDRESS LINE 1;Cust Address Line 1;true,454;NAME2;CUSTOMER;NAME;Cust Name;false |
  When importing data policies with the importexport tool
  Then the data policy [12345] should have data points [345]
   And the data policy [12346] should have no data points
   And the data policy [12347] should have data points [345,454]

Scenario: Import Data Policies with existing Data Policies
    Given the data policy import data has been emptied out
    And the existing data policies of
      | guid  | dataPolicyName | description   | referenceKey    | retentionPeriod | policyState | retentionType      | segments | dataPoints |
      | 45678 | Policy 1       | Description 1 | DP12345	     | 100	           | DRAFT       | FROM_CREATION_DATE | US,CA,FR | 345;NAME1;CUSTOMER;ADDRESS LINE 1;Cust Address Line 1;true |
      | 75333 | Policy 2       | Description 2 | DP12346	     | 120	           | ACTIVE      | FROM_LAST_UPDATE   | US,CA,FR |            |
    And the data policies to import of
      | guid  | dataPolicyName | description   | referenceKey  | retentionPeriod | policyState | retentionType      | segments | dataPoints |
      | 45678 | Policy 1       | Description 1 | DP12345	   | 100	         | DRAFT       | FROM_CREATION_DATE | US,CA,FR | 346;NAME3;CUSTOMER;ADDRESS LINE 2;Cust Address Line 2;true |
      | 75333 | Policy 2       | Description 2 | DP12346	   | 120	         | ACTIVE      | FROM_LAST_UPDATE   | US,CA,FR | 454;NAME2;CUSTOMER;NAME;Cust Name;false |
      | 12347 | Policy 3       | Description 3 | DP12347	   | 120	         | DISABLED    | FROM_CREATION_DATE | US,CA    | 345;NAME1;CUSTOMER;ADDRESS LINE 1;Cust Address Line 1;true,454;NAME2;CUSTOMER;NAME;Cust Name;false |
    When importing data policies with the importexport tool
    Then the data policy [45678] should have data points [345]
    And the data policy [75333] should have no data points
    And the data policy [12347] should have data points [345,454]
    And In the summary are the unsupported data policy operation warning messages of
     | code     | details |
     | IE-31200 | 45678   |
     | IE-31200 | 75333   |

Scenario: Import Data Policies with existing Data Policies with different data point attributes
    Given the data policy import data has been emptied out
    And the existing data policies of
      | guid  | dataPolicyName | description   | referenceKey    | retentionPeriod | policyState | retentionType      | segments | dataPoints |
      | 45678 | Policy 1       | Description 1 | DP12345	     | 100	           | DRAFT       | FROM_CREATION_DATE | US,CA,FR | 345;NAME1;CUSTOMER;ADDRESS LINE 2;Cust Address Line 1;true |
     And the data policies to import of
      | guid  | dataPolicyName | description   | referenceKey  | retentionPeriod | policyState | retentionType      | segments | dataPoints |
      | 12347 | Policy 3       | Description 3 | DP12347	   | 120	         | DISABLED    | FROM_CREATION_DATE | US,CA    | 345;NAME1;CUSTOMER;ADDRESS LINE 1;Cust Address Line 1;true,454;NAME2;CUSTOMER;NAME;Cust Name;false |
      | 75333 | Policy 2       | Description 2 | DP12346	   | 120	         | ACTIVE      | FROM_LAST_UPDATE   | US,CA,FR | 454;NAME2;CUSTOMER;NAME;Cust Name;false |
    When importing data policies with the importexport tool
    Then the data policy [45678] should have data points [345]
    And the data policy [75333] should have data points [454]
    And In the summary are the unsupported data policy operation error messages of
      | code     | details |
      | IE-31201 | 12347,345   |
      | IE-30407 | 1,DATA_POLICY   |

Scenario: Import Data Policies with existing Data Policies with different data point attributes because of case sensitivity
    Given the data policy import data has been emptied out
    And the existing data policies of
      | guid  | dataPolicyName | description   | referenceKey  | retentionPeriod | policyState | retentionType      | segments | dataPoints |
      | 45678 | Policy 1       | Description 1 | DP12345	   | 100	         | DRAFT       | FROM_CREATION_DATE | US,CA,FR | 345;NAME1;CUSTOMER;ADDRESS LINE 1;Cust Address Line 1;true |
    And the data policies to import of
      | guid  | dataPolicyName | description   | referenceKey  | retentionPeriod | policyState | retentionType      | segments | dataPoints |
      | 12347 | Policy 3       | Description 3 | DP12347	   | 120	         | DISABLED    | FROM_CREATION_DATE | US,CA    | 345;NAME1;Customer;Address Line 1;Cust Address Line 1;true,454;NAME2;CUSTOMER;NAME;Cust Name;false |
      | 75333 | Policy 2       | Description 2 | DP12346	   | 120	         | ACTIVE      | FROM_LAST_UPDATE   | US,CA,FR | 454;NAME2;CUSTOMER;NAME;Cust Name;false |
    When importing data policies with the importexport tool
    Then the data policy [45678] should have data points [345]
    And the data policy [75333] should have data points [454]
    And In the summary are the unsupported data policy operation error messages of
      | code     | details |
      | IE-31201 | 12347,345   |
      | IE-30407 | 1,DATA_POLICY   |
