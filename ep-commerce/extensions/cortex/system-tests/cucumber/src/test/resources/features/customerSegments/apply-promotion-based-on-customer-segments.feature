@CustomerSegments @Promotions
Feature:Promotions can be applied based on customer segments

  Background:
    Given a customer segment promotion exists that gives $1 off

  Scenario Outline: Promotion is applied when customer segment condition is matched
    When I login to the registered shopper with email ID <CUSTOMER_SEGMENT_ID>
    And I add item <ITEM_WITH_CUSTOMER_SEGMENT_PROMOTION> to the cart
    Then the customer segment promotion discount is $1.00

    Examples:
      | CUSTOMER_SEGMENT_ID          | ITEM_WITH_CUSTOMER_SEGMENT_PROMOTION |
      | itestsegment@elasticpath.com | productUsedInCustomerSegmentsItests  |

  @HeaderAuth
  Scenario: Promotion is not applied when customer segment condition does not match
    When I login as a registered shopper
    And I add item productUsedInCustomerSegmentsItests to the cart
    Then the customer segment promotion discount is $0.00

  @HeaderAuth
  Scenario Outline: Promotion is applied when header override includes customer segment trait
    Given I login as a registered shopper
    When I am a member of customer segment <SEGMENT>
    And I add item <ITEM_WITH_CUSTOMER_SEGMENT_PROMOTION> to the cart
    Then the customer segment promotion discount is $1.00

    Examples:
      | SEGMENT       | ITEM_WITH_CUSTOMER_SEGMENT_PROMOTION |
      | ITEST_SEGMENT | productUsedInCustomerSegmentsItests  |