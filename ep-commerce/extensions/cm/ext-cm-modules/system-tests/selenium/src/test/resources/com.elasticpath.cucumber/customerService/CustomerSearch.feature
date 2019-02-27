@smoketest @customerService @customer
Feature: Customer Search

  Background:
    Given I sign in to CM as CSR user

  Scenario Outline: Search for customer by email
    Given I go to Customer Service
    When I search for customer with email ID <email-id>
    Then I should see customer with email ID <email-id> in result list

    Examples:
      | email-id                     |
      | harry.potter@elasticpath.com |

  Scenario Outline: Search for customer by phone number
    Given I go to Customer Service
    And I search and open customer editor for email ID <EMAIL>
    And I update the phone number to <PHONE_NUMBER>
    When I search for customer by phone number <PHONE_NUMBER_SEARCH_VALUES>
    Then I should see customer with email ID <EMAIL> in result list

    Examples:
      | EMAIL                    | PHONE_NUMBER | PHONE_NUMBER_SEARCH_VALUES |
      | view_subs_user@itest.com | 6045556666   | 6045556666                 |