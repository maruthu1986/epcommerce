@smoketest @catalogManagement @globalAttribute
Feature: Global Attribute

  Background:
    Given I sign in to CM as admin user

  Scenario: Create Edit Delete global attribute
    When I go to Catalog Management
    And I create a new global attribute with name Prod Desc for Product of type Short Text with required true
    Then newly created global attribute is in the list
    When I edit newly created global attribute name to testChanged
    Then newly edited global attribute is in the list
    When I select newly edited global attribute in the list
    And I delete newly created global attribute
    Then I verify newly created global attribute is deleted