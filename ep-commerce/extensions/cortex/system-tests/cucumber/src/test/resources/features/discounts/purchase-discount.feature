@discountsInProgress @HeaderAuth
Feature: Purchase discounts

  Background:
    Given I login as a registered shopper

  Scenario Outline: Ensure purchase discount is displayed correctly for cart total discount promotion
    Given item <ITEM_NAME> triggers the cart total discount promotion fifty percent off
    And item <ITEM_NAME> has a purchase price of $47.47
    When I have item <ITEM_NAME> in the cart
    And I retrieve the purchase
    Then the purchase discount amount is $23.74
    And the purchase total after discount is $26.58

    Examples:
      | ITEM_NAME                                 |
      | triggerprodforfiftyoffentirepurchasepromo |


  Scenario Outline: Ensure Line Item Discount are correct in the purchase
    Given item <ITEM_NAME> triggers the line item discount promotion 10 percent
    And item <ITEM_NAME> has a purchase price of $10.00
    When I have item <ITEM_NAME> in the cart
    And I retrieve the purchase
    Then the line item <ITEM_NAME> that had a ten percent discount has the amount $9.00
    And the purchase discount amount is $0.00
    And the purchase total after discount is $10.08

    Examples:
      | ITEM_NAME                        |
      | Product With Cart Lineitem Promo |


  Scenario Outline:  Ensure discounts are correct in purchase with both cart total and line item discounts
    Given item <ITEM_NAME> triggers the cart total discount promotion fifty percent off
    And item <ITEM_NAME> has a purchase price of $47.47
    And item <ITEM_NAME2> triggers the line item discount promotion 10 percent
    And item <ITEM_NAME2> has a purchase price of $10.00
    When I add item <ITEM_NAME> to the cart
    And I add item <ITEM_NAME2> to the cart
    And I retrieve the purchase
    Then the line item <ITEM_NAME> that had the total discount has the amount $47.47
    And the line item <ITEM_NAME2> that had a ten percent discount has the amount $9.00
    And the purchase discount amount is $28.24
    And the purchase total after discount is $31.63

    Examples:
      | ITEM_NAME                                 | ITEM_NAME2                       |
      | triggerprodforfiftyoffentirepurchasepromo | Product With Cart Lineitem Promo |