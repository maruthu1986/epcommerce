@ResouceIdentifier
Feature: requests to the system are handled properly
  As a client developer,
  I want to be able to distinguish between requests not found and bad requests
  so that I can take the appropriate action to resolve the issue.

  Scenario: Response found for the given id
    Given there is a list of supported countries for scope mobee
    When I request region BC
    Then the HTTP status is OK

  Scenario: No response found for the given id
    Given there is a list of supported countries for scope mobee
    When I request region RE
    Then the HTTP status is not found

  Scenario: Then given id is not formed properly
    Given there is a list of supported countries for scope mobee
    When I request a region with an undecodable id
    Then the HTTP status is bad request
