@GiftCertificate

Feature: Purchase multiple gift certificates

  Scenario Outline: Purchase multiple quantity of gift certificate and ensure purchase is successful and displays correct purchase amount
    Given I login as a public shopper
    And I add the item <GIFT_CERTIFICATE> to the cart with quantity <QTY> and configurable fields:
      | giftCertificate.message        | <MESSAGE>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |
    When I create a purchase and view the purchase details
    Then the purchase status is <STATUS>
    And purchase item monetary total has fields amount: <AMOUNT>, currency: <CURRENCY> and display: <DISPLAY_AMOUNT>

    Examples:
      | GIFT_CERTIFICATE | MESSAGE     | RECIPIENT_EMAIL              | RECIPIENT_NAME | SENDER_NAME  | QTY | STATUS    | AMOUNT | CURRENCY | DISPLAY_AMOUNT |
      | berries_20       | Hello World | harry.potter@elasticpath.com | Harry Potter   | MOBEE tester | 2   | COMPLETED | 40.0   | CAD      | $40.00         |


  Scenario Outline: Purchase multiple gift certificates and ensure purchase is successful and displays correct purchase amount
    Given I login as a public shopper
    And I add the item <ITEMCODE_1> to the cart with quantity <QTY> and configurable fields:
      | giftCertificate.message        | <MESSAGE>           |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL_1> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>    |
      | giftCertificate.senderName     | <SENDER_NAME>       |
    And I add the item <ITEMCODE_2> to the cart with quantity <QTY> and configurable fields:
      | giftCertificate.message        | <MESSAGE>           |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL_2> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>    |
      | giftCertificate.senderName     | <SENDER_NAME>       |

    When I create a purchase and view the purchase details
    Then the purchase status is <STATUS>
    And purchase item monetary total has fields amount: <AMOUNT>, currency: <CURRENCY> and display: <DISPLAY_AMOUNT>
    And the number of purchase lineitems is 2
    And there exists a purchase line item for item <ITEMCODE_1> with configurable fields:
      | giftCertificate.message        | <MESSAGE>           |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL_1> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>    |
      | giftCertificate.senderName     | <SENDER_NAME>       |
    And there exists a purchase line item for item <ITEMCODE_2> with configurable fields:
      | giftCertificate.message        | <MESSAGE>           |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL_2> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>    |
      | giftCertificate.senderName     | <SENDER_NAME>       |


    Examples:
      | ITEMCODE_1 | ITEMCODE_2      | MESSAGE     | RECIPIENT_EMAIL_1            | RECIPIENT_EMAIL_2 | RECIPIENT_NAME | SENDER_NAME  | QTY | STATUS    | AMOUNT | CURRENCY | DISPLAY_AMOUNT |
      | berries_20 | hummingbird_100 | Hello World | harry.potter@elasticpath.com | test@test.com     | Harry Potter   | MOBEE tester | 1   | COMPLETED | 120.0  | CAD      | $120.00        |