@datapolicies
Feature: Data policy advisors on addresses

  Background:
    Given I set X-EP-Data-Policy-Segments header EU_Data_Policy
    And I login as a new public shopper

  Scenario: Data policy advisor on addresses when data policy not acknowledged
    Given I access the data policies resource from root
    And I select the data policy Saved Addresses
    And I can access a data policy with the following field:
      | data-policy-consent  | false           |
      | policy-reference-key | DP12347         |
      | policy-name          | Saved Addresses |
    When I get address form
    Then there are advisor messages with the following fields:
      | messageType | messageId               | debugMessage                                                      | linkedTo                              |
      | needinfo    | need.datapolicy.consent | Need user consent for data policy 'Marketing Contact Information' | datapolicies.data-policy-consent-form |
      | needinfo    | need.datapolicy.consent | Need user consent for data policy 'Saved Addresses'               | datapolicies.data-policy-consent-form |

  Scenario: All data policy advisors with the same advising data points disappear when the user accepts one of the data policies
    Given I access the data policies resource from root
    And I select the data policy Saved Addresses
    And I can access a data policy with the following field:
      | data-policy-consent  | false           |
      | policy-reference-key | DP12347         |
      | policy-name          | Saved Addresses |
    When I get address form
    Then there are advisor messages with the following fields:
      | messageType | messageId               | debugMessage                                                      | linkedTo                              |
      | needinfo    | need.datapolicy.consent | Need user consent for data policy 'Marketing Contact Information' | datapolicies.data-policy-consent-form |
      | needinfo    | need.datapolicy.consent | Need user consent for data policy 'Saved Addresses'               | datapolicies.data-policy-consent-form |
    When I access the data policies resource from root
    And I select the data policy Saved Addresses
    And I access the data policy form
    And I post the following fields to the data policy form:
      | data-policy-consent | true |
    And I get address form
    Then there is no advisor linked to datapolicies.data-policy-consent-form

  Scenario: No data policy advisor on addresses when no policy segments exist
    Given I remove the X-EP-Data-Policy-Segments header
    When I get address form
    Then there is no advisor linked to datapolicies.data-policy-consent-form