@example

Feature: Purchase

  Scenario: Purchase has purchase number and same total as the order
    Given I login as a newly registered shopper
    And Adding an item with item code plantsVsZombies and quantity 1 to the cart
    And I fill in email needinfo
    And I fill in payment methods needinfo
    And I fill in billing address needinfo
    And the order total has amount: 0.99, currency: CAD and display: $0.99
    And the order is submitted
    And the HTTP status is OK
    When I view my profile
    And I follow links purchases -> element
    Then the field purchase-number matches \d\d\d\d\d
    And the field monetary-total contains value $0.99

  Scenario: Search product
    Given I am logged in as a public shopper
    When I search for an item name Alien
    Then the item code is alien_sku

  Scenario Outline: Validate messages from VirtualTopic.ep.customers and VirtualTopic.ep.orders queue
    Given I am listening to Consumer.test.VirtualTopic.ep.customers queue
    And I am listening to Consumer.test.VirtualTopic.ep.orders queue
    And I have an order for scope <scope> with following skus
      | skuCode      | quantity |
      | <sku-code-1> | 1        |
    When I read Consumer.test.VirtualTopic.ep.customers message from queue
    Then json eventType should contain following values
      | key  | value               |
      | name | CUSTOMER_REGISTERED |
    When I read Consumer.test.VirtualTopic.ep.orders message from queue
    Then json eventType should contain following values
      | key  | value          |
      | name | ORDER_RELEASED |
      | name | ORDER_CREATED  |

    Examples:
      | scope | sku-code-1 |
      | mobee | alien_sku  |

