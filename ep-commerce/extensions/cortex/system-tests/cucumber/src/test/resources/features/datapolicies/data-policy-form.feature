@datapolicies
Feature: Data policy form validation

  Background:
    Given I set X-EP-Data-Policy-Segments header EU_Data_Policy
    And I login as a new public shopper
    And I access the data policies resource from root
    And I select the data policy Marketing Contact Information
    And I can access the data policy with the following fields:
      | data-policy-consent  | false                         |
      | policy-reference-key | DP12346                       |
      | policy-name          | Marketing Contact Information |

  Scenario: Cannot access data policy form without the data policy segments header
    Given I remove the X-EP-Data-Policy-Segments header
    When I access the data policy form
    Then the HTTP status code is 404

  Scenario: Cannot grant consent to a data policy without the data policy segments header
    Given I access the data policy form
    And I remove the X-EP-Data-Policy-Segments header
    When I post the following fields to the data policy form:
      | data-policy-consent | true |
    Then the HTTP status code is 404

  Scenario: Cannot change other fields on the data policy consent form
    Given I access the data policy form
    When I post the following fields to the data policy form:
      | policy-reference-key | INVALID |
      | policy-name          | INVALID |
    Then the HTTP status code is 400
    And I access the data policies resource from root
    And I select the data policy Marketing Contact Information
    And I can see the data policy with the following fields:
      | data-policy-consent  | false                         |
      | policy-reference-key | DP12346                       |
      | policy-name          | Marketing Contact Information |

  Scenario: Invalid consent values are not persisted
    Given I access the data policy form
    When I post the following fields to the data policy form:
      | data-policy-consent  | INVALID |
      | policy-reference-key | INVALID |
      | policy-name          | INVALID |
    And I access the data policies resource from root
    And I select the data policy Marketing Contact Information
    Then I can see the data policy with the following fields:
      | data-policy-consent  | false                         |
      | policy-reference-key | DP12346                       |
      | policy-name          | Marketing Contact Information |