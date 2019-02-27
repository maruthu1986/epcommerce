@smoketest @configuration @user @db
Feature: CM Users
#	This replaces core fit tests cmuser/ChangePasswordTest001.fit, cmuser/PasswordHistoryTest001.fit,
#  	cmuser/PasswordHistoryTest002.fit, cmuser/ResetPasswordTest001.fit, cmuser/ValidatePasswordGeneratedPasswordTest001.fit

  Scenario: Create new user
    Given I sign in to CM as admin user
    When I create a user with following values
      | User Name  | testuser   |
      | First Name | Test       |
      | Last Name  | User       |
      | User Role  | Super User |
      | Password   | password1  |
    And I sign out
    Then I can sign in as the new user with password password1

  Scenario: Change password
    Given I sign in as a new user with following values
      | User Name  | testuser  |
      | First Name | Test      |
      | Last Name  | User      |
      | Password   | password1 |
    When I change the password from password1 to password2
    And I sign out
    Then I can sign in as the new user with password password2

  Scenario: Change UserName
    Given I sign in to CM as admin user
    And I search for user with the username query_manager_all
    When I change query_manager_all user's first name to changedUserName
    Then I should see the new first name in the list for the same user
    When I change query_manager_all user's first name to query_manager_all
    Then I should see the new first name in the list for the same user

  Scenario: Disable/Enable User
    Given I sign in to CM as admin user
    And I search for user with the username query_manager_all
    When I disable user with the given name query_manager_all
    And I sign out
    And I attempt sign in with disabled user query_manager_all with password 111111
    Then I am prompted with User is Disabled message Dialog
    Given I sign in again to CM as admin with password 111111
    And I search for user with the username query_manager_all
    When I enable user with the given name query_manager_all
    And I sign out
    Then I should be able to sign in again to CM as query_manager_all with password 111111

  Scenario: Search User
    Given I sign in to CM as admin user
    Then I can search for user with the username admin

 #PB-2779
  @notready
  Scenario: Password rules
    Given I sign in as a new user with following values
      | User Name  | testuser  |
      | First Name | Test      |
      | Last Name  | User      |
      | Password   | password1 |
    When I attempt to change the password from password1 to password
    Then the password change fails with message "Password should contain both alphabetic and numeric characters"

 #PB-2779
  @notready
  Scenario: Password history - cannot change to a recent password
    Given I sign in as a new user with following values
      | User Name  | testuser  |
      | First Name | Test      |
      | Last Name  | User      |
      | Password   | password1 |
    When I attempt to change the password from password1 to password1
    Then the password change fails with message "Password should differ from your last 4 passwords"

  Scenario: Password history - old passwords can be reused afters 4 changes
    Given I sign in as a new user with following values
      | User Name  | testuser  |
      | First Name | Test      |
      | Last Name  | User      |
      | Password   | password1 |
    When I change the password from password1 to password2
    And I change the password from password2 to password3
    And I change the password from password3 to password4
    And I change the password from password4 to password5
    Then I can change the password from password5 to password1
    And I sign out
    And I can sign in as the new user with password password1

  Scenario: Passwords older than 90 days are expired and need to be reset
    Given I create a new user with following values
      | User Name  | testuser  |
      | First Name | Test      |
      | Last Name  | User      |
      | Password   | password1 |
    And the new user's password is more than 36500 days old
    When I sign in with expired password for the new user
    Then I am prompted to change my password