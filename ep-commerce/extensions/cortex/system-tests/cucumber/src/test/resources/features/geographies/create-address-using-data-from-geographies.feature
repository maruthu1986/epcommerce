@Geographies
Feature: Geographies - Retrieve Specific Country and Region Codes

  Scenario: Retrieve specific country and region codes to create an address
    Given there is a list of supported countries and regions for scope mobee
    When the country Canada and the region British Columbia is selected
    Then I can obtain the country and region code to create an address