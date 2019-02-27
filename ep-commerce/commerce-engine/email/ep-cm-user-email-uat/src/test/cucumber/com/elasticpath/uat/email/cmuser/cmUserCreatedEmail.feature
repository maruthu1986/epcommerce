# language: en
@cmUserCreatedEmail

Feature: CM User Created Email
  As a Commerce Manager or CSR,
  When a user account is created for me,
  I need to be sent an email with my username and temporary password that I can use to gain access to the system.
  
  Background:
    Given email sending is enabled
	
  Scenario: a CM User is created and is notified by email.
  	When a new CM User is created with the email address newCmUser@test.com
  	Then a user should receive 1 email in their newCmUser@test.com inbox
  	And the subject of the email should be "New Elastic Path Commerce Account"
  	And the email should contain the CM User's username
