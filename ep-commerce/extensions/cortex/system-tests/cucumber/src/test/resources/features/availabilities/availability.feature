@Items @Availability
Feature: Item availability
  As a client developer,
  I can retrieve the availability of an item
  so I can decide whether to display it or not

  Background:
    Given I am logged in as a public shopper

  Scenario Outline: An item availability is based on stock
    When I look up an item with code <ITEM>
    And I view the item availability
    Then The availability should be <AVAILABILITY>

    Examples:
      | ITEM                             | AVAILABILITY  |
      | multiSkuProduct-out-of-stock-sku | NOT_AVAILABLE |
      | multiSkuProduct-available-sku    | AVAILABLE     |

  Scenario Outline: Expired and backordered items do not display a release date
    When I look up an item with code <EXPIRED_ITEM>
    And I view the item availability
    Then The availability should be <AVAILABILITY>
    And the field release-date does not exist

    Examples:
      | EXPIRED_ITEM                | AVAILABILITY             |
      | multiSkuProduct-expired-sku | NOT_AVAILABLE            |
      | tt0926084_sku               | AVAILABLE_FOR_BACK_ORDER |


  Scenario Outline: An item with availability rule of "AVAILABLE_FOR_PRE_ORDER" will display release date as well
    When I look up an item with code <AVAILABLE_FOR_PRE_ORDER_ITEM>
    When I view the item availability
    Then The availability should be AVAILABLE_FOR_PRE_ORDER
    And the field release-date contains a valid date

    Examples:
      | AVAILABLE_FOR_PRE_ORDER_ITEM |
      | tt0970179_sku                |

  Scenario Outline: User should not be able to add unavailable items to cart
    When I look up an item with code <UNAVAILABLE_ITEM>
    Then I am prevented from adding the item to the cart

    Examples:
      | UNAVAILABLE_ITEM                 |
      | multiSkuProduct-out-of-stock-sku |
      | multiSkuProduct-expired-sku      |

  Scenario Outline: User should be able to add an item to cart that is in stock
    When I look up an item with code <AVAILABLE_ITEM>
    Then I am allowed to add to cart

    Examples:
      | AVAILABLE_ITEM                |
      | multiSkuProduct-available-sku |

  Scenario Outline: User should not be able to add unavailable items to cart
    Given I am logged into scope mobee as a public shopper
    When Adding an item with item code <AVAILABLE_ITEM> and quantity 5000000 to the cart
    Then the HTTP status is bad request
    And Structured error message contains:
      | Item '<AVAILABLE_ITEM>' only has 10 available but 5000000 were requested. |

    Examples:
      | AVAILABLE_ITEM |
      | sony_bt_sku    |

  Scenario Outline: Verifying an item availability after adding to cart
    Given I am logged into scope mobee as a public shopper
    When I add item with code <SKU_CODE> to my cart with quantity 1
    And I view the item availability
    Then The availability should be AVAILABLE

    Examples:
      | SKU_CODE    |
      | sony_bt_sku |