@CustomerSegments @Pricing
Feature:Pricing can be displayed based on customer segments

  Background:
    Given an item productUsedInCustomerSegmentsItests with purchase price of $15.00
    And a customer segment price list assignment exists that gives a purchase price of $9999.00 to item productUsedInCustomerSegmentsItests

  Scenario Outline: Customer segment pricing is displayed when customer segment condition is matched
    When I login to the registered shopper with email ID <CUSTOMER_SEGMENT_ID>
    And I add item <ITEM_WITH_CUSTOMER_SEGMENT_PRICING> to the cart
    Then the item <ITEM_WITH_CUSTOMER_SEGMENT_PRICING> list price is $9999.00 and purchase price is $9999.00

    Examples:
      | CUSTOMER_SEGMENT_ID          | ITEM_WITH_CUSTOMER_SEGMENT_PRICING  |
      | itestsegment@elasticpath.com | productUsedInCustomerSegmentsItests |

  @HeaderAuth
  Scenario: Customer segment pricing is not displayed when customer segment condition is matched
    When I login as a registered shopper
    And I add item productUsedInCustomerSegmentsItests to the cart
    Then the item productUsedInCustomerSegmentsItests list price is $18.99 and purchase price is $15.00

  @HeaderAuth
  Scenario Outline: Customer segment pricing is displayed when header override includes customer segment trait
    Given I login as a registered shopper
    When I am a member of customer segment <TRAITS_VALUE>
    And I add item <ITEM_WITH_CUSTOMER_SEGMENT_PRICING> to the cart
    Then the item <ITEM_WITH_CUSTOMER_SEGMENT_PRICING> list price is $9999.00 and purchase price is $9999.00

    Examples:
      | TRAITS_VALUE  | ITEM_WITH_CUSTOMER_SEGMENT_PRICING  |
      | ITEST_SEGMENT | productUsedInCustomerSegmentsItests |