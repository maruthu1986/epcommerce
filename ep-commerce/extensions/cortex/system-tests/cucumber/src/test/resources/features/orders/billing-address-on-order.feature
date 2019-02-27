@Orders
Feature: Test billing address on order

  @HeaderAuth
  Scenario: Default is set on order when I have existing default billing address on profile
    Given I login as a registered shopper
    And Shopper gets the default billing address
    When I retrieve the shoppers billing address info on the order
    Then the default billing address is automatically applied to the order

  Scenario: Default is set on order when I create default billing address on profile
    Given I have authenticated as a newly registered shopper
    And the shoppers order does not have a billing address applied
    When I create a default billing address on the profile
    And I retrieve the shoppers billing address info on the order
    Then the default billing address is automatically applied to the order

  Scenario: Billing address choice becomes chosen when selected
    Given I have authenticated as a newly registered shopper
    And I add an address with country CA and region BC
    And I add an address with country CA and region QC
    And I retrieve the order
    And I follow links billingaddressinfo -> selector
    And there is a choice link
    And there is a chosen link
    When I follow links choice
    And save the choice uri
    And I use the selectaction
    Then the HTTP status is OK, created
    And I retrieve the order
    And I follow links billingaddressinfo -> selector
    Then the saved choice uri has rel chosen

  Scenario:  Billing address chosen becomes choice when new address is selected
    Given I have authenticated as a newly registered shopper
    And I add an address with country CA and region BC
    And I add an address with country CA and region QC
    And I retrieve the order
    And I follow links billingaddressinfo -> selector -> chosen
    And save the chosen uri
    And I retrieve the order
    And I follow links billingaddressinfo -> selector -> choice
    And I use the selectaction
    Then the HTTP status is OK, created
    And I retrieve the order
    And I follow links billingaddressinfo -> selector
    Then the saved chosen uri has rel choice

  Scenario:  Billing address chosen can be reselected
    Given I have authenticated as a newly registered shopper
    And I add an address with country CA and region BC
    And I add an address with country CA and region QC
    And I retrieve the order
    And I follow links billingaddressinfo -> selector -> chosen
    And save the chosen uri
    And I use the selectaction
    Then the HTTP status is OK
    And I retrieve the order
    And I follow links billingaddressinfo -> selector
    Then the saved chosen uri has rel chosen

  Scenario: Billing address selector shows choices when selected address is deleted
    Given I have authenticated as a newly registered shopper
    And I create a unique address
    And I create another unique address
    And I retrieve the order
    And I follow links billingaddressinfo -> selector
    And there is a choice link
    And there is a chosen link
    And I delete the chosen billing address
    And I retrieve the order
    And I follow links billingaddressinfo -> selector
    Then there is a choice link
    And there is no chosen link found
    And I follow links choice
    Then there is a selectaction link
    And there is a selector link
    And I follow links description
    Then I see an address with key-value pairs of data indicated below:
      | country-name | CA         |
      | locality     | Vancouver  |
      | postal-code  | V7V7V7     |
      | region       | BC         |