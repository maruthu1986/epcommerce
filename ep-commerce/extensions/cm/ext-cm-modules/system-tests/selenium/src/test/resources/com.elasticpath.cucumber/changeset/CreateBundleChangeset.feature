@changeset @csproduct
Feature: Create Bundle with change set

  Background:
    Given I sign in to CM as admin user
    And I go to Change Set

  @lockAndFinalize @cleanupProduct
  Scenario: Create new bundle for existing category with change set
    Given I create a new change set ChangeSetCI_Create_Bundle
    And I go to Catalog Management
    And I select newly created change set
    When I create a new bundle with following attributes
      | catalog        | category    | productName | bundlePricing | productType | brand  | storeVisible | availability     | attrShortTextMulti | attrShortTextMultiValue | attrInteger | attrIntegerValue | attrDecimal     | attrDecimalValue | bundleProductSKUList | priceList               | listPrice |
      | Mobile Catalog | Accessories | Bundle      | Assigned      | Movies      | Disney | true         | Always available | Languages          | English                 | Runtime     | 120              | Viewer's Rating | 5.5              | tt64464fn,tt0162661  | Mobile Price List (CAD) | 311.00    |
    And the newly created bundle exists and contains the added items
    Then I should see newly created bundle in the change set