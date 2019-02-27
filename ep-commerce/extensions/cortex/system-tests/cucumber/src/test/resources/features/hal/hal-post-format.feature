@HAL
Feature: HAL POST format

  Background:
    Given I am logged in as a public shopper

  Scenario Outline: POST should get HAL format back
    Given I post to address form with Country <COUNTRY>, Extended-Address <EXTENDED_ADDRESS>, Locality <LOCALITY>, Organization <ORGANIZATION>, Phone-Number <PHONE_NUMBER>, Postal-Code <POSTAL_CODE>, Region <REGION>, Street-Address <STREET_ADDRESS>, Family-Name <FAMILY_NAME> and Given-Name <GIVEN_NAME>
    Then I should see a list of links mapped to _links and not to links
    Then I should see the address from the POST
      | country-name     | <COUNTRY>          |
      | extended-address | <EXTENDED_ADDRESS> |
      | locality         | <LOCALITY>         |
      | organization     | <ORGANIZATION>     |
      | phone-number     | <PHONE_NUMBER>     |
      | postal-code      | <POSTAL_CODE>      |
      | region           | <REGION>           |
      | street-address   | <STREET_ADDRESS>   |
    And I should see the name from the POST
      | family-name | <FAMILY_NAME> |
      | given-name  | <GIVEN_NAME>  |

    Examples:
      | COUNTRY | EXTENDED_ADDRESS | LOCALITY  | ORGANIZATION | PHONE_NUMBER | POSTAL_CODE | REGION | STREET_ADDRESS | FAMILY_NAME | GIVEN_NAME    |
      | CA      | 1234             | Vancouver | Company Inc  | 555-555-5555 | V5C1N2      | BC     | 123 Broadway   | Customer    | Public Tester |


  Scenario Outline: POST invalid values should get validation error message back
    When I post to address form with Country <COUNTRY>, Extended-Address <EXTENDED_ADDRESS>, Locality <LOCALITY>, Organization <ORGANIZATION>, Phone-Number <PHONE_NUMBER>, Postal-Code <POSTAL_CODE>, Region <REGION>, Street-Address <STREET_ADDRESS>, Family-Name <FAMILY_NAME> and Given-Name <GIVEN_NAME>
    Then the HTTP status is bad request
    And Structured error message contains:
      | locality must not be blank              |
      | locality size must be between 1 and 200 |

    Examples:
      | COUNTRY | EXTENDED_ADDRESS | LOCALITY | ORGANIZATION | PHONE_NUMBER | POSTAL_CODE | REGION | STREET_ADDRESS   | FAMILY_NAME | GIVEN_NAME |
      | CA      |                  |          |              |              | V5C1N2      | BC     | 1234 Coco Avenue | Harris      | Ollie      |