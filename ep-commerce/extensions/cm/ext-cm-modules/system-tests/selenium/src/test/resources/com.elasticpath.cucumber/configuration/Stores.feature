@smoketest @configuration @store
Feature: Store

  Background:
    Given I sign in to CM as admin user
    When I go to Configuration
    And I go to Stores

  Scenario: Add, edit and delete a store
    When I create store with following values
      | timezone          | GMT -8:00 Pacific Standard Time |
      | store country     | United States                   |
      | store sub country | California                      |
      | payment gateway   | DemoTokenPaymentProcessor       |
      | store name        | TestStore-                      |
      | warehouse         | Generic Warehouse A             |
      | catalog           | Master Catalog A                |
      | language          | English (United States)         |
      | currency          | USD                             |
    Then the store should exist in the list
    When I edit the store name to testStoreNameChanged
    Then the store should exist in the list
    When I add new language Arabic to the store
    Then I should see the new language in the store list
    When I change the store state to Open
    Then the store list should show Open state
    When I change the store state to Restricted Access
    Then the store list should show Restricted Access state
    When I change the store state to Open
    Then the store list should show Open state
    When I delete the store
    Then store should not exist
