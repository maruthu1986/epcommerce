@Navigations
Feature: Retrieve Navigation nodes and child nodes

  Scenario: Can find a list of navigations from the root
    Given I am logged in as a public shopper
    When I open the root navigations
    Then the expected navigation list exactly matches the following
      | Games               |
      | Accessories         |
      | Smartphones         |
      | Movies              |
      | TV                  |
      | GiftCertificate     |
      | phone_plan          |
      | AcceptanceTestItems |

  Scenario: Can navigate to child nodes from the root navigations resource
    Given I am logged in as a public shopper
    When I open the root navigations
    Then the navigation node Games should contain exactly the following child nodes
      | MobileGames |
      | VideoGames  |
    And the child node MobileGames should contain the following sub child nodes
      | AndroidGames |
      | IPhoneGames  |

  Scenario: Scope with no nodes has no navigation elements
    Given the catalog rockjam has no categories
    And I am logged into scope rockjam as a public shopper
    When I open the root navigations
    Then there are no element links