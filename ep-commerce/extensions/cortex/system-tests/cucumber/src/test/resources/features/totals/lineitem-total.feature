@totals @HeaderAuth

Feature: Retrieve Line Item Total
  As a client developer,
  I want to retrieve the pricing information of a lineitem
  so that the shopper could see how much they are paying for each lineitem

  Background:
    Given I login as a registered shopper

  Scenario Outline: Retrieve Line Item Total
    Given I add item <ITEM_NAME> to the cart with quantity <QTY>
    When I follow the total link from the cart lineitem for <ITEM_NAME>
    Then the cost fields has amount: <AMOUNT>, currency: <CURRENCY> and display: <DISPLAY_AMOUNT>

    Examples:
      | ITEM_NAME | QTY | AMOUNT | CURRENCY | DISPLAY_AMOUNT |
      | Twilight  | 2   | 94.94  | CAD      | $94.94         |