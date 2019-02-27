@smoketest @catalogManagement @catalog
Feature: Manage Virtual Catalog

  Background:
    Given I sign in to CM as admin user

  Scenario: Create Edit and Delete new virtual catalog
    When I create a new virtual catalog with following details
      | catalogName           | language |
      | ATest Virtual Catalog | English  |
    Then newly created virtual catalog is in the list
    When I edit the virtual catalog name to a different name
    Then newly edited virtual catalog is in the list
    When I delete newly created virtual catalog
    Then newly created virtual catalog is deleted

  @resetVirtualCatalogProduct
  Scenario: Exclude and Include product in Virtual Catalog
    Given there is an existing product 12 Angry Men in category Movies in virtual catalog Mobile Virtual Catalog
    When I exclude the product from the virtual catalog
    Then the product does not have Mobile Virtual Catalog in category assignment and merchandising associations
    When I include the product in the virtual catalog
    Then the product has Mobile Virtual Catalog in category assignment and merchandising associations

  Scenario Outline: I delete existing virtual catalog Rock Jam Virtual Catalog
    When I delete selected catalog Rock Jam Virtual Catalog in the list
    Then I should see the following error: <ERROR_MESSAGE>

    Examples:
      | ERROR_MESSAGE                     |
      | The catalog is in use by a store. |