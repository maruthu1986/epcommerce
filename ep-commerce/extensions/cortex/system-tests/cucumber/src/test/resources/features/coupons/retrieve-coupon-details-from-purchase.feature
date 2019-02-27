@Coupons @HeaderAuth
Feature: Retrieve Coupon Details From Purchase

  Scenario Outline: Retrieve coupon details from a purchase
    Given I have the product <ACTIVE_PROMOTION_ITEM> with coupon <COUPON> applied to my purchase
    When I retrieve the coupon <COUPON> details of my purchase
    Then the code of the coupon <COUPON> is displayed

    Examples:
      | COUPON                                                   | ACTIVE_PROMOTION_ITEM              |
      | CouponWillApply10PercentOffTheProductWith10PercentCoupon | Product With 10 Percent Off Coupon |