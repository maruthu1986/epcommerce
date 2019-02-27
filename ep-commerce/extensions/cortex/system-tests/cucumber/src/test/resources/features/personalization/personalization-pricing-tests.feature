@Personalization

Feature: Testing CE customer-trait based pricing
  I want to retrieve pricing information based on customer traits directly provided by commerce engine
  so that I could display personalised pricing information to the shopper

  Scenario: Discount product price for registered shopper without previous orders
    Given I am logged in as a public shopper
    And Transformers Registered has an original purchase price equal to $29.99

    When I authenticate as a registered shopper itest.default.user@elasticpath.com with the default scope
    And I request the purchase price for item Transformers Registered
    Then I get the purchase price equal to $17.99

  Scenario: Regular product price for unregistered shopper
    Given I am logged in as a public shopper
    And Transformers Registered has an original purchase price equal to $29.99

    When I request the purchase price for item Transformers Registered
    Then I get the purchase price equal to $29.99

  Scenario: Discount product price for newly registered shopper
    Given I am logged in as a public shopper
    And Transformers Registered has an original purchase price equal to $29.99

    When I have authenticated as a newly registered shopper
    And I request the purchase price for item Transformers Registered
    Then I get the purchase price equal to $17.99

  Scenario Outline: Discount product price based on customer age (50 year threshold - customers pre-configured in test data)
    Given I am logged in as a public shopper
    And Transformers Over 50 has an original purchase price equal to $29.99

    When I authenticate as a registered shopper <CUSTOMER_EMAIL> with the default scope
    And I request the purchase price for item <PRODUCT>
    Then I get the purchase price equal to <PRICE>

    Examples:
      | CUSTOMER_EMAIL          | PRODUCT              | PRICE  |
      | under50@elasticpath.com | Transformers Over 50 | $29.99 |
      | over50@elasticpath.com  | Transformers Over 50 | $9.99  |

  Scenario: Product price for unregistered shopper using First-Time-Buyer Price List
    Given I authenticate as a registered shopper itest.default.user@elasticpath.com with the default scope
    And I have previously made a purchase with "1" physical item "Sony Ericsson Xperia Pro"
    And Transformers First Time Buyer has an original purchase price equal to $29.99

    When I am logged in as a public shopper
    And I request the purchase price for item Transformers First Time Buyer
    Then I get the purchase price equal to $0.99

  Scenario Outline: Product price for registered shopper with previous orders using First-Time-Buyer Price List
    Given I authenticate as a registered shopper itest.default.user@elasticpath.com with the default scope
    And I have previously made a purchase with "1" physical item "Sony Ericsson Xperia Pro"

    When I authenticate as a registered shopper <CUSTOMER_EMAIL> with the default scope
    And I request the purchase price for item <PRODUCT>
    Then I get the purchase price equal to <PRICE>

    Examples:
      | CUSTOMER_EMAIL                        | PRODUCT                       | PRICE  |
      | itest.nopurchase.user@elasticpath.com | Transformers First Time Buyer | $0.99  |
      | itest.default.user@elasticpath.com    | Transformers First Time Buyer | $29.99 |

  Scenario Outline: Unique product pricing based on different scopes
    Given I am logged into scope <SCOPE> as a public shopper

    When I request the purchase price in scope <SCOPE> for item <PRODUCT>
    Then I get the purchase price equal to <PRICE>

    Examples:
      | SCOPE   | PRICE | PRODUCT      |
      | mobee   | $0.50 | Wing Warrior |
      | tokenee | $0.99 | Wing Warrior |