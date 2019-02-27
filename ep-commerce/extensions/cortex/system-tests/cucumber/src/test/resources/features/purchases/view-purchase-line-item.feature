@Purchases
Feature: View purchase line item
  As a shopper, I want to see the configurable items details in my purchase, so that I know what I have configured.

  Scenario Outline: View configurable item details in purchase line item as registered shopper
    Given I login as a registered shopper
    When I look up an item with code <ITEMCODE>
    Then I go to add to cart form
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
    And I select shipping option Canada Post Express
    And I make a purchase
    Then the purchase line item configurable fields for item <ITEM_NAME> are:
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
      | ITEMCODE | QTY | BOOLEAN | DATE       | DATETIME                  | DECIMAL | EMAIL                        | INTEGER | MULTI_OPTION | SHORT_TEXT | SINGLE_OPTION | ITEM_NAME                                     |
      | sscpwaft | 1   | true    | 1996-10-22 | 1996-10-22T03:12:31+04:00 | 13.99   | harry.potter@elasticpath.com | 12      | multiValue1  | hello      | singleValue1  | SingleSkuConfigurableProductWithAllFieldTypes |

  Scenario Outline: View configurable item details in purchase line item as public shopper
    Given I login as a public shopper
    When I look up an item with code <ITEMCODE>
    Then I go to add to cart form
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
    When I fill in email needinfo
    And I fill in payment methods needinfo
    And I fill in billing address needinfo
    And I make a purchase
    Then the purchase line item configurable fields for item <ITEM_NAME> are:
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
      | ITEMCODE      | QTY | BOOLEAN | DATE       | DATETIME                  | DECIMAL | EMAIL                        | INTEGER | MULTI_OPTION | SHORT_TEXT | SINGLE_OPTION | ITEM_NAME                                    |
      | mscpwaftblack | 1   | true    | 1996-10-22 | 1996-10-22T03:12:31+04:00 | 13.99   | harry.potter@elasticpath.com | 12      | multiValue1  | hello      | singleValue1  | MultiSkuConfigurableProductWithAllFieldTypes |

  Scenario Outline: Two identical items with different configuration are shown in purchase line items
    Given I have authenticated as a newly registered shopper
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
      | giftCertificate.message        | <MESSAGE_2>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL_2> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>    |
      | giftCertificate.senderName     | <SENDER_NAME>       |
    And I create a purchase and view the purchase details
    Then the number of purchase lineitems is 2
    And there exists a purchase line item for item <ITEMCODE> with configurable fields:
      | giftCertificate.message        | <MESSAGE>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |
    And there exists a purchase line item for item <ITEMCODE> with configurable fields:
      | giftCertificate.message        | <MESSAGE_2>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL_2> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>    |
      | giftCertificate.senderName     | <SENDER_NAME>       |

    Examples:
      | ITEMCODE   | MESSAGE     | RECIPIENT_EMAIL              | RECIPIENT_NAME | SENDER_NAME  | MESSAGE_2     | RECIPIENT_EMAIL_2    |
      | berries_20 | Hello World | harry.potter@elasticpath.com | Harry Potter   | MOBEE tester | Hello World 2 | test@elasticpath.com |

  Scenario Outline: View components and available options of bundle item (2 components: no options, 2 options
    Given I login as a public shopper
    When I add item with code <ITEMCODE> to my cart
    When I fill in email needinfo
    And I fill in payment methods needinfo
    And I fill in billing address needinfo
    And I make a purchase
    Then I open purchase line item <ITEM_NAME>
    And I follow links components
    And open the element with field name of <COMPONENT_1>
    And I follow links options
    And I can view the "<COMPONENT_1_OPTION_1_NAME>" option for that shipment line item
    And I can view the "<COMPONENT_1_OPTION_1_VALUE>" value for that option
    And I open purchase line item <ITEM_NAME>
    And I follow links components
    And open the element with field name of <COMPONENT_1>
    And I follow links options
    And I can view the "<COMPONENT_1_OPTION_2_NAME>" option for that shipment line item
    And I can view the "<COMPONENT_1_OPTION_2_VALUE>" value for that option
    And I open purchase line item <ITEM_NAME>
    And I follow links components
    And open the element with field name of <COMPONENT_2>
    And there are no options links

    Examples:
      | ITEMCODE         | COMPONENT_1 | COMPONENT_1_OPTION_1_NAME | COMPONENT_1_OPTION_1_VALUE | COMPONENT_1_OPTION_2_NAME | COMPONENT_1_OPTION_2_VALUE | COMPONENT_2        | ITEM_NAME |
      | tbcp_0123456_sku | Avatar      | Purchase Type             | Buy                        | Video Quality             | Standard Definition        | The Social Network | House     |

  Scenario: Validate free line item prices
    Given I login as a public shopper
    And Adding an item with item code tt0034583_sku and quantity 1 to the cart
    And I fill in email needinfo
    And I fill in payment methods needinfo
    And I fill in billing address needinfo
    When I retrieve the purchase
    Then the field monetary-total contains value $0.00
    And the field tax-total contains value $0.00
    And I open purchase line item Casablanca
    And the field line-extension-amount contains value $0.00
    And the field line-extension-tax contains value $0.00
    And the field line-extension-total contains value $0.00

  Scenario: Purchase line item shows correct tax information on scope with inclusive tax
    Given I have authenticated on scope toastie as a newly registered shopper
    And I have item with code tt0970179_sku in my cart
    And I fill in payment methods needinfo
    And I add a GB address
    And I make a purchase
    When I open purchase line item les aventures de Hugo
    Then the field line-extension-amount contains value 19.99
    And the field line-extension-tax contains value 2.98
    And the field line-extension-total contains value 19.99

  Scenario: Bundle line item has price info and component has no price info
    Given I have authenticated as a newly registered shopper
    And I look up an item with code tbcp_0123456_sku
    And I follow links addtocartform
    And I add to cart with quantity of 1
    And I fill in email needinfo
    And I fill in payment methods needinfo
    And I fill in billing address needinfo
    And I make a purchase
    When I open purchase line item House
    Then the field line-extension-amount contains value $30.00
    And the field line-extension-tax contains value $3.60
    And the field line-extension-total contains value $33.60
    And I follow links components
    And open the element with field name of Avatar
    Then the field line-extension-amount does not exist
    And the field line-extension-tax does not exist
    And the field line-extension-total does not exist

  Scenario Outline: Nested bundle line item
    Given I have authenticated as a newly registered shopper
    And I look up an item with code <BUNDLE_CODE>
    And I follow links addtocartform
    And I add to cart with quantity of 2
    And I fill in email needinfo
    And I fill in payment methods needinfo
    And I fill in billing address needinfo
    And I make a purchase
    When I open purchase line item <BUNDLE_NAME>
    Then the field line-extension-amount contains value <line-extension-amount>
    And the field line-extension-tax contains value <line-extension-tax>
    And the field line-extension-total contains value <line-extension-total>

    Examples:
      | BUNDLE_TYPE              | BUNDLE_CODE      | BUNDLE_NAME         | line-extension-amount | line-extension-tax | line-extension-total |
      | Nested assigned bundle   | tbcp_1234567_sku | Best Series 2011    | $159.98               | $19.20             | $179.18              |
      | Nested calculated bundle | mb_4324324_sku   | RentMovieLowTVCombo | $405.86               | $48.72             | $454.58              |

  Scenario Outline: Only bundle line item shows components link
    Given I have authenticated as a newly registered shopper
    And I look up an item with code <BUNDLE_CODE>
    And I follow links addtocartform
    And I add to cart with quantity of 1
    And I look up an item with code <ITEM_CODE>
    And I follow links addtocartform
    And I add to cart with quantity of 1
    And I fill in email needinfo
    And I fill in payment methods needinfo
    And I fill in billing address needinfo
    And I retrieve the purchase
    When I open purchase line item <BUNDLE_NAME>
    And I follow links components
    And open the element with field name of <NESTED_BUNDLE>
    Then there is a components link
    And I open purchase line item <BUNDLE_NAME>
    And I follow links components
    And open the element with field name of <NESTED_ITEM>
    And there are no components links
    And I open purchase line item <ITEM_NAME>
    And there are no components links

    Examples:
      | BUNDLE_CODE      | BUNDLE_NAME      | ITEM_CODE     | ITEM_NAME | NESTED_BUNDLE | NESTED_ITEM |
      | tbcp_1234567_sku | Best Series 2011 | tt0970179_sku | Hugo      | House         | Superheroes |

  Scenario Outline: Traverse nested bundle tree from root to the leaf and back
    Given I login as a registered shopper
    And I look up an item with code <BUNDLE_CODE>
    And I follow links addtocartform
    And I add to cart with quantity of 1
    And I make a purchase
    Then I open purchase line item <BUNDLE_NAME>
    And I follow links components
    And open the element with field name of <LEVEL_1>
    And I follow links components
    And open the element with field name of <LEVEL_2>
    And I follow links options
    And open the element with field name of <LEVEL_3>
    And I follow links list
    And I follow links lineitem
    Then the field name has value <LEVEL_2>
    And I follow links list
    And I follow links lineitem
    Then the field name has value <LEVEL_1>
    And I follow links list
    And I follow links lineitem
    Then the field name has value <BUNDLE_NAME>

    Examples:
      | BUNDLE_TYPE            | BUNDLE_CODE      | BUNDLE_NAME      | LEVEL_1 | LEVEL_2 | LEVEL_3      |
      | Nested assigned bundle | tbcp_1234567_sku | Best Series 2011 | House   | Avatar  | VideoQuality |

  Scenario Outline: Traverse nested bundle tree to the leaf then to the neighbouring leaf and then back to the root
    Given I login as a registered shopper
    And I look up an item with code <BUNDLE_CODE>
    And I follow links addtocartform
    And I add to cart with quantity of 1
    And I make a purchase
    Then I open purchase line item <BUNDLE_NAME>
    And I follow links components
    And open the element with field name of <LEVEL_1>
    And I follow links components
    And open the element with field name of <LEVEL_2>
    And I follow links options
    And open the element with field name of <LEVEL_3>
    And I follow links list
    And I follow links lineitem
    Then the field name has value <LEVEL_2>
    And I follow links list
    And open the element with field name of <LEVEL_2_NEIGHBOUR>
    And I follow links list
    And I follow links lineitem
    Then the field name has value <LEVEL_1>
    And I follow links list
    And I follow links lineitem
    Then the field name has value <BUNDLE_NAME>

    Examples:
      | BUNDLE_TYPE            | BUNDLE_CODE      | BUNDLE_NAME      | LEVEL_1 | LEVEL_2 | LEVEL_3      | LEVEL_2_NEIGHBOUR  |
      | Nested assigned bundle | tbcp_1234567_sku | Best Series 2011 | House   | Avatar  | VideoQuality | The Social Network |

  Scenario: Structured message shows when attempting to purchase empty cart
    Given I login as a registered shopper
    When I retrieve the purchase form
    Then there are advisor messages with the following fields:
      | messageType | messageId  | debugMessage            |
      | needinfo    | cart.empty | Shopping cart is empty. |
