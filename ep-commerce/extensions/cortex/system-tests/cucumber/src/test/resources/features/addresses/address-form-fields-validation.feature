@Addresses
Feature: Addresses Form Fields Validations
  Field validations for address fields. The response should be 400 error if it is invalid fields.

  Background:
    Given I login as a public shopper

  Scenario Outline: Address fields validations - all fields empty
    When I get address form
    And I create address with Country <COUNTRY>, Extended-Address <EXTENDED_ADDRESS>, Locality <LOCALITY>, Organization <ORGANIZATION>, Phone-Number <PHONE_NUMBER>, Postal-Code <POSTAL_CODE>, Region <REGION>, Street-Address <STREET_ADDRESS>, Family-Name <FAMILY_NAME> and Given-Name <GIVEN_NAME>
    Then the HTTP status is bad request
    And Structured error message contains:
      | given-name must not be blank                  |
      | locality must not be blank                    |
      | street-address must not be blank              |
      | country-name size must be between 2 and 2     |
      | postal-code size must be between 1 and 50     |
      | locality size must be between 1 and 200       |
      | family-name must not be blank                 |
      | family-name size must be between 1 and 100    |
      | postal-code must not be blank                 |
      | country-name must not be blank                |
      | given-name size must be between 1 and 100     |
      | street-address size must be between 1 and 200 |

    Examples:
      | COUNTRY | EXTENDED_ADDRESS | LOCALITY | ORGANIZATION | PHONE_NUMBER | POSTAL_CODE | REGION | STREET_ADDRESS | FAMILY_NAME | GIVEN_NAME |
      |         |                  |          |              |              |             |        |                |             |            |

  Scenario Outline: Address field validation - missing locality field
    When I get address form
    And I create address with Country <COUNTRY>, Extended-Address <EXTENDED_ADDRESS>, Locality <LOCALITY>, Organization <ORGANIZATION>, Phone-Number <PHONE_NUMBER>, Postal-Code <POSTAL_CODE>, Region <REGION>, Street-Address <STREET_ADDRESS>, Family-Name <FAMILY_NAME> and Given-Name <GIVEN_NAME>
    Then the HTTP status is bad request
    And Structured error message contains:
      | locality must not be blank              |
      | locality size must be between 1 and 200 |

    Examples:
      | COUNTRY | EXTENDED_ADDRESS | LOCALITY | ORGANIZATION | PHONE_NUMBER | POSTAL_CODE | REGION | STREET_ADDRESS   | FAMILY_NAME | GIVEN_NAME |
      | CA      |                  |          |              |              | V5C1N2      | BC     | 1234 Coco Avenue | Harris      | Ollie      |

  Scenario Outline: Address field validation - missing postal code
    When I get address form
    And I create address with Country <COUNTRY>, Extended-Address <EXTENDED_ADDRESS>, Locality <LOCALITY>, Organization <ORGANIZATION>, Phone-Number <PHONE_NUMBER>, Postal-Code <POSTAL_CODE>, Region <REGION>, Street-Address <STREET_ADDRESS>, Family-Name <FAMILY_NAME> and Given-Name <GIVEN_NAME>
    Then the HTTP status is bad request
    And Structured error message contains:
      | postal-code must not be blank             |
      | postal-code size must be between 1 and 50 |

    Examples:
      | COUNTRY | EXTENDED_ADDRESS | LOCALITY  | ORGANIZATION | PHONE_NUMBER | POSTAL_CODE | REGION | STREET_ADDRESS   | FAMILY_NAME | GIVEN_NAME |
      | CA      |                  | Vancouver |              |              |             | BC     | 1234 Coco Avenue | Harris      | Ollie      |

  Scenario Outline: Address field validation - missing name block
    When I get address form
    And I create address with Country <COUNTRY>, Extended-Address <EXTENDED_ADDRESS>, Locality <LOCALITY>, Organization <ORGANIZATION>, Phone-Number <PHONE_NUMBER>, Postal-Code <POSTAL_CODE>, Region <REGION>, Street-Address <STREET_ADDRESS>, Family-Name <FAMILY_NAME> and Given-Name <GIVEN_NAME>
    Then the HTTP status is bad request
    And Structured error message contains:
      | family-name must not be blank              |
      | family-name size must be between 1 and 100 |
      | given-name must not be blank               |
      | given-name size must be between 1 and 100  |

    Examples:
      | COUNTRY | EXTENDED_ADDRESS | LOCALITY  | ORGANIZATION | PHONE_NUMBER | POSTAL_CODE | REGION | STREET_ADDRESS   | FAMILY_NAME | GIVEN_NAME |
      | CA      |                  | Vancouver |              |              | V5C1N2      | BC     | 1234 Coco Avenue |             |            |

  Scenario Outline: Address field validation - invalid address key
    When I get address form
    And I create address with invalid address key with Country <COUNTRY>, Locality <LOCALITY>, Postal-Code <POSTAL_CODE>, Region <REGION>, Street-Address <STREET_ADDRESS>, Family-Name <FAMILY_NAME> and Given-Name <GIVEN_NAME>
    Then the HTTP status is bad request
    And Structured error message contains:
      | postal-code may not be null    |
      | locality may not be null       |
      | street-address may not be null |
      | country-name may not be null   |

    Examples:
      | COUNTRY | LOCALITY  | POSTAL_CODE | REGION | STREET_ADDRESS   | FAMILY_NAME | GIVEN_NAME |
      | CA      | Vancouver | V5C1N2      | BC     | 1234 Coco Avenue | Harris      | Ollie      |

  Scenario Outline: Address field validation - invalid name key
    When I get address form
    And I create address with invalid name key with Country <COUNTRY>, Locality <LOCALITY>, Postal-Code <POSTAL_CODE>, Region <REGION>, Street-Address <STREET_ADDRESS>, Family-Name <FAMILY_NAME> and Given-Name <GIVEN_NAME>
    Then the HTTP status is bad request
    And Structured error message contains:
      | given-name may not be null  |
      | family-name may not be null |

    Examples:
      | COUNTRY | LOCALITY  | POSTAL_CODE | REGION | STREET_ADDRESS   | FAMILY_NAME | GIVEN_NAME |
      | CA      | Vancouver | V5C1N2      | BC     | 1234 Coco Avenue | Harris      | Ollie      |

  Scenario Outline: Address field validation - invalid country field
    When I get address form
    And I create address with Country <COUNTRY>, Extended-Address <EXTENDED_ADDRESS>, Locality <LOCALITY>, Organization <ORGANIZATION>, Phone-Number <PHONE_NUMBER>, Postal-Code <POSTAL_CODE>, Region <REGION>, Street-Address <STREET_ADDRESS>, Family-Name <FAMILY_NAME> and Given-Name <GIVEN_NAME>
    Then the HTTP status is bad request
    And I should see validation error message with message type, message id, and debug message
      | messageType | messageId                    | debugMessage                                           |
      | error       | field.invalid.size           | country-name size must be between 2 and 2              |
      | error       | field.invalid.country.format | country-name does not exist in list of supported codes |

    Examples:
      | COUNTRY | EXTENDED_ADDRESS | LOCALITY  | ORGANIZATION | PHONE_NUMBER | POSTAL_CODE | REGION | STREET_ADDRESS   | FAMILY_NAME | GIVEN_NAME |
      | CANADA  |                  | Vancouver |              |              | V5C1N2      | BC     | 1234 Coco Avenue | Harris      | Ollie      |

  Scenario Outline: Address field validation - invalid region field
    When I get address form
    And I create address with Country <COUNTRY>, Extended-Address <EXTENDED_ADDRESS>, Locality <LOCALITY>, Organization <ORGANIZATION>, Phone-Number <PHONE_NUMBER>, Postal-Code <POSTAL_CODE>, Region <REGION>, Street-Address <STREET_ADDRESS>, Family-Name <FAMILY_NAME> and Given-Name <GIVEN_NAME>
    Then the HTTP status is bad request
    And I should see validation error message with message type, message id, and debug message
      | messageType | messageId  | debugMessage    |
      | error       | <ERROR_ID> | <DEBUG_MESSAGE> |

    Examples:
      | COUNTRY | EXTENDED_ADDRESS | LOCALITY  | ORGANIZATION | PHONE_NUMBER | POSTAL_CODE | REGION           | STREET_ADDRESS   | FAMILY_NAME | GIVEN_NAME | ERROR_ID                        | DEBUG_MESSAGE                                    |
      | CA      |                  | Vancouver |              |              | V5C1N2      |                  | 1234 Coco Avenue | Harris      | Ollie      | field.required                  | region must not be blank                         |
      | CA      |                  | Vancouver |              |              | V5C1N2      | British Columbia | 1234 Coco Avenue | Harris      | Ollie      | field.invalid.subcountry.format | region does not exist in list of supported codes |