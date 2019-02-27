@smoketest @promotionsShipping @promotion
Feature: Cart Promotion

  Background:
    Given I sign in to CM as admin user

  Scenario: Create cart promotion
    When I go to Promotions and Shipping
    And I create cart promotion with following values
      | store                  | SearchStore                     |
      | name                   | $10 off Cart Subtotal Promotion |
      | display name           | $10 off Cart Subtotal Promotion |
      | condition menu item    | Currency is []                  |
      | discount menu item     | Cart Subtotal Discount          |
      | discount sub menu item | Get $[]  off cart subtotal      |
      | discount value         | 10                              |
    Then I verify newly created cart promotion exists
    When I disable newly created cart promotion
    Then cart promotion state should be Disabled

  Scenario Outline: Create coupon cart promotion
    When I go to Promotions and Shipping
    And I create coupon cart promotion with following values
      | store                  | SearchStore                     |
      | name                   | $10 off Cart Subtotal Promotion |
      | display name           | $10 off Cart Subtotal Promotion |
      | condition menu item    | Currency is []                  |
      | discount menu item     | Cart Subtotal Discount          |
      | discount sub menu item | Get $[]  off cart subtotal      |
      | discount value         | 10                              |
      | coupon type            | <COUPON TYPE>                   |
      | coupon code            | pcode                           |
      | coupon email           | autotestuser                    |
    Then I verify newly created cart promotion exists
    When I suspend newly created coupon and disable cart promotion
    Then cart promotion state should be Disabled

    Examples:
      | COUPON TYPE |
      | public      |
      | private     |

