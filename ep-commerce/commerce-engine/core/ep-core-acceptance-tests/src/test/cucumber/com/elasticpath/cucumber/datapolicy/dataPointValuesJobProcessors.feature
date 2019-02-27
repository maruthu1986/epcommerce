# language: en
@dataPointValuesJobProcessors
Feature: Scheduled data point value jobs

  Background:
    Given a customer with billing address of
      | guid    | lastName | firstName | street1       | street2       | city     | country | state    | zip | phone      | creationDate  | lastModifiedDate  |
      | abc123  | Bird     | Big       | Sesame Street | Robson Street | New York | USA     | New York | zip | 5555555555 | 1 month ago   | 1 month ago       |

    Given a customer with billing address of
      | guid    | lastName | firstName | street1       | street2       | city     | country | state    | zip | phone      | creationDate  | lastModifiedDate |
      | abc345  | Smith    | John      | Sesame Street | Robson Street | New York | USA     | New York | zip | 5555555555 | 1 month ago   | 1 month ago      |

    Given a customer with billing address of
      | guid    | lastName | firstName | street1       | street2       | city     | country | state    | zip | phone      | creationDate  | lastModifiedDate |
      | abc678  | Smith    | Steve     | Sesame Street | Robson Street | New York | USA     | New York | zip | 5555555555 | 3 days  ago   | 1 day ago        |

  Scenario: Expired data point value job should remove
  billing address data point values when retention period has passed.

    Given the existing data policies of
      | guid  | dataPolicyName | description   | referenceKey | retentionPeriod | policyState | retentionType      | segments | dataPoints                                                         |
      | 12345 | Policy 1       | Description 1 | DP12345      | 5               | ACTIVE      | FROM_CREATION_DATE | US,CA,FR | 345;NAME1;CUSTOMER_BILLING_ADDRESS;FIRST_NAME;Cust first name;true |

    And the existing customer consents of
      | guid  | customerGuid  | dataPolicyGuid | consentDate              | action  |
      | 12422 | abc123        | 12345          | 1 month ago              | GRANTED |

    When expired data point values job processor runs
    Then the data point value with customer guid [abc123] and data point guid [345] should have value [‐]


  Scenario: Expired data point value job should not remove
  any data point values if most recent consent has been revoked.

    Given the existing data policies of
      | guid  | dataPolicyName | description   | referenceKey | retentionPeriod | policyState | retentionType      | segments | dataPoints                                                         |
      | 12345 | Policy 1       | Description 1 | DP12345      | 5               | ACTIVE      | FROM_CREATION_DATE | US,CA,FR | 345;NAME1;CUSTOMER_BILLING_ADDRESS;FIRST_NAME;Cust first name;true |

    And the existing customer consents of
      | guid  | customerGuid  | dataPolicyGuid | consentDate              | action  |
      | 12422 | abc123        | 12345          | 29 days ago              | GRANTED |
      | 12423 | abc123        | 12345          | 28 days ago              | REVOKED |

    When expired data point values job processor runs
    Then the data point value with customer guid [abc123] and data point guid [345] should have value [Big]

  Scenario: Expired data point values job should remove
  data point values even if data policy state is DISABLED.

    Given the existing data policies of
      | guid  | dataPolicyName | description   | referenceKey | retentionPeriod | policyState | retentionType      | segments | dataPoints                                                         |
      | 12345 | Policy 1       | Description 1 | DP12345      | 5               | DISABLED    | FROM_CREATION_DATE | US,CA,FR | 345;NAME1;CUSTOMER_BILLING_ADDRESS;FIRST_NAME;Cust first name;true |

    And the existing customer consents of
      | guid  | customerGuid  | dataPolicyGuid | consentDate              | action  |
      | 12422 | abc123        | 12345          | 1 month ago              | GRANTED |

    When expired data point values job processor runs
    Then the data point value with customer guid [abc123] and data point guid [345] should have value [‐]

  Scenario: Expired data point values job should remove
  data point values only if comply with all associated
  data policies retention types and retention periods.
  Thus, data point value could not be removed when
  retention period has not passed for any of data policies.

    Given the existing data policies of
      | guid   | dataPolicyName | description   | referenceKey | retentionPeriod | policyState | retentionType      | segments | dataPoints                                                                                                                                                             |
      | 12345  | Policy 1       | Description 1 | DP12345      | 5               | ACTIVE      | FROM_CREATION_DATE | US,CA,FR | 345;BILLING_FIRST_NAME;CUSTOMER_BILLING_ADDRESS;FIRST_NAME;Cust first name;true, 346;BILLING_LAST_NAME;CUSTOMER_BILLING_ADDRESS;LAST_NAME;Cust last name;true          |
      | 123456 | Policy 2       | Description 2 | DP123456     | 1000            | ACTIVE      | FROM_CREATION_DATE | US,CA,FR | 345;BILLING_FIRST_NAME;CUSTOMER_BILLING_ADDRESS;FIRST_NAME;Cust first name;true, 347;BILLING_PHONE_NUMBER;CUSTOMER_BILLING_ADDRESS;PHONE_NUMBER;Cust phone number;true |

    And the existing customer consents of
      | guid  | customerGuid  | dataPolicyGuid | consentDate              | action  |
      | 12422 | abc123        | 12345          | 1 month ago              | GRANTED |
      | 12423 | abc123        | 123456         | 1 month ago              | GRANTED |

    When expired data point values job processor runs
    Then the data point value with customer guid [abc123] and data point guid [345] should have value [Big]
    And the data point value with customer guid [abc123] and data point guid [346] should have value [‐]
    And the data point value with customer guid [abc123] and data point guid [347] should have value [5555555555]

  Scenario: Expired data point values job should remove
  data point values only if comply with all associated
  data policies retention types and retention periods
  and even if data policy has expired.

    Given the existing data policies of
      | guid  | dataPolicyName | description   | referenceKey | retentionPeriod | policyState | retentionType      | endDate                  | segments | dataPoints                                                         |
      | 12345 | Policy 1       | Description 1 | DP12345      | 5               | ACTIVE      | FROM_CREATION_DATE | 3 weeks ago              | US,CA,FR |345;NAME1;CUSTOMER_BILLING_ADDRESS;FIRST_NAME;Cust first name;true |

    And the existing customer consents of
      | guid  | customerGuid  | dataPolicyGuid | consentDate              | action  |
      | 12422 | abc123        | 12345          | 1 month ago              | GRANTED |

    When expired data point values job processor runs
    Then the data point value with customer guid [abc123] and data point guid [345] should have value [‐]

  Scenario: Expired data point values job should not remove
  data point values if data point configured to be non-removable.

    Given the existing data policies of
      | guid  | dataPolicyName | description   | referenceKey | retentionPeriod | policyState | retentionType      | segments | dataPoints                                                          |
      | 12345 | Policy 1       | Description 1 | DP12345      | 5               | ACTIVE      | FROM_CREATION_DATE | US,CA,FR |345;NAME1;CUSTOMER_BILLING_ADDRESS;FIRST_NAME;Cust first name;false |

    And the existing customer consents of
      | guid  | customerGuid  | dataPolicyGuid | consentDate              | action  |
      | 12422 | abc123        | 12345          | 3 weeks ago              | GRANTED |

    When expired data point values job processor runs
    Then the data point value with customer guid [abc123] and data point guid [345] should have value [Big]

  Scenario: Expired data point values job should not remove
  data point values if data point is configured to be non-removable
  even if policy has been disabled.

    Given the existing data policies of
      | guid  | dataPolicyName | description   | referenceKey | retentionPeriod | policyState | retentionType      | segments | dataPoints                                                          |
      | 12345 | Policy 1       | Description 1 | DP12345      | 5               | DISABLED    | FROM_CREATION_DATE | US,CA,FR | 345;NAME1;CUSTOMER_BILLING_ADDRESS;FIRST_NAME;Cust first name;false |

    And the existing customer consents of
      | guid  | customerGuid  | dataPolicyGuid | consentDate              | action  |
      | 12422 | abc123        | 12345          | 1 month ago              | GRANTED |

    When expired data point values job processor runs
    Then the data point value with customer guid [abc123] and data point guid [345] should have value [Big]

  Scenario: Expired data point values job should remove
  data point values for DISABLED data policy with retention type FROM_LAST_UPDATE
  if data policy end date is less than data point value last modified date.

    And the existing data policies of
      | guid  | dataPolicyName | description   | referenceKey | retentionPeriod | policyState | retentionType    | endDate                  | segments | dataPoints                                                         |
      | 12345 | Policy 1       | Description 1 | DP12345      | 5               | DISABLED    | FROM_LAST_UPDATE | 3 weeks ago              | US,CA,FR | 345;NAME1;CUSTOMER_BILLING_ADDRESS;FIRST_NAME;Cust first name;true |

    And the existing customer consents of
      | guid  | customerGuid  | dataPolicyGuid | consentDate              | action  |
      | 12422 | abc123        | 12345          | 1 month ago              | GRANTED |

    When expired data point values job processor runs
    Then the data point value with customer guid [abc123] and data point guid [345] should have value [‐]

  Scenario: Revoked consent data point value job should not remove
  data point values for GRANTED consents.

    Given the existing data policies of
      | guid  | dataPolicyName | description   | referenceKey | retentionPeriod | policyState | retentionType      | segments | dataPoints                                                         |
      | 12345 | Policy 1       | Description 1 | DP12345      | 5               | ACTIVE      | FROM_CREATION_DATE | US,CA,FR | 345;NAME1;CUSTOMER_BILLING_ADDRESS;FIRST_NAME;Cust first name;true |

    And the existing customer consents of
      | guid  | customerGuid  | dataPolicyGuid | consentDate              | action  |
      | 12422 | abc123        | 12345          | 1 month ago              | GRANTED |

    When revoked consents data point values job processor runs
    Then the data point value with customer guid [abc123] and data point guid [345] should have value [Big]

  Scenario: Revoked consent data point value job should remove
  data point values for REVOKED consents.

    Given the existing data policies of
      | guid  | dataPolicyName | description   | referenceKey | retentionPeriod | policyState | retentionType      | segments | dataPoints                                                         |
      | 12345 | Policy 1       | Description 1 | DP12345      | 5               | ACTIVE      | FROM_CREATION_DATE | US,CA,FR | 345;NAME1;CUSTOMER_BILLING_ADDRESS;FIRST_NAME;Cust first name;true |

    And the existing customer consents of
      | guid  | customerGuid  | dataPolicyGuid | consentDate              | action  |
      | 12422 | abc123        | 12345          | 1 month ago              | REVOKED |

    When revoked consents data point values job processor runs
    Then the data point value with customer guid [abc123] and data point guid [345] should have value [‐]

  Scenario: Revoked consent data point value job should remove
  data point values only for revoked consents and only if data point is
  configured to be removable.

    Given the existing data policies of
      | guid  | dataPolicyName | description   | referenceKey | retentionPeriod | policyState | retentionType      | segments | dataPoints                                                          |
      | 12345 | Policy 1       | Description 1 | DP12345      | 5               | ACTIVE      | FROM_CREATION_DATE | US,CA,FR | 345;NAME1;CUSTOMER_BILLING_ADDRESS;FIRST_NAME;Cust first name;false |

    And the existing customer consents of
      | guid  | customerGuid  | dataPolicyGuid | consentDate              | action  |
      | 12422 | abc123        | 12345          | 1 month ago              | REVOKED |

    When revoked consents data point values job processor runs
    Then the data point value with customer guid [abc123] and data point guid [345] should have value [Big]

  Scenario: Revoked consent data point value job should remove
  data point values only for revoked consents and only if data point
  comply with all associated data policies retention types
  and retention periods.

    Given the existing data policies of
      | guid  | dataPolicyName | description   | referenceKey | retentionPeriod | policyState | retentionType      | segments | dataPoints                                                         |
      | 12345 | Policy 1       | Description 1 | DP12345      | 5               | DISABLED    | FROM_CREATION_DATE | US,CA,FR | 345;NAME1;CUSTOMER_BILLING_ADDRESS;FIRST_NAME;Cust first name;true |
      | 12346 | Policy 2       | Description 2 | DP12345      | 1000            | ACTIVE      | FROM_CREATION_DATE | US,CA,FR | 345;NAME1;CUSTOMER_BILLING_ADDRESS;FIRST_NAME;Cust first name;true |

    And the existing customer consents of
      | guid  | customerGuid  | dataPolicyGuid | consentDate              | action  |
      | 12422 | abc123        | 12345          | 1 month ago              | REVOKED |
      | 12423 | abc123        | 12346          | 1 month ago              | GRANTED |

    When revoked consents data point values job processor runs
    Then the data point value with customer guid [abc123] and data point guid [345] should have value [Big]

  Scenario: Verify data points values removed by expired data point values job,
    not the revoked consents data point values job, when expiry period reached
    and the data point has both revoked and granted consents.

    Given the existing data policies of
      | guid  | dataPolicyName | description   | referenceKey | retentionPeriod | policyState | retentionType      | segments | dataPoints                                                         |
      | 12345 | Policy 1       | Description 1 | DP12345      | 5               | DISABLED    | FROM_CREATION_DATE | US,CA,FR | 345;NAME1;CUSTOMER_BILLING_ADDRESS;FIRST_NAME;Cust first name;true |
      | 12346 | Policy 2       | Description 2 | DP12345      | 5               | ACTIVE      | FROM_CREATION_DATE | US,CA,FR | 345;NAME1;CUSTOMER_BILLING_ADDRESS;FIRST_NAME;Cust first name;true |

    And the existing customer consents of
      | guid  | customerGuid  | dataPolicyGuid | consentDate              | action  |
      | 12422 | abc123        | 12345          | 1 month ago              | REVOKED |
      | 12423 | abc123        | 12346          | 1 month ago              | GRANTED |

    When revoked consents data point values job processor runs
    Then the data point value with customer guid [abc123] and data point guid [345] should have value [Big]
    And expired data point values job processor runs
    Then the data point value with customer guid [abc123] and data point guid [345] should have value [‐]

  Scenario: Revoked consent data point value job should remove
  data point values only for REVOKED consents no matter what policy state is.

    Given the existing data policies of
      | guid  | dataPolicyName | description   | referenceKey | retentionPeriod | policyState | retentionType      | segments | dataPoints                                                         |
      | 12345 | Policy 1       | Description 1 | DP12345      | 5               | DISABLED    | FROM_CREATION_DATE | US,CA,FR | 345;NAME1;CUSTOMER_BILLING_ADDRESS;FIRST_NAME;Cust first name;true |

    And the existing customer consents of
      | guid  | customerGuid  | dataPolicyGuid | consentDate              | action  |
      | 12422 | abc123        | 12345          | 1 month ago              | REVOKED |

    When revoked consents data point values job processor runs
    Then the data point value with customer guid [abc123] and data point guid [345] should have value [‐]

  Scenario: Revoked consent data point value job should remove
  data point values only for revoked consents and only if data point
  comply with all associated data policies retention types
  and retention periods.

    Given the existing data policies of
      | guid  | dataPolicyName | description   | referenceKey | retentionPeriod | policyState | retentionType      | segments | dataPoints                                                         |
      | 12345 | Policy 1       | Description 1 | DP12345      | 5               | DISABLED    | FROM_CREATION_DATE | US,CA,FR | 345;NAME1;CUSTOMER_BILLING_ADDRESS;FIRST_NAME;Cust first name;true |
      | 12346 | Policy 2       | Description 1 | DP12345      | 5               | ACTIVE      | FROM_CREATION_DATE | US,CA,FR | 345;NAME1;CUSTOMER_BILLING_ADDRESS;FIRST_NAME;Cust first name;true |

    And the existing customer consents of
      | guid  | customerGuid  | dataPolicyGuid | consentDate              | action  |
      | 12422 | abc123        | 12345          | 1 month ago              | REVOKED |
      | 12423 | abc123        | 12346          | 1 month ago              | REVOKED |

    When revoked consents data point values job processor runs
    Then the data point value with customer guid [abc123] and data point guid [345] should have value [‐]

  Scenario: Revoked consent data point value job should remove
  data point values only for revoked consents and only if data point
  comply with all associated data policies retention types
  and retention periods even if customer hasn't granted nor revoked consent
  for other data policies the data point associated with.

    Given the existing data policies of
      | guid  | dataPolicyName | description   | referenceKey | retentionPeriod | policyState | retentionType      | segments | dataPoints                                                         |
      | 12345 | Policy 1       | Description 1 | DP12345      | 5               | ACTIVE      | FROM_CREATION_DATE | US,CA,FR | 345;NAME1;CUSTOMER_BILLING_ADDRESS;FIRST_NAME;Cust first name;true |
      | 12346 | Policy 2       | Description 1 | DP12345      | 5               | ACTIVE      | FROM_CREATION_DATE | US,CA,FR | 345;NAME1;CUSTOMER_BILLING_ADDRESS;FIRST_NAME;Cust first name;true |

    And the existing customer consents of
      | guid  | customerGuid  | dataPolicyGuid | consentDate              | action  |
      | 12422 | abc123        | 12345          | 1 month ago              | REVOKED |

    When revoked consents data point values job processor runs
    Then the data point value with customer guid [abc123] and data point guid [345] should have value [‐]

  Scenario: Expired data point values job should remove
  data point values only for revoked consents and only if data point
  comply with all associated data policies retention types
  and retention periods even if customer hasn't granted nor revoked consent
  for other data policies the data point associated with.

    Given the existing data policies of
      | guid  | dataPolicyName | description   | referenceKey | retentionPeriod | policyState | retentionType      | segments | dataPoints                                                         |
      | 12345 | Policy 1       | Description 1 | DP12345      | 5               | ACTIVE      | FROM_CREATION_DATE | US,CA,FR | 345;NAME1;CUSTOMER_BILLING_ADDRESS;FIRST_NAME;Cust first name;true |
      | 12346 | Policy 2       | Description 2 | DP12345      | 1000            | ACTIVE      | FROM_CREATION_DATE | US,CA,FR | 345;NAME1;CUSTOMER_BILLING_ADDRESS;FIRST_NAME;Cust first name;true |

    And the existing customer consents of
      | guid  | customerGuid  | dataPolicyGuid | consentDate              | action  |
      | 12422 | abc123        | 12345          | 1 month ago              | GRANTED |

    When expired data point values job processor runs
    Then the data point value with customer guid [abc123] and data point guid [345] should have value [‐]

  Scenario: Expired data point values job should remove
  customer profile values and does not throw null pointer exception if runs twice or more.

    Given the existing data policies of
      | guid  | dataPolicyName | description   | referenceKey | retentionPeriod | policyState | retentionType      | segments | dataPoints                                                                                                                                      |
      | 12345 | Policy 1       | Description 1 | DP12345      | 0               | ACTIVE      | FROM_CREATION_DATE | US,CA,FR | 500;NAME2;CUSTOMER_PROFILE;CP_FIRST_NAME;Customer profile first name;true,501;NAME3;CUSTOMER_PROFILE;CP_DUMMY;Customer profile dummy value;true |

    And the existing customer consents of
      | guid  | customerGuid  | dataPolicyGuid | consentDate              | action  |
      | 12422 | abc123        | 12345          | 1 month ago              | GRANTED |

    And the data point value with customer guid [abc123] and data point guid [500] should have value [Test]
    And the data point value with customer guid [abc123] and data point guid [501] should have value [null]
    When expired data point values job processor runs
    Then the customer profile data point value with customer guid [abc123] and data point key [CP_FIRST_NAME] has been deleted
    When expired data point values job processor runs
    Then the customer profile data point value with customer guid [abc123] and data point key [CP_FIRST_NAME] has been deleted
    And the customer profile data point value with customer guid [abc123] and data point key [CP_DUMMY] has been deleted

  Scenario: Revoked consent data point value job should remove
  customer profile values and does not throw null pointer exception if runs twice or more.

    Given the existing data policies of
      | guid  | dataPolicyName | description   | referenceKey | retentionPeriod | policyState | retentionType      | segments | dataPoints                                                                                                                                      |
      | 12345 | Policy 1       | Description 1 | DP12345      | 0               | ACTIVE      | FROM_CREATION_DATE | US,CA,FR | 500;NAME2;CUSTOMER_PROFILE;CP_FIRST_NAME;Customer profile first name;true,501;NAME3;CUSTOMER_PROFILE;CP_DUMMY;Customer profile dummy value;true |

    And the existing customer consents of
      | guid  | customerGuid  | dataPolicyGuid | consentDate              | action  |
      | 12422 | abc123        | 12345          | 1 month ago              | REVOKED |

    And the data point value with customer guid [abc123] and data point guid [500] should have value [Test]
    And the data point value with customer guid [abc123] and data point guid [501] should have value [null]
    When revoked consents data point values job processor runs
    Then the customer profile data point value with customer guid [abc123] and data point key [CP_FIRST_NAME] has been deleted
    When revoked consents data point values job processor runs
    Then the customer profile data point value with customer guid [abc123] and data point key [CP_FIRST_NAME] has been deleted
    And the customer profile data point value with customer guid [abc123] and data point key [CP_DUMMY] has been deleted

    Scenario: Multiple customer scenario - Expired data point values job should remove data points
    when retention period has passed

      Given the existing data policies of
        | guid  | dataPolicyName | description   | referenceKey | retentionPeriod | policyState | retentionType      | segments | dataPoints                                                          |
        | 12345 | Policy 1       | Description 1 | DP12345      | 5               | ACTIVE      | FROM_CREATION_DATE | US,CA,FR | 345;NAME1;CUSTOMER_BILLING_ADDRESS;FIRST_NAME;Cust first name;true  |

      And the existing customer consents of
        | guid  | customerGuid  | dataPolicyGuid | consentDate              | action  |
        | 12422 | abc123        | 12345          | 1 month ago              | GRANTED |
        | 12423 | abc345        | 12345          | 1 month ago              | GRANTED |
        | 12424 | abc678        | 12345          | 1 month ago              | GRANTED |

      When expired data point values job processor runs
      Then the data point value with customer guid [abc123] and data point guid [345] should have value [‐]
      Then the data point value with customer guid [abc345] and data point guid [345] should have value [‐]
      Then the data point value with customer guid [abc678] and data point guid [345] should have value [Steve]

  Scenario: Multiple customer scenario - Expired data point values job should remove data points based on customer consent action

    Given the existing data policies of
      | guid  | dataPolicyName | description   | referenceKey | retentionPeriod | policyState | retentionType      | segments | dataPoints                                                          |
      | 12345 | Policy 1       | Description 1 | DP12345      | 5               | ACTIVE      | FROM_CREATION_DATE | US,CA,FR | 345;NAME1;CUSTOMER_BILLING_ADDRESS;FIRST_NAME;Cust first name;true  |

    And the existing customer consents of
      | guid  | customerGuid  | dataPolicyGuid | consentDate              | action  |
      | 12422 | abc123        | 12345          | 1 month ago              | GRANTED |
      | 12423 | abc345        | 12345          | 1 month ago              | GRANTED |
      | 12424 | abc345        | 12345          | yesterday                | REVOKED |

    When expired data point values job processor runs
    Then the data point value with customer guid [abc123] and data point guid [345] should have value [‐]
    Then the data point value with customer guid [abc345] and data point guid [345] should have value [John]



