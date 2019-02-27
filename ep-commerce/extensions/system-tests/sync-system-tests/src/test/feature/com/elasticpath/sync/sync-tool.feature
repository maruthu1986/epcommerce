Feature: Smoke test
  In order to catch critical problems
  As a developer
  I want a test that realistically exercises sync tool

  Scenario: Sync tool works as advertised
    Given a source system
    And a target system
    And a change set on the source system
    When the sync tool transfers the change set
    Then the changes are found on the target system