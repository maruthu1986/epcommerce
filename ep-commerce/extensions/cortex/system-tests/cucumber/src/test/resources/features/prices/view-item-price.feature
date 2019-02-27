@Prices

Feature: Prices - Retrieve Item Price
  as a client developer,
  I want to retrieve the price of an item,
  so that I can display the list-price which should represent the original price before any discounts applied,
  and so that I can display the purchase-price which should represent the price the shopper pays for one unit after catalog promotions discounts and sale pricing has applied.

  Background:
    Given I am logged in as a public shopper

  Scenario: Item without sale pricing has the same list-price and purchase-price
    Given an item Product With No Discounts exists in my catalog
    When I view the item price
    Then the list-price has fields amount: 10.0, currency: CAD and display: $10.00
    And the purchase-price has fields amount: 10.0, currency: CAD and display: $10.00

  Scenario: Item with sale pricing has a lower purchase-price than the list-price
    Given an item Product With Sale Price exists in my catalog
    When I view the item price
    Then the list-price has fields amount: 10.0, currency: CAD and display: $10.00
    And the purchase-price has fields amount: 8.0, currency: CAD and display: $8.00

  Scenario: Item without sale pricing, but with a catalog promo has a lower purchase-price than list-price
    Given an item Product With Catalog Promotion of 10 Percent Off exists in my catalog
    When I view the item price
    Then the list-price has fields amount: 10.0, currency: CAD and display: $10.00
    And the purchase-price has fields amount: 9.0, currency: CAD and display: $9.00

  Scenario: Item with both sale pricing and catalog promo has lower purchase-price than list-price
    Given an item Product With Sale Price and Catalog Promo of 10 Percent Off exists in my catalog
    When I view the item price
    Then the list-price has fields amount: 10.0, currency: CAD and display: $10.00
    And the purchase-price has fields amount: 7.2, currency: CAD and display: $7.20

  Scenario: A configurable item (multi-sku product) has different pricing per configuration
    Given an item Finding Nemo exists in my catalog
    When I view the item price
    Then the list-price has fields amount: 30.0, currency: CAD and display: $30.00
    And the purchase-price has fields amount: 30.0, currency: CAD and display: $30.00
    When I view the pricing for the SD configuration
    Then the list-price has fields amount: 35.99, currency: CAD and display: $35.99
    And the purchase-price has fields amount: 25.99, currency: CAD and display: $25.99