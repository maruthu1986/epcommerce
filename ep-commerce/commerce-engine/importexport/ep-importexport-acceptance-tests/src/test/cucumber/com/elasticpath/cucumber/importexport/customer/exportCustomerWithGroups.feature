# language: en
@exportCustomerWithGroups
Feature: Export customers with assigned groups
  As Operations,
  I want to ensure exported customers retain their associations to assigned groups.

Scenario: Export Customers with assigned groups
  Given there is no existing customer in the system
    And the customer segment [CS123] has been created
    And the customer segment [CS444] has been created
    And the customer [Jay Johnson] has been created and assigned customer segments [CS123,CS444,PUBLIC]
    And the customer [John Smith] has been created and assigned customer segment [CS123,PUBLIC]
    And the customer [Hanna Rogers] has been created with no assigned customer segment
  When exporting customers with the importexport tool
   And the exported customers data is parsed
  Then the exported customer record [Jay Johnson] should include associations to customer segments [CS123,CS444,PUBLIC]
   And the exported customer record [John Smith] should include association to customer segment [CS123,PUBLIC]
   And the exported customer record [Hanna Rogers] should include association to customer segment [PUBLIC]
   And the exported manifest file should have an entry for customers
