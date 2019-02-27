@Prices

Feature: Prices - Cart LineItem Price Depends on Quantity 'price tiers'
  As a client developer,
  I want to retrieve tiered pricing,
  to entice shoppers to purchase in larger quantities.

  Background:
    Given I am logged in as a public shopper

  Scenario: Retrieve the first tier pricing item
    When I add item Acon Bluetooth headset to the cart with quantity 1
    Then the lineitem price has list-price fields amount: 120.0, currency: CAD and display: $120.00

  Scenario: Retrieve the next tier pricing when sufficient quantity has been added to cart
    When I add item Acon Bluetooth headset to the cart with quantity 5
    Then the lineitem price has list-price fields amount: 110.0, currency: CAD and display: $110.00
