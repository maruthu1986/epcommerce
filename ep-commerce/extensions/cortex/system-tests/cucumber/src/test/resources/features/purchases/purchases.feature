@Purchases

Feature: purchase resource tests

  Scenario: Unable to view another shoppers purchases
    Given I have authenticated as a newly registered shopper
    And Adding an item with item code plantsVsZombies and quantity 1 to the cart
    And I fill in email needinfo
    And I fill in payment methods needinfo
    And I fill in billing address needinfo
    And the order is submitted
    And the HTTP status is OK
    And save the purchase uri
    When I have authenticated as a newly registered shopper
    And attempt to access the other shoppers purchase
    Then the HTTP status is forbidden

  Scenario: List of purchases not displayed when no purchases
    Given I have authenticated as a newly registered shopper
    When I view my profile
    And I follow links purchases
    Then there are no element links

  Scenario: List of purchases is single scoped
    Given I have authenticated on scope mobee as a newly registered shopper
    And Adding an item with item code plantsVsZombies and quantity 1 to the cart
    And I fill in email needinfo
    And I fill in payment methods needinfo
    And I fill in billing address needinfo
    And the order is submitted
    And the HTTP status is OK
    When I re-authenticate on scope kobee with the newly registered shopper
    And I view my profile
    And I follow links purchases
    Then there are no element links

  Scenario: Can retrieve all successful purchases
    Given I login as a newly registered shopper
    And Adding an item with item code plantsVsZombies and quantity 1 to the cart
    And I fill in email needinfo
    And I fill in payment methods needinfo
    And I fill in billing address needinfo
    And the order is submitted
    And the HTTP status is OK
    And Adding an item with item code plantsVsZombies and quantity 1 to the cart
    And I fill in email needinfo
    And I fill in payment methods needinfo
    And I fill in billing address needinfo
    And the order is submitted
    And the HTTP status is OK
    When I view my profile
    And I follow links purchases
    Then there are 2 links of rel element

  Scenario: Purchase has purchase number and same total as the order
    Given I login as a newly registered shopper
    And Adding an item with item code plantsVsZombies and quantity 1 to the cart
    And I fill in email needinfo
    And I fill in payment methods needinfo
    And I fill in billing address needinfo
    And the order total has amount: 0.99, currency: CAD and display: $0.99
    And the order is submitted
    And the HTTP status is OK
    When I view my profile
    And I follow links purchases -> element
    Then the field purchase-number matches \d\d\d\d\d
    And the field monetary-total contains value $0.99