@Lookups
Feature: Lookup a batch of Items

  Background:
    Given I am logged in as a public shopper

  Scenario Outline: Lookup a batch of Items from a list of valid and invalid codes
    Given I retrieve the batch items lookup form
    When I submit a batch of sku codes <SKU_CODES>
    Then a batch of <NUMBER_OF_ITEMS> items is returned

    Examples:
      | SKU_CODES                                                                                                   | NUMBER_OF_ITEMS |
      | ["product_with_no_discounts_sku", "handsfree_shippable_sku", "phyItemNoStockSku", "portable_tv_hdrent_sku"] | 4               |
      | ["invalid_code", "product_with_no_discounts_sku", "handsfree_shippable_sku"]                                | 2               |

  Scenario Outline: Lookup a batch of Items and verify the correct items are returned
    Given I retrieve the batch items lookup form
    When I submit a batch of sku codes <SKU_CODES>
    Then the batch lookup returns the correct <SKU_CODES>

    Examples:
      | SKU_CODES                                                                                                |
      | ["product_with_no_discounts_sku","handsfree_shippable_sku","phyItemNoStockSku","portable_tv_hdrent_sku"] |

