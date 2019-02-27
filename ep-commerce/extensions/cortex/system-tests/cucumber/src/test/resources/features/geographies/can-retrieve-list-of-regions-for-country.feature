@Geographies

Feature: Geographies - Retrieve List of Regions For a Given Supported Country

  Scenario: I can retrieve the complete list of all regions for Canada
    Given there is a list of 13 supported regions for Canada in scope mobee
    When I request the list of regions for Canada in scope mobee
    Then I get back all 13 supported regions for Canada

  Scenario: I get back an empty list of regions for a country where no regions have been configured
    Given there are no supported regions for Japan
    When I request the list of regions for Japan
    Then I get back an empty list

  Scenario Outline: List of regions for Canada is localized
    Given scope mobee supports <LANGUAGE> language
    And one of the supported regions for Canada is British Columbia
    When I request the list of sub-countries in language <LANGUAGE> in scope mobee
    Then one of the regions is <LOCALIZED_SUB_COUNTRY_NAME>

    Examples:
      | LANGUAGE | LOCALIZED_SUB_COUNTRY_NAME |
      | en       | British Columbia           |
      | fr       | Colombie Britannique       |

