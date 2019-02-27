@Shipping

Feature: Shipment Destination

  Background:
    Given I have authenticated as a newly registered shopper
    And I add an address with country CA and region BC
    And I add an address with country US and region WA

  Scenario Outline: selectaction link will not appear if the value is already selected
    Given I add item <PHYSICAL_ITEM> to the cart
    When I navigate links <TO_THE_CHOSEN_DESTINATION>
    Then there are no selectaction links

    Examples:
      | PHYSICAL_ITEM          | TO_THE_CHOSEN_DESTINATION                                                              |
      | Acon Bluetooth headset | defaultcart -> order -> deliveries -> element -> destinationinfo -> selector -> chosen |

  Scenario Outline: Chosen is default shipping address
    Given I get the default billing address
    And the field address contains value country-name=CA
    And save the default address uri
    When I add item <PHYSICAL_ITEM> to the cart
    And I navigate links <TO_THE_CHOSEN_DESTINATION>
    Then the field address contains value country-name=CA
    And the uri of selected address matches the uri of saved default address uri

    Examples:
      | PHYSICAL_ITEM          | TO_THE_CHOSEN_DESTINATION                                                                             |
      | Acon Bluetooth headset | defaultcart -> order -> deliveries -> element -> destinationinfo -> selector -> chosen -> description |

  Scenario Outline: No shipment destination is chosen if the shopper does not have a default shipping address
    Given I have authenticated as a newly registered shopper
    When I add item <PHYSICAL_ITEM> to the cart
    And I navigate links <TO_THE_DESTINATION_INFO>
    Then there are no destination links
    And I follow links selector
    And there are no chosen links

    Examples:
      | PHYSICAL_ITEM          | TO_THE_DESTINATION_INFO                                          |
      | Acon Bluetooth headset | defaultcart -> order -> deliveries -> element -> destinationinfo |

  Scenario Outline: Cannot use someone elses address
    Given I authenticate as a registered shopper harry.potter@elasticpath.com with the default scope
    And I get the default shipping address
    And save the address uri
    When I have authenticated as a newly registered shopper
    And I add item <PHYSICAL_ITEM> to the cart
    And I navigate links <TO_THE_DESTINATION_SELECTOR>
    And attempt to select the original shoppers address
    Then the HTTP status is not found

    Examples:
      | PHYSICAL_ITEM          | TO_THE_DESTINATION_SELECTOR                                                  |
      | Acon Bluetooth headset | defaultcart -> order -> deliveries -> element -> destinationinfo -> selector |

  Scenario Outline: No destination info from order without physical item
    Given I add item <DIGITAL_ITEM> to the cart
    And I navigate links defaultcart -> order -> deliveries
    Then there are no element links

    Examples:
      | DIGITAL_ITEM  |
      | Tetris Heaven |

  Scenario Outline: Cant get destination info from purchased order
    Given I add item <PHYSICAL_ITEM> to the cart
    And I navigate links <TO_THE_DESTINATION_INFO>
    And save the destinationinfo uri
    When I fill in payment methods needinfo
    And the order is submitted
    And attempt to access the saved destinationinfo uri
    Then the HTTP status is forbidden

    Examples:
      | PHYSICAL_ITEM          | TO_THE_DESTINATION_INFO                                          |
      | Acon Bluetooth headset | defaultcart -> order -> deliveries -> element -> destinationinfo |

  Scenario Outline: POSTing to the selectaction link of a choice will select that choice
    Given I add item <PHYSICAL_ITEM> to the cart
    And I navigate links <TO_THE_DESTINATION>
    And the field address contains value <DEFAULT_ADDRESS>
    When I retrieve the order
    And I follow links <TO_THE_DESTINATION_CHOICE>
    And I post the selectaction
    Then the HTTP status is OK, created
    And I navigate links <TO_THE_DESTINATION>
    Then the field address contains value <SELECTED_ADDRESS>

    Examples:
      | PHYSICAL_ITEM          | DEFAULT_ADDRESS | SELECTED_ADDRESS | TO_THE_DESTINATION                                                              | TO_THE_DESTINATION_CHOICE                                      |
      | Acon Bluetooth headset | country-name=CA | country-name=US  | defaultcart -> order -> deliveries -> element -> destinationinfo -> destination | deliveries -> element -> destinationinfo -> selector -> choice |