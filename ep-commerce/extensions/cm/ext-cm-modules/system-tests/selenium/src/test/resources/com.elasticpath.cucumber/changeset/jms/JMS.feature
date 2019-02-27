@changeset @jms-tests
Feature: JMS publish message

  @lockAndFinalize
  Scenario: Publish change set sends a message to JMS queue
#    Listening to JMS Queue
    Given I am listening to ep.changesets queue
    And I sign in to CM as admin user
    And I create and select the newly created change set CSetCI_JMS
    And I go to Catalog Management
    And I create a new virtual catalog with following details
      | catalogName   | language |
      | ATest Catalog | English  |
    And I delete newly created virtual catalog
    When I lock and publish latest change set
#    Reading from JMS Queue
    And I read ep.changesets message from queue
    Then the json guid value should be same as change set guid
    And json eventType should contain following values
      | key  | value                        |
      | name | CHANGE_SET_READY_FOR_PUBLISH |

