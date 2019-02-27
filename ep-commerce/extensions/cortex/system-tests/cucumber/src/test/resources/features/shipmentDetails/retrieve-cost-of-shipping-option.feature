@Shipping @HeaderAuth

Feature: Shipping Costs

  Background:
    Given I login as a registered shopper

  Scenario Outline: Shipping cost with fixed price calculation
    Given a shipping option <FIXED_PRICE_SHIPPING> has cost of <AMOUNT>
    And I have item with code <ITEM_SKU> in my cart with quantity 1
    When I select a shipping option <FIXED_PRICE_SHIPPING>
    Then the shipping cost has fields amount: <AMOUNT>, currency: <CURRENCY> and display: <DISPLAY_AMOUNT>

    Examples:
      | ITEM_SKU     | FIXED_PRICE_SHIPPING            | AMOUNT | CURRENCY | DISPLAY_AMOUNT |
      | physical_sku | FixedPriceNoPromoShippingOption | 100.0  | CAD      | $100.00        |

  Scenario Outline: Fixed shipping cost with shipping promotion
    Given a shipping promotion FixedPriceWith100PercentOffPromoShippingOption for the shipping option <SHIPPING_OPTION>
    And I have item with code <ITEM_SKU> in my cart with quantity 1
    When I select a shipping option <FIXED_PRICE_SHIPPING>
    Then the shipping cost has fields amount: <AMOUNT>, currency: <CURRENCY> and display: <DISPLAY_AMOUNT>

    Examples:
      | ITEM_SKU     | FIXED_PRICE_SHIPPING                           | AMOUNT | CURRENCY | DISPLAY_AMOUNT |
      | physical_sku | FixedPriceWith100PercentOffPromoShippingOption | 0.0    | CAD      | $0.00          |

  Scenario Outline: Shipping cost calculation with percentage of total order
    Given an item with cost of <ITEM_PRICE> and shipping cost is 10 percent of order total
    And I have item with code <ITEM_SKU> in my cart with quantity 1
    When I select a shipping option <PERCENTAGE_SHIPPING_COST>
    Then the shipping cost has fields amount: <AMOUNT>, currency: <CURRENCY> and display: <DISPLAY_AMOUNT>

    Examples:
      | ITEM_SKU     | ITEM_PRICE | PERCENTAGE_SHIPPING_COST | AMOUNT | CURRENCY | DISPLAY_AMOUNT |
      | physical_sku | 25         | CanadaPostExpress        | 2.5    | CAD      | $2.50          |