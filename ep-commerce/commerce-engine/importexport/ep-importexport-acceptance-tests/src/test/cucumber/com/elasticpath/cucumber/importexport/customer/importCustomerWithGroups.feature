# language: en
@importCustomerWithGroups
Feature: Import customer segments
  As Operations,
  I want to ensure customer group assignments are imported with customers.

Scenario: Import Customers with assigned groups, default PUBLIC group is automatically assigned
  Given there is no existing customer in the system
    And the customer segment [PUBLIC] exists
    And the customer segment [CS123] has been created
    And the customer segment [CS444] has been created
    And the customer [Jay Johnson] has been created and assigned customer segment [CS444]
    And the customer [John Smith] has been created with no assigned customer segment
    And the customer [Hanna Rogers] has been created and assigned customer segment [CS444]
    And the customer import data has been emptied out
    And the customer [Jay Johnson] is included in the import data with assigned customer segments [CS123,CS444]
    And the customer [John Smith] is included in the import data with assigned customer segments [CS123]
    And the customer [Hanna Rogers] is included in the import data with no assigned customer segment
    And the customer [Alice Cooper] is included in the import data with assigned customer segments [CS123]
  When importing customers with the importexport tool
  Then the customer [Jay Johnson] should be assigned to customer segments [PUBLIC,CS123,CS444]
   And the customer [John Smith] should be assigned to customer segments [PUBLIC,CS123]
   And the customer [Hanna Rogers] should be assigned to customer segments [PUBLIC]
   And the customer [Alice Cooper] should be assigned to customer segments [PUBLIC,CS123]
