@Addresses
Feature: No default addresses for public or newly registered shopper

  Scenario: No Billing address found for public shopper
    Given I login as a public shopper
    When I get the default billing address
    Then the HTTP status is not found

  Scenario: No Billing address found for newly registered shopper
    Given I have authenticated as a newly registered shopper
    When I get the default billing address
    Then the HTTP status is not found

  Scenario: No shipping address found for public shopper
    Given I login as a public shopper
    When I get the default shipping address
    Then the HTTP status is not found

  Scenario: No shipping address found for newly registered shopper
    Given I have authenticated as a newly registered shopper
    When I get the default shipping address
    Then the HTTP status is not found

  Scenario: Can get default billing address
    Given I authenticate as a registered shopper harry.potter@elasticpath.com with the default scope
    When I get the default billing address
    Then the field address contains value 1234 Hogwarts Avenue

  Scenario: Can get default shipping address
    Given I authenticate as a registered shopper harry.potter@elasticpath.com with the default scope
    When I get the default shipping address
    Then the field address contains value 1234 Hogwarts Avenue

  Scenario: Different default billing and shipping addresses
    Given I authenticate as a registered shopper testuser.different.default.addresses@elasticpath.com with the default scope
    When I get the default billing address
    Then the field address contains value 61 Clair Street
    When I get the default shipping address
    Then the field address contains value 4246 Heavner Court