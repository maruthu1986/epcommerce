@shoppingCart
@shoppingItem
@promotions
Feature: Shopping Item sort by price
  In order to limit losses incurred by offering promotion pricing,
  As a Store Marketer,
  I want to prioritise lower-priced shopping items when applying discounts.

  Background:
	Given a default customer

  Scenario: Items are sorted lowest-to-highest by list price
	Given a Cart Item S1 with List Price $10
	And a Cart Item S2 with List Price $8
	And a Cart Item S3 with List Price $30
	When the promotion engine evaluates the list of discountable shopping items
	Then the items are sorted in the order of S2, S1, S3

  Scenario: Items are sorted taking existing discounts into account
	Given a Cart Item S1 with List Price $10 and cart discount $3
	And a Cart Item S2 with List Price $10 and cart discount $2
	And a Cart Item S3 with List Price $10 and cart discount $5
	When the promotion engine evaluates the list of discountable shopping items
	Then the items are sorted in the order of S3, S1, S2

  Scenario: Items are sorted taking quantity into account
	Given a Cart Item S1 with List Price $10 and quantity 3
	And a Cart Item S2 with List Price $10 and quantity 2
	And a Cart Item S3 with List Price $10 and quantity 1
	When the promotion engine evaluates the list of discountable shopping items
	Then the items are sorted in the order of S3, S2, S1

  Scenario: Items are sorted taking list price, discounts, and quantity into account
	Given a Cart Item S1 with List Price $5 and quantity 3
	And a Cart Item S2 with List Price $10 and quantity 1
	And a Cart Item S3 with List Price $20 and cart discount $15
	When the promotion engine evaluates the list of discountable shopping items
	Then the items are sorted in the order of S3, S2, S1

  Scenario: Non-discountable items are omitted from the sorted list
	Given a Cart Item S1 with List Price $5 and quantity 3
	And a non-discountable Cart Item S4
	When the promotion engine evaluates the list of discountable shopping items
	Then the set of items does not include Shopping Item S4
