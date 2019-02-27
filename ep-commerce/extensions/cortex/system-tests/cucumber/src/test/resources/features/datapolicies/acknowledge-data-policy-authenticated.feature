@datapolicies
Feature: Registered users acknowledge the data policy

  Background:
    Given I set X-EP-Data-Policy-Segments header EU_Data_Policy
    And I have authenticated as a newly registered shopper
    And I access the data policies resource from root
    And I select the data policy Marketing Contact Information
    And I can access the data policy with the following fields:
      | data-policy-consent  | false                         |
      | policy-reference-key | DP12346                       |
      | policy-name          | Marketing Contact Information |

  Scenario: The data policy form is visible
    When I access the data policy form
    Then I can see the data policy with the following fields:
      | data-policy-consent  | false                         |
      | policy-reference-key | DP12346                       |
      | policy-name          | Marketing Contact Information |

  Scenario: Data policy consent is persisted for granting and revoking
    Given I access the data policy form
    When I post the following fields to the data policy form:
      | data-policy-consent | true |
    And I access the data policies resource from root
    And I select the data policy Marketing Contact Information
    Then I can see the data policy with the following fields:
      | data-policy-consent  | true                          |
      | policy-reference-key | DP12346                       |
      | policy-name          | Marketing Contact Information |
    When I access the data policies resource from root
    And I select the data policy Marketing Contact Information
    And I access the data policy form
    And I post the following fields to the data policy form:
      | data-policy-consent | false |
    And I access the data policies resource from root
    And I select the data policy Marketing Contact Information
    Then I can see the data policy with the following fields:
      | data-policy-consent  | false                         |
      | policy-reference-key | DP12346                       |
      | policy-name          | Marketing Contact Information |