@Promotions @HeaderAuth
Feature: Retrieve Purchase Promotions
  As a client developer,
  I want to retrieve the list of promotions applied on a purchase
  so that I could display to the shopper the deals they have received with this purchase.

  Background:
    Given I login as a registered shopper

  Scenario Outline: Can retrieve promotions applied on purchase
    When a purchase is created with promotion <ACTIVE_PROMO_ITEM_NAME>
    Then there is a list of applied promotions on the purchase
    # the promotion used during the purchase
    And the list of applied promotions contains promotion <PROMOTION>

    Examples:
      | ACTIVE_PROMO_ITEM_NAME           | PROMOTION                                |
      | Product With Cart Lineitem Promo | 10PercentOffProductWithCartLineitemPromo |

  Scenario Outline:
    When a purchase is created with promotion <INACTIVE_PROMO_ITEM_NAME>
    Then there is a list of applied promotions on the purchase
    And the list of applied promotions is empty

    Examples:
      | INACTIVE_PROMO_ITEM_NAME           |
      | Product With 10 Percent Off Coupon |

# Good example...