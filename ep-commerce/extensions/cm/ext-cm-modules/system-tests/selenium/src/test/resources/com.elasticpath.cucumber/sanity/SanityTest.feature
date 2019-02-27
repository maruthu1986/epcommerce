@sanity @smoketest
Feature: Admin user can navigate all activities

  Background:
    Given I sign in to CM as admin user

  Scenario: Navigate all activities
    When I go to Catalog Management
    And I expand Mobile Catalog catalog
    And I open category Accessories to view products list
    Then Product Listing should contain following products
      | Portable TV |

    When I search for product name Samsung Focus
    Then Product name Samsung Focus should appear in result

    When I go to Price List Manager
    And I search for price list
    Then I should see price list Mobee CA Shopper Price List in the result
    When I select Price List Assignments tab
    And I search for all Price List Assignments
    Then Search result should contain following Price List Assignments
      | Default Pricing for Mobee store in CAD |
      | Europe Pricing for Toastie             |

    When I go to Promotions and Shipping
    And I click Search button in Promotion tab
    Then Promotion Search Results should contain following promotions
      | 10% off Cart Total |
    When I click Search button in Shipping Service Levels tab
    Then Shipping Service Level Search Results should contain following service level codes
      | FedExExpress |

    Given I have an order for scope mobee with following skus
      | skuCode                 | quantity |
      | handsfree_shippable_sku | 1        |
    When I go to Customer Service
    Then I can search and open order editor for the latest order
    And I can select Details tab in the Order Editor

    When I select following report options
      | reportType    | store | currency | orderStatus          |
      | Order Summary | Mobee | CAD      | In Progress, Created |
    And I enter dates and run the report
    And I view the number of orders for order summary
    When I create an order for scope mobee with following sku
      | skuCode                                      | quantity |
      | physical_product_with_lineitem_promotion_sku | 1        |
    And I view the Order Summary report
    Then the number of orders for Order Summary report should have increased

    When I go to Configuration
    And I enter setting name mailHost in filter
    Then I should see setting COMMERCE/SYSTEM/EMAIL/mailHost in the filter result
    When I go to Stores
    And I edit store MOBEE in editor
    Then The store code MOBEE should match in the store editor

