@Prices

Feature: Prices - Retrieve from-price For Items with Variants
  As a client developer,
  I want to retrieve the lowest price out of all variants that a shopper pays for an item,
  so that I can entice the shopper to make a purchase.

  Background:
    Given I am logged in as a public shopper

  Scenario: The lowest price is the lowest price of all of the skus since there is no price set on the product
  # Pricing data for the item used in this test:
  # | Item	| List-Price	| Sale-Price	|
  # | Nemo	| -	           	| -				|
  # | HD  	| $30.00       	| -				|
  # | SD  	| $35.99       	| $25.99		|
    Given an item Finding Nemo exists in my catalog
    When I follow the fromprice link on the item definition
    Then the from-price has fields amount: 25.99, currency: CAD and display: $25.99

  Scenario: The lowest price is the sku level list-price which is over-riding the product price
  # Pricing data for the item used in this test:
  # | Item		| List-Price	| Sale-Price	|
  # | Sancturary 	| $17.99		| -				|
  # | 400Pixels  	| $11.00		| -    			|
  # | 700Pixels  	| -		 		| -				|
    Given an item Sanctuary exists in my catalog
    When I follow the fromprice link on the item definition
    Then the from-price has fields amount: 11.0, currency: CAD and display: $11.00

  Scenario: The lowest price is the product list-price since there is no sale-price, nor any sku price over-rides
  # Pricing data for the item used in this test:
  # | Item					| List-Price	| Sale-Price	|
  # | Finding Santa Clause	| $22.00		| -				|
  # | 400Pixels  				| -				| -				|
  # | 720Pixels				| -		 		| -   			|
    Given an item Finding Santa Clause exists in my catalog
    When I follow the fromprice link on the item definition
    Then the from-price has fields amount: 22.0, currency: CAD and display: $22.00

  Scenario: The lowest price is the product sale-price since there are no sku price over-rides
  # Pricing data for the item used in this test:
  # | Item		| List-Price	| Sale-Price	|
  # | Twilight	| $48.48	    | $47.47		|
  # | Buy,HD  	| -				| -           	|
    Given an item Twilight exists in my catalog
    When I follow the fromprice link on the item definition
    Then the from-price has fields amount: 47.47, currency: CAD and display: $47.47
