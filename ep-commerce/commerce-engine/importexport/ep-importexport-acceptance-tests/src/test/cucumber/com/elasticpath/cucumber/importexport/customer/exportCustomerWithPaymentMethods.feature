# language: en
@exportCustomerWithPaymentMethods
Feature: Export a customer with payment methods
  In order to export existing CE customers with payment methods
  As a QA,
  I want to export a customer's payment methods for testing purposes

Scenario: Export a customer's payment methods
  Given a customer exists with payment methods A and B and A is the default
  When I export the customer
  Then the exported customer will have payment methods A and B and A is the default


Scenario: Export a customer with no payment methods
  Given a customer exists with no payment methods
  When I export the customer
  Then the exported customer will have no payment methods
