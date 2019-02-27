@dst
Feature: Data Sync tool

  Scenario: Sync after creating and deleting a price list
    Given I am listening to ep.changesets queue
    And I sign in to the author environment CM as admin user
    And I create and select the newly created change set DST_AddPL
    And I go to Price List Manager
    When I create a new price list with description Test Description and currency USD
    Then I should see newly created price list in the change set
    When I lock and publish latest change set
    And I read ep.changesets message from queue
    Then the json guid value should be same as change set guid
    And json eventType should contain following values
      | key  | value                        |
      | name | CHANGE_SET_READY_FOR_PUBLISH |
    When I run the data sync tool
    And I finalize the latest change set
    And I sign in to the publish environment CM as admin user
    And I go to the publish environment Price List Manager
    Then I should see the new price list in the publish environment
    When I switch to author environment
    And I create and select the newly created change set DST_DeletePL
    And I go to Price List Manager and select the newly created price list
    And I click add item to change set button
    And I delete the newly created price list
    Then I should see deleted price list in the change set
    When I lock and publish latest change set
    And I read ep.changesets message from queue
    Then the json guid value should be same as change set guid
    And json eventType should contain following values
      | key  | value                        |
      | name | CHANGE_SET_READY_FOR_PUBLISH |
    When I run the data sync tool
    And I finalize the latest change set
    And I switch to publish environment
    And I go to the publish environment Price List Manager
    Then the deleted price list no longer exists in publish environment

  Scenario: Sync after creating and deleting a product
    Given I am listening to ep.changesets queue
    And I sign in to the author environment CM as admin user
    And I create and select the newly created change set DST_Add_Prod
    And I go to Catalog Management
    When I create new product with following attributes
      | catalog        | category    | productName | productType | taxCode | brand  | storeVisible | availability     | attrShortTextMulti | attrShortTextMultiValue | attrInteger | attrIntegerValue | attrDecimal     | attrDecimalValue | shippableType | priceList               | listPrice |
      | Mobile Catalog | Accessories | Product     | Movies      | DIGITAL | Disney | true         | Always available | Languages          | English                 | Runtime     | 120              | Viewer's Rating | 5.5              | Digital Asset | Mobile Price List (CAD) | 111.00    |
    Then the newly created product is in the list
    And I should see newly created product in the change set
    When I lock and publish latest change set
    And I read ep.changesets message from queue
    Then the json guid value should be same as change set guid
    And json eventType should contain following values
      | key  | value                        |
      | name | CHANGE_SET_READY_FOR_PUBLISH |
    When I run the data sync tool
    And I finalize the latest change set
    And I sign in to the publish environment CM as admin user
    And I go to the publish environment Catalog Management
    Then I should see the new product in publish environment
    When I switch to author environment
    And I create a new change set DST_Delete_Prod
    And I go to Catalog Management
    And I select newly created change set
    When I delete the newly created product
    Then I should see deleted product in the change set
    When I lock and publish latest change set
    And I read ep.changesets message from queue
    Then the json guid value should be same as change set guid
    And json eventType should contain following values
      | key  | value                        |
      | name | CHANGE_SET_READY_FOR_PUBLISH |
    And I run the data sync tool
    And I finalize the latest change set
    And I switch to publish environment
    Then the deleted product no longer exists in publish environment

  Scenario: Sync after creating and deleting a bundle
    Given I am listening to ep.changesets queue
    And I sign in to the author environment CM as admin user
    And I create and select the newly created change set DST_Add_Buldle
    And I go to Catalog Management
    When I create a new bundle with following attributes
      | catalog        | category    | productName | bundlePricing | productType | brand  | storeVisible | availability     | attrShortTextMulti | attrShortTextMultiValue | attrInteger | attrIntegerValue | attrDecimal     | attrDecimalValue | bundleProductSKUList | priceList               | listPrice |
      | Mobile Catalog | Accessories | Bundle      | Assigned      | Movies      | Disney | true         | Always available | Languages          | English                 | Runtime     | 120              | Viewer's Rating | 5.5              | tt64464fn,tt0162661  | Mobile Price List (CAD) | 311.00    |
    And the newly created bundle exists and contains the added items
    Then I should see newly created bundle in the change set
    When I lock and publish latest change set
    And I read ep.changesets message from queue
    Then the json guid value should be same as change set guid
    And json eventType should contain following values
      | key  | value                        |
      | name | CHANGE_SET_READY_FOR_PUBLISH |
    When I run the data sync tool
    And I finalize the latest change set
    And I sign in to the publish environment CM as admin user
    And I go to the publish environment Catalog Management
    Then I should see the new bundle in publish environment
    When I switch to author environment
    And I create a new change set DST_Delete_Bundle
    And I go to Catalog Management
    And I select newly created change set
    And I delete the newly created bundle
    Then I should see deleted bundle in the change set
    When I lock and publish latest change set
    And I read ep.changesets message from queue
    Then the json guid value should be same as change set guid
    And json eventType should contain following values
      | key  | value                        |
      | name | CHANGE_SET_READY_FOR_PUBLISH |
    When I run the data sync tool
    And I finalize the latest change set
    And I switch to publish environment
    Then the deleted bundle no longer exists in publish environment
