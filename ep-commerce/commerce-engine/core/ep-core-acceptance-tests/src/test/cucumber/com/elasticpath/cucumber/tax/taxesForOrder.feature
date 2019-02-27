@tax
Feature: taxesForOrder

	As Finance, when an order is completed, I want to ensure that the taxes journaled match the taxes configured at order time even if they are
	subsequently changed in the store, this allows for refunds and exchanges to be handled correctly using the tax rates (if any) at the time of purchase
	rather than at the time of refund/exchange.

	Background:

		Given a store with an [exclusive] tax jurisdiction of [CA]

		And with shipping regions of
			| region | regionString |
			| Canada | [CA(BC)]     |

		And with shipping service levels of
			| region | shipping service level code | price |
			| Canada | 2-Business-Days | 10.00 |

		And with products of
			| skuCode   | price  |
			| ETX-105PE | 599.00 |

		And with a default customer

	Scenario: Tax Calculation with no taxes configured for the customer's tax jurisdiction at time of purchase.

		Given the customer's shipping address is in
			| subCountry | country |
			| BC         | CA      |

		And the customer shipping method is [2-Business-Days]

		And the customer purchases these items
			| quantity | skuCode   |
			| 1        | ETX-105PE |

		When tax rates are changed to
			| taxName | taxRate | taxRegion |
			| PST     | 10      | BC        |
			| GST     | 10      | CA        |

		Then I expect that the tax journal should have purchase entries
			| journalType | transactionType | itemCode  | itemAmount | taxName | taxCode  | taxAmount | taxRate | taxJurisdiction     | taxRegion     |
			| purchase    | Order           | ETX-105PE | 599.00     | NO_TAX  | GOODS    | 0.00      | 0.00    | NO_TAX_JURISDICTION | NO_TAX_REGION |
			| purchase    | Order           | SHIPPING  | 10.00      | NO_TAX  | SHIPPING | 0.00      | 0.00    | NO_TAX_JURISDICTION | NO_TAX_REGION |
