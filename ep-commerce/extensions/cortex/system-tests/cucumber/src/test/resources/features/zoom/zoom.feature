@Zoom
Feature: Zoom

  Background:
    Given I have authenticated as a newly registered shopper
    And I add item with code tt0970179_sku to my cart with quantity 1

  Scenario: Zoom only contains specified path elements
    Given I GET /navigations/mobee?zoom=element:items:element
    When I inspect the zoom object _element[0]
    Then the zoom object does not contain path self.uri
    And the zoom object does not contain path self.type
    And the zoom object does not contain path attributes
    And the zoom object does not contain path name
    And the zoom object does not contain path links
    When I inspect the zoom object _element[0]._items[0]._element[0]
    Then the zoom object contains path self.uri
    And the zoom object contains path self.type
    And the zoom object contains path links

  Scenario: Zoom does not contain self when not omitted
    Given I GET /navigations/mobee?zoom=element:items:element&format=zoom.noself
    When I inspect the zoom object _element[0]._items[0]._element[0]
    Then the zoom object does not contain path self

  Scenario: Zoom contains standard links when requested
    Given I GET /navigations/mobee?zoom=element:items:element&format=standardlinks
    When I inspect the zoom object _element[0]._items[0]._element[0]
    Then the zoom object does not contain path links[0].uri
    And the zoom object does not contain path links[0].rev
    And the zoom object contains path links[0].href
    And the zoom object contains path links[0].type
    And the zoom object contains path links[0].rel

  Scenario: Zoom is case Insensitive
    Given I GET /navigations/mobee?zoom=element
    And save the zoom response
    When I GET /navigations/mobee?zoom=eLeMeNt
    Then the response is identical to the saved response

  Scenario Outline: Single zoom object is identical to non-zoomed object
    Given I GET /geographies/mobee/countries?zoom=element&format=<FORMAT>
    And I inspect the zoom object _element[0]
    And save the zoomed response
    When I GET /geographies/mobee/countries
    And I specify the <FORMAT> and follow the link element
    Then In the given format <FORMAT> the response is identical to the saved response

    Examples:
      | FORMAT                    |
      |                           |
      | standardlinks             |
      | zoom.noself               |
      | standardlinks,zoom.noself |
    

  Scenario: Multiple zoom object is identical to non-zoomed object
    Given I GET /carts/mobee/default?zoom=lineitems,total
    And I inspect the zoom object _lineitems[0]
    And save the zoomed response
    When I GET /carts/mobee/default
    And I follow links lineitems
    Then the response is identical to the saved response
    And I GET /carts/mobee/default?zoom=lineitems,total
    And I inspect the zoom object _total[0]
    And save the zoomed response
    When I GET /carts/mobee/default
    And I follow links total
    Then the response is identical to the saved response

  Scenario: Test zooming a rel inside another rel is identical to non-zoomed object
    Given I GET /carts/mobee/default?zoom=lineitems:element:item
    And I inspect the zoom object _lineitems[0]._element[0]._item[0]
    And save the zoomed response
    When I GET /carts/mobee/default
    And I follow links lineitems -> element -> item
    Then the response is identical to the saved response

  Scenario: Test zooming the same rel twice should return one result
    When I GET /carts/mobee/default?zoom=lineitems,lineitems
    Then the response contains path _lineitems[0]
    And the response does not contain path _lineitems[1]
    When I inspect the zoom object _lineitems[0]
    And save the zoomed response
    And I GET /carts/mobee/default
    And I follow links lineitems
    Then the response is identical to the saved response
    And the zoom object does not contain path _lineitems[0]

  Scenario: Test zooming a rel and that rel's subrel at the same time
    Given I GET /carts/mobee/default?zoom=lineitems,lineitems:element
    And I inspect the zoom object _lineitems[0]._element[0]
    And save the zoomed response
    When I GET /carts/mobee/default
    And I follow links lineitems -> element
    Then the response is identical to the saved response

  Scenario: The same zoom parameter is returned in the response self uri
    When I GET /lookups/mobee?zoom=itemlookupform
    Then the json path self.uri equals /lookups/mobee?zoom=itemlookupform

  Scenario: The return representation self uri should contain a zoom parameter that was not found
    When I GET /lookups/mobee?zoom=elephant
    Then the json path self.uri equals /lookups/mobee?zoom=elephant

  Scenario: If the zoom query contains rel that are not found, the return representation self uri should contain the zoom parameter that was not found
    When I GET /lookups/mobee?zoom=elephant,itemlookupform
    Then the json path self.uri equals /lookups/mobee?zoom=elephant,itemlookupform

  Scenario: When you have multiple zoom parameters those from the same paths will be grouped together automatically
    Given I GET /carts/mobee/default?zoom=lineitems:element:item,lineitems:element:item:price,lineitems:element:item:availability,lineitems:element:item:definition
    When I inspect the zoom object _lineitems[0]._element[0]._item[0]
    Then the zoomed object contains path _price[0].purchase-price[0].display equal to $34.99
    And the zoomed object contains path _availability[0].state equal to AVAILABLE_FOR_PRE_ORDER
    And the zoomed object contains path _definition[0].display-name equal to Hugo

  Scenario: Zoom can be used with selectors to display all possible options and zoomed objects are ordered
    Given I login as a registered shopper
    And I GET /carts/mobee/default
    And I follow links order -> billingaddressinfo -> selector
    And save the selector uri
    And I zoom the current url choice,chosen
    And I inspect the zoom object _chosen[0]
    And save the zoomed response
    When I navigate links defaultcart -> order -> billingaddressinfo -> selector -> chosen
    Then the response is identical to the saved response
    And I GET /carts/mobee/default
    And I follow links order -> billingaddressinfo -> selector
    And save the selector uri
    And I zoom the current url choice,chosen
    And I inspect the zoom object _choice[0]
    And save the zoomed response
    When I navigate links defaultcart -> order -> billingaddressinfo -> selector -> choice
    Then the response is identical to the saved response

  Scenario: Test zooming a rel that doesn't exist
    When I GET /carts/mobee/default?zoom=nonexistingrel
    Then the response does not contain path _nonexistingrel[0]

  Scenario: Test zooming a rel that doesn't exist with a sub-rel that doesn't exist
    When I GET /carts/mobee/default?zoom=nonexistingrel:nonexistingsubrel
    Then the response does not contain path _nonexistingrel[0]

  Scenario: Test zooming a rel that exists but with a sub-rel that doesn't exist
    When I GET /carts/mobee/default?zoom=lineitems:nonexistingsubrel
    Then the response does not contain path _lineitems[0]._nonexistingsubrel[0]

  Scenario: Test zooming an invalid and valid rel at the same time
    When I GET /carts/mobee/default?zoom=nonexistingrel,lineitems
    Then the response does not contain path _nonexistingsubrel[0]
    And I inspect the zoom object _lineitems[0]
    And save the zoomed response
    And I navigate links defaultcart -> lineitems
    And the response is identical to the saved response

  Scenario: Test zooming an valid rel and invalid sub rel at the same time
    When I GET /carts/mobee/default?zoom=lineitems,lineitems:nonexistingrel
    Then the response does not contain path _lineitems[0]._nonexistingsubrel[0]
    And I inspect the zoom object _lineitems[0]
    And save the zoomed response
    And I navigate links defaultcart -> lineitems
    And the response is identical to the saved response

  Scenario: Test zooming an invalid rel with a sub-rel that doesn't exist
    When I GET /carts/mobee/default?zoom=lineitems:nonexistingsubrel
    Then the response does not contain path _lineitems[0]
    And the response does not contain path _lineitems[0]._nonexistingsubrel[0]

  Scenario: POST with valid followlocation and zoom query parameters
    Given I POST to /searches/mobee/keywords/form?zoom=next&followlocation with request body {"keywords":"Hugo"}
    And save the zoom response
    When I POST to /searches/mobee/keywords/form?followlocation with request body {"keywords":"Hugo"}
    And I zoom the current url next
    Then the response is identical to the saved response

  Scenario: POST with zoom specified without followlocation
    When I POST to /searches/mobee/keywords/form?zoom=element with request body {"keywords":"Hugo"}
    Then the HTTP status is OK, created
    And the response is empty

  Scenario: Unsuccessful POST with valid followlocation and zoom query parameters
    When I POST to /searches/mobee/keywords/form?zoom=next&followlocation with request body {"keywords":""}
    Then the HTTP status is bad request

  Scenario: The ordering of element rels prefetched using zoom from the navigations starting point are consistent with the order of the non-zoomed object
    Given I GET navigations/mobee?zoom=element
    And I inspect the zoom object _element[0]
    And save the zoomed response
    When I navigate links navigations -> element
    Then the response is identical to the saved response