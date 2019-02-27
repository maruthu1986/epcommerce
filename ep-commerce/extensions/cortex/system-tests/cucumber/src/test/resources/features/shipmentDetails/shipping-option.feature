@Shipping

Feature: Shipping Options

  Background:
    Given I login as a newly registered shopper
    And I add an address with country CA and region BC
    And I add an address with country US and region WA

  Scenario Outline: selectaction link will not appear if the value is already selected
    Given I add item <PHYSICAL_ITEM> to the cart
    When I navigate links <TO_THE_CHOSEN_SHIPPING_OPTION>
    Then there are no selectaction links

    Examples:
      | PHYSICAL_ITEM   | TO_THE_CHOSEN_SHIPPING_OPTION                                                             |
      | Samsung Headset | defaultcart -> order -> deliveries -> element -> shippingoptioninfo -> selector -> chosen |


  Scenario Outline: Correct choice shows
    Given I add item <PHYSICAL_ITEM> to the cart
    When I navigate links <TO_THE_SHIPPING_OPTION_CHOICE>
    Then the fields contain the following values
      | key          | value               |
      | name         | CanadaPostExpress   |
      | display-name | Canada Post Express |
      | carrier      | Canada Post         |
      | cost         | currency:CAD        |
      | cost         | amount:10           |
      | cost         | display:$10.00      |

    Examples:
      | PHYSICAL_ITEM   | TO_THE_SHIPPING_OPTION_CHOICE                                                                            |
      | Samsung Headset | defaultcart -> order -> deliveries -> element -> shippingoptioninfo -> selector -> choice -> description |


  Scenario Outline: No shipping option for selected destination
    Given I add item <PHYSICAL_ITEM> to the cart
    And I navigate links <TO_A_CA_SHIPPINGOPTION>
    And save the Canadian shipping option uri
    When I navigate links <TO_A_US_DESTINATION_ADDRESS>
    And I post the selectaction
    And return to the saved Canadian shipping option
    Then the HTTP status is not found

    Examples:
      | PHYSICAL_ITEM   | TO_A_CA_SHIPPINGOPTION                                                                                   | TO_A_US_DESTINATION_ADDRESS                                                            |
      | Samsung Headset | defaultcart -> order -> deliveries -> element -> shippingoptioninfo -> selector -> chosen -> description | defaultcart -> order -> deliveries -> element -> destinationinfo -> selector -> choice |

  Scenario Outline: Delivery method selector only shows available choices after shipping address is selected
    Given I have authenticated as a newly registered shopper
    And I add item <PHYSICAL_ITEM> to the cart
    And I navigate links <TO_SHIPPINGOPTIONS>
    And I follow links selector
    And the HTTP status is not found
    When I add an address with country CA and region BC
    And I navigate links <TO_SHIPPINGOPTIONS>
    And I follow links selector
    Then there are 3 links of rel choice
    And there are 1 links of rel chosen

    Examples:
      | PHYSICAL_ITEM   | TO_SHIPPINGOPTIONS                                                  |
      | Samsung Headset | defaultcart -> order -> deliveries -> element -> shippingoptioninfo |

  Scenario Outline: Delivery methods relevant to currently selected shipping address are displayed
    Given I add item <PHYSICAL_ITEM> to the cart
    And I navigate links <TO_SHIPPINGOPTIONS>
    And there are 3 links of rel choice
    And there are 1 links of rel chosen
    And I follow links chosen -> description
    And the field name has value <CANADIAN_SHIPPING_OPTION>
    And I navigate links <TO_A_US_DESTINATION_ADDRESS>
    And I post the selectaction
    Then the HTTP status is OK, created
    And I navigate links <TO_SHIPPINGOPTIONS>
    Then there are no choice links
    And there are 1 links of rel chosen
    And I follow links chosen -> description
    And the field name has value <US_SHIPPING_OPTION>

    Examples:
      | PHYSICAL_ITEM   | TO_SHIPPINGOPTIONS                                                              | TO_A_US_DESTINATION_ADDRESS                                                            | CANADIAN_SHIPPING_OPTION | US_SHIPPING_OPTION |
      | Samsung Headset | defaultcart -> order -> deliveries -> element -> shippingoptioninfo -> selector | defaultcart -> order -> deliveries -> element -> destinationinfo -> selector -> choice | CanadaPostTwoDays        | FedExExpress       |

  Scenario Outline: Can change delivery method selection and order total is updated
    Given I add item <PHYSICAL_ITEM> to the cart
    And I navigate links <TO_SHIPPINGOPTIONS>
    And I follow links chosen -> description
    And the field name has value <ORIGINAL_SHIPPING_OPTION>
    And the order total has amount: 117.6, currency: CAD and display: $117.60
    When I navigate links <TO_SHIPPINGOPTIONS>
    And I follow links choice
    And I use the selectaction
    Then the HTTP status is OK, created
    And I navigate links <TO_SHIPPINGOPTIONS>
    And I follow links chosen -> description
    Then the field name has value <NEW_SHIPPING_OPTION>
    And the order total has amount: 123.2, currency: CAD and display: $123.20

    Examples:
      | PHYSICAL_ITEM   | TO_SHIPPINGOPTIONS                                                              | ORIGINAL_SHIPPING_OPTION | NEW_SHIPPING_OPTION |
      | Samsung Headset | defaultcart -> order -> deliveries -> element -> shippingoptioninfo -> selector | CanadaPostTwoDays        | CanadaPostExpress   |

  Scenario Outline: Shipping options can't be navigated to if there is not a physical shipment
    Given I add item <DIGITAL_ITEM> to the cart
    When I navigate links <TO_THE_DELIVERIES>
    Then there are no element links

    Examples:
      | DIGITAL_ITEM  | TO_THE_DELIVERIES                  |
      | Tetris Heaven | defaultcart -> order -> deliveries |

  Scenario Outline:  Shipping option cannot be accessed if there are no physical items in the cart
    Given I add item <PHYSICAL_ITEM> to the cart
    And I add item <DIGITAL_ITEM> to the cart
    And I navigate links <TO_THE_CHOSEN_SHIPPING_OPTION>
    And save the shipping option uri
    When I delete item <PHYSICAL_ITEM> from my cart
    And attempt to access the previously selected shipping option
    Then the HTTP status is forbidden

    Examples:
      | PHYSICAL_ITEM   | DIGITAL_ITEM  | TO_THE_CHOSEN_SHIPPING_OPTION                                                             |
      | Samsung Headset | Tetris Heaven | defaultcart -> order -> deliveries -> element -> shippingoptioninfo -> selector -> chosen |

  Scenario Outline:  Shipping option is persisted in purchase
    Given I add item <PHYSICAL_ITEM> to the cart
    And I navigate links <TO_THE_SHIPPING_OPTION_CHOSEN>
    And the fields contain the following values
      | key          | value              |
      | name         | CanadaPostTwoDays  |
      | display-name | Canada Post 2 days |
      | carrier      | Canada Post        |
      | cost         | currency:CAD       |
      | cost         | amount:5           |
      | cost         | display:$5.00      |
    And I fill in payment methods needinfo
    When the order is submitted
    And I follow links <TO_THE_PURCHASE_SHIPPING_OPTION>
    Then the fields contain the following values
      | key          | value              |
      | name         | CanadaPostTwoDays  |
      | display-name | Canada Post 2 days |
      | carrier      | Canada Post        |
      | cost         | currency:CAD       |
      | cost         | amount:5           |
      | cost         | display:$5.00      |

    Examples:
      | PHYSICAL_ITEM   | TO_THE_SHIPPING_OPTION_CHOSEN                                                                            | TO_THE_PURCHASE_SHIPPING_OPTION        |
      | Samsung Headset | defaultcart -> order -> deliveries -> element -> shippingoptioninfo -> selector -> chosen -> description | shipments -> element -> shippingoption |
