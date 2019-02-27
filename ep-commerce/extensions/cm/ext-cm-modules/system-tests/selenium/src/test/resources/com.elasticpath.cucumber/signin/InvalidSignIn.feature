@smoketest @signin @invalidSignin
Feature: Invalid signin

  Scenario Outline: Invalid sign in to CM
    When I attempt to sign in with invalid credentials to CM as <INVALID_ID> with password <INVALID_PASSWORD>
    Then I should not be able to sign in

    Examples:
      | INVALID_ID | INVALID_PASSWORD |
      | abc        | 111111           |

  Scenario: Login, Logout and ReLogin
    Given I sign in to CM as admin with password 111111
    When I sign out
    Then I should be able to sign in again to CM as admin with password 111111

