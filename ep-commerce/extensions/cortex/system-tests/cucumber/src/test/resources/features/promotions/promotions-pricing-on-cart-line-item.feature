@Promotions
@Pricing

Feature: Can retrieve promotions pricing on cart line ltem
  As a client developer
  I want to display the adjusted promotional pricing on a cart lineitem
  so that the shopper can see the resulting list price and purchase price

  Scenario: Item with no sale price and no promotions
    Given I login as a public shopper
    When I add item Product With No Discounts to the cart with quantity 2
    And I can follow the price link
    Then the list-price has fields amount: 10.0, currency: CAD and display: $10.00
    And the purchase-price has fields amount: 10.0, currency: CAD and display: $10.00

  Scenario: Item with sale price and no promotions
    Given I login as a public shopper
    When I add item Product With Sale Price to the cart with quantity 2
    And I can follow the price link
    Then the list-price has fields amount: 10.0, currency: CAD and display: $10.00
    And the purchase-price has fields amount: 8.0, currency: CAD and display: $8.00

  Scenario: Item with no sale price and catalog promotion
    Given I login as a public shopper
    When I add item Product With Catalog Promotion of 10 Percent Off to the cart with quantity 2
    And I can follow the price link
    Then the list-price has fields amount: 10.0, currency: CAD and display: $10.00
    And the purchase-price has fields amount: 9.0, currency: CAD and display: $9.00

  Scenario: Item with sale price and catalog promotion
    Given I login as a public shopper
    When I add item Product With Sale Price and Catalog Promo of 10 Percent Off to the cart with quantity 2
    And I can follow the price link
    Then the list-price has fields amount: 10.0, currency: CAD and display: $10.00
    And the purchase-price has fields amount: 7.2, currency: CAD and display: $7.20

  Scenario: A unique case where the promotion is $5 off with 2+ in cart
    Given I login as a public shopper
    When I add item Product With Amount Discount For Quantity to the cart with quantity 4
    And I can follow the price link
    Then the list-price has fields amount: 10.0, currency: CAD and display: $10.00
    And the purchase-price has fields amount: 8.75, currency: CAD and display: $8.75

  Scenario: Item with no sale price and tiered pricing
    Given I login as a public shopper
    When I add item Product With Tiered Pricing to the cart with quantity 2
    And I can follow the price link
    Then the list-price has fields amount: 9.0, currency: CAD and display: $9.00
    And the purchase-price has fields amount: 9.0, currency: CAD and display: $9.00

  Scenario: Item with sale price and tiered pricing
    Given I login as a public shopper
    When I add item Product With Sale Price And Tiered Pricing to the cart with quantity 2
    And I can follow the price link
    Then the list-price has fields amount: 9.0, currency: CAD and display: $9.00
    And the purchase-price has fields amount: 7.0, currency: CAD and display: $7.00

  Scenario: Item with no sale price and cart lineitem promotion
    Given I login as a public shopper
    When I add item Product With Cart Lineitem Promo to the cart with quantity 2
    And I can follow the price link
    Then the list-price has fields amount: 10.0, currency: CAD and display: $10.00
    And the purchase-price has fields amount: 9.0, currency: CAD and display: $9.00

  Scenario: Item with sale price and cart lineitem promotion
    Given I login as a public shopper
    When I add item Product With Sale Price And Cart Lineitem Promo to the cart with quantity 2
    And I can follow the price link
    Then the list-price has fields amount: 10.0, currency: CAD and display: $10.00
    And the purchase-price has fields amount: 7.2, currency: CAD and display: $7.20

  Scenario: Item with no sale price and catalog promotion and cart lineitem promotion
    Given I login as a public shopper
    When I add item Product With Catalog Promo And Cart Lineitem Promo to the cart with quantity 2
    And I can follow the price link
    Then the list-price has fields amount: 10.0, currency: CAD and display: $10.00
    And the purchase-price has fields amount: 8.1, currency: CAD and display: $8.10

  Scenario: Item with sale price and catalog promotion and cart lineitem promotion
    Given I login as a public shopper
    When I add item Product With Sale Price And Catalog Promo And Cart Lineitem Promo to the cart with quantity 2
    And I can follow the price link
    Then the list-price has fields amount: 10.0, currency: CAD and display: $10.00
    And the purchase-price has fields amount: 6.48, currency: CAD and display: $6.48

  Scenario: Item with no sale price and tiered pricing and cart lineitem promotion
    Given I login as a public shopper
    When I add item Product With Tiered Pricing And Cart Lineitem Promo to the cart with quantity 2
    And I can follow the price link
    Then the list-price has fields amount: 9.0, currency: CAD and display: $9.00
    And the purchase-price has fields amount: 8.1, currency: CAD and display: $8.10

  Scenario: Item with sale price and tiered pricing and cart lineitem promotion
    Given I login as a public shopper
    When I add item Product With Sale Price And Tiered Pricing And Cart Lineitem Promo to the cart with quantity 2
    And I can follow the price link
    Then the list-price has fields amount: 9.0, currency: CAD and display: $9.00
    And the purchase-price has fields amount: 6.3, currency: CAD and display: $6.30

  Scenario: Item with stacked cart lineitem promotions
    Given I login as a public shopper
    When I add item Product With Stacked Cart Lineitem Promos to the cart with quantity 2
    And I can follow the price link
    Then the list-price has fields amount: 10.0, currency: CAD and display: $10.00
    And the purchase-price has fields amount: 8.55, currency: CAD and display: $8.55