@Items

Feature: Items with options

  Background:
    Given I am logged in as a public shopper

  Scenario Outline: Items can be configured by selecting options
    Given a product with items having codes <ITEM_1_CODE> and <ITEM_2_CODE> distinguished by option <OPTION_KEY>
    And item with code <ITEM_1_CODE> has <OPTION_KEY> value <ITEM_1_OPTION_VALUE>
    And item with code <ITEM_2_CODE> has <OPTION_KEY> value <ITEM_2_OPTION_VALUE>
    When I view the item definition for item with code <ITEM_1_CODE>
    And I select option <OPTION_KEY> value <ITEM_2_OPTION_VALUE>
    Then I am presented with item having <ITEM_2_CODE>

    Examples:
      | ITEM_1_CODE  | ITEM_2_CODE  | OPTION_KEY    | ITEM_1_OPTION_VALUE | ITEM_2_OPTION_VALUE |
      | tt64464fn_hd | tt64464fn_sd | Video Quality | High Definition     | Standard Definition |
      | tt64464fn_sd | tt64464fn_hd | Video Quality | Standard Definition | High Definition     |


