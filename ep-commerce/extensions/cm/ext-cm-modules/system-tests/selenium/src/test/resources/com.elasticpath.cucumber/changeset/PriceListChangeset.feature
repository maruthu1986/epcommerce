@changeset @cspricelist
Feature: Price List with change set

  Background:
    Given I sign in to CM as admin user

  @lockAndFinalize
  Scenario:  Add, Edit and delete price list with change set
    Given I create and select the newly created change set ChangeSetCI_Add_PL
    And I go to Price List Manager
    When I create a new price list with description Test Description and currency USD
    Then I should see newly created price list in the change set
    Given I lock and finalize latest change set
    And I create and select the newly created change set ChangeSetCI_Edit_PL
    And I go to Price List Manager and select the newly created price list
    And I click add item to change set button
    And I open newly created price list in editor
    When I edit the price list description
    Then I should see edited price list in the change set
    Given I lock and finalize latest change set
    And I create and select the newly created change set ChangeSetCI_Delete_PL
    And I go to Price List Manager and select the newly created price list
    And I click add item to change set button
    When I delete the newly created price list
    Then I should see deleted price list in the change set

  @lockAndFinalize
  Scenario Outline: Add, edit and delete Price List Assignment with change set
    Given I create and select the newly created change set ChangeSetCI_Add_PLA
    And I go to Price List Manager
    When I create Price List Assignment with existing price list <price-list> for catalog Mobile Virtual Catalog
    Then I should see newly created price list assignment in the change set
    Given I lock and finalize latest change set
    And I create and select the newly created change set ChangeSetCI_Edit_PLA
    And I go to Price List Manager
    And I select the newly created price list assignment for price list <price-list>
    And I click add item to change set button
    When I edit the newly created price list assignment for price list <price-list> description to "New Description"
    Then I should see edited price list assignment in the change set
    Given I lock and finalize latest change set
    And I create and select the newly created change set ChangeSetCI_Delete_PLA
    And I go to Price List Manager
    And I select the newly created price list assignment for price list <price-list>
    And I click add item to change set button
    When I delete newly created price list assignment for price list <price-list>
    Then I should see deleted price list assignment in the change set

    Examples:
      | price-list        |
      | Mobile Price List |

  @lockAndFinalize
  Scenario Outline: Add and delete price with change set
    Given I create and select the newly created change set ChangeSetCI_Add_Price
    And I go to Price List Manager
    And I create a new price list with description Test Description and currency USD
    And I open newly created price list in editor
    When I add a list price 24.99 for product <product-name>
    Then I should see newly created price for Finding Nemo @ 1 in the change set
    Given I lock, finalize and close latest change set editor
    And I create and select the newly created change set ChangeSetCI_Delete_Price
    And I go to Price List Manager
    And I open newly created price list in editor
    And I click add item to change set button
    When I delete price for product name <product-name>
    And I delete the newly created price list
    Then I should see deleted price for Finding Nemo @ 1.00 in the change set

    Examples:
      | product-name |
      | Finding Nemo |