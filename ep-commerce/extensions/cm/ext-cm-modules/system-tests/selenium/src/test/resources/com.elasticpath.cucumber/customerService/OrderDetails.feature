@smoketest @customerService @order
Feature: Order Item Detail

  Background:
    Given I sign in to CM as admin user
    And I go to Customer Service

  Scenario Outline: Configurable fields values saved in order item detail
    Given I have an order for scope mobee with sku <SKU>, quantity 1 and following configurable fields
      | giftCertificate.message        | <MESSAGE>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |
    And I search and open order editor for the latest order
    And I select Details tab in the Order Editor
    When I view item detail of the order line item <SKU>
    Then the item detail matches the configurable field values from the purchase
      | giftCertificate.message        | <MESSAGE>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |

    Examples:
      | SKU        | MESSAGE      | RECIPIENT_EMAIL | RECIPIENT_NAME | SENDER_NAME |
      | berries_20 | Test Message | test@test.com   | Test Recipient | Test Sender |

  Scenario: Order is unlocked when modified order saved
    Given I have an order for scope mobee with following skus
      | skuCode                 | quantity |
      | handsfree_shippable_sku | 1        |
    When I search and open order editor for the latest order
    And I update and save the following shipping address for the order
      | address line 1 | 751 Pike Rd |
      | phone          | 616 2323231 |
    Then Unlock Order button is disabled

  Scenario: Verify Order Shipment Discount Value
    Given there is a 30 percent off cart subtotal coupon blackfriday and sku digital_sku has purchase price of price $20
    And I create an order for scope mobee with coupon blackfriday for following sku
      | skuCode     | quantity |
      | digital_sku | 1        |
    When I search and open order editor for the latest order
    And I select Details tab in the Order Editor
    Then Shipment Discount of 6.00 is present in the order details
