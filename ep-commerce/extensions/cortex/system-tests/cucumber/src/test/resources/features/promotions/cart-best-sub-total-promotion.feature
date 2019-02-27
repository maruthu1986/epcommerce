@Promotions
Feature: Applying 2 cart sub total discount to cart and ensuring best discount gets applied to the cart
  As a client developer
  I apply cart discount which is 10% off on cart subtotal by adding an ITEM then applying another discount - blackfriday 30% off.
  Ensure best discount gets applied to cart which is blackfriday - 30%

  Scenario Outline:  Ensure Best cart sub total discount has been applied to cart
    Given I am logged in as a public shopper
    When I add item with code <ITEM_SKU> to my cart
    And the cart discount fields has amount: 5.55, currency: CAD and display: $5.55
    When I apply a coupon code blackfriday to my order
    And the cart discount fields has amount: 16.65, currency: CAD and display: $16.65
    And I go to my cart
    Then the list of applied promotions contains promotion <PROMOTION>

    Examples:
      | ITEM_SKU    | PROMOTION                |
      | sony_bt_sku | 30PercentOffCartSubtotal |


  # TODO: Once we fixed PB-2039 then last Then step of this feature file will be replaced with below step where we check only 1 promotion is applied.
  # Then I can see applied promotion shows <PROMOTION_DISPLAY_NAME>