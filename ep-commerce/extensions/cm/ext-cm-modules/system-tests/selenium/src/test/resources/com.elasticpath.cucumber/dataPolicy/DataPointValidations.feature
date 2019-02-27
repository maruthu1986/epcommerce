@smoketest @datapolicy
Feature: Data Points Validations

  Background:
    Given I sign in to CM as admin user
    And I go to Data Policies

  Scenario: Data Point name must be unique
    When I Create Data points with following existing values
      | data point name | Customer email |
      | location key    | ORDER_DATA     |
      | data key        | Test           |
      | description     | Test           |
    Then I can see Validation message for DuplicateNameTitle

  Scenario: Data Key and Location Key must be unique
    When I Create Data points with following existing values
      | data point name | Customeremail    |
      | location key    | CUSTOMER_PROFILE |
      | data key        | CP_EMAIL         |
      | description     | Customer email   |
    Then I can see Validation message for KeyAlreadyInUseTitle

  Scenario: Create Data Point button is not visible for Active Data Policy
    When I open an existing Active data policy Order Information
    Then I can not Create Data Point for Active Policy