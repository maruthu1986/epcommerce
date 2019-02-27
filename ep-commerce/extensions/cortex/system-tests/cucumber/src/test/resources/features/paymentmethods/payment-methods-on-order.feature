@Paymentmethods
Feature: Payment methods on order
  As a shopper
  I want to manage my payment methods on my order
  so that checkout can be completed faster

  Scenario: A newly added payment method on a profile becomes chosen by default on order
    Given I authenticate as a registered shopper with a saved payment method on my profile
    When I view the payment methods available to be selected for my order
    Then I see only the payment method available from my profile

  Scenario: Shopper's default payment method is chosen on their order payment method selector
    Given I authenticate as a shopper with payment tokens X Y and Z and X as the default
    When I view the payment methods available to be selected for my order
    Then payment method X is chosen as the payment method for my purchase

  Scenario: A shopper is able to add a payment method to their order, which only exists for the life cycle
  of that order and hence should never appear on the shopper's profile.
    Given I login as a newly registered shopper
    When I create a payment method for my order
    Then the new payment method will be set for their order
    And the new payment method is not available from their profile

  Scenario: Order always has default payment method
    Given I login as a newly registered shopper
    And my order does not have a payment method applied
    When I create a payment method for my profile
    And I retrieve my order
    Then the default payment method is automatically applied to the order

  Scenario: Shopper can change the payment method for their order
    Given I authenticate as a shopper with saved payment methods X Y and Z and payment method X is chosen on their order
    When I select payment method Y on the order
    Then payment method X is now a choice as the payment method on the order
    And payment method Y is now chosen as the payment method on the order

  Scenario: Shopper cannot reselect the chosen payment method on their order
    Given I authenticate as a shopper with payment tokens X Y and Z and X as the default
    When I get the chosen payment method X
    Then there is no way to select the payment method for my order

  Scenario: Shopper can see the details for the chosen payment method on their order
    Given I authenticate as a shopper with payment method X as the chosen payment method for their order
    When I get the payment method details for the chosen payment method X
    Then the payment method details display the correct values

  Scenario: Shopper can see the payment method details for payment method choices on their order
    Given I authenticate as a shopper with payment tokens X Y and Z and X as the default
    When I get the payment method details for payment method choice Y or Z
    Then the payment method details display the correct values for that choice

  Scenario: Shopper's chosen payment method is used to complete the purchase
    Given I authenticate as a shopper with saved payment methods X Y and Z and payment method X is chosen on their order
    When I complete the purchase for the order
    Then the chosen payment method is displayed correctly as the payment mean for the purchase

  Scenario: A shopper cannot retrieve a payment method of another shopper
    Given I login as a newly registered shopper
    And I create a payment method for my order
    And the new payment method will be set for their order
    And save the payment method uri
    When I am logged in as a public shopper
    And attempt to access the other shoppers payment method
    Then the HTTP status is forbidden
