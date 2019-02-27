@Addresses
Feature: Address created as public shopper is transferred to the newly created shopper

  Scenario:
    Given I login as a public shopper
    When I add item with code tt888456tw to my cart
    And I fill in email needinfo
    And I fill in payment methods needinfo
    And I fill in billing address needinfo
    And I make a purchase
    And I register and transition to a new shopper
    Then I should see 1 element on addresses
    And address element 1 is identical to the public shopper's address