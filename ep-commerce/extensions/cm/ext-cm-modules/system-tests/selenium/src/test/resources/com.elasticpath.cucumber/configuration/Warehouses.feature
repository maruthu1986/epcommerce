@smoketest @configuration @warehouse
Feature: Warehouse

  Background:
    Given I sign in to CM as admin user
    When I go to Configuration
    And I go to Warehouses

  Scenario: Add, edit and delete a warehouse
    When I create warehouse with following values
      | warehouse name | Test            |
      | address line 1 | 123 Main Street |
      | city           | Los Angeles     |
      | state          | California      |
      | zip            | 12345           |
      | country        | United States   |
    Then the new warehouse should exist in the list
    When I edit newly created warehouse name to testChanged
    Then the new warehouse should exist in the list
    When I delete newly created warehouse
    Then newly created warehouse no longer exists

  Scenario: Verify created warehouse present in store and warehouse list
    When I create warehouse with following values
      | warehouse name | Test            |
      | address line 1 | 123 Main Street |
      | city           | Los Angeles     |
      | state          | California      |
      | zip            | 12345           |
      | country        | United States   |
    Then the new warehouse should exist in the list
    And the new warehouse is in the warehouse list for store MOBEE
    And the new warehouse is in shipping receiving warehouse list
    When I delete newly created warehouse
    Then newly created warehouse no longer exists

