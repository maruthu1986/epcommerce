@smoketest @configuration @shippingregions
Feature: Shipping Region

  Background:
    Given I sign in to CM as admin user
    When I go to Configuration
    And I go to Shipping Regions

  Scenario: Add, edit and delete a Shipping Region
    When I create a Shipping Region for country Belgium named testing
    Then the new shipping region name should exist in the list
    When I edit newly created shipping region to the country Australia named testChanged
    Then the new shipping region name should exist in the list
    When I delete newly created shipping region
    Then the newly created shipping region no longer exists