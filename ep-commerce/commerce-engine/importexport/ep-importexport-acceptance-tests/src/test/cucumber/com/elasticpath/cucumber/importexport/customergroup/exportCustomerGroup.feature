# language: en
@exportCustomerGroup
Feature: Export customer segments
  In order to archive customer segments for backup or data migration,
  As Operations,
  I want to export customer segments to the file system.

Scenario: Export Customer Segments
  Given the customer segment [CS123] has been created with enabled value of [TRUE]
    And the customer segment [CS444] has been created with enabled value of [TRUE]
    And the customer segment [CS789] has been created with enabled value of [FALSE]
  When exporting customer segments with the importexport tool
   And the exported customer segments data is parsed
  Then the exported customer segment records should include [CS123] with enabled value of [TRUE]
   And the exported customer segment records should include [CS444] with enabled value of [TRUE]
   And the exported customer segment records should include [CS789] with enabled value of [FALSE]
   And the exported manifest file should have an entry for customer segments

Scenario: Export Customer Segments with Customer Roles
  Given the customer segment [SUBSCRIBERS] has been created
    And the customer segment [MEMBERS] has been created with roles [ROLE_MEMBER]
    And the customer segment [PREMIUM_MEMBERS] has been created with roles [ROLE_MEMBER,ROLE_EXCLUSIVE_MEMBER]
   When exporting customer segments with the importexport tool
    And the exported customer segments data is parsed
   Then the exported customer segment records should include [SUBSCRIBERS] with no role
    And the exported customer segment records should include [MEMBERS] with roles [ROLE_MEMBER]
    And the exported customer segment records should include [PREMIUM_MEMBERS] with roles [ROLE_MEMBER,ROLE_EXCLUSIVE_MEMBER]
    And the exported manifest file should have an entry for customer segments
