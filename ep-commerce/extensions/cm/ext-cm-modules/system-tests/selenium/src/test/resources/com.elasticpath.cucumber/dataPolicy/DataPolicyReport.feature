@smoketest @datapolicy
Feature: Data Policy Reporting

  Background:
    Given I sign in to CM as admin user

  Scenario Outline: Report for Customer with Data Points
    When I select following report options
      | reportType    | store   |
      | <report-type> | <store> |
    And I run Customer Personal Data report for customer <user-id>
    Then the User ID <user-id> is in the Customer Personal Data report
    And the Report Content shows following Data Points
      | Data Point Value                    |
      | policy_user2                        |
      | automation_data_policy_user2@ep.com |
      | automation                          |
      | 4567 Broadway Street                |

    Examples:
      | report-type            | store | user-id                             |
      | Customer Personal Data | Mobee | automation_data_policy_user2@ep.com |

  Scenario Outline: Report for Customer with No Data Points
    When I select following report options
      | reportType    | store   |
      | <report-type> | <store> |
    And I run Customer Personal Data report for customer <user-id>
    Then the User ID <user-id> is in the Customer Personal Data report
    And the Report is empty

    Examples:
      | report-type            | store | user-id                      |
      | Customer Personal Data | Mobee | harry.potter@elasticpath.com |

  Scenario Outline: Customer ID validation for Customer Personal Data report
    When I select following report options
      | reportType    | store   |
      | <report-type> | <store> |
    And I enter empty value in User ID
    Then I can see validation error messages for User ID

    Examples:
      | report-type            | store |
      | Customer Personal Data | Mobee |
