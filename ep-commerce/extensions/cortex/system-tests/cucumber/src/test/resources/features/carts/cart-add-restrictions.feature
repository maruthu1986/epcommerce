@Carts
Feature: Add To Cart is disabled for certain items
  As a Store Manager,
  In order to avoid poor customer experiences* resulting from invalid purchases,
  I need to prevent customers from adding to their cart items that I cannot fulfil or that do not have a valid price.

  Background:
    Given I am logged in as a public shopper

  Scenario Outline: Add To Cart is disabled for items without a price
    Given item <ITEM> does not have a price
    When I view <ITEM> in the catalog
    Then I am not able to add the item to my cart

    Examples:
      | ITEM                   |
      | bundle_nopriceitem_sku |
      | noPrice_sku            |

  Scenario Outline: Get
    Given that <LINK> is the url of <TEST>
    When I GET <LINK>
    And I go to add to cart form
    Then there is an advisor message with the following fields:
      | messageType | messageId    | debugMessage    | dataField | blocks                 |
      | error       | <MESSAGE_ID> | <DEBUG_MESSAGE> | <ITEM>    | addtodefaultcartaction |

    Examples:
      | TEST                          | LINK                                   | ITEM        | MESSAGE_ID          | DEBUG_MESSAGE                      |
      | sku that is not store visible | /items/mobee/qgqvhk2hiezdsmzzgfpxg23v= | GA29391_sku | item.not.visible | Item 'GA29391_sku' is not visible. |

  Scenario Outline: Can't add an item not available
    Given that <LINK> is the url of <TEST>
    When I POST request body {"quantity":"1"} to <LINK>
    Then the operation is identified as conflict
    And the response message is <DEBUG_MESSAGE>

    Examples:
      | TEST                                  | LINK                                              | DEBUG_MESSAGE                                                 |
      | sku that is not part of current scope | /carts/items/mobee/qgqvhk3qom4tiojuhfpxg23v=/form | Item 'ps94949_sku' is not part of the current store's catalog. |
      | sku that is not store visible         | /carts/items/mobee/qgqvhk2hiezdsmzzgfpxg23v=/form | Item 'GA29391_sku' is not visible.                            |
