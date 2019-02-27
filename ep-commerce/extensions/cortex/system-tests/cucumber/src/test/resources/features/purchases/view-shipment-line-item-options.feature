@Shipments @Purchases @HeaderAuth
Feature: View purchase shipment lineitems with options
  As an Customer
  I want to see sku options on my shipment line items in each shipment
  so that I can see how the items I purchased items will be delivered to me.

  Background:
    Given I login as a registered shopper

  Scenario: Purchase of physical item with multiple options contains shipment line item with multiple options
    When I have previously made a purchase with "1" physical item "Portable TV"
    And I view the shipment line item for item "Portable TV"
    Then I can follow the link to options for that shipment line item
    And I can view the "Purchase Type" option for that shipment line item
    And I can view the "Rent" value for that option
    And I can follow the back link to the shipment line item option
    And I can follow the back link to the shipment line item options list
    And I can view the "Video Quality" option for that shipment line item
    And I can view the "High Definition" value for that option

  Scenario: Purchase of physical item with no options contains shipment line item with no options
    When I have previously made a purchase with "1" physical item "Sony Ericsson Xperia Pro"
    And I view the shipment line item for item "Sony Ericsson Xperia Pro"
    Then I do not see a link to options on a single sku item

  Scenario: Purchase contains multiple shipment line items: one contains options the other does not
    When I have previously made a purchase with item "Sony Ericsson Xperia Pro" quantity "2" and item "Portable TV" quantity "1"
    And I view the shipment line item for item "Portable TV"
    Then I can follow the link to options for that shipment line item
    And I can view the "Purchase Type" option for that shipment line item
    And I can view the "Rent" value for that option
    And I can follow back links from a shipment option value all the way to the purchase
    And I view the shipment line item for item "Sony Ericsson Xperia Pro"
    And I do not see a link to options on a single sku item


