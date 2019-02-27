@changeset @csproduct
Feature: Edit Bundle with change set

  Background:
    Given I sign in to CM as admin user
    And I go to Change Set
    And I create a new change set ChangeSetCI_Create_Bundle
    And I go to Catalog Management
    And I select newly created change set
    And I create a new bundle with following attributes
      | catalog        | category    | productName | bundlePricing | productType | brand  | storeVisible | availability     | attrShortTextMulti | attrShortTextMultiValue | attrInteger | attrIntegerValue | attrDecimal     | attrDecimalValue | bundleProductSKUList | priceList               | listPrice |
      | Mobile Catalog | Accessories | Bundle      | Assigned      | Movies      | Disney | true         | Always available | Languages          | English                 | Runtime     | 120              | Viewer's Rating | 5.5              | tt64464fn,tt0162661  | Mobile Price List (CAD) | 311.00    |
    And I lock and finalize latest change set

  @lockAndFinalize @cleanupProduct
  Scenario: Add new bundle item with change set
    Given I create a new change set ChangeSetCI_Edit_Bundle
    And I go to Catalog Management
    And I select newly created change set
    And the newly created bundle exists and contains the added items
    And I click add item to change set button
    When I add new Item with product code alien to the bundle
    And I save bundle changes
    Then the bundle item with product code alien exists in the bundle
    And I should see edited bundle in the change set

  @lockAndFinalize @cleanupProduct
  Scenario: Update bundle product item quantity with change set
    Given I create a new change set ChangeSetCI_Update_Bundle
    And I go to Catalog Management
    And I select newly created change set
    And the newly created bundle exists and contains the added items
    And I click add item to change set button
    When I update bundle item with product code tt0162661 quantity to 3
    And I save bundle changes
    Then the bundle item with product code tt0162661 has quantity of 3
    And I should see edited bundle in the change set

  @lockAndFinalize @cleanupProduct
  Scenario: Delete bundle product item with change set
    Given I create a new change set ChangeSetCI_Delete_Bundle
    And I go to Catalog Management
    And I select newly created change set
    And the newly created bundle exists and contains the added items
    Then I click add item to change set button
    When I delete item with product code tt64464fn from the bundle
    And I save bundle changes
    Then the bundle item with product code tt64464fn is deleted
    Then I should see edited bundle in the change set

  @lockAndFinalize
  Scenario: Delete bundle with change set
    Given I create a new change set ChangeSetCI_Delete_Bundle
    And I go to Catalog Management
    And I select newly created change set
    When I delete the newly created bundle
    Then the bundle is deleted
    And I should see deleted bundle in the change set
