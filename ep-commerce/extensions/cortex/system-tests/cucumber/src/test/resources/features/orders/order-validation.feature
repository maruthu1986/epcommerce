@Orders

Feature: Tests order resource validation

  Scenario: Cart with physical product has billing address, email, payment method, and shipping details advisors on order when none set
    Given I am logged in as a public shopper
    When I add item with code FocUSsku to my cart
    Then I retrieve the order
    And there are advisor messages with the following fields:
      | messageType | messageId             | debugMessage                              | linkedTo                             |
      | needinfo    | need.billing.address  | Billing address must be specified.        | orders.billingaddress-info           |
      | needinfo    | need.email            | Customer email address must be specified. | orders.email-info                    |
      | needinfo    | need.payment.method   | Payment method must be specified.         | paymentmethods.paymentmethod-info    |
      | needinfo    | need.shipping.option  | Shipping option must be specified.           | shipmentdetails.shipping-option-info |
      | needinfo    | need.shipping.address | Shipping address must be specified.       | shipmentdetails.destination-info     |

  Scenario: Cart with digital product has billing address and email advisors on order when none set
    Given I am logged in as a public shopper
    And I add item with code tt888456tw to my cart
    When I retrieve the order
    Then there are advisor messages with the following fields:
      | messageType | messageId            | debugMessage                              | linkedTo                          |
      | needinfo    | need.billing.address | Billing address must be specified.        | orders.billingaddress-info        |
      | needinfo    | need.email           | Customer email address must be specified. | orders.email-info                 |
      | needinfo    | need.payment.method  | Payment method must be specified.         | paymentmethods.paymentmethod-info |
    And there is no advisor linked to shipping-option-info
    And there is no advisor linked to destination-info

  Scenario Outline: Need info links in order removed when required fields added
    Given I am logged in as a public shopper
    And I add item with code <SHIPPABLE_ITEM> to my cart
    When I retrieve the order
    Then there are advisor messages with the following fields:
      | messageType | messageId             | debugMessage                              |
      | needinfo    | need.billing.address  | Billing address must be specified.        |
      | needinfo    | need.email            | Customer email address must be specified. |
      | needinfo    | need.payment.method   | Payment method must be specified.         |
      | needinfo    | need.shipping.option  | Shipping option must be specified.           |
      | needinfo    | need.shipping.address | Shipping address must be specified.       |
    When I create a payment method for my order
    And I add an address with country CA and region BC
    And I create an email for my order
    And I retrieve the order
    Then there is no advisor linked to billingaddress-info
    And there is no advisor linked to email-info
    And there is no advisor linked to paymentmethod-info
    And there is no advisor linked to destination-info
    And there is no advisor linked to shipping-option-info
    When I submit the order
    Then the HTTP status is OK, created

    Examples:
      | SHIPPABLE_ITEM          |
      | handsfree_shippable_sku |

  Scenario Outline: Shipping option need info links
    Given I have authenticated as a newly registered shopper
    And I add item with code <SHIPPABLE_ITEM> to my cart
    And I create address with Country GB, Extended-Address County with no shipping options, Locality London, Organization Company Inc, Phone-Number 555-555-5555, Postal-Code N0N 0N0, Region , Street-Address fake street, Family-Name Test and Given-Name Test
    And I fill in payment methods needinfo
    When I retrieve the order
    Then there is an advisor message with the following fields:
      | messageType | messageId            | debugMessage                    | linkedTo                             |
      | needinfo    | need.shipping.option | Shipping option must be specified. | shipmentdetails.shipping-option-info |
    And I retrieve the purchase form
    And there is an advisor message with the following fields:
      | messageType | messageId            | debugMessage                    | linkedTo                             |
      | needinfo    | need.shipping.option | Shipping option must be specified. | shipmentdetails.shipping-option-info |
    And there are no submitorderaction links
    When I add an address with country CA and region BC
    And resolve the shipping-option-info needinfo
    And I retrieve the order
    Then there is no advisor linked to shipping-option-info
    And I retrieve the purchase form
    And there are no needinfo links
    And there is a submitorderaction link

    Examples:
      | SHIPPABLE_ITEM          |
      | handsfree_shippable_sku |

  Scenario Outline: Structured error message appears when trying to add item that can't be added to cart
    Given I am logged into scope mobee as a public shopper
    When I look up an item with code <ITEM>
    And I go to add to cart form
    Then there is an advisor message with the following fields:
      | messageType | messageId    | debugMessage    | dataField | blocks                 |
      | error       | <MESSAGE_ID> | <DEBUG_MESSAGE> | <ITEM>    | addtodefaultcartaction |

    Examples:
      | ITEM                   | MESSAGE_ID               | DEBUG_MESSAGE                                        |
      | tt8789434_HD_Buy       | item.not.sold.separately | Item 'tt8789434_HD_Buy' is not sold separately.      |
      | bundle_nopriceitem_sku | item.missing.price       | Item 'htcevo_sku' does not have a price. |
#      |                        | cart.item.invalid.bundle.structure  | Item in cart doesn't correspond with valid bundle structure.                           |
#      |                        | cart.item.invalid.bundle.selections | Item in cart does not match with bundle selection rules.                               |
    #TODO add tests for no inventory and marked as unavailable


  Scenario: Cart with bundled product advisors on order when minimum requirement isn't met
    Given I am logged in as a public shopper
    When I add item with code tb_dyn12345sku to my cart
    And I retrieve the order
    Then there are advisor messages with the following fields:
      | messageType | messageId                                | debugMessage                                                                | linkedTo        |
      | needinfo    | bundle.does.not.contain.min.constituents | Bundle does not contain the minimum number of required bundle constituents. | carts.line-item |