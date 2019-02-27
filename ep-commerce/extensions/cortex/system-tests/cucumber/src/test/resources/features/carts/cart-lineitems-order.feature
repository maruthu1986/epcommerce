@Carts
Feature: Cart Lineitems Ordering
  The order in which items are added to the cart is preserved

  Background:
    Given I login as a public shopper

  Scenario: Cart items are ordered from oldest to newest
    When I add item with code firstProductAddedToCart_sku to my cart
    And I add item with code secondProductAddedToCart_sku to my cart
    And I add item with code thirdProductAddedToCart_sku to my cart
    Then the items in the cart are ordered as follows
      | firstProductAddedToCart  |
      | secondProductAddedToCart |
      | thirdProductAddedToCart  |

  Scenario: Cart items are ordered from oldest to newest in zoomed result
    When I add item with code firstProductAddedToCart_sku to my cart
    And I add item with code secondProductAddedToCart_sku to my cart
    And I add item with code thirdProductAddedToCart_sku to my cart
    Then the items in the zoomed cart are ordered as follows
      | firstProductAddedToCart  |
      | secondProductAddedToCart |
      | thirdProductAddedToCart  |
