@Wishlists
Feature: Manage wishlist items
  As a shopper
  I want to manage wishlist items
  so I can keep track of items I want to purchase at a later date

  Background:
    Given I have authenticated as a newly registered shopper

  Scenario Outline: Delete an item from a wishlist
    Given I add item with code <ITEMCODE> to my default wishlist
    When I delete item with code <ITEMCODE> from my default wishlist
    Then item with code <ITEMCODE> is not found in my default wishlist

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

  Scenario: Delete all items from a wishlist in a single call
    Given I add item with name digitalProduct to my default wishlist
    And I add item with name physicalProduct to my default wishlist
    When I delete the list of wishlist items
    Then my default wishlist has 0 lineitems
