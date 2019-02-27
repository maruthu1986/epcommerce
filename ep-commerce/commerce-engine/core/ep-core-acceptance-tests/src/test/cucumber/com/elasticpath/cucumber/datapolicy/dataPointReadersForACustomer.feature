# language: en
@dataPointManagement
Feature: Data point management for customer profile.

  Background:
    Given a default customer

  Scenario: Read customer profile first name data point value
    Given a data point defined with location CUSTOMER_PROFILE and key CP_FIRST_NAME
    When I request the data point value belonging to customer
    Then I should see the value Test

  Scenario: Remove data point value for customer profile first name
    Given a data point defined with location CUSTOMER_PROFILE and key CP_FIRST_NAME
    When I remove the data point value belonging to customer
    Then there should no longer be a value for that data point for customer