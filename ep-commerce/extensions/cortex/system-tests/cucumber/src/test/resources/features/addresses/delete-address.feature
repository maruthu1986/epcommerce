@Addresses
Feature: DELETE address

  Background:
    Given I login as a newly registered shopper

  Scenario Outline: Can delete address from user profile
    Given I am logged in as a public shopper
    And I get address form
    And I create address with Country <COUNTRY>, Extended-Address <EXTENDED_ADDRESS>, Locality <LOCALITY>, Organization <ORGANIZATION>, Phone-Number <PHONE_NUMBER>, Postal-Code <POSTAL_CODE>, Region <REGION>, Street-Address <STREET_ADDRESS>, Family-Name <FAMILY_NAME> and Given-Name <GIVEN_NAME>
    And I should see address matches the following
      | country-name     | <COUNTRY>          |
      | extended-address | <EXTENDED_ADDRESS> |
      | locality         | <LOCALITY>         |
      | organization     | <ORGANIZATION>     |
      | phone-number     | <PHONE_NUMBER>     |
      | postal-code      | <POSTAL_CODE>      |
      | region           | <REGION>           |
      | street-address   | <STREET_ADDRESS>   |
    When attempting a DELETE on the address
    Then the HTTP status is no content
    And I view my profile
    And I follow links addresses
    And there are no element links

    Examples:
      | COUNTRY | EXTENDED_ADDRESS | LOCALITY  | ORGANIZATION | PHONE_NUMBER | POSTAL_CODE | REGION | STREET_ADDRESS | FAMILY_NAME | GIVEN_NAME    |
      | CA      | 1234             | Vancouver | Company Inc  | 555-555-5555 | V5C1N2      | BC     | 123 Broadway   | Customer    | Public Tester |

  Scenario: Cannot delete address of a different shopper
    Given I create address with Country CA, Extended-Address extended address, Locality Vancouver, Organization Company Inc, Phone-Number 555-555-5555, Postal-Code V5C1N2, Region BC, Street-Address 123 Broadway, Family-Name Customer and Given-Name Public Tester
    And I view my profile
    And I follow links addresses
    And there are 1 links of rel element
    And open the element with field address containing 123 Broadway
    And save the address uri
    When I have authenticated as a newly registered shopper
    And attempt to delete the first shopper's address
    Then the HTTP status is forbidden
    And I re-authenticate on scope mobee with the original registered shopper
    And I view my profile
    And I follow links addresses
    And there are 1 links of rel element
