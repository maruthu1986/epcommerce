@Prices

Feature: Prices - When item with different list price
  and purchase price have been added to the cart,
  those prices should be different in the cart.

  Scenario: List price and sale price are different for item
    Given I am logged into scope mobee as a public shopper
    When I add item with code tt777789bttf_hd_rent to my cart
    Then the lineitem price has list-price fields amount: 10.99, currency: CAD and display: $10.99
    And the lineitem price has purchase-price fields amount: 6.99, currency: CAD and display: $6.99