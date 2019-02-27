@smoketest @datapolicy
Feature: Data Policy User Role Permissions

  Scenario: Data Policy user should have access to Data Policy activities only
    Given I sign in to CM as datapolicyuser with password 111111
    When I go to Data Policies
    Then Data policies toolbar is visible
    And I should not have access to the following Configurations
      | System Administration |
      | Users                 |
      | Customer Profiles     |
      | Payment Methods       |
      | Shipping              |
      | Stores                |
      | Taxes                 |
      | Warehouses            |

  Scenario: Config user should Not have access to Data Policy activities
    When I sign in to CM as configuration_user_without_data_policy with password 111111
    Then I have access to Configuration
    And I should not have access to the following Configurations
      | Data Policies |
    And I should have access to the following Configurations
      | System Administration |
      | Users                 |
      | Customer Profiles     |
      | Payment Methods       |
      | Shipping              |
      | Stores                |
      | Taxes                 |
      | Warehouses            |