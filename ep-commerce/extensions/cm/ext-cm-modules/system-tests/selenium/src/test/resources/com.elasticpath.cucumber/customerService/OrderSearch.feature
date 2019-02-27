@smoketest @customerService @order
Feature: Order Search

  Background:
    Given I sign in to CM as admin user
    And I go to Customer Service

  Scenario: Order search by number
    Given I have an order for scope mobee with following skus
      | skuCode                 | quantity |
      | handsfree_shippable_sku | 1        |
    When I search the latest order by number
    Then I should see the latest order in results pane

  Scenario: Order search by email
    Given I have an order for scope mobee with following skus
      | skuCode                 | quantity |
      | handsfree_shippable_sku | 1        |
    When I search for the latest orders by email
    Then I should see customer name Test User in search results pane