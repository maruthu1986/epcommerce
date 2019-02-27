# language: en
@importCustomerWithPaymentMethods
Feature: Import a customer with payment tokens
  In order to import existing CE customers with payment tokens into my target database
  As a QA,
  I want to import a customer's payment tokens so that they are available as payment methods for testing

Scenario: Update a customer's list of payment methods (including defaults)
  Given a customer exists
  And an import with payment methods A,B and C and C is the default
  When I execute the import
  Then the customer will be updated with payment methods A,B and C and C is chosen as the default

Scenario: Update a customer's list of payment methods without a default specified
  Given a customer exists
  And an import with payment methods A,B and C and no default payment method
  When I execute the import
  Then  the customer will be updated with payment methods A,B, and C and A will be chosen as the default


Scenario: Clear the list of payment methods for a customer
  Given a customer exists with payment methods
  And an import with an empty collection of payment methods
  When I execute the import
  Then the customer is updated and has no payment methods

Scenario: Import payment methods with collection strategy that isn't CLEAR_COLLECTION throws an unsupported exception
  Given a customer exists
  And an import configured with a RETAIN_COLLECTION collection strategy for payment methods
  When I execute the import
  Then an unsupported operation exception is thrown