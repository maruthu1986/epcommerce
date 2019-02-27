@smoketest @catalogManagement @catalog
Feature: Catalog Search

  Background:
    Given I sign in to CM as admin user

  Scenario Outline: Product search
    When I go to Catalog Management
    And I search for product name <PRODUCT_NAME>
    Then Product name <PRODUCT_NAME> should appear in result

    Examples:
      | PRODUCT_NAME  |
      | Samsung Focus |
