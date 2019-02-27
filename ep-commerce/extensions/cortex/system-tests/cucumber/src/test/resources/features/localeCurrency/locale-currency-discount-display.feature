@Locale
Feature: View discount details as per provided locale

  Background:
    Given I am logged in as a public shopper

  Scenario Outline: Ensure shopping cart displays correct discount amount and currency with given locale

    Given I am shopping in locale <LOCALE> with currency <CURRENCY>
    And I have item with code tt0970179_sku in my cart
    When I apply a coupon code blackfriday to my order
    Then the cart discount has currency <CURRENCY> and display <DISCOUNT_DISPLAY>

    Examples:
      | LOCALE | CURRENCY | DISCOUNT_DISPLAY |
      | en     | CAD      | $10.50           |
      | en     | EUR      | €0.60            |
      | fr     | CAD      | $10.50           |
      | fr     | EUR      | €0.60            |
      | fr-CA  | EUR      | €0.60            |
      | fr-CA  | CAD      | 10,50$           |

