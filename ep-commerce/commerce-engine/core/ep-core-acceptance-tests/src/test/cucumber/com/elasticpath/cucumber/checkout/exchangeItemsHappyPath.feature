@checkout
Feature: Exchange items in a completed shipment

  As a CSR, after a physical shipment is shipped, I want to ensure that I can create an exchange order for the goods that were shipped.

  Background:

    Given a store with an [exclusive] tax jurisdiction of [CA]

    And with shipping regions of
      | region | regionString |
      | Canada | [CA(BC)]     |

    And with shipping service levels of
      | region | shipping service level code | price |
      | Canada | 2-Business-Days | 10.00 |

    And with products of
      | skuCode     | price  |
      | originalSku | 100.00 |
      | exchangeSku | 200.00 |

    And with a default customer

  Scenario: Complete shipment and create exchange order

    Given the customer's shipping address is in
      | subCountry | country |
      | BC         | CA      |

    And the customer shipping method is [2-Business-Days]

    And the customer purchases these items
      | quantity | skuCode     |
      | 1        | originalSku |

    And the order is completed

    And an exchange is created with returning following items
      | quantity | skuCode     |
      | 1        | originalSku |

    And exchanging with following items
      | quantity | skuCode |
      | 1	     | exchangeSku |
