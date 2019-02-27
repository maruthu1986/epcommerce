@Promotions @Coupons @HeaderAuth
Feature: Retrieve promotions for coupon on purchase
  As a client developer
  I want to retrieve promotions that my coupon has applied to the purchase
  so that I could display the information to the shopper

  Scenario Outline: Coupon with active promotion is applied to purchase
    Given I have the product <ACTIVE_PROMOTION_ITEM> with coupon <COUPON> applied to my purchase
    When I retrieve the coupon <COUPON> details of my purchase
    Then there is a list of applied promotions on the coupon details
    And the applied promotion <PROMOTION> matches the coupons

    Examples:
      | COUPON                                                   | ACTIVE_PROMOTION_ITEM              | PROMOTION                                                 |
      | CouponWillApply10PercentOffTheProductWith10PercentCoupon | Product With 10 Percent Off Coupon | 10PercentOffForProductWith10PercentCouponCartItemDiscount |

  Scenario Outline: Coupon with inactive promotion is applied to purchase
    Given I have the product <INACTIVE_PROMOTION_ITEM> with coupon <COUPON> applied to my purchase
    When Shopper retrieves the coupon info of their purchase
    Then there is exactly 0 coupon applied to the order

    Examples:
      | COUPON                                                   | INACTIVE_PROMOTION_ITEM   |
      | CouponWillApply10PercentOffTheProductWith10PercentCoupon | Product With No Discounts |