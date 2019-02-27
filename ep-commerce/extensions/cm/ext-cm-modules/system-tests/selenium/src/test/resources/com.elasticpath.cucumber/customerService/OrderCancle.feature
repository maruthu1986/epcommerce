@smoketest @customerService @order
Feature: Cancel Order/Shipment

  Background:
    Given I sign in to CM as CSR user
    And I go to Customer Service

  Scenario: Cancel order
    And I have an order for scope mobee with following skus
      | skuCode                 | quantity |
      | handsfree_shippable_sku | 1        |
    When I search and open order editor for the latest order
    And I select Payments tab in the Order Editor
    Then I should see transaction type Authorization in the Payment History
    When I select Summary tab in the Order Editor
    And I cancel the order
    Then the order status should be Cancelled
    When I select Payments tab in the Order Editor
    Then I should see transaction type Authorization Reversal in the Payment History

  Scenario: Cancel shipment
    And I have an order for scope mobee with following skus
      | skuCode                 | quantity |
      | handsfree_shippable_sku | 1        |
    When I search and open order editor for the latest order
    And I select Payments tab in the Order Editor
    Then I should see transaction type Authorization in the Payment History
    When I select Details tab in the Order Editor
    And I cancel the shipment
    Then the shipment status should be Cancelled
    When I select Payments tab in the Order Editor
    Then I should see transaction type Authorization Reversal in the Payment History
    When I select Summary tab in the Order Editor
    Then the order status should be Cancelled
