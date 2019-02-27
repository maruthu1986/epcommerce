# language: en
@importCustomerConsent
Feature: Import Customer Consent
  In order to migrate customer consents from an archive,
  As Operations,
  I want to import customer consents from the file system.

Scenario: Import Customer Consents
    And the customer consent import data has been emptied out
    And the customer consents to import of
      | guid  | dataPolicyGuid | consentDate         | customerFirstName |customerLastName | action  |
      | 12422 | 45678          | 2018-02-07T10:41:00.000Z | Jay               |Johnson          | GRANTED |
      | 65433 | 75333          | 2018-01-07T10:41:00.000Z | Jay               |Johnson          | GRANTED |
      | 64343 | 90743          | 2018-01-27T10:41:00.000Z | Jay               |Johnson          | REVOKED |
    And the customer [Jay Johnson] has been created for customer consent
    And the existing data policies of
      | guid  | dataPolicyName | description   | referenceKey  | retentionPeriod | policyState | retentionType      | segments | dataPoints |
      | 45678 | Policy 1       | Description 1 | DP12345	   | 100	         | DRAFT       | FROM_CREATION_DATE | US,CA,FR | 345;NAME1;CUSTOMER;ADDRESS LINE 1;Cust Address Line 1;true |
      | 75333 | Policy 2       | Description 2 | DP12346	   | 120	         | ACTIVE      | FROM_LAST_UPDATE   | US,CA,FR |            |
      | 90743 | Policy 3       | Description 3 | DP12347	   | 120	         | DISABLED    | FROM_CREATION_DATE | US,CA    | 345;NAME1;CUSTOMER;ADDRESS LINE 1;Cust Address Line 1;true,454;NAME2;CUSTOMER;NAME;Cust Name;false |
  When importing customer consents with the importexport tool
  Then the imported customer consents records should equal
    | guid  | dataPolicyGuid | consentDate         | customerFirstName |customerLastName | action  |
    | 12422 | 45678          | 2018-02-07T10:41:00.000Z | Jay               |Johnson          | GRANTED |
    | 65433 | 75333          | 2018-01-07T10:41:00.000Z | Jay               |Johnson          | GRANTED |
    | 64343 | 90743          | 2018-01-27T10:41:00.000Z | Jay               |Johnson          | REVOKED |

Scenario: Import Customer Consents with existing consents
  And the customer consent import data has been emptied out
  And the customer consents to import of
    | guid  | dataPolicyGuid | consentDate         | customerFirstName |customerLastName | action  |
    | 12422 | 45678          | 2018-02-07T10:41:00.000Z | Jay               |Johnson          | GRANTED |
    | 65433 | 75333          | 2018-01-07T10:41:00.000Z | Jay               |Johnson          | GRANTED |
    | 64343 | 90743          | 2018-01-27T10:41:00.000Z | Jay               |Johnson          | REVOKED |
  And the customer [Jay Johnson] has been created for customer consent
  And the existing data policies of
    | guid  | dataPolicyName | description   | referenceKey  | retentionPeriod | policyState | retentionType      | segments | dataPoints |
    | 45678 | Policy 1       | Description 1 | DP12345	   | 100	         | DRAFT       | FROM_CREATION_DATE | US,CA,FR | 345;NAME1;CUSTOMER;ADDRESS LINE 1;Cust Address Line 1;true |
    | 75333 | Policy 2       | Description 2 | DP12346	   | 120	         | ACTIVE      | FROM_LAST_UPDATE   | US,CA,FR |            |
    | 90743 | Policy 3       | Description 3 | DP12347	   | 120	         | DISABLED    | FROM_CREATION_DATE | US,CA    | 345;NAME1;CUSTOMER;ADDRESS LINE 1;Cust Address Line 1;true,454;NAME2;CUSTOMER;NAME;Cust Name;false |
  And the existing customer consents of
    | guid  | dataPolicyGuid | consentDate         | customerFirstName |customerLastName | action  |
    | 12422 | 45678          | 2018-02-07T10:41:00.000Z | Jay               |Johnson          | REVOKED |
  When importing customer consents with the importexport tool
  Then the imported customer consents records should equal
    | guid  | dataPolicyGuid | consentDate         | customerFirstName |customerLastName | action  |
    | 12422 | 45678          | 2018-02-07T10:41:00.000Z | Jay               |Johnson          | REVOKED |
    | 65433 | 75333          | 2018-01-07T10:41:00.000Z | Jay               |Johnson          | GRANTED |
    | 64343 | 90743          | 2018-01-27T10:41:00.000Z | Jay               |Johnson          | REVOKED |
  And In the summary are the unsupported customer consent operation warning messages of
    | code     | details |
    | IE-31300 | 12422   |

Scenario: Import Customer Consents with missing customer
    And the customer consent import data has been emptied out
    And the customer consents to import of
      | guid  | dataPolicyGuid | consentDate         | customerFirstName |customerLastName | action  |
      | 12422 | 45678          | 2018-02-07T10:41:00.000Z | Jay               |Johnson          | GRANTED |
      | 65433 | 75333          | 2018-01-07T10:41:00.000Z | Jay               |Johnson          | GRANTED |
      | 64343 | 90743          | 2018-01-27T10:41:00.000Z | James             |Bond             | REVOKED |
    And the customer [Jay Johnson] has been created for customer consent
    And the existing data policies of
      | guid  | dataPolicyName | description   | referenceKey  | retentionPeriod | policyState | retentionType      | segments | dataPoints |
      | 45678 | Policy 1       | Description 1 | DP12345	   | 100	         | DRAFT       | FROM_CREATION_DATE | US,CA,FR | 345;NAME1;CUSTOMER;ADDRESS LINE 1;Cust Address Line 1;true |
      | 75333 | Policy 2       | Description 2 | DP12346	   | 120	         | ACTIVE      | FROM_LAST_UPDATE   | US,CA,FR |            |
    When importing customer consents with the importexport tool
    Then the imported customer consents records should equal
      | guid  | dataPolicyGuid | consentDate         | customerFirstName |customerLastName | action  |
      | 12422 | 45678          | 2018-02-07T10:41:00.000Z | Jay               |Johnson          | GRANTED |
      | 65433 | 75333          | 2018-01-07T10:41:00.000Z | Jay               |Johnson          | GRANTED |
    And In the summary are the unsupported customer consent operation warning messages of
      | code     | details                 |
      | IE-31301 | 64343,guid_James_Bond   |

Scenario: Import Customer Consents with missing data policy
    And the customer consent import data has been emptied out
    And the customer consents to import of
      | guid  | dataPolicyGuid | consentDate         | customerFirstName |customerLastName | action  |
      | 12422 | 45678          | 2018-02-07T10:41:00.000Z | Jay               |Johnson          | GRANTED |
      | 65433 | 75333          | 2018-01-07T10:41:00.000Z | Jay               |Johnson          | GRANTED |
      | 64343 | 90743          | 2018-01-27T10:41:00.000Z | Jay               |Johnson          | REVOKED |
    And the customer [Jay Johnson] has been created for customer consent
    And the existing data policies of
      | guid  | dataPolicyName | description   | referenceKey  | retentionPeriod | policyState | retentionType      | segments | dataPoints |
      | 45678 | Policy 1       | Description 1 | DP12345	   | 100	         | DRAFT       | FROM_CREATION_DATE | US,CA,FR | 345;NAME1;CUSTOMER;ADDRESS LINE 1;Cust Address Line 1;true |
      | 75333 | Policy 2       | Description 2 | DP12346	   | 120	         | ACTIVE      | FROM_LAST_UPDATE   | US,CA,FR |            |
    When importing customer consents with the importexport tool
    Then the imported customer consents records should equal
      | guid  | dataPolicyGuid | consentDate         | customerFirstName |customerLastName | action  |
      | 12422 | 45678          | 2018-02-07T10:41:00.000Z | Jay               |Johnson          | GRANTED |
      | 65433 | 75333          | 2018-01-07T10:41:00.000Z | Jay               |Johnson          | GRANTED |
     And In the summary are the unsupported customer consent operation warning messages of
      | code     | details       |
      | IE-31302 | 64343,90743   |
