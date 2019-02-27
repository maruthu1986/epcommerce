# language: en
@resendOrderConfirmationEmail

Feature: Resend Order Confirmation Email
  As a customer, when I make a purchase,
  I want to be able to have the Order Confirmation Email resent 
  in the event that the original email has been deleted.

  Background:
    Given email sending is enabled
    And I am shopping in Store "Test Store"
    And the Store supports the en_US locale
    And my shopping locale is en_US
    And my email address customer@example.org has been registered in my user account
    And I have an item in my shopping cart

  Scenario: A gift certificate email is resent
    When I successfully purchase my shopping cart contents
	And the CSR resends the order confirmation email
	Then I should receive 2 email(s) in my customer@example.org inbox
	And the subject of the email should be "Order Confirmation"