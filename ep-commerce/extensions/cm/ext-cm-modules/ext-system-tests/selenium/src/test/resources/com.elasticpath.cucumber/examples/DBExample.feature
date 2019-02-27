@example
Feature: DB Connector Example

  Scenario: Create New User and Disable New User
    Given I sign in to CM as admin user
    When I create a user with following values
      | User Name  | testuser   |
      | First Name | Test       |
      | Last Name  | User       |
      | User Role  | Super User |
      | Password   | password1  |
    Then the newly created user status should be 1 in the database
    And I disable newly created user
    Then the disabled user status should be 0 in the database