@datapolicies
Feature: Data policy merge from an anonymous to a registered user

  Background:
    Given I set X-EP-Data-Policy-Segments header EU_Data_Policy
    And I login as a new public shopper
    And I access the data policies resource from root
    And I select the data policy Marketing Contact Information
    And I can access the data policy with the following fields:
      | data-policy-consent  | false                         |
      | policy-reference-key | DP12346                       |
      | policy-name          | Marketing Contact Information |

  Scenario: Consent acknowledgement merges from an anonymous to a registered shopper
    Given I access the data policy form
    And I post the following fields to the data policy form:
      | data-policy-consent | true |
    And I access the data policies resource from root
    And I select the data policy Marketing Contact Information
    And I can see the data policy with the following fields:
      | data-policy-consent  | true                          |
      | policy-reference-key | DP12346                       |
      | policy-name          | Marketing Contact Information |
    When I register and transition to a new shopper
    And I access the data policies resource from root
    And I select the data policy Marketing Contact Information
    Then I can see the data policy with the following fields:
      | data-policy-consent  | true                          |
      | policy-reference-key | DP12346                       |
      | policy-name          | Marketing Contact Information |