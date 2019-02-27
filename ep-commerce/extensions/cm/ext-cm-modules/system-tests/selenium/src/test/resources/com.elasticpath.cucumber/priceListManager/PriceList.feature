@smoketest @priceListManager @priceList
Feature: Price List

  Background:
    Given I sign in to CM as admin user
    And I go to Price List Manager

  Scenario: Search Price Lists
    When I search for price list
    Then I should see price list Kobee Price List in the result

  Scenario: Create and delete Price List
    When I create a new price list with description Test Description and currency USD
    Then I should see the newly created price list
    When I select Price List tab
    And I delete the newly created price list
    Then The deleted price list no longer exists

  @cleanupPriceList
  Scenario Outline: Add and delete price
    And I have a new Price List
    And I open the newly created price list editor
    When I add a list price 24.99 for product <product-name-1>
    And I add a list price 49.99 for product <product-name-2>
    And I add a list price 69.99 for sku code <sku-code>
    Then the price list should have prices for the following product codes
      | <product-code-1> |
      | <product-code-2> |
      | <sku-code>       |
    When I delete price for product code <product-code-1>
    And I delete price for sku code <sku-code>
    Then product code <product-code-1> should not be in price list editor
    And product code <sku-code> should not be in price list editor
    And the price list should have price for the following product code
      | <product-code-2> |

    Examples:
      | product-code-1 | product-name-1 | product-code-2 | product-name-2 | sku-code          |
      | tt64464fn      | Finding Nemo   | TV_72542       | Superheroes    | tt966001av_hd_buy |

  @cleanupPriceList
  Scenario: Open item from price list
    And I have a new Price List
    And I open the newly created price list editor
    And I add a list price 24.99 for product Finding Nemo
    When I open the product editor for product code tt64464fn
    Then the product name should be Finding Nemo

  Scenario Outline: PL summary tab validation
    When I click Create Price List button
    And I enter following price list summary value and save it
      | Price List  | <price-list>  |
      | Description | <description> |
      | Currency    | <currency>    |
    Then I should see following error messages
      | <error-message-1> |
      | <error-message-2> |

    Examples:
      | price-list        | description      | currency | error-message-1                | error-message-2        |
      |                   |                  | USD      | This value is required         |                        |
      | Mobile Price List |                  | CAD      | Price List name must be unique |                        |
      | Test              |                  | ABC      | Unsupported currency code      |                        |
      | Test              |                  |          | Unsupported currency code      |                        |
      |                   | Test Description |          | Unsupported currency code      | This value is required |

  @cleanupPriceList
  Scenario: Price list without description
    When I create new price list with currency USD and without description
    Then I should see the newly created price list

  Scenario Outline: Search price list
    When I search for price list
    And I open price list Kobee Price List in editor
    And I search price for code <product-code>
    Then the price list should have price for the following product code
      | <product-code> |
    Examples:
      | product-code |
      | tt0970179    |

  @cleanupPriceList
  Scenario Outline: Edit price
    Given I have a new Price List
    And I open the newly created price list editor
    When I add a sale price <sale-price> and a list price <list-price> for the product <product-name>
    Then the price list should have a list price <list-price> and a sale price <sale-price> for the product <product-name>
    When I edit price for product <product-name> as list price <edit-list-price> and sale price <edit-sale-price>
    Then the price list should have a list price <edit-list-price> and a sale price <edit-sale-price> for the product <product-name>

    Examples:
      | product-name | list-price | sale-price | edit-list-price | edit-sale-price |
      | Finding Nemo | 49.99      | 39.99      | 44.99           | 34.99           |
      | Finding Nemo | 49.99      | 39.99      | 44.99           | 0.00            |
      | Finding Nemo | 49.99      | 0.00       | 44.99           | 34.99           |
#	TODO create new Scenario, since the edit list and sale prices are same as original prices, after the edit Save All button remains disabled
#	  | Finding Nemo | 0.00       | 0.00       | 0.00            | 0.00            |

  @cleanupPriceList
  Scenario Outline: Duplicate pricing tiers are prohibited
    And I have a new Price List
    And I open the newly created price list editor
    And I add list price 10.00 and sale price 9.99 for quantity 1 for product <product-name>
    When I attempt to add list price 12.00 and sale price 5 for quantity 1 for product <product-name>
    Then I should see following validation alert
      | Price tier for minimum quantity tt64464fn already exists |
    And I can add list price 10.00 and sale price 9.99 for quantity 2 for product <product-name>

    Examples:
      | product-name |
      | Finding Nemo |

  Scenario Outline: Search and filter price list
    Given I search for price list
    And I open price list Mobile Price List in editor
    When I search the price list with prices from <searchFromPrice> and to <SearchToPrice>
    Then I should see <expSearchResults> prices returned
    When I filter the price list with prices from <filterFromPrice> and to <filterToPrice>
    Then I should see <expFilterResults> prices returned

    Examples:
      | searchFromPrice | SearchToPrice | expSearchResults | filterFromPrice | filterToPrice | expFilterResults |
      | 1               | 3             | 4                | 2               | 3             | 1                |