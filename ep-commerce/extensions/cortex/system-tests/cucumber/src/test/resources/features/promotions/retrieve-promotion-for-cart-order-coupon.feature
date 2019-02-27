@Promotions @Coupons
Feature: Retrieve promotions from coupon on order
  As a client developer
  I want to retrieve promotions that my coupon has applied
  so that I could display the information to the shopper

  Scenario Outline: Promotion is active on cart.
    Given Shopper has coupon <COUPON> applied to their order
    And I add item <ITEM_NAME> to the cart
    When I retrieve the coupon <COUPON> details of my order
    Then there is a list of applied promotions on the coupon details
    And the applied promotion <PROMOTION> matches the coupons

    Examples:
      | ITEM_NAME                          | COUPON                                                   | PROMOTION                                                 |
      | Product With 10 Percent Off Coupon | CouponWillApply10PercentOffTheProductWith10PercentCoupon | 10PercentOffForProductWith10PercentCouponCartItemDiscount |

  Scenario Outline: Promotion is inactive on cart.
    Given Shopper has coupon <COUPON> applied to their order
    And the corresponding promotion of the coupon is inactive
    When I retrieve the coupon <COUPON> details of my order
    Then there is a list of applied promotions on the coupon details
    And the list of applied promotions is empty

    Examples:
      | COUPON                                                   |
      | CouponWillApply10PercentOffTheProductWith10PercentCoupon |