@Wishlists
Feature: Manage wishlists
  As a shopper
  I want to manage my list of wishlists
  so I can track and organize the list of items I want to purchase in the future.

  Scenario: Newly registered shopper has 1 wishlist, which is the default
    Given I have authenticated as a newly registered shopper
    When I view my profile
    Then there is a wishlists link
    And my list of wishlists contains 1 wishlist

  Scenario: Cannot delete default wishlist
    Given I have authenticated as a newly registered shopper
    When I view my default wishlist
    Then I cannot delete my default wishlist

  Scenario: Anonymous shoppers have wishlists
    Given I am logged in as a public shopper
    When I view my profile
    Then there is a wishlists link
    And my list of wishlists contains 1 wishlist

  Scenario Outline: Wishlists are unique to scope
    Given I have authenticated on scope mobee as a newly registered shopper
    And I add item with name <ITEMNAME> to my default wishlist
    And item with name <ITEMNAME> is in my default wishlist
    When I re-authenticate on scope toastie with the newly registered shopper
    Then my default wishlist has 0 lineitems

    Examples:
      | ITEMNAME       |
      | digitalProduct |

  Scenario Outline: Wishlists are persisted
    Given I login as a newly registered shopper
    And I add item with code <ITEMCODE> to my default wishlist
    And I login as a public shopper
    And my default wishlist has 0 lineitems
    When I re-login with the original registered shopper
    Then my default wishlist has 1 lineitem
    And item with code <ITEMCODE> is in my default wishlist

    Examples:
      | ITEMCODE               |
      | portable_tv_hdrent_sku |