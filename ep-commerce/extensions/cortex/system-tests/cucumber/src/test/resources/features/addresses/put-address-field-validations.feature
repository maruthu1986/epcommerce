@Addresses
Feature: PUT address field validations

  Background:
    Given I have authenticated as a newly registered shopper
    When I create a default billing address on the profile

  Scenario Outline: PUT address with valid field values
    When I go to registered shopper profile addresses
    And I update address via put with country <COUNTRY>, locality <LOCALITY>, postal code <POSTAL_CODE>, region <REGION>, street address <STREET_ADDRESS>, family name <FAMILY_NAME>, given name <GIVEN_NAME>
    Then the HTTP status is no content

    Examples:
      | COUNTRY | LOCALITY | POSTAL_CODE | REGION | STREET_ADDRESS | FAMILY_NAME | GIVEN_NAME |
      | CA      | Calgary  | v1v1v1      | AB     | 123 street     | testFName   | testLName  |

  Scenario Outline: PUT address with empty fields
    When I go to registered shopper profile addresses
    And I update address via put with country <COUNTRY>, locality <LOCALITY>, postal code <POSTAL_CODE>, region <REGION>, street address <STREET_ADDRESS>, family name <FAMILY_NAME>, given name <GIVEN_NAME>
    Then the HTTP status is bad request
    And Structured error message contains:
      | locality may not be null       |
      | family-name may not be null    |
      | street-address may not be null |
      | given-name may not be null     |
      | country-name may not be null   |
      | postal-code may not be null    |

    Examples:
      | COUNTRY | LOCALITY | POSTAL_CODE | REGION | STREET_ADDRESS | FAMILY_NAME | GIVEN_NAME |
      |         |          |             |        |                |             |            |

  Scenario Outline: PUT address with invalid country values
    When I go to registered shopper profile addresses
    And I update address via put with country <COUNTRY>, locality <LOCALITY>, postal code <POSTAL_CODE>, region <REGION>, street address <STREET_ADDRESS>, family name <FAMILY_NAME>, given name <GIVEN_NAME>
    Then the HTTP status is bad request
    And I should see validation error message with message type, message id, and debug message
      | messageType | messageId                    | debugMessage                                           |
      | error       | field.invalid.size           | country-name size must be between 2 and 2              |
      | error       | field.invalid.country.format | country-name does not exist in list of supported codes |

    Examples:
      | COUNTRY | LOCALITY | POSTAL_CODE | REGION | STREET_ADDRESS | FAMILY_NAME | GIVEN_NAME |
      | Canada  | Calgary  | v1v1v1      | AB     | 123 street     | testFName   | testLName  |

  Scenario Outline: PUT address with invalid region values
    When I go to registered shopper profile addresses
    And I update address via put with country <COUNTRY>, locality <LOCALITY>, postal code <POSTAL_CODE>, region <REGION>, street address <STREET_ADDRESS>, family name <FAMILY_NAME>, given name <GIVEN_NAME>
    Then the HTTP status is bad request
    And I should see validation error message with message type, message id, and debug message
      | messageType | messageId  | debugMessage    |
      | error       | <ERROR_ID> | <DEBUG_MESSAGE> |

    Examples:
      | COUNTRY | LOCALITY  | POSTAL_CODE | REGION           | STREET_ADDRESS   | FAMILY_NAME | GIVEN_NAME | ERROR_ID                        | DEBUG_MESSAGE                                    |
      | CA      | Vancouver | V5C1N2      |                  | 1234 Coco Avenue | Harris      | Ollie      | field.required                  | region must not be blank                         |
      | CA      | Vancouver | V5C1N2      | British Columbia | 1234 Coco Avenue | Harris      | Ollie      | field.invalid.subcountry.format | region does not exist in list of supported codes |

  Scenario: Can modify an address with optional fields left empty
    Given I am logged in as a public shopper
    And I get address form
    And I create address with Country CA, Extended-Address 1234, Locality Vancouver, Organization Company Inc, Phone-Number 555-555-5555, Postal-Code V5C1N2, Region BC, Street-Address 123 Broadway, Family-Name Customer and Given-Name Public Tester
    And I should see address matches the following
      | country-name     | CA           |
      | extended-address | 1234         |
      | locality         | Vancouver    |
      | postal-code      | V5C1N2       |
      | region           | BC           |
      | street-address   | 123 Broadway |
    And I modify the address with Country US, Extended-Address , Locality Vancouver, Organization , Phone-Number , Postal-Code 90210, Region WA, Street-Address 123 Broadway, Family-Name Customer and Given-Name Public Tester
    Then the HTTP status is no content
    And I view my profile
    And I follow links addresses
    And there are 1 links of rel element
    And I should see address matches the following
      | country-name   | US           |
      | locality       | Vancouver    |
      | postal-code    | 90210        |
      | region         | WA           |
      | street-address | 123 Broadway |
    And the field extended-address matches null
    And the field organization matches null
    And the field phone-number matches null
