@smoketest @datapolicy
Feature: Manage Data Policy

  Background:
    Given I sign in to CM as admin user

  Scenario: Create and Edit Data Policy
    Given a new Data Policy with following values
      | policy-name          | Test                |
      | policy-reference-key | Test                |
      | retention days       | 1                   |
      | state                | Draft               |
      | data points          | Customer first name |
      | segment              | uk_dp               |
    When I edit newly created Data Policy
    And I update Data Policy State to Active
    Then Data Policy State is Active

  Scenario: Disable Data Policy
    Given a new Data Policy with following values
      | policy-name          | Test                |
      | policy-reference-key | Test                |
      | retention days       | 1                   |
      | state                | Draft               |
      | data points          | Customer first name |
      | segment              | uk_dp               |
    When I Disable newly created Data Policy
    Then Data Policy State is Disabled
    And Data Policy End Date is set to current time
    And the following data policy fields are disabled
      | NameField         |
      | ReferenceKeyField |
      | RetentionType     |
      | RetentionPeriod   |
      | State             |
      | StartDate         |
      | EndDate           |
      | Activities        |

  Scenario: Active Data Policy
    Given a new Data Policy with following values
      | policy-name          | Test                |
      | policy-reference-key | Test                |
      | retention days       | 1                   |
      | state                | Active              |
      | data points          | Customer first name |
      | segment              | uk_dp               |
    Then the following data policy fields are disabled
      | NameField         |
      | ReferenceKeyField |
      | RetentionType     |
      | RetentionPeriod   |
      | State             |
      | StartDate         |
      | Activities        |
    When I update Data Policy End Date to future date
    Then Data Policy End Date is in the future date