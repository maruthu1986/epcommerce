@smoketest @datapolicy
Feature: Data Policy Validations

  Background:
    Given I sign in to CM as admin user

  Scenario: Data Policy required field validation
    When I go to Data Policies
    And I Create Data Policy without entering required fields
    Then I can see validation error messages for the following fields
      | NameField         |
      | ReferenceKeyField |
      | RetentionPeriod   |
      | StartDate         |

  Scenario: Data Policy Segment validation
    When I go to Data Policies
    Then I am unable to Create Data Policy without data policy segment

  Scenario: Retention Period must be an integer
    When I go to Data Policies
    And I Create Data Policy with invalid Retention Period AA
    Then I can see Retention Period validation displayed as Must be an integer

  Scenario: Data Policy End Date must be after Start Date
    When I go to Data Policies
    And I Create Data Policy with End Date before Start Date
    Then I can see Date validation displayed as End date must be after start date.