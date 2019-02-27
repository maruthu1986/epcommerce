@Searches
Feature: Navigate to searches from root

  Scenario: Can find a link to searches from the root.
    Given I am logged in as a public shopper
    When I follow the root searches link
    Then I find the searches resource
