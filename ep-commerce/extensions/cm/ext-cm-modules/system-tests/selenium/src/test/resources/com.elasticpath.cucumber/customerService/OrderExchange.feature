@smoketest @customerService @order
Feature: Order Exchange

  Scenario Outline: Create Exchange
    Given I have an order for scope <scope> with following skus
      | skuCode             | quantity |
      | <original-sku-code> | 1        |
    And I sign in to CM as admin user
    And I go to Customer Service
    And I search and open order editor for the latest order
    And I complete the order shipment
    And I select Details tab in the Order Editor
    When I create a exchange with following values
      | Return Qty        | 1                         |
      | Return Sku Code   | <original-sku-code>       |
      | Exchange Sku Code | <exchange-sku-code>       |
      | Price List Name   | Mobile Price List         |
      | Shipping Address  | Test User, 98119, Seattle |
      | Shipping Method   | FedEx Express             |
      | Payment Source    | test token name           |
    And I select Returns and Exchanges tab in the Order Editor
    Then I should see the returned sku code <original-sku-code>
    And I should see exchange order number
    When I open the exchange order editor
    Then I should see the original order# as External Order# and exchange order# as Order#
    When I select Details tab in the Order Editor
    Then I should see the following sku in item list
      | <exchange-sku-code> |

    Examples:
      | scope | original-sku-code       | exchange-sku-code |
      | mobee | handsfree_shippable_sku | t384lkef          |