# language: en
@cmUserPasswordChangeEmail

Feature: CM User Password Change Email
  In order to detect attempts to gain unauthorised access to my user account,
  As a Commerce Manager or CSR,
  When my password has been changed,
  I want to be sent an email informing me of the change in password.
  
  Background:
    Given email sending is enabled
    Given I am a CM User with the email address cmuser@test.com
	
  Scenario: a CM User changes their own password
  	When I change my CM password to "NEWPASSWORD"
  	Then I should receive 1 email in my cmuser@test.com inbox
  	And the subject of the email should be "Elastic Path Commerce Password Changed"
  	And the email should contain the CM User's name 	