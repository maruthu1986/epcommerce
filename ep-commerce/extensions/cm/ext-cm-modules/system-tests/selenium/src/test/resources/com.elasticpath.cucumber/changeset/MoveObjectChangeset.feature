@changeset @cspromotion
Feature: Move Object between Changesets

  Background:
    Given I sign in to CM as admin user
    And I go to Change Set

  @lockAndFinalize @lockAndFinalizeSecond
  Scenario: Move object from changeset 1 to changeset 2
    Given I create a new change set TestChangeSet1
    And I select newly created change set
    And I go to Catalog Management
    When I add product Alien to the change set
    Then I should see product Alien in the change set
    When I go to Change Set
    And I create a second change set TestChangeSet2
    And I select the first change set's editor
    And I select and move Alien object to the second changeset
    Then the second changeSet should contain object Alien


