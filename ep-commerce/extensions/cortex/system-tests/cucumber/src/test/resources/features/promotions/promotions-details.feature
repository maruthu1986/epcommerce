@Promotions
Feature: Promotion Details
  Promotion related acceptance tests for promotion details

  Scenario Outline: Retrieve the details of a promotion trigger
    Given I am logged in as a public shopper
    And I add item <ITEM_NAME> to the cart
    And I apply a coupon code <COUPON> to the order
    When the <PROMOTION> promotion link is followed from the coupon
    Then the following promotion details are displayed
      | name                | 10PercentOffForProductWith10PercentCouponCartItemDiscount        |
      | display-name        | 10 Percent Off For ProductWith10PercentCoupon Cart Item Discount |
      | display-description | 10 Percent Promo Description                                     |

    Examples:
      | ITEM_NAME                          | COUPON                                                   | PROMOTION                                                 |
      | Product With 10 Percent Off Coupon | CouponWillApply10PercentOffTheProductWith10PercentCoupon | 10PercentOffForProductWith10PercentCouponCartItemDiscount |