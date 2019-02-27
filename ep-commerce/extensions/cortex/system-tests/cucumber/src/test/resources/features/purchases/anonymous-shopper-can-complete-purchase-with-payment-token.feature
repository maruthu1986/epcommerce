@Purchases
Feature: Anonymous shopper can complete purchase with payment token
  As a client developer
  I want to allow the shopper to purchase with a newly created payment token
  so they can create a payment token and use it on their purchase

  Scenario Outline: Anonymous shopper can complete purchase

    Given I am logged into scope mobee as a public shopper
    And I fill in email needinfo
    And I fill in payment methods needinfo
    And I fill in billing address needinfo
    When Adding an item with item code <ITEMCODE> and quantity <QUANTITY> to the cart
    And the HTTP status is OK, created
    When the order is submitted
    Then the HTTP status is OK
    And the payment token created is used for the order

    Examples:
      | ITEMCODE        | QUANTITY |
      | plantsVsZombies | 1        |

  Scenario Outline: Anonymous shopper can't complete purchase as item is not in sufficient stock available
    Given I am logged into scope mobee as a public shopper
    And I fill in email needinfo
    And I fill in payment methods needinfo
    And I fill in billing address needinfo
    When Adding an item with item code <ITEMCODE> and quantity <QUANTITY> to the cart
    Then I should see validation error message with message type, message id, and debug message
      | messageType | messageId                   | debugMessage                                                           |
      | error       | item.insufficient.inventory | Item '<ITEMCODE>' only has 10 available but <QUANTITY> were requested. |

    Examples:
      | ITEMCODE    | QUANTITY |
      | sony_bt_sku | 5000000  |