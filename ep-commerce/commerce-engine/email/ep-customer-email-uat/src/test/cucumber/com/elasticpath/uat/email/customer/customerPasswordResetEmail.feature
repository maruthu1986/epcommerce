Feature: Customer Password Reset Email
  To reduce barriers for customers making purchases in my store,
  As a Store Manager,
  When a customer has forgotten their password, I want to them to be sent an email containing a new password that they can use to log into the Store.

  Background:
	Given email sending is enabled
	And the customer is shopping in Store "Test Store"
	And the Store supports the en_US locale
	And the customer's shopping locale is en_US
	And the customer's email address customer@example.org has been registered in their user account

  Scenario: a customer has their password reset and is sent the new password in an email.
	When the customer's password is reset
	Then the customer should receive 1 email in their customer@example.org inbox
	And the subject of the email should be "Your Password Reminder"
	And the email should contain my name
	And the email should contain the new password
	And the email should contain today's date
	And the email should contain the Store URL
