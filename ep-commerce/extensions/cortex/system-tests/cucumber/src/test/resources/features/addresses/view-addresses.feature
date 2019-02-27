@Addresses
Feature: Retrieve addresses from profile

  Scenario: Customer with no addresses has no element links
    Given I have authenticated as a newly registered shopper
    When I view my profile
    And I follow links addresses
    Then there are no element links

  Scenario: Customer with no addresses has no billing addresses
    Given I have authenticated as a newly registered shopper
    When I view my profile
    And I follow links addresses -> billingaddresses
    Then there are no element links

  Scenario: Customer with no addresses has no shipping addresses
    Given I have authenticated as a newly registered shopper
    When I view my profile
    And I follow links addresses -> shippingaddresses
    Then there are no element links

  Scenario: Can retrieve list of addresses
    Given I authenticate as a registered shopper harry.potter@elasticpath.com with the default scope
    When I view my profile
    And I follow links addresses
    Then there are 2 links of rel element
    And there is an element with field address containing 1234 Hogwarts Avenue
    And there is an element with field address containing 4567 BumbleBee Dr

  Scenario: Can retrieve list of billing addresses
    Given I authenticate as a registered shopper harry.potter@elasticpath.com with the default scope
    When I view my profile
    And I follow links addresses -> billingaddresses
    Then there are 2 links of rel element
    And there is an element with field address containing 1234 Hogwarts Avenue
    And there is an element with field address containing 4567 BumbleBee Dr

  Scenario: Can retrieve list of shipping addresses
    Given I authenticate as a registered shopper harry.potter@elasticpath.com with the default scope
    When I view my profile
    And I follow links addresses -> shippingaddresses
    Then there are 2 links of rel element
    And there is an element with field address containing 1234 Hogwarts Avenue
    And there is an element with field address containing 4567 BumbleBee Dr