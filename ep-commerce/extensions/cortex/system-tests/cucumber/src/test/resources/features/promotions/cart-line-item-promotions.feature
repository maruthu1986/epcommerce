@Promotions
Feature: Cart Line Item Promotions
  Promotion related acceptance tests for cart line item promotions

  Background:
    Given I login as a public shopper

  Scenario Outline: Cart Line Item promotions appears when triggered
    When I add item <ITEM_NAME> to the cart
    Then there is a list of applied promotions on the cart line item
    And the list of applied promotions contains promotion <PROMOTION>

    Examples:
      | ITEM_NAME                        | PROMOTION                                |
      | Product With Cart Lineitem Promo | 10PercentOffProductWithCartLineitemPromo |

  Scenario Outline: Cart Line Item applied promotions appear when applied action is a free sku
    When I add item <ITEM_NAME> to the cart
    Then there is a list of applied promotions on the cart line item
    And the list of applied promotions is empty
    And I zoom <ZOOM> to the line item <LINE_ITEM> that contains the promotion <PROMOTION>

    Examples:
      | ITEM_NAME                     | ZOOM                                                                          | LINE_ITEM                    | PROMOTION                                      |
      | productTriggeringFreeSkuPromo | lineitems:element:item:definition,lineitems:element:appliedpromotions:element | productReceivingFreeSkuPromo | FreeSkuWhenYouBuyProductTriggeringFreeSkuPromo |

  Scenario Outline: No Cart Line Item promotions appears when nothing triggers them
    When I add item <ITEM_NAME> to the cart
    Then there is a list of applied promotions on the cart line item
    And the list of applied promotions is empty

    Examples:
      | ITEM_NAME                                 |
      | triggerprodforfiftyoffentirepurchasepromo |

  Scenario Outline: Cart Line Item promotion is reflected in price and total when triggered by coupon
    When I apply a coupon code <COUPON> to the order
    And I add item <ITEM_NAME> to the cart
    And I zoom <ZOOM> into the cart lineitem price and total
    Then I see the lineitiem list-price display is $10.00
    And I see the lineitem purchase-price display is $9.00
    And I see the lineitem total cost display is $9.00

    Examples:
      | COUPON                                                   | ITEM_NAME                          | ZOOM                                            |
      | CouponWillApply10PercentOffTheProductWith10PercentCoupon | Product With 10 Percent Off Coupon | lineitems:element:price,lineitems:element:total |