@smoketest @catalogManagement @catalog
Feature: Catalog Brand Management

  Background:
    Given I sign in to CM as admin user
    And I go to Catalog Management

  @cleanupCatalog
  Scenario: Update, Edit, Delete new brand for an existing catalog
    Given I create a new catalog with following details
      | catalogName   | language |
      | ATest Catalog | English  |
    And I go to Catalog Management
    And I open the newly created catalog editor
    When I add a brand testBrand
    Then the brand testBrand is displayed in the brands table

    When I edit brand name to newBrandName for the newly added brand
    Then the brand newBrandName is displayed in the brands table
    When I delete brand newBrandName
    Then The brand newBrandName is deleted

  Scenario: Unable to add duplicate brand if it already exists
    When I select catalog Mobile Catalog in the list
    And I open the selected catalog
    And I add an existing brand Disney
    Then an error message of A brand with the code you provided already exists. It cannot be added. is displayed in the add dialog

  Scenario: Unable to delete a brand already used by a product
    When I select catalog Mobile Catalog in the list
    And I open the selected catalog
    And I attempt to delete an existing brand Samsung used by product
    Then an error message of The following brand is currently in use. It cannot be removed. is displayed