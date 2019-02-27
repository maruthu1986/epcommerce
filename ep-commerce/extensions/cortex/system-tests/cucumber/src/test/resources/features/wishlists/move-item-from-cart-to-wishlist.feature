@Wishlists
Feature: Move item from cart to wishlist
  As a shopper,
  I want to move items from my cart to my default wishlist,
  so that I can save them for purchase at a later time

  Background:
    Given I am logged in as a public shopper

  Scenario Outline: Move an item from default cart to wishlist
    Given item with code <ITEMCODE> already exists in my cart with quantity 1
    When I move an item with code <ITEMCODE> from my cart to my default wishlist
    Then the HTTP status is OK, created
    And item with code <ITEMCODE> is in my default wishlist
    And item with code <ITEMCODE> is not found in my cart

    Examples:
      | ITEMCODE                                   |
      #   multisku item
      | portable_tv_hdrent_sku                     |
      #   Singlesku item
      | FocUSsku                                   |
      #   Bundle item
      | bundleWithPhysicalAndDigitalComponents_sku |
      #   Digital item
      | alien_sku                                  |
       # Back-Order item
      | tt0926084_sku                              |
      # Pre-Order item
      | plantsVsZombies                            |

  Scenario Outline: Move an item from default cart to default wishlist
    Given item with name digitalProduct already exists in my cart with quantity <QUANTITY>
    When I move an item with name digitalProduct from my cart to my default wishlist
    Then the HTTP status is OK, created
    And item with name digitalProduct is in my default wishlist
    And item with name digitalProduct is not found in my cart

    Examples:
      | QUANTITY |
      | 1        |
      | 3        |

  Scenario Outline: Move configurable item from cart to wishlist and move back to cart with previous entered values
    When I look up an item with code <ITEMCODE>
    And I go to add to cart form
    And I add the item to the cart with quantity <QTY> and configurable fields:
      | giftCertificate.message        | <MESSAGE>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |
    And I move an item with code <ITEMCODE> from my cart to my default wishlist
    Then I should see wishlist line item configurable fields for itemcode <ITEMCODE> as:
      | giftCertificate.message        | <MESSAGE>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |
    And item with code <ITEMCODE> is not found in my cart

    Then I can move configurable itemcode <ITEMCODE> from wishlist to cart with quantity <QTY> and preserved data values:
      | giftCertificate.message        | <MESSAGE>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |

    And item with code <ITEMCODE> is not found in my default wishlist
    Then the cart lineitem with itemcode <ITEMCODE> has quantity <QTY> and configurable fields as:
      | giftCertificate.message        | <MESSAGE>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |

    Examples:
      | ITEMCODE   | MESSAGE     | RECIPIENT_EMAIL              | RECIPIENT_NAME | SENDER_NAME  | QTY |
      | berries_20 | Hello World | harry.potter@elasticpath.com | Harry Potter   | MOBEE tester | 1   |

  Scenario Outline: Same configurable items should be in same wishlist lineitem
 #   Item 1
    When I look up an item with code <ITEMCODE>
    And I go to add to cart form
    And I add the item to the cart with quantity <QTY> and configurable fields:
      | allFieldTypes.boolean           | <BOOLEAN>       |
      | allFieldTypes.date              | <DATE>          |
      | allFieldTypes.dateTime          | <DATETIME>      |
      | allFieldTypes.decimal           | <DECIMAL>       |
      | allFieldTypes.email             | <EMAIL>         |
      | allFieldTypes.integer           | <INTEGER>       |
      | allFieldTypes.multiSelectOption | <MULTI_OPTION>  |
      | allFieldTypes.shortText         | <SHORT_TEXT>    |
      | allFieldTypes.singleOption      | <SINGLE_OPTION> |
    And I move an item with code <ITEMCODE> from my cart to my default wishlist
    Then item with code <ITEMCODE> is in my default wishlist
    And item with code <ITEMCODE> is not found in my cart

 #   Item 2
    When I look up an item with code <ITEMCODE>
    And I go to add to cart form
    And I add the item to the cart with quantity <QTY> and configurable fields:
      | allFieldTypes.boolean           | <BOOLEAN>       |
      | allFieldTypes.date              | <DATE>          |
      | allFieldTypes.dateTime          | <DATETIME>      |
      | allFieldTypes.decimal           | <DECIMAL>       |
      | allFieldTypes.email             | <EMAIL>         |
      | allFieldTypes.integer           | <INTEGER>       |
      | allFieldTypes.multiSelectOption | <MULTI_OPTION>  |
      | allFieldTypes.shortText         | <SHORT_TEXT>    |
      | allFieldTypes.singleOption      | <SINGLE_OPTION> |
    And I move an item with code <ITEMCODE> from my cart to my default wishlist
    Then the HTTP status is OK
    And item with code <ITEMCODE> is in my default wishlist
    And item with code <ITEMCODE> is not found in my cart

    Then I should see wishlist line item configurable fields for itemcode <ITEMCODE> as:
      | allFieldTypes.boolean           | <BOOLEAN>       |
      | allFieldTypes.date              | <DATE>          |
      | allFieldTypes.dateTime          | <DATETIME>      |
      | allFieldTypes.decimal           | <DECIMAL>       |
      | allFieldTypes.email             | <EMAIL>         |
      | allFieldTypes.integer           | <INTEGER>       |
      | allFieldTypes.multiSelectOption | <MULTI_OPTION>  |
      | allFieldTypes.shortText         | <SHORT_TEXT>    |
      | allFieldTypes.singleOption      | <SINGLE_OPTION> |

    Then my default wishlist has 1 lineitems

    Examples:
      | ITEMCODE | QTY | BOOLEAN | DATE       | DATETIME                  | DECIMAL | EMAIL                        | INTEGER | MULTI_OPTION | SHORT_TEXT | SINGLE_OPTION |
      | sscpwaft | 1   | true    | 1996-10-22 | 1996-10-22T03:12:31+04:00 | 13.99   | harry.potter@elasticpath.com | 12      | multiValue1  | hello      | singleValue1  |

  Scenario Outline: Same configurable items with different field values should be in separate wishlist lineitem
#   Item 1
    When I look up an item with code <ITEMCODE>
    And I go to add to cart form
    And I add the item to the cart with quantity <QTY> and configurable fields:
      | allFieldTypes.boolean           | <BOOLEAN>       |
      | allFieldTypes.date              | <DATE>          |
      | allFieldTypes.dateTime          | <DATETIME>      |
      | allFieldTypes.decimal           | <DECIMAL>       |
      | allFieldTypes.email             | <EMAIL>         |
      | allFieldTypes.integer           | <INTEGER>       |
      | allFieldTypes.multiSelectOption | <MULTI_OPTION>  |
      | allFieldTypes.shortText         | <SHORT_TEXT>    |
      | allFieldTypes.singleOption      | <SINGLE_OPTION> |
    And I move an item with code <ITEMCODE> from my cart to my default wishlist
    Then I should see wishlist line item configurable fields for itemcode <ITEMCODE> as:
      | allFieldTypes.boolean           | <BOOLEAN>       |
      | allFieldTypes.date              | <DATE>          |
      | allFieldTypes.dateTime          | <DATETIME>      |
      | allFieldTypes.decimal           | <DECIMAL>       |
      | allFieldTypes.email             | <EMAIL>         |
      | allFieldTypes.integer           | <INTEGER>       |
      | allFieldTypes.multiSelectOption | <MULTI_OPTION>  |
      | allFieldTypes.shortText         | <SHORT_TEXT>    |
      | allFieldTypes.singleOption      | <SINGLE_OPTION> |
    And item with code <ITEMCODE> is not found in my cart

#   Item 2
    When I look up an item with code <ITEMCODE>
    And I go to add to cart form
    And I add the item to the cart with quantity <QTY> and configurable fields:
      | allFieldTypes.boolean           | <BOOLEAN_2>     |
      | allFieldTypes.date              | <DATE>          |
      | allFieldTypes.dateTime          | <DATETIME>      |
      | allFieldTypes.decimal           | <DECIMAL>       |
      | allFieldTypes.email             | <EMAIL>         |
      | allFieldTypes.integer           | <INTEGER>       |
      | allFieldTypes.multiSelectOption | <MULTI_OPTION>  |
      | allFieldTypes.shortText         | <SHORT_TEXT>    |
      | allFieldTypes.singleOption      | <SINGLE_OPTION> |
    And I move an item with code <ITEMCODE> from my cart to my default wishlist
    Then the HTTP status is OK, created
    And I follow location
    Then I should see in the response the line item just added with configurable fields as:
      | allFieldTypes.boolean           | <BOOLEAN_2>     |
      | allFieldTypes.date              | <DATE>          |
      | allFieldTypes.dateTime          | <DATETIME>      |
      | allFieldTypes.decimal           | <DECIMAL>       |
      | allFieldTypes.email             | <EMAIL>         |
      | allFieldTypes.integer           | <INTEGER>       |
      | allFieldTypes.multiSelectOption | <MULTI_OPTION>  |
      | allFieldTypes.shortText         | <SHORT_TEXT>    |
      | allFieldTypes.singleOption      | <SINGLE_OPTION> |
    And I should see wishlist line item <ITEMCODE> with configurable field values as:
      | allFieldTypes.boolean           | <BOOLEAN_2>     |
      | allFieldTypes.date              | <DATE>          |
      | allFieldTypes.dateTime          | <DATETIME>      |
      | allFieldTypes.decimal           | <DECIMAL>       |
      | allFieldTypes.email             | <EMAIL>         |
      | allFieldTypes.integer           | <INTEGER>       |
      | allFieldTypes.multiSelectOption | <MULTI_OPTION>  |
      | allFieldTypes.shortText         | <SHORT_TEXT>    |
      | allFieldTypes.singleOption      | <SINGLE_OPTION> |
    And item with code <ITEMCODE> is not found in my cart
    Then my default wishlist has 2 lineitems

    Examples:
      | ITEMCODE | QTY | BOOLEAN | DATE       | DATETIME                  | DECIMAL | EMAIL                        | INTEGER | MULTI_OPTION | SHORT_TEXT | SINGLE_OPTION | BOOLEAN_2 |
      | sscpwaft | 1   | true    | 1996-10-22 | 1996-10-22T03:12:31+04:00 | 13.99   | harry.potter@elasticpath.com | 12      | multiValue1  | hello      | singleValue1  | false     |