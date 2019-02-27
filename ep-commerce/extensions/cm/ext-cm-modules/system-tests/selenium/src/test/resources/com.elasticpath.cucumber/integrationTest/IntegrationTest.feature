@smoketest @integration
Feature: Integration Test

  Background:
    Given I sign in to CM as admin user
    And I go to Catalog Management

  Scenario: Purchase new product and complete order shipment
    When I create new product with following attributes
      | catalog        | category    | productName | productType | taxCode | brand  | storeVisible | availability     | shippableType | priceList               | listPrice |
      | Mobile Catalog | Accessories | Product     | Headset     | GOODS   | Disney | true         | Always available | Shippable     | Mobile Price List (CAD) | 10.99     |
    Then the newly created product is in the list
    When I purchase the newly created product for scope mobee
    And I go to Customer Service
    And I search and open order editor for the latest order
    Then the order contains the newly created product
    And I can complete the order shipment