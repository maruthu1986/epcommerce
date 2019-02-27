@Wishlists
Feature: Merge wishlists
  As a shopper,
  when I transition from anonymous to registered, I want my wishlists merged
  so I don't lose track of any items

  Scenario: Merge wishlist - same item
    Given physicalProduct is in my registered shopper's default wishlist
    And physicalProduct is in my anonymous shopper's default wishlist
    When I transition to the registered shopper
    Then my default wishlist has 1 lineitem
    And item with name physicalProduct is in my default wishlist

  Scenario: Merge wishlist - different items
    Given physicalProduct is in my registered shopper's default wishlist
    And digitalProduct is in my anonymous shopper's default wishlist
    When I transition to the registered shopper
    Then my default wishlist has 2 lineitems
    And item with name physicalProduct is in my default wishlist
    And item with name digitalProduct is in my default wishlist

  Scenario Outline: Registered shopper wishlist item should not merge to another registered wishlist
    When I authenticate as a registered shopper <EMAIL_ID_1> with the default scope
    And I delete the list of wishlist items
    And I add item with code <ITEMCODE_1> to my default wishlist
    When I view my default wishlist
    Then item with code <ITEMCODE_1> is in my default wishlist
    And my default wishlist has 1 lineitems

    When I authenticate as a registered shopper <EMAIL_ID_2> with the default scope
    And I delete the list of wishlist items
    And I add item with code <ITEMCODE_2> to my default wishlist
    When I view my default wishlist
    Then item with code <ITEMCODE_2> is in my default wishlist
    And my default wishlist has 1 lineitems

    When I transition to the registered shopper with email ID <EMAIL_ID_1>
    And I view my default wishlist
    Then item with code <ITEMCODE_1> is in my default wishlist
    And my default wishlist has 1 lineitems

    Examples:
      | EMAIL_ID_1                | ITEMCODE_1 | EMAIL_ID_2                   | ITEMCODE_2             |
      | lisa.hunt@elasticpath.com | FocUSsku   | harry.potter@elasticpath.com | portable_tv_hdrent_sku |

  Scenario Outline: Registered shopper wishlist item is persisted
    When I authenticate as a registered shopper <EMAIL_ID_1> with the default scope
    And I delete the list of wishlist items
    And I add item with code <ITEMCODE_1> to my default wishlist
    When I view my default wishlist
    Then item with code <ITEMCODE_1> is in my default wishlist
    And my default wishlist has 1 lineitems

    When I am logged in as a public shopper
    And I authenticate as a registered shopper <EMAIL_ID_1> with the default scope
    And I view my default wishlist
    Then item with code <ITEMCODE_1> is in my default wishlist
    And my default wishlist has 1 lineitems

    Examples:
      | EMAIL_ID_1                | ITEMCODE_1                                 |
      #   multisku item
      | lisa.hunt@elasticpath.com | portable_tv_hdrent_sku                     |
      #   Singlesku item
      | lisa.hunt@elasticpath.com | FocUSsku                                   |
      #   Bundle item
      | lisa.hunt@elasticpath.com | bundleWithPhysicalAndDigitalComponents_sku |
      #   Digital item
      | lisa.hunt@elasticpath.com | alien_sku                                  |
      #   Dynamic Bundle
      | lisa.hunt@elasticpath.com | tb_dyn12345sku                             |

  Scenario Outline: Stores with shared login do not share wishlists
    When I have authenticated on scope mobee as a newly registered shopper
    And I add item with code <ITEMCODE_1> to my default wishlist
    And I view my default wishlist
    And item with code <ITEMCODE_1> is in my default wishlist
    And my default wishlist has 1 lineitem
    When I re-authenticate on scope toastie with the original registered shopper
    And I view my default wishlist
    Then my default wishlist has 0 lineitems

    Examples:
      | ITEMCODE_1             |
      | portable_tv_hdrent_sku |