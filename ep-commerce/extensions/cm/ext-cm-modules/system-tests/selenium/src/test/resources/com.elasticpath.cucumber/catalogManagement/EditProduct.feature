@smoketest @catalogManagement @product
Feature: Edit Product

  Background:
    Given I sign in to CM as admin user
    And I go to Catalog Management

  @cleanupProduct
  Scenario: Edit new digital product for existing category
    Given a new product with following attributes
      | catalog        | category    | productName | productType | taxCode | brand  | storeVisible | availability     | attrShortTextMulti | attrShortTextMultiValue | attrInteger | attrIntegerValue | attrDecimal     | attrDecimalValue | shippableType | priceList               | listPrice |
      | Mobile Catalog | Accessories | Product     | Movies      | DIGITAL | Disney | true         | Always available | Languages          | English                 | Runtime     | 120              | Viewer's Rating | 5.5              | Digital Asset | Mobile Price List (CAD) | 111.00    |
    And the newly created product is in the list
    When I edit newly created product name to testProductNameChanged
    Then the newly created product is in the list

  Scenario: Add Edit Remove Tier Pricing from an existing product
    Given I am viewing the Price tab of an existing product with product code tt0050083
    When I add a new price tier with List Price of 200.00 for quantity of 2
    Then the new price tier with List Price of 200.00 exists in the pricing table
    When I edit price tier List Price from 200.00 to 500.00
    Then the new price tier with List Price of 500.00 exists in the pricing table
    When I delete the price tier with List Price of 500.00
    Then the price tier with List Price of 500.00 is deleted from the pricing table

  Scenario: Clear Edit Attribute values from an existing product
    Given I am viewing the Attribute tab of an existing product with product code tt0050083
    When I clear and edit Viewer's Rating attribute value to 8.10
    Then the Viewer's Rating attribute value is updated to 8.10

  @cleanupProduct
  Scenario: Add and Delete SKU for GiftCertificate multi sku product
    Given a new product with following attributes
      | catalog        | category         | productName | productType      | taxCode | brand | storeVisible | availability     | shippableType | skuCodeList       | skuOption               |
      | Mobile Catalog | Gift Certificate | Product     | Gift Certificate | NONE    | none  | true         | Always available | Digital Asset | TWENTY_VALUE - 20 | berries_theme - Berries |
    And the newly created product is in the list
    When I add SKU FIFTY_VALUE - 50 with Digital Asset shippable type
    Then the SKU 50, Berries is in the list and sku editor
    When I delete the SKU 50, Berries
    Then the SKU 50, Berries is no longer in the list

  Scenario: Add and Remove Category from an existing product
    Given I am viewing the CategoryAssignment tab of an existing product with product code tt0050083
    When I add category Games to category assignment list
    Then product 12 Angry Men present under category Games for Mobile Catalog
    When I delete category Games from category assignment list
    Then product 12 Angry Men is not linked with Games Category

  Scenario Outline: Add Edit and Remove Merchandising Associations from all tabs
    Given I am viewing the MerchandisingAssociation tab of an existing product with product code tt0050083
    When I add product code alien to merchandising association <MERCHANDISING_TAB>
    Then the product code alien exists under merchandising association <MERCHANDISING_TAB>
    When I edit product code alien to tt966001av
    Then the product code tt966001av exists under merchandising association <MERCHANDISING_TAB>
    When I delete product code tt966001av
    Then the product code tt966001av is no longer in merchandising association <MERCHANDISING_TAB>

    Examples:
      | MERCHANDISING_TAB |
      | Cross Sell        |
      | Up Sell           |
      | Warranty          |
      | Accessory         |
      | Replacement       |