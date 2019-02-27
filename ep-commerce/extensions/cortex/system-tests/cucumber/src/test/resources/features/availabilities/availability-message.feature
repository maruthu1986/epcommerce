@Items @Availability
Feature: Cart Item availability
  As a shopper, I will see messages if items in my cart is unavailable

  Scenario Outline: User should see availability and purchase advisor messages when the cart has an unavailable item
    Given I have authenticated on scope mobee as a newly registered shopper
    And I add item with code <AVAILABLE_ITEM> to my cart with quantity 1
    Then I login as a registered shopper
    And I add item with code <AVAILABLE_ITEM> to my cart with quantity 10
    And the order is submitted
    And I re-authenticate on scope mobee with the original registered shopper
    When I retrieve my order
    Then there is an advisor message with the following fields:
      | messageType | messageId                   | debugMessage                                                       | dataField        |
      | error       | item.insufficient.inventory | Item '<AVAILABLE_ITEM>' only has 0 available but 1 were requested. | <AVAILABLE_ITEM> |
    When I retrieve the purchase form
    Then there is an advisor message with the following fields:
      | messageType | messageId                   | debugMessage                                                       | dataField        |
      | error       | item.insufficient.inventory | Item '<AVAILABLE_ITEM>' only has 0 available but 1 were requested. | <AVAILABLE_ITEM> |
    And there are no submitorderaction links

    Examples:
      | AVAILABLE_ITEM                            |
      | physical_product_with_fixed_inventory_sku |

  Scenario Outline: User should see availability and purchase advisor messages when the cart has a bundle with an unavailable item
    Given I have authenticated on scope mobee as a newly registered shopper
    And I add item with code <BUNDLE_CODE> to my cart with quantity 1
    And I login as another registered shopper
    And I add item with code <ITEM_CODE> to my cart with quantity 1
    And the order is submitted
    And I re-authenticate on scope mobee with the original registered shopper
    When I retrieve my order
    Then there is an advisor message with the following fields:
      | messageType | messageId                   | debugMessage                                                  | dataField   |
      | error       | item.insufficient.inventory | Item '<ITEM_CODE>' only has 0 available but 1 were requested. | <ITEM_CODE> |
    When I retrieve the purchase form
    Then there is an advisor message with the following fields:
      | messageType | messageId                   | debugMessage                                                  | dataField   | blocks            | linkedTo        |
      | error       | item.insufficient.inventory | Item '<ITEM_CODE>' only has 0 available but 1 were requested. | <ITEM_CODE> | submitorderaction | carts.line-item |
    And there are no submitorderaction links

    Examples:
      | BUNDLE_CODE                              | ITEM_CODE                               |
      | BundleWithOneSkuWithLimitedInventory_sku | physicalProductWithLimitedInventory_sku |