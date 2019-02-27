@smoketest @customerService @customer @order
Feature: Customer Order History

  Scenario: View Order Details in Customer order history
    Given I have an order for scope mobee with following skus
      | skuCode                 | quantity |
      | handsfree_shippable_sku | 1        |
    And I sign in to CM as admin user
    And I go to Customer Service
    When I search and open order editor for the latest order
    And I open Customer Profile Orders tab
    Then I should see the latest order details