#TODO: investigate way to test time fields
@Navigations

Feature: Retrieve Navigation nodes and child nodes

  Background:
    Given I am logged in as a public shopper

  Scenario: Can retrieve navigation details
    When I open the navigation category Games
    Then the navigation node attributes contain
      | name             | display name         | display value                                                            |
      | catReleaseDate   | Release Date         | 2012                                                                     |
      | catDescription   | Category Description | This category contains listing of all Games titles                       |
      | catSubCategories | Subcategories        | Mobile Games, Video Games, 憤怒的小鳥, 怒っている鳥, नाराज पक्षियों, پرندگان عصبانی |
      | catReleased      | Released             | True                                                                     |
      | catRating        | Category Rating      | 8.25                                                                     |
      | catTotalItems    | Total items          | 5                                                                        |
      | catLastModified  | Last modified        | 2012                                                                     |
      | catName          | Name                 | All <b><i>Games</i></b>                                                  |

  Scenario: Media attributes and details with no value are not displayed
    Given MobileGames is missing a value for catReleased
    And MobileGames is missing a value for catLastModified
    And MobileGames is missing a value for catImage
    And MobileGames is missing a value for catTheme
    When I open the navigation subcategory Games -> MobileGames
    Then the field details does not contain value catReleased
    And the field details does not contain value catLastModified
    And the field details does not contain value catImage
    And the field details does not contain value catTheme

  Scenario Outline: Node displays detail values based on requested language
    Given category Movies has an attribute catName with value <CATEGORY_NAME> in language <LANGUAGE>
    When I request the navigation definition of Movies in language <LANGUAGE>
    Then the navigation node array field details contains
      | name    | value           |
      | catName | <CATEGORY_NAME> |

    Examples:
      | LANGUAGE | CATEGORY_NAME |
      | EN       | Movies        |
      | FR       | Cinéma        |

  Scenario: Navigations node with no attributes associated with it will not display an details field
    Given category Smartphones has no attributes
    When I open the navigation category Smartphones
    Then the field details does not exist

  Scenario: Verify links on node with child and no parent
    Given category Games has a subcategory and no parent category
    When I open the navigation category Games
    Then there are no parent links
    And there is a child link
    And there is a top link

  Scenario: Verify links on node with multiple children
    Given the category Games has 2 subcategories
    When I open the navigation category Games
    Then there are 2 links of rel child

  Scenario: Verify links on node with no parent and no child
    Given the category Smartphones is a top level category with no subcategories
    When I open the navigation category Smartphones
    Then there are no parent links
    And there are no child links
    And there is a top link

  Scenario: Verify links on node with parent and child
    Given the category Mobile games has a parent category and a subcategory
    When I open the navigation subcategory Games -> MobileGames
    Then there is a parent link
    And there is a child link
    And there is a top link

  Scenario: Verify links on node with parent and no child
    Given the category IPhoneGames has a parent category and no subcategories
    When I open the navigation subcategory Games -> MobileGames -> IPhoneGames
    Then there is a parent link
    And there are no child links
    And there is a top link
    And I follow links parent
    And the field name has value MobileGames

  Scenario Outline: Nodes have links to items
    Given the category <CATEGORY> contains <NUMBER_OF_ITEMS> items
    When I open the navigation category <CATEGORY>
    Then there is an items link
    And the items link contains <NUMBER_OF_ITEMS> elements

    Examples:
      | CATEGORY        | NUMBER_OF_ITEMS |
      | GiftCertificate | 1               |
      | Games           | 0               |

  Scenario Outline: Navigate to items in node
    Given the category <CATEGORY> contains the item <ITEM>
    When I open the navigation category <CATEGORY>
    And I follow links items
    Then there is an item with display-name <ITEM>

    Examples:
      | CATEGORY | ITEM   |
      | Movies   | Avatar |

  Scenario Outline: Items in node do not include subnode items
    Given the item <SUBNODE_ITEM> belongs to a subcategory of MobileGames
    When I open the navigation subcategory Games -> MobileGames
    And I follow links items
    Then there is not an item with display-name <SUBNODE_ITEM>

    Examples:
      | SUBNODE_ITEM    |
      | Super Game Pack |

  Scenario Outline: Items in node do not include parent items
    Given the item <SUBNODE_ITEM> belongs to a subcategory of MobileGames
    When I open the navigation subcategory Games -> MobileGames -> IPhoneGames
    And I follow links items
    Then there is not an item with display-name <PARENT_ITEM>

    Examples:
      | PARENT_ITEM   |
      | Tetris Heaven |

  Scenario: Featured items are in the specified order
    Given featured items are configured for the category Movies
    When I open the navigation category Movies
    And I follow links items
    Then the items are listed in the follow order
      | Die Hard      |
      | Casablanca    |
      | Avatar        |
      | Movie Deal    |
      | Sleepy Hallow |

  Scenario: Featured items show up first
    Given featured items are configured for the category IPhoneGames
    When I open the navigation subcategory Games -> MobileGames -> IPhoneGames
    And I follow links items
    Then the items are listed in the follow order
      | Super Game Pack  |
      | Wing Warrior     |
      | Little Adventure |

  Scenario Outline: Does not display items that are not scope visible
    Given that <NOT_SCOPE_VISIBLE_PRODUCT> does not belong to the current scope
    When I open the navigation subcategory Games -> MobileGames -> AndroidGames
    And I follow links items
    Then there is not an item with display-name <NOT_SCOPE_VISIBLE_PRODUCT>

    Examples:
      | NOT_SCOPE_VISIBLE_PRODUCT        |
      | Angry Birds Seasons - Valentines |