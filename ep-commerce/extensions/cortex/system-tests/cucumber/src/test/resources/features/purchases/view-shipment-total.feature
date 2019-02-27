@Shipments @Purchases @HeaderAuth
Feature: View purchase shipment total
  As a Shopper
  I want to see the total for a shipment
  so that I paid in total for that shipment

  Background:
    Given I login as a registered shopper

  Scenario: View shipment total
    When I add item with code portable_tv_hdbuy_sku to my cart
    And I add item with code bundle_with_physical_and_multisku_items_bundle_sku to my cart
    And I select shipping option Canada Post Express
    And I make a purchase
    When I go to the purchases
    And I navigate to shipment
    And I follow the shipment total link
    Then I see the cost field has amount: 574.19, currency: CAD and display: $574.19
    And I can follow a link back to the shipment

