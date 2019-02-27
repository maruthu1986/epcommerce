@Carts @HeaderAuth
Feature: Clear Cart Items
  As the developer
  I want to ensure the shopper cart is empty at the start of any new transactional session
  so that no unintended items are in the shoppers cart at checkout

  Background:
    Given I login as a registered shopper
    And I clear the cart

  Scenario: Clear cart with single line item
    Given I have item with code firstProductAddedToCart_sku in my cart with quantity 3
    When I clear the cart
    Then the total quantity in the cart is 0
    And there are no lineitems in the cart

  Scenario: Clear cart with multiple line items
    Given I have item with code firstProductAddedToCart_sku in my cart with quantity 1
    And I have item with code secondProductAddedToCart_sku in my cart with quantity 1
    And I have item with code thirdProductAddedToCart_sku in my cart with quantity 1
    And the total quantity in the cart is 3
    When I clear the cart
    Then the total quantity in the cart is 0
    And there are no lineitems in the cart

  Scenario Outline: Clear cart with cart promotion item
    Given I add item with code <ITEM_CODE> to my cart
    And I go to my default cart
    And the list of applied promotions contains promotion <PROMOTION>
    And the cart total has amount: 47.47, currency: CAD and display: $47.47
    And the total quantity in the cart is 1
    When I clear the cart
    Then the total quantity in the cart is 0
    And there are no lineitems in the cart
    And the list of applied promotions is empty
    And the cart total has amount: 0.0, currency: CAD and display: $0.00

    Examples:
      | ITEM_CODE                                     | PROMOTION              |
      | triggerprodforfiftyoffentirepurchasepromo_sku | FiftyOffEntirePurchase |