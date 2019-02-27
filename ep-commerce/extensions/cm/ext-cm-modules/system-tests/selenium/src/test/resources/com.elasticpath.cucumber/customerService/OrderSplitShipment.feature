@smoketest @customerService @order
Feature: Split Shipment

  Scenario Outline: Split Shipment
    Given I have an order for scope <scope> with following skus
      | skuCode      | quantity |
      | <sku-code-1> | 1        |
      | <sku-code-2> | 1        |
    And I sign in to CM as CSR user
    And I go to Customer Service
    And I search and open order editor for the latest order
    And I select Details tab in the Order Editor
    When I create a new shipment for sku <sku-code-2> with following values
      | Address         | 555 Elliott Avenue W, Seattle, WA, 98119 |
      | Shipment Method | FedEx Express                            |
      | Payment Source  | test token name                          |
    Then I should see 2 shipments

    Examples:
      | scope | sku-code-1              | sku-code-2 |
      | mobee | handsfree_shippable_sku | t384lkef   |