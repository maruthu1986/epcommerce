@changeset @cscatalog
Feature: Catalog with change set

  Background:
    Given I sign in to CM as admin user
    And I go to Change Set

  @lockAndFinalize
  Scenario: Add delete virtual catalog with change set
    Given I create a new change set ChangeSetCI_Add_VC
    And I go to Catalog Management
    And I select newly created change set
    When I create a new virtual catalog with following details
      | catalogName   | language |
      | ATest Catalog | English  |
    Then I should see newly created virtual catalog in the change set
    When I lock the latest change set
    Then the change set status should be Locked
    When I finalize the latest change set
    Then the change set status should be Finalized
    When I create a new change set ChangeSetCI_Delete_VC
    And I go to Catalog Management
    And I select newly created change set
    And I select newly created virtual catalog in the list
    And I click add item to change set button
    And I select newly created virtual catalog in the list
    When I delete newly created virtual catalog
    Then I should see deleted virtual catalog in the change set

  @lockAndFinalize @cleanupCatalog
  Scenario: Add delete Cart Item Modifier Group with change set
    Given I create a new change set ChangeSetCI_Add_CartItemModifier
    And I go to Catalog Management
    And I select newly created change set
    And I create a new catalog with following details
      | catalogName   | language |
      | ATest Catalog | English  |
    When I select CartItemModifierGroups tab in the Catalog Editor
    And I create a new modifier group with group code groupCode and group name groupName
    And I add a new field to this group with following details
      | fieldCode | fieldName     | fieldType  | shortTextSize |
      | textField | textFieldName | Short Text | 15            |
    And I add a new field to this group with following details
      | fieldCode   | fieldName       | fieldType           | optionValue | optionName |
      | optionField | optionFieldName | Multi Select Option | optionCode  | optionName |
    And newly created group is in the list
    Then I should see newly created group in the change set
    When I lock and finalize latest change set
    And I create a new change set ChangeSetCI_Delete_CartItemModifier
    And I go to Catalog Management
    And I select newly created change set
    And I open the newly created catalog editor
    And I delete the newly created group
    Then I should see deleted group in the change set

  @lockAndFinalize @cleanupCatalog
  Scenario: Add Delete Product Type with change set
    Given I create a new change set ChangeSetCI_Add_ProductType
    And I go to Catalog Management
    And I select newly created change set
    When I create a new catalog with following details
      | catalogName   | language |
      | ATest Catalog | English  |
    And I open the newly created catalog editor
    And I select ProductTypes tab in the Catalog Editor
    When I create a new product type Prod Type with following attributes
      | Features |
    And newly created product type is in the list
    Then I should see newly created product type in the change set
    When I lock and finalize latest change set
    And I create a new change set ChangeSetCI_Delete_ProductType
    And I go to Catalog Management
    And I select newly created change set
    And I open the newly created catalog editor
    And I delete the newly created product type
    Then I should see deleted product type in the change set

  @lockAndFinalize @cleanupCatalog
  Scenario: Add, Delete catalog attribute with change set
    Given I create a new change set ChangeSetCI_Add_ProductType
    And I go to Catalog Management
    And I select newly created change set
    And I create a new catalog with following details
      | catalogName   | language |
      | ATest Catalog | English  |
    And I open the newly created catalog editor
    And I select Attributes tab in the Catalog Editor
    When I create a new catalog attribute with following details
      | attributeName     | attributeUsage | attributeType | attributeRequired |
      | testAttributeName | Product        | Short Text    | true              |
    Then newly created catalog attribute is in the list
    When I edit the catalog attribute name
    And edited catalog attribute is in the list
    Then I should see the edited catalog attribute in the change set
    When I lock and finalize latest change set
    And I create a new change set ChangeSetCI_Delete_ProductType
    And I go to Catalog Management
    And I select newly created change set
    And I open the newly created catalog editor
    And I delete the newly created catalog attribute
    And the newly created catalog attribute is deleted
    Then I should see the deleted catalog attribute in the change set

  @lockAndFinalize @cleanupCatalog
  Scenario: Add, Edit, and Delete Sku Options with change set enabled
    Given I create a new change set ChangeSetCI_SkuOptions
    And I go to Catalog Management
    And I select newly created change set
    And I create a new catalog with following details
      | catalogName   | language |
      | ATest Catalog | English  |
    And I open the newly created catalog editor
    And I select SkuOptions tab in the Catalog Editor
    When I create a new sku option with sku code f00bar and display name automationTesting
    Then I should see the newly created sku option in the change set
    When I go to Catalog Management
    And I open the newly created catalog editor
    And I edit the sku option name
    Then I should see the edited sku option in the change set
    When I go to Catalog Management
    And I open the newly created catalog editor
    And I delete the newly created sku option
    Then the newly created sku option is deleted
    And I should see the deleted sku option in the change set
