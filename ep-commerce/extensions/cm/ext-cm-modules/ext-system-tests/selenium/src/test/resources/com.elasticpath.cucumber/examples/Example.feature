@example
Feature: Catalog Browse

  Background:
    Given I sign in to CM as admin user

  Scenario: Browse catalog
    When I go to Catalog Management
    And I expand Mobile Catalog catalog
    And I open category Accessories to view products list
    Then Product Listing should contain following products
      | Portable TV     |
      | Samsung Headset |

    #example definition
    When Example Definition - I open category TV Series to view products list
    Then Example Definition - Product Listing should contain following products
      | House       |
      | Superheroes |


