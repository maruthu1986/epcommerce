@Shipments @Purchases @HeaderAuth
Feature: View purchase shipment tax
  As a Shopper
  I want to see the tax I paid for a shipment
  so that I can understand the price structure for the shipment

  Background:
    Given I login as a registered shopper

  Scenario: Shipment contains tax - gst and pst
    When I add item with code portable_tv_hdbuy_sku to my cart
    And I add item with code bundle_with_physical_and_multisku_items_bundle_sku to my cart
    And I select shipping option Canada Post Express
    And I make a purchase
    When I go to the purchases
    And I navigate to shipment
    And I follow the shipment tax link
    Then I see the total field has amount: 53.92, currency: CAD and display: $53.92
    And I see the tax type is PST currency is CAD cost is $31.45
    And I see the tax type is GST currency is CAD cost is $22.47
    And I can follow a link back to the shipment

  Scenario: Shipment has no tax in tax-free scenario
    When I have previously made a purchase with a physical item Moto X in a tax free state code is 99501
    And I navigate to shipment
    And I follow the shipment tax link
    Then I see the total field has amount: 0, currency: CAD and display: $0.00



