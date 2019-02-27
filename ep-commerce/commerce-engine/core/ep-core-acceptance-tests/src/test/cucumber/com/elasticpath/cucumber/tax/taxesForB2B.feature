@tax
Feature: taxesForB2B

  As Finance for a B2B business, I want to be sure the Customer's tax-related information is forwarded to our
  external tax system so that taxes can be calculated correctly.

  Scenario: The customer's business number and tax exemption id should be available to the tax API

    Given a customer with a business number of [12345]
    And a tax exemption id of [TaxFreeMe]

    When I examine the tax details sent to the tax API during checkout
    Then I expect the business number to be 12345
     And I expect the tax exemption id to be TaxFreeMe

  Scenario: A tax exemption id entered during the checkout process should be used by the tax API
  instead of the one stored in the account profile

    Given a customer with a tax exemption id of [TaxFreeMe]
      And a shopping cart with a manually entered tax exemption id of [ManuallyEntered]

    When I examine the tax details sent to the tax API during checkout
    Then I expect the tax exemption id to be ManuallyEntered
