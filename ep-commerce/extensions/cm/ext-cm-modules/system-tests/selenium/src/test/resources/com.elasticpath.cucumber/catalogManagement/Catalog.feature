@smoketest @catalogManagement @catalog
Feature: Create Catalog

  Background:
    Given I sign in to CM as admin user
    When I go to Catalog Management
    And I create a new catalog with following details
      | catalogName   | language |
      | ATest Catalog | English  |

  Scenario: Create new catalog
    Given newly created catalog is in the list
    When I delete newly created catalog
    Then newly created catalog is deleted

  @cleanupCatalog
  Scenario Outline: Add / Edit / Delete catalog attribute
    And I select Attributes tab in the Catalog Editor
    When I create a new catalog attribute with following details
      | attributeName     | attributeUsage | attributeType | attributeRequired |
      | testAttributeName | Product        | Short Text    | true              |
    Then newly created catalog attribute is in the list
    When I edit the catalog attribute name
    Then edited catalog attribute is in the list
    When I close the editor and try to delete the newly created catalog
    Then I should see following error messages
      | <error-message-1> |
      | <error-message-2> |
    When I open the newly created catalog editor
    And I delete the newly created catalog attribute
    Then the newly created catalog attribute is deleted

    Examples:
      | error-message-1                        | error-message-2       |
      | Unable to delete the following catalog | The catalog is in use |

  @cleanupCatalog
  Scenario Outline: Add / Edit / Delete category type
    And I select CategoryTypes tab in the Catalog Editor
    When I create a new category type Cat Type with following attributes
      | Category Description |
      | Name                 |
    Then newly created category type is in the list
    When I edit the category type name
    Then updated category type is in the list
    When I close the editor and try to delete the newly created catalog
    Then I should see following error messages
      | <error-message-1> |
      | <error-message-2> |
    When I open the newly created catalog editor
    And I delete the newly created category type
    Then the newly created category type is deleted

    Examples:
      | error-message-1                        | error-message-2       |
      | Unable to delete the following catalog | The catalog is in use |

  @cleanupCatalog
  Scenario Outline: Add / Edit / Delete product type
    And I select ProductTypes tab in the Catalog Editor
    When I create a new product type Prod Type with following attributes
      | Features |
    Then newly created product type is in the list
    When I edit the product type name
    Then updated product type is in the list
    When I close the editor and try to delete the newly created catalog
    Then I should see following error messages
      | <error-message-1> |
      | <error-message-2> |
    When I open the newly created catalog editor
    And I delete the newly created product type
    Then the newly created product type is deleted

    Examples:
      | error-message-1                        | error-message-2       |
      | Unable to delete the following catalog | The catalog is in use |

  @cleanupCatalog
  Scenario: Add / Edit / Delete Sku Options
    And I select SkuOptions tab in the Catalog Editor
    When I create a new sku option with sku code skuCode and display name testName
    Then newly created sku option is in the list
    When I create a new sku option value with sku code skuValueCode and display name valueTestName
    Then newly created sku option value is in the list
    When I edit the sku option name
    Then updated sku option name is in the list
    When I delete the newly created sku option
    Then the newly created sku option is deleted

  @cleanupCatalog
  Scenario: Add / Edit / Delete cart item modifier group
    And I select CartItemModifierGroups tab in the Catalog Editor
    When I create a new modifier group with group code groupCode and group name groupName
    And I add a new field to this group with following details
      | fieldCode | fieldName     | fieldType  | shortTextSize |
      | textField | textFieldName | Short Text | 15            |
    And I add a new field to this group with following details
      | fieldCode   | fieldName       | fieldType           | optionValue | optionName |
      | optionField | optionFieldName | Multi Select Option | optionCode  | optionName |
    Then newly created group is in the list
    And this new cart item modifier group should be available as a selection when creating Product Type
    When I edit and verify the edited field name
    And I edit and verify the edited option name
    And I edit the group name
    Then updated group name is in the list
    When I delete the newly created field
    Then the field is deleted
    When I delete the newly created group
    Then the newly created group should be deleted
    And the deleted group should not appear when creating a new Product Type