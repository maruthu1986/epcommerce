@smoketest @configuration @customersegments
Feature: Customer Segments

  Background:
    Given I sign in to CM as admin user

  Scenario Outline: Create,Edit,Verify and Delete Customer Segment
    When I go to Customer Segments
    And I create a customer segment with description Test Description
    Then I should see the newly created customer segment
    When I enable newly created customer segment
    Then I should see the newly created customer segment
    When I search and open customer editor for email ID <EMAIL_ID>
    And I select Customer Segments tab in the Customer Editor
    Then the newly created Customer Segment is available in the segment list
    When I delete newly created customer segment
    Then newly created customer segment no longer exists

    Examples:
      | EMAIL_ID                     |
      | harry.potter@elasticpath.com |

  @resetCustomerSegment
  Scenario Outline: Item price depends on customer segment
    Given a customer <EMAIL_ID> who is member of segment <CUSTOMER_SEGMENT> that has item price <SEGMENT_ITEM_PRICE> for sku <SKU_CODE> in store <STORE>
    When I remove customer segment <CUSTOMER_SEGMENT> for the customer <EMAIL_ID>
    Then the item price for sku <SKU_CODE> is $15.00 when customer <EMAIL_ID> retrieve the item price in store <STORE>
    When I add segment ITEST_SEGMENT - ITEST_SEGMENT
    Then the item price for sku <SKU_CODE> is <SEGMENT_ITEM_PRICE> when customer <EMAIL_ID> retrieve the item price in store <STORE>

    Examples:
      | EMAIL_ID                     | CUSTOMER_SEGMENT | SEGMENT_ITEM_PRICE | SKU_CODE                                | STORE |
      | itestsegment@elasticpath.com | ITEST_SEGMENT    | $9999.00           | productUsedInCustomerSegmentsItests_sku | mobee |