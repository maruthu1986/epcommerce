@Profile
Feature: Create new shopper profile

  Scenario Outline: Create new shopper and verify family-name and given-name correctly set
    When I create a new shopper profile with family-name <FAMILY_NAME>, given-name <GIVEN_NAME>, password <PASSWORD>, and unique user name
    And the HTTP status is OK, created
    And I authenticate with newly created shopper
    Then I should see my profile name as family-name <FAMILY_NAME> and given-name <GIVEN_NAME>

    Examples:
      | FAMILY_NAME    | GIVEN_NAME    | PASSWORD |
      | testFamilyName | testGivenName | password |

  Scenario Outline: Create new profile with existing username
    When I create a new shopper profile with family-name <FAMILY_NAME>, given-name <GIVEN_NAME>, password <PASSWORD>, and user name <USER_NAME> in scope mobee
    Then the HTTP status is conflict
    And I should see validation error message with message type, message id, and debug message
      | messageType | messageId                     | debugMessage                                   |
      | error       | profile.userid.already.exists | Customer with the given user Id already exists |

    Examples:
      | FAMILY_NAME    | GIVEN_NAME    | PASSWORD | USER_NAME                     |
      | testFamilyName | testGivenName | password | oliver.harris@elasticpath.com |

 Scenario Outline: Create new profile with existing username in different scopes
    When I create a new shopper profile with family-name <FAMILY_NAME>, given-name <GIVEN_NAME>, password <PASSWORD>, and user name <USER_NAME> in scope mobee
    And I create a new shopper profile with family-name <FAMILY_NAME>, given-name <GIVEN_NAME>, password <PASSWORD>, and user name <USER_NAME> in scope kobee
    Then the HTTP status is conflict
    And I should see validation error message with message type, message id, and debug message
      | messageType | messageId                     | debugMessage                                   |
      | error       | profile.userid.already.exists | Customer with the given user Id already exists |

   Examples:
     | FAMILY_NAME          | GIVEN_NAME          | PASSWORD | USER_NAME     |
     | testFamilyNameScopes | testGivenNameScopes | 11111111 | scopes@ep.com |

  Scenario Outline: Create new profile with invalid field values
    When I create a new shopper profile with family-name <FAMILY_NAME>, given-name <GIVEN_NAME>, password <PASSWORD>, and user name <USER_NAME> in scope mobee
    Then the HTTP status is bad request
    And I should see validation error message with message type, message id, and debug message
      | messageType | messageId  | debugMessage    |
      | error       | <ERROR_ID> | <DEBUG_MESSAGE> |

    Examples:
      | FAMILY_NAME    | GIVEN_NAME    | PASSWORD | USER_NAME         | ERROR_ID                   | DEBUG_MESSAGE                                          |
      | testFamilyName | testGivenName | 111      | validEmail@ep.com | field.invalid.size         | Password must be between 8 to 255 characters inclusive |
      | testFamilyName | testGivenName | password | invalidEmail      | field.invalid.email.format | not a well-formed email address                        |
      |                | testGivenName | password | validEmail@ep.com | field.required             | family-name attribute is required                      |
      | testFamilyName |               | password | validEmail@ep.com | field.required             | given-name attribute is required                       |
      | testFamilyName | testGivenName |          | validEmail@ep.com | field.required             | Password must not be blank                             |

  Scenario Outline: POST to registration with invalid JSON body
    When I am logged in as a public shopper
    When I POST to registration with body <JSON>
    Then the HTTP status is bad request

    Examples:
      | JSON                                                                                                       |
      | {"family-name":"Kitty","given-name":"Hello","username":"Hello.Kitty@elasticpath.com"}                      |
      | {"family-name":"Kitty","given-name":"Hello"}                                                               |
      | {"family-name":"kitty","givenname":"Hello","username":"Hello.Kitty@elasticpath.com","password":"password"} |
      | {}                                                                                                         |

  Scenario Outline: Registered shopper should not be able to register new shopper
    When I have authenticated as a newly registered shopper
    When I POST to registration with body <JSON>
    Then the HTTP status is forbidden

    Examples:
      | JSON                                                                                                        |
      | {"family-name":"kitty","given-name":"Hello","username":"Hello.Kitty@elasticpath.com","password":"password"} |

