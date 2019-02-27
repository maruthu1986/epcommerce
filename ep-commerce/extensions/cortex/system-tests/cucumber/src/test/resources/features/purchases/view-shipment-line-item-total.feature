@Shipments @Purchases @HeaderAuth
Feature: View purchase shipment lineitems
  As an Customer
  I want to see a total cost of each shipment line item in my shipment
  so that I can understand the price breakdown of my purchase.

  Background:
    Given I login as a registered shopper

  Scenario: View shipment line item total for purchase with one shipment single quantity
    When I have previously made a purchase with "1" physical item "Sony Ericsson Xperia Pro"
    And I view the shipment line item for item "Sony Ericsson Xperia Pro"
    Then I can follow the total link
    And I see the cost field has amount: 58.09, currency: CAD and display: $58.09
    And I can follow the back link to the shipment line item

  Scenario: View shipment line item total for purchase with one shipment multiple quantity
    When I have previously made a purchase with "2" physical item "Sony Ericsson Xperia Pro"
    And I view the shipment line item for item "Sony Ericsson Xperia Pro"
    Then I can follow the total link
    And I see the cost field has amount: 116.18, currency: CAD and display: $116.18

  Scenario: View shipment line item total for purchase with multiple shipments
    When I have previously made a purchase with item "Sony Ericsson Xperia Pro" quantity "1" and item "Portable TV" quantity "1"
    And I view the shipment line item for item "Sony Ericsson Xperia Pro"
    Then I can follow the total link
    And I see the cost field has amount: 58.09, currency: CAD and display: $58.09
    And I can follow line item back links all the way to the purchase
    And I view the shipment line item for item "Portable TV"
    And I can follow the total link
    And I see the cost field has amount: 169.11, currency: CAD and display: $169.11

  Scenario: View shipment line item total for a bundle
    When I have previously made a purchase with "1" physical item "bundle with physical and multisku items"
    And I view the shipment line item for item "Portable TV"
    Then I can follow the total link
    And I see the cost field has amount: 169.11, currency: CAD and display: $169.11
    And I can follow line item back links all the way to the purchase
    And I view the shipment line item for item "Samsung Galaxy Q"
    And I can follow the total link
    And I see the cost field has amount: 70.99, currency: CAD and display: $70.99
    And I can follow line item back links all the way to the purchase
    And I view the shipment line item for item "Samsung Headset"
    And I can follow the total link
    And I see the cost field has amount: 112.0, currency: CAD and display: $112.00
