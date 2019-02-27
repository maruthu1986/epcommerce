@smoketest @customerService @profile
Feature: Add address and update name

  Scenario Outline: Update customer name
    Given I have an order for scope <scope> with following skus
      | skuCode      | quantity |
      | <sku-code-1> | 1        |
    And I sign in to CM as CSR user
    And I go to Customer Service
    And I search and open order editor for the latest order
    And I open Customer Profile
    When I update first name <new-first-name> and last name <new-last-name> for the customer
    Then I should see the updated first name <new-first-name> and last name <new-last-name>

    Examples:
      | scope | sku-code-1              | new-first-name | new-last-name |
      | mobee | handsfree_shippable_sku | Jeff           | Vader         |

  Scenario Outline: Add new address
    Given I have an order for scope <scope> with following skus
      | skuCode      | quantity |
      | <sku-code-1> | 1        |
    And I sign in to CM as CSR user
    And I go to Customer Service
    And I search and open order editor for the latest order
    And I open Customer Profile Addresses tab
    And I click Add Address button
    When I add new address with the following values
      | first name     | Jeff          |
      | last name      | Vader         |
      | address line 1 | 751 Pike Rd   |
      | city           | Woodstock     |
      | state          | Alabama       |
      | zip            | 35188         |
      | country        | United States |
      | phone          | 616 2323231   |
    Then the new address should exist in the address list

    Examples:
      | scope | sku-code-1              |
      | mobee | handsfree_shippable_sku |
