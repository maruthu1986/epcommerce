@Orders
Feature: Taxes on order vary by jurisdiction
  As a client developer
  I want to retrieve tax data specific to a jurisdiction
  so that I can display to the shopper how much they need to pay for each tax

#  Tax Jurisdiction where two taxes apply
#    Given the tax jurisdiction for Canada has been configured with PST (7%) and GST (5%)
#    And province BC has been configured to charge both PST and GST
#    And province AB has been configured to only charge GST

  Background:
    Given I am logged in as a public shopper

  Scenario: In BC, both 7% PST and 5% GST apply
    And I add item with code taxablegoods to my cart
    When I add an address with country CA and region BC
    And I retrieve the order taxes
    Then the PST cost is $0.70
    And the GST cost is $0.50

  Scenario:  In AB, only 5% GST applies
    And I add item with code taxablegoods to my cart
    When I add an address with country CA and region AB
    And I retrieve the order taxes
    Then the PST cost is $0.00
    And the GST cost is $0.50

  Scenario: In California, 6% States Sales Tax applies
    And I add item with code taxablegoods to my cart
    When I add an address with country US and region CA
    And I retrieve the order taxes
    Then the States Sales Tax cost is $0.60

  Scenario: Taxes change when billing addresses change
    And I add item with code FocUSsku to my cart
    And I select only the billing address
    And I also select the shipping address
    And I retrieve the order taxes
    And the tax total on the order is $13.20
    And the field cost contains value display:$7.70, title:PST
    And the field cost contains value display:$5.50, title:GST
    When I add an address with country CA and region QC
    And I select the new shipping address
    And I retrieve the order taxes
    Then the tax total on the order is $13.75
    And the field cost contains value display:$8.25, title:PST
    And the field cost contains value display:$5.50, title:GST

#  Scenario: For non-taxable items, the tax cost is zero
#  This scenario does not work yet, COR-2174
#  Given an order contains a non-taxable item
#  When I add an address with country CA and region BC
#  And I retrieve the order taxes
#  Then the PST cost is $0.00
#  And the GST cost is $0.00
#
#  Scenario: Tax not yet calculated
#  This scenario does not work yet, COR-2174
#  Given an order contains a 10 dollar taxable item
#  When I have not selected any shipping or billing address
#  And I retrieve the order taxes
#  Then the PST cost is $0.00
#  And the GST cost is $0.00

#  Tax Jurisdiction where one tax can apply
#    Given the tax jurisdiction for United States has been configured with State Sales Tax (6%)
#    And state CA has been configured to charge State Sales Tax
#    And state AK has been configured to not charge State Sales Tax

#  Scenario:  In Alaska, the tax cost is zero
#  This scenario does not work yet, COR-2174
#  Given an order contains a 10 dollar taxable item
#  When I add an address with country US and region AK
#  And I retrieve the order taxes
#  Then the States Sales Tax cost is $0.00
