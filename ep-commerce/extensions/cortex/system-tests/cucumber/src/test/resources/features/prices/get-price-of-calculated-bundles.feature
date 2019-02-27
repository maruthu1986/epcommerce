@Prices

Feature: Prices - Retrieve Item Price for Calculated Bundles
  As a client developer,
  I want to retrieve the price of calculated bundles,
  so I can display pricing information to my shoppers.

  Background:
    Given I am logged in as a public shopper

  Scenario: Get price information for bundle product
    Given an item Movie Classics Bundle exists in my catalog
    When I view the item price
    Then the list-price has fields amount: 34.0, currency: CAD and display: $34.00
    And the purchase-price has fields amount: 29.0, currency: CAD and display: $29.00

  Scenario: Price link should not exist for calculated bundles if any of its components do not have a price
    When an item SmartPhones Bundle exists in my catalog
    Then the item does not have a price link

  Scenario: You should not be able to hack the uri to get the price of a calculated bundle if any of its components do not have a price
    When I hack a URI to attempt to GET the price for SmartPhones Bundle
    Then the HTTP status is conflict