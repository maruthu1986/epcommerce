@smoketest @datapolicy
Feature: Store Setting Data Policy

  Background:
    Given I sign in to CM as admin user
    And I go to Configuration

  Scenario: Data Policy setting for store
    When I go to System Configuration
    And I enter setting name DataPolicies in filter
    Then I should see setting COMMERCE/STORE/enableDataPolicies in the filter result
    And Defined Value for Store MOBEE is true
    When I go to Stores
    And I edit store MOBEE in editor
    Then Data Policies for the store is enabled