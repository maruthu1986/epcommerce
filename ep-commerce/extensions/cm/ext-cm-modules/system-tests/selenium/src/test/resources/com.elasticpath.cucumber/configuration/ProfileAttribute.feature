@smoketest @configuration @profileattribute
Feature: Profile Attribute

  Background:
    Given I sign in to CM as admin user
    When I go to Configuration
    And I go to Profile Attributes

  Scenario: Add, edit and delete a profile attribute
    When I create profile attribute with following values
      | attribute name | Test    |
      | type           | Integer |
    Then the new profile attribute name should exist in the list
    When I edit newly created profile attribute name to testChanged
    Then the new profile attribute name should exist in the list
    When I delete newly created profile attribute
    Then newly created profile attribute no longer exists

