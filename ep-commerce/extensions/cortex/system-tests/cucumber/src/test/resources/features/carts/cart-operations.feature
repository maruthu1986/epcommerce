@Carts
Feature: Cart operations
  As a shopper
  I want to be able to modify the contents of my shopping cart
  So that the cart only contains the items I want to purchase

  Background:
    Given I have authenticated as a newly registered shopper

  Scenario Outline: Adding an item to the cart
    When I add item with code <ITEM_CODE> to my cart with quantity 1 and do not follow location
    Then the HTTP status is OK, created
    And the cart total-quantity is 1
    And the number of cart lineitems is 1
    And the cart lineitem for item code <ITEM_CODE> has quantity of 1

    Examples:
      | ITEM_CODE    |
      | tt64464fn_sd |

  Scenario Outline: Adding additional quantities of an item to a cart
    Given I have item with code <ITEM_CODE> in my cart with quantity 2
    When I add item with code <ITEM_CODE> to my cart with quantity 3 and do not follow location
    Then the HTTP status is OK
    And the cart total-quantity is 5
    And the number of cart lineitems is 1
    And the cart lineitem for item code <ITEM_CODE> has quantity of 5

    Examples:
      | ITEM_CODE    |
      | tt64464fn_sd |

  Scenario Outline: Adding different items to a cart
    Given I have item with code <ITEM_CODE_1> in my cart with quantity 2
    When I add item with code <ITEM_CODE_2> to my cart with quantity 3
    Then the cart total-quantity is 5
    And the number of cart lineitems is 2
    And the cart lineitem for item code <ITEM_CODE_1> has quantity of 2
    And the cart lineitem for item code <ITEM_CODE_2> has quantity of 3

    Examples:
      | ITEM_CODE_1  | ITEM_CODE_2 |
      | tt64464fn_sd | alien_sku   |

  Scenario Outline: Decrement the quantity of a lineitem in a cart
    Given I have item with code <ITEM_CODE> in my cart with quantity 2
    When I change the lineitem quantity of item code <ITEM_CODE> to 1
    Then the cart total-quantity is 1
    And the cart lineitem for item code <ITEM_CODE> has quantity of 1

    Examples:
      | ITEM_CODE    |
      | tt64464fn_sd |

  Scenario Outline: Increment the quantity of a lineitem in a cart
    Given I have item with code <ITEM_CODE> in my cart with quantity 1
    When I change the lineitem quantity of item code <ITEM_CODE> to 3
    Then the cart total-quantity is 3
    And the cart lineitem for item code <ITEM_CODE> has quantity of 3

    Examples:
      | ITEM_CODE    |
      | tt64464fn_sd |

  Scenario Outline: Changing the lineitem quantity to 0 deletes the item from the cart
    Given I have item with code <ITEM_CODE> in my cart with quantity 1
    When I change the lineitem quantity of item code <ITEM_CODE> to 0
    Then the list of cart lineitems is empty
    And the cart total-quantity is 0

    Examples:
      | ITEM_CODE    |
      | tt64464fn_sd |

  Scenario Outline: Delete a lineitem from a cart
    Given I have item with code <ITEM_CODE> in my cart with quantity 10
    When I delete the lineitem with code <ITEM_CODE> from my cart
    Then the list of cart lineitems is empty

    Examples:
      | ITEM_CODE    |
      | tt64464fn_sd |

  Scenario Outline: Cannot add an item to the cart with an invalid quantity
    When I attempt to add firstProductAddedToCart with invalid quantity <QUANTITY>
    Then the operation is identified as bad request
    And nothing has been added to the cart

    Examples:
      | QUANTITY      |
      | 0             |
      | -2            |
      | 0.1           |
      | invalidFormat |
      | 2147483648    |

  Scenario Outline: Cannot update a cart lineitem quantity an invalid quantity
    Given I have item with code firstProductAddedToCart_sku in my cart with quantity 1
    When I attempt to change the lineitem quantity for firstProductAddedToCart to <INVALID_QUANTITY>
    Then the operation is identified as bad request
    And the cart total-quantity remains 1

    Examples:
      | INVALID_QUANTITY |
      | -1               |
      | 0.1              |
      | invalidFormat    |
      | 2147483648       |

  Scenario Outline: Updating lineitem quantity gets correctly validated
    Given I have item with code <ITEM_CODE> in my cart with quantity 10
    And I cannot add to cart line item with code <ITEM_CODE> with quantity 1
    When I change the lineitem quantity of item code <ITEM_CODE> to 8
    Then the cart lineitem for item code <ITEM_CODE> has quantity of 8

    Examples:
      | ITEM_CODE    |
      | sony_bt_sku  |