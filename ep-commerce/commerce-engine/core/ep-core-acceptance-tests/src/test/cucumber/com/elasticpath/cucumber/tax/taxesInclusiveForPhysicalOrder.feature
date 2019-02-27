@tax
Feature: taxesInclusiveForOrder

  As Finance, when an order is created for physical item, I want item taxes to be calculated.

  Background:

	Given a store with an [inclusive] tax jurisdiction of [CA]
	And with tax rates of
	  | taxName | taxRate | taxRegion |
	  | PST     | 7       | BC        |
	  | GST     | 5       | CA        |

	And with shipping regions of
	  | region | regionString |
	  | Canada | [CA(BC)]     |

	And with shipping service levels of
	  | region | shipping service level code | price |
	  | Canada | 2 Business Days             | 10.00 |

	And with products of
	  | skuCode   | price  | type     |
	  | ETX-105PE | 599.00 | physical |

	And with a default customer

  Scenario: Tax Inclusive Calculation for Order

	Given the customer's shipping address is in
	  | subCountry | country |
	  | BC         | CA      |

	And the customer shipping method is [2 Business Days]

	And the customer purchases these items
	  | quantity | skuCode   |
	  | 1        | ETX-105PE |

	When the order is completed

	Then I expect that the tax journal should have purchase entries
	  | journalType | transactionType | itemCode  | itemAmount | taxName | taxCode  | taxAmount | taxRate | taxJurisdiction | taxRegion |
	  | purchase    | Order           | ETX-105PE | 599.00     | PST     | GOODS    | 37.44     | 0.07    | CA              | BC        |
	  | purchase    | Order           | ETX-105PE | 599.00     | GST     | GOODS    | 26.74     | 0.05    | CA              | CA        |
	  | purchase    | Order           | SHIPPING  | 10.00      | PST     | SHIPPING | 0.63      | 0.07    | CA              | BC        |
	  | purchase    | Order           | SHIPPING  | 10.00      | GST     | SHIPPING | 0.45      | 0.05    | CA              | CA        |