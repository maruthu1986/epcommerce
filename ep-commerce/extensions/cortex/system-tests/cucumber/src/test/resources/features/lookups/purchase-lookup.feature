@PurchaseLookup @Lookups
Feature: Lookup by purchase number

  Scenario: Lookup purchase number for a registered shopper
    Given I login as a newly registered shopper
    When Adding an item with item code plantsVsZombies and quantity 1 to the cart
    And I create a purchase and view the purchase details
    When I lookup the newly purchased number
    Then the purchase number matches my new purchase

  Scenario: A Public user is unable to lookup a Registered shoppers purchase number
    Given I login as a newly registered shopper
    When Adding an item with item code plantsVsZombies and quantity 1 to the cart
    And I create a purchase and view the purchase details
    When I login as a public shopper
    And I lookup other user's purchase number
    Then lookup fails with status not found

  Scenario: A Registered user is unable to lookup a public shoppers purchase number
    Given I login as a public shopper
    When Adding an item with item code plantsVsZombies and quantity 1 to the cart
    And I create a purchase and view the purchase details
    When I login as a newly registered shopper
    And I lookup other user's purchase number
    Then lookup fails with status not found

  Scenario: A user is unable to lookup another shoppers purchase number in another store
    Given I am logged into scope mobee as a public shopper
    And Adding an item with item code plantsVsZombies and quantity 1 to the cart
    And I create a purchase and view the purchase details
    When I am logged into scope toastie as a public shopper
    And I lookup other user's purchase number
    Then lookup fails with status not found

  # Blocked by PB-3715
  @notready
  Scenario: A shared user should only see order number from the logged in store
    Given I authenticate as a registered shopper harry.potter@elasticpath.com on scope mobee
    And Adding an item with item code plantsVsZombies and quantity 1 to the cart
    And I create a purchase and view the purchase details
    When I authenticate as a registered shopper harry.potter@elasticpath.com on scope toastie
    And I lookup other user's purchase number
    Then lookup fails with status not found

  Scenario Outline: Lookup for an invalid purchase number
    Given I am logged in as a public shopper
    When I look up an invalid purchase number <PURCHASE_NUMBER>
    Then lookup fails with status not found

    Examples:
      | PURCHASE_NUMBER         |
      | invalid_purchase_number |

  Scenario: Revoking access to public user is now unable to lookup purchase number
    Given I login as a public shopper
    When Adding an item with item code plantsVsZombies and quantity 1 to the cart
    And I create a purchase and view the purchase details
    And I invalidate the authentication
    When I login as a new public shopper
    And I lookup other user's purchase number
    Then lookup fails with status not found
