@changeset @cspromotion
Feature: Promotion with change set

  Background:
    Given I sign in to CM as admin user
    And I go to Change Set

  @lockAndFinalize
  Scenario: Add and disable cart promotion with change set
    Given I create a new change set ChangeSetCI_Add_CartPromo
    And I go to Promotions and Shipping
    And I select newly created change set
    When I create cart promotion with following values
      | store                  | SearchStore                     |
      | name                   | $10 off Cart Subtotal Promotion |
      | display name           | $10 off Cart Subtotal Promotion |
      | condition menu item    | Currency is []                  |
      | discount menu item     | Cart Subtotal Discount          |
      | discount sub menu item | Get $[]  off cart subtotal      |
      | discount value         | 10                              |
    Then I should see newly created cart promotion in the change set
    Given I lock and finalize latest change set
    And I create a new change set ChangeSetCI_Disable_CartPromo
    And I go to Promotions and Shipping
    And I select newly created change set
    And I verify newly created cart promotion exists
    And I select the newly created promotion
    And I click add item to change set button
    And I select the newly created promotion
    When I disable newly created cart promotion
    Then cart promotion state should be Disabled
    When I select the newly created change set's editor
    Then I should see disabled cart promotion in the change set


