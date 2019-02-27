@Wishlists
Feature: Add to wishlist
  As a shopper
  I want to save items to my wishlist
  so I can easily find them for purchase at a later date

  Background:
    Given I have authenticated as a newly registered shopper

  Scenario Outline: Add item to wishlist
    Given I add item with name <ITEMNAME> to my default wishlist
    When I view my default wishlist
    Then item with name <ITEMNAME> is in my default wishlist

    Examples:
      | ITEMNAME                                      |
      | digitalProduct                                |
      | physicalProduct                               |
      | multiskuProduct                               |
      | SingleSkuConfigurableProductWithAllFieldTypes |
      | Dynamic Bundle                                |

  Scenario Outline: Add item to wishlist returns correct HTTP status
    When I add item with name <ITEMNAME> to my default wishlist
    Then the HTTP status is OK, created

    Examples:
      | ITEMNAME        |
      | physicalProduct |

  Scenario Outline: Add existing item to wishlist returns correct HTTP status
    Given I have <ITEMNAME> in my default wishlist
    When I add item with name <ITEMNAME> to my default wishlist
    Then the HTTP status is OK

    Examples:
      | ITEMNAME        |
      | physicalProduct |

  Scenario Outline: Non-purchaseable item can still be added to wishlist
    Given <ITEMNAME> is not purchaseable
    When I add item with name <ITEMNAME> to my default wishlist
    Then item with name <ITEMNAME> is in my default wishlist
    And I cannot move item <ITEMNAME> to my cart

    Examples:
      | ITEMNAME                        |
      | physicalProductWithoutInventory |
      | productWithoutPrice             |
      | calculatedBundleWithNoPriceItem |
      | bundleWithOutOfStockConstituent |

  Scenario Outline: Add item to wishlist - retrieve from root
    Given I add item with name <ITEMNAME> to my default wishlist
    When I navigate to root's default wishlist
    Then item with name <ITEMNAME> is in my default wishlist

    Examples:
      | ITEMNAME        |
      | digitalProduct  |
      | physicalProduct |
      | multiskuProduct |

  Scenario Outline: Adding configurable item to wishlist should be possible
    Given I add item with code <ITEMCODE> to my default wishlist
    When I view my default wishlist
    Then item with code <ITEMCODE> is in my default wishlist
    Then I should see wishlist line item configurable fields for itemcode <ITEMCODE> as:
      | giftCertificate.message        | <MESSAGE>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |

    Examples:
      | ITEMCODE   | MESSAGE | RECIPIENT_EMAIL | RECIPIENT_NAME | SENDER_NAME |
      | berries_20 |         |                 |                |             |

  Scenario Outline: Same sku items should be in same wishlist lineitem
#   Item 1
    Given I add item with code <ITEMCODE> to my default wishlist
    When I view my default wishlist
    Then item with code <ITEMCODE> is in my default wishlist
#   Item 2
    Given I add item with code <ITEMCODE> to my default wishlist
    When I view my default wishlist
    Then item with code <ITEMCODE> is in my default wishlist

    Then my default wishlist has 1 lineitems
#   Test data contains:
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

  Scenario Outline: Multisku items with different sku values should be in separate wishlist lineitems
#   Item 1
    Given I add item with code <ITEMCODE> to my default wishlist
    When I view my default wishlist
    Then item with code <ITEMCODE> is in my default wishlist
#   Item 2
    When I look up an item with code <ITEMCODE>
    And I change the multi sku selection by <OPTION> and select choice <VALUE>
    And the item code is <ITEM_SKU_CODE>
    And I add selected multisku item to the wishlist
    Then item with code <ITEM_SKU_CODE> is in my default wishlist

    Then my default wishlist has 2 lineitems

    Examples:
      | ITEMCODE               | OPTION        | ITEM_SKU_CODE         | VALUE |
      | portable_tv_hdrent_sku | Purchase Type | portable_tv_hdbuy_sku | Buy   |


  Scenario Outline: Adding configurable item to wishlist should return correct line item
    When I look up an item with code <ITEMCODE>
    Then I go to add to cart form
    And I add the item to the cart with quantity 1 and configurable fields:
      | giftCertificate.message        | <MESSAGE_1>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL_1> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME_1>  |
      | giftCertificate.senderName     | <SENDER_NAME_1>     |
    When I move an item with code <ITEMCODE> from my cart to my default wishlist
    Then I should see wishlist line item <ITEMCODE> with configurable field values as:
      | giftCertificate.message        | <MESSAGE_1>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL_1> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME_1>  |
      | giftCertificate.senderName     | <SENDER_NAME_1>     |
    And item with code <ITEMCODE> is not found in my cart

    When I add item with code <ITEMCODE> to my default wishlist
    Then I should see in the response the line item just added with configurable fields as:
      | giftCertificate.message        | <MESSAGE_2>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL_2> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME_2>  |
      | giftCertificate.senderName     | <SENDER_NAME_2>     |

    Examples:
      | ITEMCODE   | MESSAGE_1   | RECIPIENT_EMAIL_1            | RECIPIENT_NAME_1 | SENDER_NAME_1 | MESSAGE_2 | RECIPIENT_EMAIL_2 | RECIPIENT_NAME_2 | SENDER_NAME_2 |
      | berries_20 | Hello World | harry.potter@elasticpath.com | Harry Potter     | MOBEE tester  |           |                   |                  |               |

