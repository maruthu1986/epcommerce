@smoketest @catalogManagement @product
Feature: Create New Product

  Background:
    Given I sign in to CM as admin user
    And I go to Catalog Management

  Scenario: Create and Delete new digital product for existing category
    When I create new product with following attributes
      | catalog        | category    | productName | productType | taxCode | brand  | storeVisible | availability     | attrShortTextMulti | attrShortTextMultiValue | attrInteger | attrIntegerValue | attrDecimal     | attrDecimalValue | shippableType | priceList               | listPrice |
      | Mobile Catalog | Accessories | Product     | Movies      | DIGITAL | Disney | true         | Always available | Languages          | English                 | Runtime     | 120              | Viewer's Rating | 5.5              | Digital Asset | Mobile Price List (CAD) | 111.00    |
    Then the newly created product is in the list
    When I delete the newly created product
    Then the product is deleted

  Scenario: Create and Delete new Shippable product for existing category
    When I create new product with following attributes
      | catalog        | category    | productName | productType | taxCode | brand   | storeVisible | availability     | shippableType | priceList               | listPrice |
      | Mobile Catalog | Accessories | Product     | Phones      | GOODS   | Samsung | true         | Always available | Shippable     | Mobile Price List (CAD) | 111.00    |
    Then the newly created product is in the list
    When I delete the newly created product
    Then the product is deleted

  Scenario: Create and Delete new GiftCertificate Sku product for existing category
    When I create new product with following attributes
      | catalog        | category         | productName | productType      | taxCode | brand | storeVisible | availability     | shippableType | skuCodeList                        | skuOption               |
      | Mobile Catalog | Gift Certificate | Product     | Gift Certificate | NONE    | none  | true         | Always available | Digital Asset | TWENTY_VALUE - 20,FIFTY_VALUE - 50 | berries_theme - Berries |
    Then the newly created product is in the list
    When I delete the newly created product
    Then the product is deleted

  @cleanupCatalog @cleanupProduct @cleanUpCategory
  Scenario: Create a product of new cart item modifier group
    Given a new catalog with following details
      | catalogName   | language | brand     |
      | ATest Catalog | English  | TestBrand |
    And I select CartItemModifierGroups tab in the Catalog Editor
    And I create a new modifier group with group code groupCode and group name groupName
    And I add a new field to this group with following details
      | fieldCode | fieldName     | fieldType  | shortTextSize |
      | textField | textFieldName | Short Text | 15            |
    And newly created group is in the list
    And I select ProductTypes tab in the Catalog Editor
    When I create a new product type TestProductType with newly created cart item modifier group
    Then newly created product type is in the list
    And I select CategoryTypes tab in the Catalog Editor
    And I create a new category type TestCat Type with following attributes
      | Category Description |
      | Name                 |
    When I create new category of the new category type
      | categoryName   | storeVisible | attrLongTextName     | attrLongTextValue   | attrDecimalName | attrDecimalValue | attrShortTextName | attrShortTextValue   |
      | ATest Category | true         | Category Description | long text attribute | Category Rating | 1.1              | Name              | short text attribute |
    And I create new product with following attributes
      | productName | taxCode | brand  | storeVisible | availability     | shippableType |
      | TestProduct | DIGITAL | Disney | true         | Always available | Digital Asset |
    Then the newly created product is in the list