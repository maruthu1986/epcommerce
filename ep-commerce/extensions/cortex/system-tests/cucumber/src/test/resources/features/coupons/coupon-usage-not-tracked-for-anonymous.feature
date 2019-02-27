@Coupons
Feature: Coupons usage is not tracked against anonymous shoppers

  Background:
    Given I have no coupons applied to an order

  Scenario Outline: A coupon used by one anonymous shopper is not auto-applied to another
    Given I am logged in as a public shopper
    And I apply a coupon code <COUPON> that has not been previously applied to the order
    When I retrieve the coupon info for the order
    Then there is exactly 1 coupon applied to the order
    When I relogin as a new public shopper
    And I retrieve the coupon info for the order
    Then there is exactly 0 coupon applied to the order

    Examples:
      | COUPON                                      |
      | couponCodeLimitedByUserFor25PercentDiscount |
