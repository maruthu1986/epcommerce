@Shipments @Purchases @HeaderAuth
Feature: View purchase shipment lineitems
  As an Customer
  I want to see a list of the line items in each shipment
  so that I can see how the items I purchased items will be delivered to me.

  Background:
    Given I login as a registered shopper

  Scenario: Shipment line items list contains single line item
    When I have previously made a purchase with "1" physical item "Sony Ericsson Xperia Pro"
    And I view the shipment line items
    Then I see "1" shipment line items
    And I see a back link to the shipment

  Scenario: Shipment line item representation has expected fields
    When I have previously made a purchase with "1" physical item "Sony Ericsson Xperia Pro"
    And I view the shipment line item for item "Sony Ericsson Xperia Pro"
    Then I see the quantity "1" and name "Sony Ericsson Xperia Pro" fields on the shipment line item
    And I see a back link to the list of shipment line items
    And I see a back link to the shipment

  Scenario: Shipment contains multiple physical line items and not digital line items
    When I have purchased physical and digital items: "Finding Nemo" "Sony Ericsson Xperia Pro" "Portable TV"
    And I view the shipment line items
    Then I see "2" shipment line items
    And I can follow the line item link for product "Sony Ericsson Xperia Pro" and back to the list
    And I can follow the line item link for product "Portable TV" and back to the list
    And I do not see a link to line item "Finding Nemo"

  Scenario: Shipment has mutliple line items with different quantities
    When I have previously made a purchase with item "Sony Ericsson Xperia Pro" quantity "2" and item "Portable TV" quantity "1"
    And I view the shipment line item for item "Sony Ericsson Xperia Pro"
    Then I see the quantity "2" and name "Sony Ericsson Xperia Pro" fields on the shipment line item
    And I go back to the purchase
    And I view the shipment line item for item "Portable TV"
    And I see the quantity "1" and name "Portable TV" fields on the shipment line item

  Scenario: Shipment line item contains bundle items
    When I have previously made a purchase with "1" physical item "bundle with physical and multisku items"
    And I view the shipment line items
    Then I see "3" shipment line items
    And I can follow the line item link for product "Portable TV" and back to the list
    And I can follow the line item link for product "Samsung Galaxy Q" and back to the list
    And I can follow the line item link for product "Samsung Headset" and back to the list

  Scenario: Shipment only has line items for physical part of bundle containing digital and physical components
    When I have previously made a purchase with "1" bundle item "Bundle with Physical and Digital Components"
    And I view the shipment line items
    Then I see "1" shipment line items
    And I can follow the line item link for product "Physical Item From Bundle With Physical and Digital Components" and back to the list

