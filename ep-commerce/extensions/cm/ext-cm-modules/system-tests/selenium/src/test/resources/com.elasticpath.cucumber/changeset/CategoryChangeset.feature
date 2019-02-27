@changeset @cscatalog
Feature: Category with change set

  Background:
    Given I sign in to CM as admin user
    And I go to Change Set

  @lockAndFinalize
  Scenario Outline: Add delete category with change set
    Given I create a new change set ChangeSetCI_Add_Category
    And I go to Catalog Management
    And I select newly created change set
    And I create new category for <catalog> with following data
      | categoryName   | categoryType | storeVisible | attrLongTextName     | attrLongTextValue | attrDecimalName | attrDecimalValue | attrShortTextName | attrShortTextValue |
      | ATest Category | Movies       | true         | Category Description | <attribute-1>     | Category Rating | <attribute-2>    | Name              | <attribute-3>      |

    Then I should see newly created category in the change set
    When I lock and finalize latest change set
    And I create a new change set ChangeSetCI_Delete_Category
    And I go to Catalog Management
    And I select newly created change set
    And I expand <catalog> catalog
    And I select newly created category
    And I click add item to change set button
    And I delete newly created category
    Then I should see deleted category in the change set

    Examples:
      | catalog        | attribute-1               | attribute-2 | attribute-3        |
      | Mobile Catalog | Test Category Description | 5.50        | Test Category Name |

  @lockAndFinalize @cleanupCatalog
  Scenario: Add Delete Category Type with change set
    Given I create a new change set ChangeSetCI_Add_CategoryType
    And I go to Catalog Management
    And I select newly created change set
    When I create a new catalog with following details
      | catalogName   | language |
      | ATest Catalog | English  |
    And I open the newly created catalog editor
    And I select CategoryTypes tab in the Catalog Editor
    When I create a new category type TestCat Type with following attributes
      | Category Description |
      | Name                 |
    And newly created category type is in the list
    Then I should see newly created category type in the change set
    When I lock and finalize latest change set
    And I create a new change set ChangeSetCI_Delete_CategoryType
    And I go to Catalog Management
    And I select newly created change set
    And I open the newly created catalog editor
    And I delete the newly created category type
    Then I should see deleted category type in the change set

  @lockAndFinalize
  Scenario Outline: Add delete linked category with change set
    Given I create a new change set ChangeSetCI_Add_Linked_Category
    And I go to Catalog Management
    And I select newly created change set
    And I add new linked category with changeset to <catalog> with following data
      | <master catalog>  |
      | <linked category> |
    Then I should see newly created linked category in the change set
    When I lock and finalize latest change set
    And I create a new change set ChangeSetCI_Delete_Linked_Category
    And I go to Catalog Management
    And I select newly created change set
    And I select newly created linkedcategory
    And I click add item to change set button
    And I delete newly created linked category
    Then I should see deleted linked category in the change set

    Examples:
      | master catalog | linked category | catalog                  |
      | Mobile Catalog | TV Series       | Rock Jam Virtual Catalog |