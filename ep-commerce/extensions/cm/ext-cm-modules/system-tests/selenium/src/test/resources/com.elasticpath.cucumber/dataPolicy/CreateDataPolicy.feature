@smoketest @datapolicy
Feature: Create Data Policy

  Background:
    Given I sign in to CM as admin user

   Scenario Outline: Create new Active Data Policy
    Given I create a new Data Policy with following values
      | policy-name          | Test                |
      | policy-reference-key | Test                |
      | retention days       | 1                   |
      | state                | Active              |
      | data points          | Customer first name |
      | segment              | <POLICY_SEGMENT>    |
    And the newly created Data Policy exists in the list
    When I set request header X-EP-Data-Policy-Segments with value <POLICY_SEGMENT>
    And I authenticate as a public user with scope mobee
    Then I can see the newly created data policy in my profile with following details
      | data-policy-consent  | false |
      | policy-reference-key | Test  |
      | policy-name          |       |

     Examples:
      | POLICY_SEGMENT |
      | test_policy    |