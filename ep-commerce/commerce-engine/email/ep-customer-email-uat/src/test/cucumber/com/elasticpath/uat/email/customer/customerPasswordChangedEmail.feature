# language: en
@customerPasswordChangedEmail

Feature: Customer Password Changed Email
  In order to detect attempts to gain unauthorised control of my user account,
  as a Customer,
  I want to be sent an email informing me whenever my password is changed.

  Background:
	Given email sending is enabled
	And I am shopping in Store "Test Store"
	And the Store supports the en_US locale
	And my shopping locale is en_US
	And my email address customer@example.org has been registered in my user account

  Scenario: a customer updates their password and is sent a confirmation email.
	When I change my password
	Then I should receive 1 email in my customer@example.org inbox
	And the subject of the email should be "Password Change Confirmation"
	And the email should contain my name
	And the email should contain today's date
	And the email should contain the Store URL