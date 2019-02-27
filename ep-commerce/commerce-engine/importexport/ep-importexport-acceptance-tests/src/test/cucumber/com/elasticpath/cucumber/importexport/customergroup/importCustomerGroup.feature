# language: en
@importCustomerGroup
Feature: Import customer segments
  In order to migrate customer segments from an archive,
  As Operations,
  I want to import customer segments from the file system.

Scenario: Import Customer Segments
  Given the customer segment [CS123] has been created with enabled value of [FALSE]
    And the customer group import data has been emptied out
    And the customer segment [CS123] is included in the import data with enabled value of [TRUE]
    And the customer segment [CS444] is included in the import data with enabled value of [TRUE]
    And the customer segment [CS789] is included in the import data with enabled value of [FALSE]
  When importing customer segments with the importexport tool
  Then the customer segment [CS123] should have an enabled value of [TRUE]
   And the customer segment [CS444] should have an enabled value of [TRUE]
   And the customer segment [CS789] should have an enabled value of [FALSE]

Scenario: Import Customer Segments with Customer Roles
  Given the customer segment [SUBSCRIBERS] has been created with role [ROLE_MEMBER]
    And the customer segment [PREMIUM_MEMBERS] has been created with role [ROLE_MEMBER]
    And the customer group import data has been emptied out
    And the customer segment [SUBSCRIBERS] is included in the import data with no role
    And the customer segment [MEMBERS] is included in the import data with roles [ROLE_MEMBER]
    And the customer segment [PREMIUM_MEMBERS] is included in the import data with roles [ROLE_MEMBER,ROLE_EXCLUSIVE_MEMBER]
  When importing customer segments with the importexport tool
  Then the customer segment [SUBSCRIBERS] should have no role
   And the customer segment [MEMBERS] should have roles [ROLE_MEMBER]
   And the customer segment [PREMIUM_MEMBERS] should have roles [ROLE_MEMBER,ROLE_EXCLUSIVE_MEMBER]
