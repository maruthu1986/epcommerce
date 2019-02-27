@changeset @cscatalog
Feature: New Product from new cart modifier and product type using change set

  Background:
    Given I sign in to CM as admin user
    And I go to Change Set

  @cleanupProduct @cleanUpCategory @cleanupCatalog @lockAndFinalize
  Scenario: Create new product from new cart modifier and product type using Change set
    Given I create and select the newly created change set ChangeSetCI_Add_CIMG_Product
    And I go to Catalog Management
    When I create a new catalog with following details
      | catalogName   | language | brand     |
      | ATest Catalog | English  | TestBrand |
    And I select CartItemModifierGroups tab in the Catalog Editor
    When I create a new modifier group with group code groupCode and group name groupName
    And I add a new field to this group with following details
      | fieldCode | fieldName     | fieldType  | shortTextSize |
      | textField | textFieldName | Short Text | 15            |
    Then newly created group is in the list
    And I should see newly created group in the change set
    When I select ProductTypes tab in the Catalog Editor
    And I create a new product type TestProductType with newly created cart item modifier group
    Then newly created product type is in the list
    And I should see newly created product type in the change set
    When I select CategoryTypes tab in the Catalog Editor
    And I create a new category type TestCat Type with following attributes
      | Category Description |
      | Name                 |
    And I create new category of the new category type
      | categoryName   | storeVisible | attrLongTextName     | attrLongTextValue   | attrDecimalName | attrDecimalValue | attrShortTextName | attrShortTextValue   |
      | ATest Category | true         | Category Description | long text attribute | Category Rating | 1.1              | Name              | short text attribute |
    And I create new product with following attributes
      | productName | taxCode | brand  | storeVisible | availability     | shippableType |
      | TestProduct | DIGITAL | Disney | true         | Always available | Digital Asset |
    Then the newly created product is in the list
    And I should see newly created product in the change set