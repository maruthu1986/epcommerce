@smoketest @shippingReceiving @inventory
Feature: Inventory

  Background:
    Given I sign in to CM as admin user

  Scenario Outline: Add/Remove stock
    When I go to Shipping/Receiving
    And I select MOBEE Warehouse warehouse
    And I add <quantity> units to the stock of sku <sku_code>
    Then on hand quantity should increase by <quantity>
    When I remove <quantity> units from the stock of sku <sku_code>
    Then on hand quantity should decrease by <quantity>

    Examples:
      | sku_code    | quantity |
      | sony_bt_sku | 10       |



