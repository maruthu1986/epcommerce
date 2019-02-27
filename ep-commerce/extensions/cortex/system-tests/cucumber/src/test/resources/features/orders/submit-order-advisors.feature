@Purchases @Orders

Feature: purchase resource advisor tests

  Scenario: Cart with product has billing address, email, and payment method advisors on order when none set
    Given I am logged in as a public shopper
    And I add item with code FocUSsku to my cart
    When I retrieve the purchase form
    Then there are advisor messages with the following fields:
      | messageType | messageId             | debugMessage                              | linkedTo                             | blocks            |
      | needinfo    | need.billing.address  | Billing address must be specified.        | orders.billingaddress-info           | submitorderaction |
      | needinfo    | need.email            | Customer email address must be specified. | orders.email-info                    | submitorderaction |
      | needinfo    | need.payment.method   | Payment method must be specified.         | paymentmethods.paymentmethod-info    | submitorderaction |
      | needinfo    | need.shipping.address | Shipping address must be specified.       | shipmentdetails.destination-info     | submitorderaction |
      | needinfo    | need.shipping.option  | Shipping option must be specified.           | shipmentdetails.shipping-option-info | submitorderaction |
    When I create an email for my order
    And I fill in billing address needinfo
    And I fill in payment methods needinfo
    Then there is no advisor linked to billingaddress-info
    And there is no advisor linked to email-info
    And there is no advisor linked to paymentmethod-info
    And there is no advisor linked to destination-info
    And there is no advisor linked to shipping-option-info
    And I can make a purchase

  Scenario: Cannot submit order when needinfo link exists
    Given I login as a public shopper
    And I add item with code FocUSsku to my cart
    And I create address with Country GB, Extended-Address County with no shipping options, Locality London, Organization Company Inc, Phone-Number 555-555-5555, Postal-Code N0N 0N0, Region , Street-Address fake street, Family-Name Test and Given-Name Test
    And I create an email for my order
    And I fill in payment methods needinfo
    And I retrieve the purchase form
    And there is an advisor message with the following fields:
      | messageType | messageId            | debugMessage                    | linkedTo                             | blocks            |
      | needinfo    | need.shipping.option | Shipping option must be specified. | shipmentdetails.shipping-option-info | submitorderaction |
    When post to a created submitorderaction uri
    Then the HTTP status is conflict

  Scenario: Cannot submit an order when you have no cart content
    Given I am logged in as a public shopper
    When I retrieve the purchase form
    Then there are advisor messages with the following fields:
      | messageType | messageId            | debugMessage                              | linkedTo                   |
      | needinfo    | need.billing.address | Billing address must be specified.        | orders.billingaddress-info |
      | needinfo    | need.email           | Customer email address must be specified. | orders.email-info          |
      | needinfo    | cart.empty           | Shopping cart is empty.                   |                            |
    And there are no submitorderaction links
    And post to a created submitorderaction uri
    And the HTTP status is conflict