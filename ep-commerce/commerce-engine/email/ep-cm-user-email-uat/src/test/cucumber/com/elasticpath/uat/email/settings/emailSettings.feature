# language: en
#@emailSettings
@cmUserPasswordChangeEmail
Feature: Email Settings
 As Operations, I want to change email settings
 without having to restart the server.


  Scenario: Email settings can be updated.
	Given email sending is disabled
	And email sending is updated to be enabled
	When a new CM User is created with the email address newCmUser2@test.com
	Then a user should receive 1 email in their newCmUser2@test.com inbox