@Coupons
Feature: Apply Coupon to Order

  Background:
    Given I have no coupons applied to an order

  Scenario Outline: Apply a new coupon to an order
    When I apply a coupon code <COUPON> to the order
    Then the coupon <COUPON> is applied to the order

    Examples:
      | COUPON                                                   |
      | CouponWillApply10PercentOffTheProductWith10PercentCoupon |

  Scenario Outline: Identical coupons are not duplicated in the order
    Given I applied a coupon code <COUPON> to the order
    When I re-apply the same coupon code <COUPON> to the same order
    Then the coupon <COUPON> is applied to the order
    And there is exactly 1 coupon applied to the order

    Examples:
      | COUPON                                                   |
      | CouponWillApply10PercentOffTheProductWith10PercentCoupon |

  Scenario Outline: Invalid coupon is not accepted
    When I apply an invalid coupon <INVALID_COUPON> to the order
    Then the coupon is not accepted
    And I should see validation error message with message type, message id, and debug message
      | messageType | messageId        | debugMessage                           |
      | error       | coupon.not.valid | Coupon '<INVALID_COUPON>' is not valid |

    Examples:
      | INVALID_COUPON    |
      | invalidCouponCode |

  Scenario Outline: Expired coupon is not accepted
    When I apply an invalid coupon <EXPIRED_COUPON> to the order
    Then the coupon is not accepted
    And I should see validation error message with message type, message id, and debug message
      | messageType | messageId                  | debugMessage                                     |
      | error       | coupon.no.longer.available | Coupon '<EXPIRED_COUPON>' is no longer available |

    Examples:
      | EXPIRED_COUPON |
      | expiredcoupon  |

  Scenario Outline: Coupon code is not case sensitive
    Given a coupon code <COUPON> exists
    When I apply a coupon code <COUPON_IN_OTHER_CASE> to the order
    Then the coupon <COUPON> is applied to the order
    And there is exactly 1 coupon applied to the order

    Examples:
      | COUPON                                                   | COUPON_IN_OTHER_CASE                                     |
      | CouponWillApply10PercentOffTheProductWith10PercentCoupon | COUPONWILLAPPLY10PERCENTOFFTHEPRODUCTWITH10PERCENTCOUPON |
      | CouponWillApply10PercentOffTheProductWith10PercentCoupon | couponwillapply10percentofftheproductwith10percentcoupon |

