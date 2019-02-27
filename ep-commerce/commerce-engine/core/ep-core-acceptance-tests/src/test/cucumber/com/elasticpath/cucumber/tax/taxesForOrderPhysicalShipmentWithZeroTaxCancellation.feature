@tax
Feature: taxesForOrderPhysicalShipmentWithZeroTaxCancellation

  As Finance, when a tax rate is zero I want to record physical shipping taxes for purchases and cancellations,
  so I can differentiate between a zero-valued tax assessment and a lack of a tax assessment.

  Background:

    Given a store with an [exclusive] tax jurisdiction of [CA]
    And with tax rates of
      | taxName	| taxRate	| taxRegion	|
      | PST		| 0			| AB		|
      | GST		| 5			| CA		|

    And with shipping regions of
      | region	| regionString	|
      | Canada	| [CA(AB)]		|

    And with shipping service levels of
      | region	| shipping service level code			| price	|
      | Canada	| 2 Business Days	| 7.00  |

    And with products of
      | skuCode	| price		| type		|
      | ETX-105PE	| 599.00	| physical	|

    And with a default customer



  Scenario: Tax Calculation for Order Shipment with Zero Tax

    Given the customer's shipping address is in
      | subCountry | country |
      | AB		 | CA	   |

    And the customer shipping method is [2 Business Days]

    When the customer purchases these items
      | quantity | skuCode   |
      | 1		   | ETX-105PE |

    Then I expect that the tax journal should have purchase entries
      | journalType | transactionType | itemCode  | itemAmount | taxName | taxCode  | taxAmount	| taxRate | taxJurisdiction | taxRegion |
      | purchase	  | Order		    | ETX-105PE | 599.00	 | PST	   | GOODS	  | 0.00		| 0.00    | CA			  | AB		  |
      | purchase	  | Order		    | ETX-105PE | 599.00	 | GST	   | GOODS	  | 29.95		| 0.05    | CA			  | CA		  |
      | purchase	  | Order			| SHIPPING	| 7.00		 | PST	   | SHIPPING | 0.00		| 0.00    | CA			  | AB		  |
      | purchase	  | Order			| SHIPPING	| 7.00		 | GST	   | SHIPPING | 0.35		| 0.05    | CA			  | CA		  |



  Scenario: Tax Calculation for Order Shipment Cancellation with Zero Tax

    Given the customer's shipping address is in
      | subCountry | country |
      | AB		 | CA	   |

    And the customer shipping method is [2 Business Days]

    And the customer purchases these items
      | quantity | skuCode   |
      | 1		   | ETX-105PE |

    When the order physical shipment is canceled

    Then I expect that the tax journal should have purchase entries and reversal entries at the corresponding tax rates
      | journalType | transactionType | itemCode  | itemAmount | taxName | taxCode  | taxAmount	| taxRate | taxJurisdiction	| taxRegion	|
      | purchase	  | Order		    | ETX-105PE | 599.00	 | PST	   | GOODS	  | 0.00		| 0.00    | CA				| AB		|
      | purchase	  | Order		    | ETX-105PE | 599.00	 | GST	   | GOODS	  | 29.95		| 0.05    | CA				| CA		|
      | purchase	  | Order			| SHIPPING	| 7.00		 | PST	   | SHIPPING | 0.00		| 0.00    | CA				| AB		|
      | purchase	  | Order			| SHIPPING	| 7.00		 | GST	   | SHIPPING | 0.35		| 0.05    | CA				| CA		|
      | reversal	  | Order Cancel	| ETX-105PE | -599.00	 | PST     | GOODS	  | 0.00		| 0.00    | CA				| AB		|
      | reversal	  | Order Cancel	| ETX-105PE | -599.00	 | GST     | GOODS	  | -29.95		| 0.05    | CA				| CA		|
      | reversal	  | Order Cancel	| SHIPPING	| -7.00		 | PST	   | SHIPPING | 0.00		| 0.00    | CA				| AB		|
      | reversal	  | Order Cancel	| SHIPPING	| -7.00		 | GST	   | SHIPPING | -0.35		| 0.05    | CA				| CA		|
