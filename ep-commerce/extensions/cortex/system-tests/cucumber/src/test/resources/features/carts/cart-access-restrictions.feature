@Carts
Feature: Cart access is restricted to the owner
  As a client developer
  I want to restrict access to carts
  so that shoppers cannot view another shopper's cart

  Background:
    Given I login as a registered shopper
    And capture the uri of the registered shopper's cart

  Scenario: Cart access is restricted to the logged in shopper - public shopper
    When I am logged in as a public shopper
    And I attempt to view another shopper's cart
    Then I am not able to view the cart

  Scenario: Cart access is restricted to the logged in shopper - registered shopper
    When I have authenticated as a newly registered shopper
    And I attempt to view another shopper's cart
    Then I am not able to view the cart

  Scenario: Add to cart is restricted to the logged in shopper
    When I am logged in as a public shopper
    And attempt to add to another shopper's cart
    Then I am not able to view the cart

  Scenario: Clearing the cart is restricted to the logged in shopper
    Given I have item with code firstProductAddedToCart_sku in my cart
    When I have authenticated as a newly registered shopper
    And I attempt to clear the first shopper's cart
    Then I am forbidden from deleting the cart
    And the first shopper's cart is not cleared