@tax @quickTest
Feature: taxesInclusiveForOrder

  As Finance, when an order is created for digital item, I want item taxes to be calculated correctly.

  Background:

	Given a store with an [inclusive] tax jurisdiction of [CA]
	And with tax rates of
	  | taxName | taxRate | taxRegion |
	  | GST     | 20      | CA        |

	And with products of
	  | skuCode   | price | type    |
	  | ETX-105PE | 99.99 | digital |

	And with a default customer

  Scenario: Tax Inclusive Calculation for Order

	Given the customer's shipping address is in
	  | subCountry | country |
	  | BC         | CA      |

	And the customer purchases these items
	  | quantity | skuCode   |
	  | 1        | ETX-105PE |

	Then I expect that the order should have fields
	  | itemCode  | itemAmount | taxAmount | taxRate | beforeTaxSubtotal |
	  | ETX-105PE | 99.99      | 16.67     | 0.20    | 83.32             |
