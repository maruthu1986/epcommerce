@Recommendations
Feature: Retrieve recommendation groups for an item

  Background:
    Given I login as a public shopper

  Scenario Outline: Can retrieve recommendations when recommendation groups exist.
    When I search for item name <ITEM_NAME>
    And I go to recommendations for an item
    Then I get all the recommendation groups that exist

    Examples:
      | ITEM_NAME |
      | Twilight  |