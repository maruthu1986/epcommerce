@Items @Carts
Feature: Carts memberships

  Background:
    Given I have authenticated as a newly registered shopper

  Scenario: An item always has a link to cartmemberships, even if the item is not in my default cart
    When item digitalProduct is not in any of my carts
    Then digitalProduct's cart membership does not contain the default cart

  Scenario: Cart memberships list is not empty if an item is in a cart
    When I have item digitalProduct in the default cart
    Then digitalProduct's cart membership contains the default cart