@Prices

Feature: Prices - Retrieve Item Pricing By Scope
  As a client developer,
  I want to retrieve price data that corresponds to my scope,
  so I can display pricing information that matches the locale configured for my store

  Scenario: Mobee pricing is returned in Canadian Dollars for item
    Given I am logged into scope mobee as a public shopper
    And I look up an item with code tt0970179_sku
    When I view the item price
    Then the list-price has fields amount: 34.99, currency: CAD and display: $34.99
    And the purchase-price has fields amount: 34.99, currency: CAD and display: $34.99

  Scenario: Toastie pricing for the same product is returned in Euros
    Given I am logged into scope toastie as a public shopper
    And I look up an item with code tt0970179_sku
    When I view the item price
    Then the list-price has fields amount: 19.99, currency: EUR and display: €19.99
    And the purchase-price has fields amount: 19.99, currency: EUR and display: €19.99
