@changeset @csproduct
Feature: Edit Change Set

  Background:
    Given I sign in to CM as admin user

  Scenario: Update Change set name
    And I go to Change Set
    And I create a new change set NewTestChangeSet
    And I change the changeset name to EditedChangeSetName
    Then the changeset should have the edited name

  @lockAndFinalize
  Scenario: Lock and unlock object in changeset
    Given I create and select the newly created change set TestLockChangeSet
    And I go to Catalog Management
    And I search and open an existing product with name Alien
    When I click add item to change set button
    Then I should see product Alien in the change set
    When I lock the latest change set
    Then I should not be able to edit alien object's name to AlienEdited
    When I unlock the latest change set
    And  I select latest change set
    Then I should be able to edit alien object's name to AlienEdited