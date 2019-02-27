@Shipments @Purchases @HeaderAuth
Feature: View purchase shipment line item prices
  As a Customer
  I want to see the price of a line item in a shipment
  so that I can understand the price breakdown of my purchase.

  Background:
    Given I login as a registered shopper

  Scenario: View shipment line item price
    When I have previously made a purchase with "1" physical item "Sony Ericsson Xperia Pro"
    And I view the shipment line item for item "Sony Ericsson Xperia Pro"
    Then I can follow the shipment line item price link
    And the purchase-price has fields amount: 58.09, currency: CAD and display: $58.09
    And I can follow the back link to the shipment line item

  Scenario: View shipment line item price with discounted price
    When I have previously made a purchase with "1" physical item "physical product with lineitem promotion"
    And I view the shipment line item for item "physical product with lineitem promotion"
    Then I can follow the shipment line item price link
    And the purchase-price has fields amount: 5.0, currency: CAD and display: $5.00

  Scenario: Quantity does not effect shipment line item price
    When I have previously made a purchase with "2" physical item "Sony Ericsson Xperia Pro"
    And I view the shipment line item for item "Sony Ericsson Xperia Pro"
    Then I can follow the shipment line item price link
    And the purchase-price has fields amount: 58.09, currency: CAD and display: $58.09

  Scenario: View calculated bundle shipment line item prices
    When I have previously made a purchase with "1" physical item "bundle with physical and multisku items"
    And I view the shipment line item for item "Portable TV"
    Then I can follow the shipment line item price link
    And the purchase-price has fields amount: 150.99, currency: CAD and display: $150.99
    And I can follow line item back links all the way to the purchase
    And I view the shipment line item for item "Samsung Galaxy Q"
    And I can follow the shipment line item price link
    And the purchase-price has fields amount: 70.99, currency: CAD and display: $70.99
    And I can follow line item back links all the way to the purchase
    And I view the shipment line item for item "Samsung Headset"
    And I can follow the shipment line item price link
    And the purchase-price has fields amount: 100.0, currency: CAD and display: $100.00
    And I can follow line item back links all the way to the purchase
