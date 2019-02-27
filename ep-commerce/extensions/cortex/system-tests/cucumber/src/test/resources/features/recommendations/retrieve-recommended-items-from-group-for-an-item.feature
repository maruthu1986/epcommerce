@Recommendations
Feature: Retrieve recommended items from groups for an item

  Background:
    Given I login as a public shopper

  Scenario Outline: Search for an item which has two recommendations for crosssell.
  Retrieve them and ensure they are displayed in correct order for an item.
    When I search for item name <ITEM_NAME>
    And I go to recommendations for an item
    When I retrieve the crosssell for this item
    Then I get the 2 recommended items
    And The ordering is correctly preserved <FIRST_ASSOCIATION_NAME>

    Examples:
      | ITEM_NAME                         | FIRST_ASSOCIATION_NAME |
      | multipleAssociationsSourceProduct | associatedProductTwo   |


  Scenario Outline: Search for an item which has two recommendations for upsell.
  Retrieve them and ensure they are displayed in correct order for an item.
    When I search for item name <ITEM_NAME>
    And I go to recommendations for an item
    When I retrieve the upsell for this item
    Then I get the 2 recommended items
    And The ordering is correctly preserved <FIRST_ASSOCIATION_NAME>

    Examples:
      | ITEM_NAME                         | FIRST_ASSOCIATION_NAME |
      | multipleAssociationsSourceProduct | associatedProductTwo   |


  Scenario Outline: Search for an item which has two recommendations for replacement.
  Retrieve them and ensure they are displayed in correct order for an item.
    When I search for item name <ITEM_NAME>
    And I go to recommendations for an item
    When I retrieve the replacement for this item
    Then I get the 2 recommended items
    And The ordering is correctly preserved <FIRST_ASSOCIATION_NAME>

    Examples:
      | ITEM_NAME                         | FIRST_ASSOCIATION_NAME |
      | multipleAssociationsSourceProduct | associatedProductTwo   |


  Scenario Outline: Search for an item which two recommendations for warranty.
  Retrieve them and ensure they are displayed in correct order for an item.
    When I search for item name <ITEM_NAME>
    And I go to recommendations for an item
    When I retrieve the warranty for this item
    Then I get the 2 recommended items
    And The ordering is correctly preserved <FIRST_ASSOCIATION_NAME>

    Examples:
      | ITEM_NAME                         | FIRST_ASSOCIATION_NAME |
      | multipleAssociationsSourceProduct | associatedProductTwo   |


  Scenario Outline: Search for an item two recommendations for accessory.
  Retrieve them and ensure they are displayed in correct order for an item.
    When I search for item name <ITEM_NAME>
    And I go to recommendations for an item
    When I retrieve the accessory for this item
    Then I get the 2 recommended items
    And The ordering is correctly preserved <FIRST_ASSOCIATION_NAME>

    Examples:
      | ITEM_NAME                         | FIRST_ASSOCIATION_NAME |
      | multipleAssociationsSourceProduct | associatedProductTwo   |


  Scenario Outline: Search for an item which has two recommendations for crosssell.
  Retrieve them using zoom.Ensure they are displayed in correct order for an item.
    When I search for item name <ITEM_NAME>
    And I go to recommendations for an item
    When I zoom <ZOOMPARAM> into the cross-sells for this item
    Then The zoom ordering is correctly preserved <FIRST_ASSOCIATION_NAME> and <SECOND_ASSOCIATION_NAME>

    Examples:
      | ITEM_NAME                         | ZOOMPARAM                          | FIRST_ASSOCIATION_NAME | SECOND_ASSOCIATION_NAME |
      | multipleAssociationsSourceProduct | ?zoom=crosssell:element:definition | associatedProductTwo   | associatedProductOne    |


  Scenario Outline: Cannot retrieve non-store-visible product through a recommendation
    When I search for item name <ITEM_NAME>
    And I go to recommendations for an item
    And I go to cross-sell for an item
    Then cross-sell item is not present

    Examples:
      | ITEM_NAME                                                      |
      | SourceProductWithCrossSellsAssocToNonStoreVisibleTargetProduct |