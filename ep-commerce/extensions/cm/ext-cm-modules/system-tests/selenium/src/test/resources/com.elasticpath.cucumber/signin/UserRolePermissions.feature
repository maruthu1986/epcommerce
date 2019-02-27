@smoketest @signin @permission
Feature: User Role Permissions

  Scenario: admin should have access to activities
    Given I sign in to CM as admin with password 111111
    Then I have access to Catalog Management
    And I can view Create Catalog button
    And I have access to Price List Manager
    And I can view Create Price List button
    And I have access to Promotions and Shipping
    And I can view Create Catalog Promotion button
    And I have access to Customer Service
    And I can view Customer Import Jobs button
    And I have access to Shipping/Receiving
    And I can view Complete Shipment button
    And I have access to Configuration
    And I can view User Roles link

  Scenario: cmuser should not have access to activities
    Given I sign in to CM as cmuser with password 111111
    Then I should not have access to Catalog Management
    And I should not have access to Price List Manager
    And I should not have access to Configuration
    And I should not have access to Promotions and Shipping
    And I should not have access to Shipping Receiving
    And I should not have access to Customer Service

  Scenario: store_marketer_all user have access to Promotions and Shipping
    Given I sign in to CM as store_marketer_all with password 111111
    Then I have access to Promotions and Shipping
    And I can view Create Catalog Promotion button
    And I should not have access to Catalog Management
    And I should not have access to Price List Manager
    And I should not have access to Configuration
    And I should not have access to Shipping Receiving
    And I should not have access to Customer Service
