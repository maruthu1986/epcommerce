@Promotions
Feature: Item Promotions
  Promotion related acceptance tests for promotions that trigger item incentives

  Scenario Outline: Retrieve single promotion that applies incentive: catalog discounts
    Given I login as a public shopper
    When I view an item <ITEM_NAME> that has a discount triggered by a promotion
 # the catalog item promotion
    Then the list of applied promotions contains promotion <PROMOTION>

    Examples:
      | ITEM_NAME                                        | PROMOTION                               |
      | Product With Catalog Promotion of 10 Percent Off | 10 Percent off acceptance test products |

  Scenario Outline: Item without promotions has empty list of promotions
    Given I login as a public shopper
    When I view an item <ITEM_NAME> that does not have promotion
    Then the list of applied promotions is empty

    Examples:
      | ITEM_NAME            |
      | productwithoutpromos |