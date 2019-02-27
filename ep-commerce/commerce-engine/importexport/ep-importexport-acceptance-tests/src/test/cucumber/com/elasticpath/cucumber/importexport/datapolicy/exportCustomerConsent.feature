# language: en
@exportCustomerConsent
Feature: Export Customer Consent
  In order to archive customer consents for backup or data migration,
  As Operations,
  I want to export customer consents to the file system.

Scenario: Export Customer Consents
  Given the customer [Jay Johnson] has been created for customer consent
    And the existing data policies of
      | guid  | dataPolicyName | description   | referenceKey  | retentionPeriod | policyState | retentionType      | segments | dataPoints |
      | 45678 | Policy 1       | Description 1 | DP12345	   | 100	         | DRAFT       | FROM_CREATION_DATE | US,CA,FR | 345;NAME1;CUSTOMER;ADDRESS LINE 1;Cust Address Line 1;true |
      | 75333 | Policy 2       | Description 2 | DP12346	   | 120	         | ACTIVE      | FROM_LAST_UPDATE   | US,CA,FR |            |
      | 90743 | Policy 3       | Description 3 | DP12347	   | 120	         | DISABLED    | FROM_CREATION_DATE | US,CA    | 345;NAME1;CUSTOMER;ADDRESS LINE 1;Cust Address Line 1;true,454;NAME2;CUSTOMER;NAME;Cust Name;false |
    And the existing customer consents of
      | guid  | dataPolicyGuid | consentDate         | customerFirstName |customerLastName | action  |
      | 12422 | 45678          | 2018-02-07T10:41:00.000Z | Jay               |Johnson          | GRANTED |
      | 65433 | 75333          | 2018-01-07T10:41:00.000Z | Jay               |Johnson          | GRANTED |
      | 64343 | 90743          | 2018-01-27T10:41:00.000Z | Jay               |Johnson          | REVOKED |
  When exporting customer consents with the importexport tool
   And the exported customer consents data is parsed
  Then the exported customer consents records should equal
      | guid  | dataPolicyGuid | consentDate         | customerFirstName |customerLastName | action  |
      | 12422 | 45678          | 2018-02-07T10:41:00.000Z | Jay               |Johnson          | GRANTED |
      | 65433 | 75333          | 2018-01-07T10:41:00.000Z | Jay               |Johnson          | GRANTED |
      | 64343 | 90743          | 2018-01-27T10:41:00.000Z | Jay               |Johnson          | REVOKED |
   And the exported manifest file should have an entry for customer consents
