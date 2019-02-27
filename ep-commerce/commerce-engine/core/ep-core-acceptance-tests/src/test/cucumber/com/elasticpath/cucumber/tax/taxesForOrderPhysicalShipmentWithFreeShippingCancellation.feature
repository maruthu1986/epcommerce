@tax
Feature: taxesForOrderPhysicalShipmentWithFreeShippingCancellation

  As Finance, when a physical order shipment has free shipping I want to record shipping taxes for purchases and cancellations,
  so I can differentiate between a zero-valued tax assessment and a lack of a tax assessment.

  Background:

    Given a store with an [exclusive] tax jurisdiction of [CA]
    And with tax rates of
      | taxName	| taxRate	| taxRegion	|
      | PST		| 7			| BC		|
      | GST		| 5			| CA		|

    And with shipping regions of
      | region	| regionString	|
      | Canada	| [CA(BC)]		|

    And with shipping service levels of
      | region	| shipping service level code			| price	|
      | Canada	| Free Shipping		| 0.00	|

    And with products of
      | skuCode	| price		| type		|
      | ETX-105PE	| 599.00	| physical	|

    And with a default customer



  Scenario: Tax Calculation for Physical Order Shipment with Free Shipping

    Given the customer's shipping address is in
      | subCountry | country |
      | BC		 | CA	   |

    And the customer shipping method is [Free Shipping]

    When the customer purchases these items
      | quantity | skuCode   |
      | 1		   | ETX-105PE |

    Then I expect that the tax journal should have purchase entries
      | journalType | transactionType | itemCode  | itemAmount | taxName | taxCode  | taxAmount	| taxRate | taxJurisdiction | taxRegion	  |
      | purchase	  | Order		    | ETX-105PE | 599.00	 | PST	   | GOODS	  | 41.93		| 0.07    | CA				| BC		  |
      | purchase	  | Order		    | ETX-105PE | 599.00	 | GST	   | GOODS	  | 29.95		| 0.05    | CA				| CA		  |
      | purchase	  | Order			| SHIPPING	| 0.00		 | PST	   | SHIPPING | 0.00		| 0.07    | CA				| BC		  |
      | purchase	  | Order			| SHIPPING	| 0.00		 | GST	   | SHIPPING | 0.00		| 0.05    | CA				| CA		  |



  Scenario: Tax Calculation for Physical Order Shipment Cancellation with Free Shipping

    Given the customer's shipping address is in
      | subCountry | country |
      | BC		 | CA	   |

    And the customer shipping method is [Free Shipping]

    And the customer purchases these items
      | quantity | skuCode   |
      | 1		   | ETX-105PE |

    When the order physical shipment is canceled

    Then I expect that the tax journal should have purchase entries and reversal entries at the corresponding tax rates
      | journalType | transactionType | itemCode  | itemAmount | taxName | taxCode  | taxAmount	| taxRate | taxJurisdiction | taxRegion	  |
      | purchase	  | Order		    | ETX-105PE | 599.00	 | PST	   | GOODS	  | 41.93		| 0.07    | CA				| BC		  |
      | purchase	  | Order		    | ETX-105PE | 599.00	 | GST	   | GOODS	  | 29.95		| 0.05    | CA				| CA		  |
      | purchase	  | Order			| SHIPPING	| 0.00		 | PST	   | SHIPPING | 0.00		| 0.07    | CA				| BC		  |
      | purchase	  | Order			| SHIPPING	| 0.00		 | GST	   | SHIPPING | 0.00		| 0.05    | CA				| CA		  |
      | reversal	  | Order Cancel	| ETX-105PE | -599.00	 | PST     | GOODS	  | -41.93		| 0.07    | CA				| BC		  |
      | reversal	  | Order Cancel	| ETX-105PE | -599.00	 | GST     | GOODS	  | -29.95		| 0.05    | CA				| CA		  |
      | reversal	  | Order Cancel	| SHIPPING	| 0.00		 | PST	   | SHIPPING | 0.00		| 0.07    | CA				| BC		  |
      | reversal	  | Order Cancel	| SHIPPING	| 0.00		 | GST	   | SHIPPING | 0.00		| 0.05    | CA				| CA		  |
