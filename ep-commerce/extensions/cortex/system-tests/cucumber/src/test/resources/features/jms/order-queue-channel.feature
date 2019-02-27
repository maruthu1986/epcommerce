@jms-tests
Feature: Validate messages from the Queue channels

  Scenario Outline: Read message from consumer queues for VirtualTopic.ep.customers and VirtualTopic.ep.orders
    Given I am listening to Consumer.test.VirtualTopic.ep.customers queue
    And I delete all messages from Consumer.test.VirtualTopic.ep.customers queue
    And I am listening to Consumer.test.VirtualTopic.ep.orders queue
    And I delete all messages from Consumer.test.VirtualTopic.ep.orders queue
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
	
