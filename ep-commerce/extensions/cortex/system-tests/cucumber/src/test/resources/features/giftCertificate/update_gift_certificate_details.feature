@GiftCertificate
Feature: Update Gift Certificate details

  Scenario Outline: Update Gift Certificate <SCENARIO>
    Given I login as a public shopper
    And I have the item <GIFT_CERTIFICATE> in the cart with quantity <QTY> and configurable fields:
      | giftCertificate.message        | <MESSAGE>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |

    When I update the item <GIFT_CERTIFICATE> in the cart with quantity <UPDATED_QTY> and configurable fields:
      | giftCertificate.message        | <UPDATED_MESSAGE>         |
      | giftCertificate.recipientEmail | <UPDATED_RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <UPDATED_RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <UPDATED_SENDER_NAME>     |

    Then the cart lineitem with itemcode <GIFT_CERTIFICATE> has quantity <UPDATED_QTY> and configurable fields as:
      | giftCertificate.message        | <UPDATED_MESSAGE>         |
      | giftCertificate.recipientEmail | <UPDATED_RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <UPDATED_RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <UPDATED_SENDER_NAME>     |

    Examples:
      | SCENARIO                                                          | GIFT_CERTIFICATE | MESSAGE     | RECIPIENT_EMAIL              | RECIPIENT_NAME | SENDER_NAME  | QTY | UPDATED_MESSAGE     | UPDATED_RECIPIENT_EMAIL       | UPDATED_RECIPIENT_NAME | UPDATED_SENDER_NAME | UPDATED_QTY |
      | all configurable fields                                           | berries_20       | Hello World | harry.potter@elasticpath.com | Harry Potter   | MOBEE tester | 1   | Updated Hello World | oliver.harris@elasticpath.com | Oliver Harris          | tester MOBEE        | 2           |
      | individual configurable field                                     | berries_20       | Hello World | harry.potter@elasticpath.com | Harry Potter   | MOBEE tester | 1   | Updated Hello World | harry.potter@elasticpath.com  | Harry Potter           | MOBEE tester        | 1           |
      | quantity field does not affect the configurable fields            | berries_20       | Hello World | harry.potter@elasticpath.com | Harry Potter   | MOBEE tester | 1   | Hello World         | harry.potter@elasticpath.com  | Harry Potter           | MOBEE tester        | 2           |
      | fields without any change does not affect the configurable fields | berries_20       | Hello World | harry.potter@elasticpath.com | Harry Potter   | MOBEE tester | 1   | Hello World         | harry.potter@elasticpath.com  | Harry Potter           | MOBEE tester        | 1           |