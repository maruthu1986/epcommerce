@Carts
Feature: Cart Merge
  As an anonymous shopper, I want the ability to transfer my cart items to a registered account so that I do not need to
  add same items to cart again when I am registered and signed in.

  Background:
    Given I am logged in as a public shopper

  Scenario Outline: Public shopper's cart is merged to a newly registered shopper's cart
    Given I have item with code <ITEMCODE> in my cart
    When I register and transition to a new shopper
    Then the number of cart lineitems is 1
    And the cart total-quantity is 1
    And the cart lineitem for item code <ITEMCODE> has quantity of 1

    Examples:
      | ITEMCODE |
      | FocUSsku |

  Scenario Outline: Public shopper's cart's configurable item is merged to a newly registered shopper's  cart
    Given I have an item with code <ITEMCODE> in the cart with quantity 1 and configurable fields:
      | allFieldTypes.boolean           | <BOOLEAN>       |
      | allFieldTypes.date              | <DATE>          |
      | allFieldTypes.dateTime          | <DATETIME>      |
      | allFieldTypes.decimal           | <DECIMAL>       |
      | allFieldTypes.email             | <EMAIL>         |
      | allFieldTypes.integer           | <INTEGER>       |
      | allFieldTypes.multiSelectOption | <MULTI_OPTION>  |
      | allFieldTypes.shortText         | <SHORT_TEXT>    |
      | allFieldTypes.singleOption      | <SINGLE_OPTION> |
    When I register and transition to a new shopper
    And I go to my cart
    Then the cart lineitem with itemcode <ITEMCODE> has quantity <QTY> and configurable fields as:
      | allFieldTypes.boolean           | <BOOLEAN>       |
      | allFieldTypes.date              | <DATE>          |
      | allFieldTypes.dateTime          | <DATETIME>      |
      | allFieldTypes.decimal           | <DECIMAL>       |
      | allFieldTypes.email             | <EMAIL>         |
      | allFieldTypes.integer           | <INTEGER>       |
      | allFieldTypes.multiSelectOption | <MULTI_OPTION>  |
      | allFieldTypes.shortText         | <SHORT_TEXT>    |
      | allFieldTypes.singleOption      | <SINGLE_OPTION> |

    Examples:
      | ITEMCODE | QTY | BOOLEAN | DATE       | DATETIME                  | DECIMAL | EMAIL                        | INTEGER | MULTI_OPTION | SHORT_TEXT | SINGLE_OPTION |
      | sscpwaft | 1   | true    | 1996-10-22 | 1996-10-22T03:12:31+04:00 | 13.99   | harry.potter@elasticpath.com | 12      | multiValue1  | hello      | singleValue1  |

  Scenario Outline: Merge Gift Certificate from public shopper's cart to a newly registered shopper's cart
    Given I have a <GIFTCARD> in the cart with quantity 1 and configurable fields:
      | giftCertificate.message        | <MESSAGE>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |
    When I register and transition to a new shopper
    And I go to my cart
    Then the cart lineitem with itemcode <GIFTCARD> has quantity <QTY> and configurable fields as:
      | giftCertificate.message        | <MESSAGE>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |

    Examples:
      | GIFTCARD   | MESSAGE     | RECIPIENT_EMAIL              | RECIPIENT_NAME | SENDER_NAME  | QTY |
      | berries_20 | Hello World | harry.potter@elasticpath.com | Harry Potter   | MOBEE tester | 1   |

#	Gift certificates are considered unique even with identical configuration hence the quantities are added
  Scenario Outline: Identical Gift Certificates are merged into single lineitem in registered shopper's cart
    Given a registered shopper <EMAIL_ID> with the following configured item in their cart
      | itemcode                       | <GIFTCARD>        |
      | itemqty                        | 1                 |
      | giftCertificate.message        | <MESSAGE>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |
    And I am logged in as a public shopper
    And I have a <GIFTCARD> in the cart with quantity 1 and configurable fields:
      | giftCertificate.message        | <MESSAGE>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |
    When I transition to the registered shopper with email ID <EMAIL_ID>
    Then the number of cart lineitems is 1
    And the cart total-quantity is 2
    And the cart lineitem for item code <GIFTCARD> has quantity of 2

    Examples:
      | EMAIL_ID                  | GIFTCARD   | MESSAGE     | RECIPIENT_EMAIL              | RECIPIENT_NAME | SENDER_NAME  |
      | lisa.hunt@elasticpath.com | berries_20 | Hello World | harry.potter@elasticpath.com | Harry Potter   | MOBEE tester |

 #	Configurable items are considered identical with identical configuration hence the public user's quantity is used
  Scenario Outline: Identical configurable items are merged into single lineitem in registered shopper's cart and later quantity takes precedence
    Given a registered shopper <EMAIL_ID> with the following configured item in their cart
      | itemcode                        | <ITEMCODE>      |
      | itemqty                         | 1               |
      | allFieldTypes.boolean           | <BOOLEAN>       |
      | allFieldTypes.date              | <DATE>          |
      | allFieldTypes.dateTime          | <DATETIME>      |
      | allFieldTypes.decimal           | <DECIMAL>       |
      | allFieldTypes.email             | <EMAIL>         |
      | allFieldTypes.integer           | <INTEGER>       |
      | allFieldTypes.multiSelectOption | <MULTI_OPTION>  |
      | allFieldTypes.shortText         | <SHORT_TEXT>    |
      | allFieldTypes.singleOption      | <SINGLE_OPTION> |
    And I am logged in as a public shopper
    And I have a <ITEMCODE> in the cart with quantity 3 and configurable fields:
      | allFieldTypes.boolean           | <BOOLEAN>       |
      | allFieldTypes.date              | <DATE>          |
      | allFieldTypes.dateTime          | <DATETIME>      |
      | allFieldTypes.decimal           | <DECIMAL>       |
      | allFieldTypes.email             | <EMAIL>         |
      | allFieldTypes.integer           | <INTEGER>       |
      | allFieldTypes.multiSelectOption | <MULTI_OPTION>  |
      | allFieldTypes.shortText         | <SHORT_TEXT>    |
      | allFieldTypes.singleOption      | <SINGLE_OPTION> |
    When I transition to the registered shopper with email ID <EMAIL_ID>
    Then the number of cart lineitems is 1
    And the cart total-quantity is 3
    And the cart lineitem for item code <ITEMCODE> has quantity of 3

    Examples:
      | EMAIL_ID                  | ITEMCODE | BOOLEAN | DATE       | DATETIME                  | DECIMAL | EMAIL                        | INTEGER | MULTI_OPTION | SHORT_TEXT | SINGLE_OPTION |
      | lisa.hunt@elasticpath.com | sscpwaft | true    | 1996-10-22 | 1996-10-22T03:12:31+04:00 | 13.99   | harry.potter@elasticpath.com | 12      | multiValue1  | hello      | singleValue1  |

  Scenario Outline: Different configuration of the same item become different line items when carts are merged
    Given a registered shopper <EMAIL_ID> with the following configured item in their cart
      | itemcode                        | <ITEMCODE>      |
      | itemqty                         | 1               |
      | allFieldTypes.boolean           | <BOOLEAN>       |
      | allFieldTypes.date              | <DATE>          |
      | allFieldTypes.dateTime          | <DATETIME>      |
      | allFieldTypes.decimal           | <DECIMAL>       |
      | allFieldTypes.email             | <EMAIL>         |
      | allFieldTypes.integer           | <INTEGER>       |
      | allFieldTypes.multiSelectOption | <MULTI_OPTION>  |
      | allFieldTypes.shortText         | <SHORT_TEXT>    |
      | allFieldTypes.singleOption      | <SINGLE_OPTION> |
    And I am logged in as a public shopper
    And I have a <ITEMCODE> in the cart with quantity 1 and configurable fields:
      | allFieldTypes.boolean           | <BOOLEAN_2>     |
      | allFieldTypes.date              | <DATE>          |
      | allFieldTypes.dateTime          | <DATETIME>      |
      | allFieldTypes.decimal           | <DECIMAL>       |
      | allFieldTypes.email             | <EMAIL>         |
      | allFieldTypes.integer           | <INTEGER>       |
      | allFieldTypes.multiSelectOption | <MULTI_OPTION>  |
      | allFieldTypes.shortText         | <SHORT_TEXT>    |
      | allFieldTypes.singleOption      | <SINGLE_OPTION> |
    When I transition to the registered shopper with email ID <EMAIL_ID>
    Then the number of cart lineitems is 2
    And the cart total-quantity is 2
    And the cart lineitem for item code <ITEMCODE> has quantity of 1

    Examples:
      | EMAIL_ID                  | ITEMCODE | BOOLEAN | DATE       | DATETIME                  | DECIMAL | EMAIL                        | INTEGER | MULTI_OPTION | SHORT_TEXT | SINGLE_OPTION | BOOLEAN_2 |
      | lisa.hunt@elasticpath.com | sscpwaft | true    | 1996-10-22 | 1996-10-22T03:12:31+04:00 | 13.99   | harry.potter@elasticpath.com | 12      | multiValue1  | hello      | singleValue1  | false     |

  Scenario Outline: Registered shopper's cart items should not merge to public shopper's cart
    Given I have authenticated as a newly registered shopper
    And I have item with code <ITEMCODE> in my cart
    When I transition to a public shopper
    Then the number of cart lineitems is 0

    Examples:
      | ITEMCODE |
      | FocUSsku |

  Scenario Outline: Registered shopper's cart items should not merge to another registered shopper's cart
    Given I login as a newly registered shopper
    And I have item with code <ITEMCODE_1> in my cart
    And I have authenticated as another registered shopper
    And I have item with code <ITEMCODE_2> in my cart
    When I transition to the first registered shopper
    Then the number of cart lineitems is 1
    And the cart lineitem for item code <ITEMCODE_1> has quantity of 1

    Examples:
      | ITEMCODE_1 | ITEMCODE_2    |
      | FocUSsku   | tt0970179_sku |

  Scenario Outline: Cart merge from public shopper to registered shopper with same item latest quantity takes precedence
    Given I login as a newly registered shopper
    And I have item with code <ITEMCODE> in my cart with quantity <REGISTERED_CART_QTY>
    And I am logged in as a public shopper
    And I have item with code <ITEMCODE> in my cart with quantity <PUBLIC_CART_QTY>
    When I transition to the newly registered shopper
    Then the number of cart lineitems is 1
    And the cart total-quantity is <PUBLIC_CART_QTY>
    And the cart lineitem for item code <ITEMCODE> has quantity of <PUBLIC_CART_QTY>

 #    multi sku item
 #    bundle item
 #    single sku item
 #    digital sku item
    Examples:
      | ITEMCODE               | REGISTERED_CART_QTY | PUBLIC_CART_QTY |
      | portable_tv_hdrent_sku | 1                   | 3               |
      | portable_tv_hdrent_sku | 3                   | 1               |
      | mb_8901234_sku         | 1                   | 3               |
      | mb_8901234_sku         | 3                   | 1               |
      | FocUSsku               | 1                   | 3               |
      | FocUSsku               | 3                   | 1               |
      | tt0970179_sku          | 1                   | 3               |
      | tt0970179_sku          | 3                   | 1               |