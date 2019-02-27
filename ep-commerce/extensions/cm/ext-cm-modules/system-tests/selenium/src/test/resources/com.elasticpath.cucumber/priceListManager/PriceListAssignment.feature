@smoketest @priceList
Feature: Price List Assignment

  @cleanupPriceListAssignment
  Scenario: Create new Price List Assignment with newly created Price List
    Given I sign in to CM as admin user
    And I go to Price List Manager
    When I create a new price list with description test price list and currency CAD
    Then I should see the newly created price list
    When I create Price List Assignment with newly created price list for catalog ToastieCatalog
    Then I should see newly created Price List Assignment in search result

  @cleanupPriceList
  Scenario: Delete Price List Assignment
    Given I sign in to CM as admin user
    And I go to Price List Manager
    And I have a Price List Assignment for catalog Mobile Catalog
    When I delete newly created price list assignment
    Then the deleted price list assignment no longer exists

  Scenario: Search Price List Assignment by Catalog
    Given I sign in to CM as admin user
    And I go to Price List Manager
    When I select Price List Assignments tab
    And I search Price List Assignments for catalog ToastieCatalog
    Then Search result should contain following Price List Assignments
      | Default Pricing for Toastie |
      | Europe Pricing for Toastie  |

  @cleanupPriceListAssignment
  Scenario: Price List with a Price List Assignment can't be deleted
    Given I sign in to CM as admin user
    And I go to Price List Manager
    When I create a new price list with description test price list and currency CAD
    And I should see the newly created price list
    And I create Price List Assignment with newly created price list for catalog ToastieCatalog
    And I should see newly created Price List Assignment in search result
    When I select Price List tab
    And I delete the newly created price list
    Then I should see the following error: Delete a price list is prohibited

  @cleanupPriceListAssignment
  Scenario: Edit Price List Assignment
    Given I sign in to CM as admin user
    And I go to Price List Manager
    When I create a new price list with description test price list and currency CAD
    And I create Price List Assignment with newly created price list for catalog ToastieCatalog
    When I edit the pricelist assignment description to "edit test"
    And I open the pricelist assignment
    Then the pricelist assignment description is "edit test"

  Scenario: Verify visible stores in PLA wizard with user that has access to all stores
    Given I sign in to CM as admin user
    And I go to Price List Manager
    And I have a Price List Assignment for catalog Tokenee Catalog
    When I open the pricelist assignment
    And I skip to "Stores" selection
    Then Available Stores should contain all Stores

  Scenario: Verify visible stores in PLA wizard with user that has access to subset of stores
    Given I sign in to CM as tokenee_product_pricing_manager with password 111111
    And I go to Price List Manager
    And I have a Price List Assignment for catalog Tokenee Catalog
    When I open the pricelist assignment
    And I skip to "Stores" selection
    Then Available Stores should contain the following Stores
      | Tokenee |
