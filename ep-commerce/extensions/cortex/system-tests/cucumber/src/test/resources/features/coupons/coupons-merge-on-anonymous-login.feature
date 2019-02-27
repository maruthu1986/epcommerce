@Coupons
Feature: Coupons merge on anonymous to registered transition

  Background:
    Given I login as a public shopper

  Scenario Outline: Shopper applies coupon as anonymous then logs in as registered
    Given I apply a coupon code <COUPON> to the order
    When I transition to registered shopper
    And I retrieve the coupon info for the order
    Then there is exactly 1 coupon applied to the order
    And the coupon <COUPON> is applied to the order

    Examples:
      | COUPON                                                   |
      | CouponWillApply10PercentOffTheProductWith10PercentCoupon |
