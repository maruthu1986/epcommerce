@tax
Feature: taxesForOrderCancelation

As Finance, when an order is canceled, I want item taxes to be calculated with original tax rates, so I am assured the correct amount of funds
	are returned to the customer even if tax rates have been changed in the interim.

Background: 

Given a store with an [exclusive] tax jurisdiction of [CA] 
	And with tax rates of
		| taxName | taxRate | taxRegion |
		| PST 	  | 7       | BC    	|
		| GST 	  | 5 		| CA		|
	
	And with shipping regions of
		| region | regionString |
		| Canada | [CA(BC)]		|
		
	And with shipping service levels of
		| region | shipping service level code | price |
		| Canada | 2 Business Days | 10.00 |
	
	And with products of
		| skuCode   | price  |
		| ETX-105PE | 599.00 |
	
	And with a default customer  
		
Scenario: Tax Calculation for Order Cancelation

Given the customer's shipping address is in
		| subCountry | country |
		| BC		 | CA	   |
	
	And the customer shipping method is [2 Business Days]
	
	And the customer purchases these items
		| quantity | skuCode   |
		| 1		   | ETX-105PE |  
	
When tax rates are changed to
		| taxName | taxRate | taxRegion |
		| PST 	  | 10      | BC		|
		| GST 	  | 10 		| CA  		|
		
	And the order is canceled

Then I expect that the tax journal should have purchase entries and reversal entries at the same tax rates as the purchase
		| journalType | transactionType | itemCode  | itemAmount | taxName | taxCode  | taxAmount | taxRate | taxJurisdiction | taxRegion |
		| purchase	  | Order		    | ETX-105PE | 599.00	 | PST	   | GOODS	  | 41.93	  | 0.07 	| CA			  | BC		  |
		| purchase	  | Order		    | ETX-105PE | 599.00	 | GST	   | GOODS	  | 29.95	  | 0.05    | CA			  | CA		  |	
		| purchase	  | Order			| SHIPPING	| 10.00		 | PST	   | SHIPPING | 0.70	  | 0.07    | CA			  | BC		  |	
		| purchase	  | Order			| SHIPPING	| 10.00		 | GST	   | SHIPPING | 0.50	  | 0.05    | CA			  | CA		  |	
		| reversal	  | Order Cancel	| ETX-105PE | -599.00	 | PST     | GOODS	  | -41.93	  | 0.07    | CA		      | BC		  |	
		| reversal	  | Order Cancel	| ETX-105PE | -599.00	 | GST     | GOODS	  | -29.95	  | 0.05    | CA		      | CA		  |	
		| reversal	  | Order Cancel	| SHIPPING	| -10.00	 | PST	   | SHIPPING | -0.70	  | 0.07    | CA		      | BC		  |	
		| reversal	  | Order Cancel	| SHIPPING	| -10.00	 | GST	   | SHIPPING | -0.50     | 0.05    | CA			  | CA		  |	