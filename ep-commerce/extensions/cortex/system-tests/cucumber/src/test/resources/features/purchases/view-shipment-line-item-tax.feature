@Shipments @Purchases @HeaderAuth
Feature: View purchase shipment lineitem tax
  As a Customer
  I want to see the tax I paid for a line item in my shipment
  so that I can understand the price structure for the shipment

  Background:
    Given I login as a registered shopper

  Scenario: Shipment line item contains tax - gst and pst for all items
    When I add item with code portable_tv_hdbuy_sku to my cart
    And I add item with code bundle_with_physical_and_multisku_items_bundle_sku to my cart
    And I select shipping option Canada Post Express
    And I make a purchase
    When I go to the purchases
    And I view the shipment line item for item "Portable TV"
    And I follow the shipment tax link
    Then I see the total field has amount: 18.12, currency: CAD and display: $18.12
    And I see the tax type is PST currency is CAD cost is $10.57
    And I see the tax type is GST currency is CAD cost is $7.55
    When I go to the purchases
    And I view the shipment line item for item "Samsung Headset"
    And I follow the shipment tax link
    Then I see the total field has amount: 12, currency: CAD and display: $12.00
    And I see the tax type is PST currency is CAD cost is $7.00
    And I see the tax type is GST currency is CAD cost is $5.00
    When I go to the purchases
    And I view the shipment line item for item "Samsung Galaxy Q"
    And I follow the shipment tax link
    Then I see the total field has amount: 0, currency: CAD and display: $0.00
    And I see the cost field has amount: 0, currency: CAD and display: $0.00
