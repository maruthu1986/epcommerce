@tax
Feature: taxesForOrderElectronicShipmentCancellation

  As Finance, when an electronic shipment is placed, I do not want to see any tax entries for shipping.

  Background:

    Given a store with an [exclusive] tax jurisdiction of [CA]
    And with tax rates of
      | taxName	| taxRate	| taxRegion	|
      | PST		| 7			| BC		|
      | GST		| 5			| CA		|

    And with products of
      | skuCode	| price		| type		|
      | GC-100 	| 100.00	| digital	|

    And with a default customer

  Scenario: Tax Calculation for Electronic Shipment

    When the customer purchases these items
      | quantity | skuCode	|
      | 1		   | GC-100		|

    Then I expect that the tax journal should have purchase entries
      | journalType | transactionType | itemCode  | itemAmount | taxName | taxCode  | taxAmount | taxRate | taxJurisdiction | taxRegion |
      | purchase	  | Order		    | GC-100 	| 100.00	 | PST	   | GOODS	  | 7.00	  | 0.07 	| CA			  | BC		  |
      | purchase	  | Order		    | GC-100 	| 100.00	 | GST	   | GOODS	  | 5.00	  | 0.05    | CA			  | CA		  |
