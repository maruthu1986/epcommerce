@Coupons
Feature: Remove Coupon From Order

  Background:
    Given I have no coupons applied to an order

  Scenario Outline: Can remove applied coupon from order
    Given I applied a coupon code <COUPON> to the order
    When I remove the coupon <COUPON> from the order
    And I retrieve the coupon info for the order
    Then there is exactly 0 coupons applied to the order

    Examples:
      | COUPON                                                   |
      | CouponWillApply10PercentOffTheProductWith10PercentCoupon |
