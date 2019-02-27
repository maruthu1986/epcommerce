@Personalization
Feature: Test overriding traits from a client application
  I want to override traits that are defined in CE for the authenticated shopper
  so that I have the flexibility to control customer traits from the client application

  Background:
    Given I am logged in as a public shopper

  Scenario Outline: Verify the default product price for each gender
    When I authenticate as a registered shopper <USERNAME> with the default scope
    And I request the purchase price for item <MOVIE>
    Then I get the purchase price equal to <PRICE>

    Examples:
      | USERNAME                    | MOVIE               | PRICE  |
      | female.user@elasticpath.com | Transformers Female | $19.99 |
      | male.user@elasticpath.com   | Transformers Female | $29.99 |

  Scenario Outline: Verify the product price when overriding the personalization header
    When I authenticate as a registered shopper <USERNAME> with the default scope
    And I append to the overwritten personalization header the key <HEADER_KEY> and value <HEADER_VALUE>
    And I request the purchase price for item <MOVIE>
    Then I get the purchase price equal to <PRICE>

    Examples:
      | USERNAME                    | MOVIE               | PRICE  | HEADER_KEY      | HEADER_VALUE |
      | female.user@elasticpath.com | Transformers Female | $29.99 | CUSTOMER_GENDER | M            |
      | male.user@elasticpath.com   | Transformers Female | $19.99 | CUSTOMER_GENDER | F            |

  Scenario: Verify the product price when overriding the personalization header with multiple traits
    Given I am logged in as a public shopper
    And Hugo has an original purchase price equal to $34.99

    When I append to the overwritten personalization header the key GEOIP_COUNTRY_CODE and value CA
    And I append to the overwritten personalization header the key GEOIP_STATE_OR_PROVINCE and value BC
    And I request the purchase price for item Hugo
    Then I get the purchase price equal to $24.99

  Scenario Outline: Verifying purchase price of shoppers from different countries
    Given I am logged in as a public shopper

    When I append to the overwritten personalization header the key <HEADER_KEY> and value <HEADER_VALUE>
    And I request the purchase price for item <MOVIE>
    Then I get the purchase price equal to <PRICE>

    Examples:
      | MOVIE    | HEADER_KEY         | HEADER_VALUE | PRICE  |
      | Twilight | GEOIP_COUNTRY_CODE | CA           | $27.99 |
      | Twilight | GEOIP_COUNTRY_CODE | US           | $47.47 |
      | Twilight | GEOIP_COUNTRY_CODE | ''           | $47.47 |