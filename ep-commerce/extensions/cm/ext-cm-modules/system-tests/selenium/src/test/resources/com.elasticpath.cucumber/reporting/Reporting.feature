@smoketest @reporting
Feature: Reporting

  Scenario Outline: <report-type> report
    Given I create an order for scope <scope> with following sku
      | skuCode    | quantity   |
      | <sku-code> | <quantity> |
    And I sign in to CM as admin user
    And I select following report options
      | reportType    | store   |
      | <report-type> | <store> |
    And I enter dates and run the report
    And I view the number of orders for promotion <promo-name>
    When I create an order for scope <scope> with following sku
      | skuCode    | quantity   |
      | <sku-code> | <quantity> |
    And I view the <report-type> report
    Then the number of orders for promotion <promo-name> should have increased

    Examples:
      | report-type                   | store | promo-name                              | scope | sku-code                                     | quantity |
      | Shopping Cart Promotion Usage | Mobee | 50_percent_off_physical_line_item_promo | mobee | physical_product_with_lineitem_promotion_sku | 1        |

  Scenario Outline: <report-type> report
    When I create an order for scope mobee with following sku
      | skuCode                                      | quantity |
      | physical_product_with_lineitem_promotion_sku | 1        |
    And I sign in to CM as admin user
    And I select following report options
      | reportType    | store   | currency   | orderStatus    |
      | <report-type> | <store> | <currency> | <order-status> |
    And I enter dates and run the report
    And I view the number of orders for order summary
    When I create an order for scope <scope> with following sku
      | skuCode    | quantity   |
      | <sku-code> | <quantity> |
    And I view the <report-type> report
    Then the number of orders for Order Summary report should have increased

    Examples:
      | report-type   | store | currency | order-status         | scope | sku-code                                     | quantity |
      | Order Summary | Mobee | CAD      | In Progress, Created | mobee | physical_product_with_lineitem_promotion_sku | 1        |

  Scenario Outline: <report-type> report
    Given I have an order for scope <scope> with following skus
      | skuCode    | quantity   |
      | <sku-code> | <quantity> |
    And I sign in to CM as admin user
    And I go to Customer Service
    And I create physical item return with quantity 1 for sku <sku-code>
    And shipping receive return is processed for quantity 1 of sku <sku-code>
    And I select following report options
      | reportType    | store   | currency   |
      | <report-type> | <store> | <currency> |
    When I enter dates and run the report
    Then Returns And Exchanges report should contain the returned order number

    Examples:
      | report-type           | store | currency | scope | sku-code     | quantity |
      | Returns And Exchanges | Mobee | CAD      | mobee | physical_sku | 1        |

  Scenario Outline: <report-type> report
    Given I have an order for scope <scope> with following skus
      | skuCode    | quantity   |
      | <sku-code> | <quantity> |
    And I sign in to CM as admin user
    And I select following report options
      | reportType    | store   | currency   | orderStatus |
      | <report-type> | <store> | <currency> | Complete    |
    When I enter dates and run the report
    Then the latest order total should be <order-total> in Order Status report

    Examples:
      | report-type  | store | currency | scope | sku-code        | quantity | order-total |
      | Order Status | Mobee | CAD      | mobee | plantsVsZombies | 1        | 0.99        |
