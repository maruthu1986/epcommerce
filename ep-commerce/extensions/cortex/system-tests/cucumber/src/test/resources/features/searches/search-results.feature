@Searches
Feature: Search by item name to verify the shown item code is correct

  Scenario Outline: Perform item search to ensure search displays correct item code and item name
    Given I am logged in as a public shopper
    When I search for item name <ITEMNAME>
    Then the item code is <ITEMCODE>
    When I follow a link back to the item
    Then I should see item name is <ITEMNAME>

    Examples:
      | ITEMNAME                  | ITEMCODE                      |
      | Product With No Discounts | product_with_no_discounts_sku |

  Scenario Outline: Search returns a product if the keyword is specified in a product's name
    Given I am logged in as a public shopper
    When I search for keyword "<PRODUCT_NAME>"
    Then there is an item with display-name <PRODUCT_NAME>

    Examples:
      | PRODUCT_NAME  |
      | Sleepy Hallow |

  Scenario Outline: Search returns a configured item if its product's name is specified in the keyword
    Given I am logged in as a public shopper
    When I search for keyword "<CONFIGURED_PRODUCT_NAME>"
    Then there is an item with display-name <CONFIGURED_PRODUCT_NAME>

    Examples:
      | CONFIGURED_PRODUCT_NAME |
      | Finding Nemo            |

  Scenario: Search returns a product if the keyword is specified in a product's attributes
    Given I am logged in as a public shopper
    When I search for keyword "Portuguese"
    Then the element list contains items with display-names
      | The Portuguese Bun |
      | Sleepy Hallow      |

  Scenario: Search returns a bundle if the keyword is specified in a bundle component's product name
    Given I am logged in as a public shopper
    When I search for keyword "htc"
    Then the element list contains items with display-names
      | HTC Evo 4G         |
      | SmartPhones Bundle |

  Scenario Outline: Search should not return products that have scope visible set to false
    Given I am logged in as a public shopper
    When I search for keyword "<OUT_OF_SCOPE_PRODUCT>"
    Then the field pagination contains value pages=1
    And the field pagination contains value results=0
    And the field pagination contains value results-on-page=0
    And there are 0 links of rel element

    Examples:
      | OUT_OF_SCOPE_PRODUCT      |
      | Motorola Wireless Headset |


  Scenario Outline: Search should not return products that have not sold separately set to true
    Given I am logged in as a public shopper
    When I search for keyword "<NOT_SOLD_SEPARATELY_PRODUCT>"
    Then the field pagination contains value pages=1
    And the field pagination contains value results=0
    And the field pagination contains value results-on-page=0
    And there are 0 links of rel element

    Examples:
      | NOT_SOLD_SEPARATELY_PRODUCT |
      | Motorola Wireless Headset   |

      #TODO review this test as ordering is not consistant
  @notready
  Scenario: Search results are ordered
    Given I am logged in as a public shopper
    When I search for keyword "bundle"
    Then the items are listed in the follow order
      | Extreme Movie Bundle |
      | Super Bundle         |
      | Top Level Bundle     |
      | RentMovieLowTVCombo  |
      | Old Movies           |
    And I follow links next
    Then the items are listed in the follow order
      | Series Mega Bundle    |
      | Just Released Movies  |
      | Rent Movies Bundle    |
      | Movie Classics Bundle |
      | Dynamic Bundle        |