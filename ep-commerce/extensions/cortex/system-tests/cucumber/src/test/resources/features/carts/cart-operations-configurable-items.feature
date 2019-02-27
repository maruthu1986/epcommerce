@Carts
Feature: Cart operations on configurable items
  As a shopper
  I want to be able to modify the contents of my shopping cart
  So that the cart only contains the items I want to purchase

  Background:
    Given I have authenticated as a newly registered shopper

  Scenario Outline: Adding a configurable item to the cart
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
    And the number of cart lineitems is 1
    And the cart total-quantity is 1

    Examples:
      | ITEMCODE   | MESSAGE     | RECIPIENT_EMAIL              | RECIPIENT_NAME | SENDER_NAME  | QTY |
      | berries_20 | Hello World | harry.potter@elasticpath.com | Harry Potter   | MOBEE tester | 1   |

  Scenario Outline: Adding additional quantities of a configurable item to a cart
    When I look up an item with code <ITEMCODE>
    And I go to add to cart form
    And I successfully add the item to the cart with quantity <QTY_1> and configurable fields:
      | giftCertificate.message        | <MESSAGE>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |
    Then the cart lineitem with itemcode <ITEMCODE> has quantity <QTY_1> and configurable fields as:
      | giftCertificate.message        | <MESSAGE>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |
    When I look up an item with code <ITEMCODE>
    And I go to add to cart form
    And I successfully add the item to the cart with quantity <QTY_2> and configurable fields:
      | giftCertificate.message        | <MESSAGE>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |
    Then the cart lineitem with itemcode <ITEMCODE> has quantity 5 and configurable fields as:
      | giftCertificate.message        | <MESSAGE>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |
    And the number of cart lineitems is 1
    And the cart total-quantity is 5

    Examples:
      | ITEMCODE   | MESSAGE     | RECIPIENT_EMAIL              | RECIPIENT_NAME | SENDER_NAME  | QTY_1 | QTY_2 |
      | berries_20 | Hello World | harry.potter@elasticpath.com | Harry Potter   | MOBEE tester | 2     | 3     |

  Scenario Outline: Adding different configurable items to a cart
    When I look up an item with code <ITEMCODE>
    And I go to add to cart form
    And I successfully add the item to the cart with quantity 1 and configurable fields:
      | giftCertificate.message        | <MESSAGE>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |
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
    And the cart lineitem with itemcode <ITEMCODE> has quantity 1 and configurable fields as:
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

  Scenario Outline: Adding same item with different configurations to a cart
    When I look up an item with code <ITEMCODE>
    And I go to add to cart form
    And I successfully add the item to the cart with quantity 1 and configurable fields:
      | giftCertificate.message        | <MESSAGE>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |
    When I look up an item with code <ITEMCODE>
    And I go to add to cart form
    And I successfully add the item to the cart with quantity 1 and configurable fields:
      | giftCertificate.message        | <MESSAGE_2>       |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |
    Then I should see in the response the line item just added with configurable fields as:
      | giftCertificate.message        | <MESSAGE_2>       |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |
    And the cart lineitem with itemcode <ITEMCODE> has quantity 1 and configurable fields as:
      | giftCertificate.message        | <MESSAGE>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |
    And the cart lineitem with itemcode <ITEMCODE> has quantity 1 and configurable fields as:
      | giftCertificate.message        | <MESSAGE_2>       |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |
    And the number of cart lineitems is 2
    And the cart total-quantity is 2

    Examples:
      | ITEMCODE   | MESSAGE     | RECIPIENT_EMAIL              | RECIPIENT_NAME | SENDER_NAME  | MESSAGE_2     |
      | berries_20 | Hello World | harry.potter@elasticpath.com | Harry Potter   | MOBEE tester | Hello World 2 |

  Scenario Outline: Update lineitem quantity does not affect item configurations
    Given I have the item <ITEMCODE> in the cart with quantity 2 and configurable fields:
      | giftCertificate.message        | <MESSAGE>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |
    When I change the lineitem quantity of item code <ITEMCODE> to 1
    Then the cart total-quantity is 1
    And the cart lineitem with itemcode <ITEMCODE> has quantity 1 and configurable fields as:
      | giftCertificate.message        | <MESSAGE>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |

    Examples:
      | ITEMCODE   | MESSAGE     | RECIPIENT_EMAIL              | RECIPIENT_NAME | SENDER_NAME  |
      | berries_20 | Hello World | harry.potter@elasticpath.com | Harry Potter   | MOBEE tester |


  Scenario Outline: Update the quantity of a configurable lineitem in a cart, when same item with different configuration also present in cart
    Given I have the item <ITEMCODE> in the cart with quantity 1 and configurable fields:
      | giftCertificate.message        | <MESSAGE>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |
    And I have the item <ITEMCODE> in the cart with quantity 1 and configurable fields:
      | giftCertificate.message        | <MESSAGE_2>       |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |
    When I change the lineitem quantity of configurable item code <ITEMCODE> with given configuration to 3
      | giftCertificate.message        | <MESSAGE_2>       |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |
    Then the cart total-quantity is 4
    And the cart lineitem with itemcode <ITEMCODE> has quantity 1 and configurable fields as:
      | giftCertificate.message        | <MESSAGE>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |
    And the cart lineitem with itemcode <ITEMCODE> has quantity 3 and configurable fields as:
      | giftCertificate.message        | <MESSAGE_2>       |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |

    Examples:
      | ITEMCODE   | MESSAGE     | RECIPIENT_EMAIL              | RECIPIENT_NAME | SENDER_NAME  | MESSAGE_2     |
      | berries_20 | Hello World | harry.potter@elasticpath.com | Harry Potter   | MOBEE tester | Hello World 2 |


  Scenario Outline: Delete a configurable lineitem from a cart
    Given I have the item <ITEMCODE> in the cart with quantity 10 and configurable fields:
      | giftCertificate.message        | <MESSAGE>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |
    When I delete the configurable lineitem with code <ITEMCODE> and with given configuration from my cart
      | giftCertificate.message        | <MESSAGE>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |
    Then the list of cart lineitems is empty

    Examples:
      | ITEMCODE   | MESSAGE     | RECIPIENT_EMAIL              | RECIPIENT_NAME | SENDER_NAME  |
      | berries_20 | Hello World | harry.potter@elasticpath.com | Harry Potter   | MOBEE tester |


  Scenario Outline: Delete a configurable lineitem from a cart, when same item with different configuration also present in cart
    Given I have the item <ITEMCODE> in the cart with quantity 10 and configurable fields:
      | giftCertificate.message        | <MESSAGE>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |
    And I have the item <ITEMCODE> in the cart with quantity 4 and configurable fields:
      | giftCertificate.message        | <MESSAGE_2>       |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |
    When I delete the configurable lineitem with code <ITEMCODE> and with given configuration from my cart
      | giftCertificate.message        | <MESSAGE_2>       |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |
    Then the cart total-quantity is 10
    And the cart lineitem with itemcode <ITEMCODE> has quantity 10 and configurable fields as:
      | giftCertificate.message        | <MESSAGE>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |

    Examples:
      | ITEMCODE   | MESSAGE     | RECIPIENT_EMAIL              | RECIPIENT_NAME | SENDER_NAME  | MESSAGE_2     |
      | berries_20 | Hello World | harry.potter@elasticpath.com | Harry Potter   | MOBEE tester | Hello World 2 |
    
  