@tax
Feature: taxesForShoppingCartWithTaxRateZeroValues

	As Finance, when items have been added to cart, before I complete the order I want taxes to be displayed correctly.

	Background:

		Given a store with an [exclusive] tax jurisdiction of [CA]
		And with tax rates of
			| taxName | taxRate | taxRegion |
			| PST     | 0       | BC        |
			| GST     | 0       | CA        |

		And with shipping regions of
			| region | regionString |
			| Canada | [CA(BC)]     |

		And with shipping service levels of
			| region | shipping service level code | price |
			| Canada | 2 Business Days | 7.00  |

		And with products of
			| skuCode   | price  | type     |
			| GPS-201   | 100.00 | physical |

		And with a default customer

	Scenario: Shopping cart should have right number of tax values/tax categories for configured tax rates.

	Given the customer's shipping address is in
		| subCountry | country |
		| BC		 | CA	   |
	
		And the customer shipping method is [2 Business Days]

		And the customer adds these items to the shopping cart
			| quantity | skuCode   |
			| 1        | GPS-201 |

		When I request the taxes to be calculated on the shopping cart

		Then I expect the tax calculation returned to contain 2 tax values
		And I expect the tax calculation returned to contain 2 tax categories
