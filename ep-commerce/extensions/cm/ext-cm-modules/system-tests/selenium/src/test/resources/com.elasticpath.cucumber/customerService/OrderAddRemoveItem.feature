@smoketest @customerService @order
Feature: Add Remove shipment item

  Scenario Outline: Add an item
    Given I have an order for scope <scope> with following skus
      | skuCode      | quantity |
      | <sku-code-1> | 1        |
    And I sign in to CM as CSR user
    And I go to Customer Service
    And I search and open order editor for the latest order
    And I select Details tab in the Order Editor
    When I add sku <sku-code-2> to the shipment with following values
      | Price List Name | Mobile Price List |
      | Payment Source  | test token name   |
    Then I should see the following sku in item list
      | <sku-code-1> |
      | <sku-code-2> |

    Examples:
      | scope | sku-code-1              | sku-code-2 |
      | mobee | handsfree_shippable_sku | t384lkef   |

  Scenario Outline: Remove an item
    Given I have an order for scope <scope> with following skus
      | skuCode      | quantity |
      | <sku-code-1> | 1        |
      | <sku-code-2> | 1        |
    And I sign in to CM as admin user
    And I go to Customer Service
    And I search and open order editor for the latest order
    And I select Details tab in the Order Editor
    And I remove sku <sku-code-2> from the shipment
    Then I should see the following sku in item list
      | <sku-code-1> |
    Then I should not see the following sku in item list
      | <sku-code-2> |

    Examples:
      | scope | sku-code-1              | sku-code-2 |
      | mobee | handsfree_shippable_sku | t384lkef   |