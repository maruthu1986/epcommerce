@Authorization

Feature: Alien Resource Authorization

  Background:
    Given I login as a registered shopper
    And I add item with code tt777789bttf_hd_rent to my cart
    And capture the uri of the registered shopper's cart line item price

  Scenario: Cart line item price access is restricted to the logged in shopper
    Given I have authenticated as a newly registered shopper
    When I attempt to view another shopper's cart line item price
    Then I am not able to view another shopper's cart line item price