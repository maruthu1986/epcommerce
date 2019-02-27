@Locale
Feature: Purchase item as per provided locale and ensure purchase item shows correct amount and currency

  Background:
    Given I am logged in as a public shopper

  Scenario Outline: The locale and currency value of a purchase persists with original values

    Given I am shopping in locale <LOCALE> with currency <CURRENCY>
    And I have previously made a purchase with item code <ITEM_CODE>
    And the purchase item monetary total has currency <CURRENCY> and display <DISPLAY_AMOUNT>
    And the purchase item tax total has currency <CURRENCY> and display <TAX_DISPLAY_AMOUNT>
    When I switch to locale <NEW_LOCALE> with currency <NEW_CURRENCY>
    Then the purchase item monetary total has currency <CURRENCY> and display <DISPLAY_AMOUNT>
    And the purchase item tax total has currency <CURRENCY> and display <TAX_DISPLAY_AMOUNT>

    Examples:
      | ITEM_CODE     | LOCALE | CURRENCY | CURRENCY | DISPLAY_AMOUNT | TAX_DISPLAY_AMOUNT | NEW_LOCALE | NEW_CURRENCY |
      | tt0970179_sku | en     | CAD      | CAD      | $39.19         | $4.20              | fr-CA      | EUR          |
      | tt0970179_sku | fr-CA  | EUR      | EUR      | €2.23          | €0.24              | en         | CAD          |


