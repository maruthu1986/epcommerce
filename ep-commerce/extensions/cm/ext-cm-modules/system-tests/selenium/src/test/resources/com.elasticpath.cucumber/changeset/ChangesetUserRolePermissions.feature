@changeset
Feature: Changeset User Role Permissions

  Scenario: Non changeset permission users do not have access to changesets
    Given the user csruser does not have changeset permission
    When I sign in to CM as csruser with password 111111
    Then I should not have access to changesets

  Scenario: Changeset permission users have access to changesets
    Given the user admin does have changeset permission
    When I sign in to CM as admin user
    Then I have access to Change Set