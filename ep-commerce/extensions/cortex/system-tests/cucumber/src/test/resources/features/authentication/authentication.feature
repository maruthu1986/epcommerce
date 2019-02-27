@Authentication @OAuth2

Feature: Authentication and Authorization

  Scenario Outline: Registered User Authentication
    When I authenticate with <SCENARIO> username <USER-NAME> and password <PASSWORD> and role <ROLE> in scope <SCOPE>
    Then the HTTP status is <RESPONSE>

    Examples:
      | SCENARIO              | USER-NAME                             | PASSWORD          | ROLE       | SCOPE        | RESPONSE     |
      | successful login      | ben.boxer@elasticpath.com             | password          | REGISTERED | mobee        | OK           |
      | mixed case user       | MiXedCase.user@elasticpath.com        | password          | REGISTERED | mobee        | OK           |
      | disabled user         | disabled.user@elasticpath.com         | password          | REGISTERED | mobee        | unauthorized |
      | invalid password      | ben.boxer@elasticpath.com             | IncorrectPassword | REGISTERED | mobee        | unauthorized |
      | invalid scope         | ben.boxer@elasticpath.com             | password          | REGISTERED | invalidScope | unauthorized |
      | invalid role          | ben.boxer@elasticpath.com             | password          | BADROLE    | mobee        | unauthorized |
      | invalid username      | incorrect.username@elasticpath.com    | password          | REGISTERED | mobee        | unauthorized |
      | user not on scope     | ben.boxer@elasticpath.com             | password          | REGISTERED | searchbee    | unauthorized |
      | pending approval user | pending.approval.user@elasticpath.com | password          | REGISTERED | mobee        | unauthorized |

  Scenario Outline: Public User Authentication
    When I am logged into scope <SCOPE> as a public shopper
    Then the HTTP status is <RESPONSE>

    Examples:
      | SCOPE        | RESPONSE     |
      | mobee        | OK           |
      | invalidScope | unauthorized |

  Scenario: Invalidate access token
    Given I have authenticated as a newly registered shopper
    And I GET /profiles/mobee/default
    And the HTTP status is OK
    When I invalidate the authentication
    Then the HTTP status is no content
    When I GET /profiles/mobee/default
    Then the HTTP status is unauthorized

  Scenario: Invalidate invalid access token
    Given I set Authorization header Bearer nonExistentToken
    When I invalidate the authentication
    Then the HTTP status is no content

  Scenario: Invalidate token with missing token
    When I invalidate the authentication
    Then the HTTP status is no content

  Scenario: Can retrieve shopper scoped resource with access token
    Given I have authenticated as a newly registered shopper
    When I GET /profiles/mobee/default
    Then the HTTP status is OK

  Scenario: Can retrieve non shopper scoped resource with access token
    Given I have authenticated as a newly registered shopper
    When I GET /searches/mobee
    Then the HTTP status is OK

  Scenario: Cannot retrieve any resource with access token of another scope
    Given I have authenticated on scope mobee as a newly registered shopper
    When I GET /searches/toastie
    Then the HTTP status is forbidden
    When I GET /profiles/toastie/default
    Then the HTTP status is forbidden

  Scenario: Cannot retrieve any resource with an invalid token
    Given I set Authorization header Bearer notAValidToken
    When I GET /profiles/mobee/default
    Then the HTTP status is unauthorized

  Scenario: Cannot retrieve any resource without access token
    When I GET /profiles/mobee/default
    Then the HTTP status is unauthorized

  Scenario Outline: Verify OAuth2 Content-Type Header
    Given I set Content-Type header <CONTENT_TYPE>
    When I POST to oauth2/tokens with request body grant_type=password&username=ben.boxer@elasticpath.com&password=password&role=REGISTERED&scope=mobee
    Then the HTTP status is <STATUS>

    Examples:
      | CONTENT_TYPE                                     | STATUS                 |
      | application/x-www-form-urlencoded; charset=utf-8 | OK                     |
      | application/json                                 | unsupported media type |

  Scenario Outline: Unsuccessful Auth with missing and unsupported fields
    Given I set Content-Type header application/x-www-form-urlencoded; charset=utf-8
    And I POST to oauth2/tokens with request body <REQUEST_BODY>
    Then the HTTP status is <RESPONSE>

    Examples:
      | REQUEST_BODY                                                                                                   | RESPONSE    |
      | grant_type=password&username=ben.boxer@elasticpath.com&password=password&role=REGISTERED&scope=mobee           | OK          |
      | grant_type=password&username=ben.boxer@elasticpath.com&role=REGISTERED&scope=mobee                             | bad request |
      | grant_type=password&username=ben.boxer@elasticpath.com&password=&role=REGISTERED&scope=mobee                   | bad request |
      | grant_type=password&username=ben.boxer@elasticpath.com&password=password&role=&scope=mobee                     | bad request |
      | grant_type=password&username=ben.boxer@elasticpath.com&password=password&scope=mobee                           | bad request |
      | grant_type=password&username=ben.boxer@elasticpath.com&password=password&role=REGISTERED&scope=                | bad request |
      | grant_type=password&username=ben.boxer@elasticpath.com&password=password&role=REGISTERED                       | bad request |
      | grant_type=password&username=&password=password&role=REGISTERED&scope=mobee                                    | bad request |
      | grant_type=password&password=password&role=REGISTERED&scope=mobee                                              | bad request |
      | grant_type=&username=ben.boxer@elasticpath.com&password=password&role=REGISTERED&scope=mobee                   | bad request |
      | username=ben.boxer@elasticpath.com&password=password&role=REGISTERED&scope=mobee                               | bad request |
      | grant_type=invalid_grant_type&username=ben.boxer@elasticpath.com&password=password&role=REGISTERED&scope=mobee | bad request |