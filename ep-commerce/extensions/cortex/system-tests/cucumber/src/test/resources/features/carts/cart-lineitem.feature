@Carts
Feature: Cart Lineitems
  Business logic tests around cart line items. Same sku should be in same cart lineitem except gift certificate.

  Background:
    Given I login as a newly registered shopper

  Scenario Outline: Identically configured gift certificates should be in the same cart lineitem
#   Item 1
    When I look up an item with code <ITEMCODE>
    And I go to add to cart form
    And I successfully add the item to the cart with quantity <QTY> and configurable fields:
      | giftCertificate.message        | <MESSAGE>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |
    Then the cart lineitem with itemcode <ITEMCODE> has quantity <QTY> and configurable fields as:
      | giftCertificate.message        | <MESSAGE>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |
#   Item 2
    When I look up an item with code <ITEMCODE>
    And I go to add to cart form
    And I successfully add the item to the cart with quantity <QTY> and configurable fields:
      | giftCertificate.message        | <MESSAGE>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |
    Then the cart lineitem with itemcode <ITEMCODE> has quantity 2 and configurable fields as:
      | giftCertificate.message        | <MESSAGE>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |
    And the number of cart lineitems is 1
    And the cart total-quantity is 2

    Examples:
      | ITEMCODE   | MESSAGE     | RECIPIENT_EMAIL              | RECIPIENT_NAME | SENDER_NAME  | QTY |
      | berries_20 | Hello World | harry.potter@elasticpath.com | Harry Potter   | MOBEE tester | 1   |

  Scenario Outline: Different gift certificate items should split to separate lineitems
#   Item 1
    When I look up an item with code <ITEMCODE>
    And I go to add to cart form
    And I successfully add the item to the cart with quantity 1 and configurable fields:
      | giftCertificate.message        | <MESSAGE>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |
    Then the cart lineitem with itemcode <ITEMCODE> has quantity 1 and configurable fields as:
      | giftCertificate.message        | <MESSAGE>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |
#   Item 2
    When I look up an item with code <ITEMCODE_2>
    And I go to add to cart form
    And I successfully add the item to the cart with quantity 1 and configurable fields:
      | giftCertificate.message        | <MESSAGE>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |
    Then I should see in the response the line item just added with configurable fields as:
      | giftCertificate.message        | <MESSAGE>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |
    And the cart lineitem with itemcode <ITEMCODE_2> has quantity 1 and configurable fields as:
      | giftCertificate.message        | <MESSAGE>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |
    And the number of cart lineitems is 2
    And the cart total-quantity is 2

    Examples:
      | ITEMCODE   | MESSAGE     | RECIPIENT_EMAIL              | RECIPIENT_NAME | SENDER_NAME  | ITEMCODE_2     |
      | berries_20 | Hello World | harry.potter@elasticpath.com | Harry Potter   | MOBEE tester | hummingbird_20 |

  Scenario Outline: Same configurable items should be in same cart lineitem
#   Item 1
    When I look up an item with code <ITEMCODE>
    And I go to add to cart form
    And I successfully add the item to the cart with quantity <QTY> and configurable fields:
      | allFieldTypes.boolean           | <BOOLEAN>       |
      | allFieldTypes.date              | <DATE>          |
      | allFieldTypes.dateTime          | <DATETIME>      |
      | allFieldTypes.decimal           | <DECIMAL>       |
      | allFieldTypes.email             | <EMAIL>         |
      | allFieldTypes.integer           | <INTEGER>       |
      | allFieldTypes.multiSelectOption | <MULTI_OPTION>  |
      | allFieldTypes.shortText         | <SHORT_TEXT>    |
      | allFieldTypes.singleOption      | <SINGLE_OPTION> |
#   Item 2
    When I look up an item with code <ITEMCODE>
    And I go to add to cart form
    And I successfully add the item to the cart with quantity <QTY> and configurable fields:
      | allFieldTypes.boolean           | <BOOLEAN>       |
      | allFieldTypes.date              | <DATE>          |
      | allFieldTypes.dateTime          | <DATETIME>      |
      | allFieldTypes.decimal           | <DECIMAL>       |
      | allFieldTypes.email             | <EMAIL>         |
      | allFieldTypes.integer           | <INTEGER>       |
      | allFieldTypes.multiSelectOption | <MULTI_OPTION>  |
      | allFieldTypes.shortText         | <SHORT_TEXT>    |
      | allFieldTypes.singleOption      | <SINGLE_OPTION> |

    Then the cart lineitem with itemcode <ITEMCODE> has quantity 2 and configurable fields as:
      | allFieldTypes.boolean           | <BOOLEAN>       |
      | allFieldTypes.date              | <DATE>          |
      | allFieldTypes.dateTime          | <DATETIME>      |
      | allFieldTypes.decimal           | <DECIMAL>       |
      | allFieldTypes.email             | <EMAIL>         |
      | allFieldTypes.integer           | <INTEGER>       |
      | allFieldTypes.multiSelectOption | <MULTI_OPTION>  |
      | allFieldTypes.shortText         | <SHORT_TEXT>    |
      | allFieldTypes.singleOption      | <SINGLE_OPTION> |

    And the number of cart lineitems is 1
    And the cart total-quantity is 2

    Examples:
      | ITEMCODE | QTY | BOOLEAN | DATE       | DATETIME                  | DECIMAL | EMAIL                        | INTEGER | MULTI_OPTION | SHORT_TEXT | SINGLE_OPTION |
      | sscpwaft | 1   | true    | 1996-10-22 | 1996-10-22T03:12:31+04:00 | 13.99   | harry.potter@elasticpath.com | 12      | multiValue1  | hello      | singleValue1  |

  Scenario Outline: Same configurable items with different field values should be in separate cart lineitem
#   Item 1
    When I look up an item with code <ITEMCODE>
    And I go to add to cart form
    And I successfully add the item to the cart with quantity <QTY> and configurable fields:
      | allFieldTypes.boolean           | <BOOLEAN>       |
      | allFieldTypes.date              | <DATE>          |
      | allFieldTypes.dateTime          | <DATETIME>      |
      | allFieldTypes.decimal           | <DECIMAL>       |
      | allFieldTypes.email             | <EMAIL>         |
      | allFieldTypes.integer           | <INTEGER>       |
      | allFieldTypes.multiSelectOption | <MULTI_OPTION>  |
      | allFieldTypes.shortText         | <SHORT_TEXT>    |
      | allFieldTypes.singleOption      | <SINGLE_OPTION> |
#   Item 2
    When I look up an item with code <ITEMCODE>
    And I go to add to cart form
    And I successfully add the item to the cart with quantity <QTY> and configurable fields:
      | allFieldTypes.boolean           | <BOOLEAN_2>     |
      | allFieldTypes.date              | <DATE>          |
      | allFieldTypes.dateTime          | <DATETIME>      |
      | allFieldTypes.decimal           | <DECIMAL>       |
      | allFieldTypes.email             | <EMAIL>         |
      | allFieldTypes.integer           | <INTEGER>       |
      | allFieldTypes.multiSelectOption | <MULTI_OPTION>  |
      | allFieldTypes.shortText         | <SHORT_TEXT>    |
      | allFieldTypes.singleOption      | <SINGLE_OPTION> |
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
    Then the number of cart lineitems is 2
    And the cart total-quantity is 2
    And the cart lineitem for item code <ITEMCODE> has quantity of 1

    Examples:
      | ITEMCODE | QTY | BOOLEAN | DATE       | DATETIME                  | DECIMAL | EMAIL                        | INTEGER | MULTI_OPTION | SHORT_TEXT | SINGLE_OPTION | BOOLEAN_2 |
      | sscpwaft | 1   | true    | 1996-10-22 | 1996-10-22T03:12:31+04:00 | 13.99   | harry.potter@elasticpath.com | 12      | multiValue1  | hello      | singleValue1  | false     |

  Scenario Outline: Same sku items should be in same cart lineitem
#   Item 1
    When I look up an item with code <ITEMCODE>
    And I go to add to cart form
    And I add to cart with quantity of 1
#   Item 2
    When I look up an item with code <ITEMCODE>
    And I go to add to cart form
    And I add to cart with quantity of 1

    Then the number of cart lineitems is 1
    And the cart total-quantity is 2
    And the cart lineitem for item code <ITEMCODE> has quantity of 2
#   Test data contains:
#   multisku item
#   Singlesku item
#   Bundle item
#   Digital item
    Examples:
      | ITEMCODE                                   |
      | portable_tv_hdrent_sku                     |
      | FocUSsku                                   |
      | bundleWithPhysicalAndDigitalComponents_sku |
      | alien_sku                                  |

  Scenario Outline: Multisku items with different sku values should be in separate cart lineitems
#   Item 1
    When I look up an item with code <ITEMCODE>
    And I go to add to cart form
    And I add to cart with quantity of 1
#   Item 2
    When I look up an item with code <ITEMCODE>
    And I change the multi sku selection by <OPTION> and select choice <VALUE>
    And the item code is <ITEM_SKU_CODE>
    And I add selected multisku item to the cart

    Then the number of cart lineitems is 2
    And the cart total-quantity is 2
    And the cart lineitem for item code <ITEMCODE> has quantity of 1
    And the cart lineitem for item code <ITEM_SKU_CODE> has quantity of 1

    Examples:
      | ITEMCODE               | OPTION        | ITEM_SKU_CODE         | VALUE |
      | portable_tv_hdrent_sku | Purchase Type | portable_tv_hdbuy_sku | Buy   |

  Scenario Outline: Product with no price cannot be added to cart
    When I look up an item with code <SKU_WITH_NO_PRICE>
    And I follow links addtocartform
    Then there are no addtodefaultcartaction links
    And post to a created addtodefaultcartaction uri
    And the HTTP status is conflict

    Examples:
      | SKU_WITH_NO_PRICE |
      | galaxys2_sku      |

  Scenario Outline: Shippable product can be added to cart
    When I add item with code <SHIPPABLE_ITEM> to my cart
    And I follow links cart
    Then the field total-quantity matches 1

    Examples:
      | SHIPPABLE_ITEM          |
      | handsfree_shippable_sku |