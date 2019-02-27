@taxCodeRetriever
Feature: taxCodeRetriever

	As Finance I need to ensure that each SKU in the product catalog has the appropriate tax code configured so that I have a greater level of tax granularity.

	Background:

		Given Product P1 with Tax Code [GOODS]

	Scenario: SKU specifies a different Tax Code to its parent Product
		Given a SKU of Product P1
		And the SKU has a Tax Code of [NONE]
		When the SKU is examined for its Tax Code
		Then the resulting Tax Code is [NONE]

	Scenario: SKU specifies the same Tax Code as its parent Product
		Given a SKU of Product P1
		And the SKU has a Tax Code of [GOODS]
		When the SKU is examined for its Tax Code
		Then the resulting Tax Code is [GOODS]

	Scenario: SKU does not specify a Tax Code and inherits from its parent Product
		Given a SKU of Product P1
		And the SKU does not specify a Tax Code
		When the SKU is examined for its Tax Code
		Then the resulting Tax Code is [GOODS]
