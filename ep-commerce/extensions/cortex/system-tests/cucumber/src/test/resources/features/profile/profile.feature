@Profile
Feature: Profile

  Background:
    Given I login as a newly registered shopper

  Scenario: Unable to access another shoppers profile
    Given I view my profile
    And save the address uri
    When I have authenticated as a newly registered shopper
    And attempt to access the first shoppers profile
    Then the HTTP status is forbidden
