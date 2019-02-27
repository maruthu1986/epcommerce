# language: en
#  NOTE: once the current date passed the retention period, the tests will fail.
@dataPointValuesJobProcessors
Feature: Scheduled data point value jobs for data points shared by data policies

  Background:
    Given a customer with billing address of
      | guid    | lastName | firstName | street1       | street2       | city     | country | state    | zip | phone      | creationDate  | lastModifiedDate  |
      | abc123  | Bird     | Big       | Sesame Street | Robson Street | New York | USA     | New York | zip | 5555555555 | 1 month ago   | 1 month ago       |

  Scenario: Expired data point value job should NOT remove billing address data point values if longest retention period is not expired
    Given the existing data policies of
      | guid  | dataPolicyName | description   | referenceKey | retentionPeriod | policyState | retentionType      | segments | dataPoints                                                         |
      | 12345 | Policy 1       | Description 1 | DP12345      | 5               | ACTIVE      | FROM_CREATION_DATE | US,CA,FR | 345;NAME1;CUSTOMER_BILLING_ADDRESS;FIRST_NAME;Cust first name;true |
      | 12346 | Policy 2       | Description 2 | DP12346      | 1000            | ACTIVE      | FROM_CREATION_DATE | US,CA,FR | 345;NAME1;CUSTOMER_BILLING_ADDRESS;FIRST_NAME;Cust first name;true |
      | 12347 | Policy 3       | Description 3 | DP12347      | 500             | ACTIVE      | FROM_CREATION_DATE | US,CA,FR | 345;NAME1;CUSTOMER_BILLING_ADDRESS;FIRST_NAME;Cust first name;true |
    And the existing customer consents of
      | guid  | customerGuid  | dataPolicyGuid | consentDate  | action  |
      | 12422 | abc123        | 12345          | 1 month ago  | GRANTED |
      | 12423 | abc123        | 12346          | 1 month ago  | GRANTED |
      | 12424 | abc123        | 12347          | 1 month ago  | GRANTED |
    When expired data point values job processor runs
    Then the data point value with customer guid [abc123] and data point guid [345] should have value [Big]

  Scenario: Expired data point values job should NOT remove data point values regardless of data policy states if longest retention period
  is not expired
    Given the existing data policies of
      | guid  | dataPolicyName | description   | referenceKey | retentionPeriod | policyState | retentionType      | segments | dataPoints                                                         |
      | 12345 | Policy 1       | Description 1 | DP12345      | 5               | DISABLED    | FROM_CREATION_DATE | US,CA,FR | 345;NAME1;CUSTOMER_BILLING_ADDRESS;FIRST_NAME;Cust first name;true |
      | 12346 | Policy 2       | Description 2 | DP12346      | 1000            | ACTIVE      | FROM_CREATION_DATE | US,CA,FR | 345;NAME1;CUSTOMER_BILLING_ADDRESS;FIRST_NAME;Cust first name;true |
    And the existing customer consents of
      | guid  |customerGuid  | dataPolicyGuid | consentDate | action  |
      | 12422 | abc123       | 12345         | 1 month ago | GRANTED |
      | 12423 | abc123       | 12346         | 1 month ago | GRANTED |
    When expired data point values job processor runs
    Then the data point value with customer guid [abc123] and data point guid [345] should have value [Big]

  Scenario: Revoked consent data point value job should remove data point values only for REVOKED consents regardless of data policy states
    Given the existing data policies of
      | guid  | dataPolicyName | description   | referenceKey | retentionPeriod | policyState | retentionType      | segments | dataPoints                                                         |
      | 12345 | Policy 1       | Description 1 | DP12345      | 5               | DISABLED    | FROM_CREATION_DATE | US,CA,FR | 345;NAME1;CUSTOMER_BILLING_ADDRESS;FIRST_NAME;Cust first name;true |
      | 12346 | Policy 2       | Description 1 | DP12346      | 5               | ACTIVE      | FROM_CREATION_DATE | US,CA,FR | 345;NAME1;CUSTOMER_BILLING_ADDRESS;FIRST_NAME;Cust first name;true |
    And the existing customer consents of
      | guid  |customerGuid  | dataPolicyGuid | consentDate | action  |
      | 12422 | abc123       | 12345          | 1 month ago | REVOKED |
      | 12423 | abc123       | 12346          | 1 month ago | REVOKED |
    When revoked consents data point values job processor runs
    Then the data point value with customer guid [abc123] and data point guid [345] should have value [‐]