@tax
Feature: taxesForShoppingCart

	As Finance, when items have been added to cart, before I complete the order I want taxes to be displayed correctly.

	Background:

		Given a store with an [exclusive] tax jurisdiction of [CA]
		And with tax rates of
			| taxName | taxRate | taxRegion |
			| PST     | 7       | BC        |
			| GST     | 5       | CA        |

		And with shipping regions of
			| region | regionString |
			| Canada | [CA(BC)]     |

		And with shipping service levels of
			| region | shipping service level code | price |
			| Canada | 2 Business Days | 7.00  |

		And with products of
			| skuCode   | price  | type     |
			| ETX-105PE | 599.00 | physical |
			| GPS-201   | 100.00 | physical |

		And with a default customer

	Scenario: Tax Calculation with no shipping address specified should result in no taxes being calculated
	since we don't have enough information to calculate taxes yet.

		Given the customer's shipping address is not set

		And the customer adds these items to the shopping cart
			| quantity | skuCode   |
			| 1        | ETX-105PE |

		When I request the taxes to be calculated on the shopping cart

		Then I expect the tax calculation returned to contain 0 tax values
		And I expect the tax calculation returned to contain 0 tax categories
