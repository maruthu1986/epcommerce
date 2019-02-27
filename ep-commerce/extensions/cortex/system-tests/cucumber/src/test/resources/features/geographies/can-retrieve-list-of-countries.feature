@Geographies
Feature: Geographies - Retrieve List of Supported Countries

  Scenario: Retrieve a complete list of all supported countries
    Given there is a list of 39 supported countries for scope mobee
    When I request the list of countries
    Then I get back all 39 supported countries

  Scenario Outline: Retrieve the list of countries for a specific scope
    Given scope mobee supports <LANGUAGE> language
    And one of the supported countries is <COUNTRY> in scope mobee
    When I request the list of countries in language <LANGUAGE> in scope mobee
    Then one of the countries is <LOCALIZED_COUNTRY_NAME>

    Examples:
      | LANGUAGE | COUNTRY       | LOCALIZED_COUNTRY_NAME |
      | en       | United States | United States          |
      | fr       | United States | ETATS-UNIS             |