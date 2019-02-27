@Orders
Feature: Tax total on orders
  As a client developer
  I want to be able to always retrieve the taxes on an order
  so I can display taxes information to the shopper

  Background:
    Given I am logged in as a public shopper

  Scenario: Cart with no items
    When I retrieve the order taxes
    Then the tax total on the order is $0.00

  Scenario: Cart with non-taxable, non-shippable item
    And I add item non taxable goods to the cart
    When I select only the billing address
    And I retrieve the order taxes
    Then the tax total on the order is $0.00

#    For items that are non-shippable, taxes are based on billing address
  Scenario: Cart with taxable, non-shippable item: a) no billing address selected
    And I add item with code taxablegoods to my cart
    When I retrieve the order taxes
    Then the tax total on the order is $0.00

  Scenario: Cart with taxable, non-shippable item: b) billing address selected
    And I add item with code taxablegoods to my cart
    When I select only the billing address
    And I retrieve the order taxes
    Then the tax total on the order is $1.20

#    For items that are shippable, taxes are based on shipping address
  Scenario: Cart with taxable, shippable item: a) No billing or shipping address selected
    And I add item with code FocUSsku to my cart
    When I retrieve the order taxes
    Then the tax total on the order is $0.00

  Scenario: Cart with taxable, shippable item: b) Billing address selected
    And I add item with code FocUSsku to my cart
    When I select only the billing address
    And I retrieve the order taxes
    Then the tax total on the order is $0.00

  Scenario: Cart with taxable, shippable item: c) Billing and shipping address selected
    And I add item with code FocUSsku to my cart
    When I select only the billing address
    And I also select the shipping address
    And I retrieve the order taxes
    Then the tax total on the order is $13.20

  Scenario: Cart with taxable, shippable item: d) Only shipping address selected
    And I add item with code FocUSsku to my cart
    When I select only the shipping address
    And I retrieve the order taxes
    Then the tax total on the order is $13.20

  Scenario: Order total includes tax if tax is calculated
    And I add item with code taxablegoods to my cart
    And I retrieve the order
    And I follow links total
    And the field cost contains value display:$10.00
    When I select only the billing address
    And I retrieve the order taxes
    And the tax total on the order is $1.20
    Then I retrieve the order
    And I follow links total
    And the field cost contains value display:$11.20

