@changeset @csproduct
Feature: Create Edit Delete Product with change set

  Background:
    Given I sign in to CM as admin user
    And I go to Change Set

  @lockAndFinalize
  Scenario: Create new digital product for existing category with change set
    Given I create a new change set ChangeSetCI_Add_Product
    And I go to Catalog Management
    And I select newly created change set
    When I create new product with following attributes
      | catalog        | category    | productName | productType | taxCode | brand  | storeVisible | availability     | attrShortTextMulti | attrShortTextMultiValue | attrInteger | attrIntegerValue | attrDecimal     | attrDecimalValue | shippableType | priceList               | listPrice |
      | Mobile Catalog | Accessories | Product     | Movies      | DIGITAL | Disney | true         | Always available | Languages          | English                 | Runtime     | 120              | Viewer's Rating | 5.5              | Digital Asset | Mobile Price List (CAD) | 111.00    |
    Then the newly created product is in the list
    And I should see newly created product in the change set
    When I lock and finalize latest change set
    And I create a new change set ChangeSetCI_Delete_Product
    And I go to Catalog Management
    And I select newly created change set
    When I delete the newly created product
    Then the product is deleted
    And I should see deleted product in the change set

  @lockAndFinalize
  Scenario Outline: Add Edit and Remove Merchandising Associations with change set
    Given I create a new change set ChangeSetCI_Edit_Product
    And I go to Catalog Management
    And I select newly created change set
    And I am viewing the MerchandisingAssociation tab of an existing product with product code tt0050083
    And I click add item to change set button
    When I add product code alien to merchandising association <MERCHANDISING_TAB>
    Then the product code alien exists under merchandising association <MERCHANDISING_TAB>
    When I edit product code alien to tt966001av
    Then the product code tt966001av exists under merchandising association <MERCHANDISING_TAB>
    When I delete product code tt966001av
    Then the product code tt966001av is no longer in merchandising association <MERCHANDISING_TAB>
    And I should see product tt0050083 in the change set

    Examples:
      | MERCHANDISING_TAB |
      | Cross Sell        |

  @lockAndFinalize
  Scenario: Add all category items to a change set
    Given I create and select the newly created change set CS_CI_AllCatProds
    And I go to Catalog Management
    And I expand Mobile Catalog catalog
    When I add all products from the following categories to a changeset
      | Phone Plans  |
      | Smart-phonés |
    Then total number of change set objects should match the number of category items added to the change set


