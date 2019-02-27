@Items @Wishlists
Feature: Wishlist memberships

  Background:
    Given I have authenticated as a newly registered shopper

  Scenario: wishlistmemberships list is empty for an item that is not in a wish list
    When item with name digitalProduct is not in my default wishlist
    Then digitalProduct's wishlist membership does not contain the default wishlist

  Scenario: wishlistmemberships list is not empty for an item that is in a wish list
    When I have digitalProduct in my default wishlist
    Then digitalProduct's wishlist membership contains the default wishlist