@NavigationLookup @Lookups
Feature: Lookup for an Navigation Item

  Scenario: Lookup a correct category code and verify the navigation item exists
    Given I am logged into scope mobee as a public shopper
    When I look up navigation item with category code Movies
    Then the category Movies that has no sub-category is at the expected category level and has items

  Scenario: Lookup a correct sub-category code and verify the navigation item exists
    Given I am logged into scope mobee as a public shopper
    When I look up navigation item with category code AndroidGames
    Then the category Android Games is at the sub-category level and has items

  Scenario: Lookup a parent category that has sub-category and verify the navigation item exists
    Given I am logged into scope mobee as a public shopper
    When I look up navigation item with category code Games
    Then the parent category Games that has sub-category is at the expected category level and has items

  Scenario: Lookup an Invalid Category Code
    Given I am logged into scope mobee as a public shopper
    When I look up navigation item that is invalid with category code invalid-code
    Then lookup fails with status not found

  Scenario: Lookup Category code for a registered shopper
    Given I login as a registered shopper
    When I look up navigation item with category code Movies
    Then the category Movies that has no sub-category is at the expected category level and has items

  Scenario: User can only look up category within logged in store
    Given I am logged into scope searchbee as a public shopper
    When I look up navigation item with category code Music
    Then I should see navigation details has display-name Music
    When I am logged into scope mobee as a public shopper
    And I look up navigation item that belongs to another scope with category code Music
    Then lookup fails with status not found