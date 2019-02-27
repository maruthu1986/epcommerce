@Addresses
Feature: Create Address

  Scenario Outline: Public user can create address
    When I am logged in as a public shopper
    And I get address form
    And I create address with Country <COUNTRY>, Extended-Address <EXTENDED_ADDRESS>, Locality <LOCALITY>, Organization <ORGANIZATION>, Phone-Number <PHONE_NUMBER>, Postal-Code <POSTAL_CODE>, Region <REGION>, Street-Address <STREET_ADDRESS>, Family-Name <FAMILY_NAME> and Given-Name <GIVEN_NAME>
    Then I should see address matches the following
      | country-name     | <COUNTRY>          |
      | extended-address | <EXTENDED_ADDRESS> |
      | locality         | <LOCALITY>         |
      | organization     | <ORGANIZATION>     |
      | phone-number     | <PHONE_NUMBER>     |
      | postal-code      | <POSTAL_CODE>      |
      | region           | <REGION>           |
      | street-address   | <STREET_ADDRESS>   |
    And I should see name matches the following
      | family-name | <FAMILY_NAME> |
      | given-name  | <GIVEN_NAME>  |

    Examples:
      | COUNTRY | EXTENDED_ADDRESS | LOCALITY  | ORGANIZATION | PHONE_NUMBER | POSTAL_CODE | REGION | STREET_ADDRESS | FAMILY_NAME | GIVEN_NAME    |
      | CA      | 1234             | Vancouver | Company Inc  | 555-555-5555 | V5C1N2      | BC     | 123 Broadway   | Customer    | Public Tester |

  Scenario Outline: Auto set default shipping and billing address
    Given I am logged in as a public shopper
    When I create address with Country <COUNTRY>, Extended-Address <EXTENDED_ADDRESS>, Locality <LOCALITY>, Organization <ORGANIZATION>, Phone-Number <PHONE_NUMBER>, Postal-Code <POSTAL_CODE>, Region <REGION>, Street-Address <STREET_ADDRESS>, Family-Name <FAMILY_NAME> and Given-Name <GIVEN_NAME>
    And I get the default shipping address
    Then the address matches the following
      | country-name     | <COUNTRY>          |
      | extended-address | <EXTENDED_ADDRESS> |
      | locality         | <LOCALITY>         |
      | postal-code      | <POSTAL_CODE>      |
      | region           | <REGION>           |
      | street-address   | <STREET_ADDRESS>   |
    And I get the default billing address
    And the address matches the following
      | country-name     | <COUNTRY>          |
      | extended-address | <EXTENDED_ADDRESS> |
      | locality         | <LOCALITY>         |
      | postal-code      | <POSTAL_CODE>      |
      | region           | <REGION>           |
      | street-address   | <STREET_ADDRESS>   |

    Examples:
      | COUNTRY | EXTENDED_ADDRESS | LOCALITY  | POSTAL_CODE | REGION | STREET_ADDRESS | FAMILY_NAME | GIVEN_NAME    |
      | CA      | 1234             | Vancouver | V5C1N2      | BC     | 123 Broadway   | Customer    | Public Tester |

  Scenario Outline: Cannot access or delete address of different shopper
    Given I have authenticated as a newly registered shopper
    And I get address form
    And I create address with Country <COUNTRY>, Extended-Address <EXTENDED_ADDRESS>, Locality <LOCALITY>, Organization <ORGANIZATION>, Phone-Number <PHONE_NUMBER>, Postal-Code <POSTAL_CODE>, Region <REGION>, Street-Address <STREET_ADDRESS>, Family-Name <FAMILY_NAME> and Given-Name <GIVEN_NAME>
    And follow the response
    And save the address uri
    When I authenticate as a registered shopper harry.potter@elasticpath.com with the default scope
    And attempt to access the other shoppers address
    Then the HTTP status is not found
    And attempt to delete the other shoppers address
    And the HTTP status is forbidden

    Examples:
      | COUNTRY | EXTENDED_ADDRESS | LOCALITY  | POSTAL_CODE | REGION | STREET_ADDRESS | FAMILY_NAME | GIVEN_NAME    |
      | CA      | 1234             | Vancouver | V5C1N2      | BC     | 123 Broadway   | Customer    | Public Tester |

  Scenario: Creating a duplicate address succeeds POST but doesn't create a new element
    Given I have authenticated as a newly registered shopper
    And I get address form
    And I create address with Country CA, Extended-Address Identical Address, Locality Vancouver, Organization Company Inc, Phone-Number 555-555-5555, Postal-Code V5C1N2, Region BC, Street-Address 123 Broadway, Family-Name Customer and Given-Name Public Tester
    And I view my profile
    And I follow links addresses
    And there are 1 links of rel element
    When I create address with Country CA, Extended-Address Identical Address, Locality Vancouver, Organization Company Inc, Phone-Number 555-555-5555, Postal-Code V5C1N2, Region BC, Street-Address 123 Broadway, Family-Name Customer and Given-Name Public Tester
    Then the HTTP status is OK
    And I view my profile
    And I follow links addresses
    And there are 1 links of rel element

  Scenario: Creating an address succeeds POST with status created
    Given I have authenticated as a newly registered shopper
    And I get address form
    And I create address with Country CA, Extended-Address Identical Address, Locality Vancouver, Organization Company Inc, Phone-Number 555-555-5555, Postal-Code V5C1N2, Region BC, Street-Address 123 Broadway, Family-Name Customer and Given-Name Public Tester
    Then the HTTP status is OK, created
    And I view my profile
    And I follow links addresses
    And there are 1 links of rel element