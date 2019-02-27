@GiftCertificate
Feature: Purchase one gift certificate and ensure purchase is successful

  Scenario Outline: Ensure Gift Certificate purchase is successful and displays correct gift certificate field values
    Given I login as a public shopper
    And I add the item <GIFT_CERTIFICATE> to the cart with quantity <QTY> and configurable fields:
      | giftCertificate.message        | <MESSAGE>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |
    When I create a purchase and view the purchase details
    Then the purchase status is <STATUS>
    And there are no shipments
    And the purchase line item configurable fields for item <ITEM_NAME> are:
      | giftCertificate.message        | <MESSAGE>         |
      | giftCertificate.recipientEmail | <RECIPIENT_EMAIL> |
      | giftCertificate.recipientName  | <RECIPIENT_NAME>  |
      | giftCertificate.senderName     | <SENDER_NAME>     |

    Examples:
      | GIFT_CERTIFICATE | MESSAGE     | RECIPIENT_EMAIL              | RECIPIENT_NAME | SENDER_NAME  | QTY | STATUS    | ITEM_NAME        |
      | berries_20       | Hello World | harry.potter@elasticpath.com | Harry Potter   | MOBEE tester | 1   | COMPLETED | Gift Certificate |

  Scenario Outline: Ensure a gift certificate cannot be purchased without field values
    Given I login as a public shopper
    And I add item with code <GIFT_CERTIFICATE> to my cart without the required configurable fields
    Then the operation is identified as bad request
    And Structured error message contains:
      | 'giftCertificate.recipientEmail' value is required. |
      | 'giftCertificate.recipientName' value is required.  |
      | 'giftCertificate.senderName' value is required.     |

    Examples:
      | GIFT_CERTIFICATE |
      | berries_20       |

  Scenario Outline: Ensure a gift certificate cannot be purchased without all required field values
    Given I login as a public shopper
    And I add the item <GIFT_CERTIFICATE> to the cart with quantity <QTY> and configurable fields:
      | giftCertificate.recipientName | <RECIPIENT_NAME> |
    Then the operation is identified as bad request
    And Structured error message contains:
      | 'giftCertificate.recipientEmail' value is required. |
      | 'giftCertificate.senderName' value is required.     |

    Examples:
      | GIFT_CERTIFICATE | QTY | RECIPIENT_NAME |
      | berries_20       | 1   | Godzilla       |
