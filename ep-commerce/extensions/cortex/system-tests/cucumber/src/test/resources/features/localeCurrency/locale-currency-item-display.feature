@Locale
Feature: Display item name, currency, list and purchase price as per provided locale

  Scenario Outline: Display item name, currency, list and purchase price as per provided locale for a public shopper
    Given I am logged in as a public shopper
    And I am shopping in locale <LOCALE> with currency <CURRENCY>
    When I look up an item with code <ITEM_CODE>
    Then I should see item name is <DISPLAY_ITEMNAME>
    And the item list price currency is <CURRENCY> and the display is <LIST_DISPLAY>
    And the item purchase price currency is <CURRENCY> and the display is <PURCHASE_DISPLAY>

    Examples:
      | ITEM_CODE     | LOCALE | CURRENCY | DISPLAY_ITEMNAME      | PURCHASE_DISPLAY | LIST_DISPLAY |
      | tt0970179_sku | en     | CAD      | Hugo                  | $34.99           | $34.99       |
      | tt0970179_sku | en     | EUR      | Hugo                  | €1.99            | €43.99       |
      | tt0970179_sku | fr     | CAD      | Hugo French only [fr] | $34.99           | $34.99       |
      | tt0970179_sku | fr     | EUR      | Hugo French only [fr] | €1.99            | €43.99       |
      | tt0970179_sku | fr-CA  | EUR      | les aventures de Hugo | €1.99            | €43.99       |
      | tt0970179_sku | fr-CA  | CAD      | les aventures de Hugo | 34,99$           | 34,99$       |

  Scenario Outline: Display item name, currency, list and purchase price as per provided locale for a Registered shopper
    Given I login as a registered shopper
    And I am shopping in locale <LOCALE> with currency <CURRENCY>
    When I look up an item with code <ITEM_CODE>
    Then I should see item name is <DISPLAY_ITEMNAME>
    And the item list price currency is <CURRENCY> and the display is <LIST_DISPLAY>
    And the item purchase price currency is <CURRENCY> and the display is <PURCHASE_DISPLAY>

    Examples:
      | ITEM_CODE     | LOCALE | CURRENCY | DISPLAY_ITEMNAME      | PURCHASE_DISPLAY | LIST_DISPLAY |
      | tt0970179_sku | en     | CAD      | Hugo                  | $34.99           | $34.99       |
      | tt0970179_sku | en     | EUR      | Hugo                  | €1.99            | €43.99       |
      | tt0970179_sku | fr     | CAD      | Hugo French only [fr] | $34.99           | $34.99       |
      | tt0970179_sku | fr     | EUR      | Hugo French only [fr] | €1.99            | €43.99       |
      | tt0970179_sku | fr-CA  | EUR      | les aventures de Hugo | €1.99            | €43.99       |
      | tt0970179_sku | fr-CA  | CAD      | les aventures de Hugo | 34,99$           | 34,99$       |
