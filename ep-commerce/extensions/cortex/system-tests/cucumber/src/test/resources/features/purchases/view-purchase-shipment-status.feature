@Shipments @Purchases @HeaderAuth
Feature: View purchase shipment
  As a Shopper, I want to view my purchase and shipment status, so that I can track my order.

  Background:
    Given I login as a registered shopper
    And The store the shopper is in fulfils shipments with no delay

  Scenario Outline: View purchase and shipment status - shippable item
    When I add item <ITEM_NAME> to the cart with quantity 1
    And I select shipping option Canada Post Express
    And I make a purchase
    When I go to the purchases
    Then the purchase status is <PURCHASE_STATUS>
    And I navigate to shipment
    Then I can see the shipment status <SHIPMENT_STATUS>
    And I can follow a link back to the list of shipments
    And I can follow a link back to the purchase

    Examples:
      | ITEM_NAME                                   | PURCHASE_STATUS | SHIPMENT_STATUS    |
      | physicalProduct                             | IN_PROGRESS     | INVENTORY_ASSIGNED |
      | bundle with physical and multisku items     | IN_PROGRESS     | INVENTORY_ASSIGNED |
      | Bundle with Physical and Digital Components | IN_PROGRESS     | INVENTORY_ASSIGNED |

  Scenario Outline: View purchase status only - non shippable item
  No shipment status available for non shippable item
    When I add item <ITEM_NAME> to the cart with quantity 1
    And I make a purchase
    When I go to the purchases
    Then the purchase status is <PURCHASE_STATUS>

    # Digital Item and Recurring Item
    Examples:
      | ITEM_NAME      | PURCHASE_STATUS |
      | digitalProduct | COMPLETED       |
      | Phone Plan     | COMPLETED       |

  Scenario Outline: View purchase and shipment status - multiple items
    When I add item <ITEM_1> to the cart with quantity 1
    And I add item <ITEM_2> to the cart with quantity 1
    And I select shipping option Canada Post Express
    And I make a purchase
    When I go to the purchases
    Then the purchase status is <PURCHASE_STATUS>
    And I navigate to shipment
    Then I can see the shipment status <SHIPMENT_STATUS>
    And I can follow a link back to the list of shipments
    And I can follow a link back to the purchase

    Examples:
      | ITEM_1          | ITEM_2         | PURCHASE_STATUS | SHIPMENT_STATUS    |
      | physicalProduct | digitalProduct | IN_PROGRESS     | INVENTORY_ASSIGNED |
      | physicalProduct | Phone Plan     | IN_PROGRESS     | INVENTORY_ASSIGNED |