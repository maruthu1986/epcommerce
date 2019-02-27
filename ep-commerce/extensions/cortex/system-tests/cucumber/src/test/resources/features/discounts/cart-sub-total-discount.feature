@discounts
Feature: Cart sub total discount

  Background:
    Given I am logged in as a public shopper

  Scenario Outline: Retrieve Cart Total Discount and applied promotion element
    Given item <ITEM_SKU> triggers the cart total discount promotion <PROMOTION_DISPLAY_NAME>
    And item <ITEM_SKU> has a purchase price of $47.47
    When I add item with code <ITEM_SKU> to my cart
    Then the cart discount amount is $23.74
    And the applied promotion shows <PROMOTION_DISPLAY_NAME>

    Examples:
      | ITEM_SKU                                      | PROMOTION_DISPLAY_NAME |
      | triggerprodforfiftyoffentirepurchasepromo_sku | FiftyOffEntirePurchase |

  Scenario Outline: Shipping discount does not affect Cart Subtotal
    Given item <ITEM_SKU> triggers the shipping discount promotion <PROMOTION_DISPLAY_NAME>
    And item <ITEM_SKU> has a purchase price of $10.00
    When I add item with code <ITEM_SKU> to my cart
    Then the cart total is unaffected by the shipping discount and has value $10.00
    And the cart discount amount is $0.00

    Examples:
      | ITEM_SKU           | PROMOTION_DISPLAY_NAME |
      | 20off_shipping_sku | 20% Off Shipping       |
