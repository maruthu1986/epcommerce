# language: en
@customerRegistrationEmail

Feature: Customer Registration Email
  In order to promote my store and to encourage return visits and purchases,
  As a marketing manager,
  I want to send an email to each customer when they register that contains login details and store information.

  Background:
	Given email sending is enabled
	And the customer is shopping in Store "Test Store"
	And the Store supports the en_US locale
	And the customer's shopping locale is en_US

  Scenario: a customer registers and is sent a registration email.
	When a customer registers as a new user with email address customer@example.org
	Then the customer should receive 1 email in their customer@example.org inbox
	And the subject of the email should be "Create Account Confirmation"
	And the email should contain the customer account user ID
	And the email should contain the customer account creation date
	And the email should contain the customer account email address
	And the email should contain the Store URL

  # How is this different to the above scenario?  Is Cortex doing a different thing to CE?  If so, why?  Should CE be updated accordingly?
  Scenario: a previously anonymous customer registers and is sent a registration email.
	When an anonymous customer registers as a new user with email address customer@example.org
	Then the customer should receive 1 email in their customer@example.org inbox
	And the subject of the email should be "Create Account Confirmation"
	And the email should contain the customer account user ID
	And the email should contain the customer account creation date
	And the email should contain the customer account email address
	And the email should contain the Store URL

  Scenario: a CSR creates a new account on the customer's account, and the customer is sent a registration confirmation email.
	When a CSR creates a new customer account with email address customer@example.org
	Then the customer should receive 1 email in their customer@example.org inbox
	And the subject of the email should be "Customer Registration Confirmation"
	And the email should contain the Store URL