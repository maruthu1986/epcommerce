@smoketest @configuration @user
Feature: User Roles

  Scenario: Verify user role exists
    Given I sign in to CM as admin user
    And I go to Configuration
    And I go to User Roles
    Then User role Super User exists

  @cleanupUserRole
  Scenario: Create user role
    Given I sign in to CM as admin user
    And I go to Configuration
    When I create a new user role with following permissions
      | key                | value                                   |
      | Catalog Management | Edit Global Attributes, Manage Catalogs |
      | Change Sets        | Manage All Change Sets                  |
    Then I should see the newly created user role in the list

  @cleanupUserRole
  Scenario: Edit user role
    Given I sign in to CM as admin user
    And I go to Configuration
    And I create a new user role with following permissions
      | key                | value                                   |
      | Catalog Management | Edit Global Attributes, Manage Catalogs |
      | Change Sets        | Manage All Change Sets                  |
    When I remove assigned permission Manage Catalogs
    Then user role should not contain the removed permission
    And user role should contain following assigned permissions
      | Edit Global Attributes |
      | Manage All Change Sets |

  @cleanupUserRole
  Scenario: Delete user role
    Given I sign in to CM as admin user
    And I go to Configuration
    And there is an existing user role with following permissions
      | key                | value                  |
      | Catalog Management | Edit Global Attributes |
    When I delete the existing user role
    Then user role is deleted
