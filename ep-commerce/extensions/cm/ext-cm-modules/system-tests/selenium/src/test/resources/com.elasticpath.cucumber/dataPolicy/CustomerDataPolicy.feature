@smoketest @datapolicy
Feature: Customer Data Policy

  Background:
    Given I sign in to CM as admin user

  Scenario Outline: View Customer Data Points for Active Data Policy
    Given I go to Customer Service
    And I search and open customer editor for email ID <EMAIL>
    And I select Customer Data Policies tab in the Customer Editor
    When I view Data Points for Data Policy <DATA-POLICY-NAME>
    Then the following data points captured correct customer data
      | Customer Billing Address Street1 | 4567 Broadway Street |
      | Customer email                   | <EMAIL>              |
      | Customer first name              | policy_user2         |
      | Customer last name               | automation           |

    Examples:
      | EMAIL                               | DATA-POLICY-NAME           |
      | automation_data_policy_user2@ep.com | Automation Active Policy 2 |

  Scenario Outline: Delete Customer Data Points
    Given I go to Customer Service
    And I search and open customer editor for email ID <EMAIL>
    And I select Customer Data Policies tab in the Customer Editor
    When I Delete Data Points for Data Policy <DATA-POLICY-NAME>
    Then the following data points remain captured
      | Customer email | <EMAIL> |
    And Data Point Values are empty for following Removable Data Points
      | Customer first name |
      | Customer last name  |
    And Data Point Values are set to Hyphen for following Removable Data Points
      | Customer Billing Address Street1 |
    Examples:
      | EMAIL                               | DATA-POLICY-NAME           |
      | automation_data_policy_user1@ep.com | Automation Active Policy 2 |

  Scenario Outline: View Disabled Data Policy for Customer
    Given I Disabled existing data policy <DATA-POLICY-NAME> where consent is given by customer <EMAIL>
    And I go to Customer Service
    And I search and open customer editor for email ID <EMAIL>
    When I select Customer Data Policies tab in the Customer Editor
    Then Disabled Data Policy <DATA-POLICY-NAME> should not be visible
    When I click on Show Disabled Data Policies
    Then I should see Disabled Data Policy <DATA-POLICY-NAME>

    Examples:
      | EMAIL                               | DATA-POLICY-NAME           |
      | automation_data_policy_user2@ep.com | Automation Active Policy 1 |

