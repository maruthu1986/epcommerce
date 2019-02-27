@changeset @cscatalog
Feature: Catalog Brand with change set

  Background:
    Given I sign in to CM as admin user
    And I create a new catalog with following details
      | catalogName   | language |
      | ATest Catalog | English  |

  @lockAndFinalize @cleanupCatalog
  Scenario Outline: Add catalog brand to existing catalog with change set
    Given I create and select the newly created change set ChangeSetCI_Add_BRAND
    And I go to Catalog Management
    And I open the newly created catalog editor
    When I add a brand <BRAND_NAME>
    Then the brand <BRAND_NAME> is displayed in the brands table
    And I should see newly created brand in the change set

    Examples:
      | BRAND_NAME |
      | BrandA     |

  @lockAndFinalize @cleanupCatalog
  Scenario Outline: Edit catalog brand with change set
    Given I create and select the newly created change set ChangeSetCI_Edit_BRAND
    And I go to Catalog Management
    And I open the newly created catalog editor
    And I add a brand <BRAND_NAME>
    And the brand <BRAND_NAME> is displayed in the brands table
    And I click add item to change set button
    When I edit brand name to <NEW_BRAND_NAME> for the newly added brand
    Then I should see edited brand <NEW_BRAND_NAME> in the change set

    Examples:
      | BRAND_NAME | NEW_BRAND_NAME |
      | BrandA     | NewBrandA      |

  @lockAndFinalize @cleanupCatalog
  Scenario Outline: Delete catalog brand with change set
    Given I create and select the newly created change set ChangeSetCI_Delete_BRAND
    And I go to Catalog Management
    And I open the newly created catalog editor
    And I add a brand <BRAND_NAME>
    And the brand <BRAND_NAME> is displayed in the brands table
    And I click add item to change set button
    When I delete the newly create brand
    Then I should see deleted brand <BRAND_NAME> in the change set

    Examples:
      | BRAND_NAME |
      | BrandA     |