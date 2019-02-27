@Promotions
Feature: Cart Promotions
  As a shopper, when I trigger a cart promotion, then I can see the details of the promotion, so I am more likely to complete a purchase.

  Background:
    Given I login as a public shopper

  Scenario Outline: Cart promotions appears when triggered
    When I add item <ITEM_NAME> to the cart
    And I go to my default cart
    Then there is a list of applied promotions on the cart
    # the cart promotion
    And the list of applied promotions contains promotion <PROMOTION>

    Examples:
      | ITEM_NAME                                 | PROMOTION              |
      | triggerprodforfiftyoffentirepurchasepromo | FiftyOffEntirePurchase |

  Scenario: No Cart promotions appears when nothing triggers them
    When I go to my default cart
    Then there is a list of applied promotions on the cart
    And the list of applied promotions is empty

  Scenario Outline: Retrieve promotions applied to cart triggered by personalisation parameters
    And a personalisation header triggers a cart promotion
    When I add item <ITEM_NAME> to the cart
    And I go to my default cart
    Then there is a list of applied promotions on the cart
    And the list of applied promotions contains promotion <PROMOTION>

    Examples:
      | ITEM_NAME                                   | PROMOTION                     |
      | triggerprodforpersonalisedcartdiscountpromo | PersonalisedCartDiscountPromo |

  Scenario Outline:  Cart promotions with coupons appear when triggered
    And I add item <ITEM_NAME> to the cart
    When I apply a coupon code <COUPON> to the order
    And I retrieve the coupon <COUPON> details of my order
    Then there is a list of applied promotions on the cart
    And the list of applied promotions contains promotion <PROMOTION>

    Examples:
      | ITEM_NAME            | COUPON                                     | PROMOTION                     |
      | productwithoutpromos | couponCodeForCartSubtotalDiscount30Percent | cartSubtotalDiscount30Percent |