@Paymentmethods

Feature: Payment methods on profile
  As a shopper
  I want to manage my payment methods on my profile
  so that checkout can be completed faster

  Scenario: A registered shopper is able to retrieve their default payment token from their profile
    Given I authenticate as a registered shopper who has a token as their default payment method
    When I retrieve the default payment method on my profile
    Then I get the default token test-token

  Scenario: A registered shopper is able to retrieve the list of payment tokens from their profile
    Given I authenticate as a shopper with payment tokens X Y and Z and X as the default
    When I get the list of payment methods from my profile
    Then the list contains payment tokens X Y and Z and X is displayed as the default

  Scenario: A registered shopper is able to add a payment method to their profile
    Given I login as a newly registered shopper
    When I create a payment method for my profile
    Then the payment method is available from their profile

  Scenario: A registered shopper can view a new payment token directly after creation
    Given I login as a newly registered shopper
    When I create a payment method for my profile
    Then the payment method has been added to their profile

  Scenario: A registered shopper is able to delete a payment method from their profile
    Given a registered shopper has payment methods saved to his profile
    When a payment method is deleted from the profile
    Then it no longer shows up in his list of saved payment methods on his profile

  Scenario: A registered shopper can not edit  payment token after creation
    Given I login as a newly registered shopper
    When I create a payment method for my profile
    And the registered shopper attempts to edit payment method
    Then the HTTP status is forbidden

  Scenario: A shopper cannot retrieve a payment method of another shopper
    Given I login as a newly registered shopper
    And I create a payment method for my profile
    And save the payment method uri
    When I am logged in as a public shopper
    And attempt to access the other shoppers payment method
    Then the HTTP status is forbidden