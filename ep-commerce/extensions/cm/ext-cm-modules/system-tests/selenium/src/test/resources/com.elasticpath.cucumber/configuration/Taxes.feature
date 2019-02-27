@smoketest @configuration @taxes
Feature: Taxes - Tax Codes, Tax Jurisdiction, Tax Values

  Background:
    Given I sign in to CM as admin user
    When I go to Configuration

  Scenario: Create edit and delete a Tax Code
    When I navigate to Tax Codes
    And I create a Tax Code named testTaxCode
    Then the new tax code should exist in the list
    When I edit a Tax Code named newTestTaxCode
    Then the new tax code should exist in the list
    When I delete newly created Tax Code
    Then the newly created tax code no longer exists

  Scenario: Creating editing and deleting a Tax Jurisdiction
    When I create a Tax Jurisdiction with the following values
      | country      | China       |
      | method       | Exclusive   |
      | taxName      | testGST     |
      | addressField | Sub Country |
    Then the created Tax Jurisdiction country China should exist in the list
    When I edit the Tax Display Name testingTAXES in Tax Jurisdiction country China of the Tax Name testGST
    Then the edited Tax Jurisdiction country China should exist in the list
    When I remove tax name testGST for Tax Jurisdiction country China
    Then the removed Tax Name testGST should no longer exists in Tax Jurisdiction Country China
    When I delete newly created Tax Jurisdiction country China
    Then the deleted Tax Jurisdiction country China should no longer exist in list

  @cleanupTaxJurisdiction
  Scenario: Adding and removing Tax Values
    Given I have a Tax Jurisdiction with the following values
      | country      | China       |
      | method       | Exclusive   |
      | taxName      | testGST     |
      | addressField | Sub Country |
    When I go to Tax Values
    And I add an sub country tax name GSTPST for Tax Jurisdiction country China
    Then the newly created sub country Tax Name exists in the list from the Country China
    When I delete newly created Tax Value for country China
    Then the newly created sub country Tax Name doesn't in the list from the Country China
