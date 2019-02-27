@HAL
Feature: HAL format

  Background:
    Given I am logged in as a public shopper

  Scenario: Check the HAL format in root
    When I open root in HAL format
    Then I should not see the self property
    And I should see the messages property
    And I should see a list of links mapped to _links and not to links
    And each link in the list should be in HAL format
    And the list should exactly contain the following links
      | data-policies   |
      | lookups         |
      | searches        |
      | defaultprofile  |
      | navigations     |
      | defaultwishlist |
      | self            |
      | defaultcart     |
      | newaccountform  |

  Scenario: Check the HAL format in navigations
    When I open navigations in HAL format
    Then I should not see the self property
    And I should see the messages property
    And I should see a list of links mapped to _links and not to links
    And I should see the self link in HAL format
    And I should see an array of links mapped to element
    And each link in the element array should be in HAL format

  Scenario: Check the HAL format in order
    When I open order in HAL format
    Then I should not see the self property
    And I should see the messages property
    And there are advisor messages with the following fields:
      | messageType | messageId            | debugMessage                              | linkedTo                   |
      | needinfo    | cart.empty           | Shopping cart is empty.                   |                            |
      | needinfo    | need.email           | Customer email address must be specified. | orders.email-info          |
      | needinfo    | need.billing.address | Billing address must be specified.        | orders.billingaddress-info |
    And I should see a list of links mapped to _links and not to links
    And I should see the self link in HAL format

