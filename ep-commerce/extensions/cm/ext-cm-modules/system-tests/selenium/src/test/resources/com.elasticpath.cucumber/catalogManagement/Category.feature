@smoketest @catalogManagement @category
Feature: Create & Edit Category

  Background:
    Given I sign in to CM as admin user
    And I go to Catalog Management

  Scenario Outline: Create category in existing catalog
    When I create new category for <catalog> with following data
      | categoryName   | categoryType | storeVisible | attrLongTextName     | attrLongTextValue | attrDecimalName | attrDecimalValue | attrShortTextName | attrShortTextValue |
      | ATest Category | Movies       | true         | Category Description | <attribute-1>     | Category Rating | <attribute-2>    | Name              | <attribute-3>      |
    And I expand <catalog> catalog
    Then the newly created category exists
    When I select newly created category
    And I open newly created category in editor
    And I select editor's Attributes tab
    Then it should have following category attribute
      | <attribute-1> |
      | <attribute-2> |
      | <attribute-3> |
    When I delete newly created category
    Then newly created category is deleted

    Examples:
      | catalog        | attribute-1               | attribute-2 | attribute-3        |
      | Mobile Catalog | Test Category Description | 5.50        | Test Category Name |

  Scenario Outline: Add linked category to Virtual Catalog
    When I go to Catalog Management
    And I add new linked category to <catalog> with following data
      | <master catalog>  |
      | <linked category> |
    Then the linked category <linked category> should be added to catalog <catalog>

    Examples:
      | master catalog | linked category | catalog                  |
      | Mobile Catalog | TV Series       | Rock Jam Virtual Catalog |

  @cleanupCatalog @cleanUpCategory
  Scenario: Create new category with newly created category type
    Given a new catalog with following details
      | catalogName   | language |
      | ATest Catalog | English  |
    And I select CategoryTypes tab in the Catalog Editor
    And I create a new category type TestCat Type with following attributes
      | Category Description |
      | Name                 |
    When I create new category of the new category type
      | categoryName   | categoryType | storeVisible | attrLongTextName     | attrLongTextValue | attrDecimalName | attrDecimalValue | attrShortTextName | attrShortTextValue |
      | ATest Category | TestCat Type | true         | Category Description | attribute-1       | Category Rating | attribute-2      | Name              | attribute-3        |
    And I expand catalog created in this scenario
    Then the newly created category with new category type exists

  @cleanUpCategory
  Scenario Outline: Edit a category
    When I create new category for <catalog> with following data
      | categoryName   | categoryType | storeVisible | attrLongTextName     | attrLongTextValue | attrDecimalName | attrDecimalValue | attrShortTextName | attrShortTextValue |
      | ATest Category | Movies       | true         | Category Description | <attribute-1>     | Category Rating | <attribute-2>    | Name              | <attribute-3>      |
    And I expand <catalog> catalog
    Then the newly created category exists
    When I select newly created category
    And I open newly created category in editor
    And I edit the category name to <newName>
    Then the category name should display as <newName>
    When I change store visible to invisible
    Then the store visibility for the category is invisible
    When I select editor's Attributes tab
    And I edit Category Description to have a value of <newAttributeValue>
    Then I should see the new attribute value <newAttributeValue>
    When I clear Category Rating attribute value
    Then I should see the new attribute value N/A
    When I select editor's Summary tab
    And I edit the category type to <newType>
    Then category type displays as <newType>

    Examples:
      | catalog        | attribute-1               | attribute-2 | attribute-3        | newName                | newType | newAttributeValue             |
      | Mobile Catalog | Test Category Description | 5.50        | Test Category Name | new Test Category Name | Phones  | New Test Category Description |
