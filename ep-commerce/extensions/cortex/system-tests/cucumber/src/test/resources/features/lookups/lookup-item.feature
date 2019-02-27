@Lookups @Items
Feature: Lookup for an Item

  Scenario Outline: Lookup an item code and verify the  item name is correct
    Given I am logged in as a public shopper
    When I look up an item with code <ITEM_CODE>
    Then I should see item name is <ITEM_NAME>

    Examples:
      | ITEM_CODE                     | ITEM_NAME                 |
      | product_with_no_discounts_sku | Product With No Discounts |

  Scenario Outline: Item lookup outside of scope should not be found
    Given I am logged into scope <SCOPE> as a public shopper
    When I look up an out of scope item <SKU_CODE>
    Then lookup fails with status not found

    Examples:
      | SCOPE | SKU_CODE       |
      | mobee | BBEP128.Dove-L |

  Scenario Outline: Lookup for an invalid item code fails
    Given I am logged in as a public shopper
    When I look up an invalid item <ITEM_CODE>
    Then lookup fails with status not found

    Examples:
      | ITEM_CODE                     |
      | invalid_code |