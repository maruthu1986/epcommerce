@smoketest @promotionsShipping @promotion
Feature: Catalog Promotion

  Background:
    Given I sign in to CM as admin user

  Scenario: Create edit and disable catalog promotion
    When I go to Promotions and Shipping
    And I create catalog promotion with following values
      | catalog                | Search Catalog                   |
      | name                   | 10% off Catalog Promotion        |
      | display name           | 10% off Mobile Catalog Promotion |
      | condition menu item    | Brand is []                      |
      | discount menu item     | Catalog Discount                 |
      | discount sub menu item | Get [] % off when currency is [] |
      | discount value         | 10                               |
    Then newly created catalog promotion exists
    When I edit and disable newly created catalog promotion
    Then catalog promotion state should be Expired


