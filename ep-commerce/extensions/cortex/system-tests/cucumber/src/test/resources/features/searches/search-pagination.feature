@Searches
Feature: Search pagination feature

  Scenario: Verify paginated search links
    Given I am logged into scope searchbee as a public shopper
    And I search for keyword "movie"
    And there are 5 links of rel element
    And there are no previous links
    And there is a next link
    And I follow links next
    When there are 5 links of rel element
    And there is a previous link
    And there is a next link
    And I follow links next
    Then there are 2 links of rel element
    And there is a previous link
    And there are no next links

  Scenario: Number of search results returned is more than the custom pagination setting of 2
    Given I am logged into scope searchbee as a public shopper
    When I search for the keyword "movie" with page-size 2
    Then the field pagination contains value page-size=2
    And the field pagination contains value pages=6
    And the field pagination contains value results=12
    And the field pagination contains value results-on-page=2
    And there are 2 links of rel element

  Scenario: Number of search results returned is less than the custom pagination setting of 20
    Given I am logged into scope searchbee as a public shopper
    When I search for the keyword "movie" with page-size 20
    Then the field pagination contains value page-size=20
    And the field pagination contains value pages=1
    And the field pagination contains value results=12
    And the field pagination contains value results-on-page=12
    And there are 12 links of rel element

  Scenario: Empty space keyword results are successfully returned
    Given I am logged into scope searchbee as a public shopper
    When I search for the keyword " " with page-size 999
    Then the field pagination contains value page-size=999
    And the field pagination contains value pages=1
    And the field pagination contains value results=13
    And the field pagination contains value results-on-page=13
    And there are 13 links of rel element

  Scenario: POST with invalid values in optional field in request body
    Given I am logged into scope mobee as a public shopper
    When I POST to the search form the keyword "movie" with page-size 0
    Then the HTTP status is bad request
    When I POST to the search form the keyword "movie" with page-size -1
    Then the HTTP status is bad request
    When I POST to the search form with a 5000 char keyword
    Then the HTTP status is bad request

    #TODO implement expected behaviour determined by CUST-1189
  Scenario: When listPagination setting is set to 0 calling GET on the search form will return a 500 error
    Given I am logged into scope kobee as a public shopper
    When I follow the root searches link
    And I follow links keywordsearchform
    Then the HTTP status is server failure
