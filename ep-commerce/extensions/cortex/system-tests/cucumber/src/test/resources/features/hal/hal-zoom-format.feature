@HAL
Feature: HAL zoom format

  Background:
    Given I am logged in as a public shopper

  Scenario: Zoom contains HAL structure
    When I GET /navigations/mobee?zoom=element:items:element
    Then I should see the _embedded property
    And I should not see the _element property
    Then I inspect the zoom object _embedded.element[0]
    And the zoom object does not contain path links
    And the zoom object does not contain path _element
    And the zoom object does not contain path _items
    And the zoom object contains path _links
    And the zoom object contains path _embedded
    Then I inspect the zoom object _embedded.element[0]._embedded.items
    And the zoom object does not contain path links
    And the zoom object does not contain path _element
    And the zoom object does not contain path _items
    And the zoom object contains path _links
    And the zoom object contains path _embedded
    Then I inspect the zoom object _embedded.element[0]._embedded.items._embedded.element[0]
    And the zoom object does not contain path links
    And the zoom object does not contain path _element
    And the zoom object does not contain path _items
    And the zoom object does not contain path _embedded
    And the zoom object contains path _links

  Scenario: Zoom contains only self and action links
    When I GET /geographies/mobee/countries?zoom=element
    Then I inspect the zoom object _embedded.element[0]
    And the zoom object contains path _links
    And the zoom object contains path _links.self
    And the zoom object does not contain path _links.regions
    When I GET /navigations/mobee?zoom=element:items:element:addtowishlistform
    Then I inspect the zoom object _embedded.element[0]._embedded.items._embedded.element[0]._embedded.addtowishlistform
    And the zoom object contains path _links
    And the zoom object contains path _links.self
    And the zoom object contains path _links.addtodefaultwishlistaction

  Scenario: Zoom does not contain self link when omitted
    When I GET /navigations/mobee?zoom=element:items:element:addtowishlistform&format=zoom.noself
    Then I inspect the zoom object _embedded.element[0]._embedded.items._embedded.element[0]._embedded.addtowishlistform
    And the zoom object contains path _links
    And the zoom object contains path _links.addtodefaultwishlistaction
    And the zoom object does not contain path _links.self
