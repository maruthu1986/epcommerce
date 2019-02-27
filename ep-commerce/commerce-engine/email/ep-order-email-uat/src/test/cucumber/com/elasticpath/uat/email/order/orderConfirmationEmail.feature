# language: en
@orderConfirmationEmail

Feature: Order Confirmation Email
  So that I am sure my order has been received correctly,
  as a customer,
  I want to be sent an email containing all relevant order details that I can keep for my own records.

  Background:
    Given email sending is enabled
    And I am shopping in Store "Test Store"
    And the Store supports the en_US locale
    And my shopping locale is en_US
    And my email address customer@example.org has been registered in my user account

  Scenario: An order confirmation email is sent after a customer makes a purchase
	Given I have an item in my shopping cart
    When I successfully purchase my shopping cart contents
    Then I should receive 1 email in my customer@example.org inbox
    And the subject of the email should be "Order Confirmation"
    And the email should contain the order number
    And the email should contain the order total
    And the email should contain the order date
    And the email should contain the order items
    And the email should contain the order shipping address