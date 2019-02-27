@datapolicies
Feature: Access Data Policies

  Background:
    Given I set X-EP-Data-Policy-Segments header EU_Data_Policy, Global_Data_Policy
    And I login as a new public shopper

  Scenario: Data policies displayed when segment header included as an anonymous shopper
    Given the following data policies are assigned to data policy segment EU_Data_Policy, Global_Data_Policy
      | Marketing Contact Information |
      | Saved Addresses               |
      | Order Information             |
    When I access the data policies resource from root
    Then the following expected data policies are visible in my profile
      | Marketing Contact Information |
      | Saved Addresses               |
      | Order Information             |

  Scenario: No data policies provided without segment header
    Given I remove the X-EP-Data-Policy-Segments header
    When I access the data policies resource from root
    Then I should not see any data policies

  Scenario: No data policies provided when scope has not enabled data policies
    When I access the data policies resource from a scope with no data polices enabled
    Then I should not see any data policies

  Scenario: Access the data policies endpoint from profile as a registered shopper
    Given the following data policies are assigned to data policy segment EU_Data_Policy, Global_Data_Policy
      | Marketing Contact Information |
      | Saved Addresses               |
      | Order Information             |
    And I have authenticated as a newly registered shopper
    When I view my profile
    And I can follow a link to data policies
    Then the following expected data policies are visible in my profile
      | Marketing Contact Information |
      | Saved Addresses               |
      | Order Information             |

  Scenario: Access individual data policy information from the data policies endpoint
    When I access the data policies resource from root
    And I select the data policy Marketing Contact Information
    Then I can see a data policy with the following field:
      | data-policy-consent  | false                         |
      | policy-reference-key | DP12346                       |
      | policy-name          | Marketing Contact Information |

  Scenario: Accessing the data point endpoint without segments header returns not found
    Given I access the data policies resource from root
    And I select the data policy Marketing Contact Information
    And I can access a data policy with the following field:
      | data-policy-consent  | false                         |
      | policy-reference-key | DP12346                       |
      | policy-name          | Marketing Contact Information |
    When I attempt to access the data policy after removing the X-EP-Data-Policy-Segments header
    Then the HTTP status code is 404

  Scenario Outline: Data Policy not visible when in Draft state
    Given the following data policies with segment EU_Data_Policy are in Draft state
      | <POLICY> |
    When I access the data policies resource from root
    Then the following list of data policies are not visible
      | <POLICY> |
    Examples:
      | POLICY                                 |
      | Required Registration Personal Details |

  Scenario Outline: Data Policy not visible when in Disabled state
    Given the following data policies with segment disable_policy are in Disabled state
      | <POLICY> |
    And I set X-EP-Data-Policy-Segments header disable_policy
    And I login as a new public shopper
    When I access the data policies resource from root
    Then the following list of data policies are not visible
      | <POLICY> |
    Examples:
      | POLICY                    |
      | Automation Disable Policy |