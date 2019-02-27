@smoketest @catalogManagement @bundle
Feature: Create Bundle

  Background:
    Given I sign in to CM as admin user
    And I go to Catalog Management

  Scenario: Create Edit Delete new bundle for existing category
    When I create a new bundle with following attributes
      | catalog        | category    | productName | bundlePricing | productType | brand  | storeVisible | availability     | attrShortTextMulti | attrShortTextMultiValue | attrInteger | attrIntegerValue | attrDecimal     | attrDecimalValue | bundleProductSKUList | priceList               | listPrice |
      | Mobile Catalog | Accessories | Bundle      | Assigned      | Movies      | Disney | true         | Always available | Languages          | English                 | Runtime     | 120              | Viewer's Rating | 5.5              | tt64464fn,tt0162661  | Mobile Price List (CAD) | 311.00    |
    Then the newly created bundle exists and contains the added items
    When I delete item with product code tt64464fn from the bundle
    Then the bundle item with product code tt64464fn is deleted
    When I add new Item with product code tt64464fn to the bundle
    Then the bundle item with product code tt64464fn exists in the bundle
    When I update bundle item with product code tt0162661 quantity to 3
    Then the bundle item with product code tt0162661 has quantity of 3
    When I delete the newly created bundle
    Then the bundle is deleted

  @cleanupProduct
  Scenario: Create and Edit Dynamic bundle
    When I create a new dynamic bundle with following attributes
      | catalog        | category    | productName | bundlePricing | productType | brand  | storeVisible | availability     | attrShortTextMulti | attrShortTextMultiValue | attrInteger | attrIntegerValue | attrDecimal     | attrDecimalValue | bundleProductSKUList            | bundleSelectionRule | bundleSelectionRuleValue | priceList               | listPrice |
      | Mobile Catalog | Accessories | Bundle      | Assigned      | Movies      | Disney | true         | Always available | Languages          | English                 | Runtime     | 120              | Viewer's Rating | 5.5              | tt0162661,portable_tv_hdbuy_sku | Select n            | 1                        | Mobile Price List (CAD) | 311.00    |
    Then the newly created bundle exists and contains the added items
    And the bundle selection rule is Select 1
    When I edit the selection rule to be Select n where n is 2
    Then the bundle selection rule is Select n
    And the bundle selection rule parameter is where n is 2