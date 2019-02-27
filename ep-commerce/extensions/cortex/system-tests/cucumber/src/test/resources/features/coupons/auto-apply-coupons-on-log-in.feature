@Coupons
Feature: Coupons auto apply when shopper logs in

  Scenario Outline: Auto apply saved coupon on login as registered shopper
    Given shopper <AUTO_APPLIED_COUPON_CUSTOMER_ID> has an auto applied coupon <COUPON>
    When I login to the registered shopper with email ID <AUTO_APPLIED_COUPON_CUSTOMER_ID>
    And I retrieve the coupon info for the order
    Then there is exactly 1 coupon applied to the order
    And the coupon <COUPON> is applied to the order

    Examples:
      | AUTO_APPLIED_COUPON_CUSTOMER_ID            | COUPON       |
      | itest.auto.applied.coupons@elasticpath.com | AUTO_APPLIED |
