@Orders
Feature: Default shipping address is set on order

  @HeaderAuth
  Scenario: Default is set on order when I have existing default shipping address on profile
    Given I login as a registered shopper
    And Shopper gets the default shipping address
    When I retrieve the shoppers shipping address info on the order
    Then the default shipping address is automatically applied to the order

  Scenario: Default is set on order when I create default shipping address on profile
    Given I have authenticated as a newly registered shopper
    And the shoppers order does not have a shipping address applied
    When I create a default shipping address on the profile
    And I retrieve the shoppers shipping address info on the order
    Then the default shipping address is automatically applied to the order

