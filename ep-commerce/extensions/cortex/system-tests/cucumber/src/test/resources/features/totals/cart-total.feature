@totals @HeaderAuth

Feature: Retrieve Total Of Cart
  As a client developer
  I want to retrieve the total of the cart
  so that I could display the total of all lineitems excluding discounts to the client

  Background:
    Given I login as a registered shopper

  Scenario Outline: Can Retrieve Total Of Cart
    Given I add item <ITEM_NAME> to the cart
    And the line item purchase price fields has amount: <AMOUNT>, currency: <CURRENCY> and display: <DISPLAY_AMOUNT>
    When I retrieve the cart total
    Then the cost fields has amount: <AMOUNT>, currency: <CURRENCY> and display: <DISPLAY_AMOUNT>

    Examples:
      | ITEM_NAME     | AMOUNT | CURRENCY | DISPLAY_AMOUNT |
      | Sleepy Hallow | 12.99  | CAD      | $12.99         |


  Scenario Outline: Verify Total Of Cart with Multiple Lineitems
    Given I add item <ITEM_NAME_1> to the cart
    And I add item <ITEM_NAME_2> to the cart
    When I retrieve the cart total
    Then the cost is the sum of each lineitem
    And the cost fields has amount: <AMOUNT>, currency: <CURRENCY> and display: <DISPLAY_AMOUNT>

    Examples:
      | ITEM_NAME_1   | ITEM_NAME_2 | AMOUNT | CURRENCY | DISPLAY_AMOUNT |
      | Sleepy Hallow | Tangled     | 25.98  | CAD      | $25.98         |


  Scenario Outline: Can retrieve total with cart promotion
    Given I add item <ITEM_NAME> to the cart
    And the line item purchase price fields has amount: <AMOUNT>, currency: <CURRENCY> and display: <DISPLAY_AMOUNT>
    When I retrieve the cart total
    Then the cart total does not include discounts
    And the cost fields has amount: <AMOUNT>, currency: <CURRENCY> and display: <DISPLAY_AMOUNT>

    Examples:
      | ITEM_NAME                                 | AMOUNT | CURRENCY | DISPLAY_AMOUNT |
      | triggerprodforfiftyoffentirepurchasepromo | 47.47  | CAD      | $47.47         |


  Scenario Outline: Verify cart total with line item discount
    Given I add item <ITEM_NAME> to the cart
    And the line item purchase price fields has amount: <AMOUNT>, currency: <CURRENCY> and display: <DISPLAY_AMOUNT>
    When I retrieve the cart total
    Then the cost fields has amount: <AMOUNT>, currency: <CURRENCY> and display: <DISPLAY_AMOUNT>

    Examples:
      | ITEM_NAME                                      | AMOUNT | CURRENCY | DISPLAY_AMOUNT |
      | Product With 50 Percent Off Cart Item Discount | 5.0    | CAD      | $5.00          |


  Scenario Outline: Verify cart total with multiple line items that have cart item discount
    Given I add item <ITEM_NAME_1> to the cart
    And I add item <ITEM_NAME_2> to the cart
    When I retrieve the cart total
    Then the cost is the sum of each lineitem
    And the cost fields has amount: <AMOUNT>, currency: <CURRENCY> and display: <DISPLAY_AMOUNT>

    Examples:
      | ITEM_NAME_1                                    | ITEM_NAME_2                                    | AMOUNT | CURRENCY | DISPLAY_AMOUNT |
      | Product With 20 Percent Off Cart Item Discount | Product With 50 Percent Off Cart Item Discount | 13.0   | CAD      | $13.00         |


  Scenario Outline: Can retrieve total of cart with no lineitems
    Given there is no lineitem in cart
    When I retrieve the cart total
    Then the cost fields has amount: <AMOUNT>, currency: <CURRENCY> and display: <DISPLAY_AMOUNT>

    Examples:
      | AMOUNT | CURRENCY | DISPLAY_AMOUNT |
      | 0.0    | CAD      | $0.00          |
