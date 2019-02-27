# language: en
@cmUserPasswordResetEmail

Feature: CM User Password Reset Email
  As a Commerce Manager or CSR,
  When my password is reset,
  I need to be sent an email with my username and temporary password that I can use to gain access to the system.
  
  Background:
    Given email sending is enabled
    Given I am a CM User with the email address cmuser@test.com
	
  Scenario: a CM User requests a password reset
  	When I request a CM password reset
  	Then I should receive 1 email in their cmuser@test.com inbox
  	And the subject of the email should be "Elastic Path Commerce Password Reset"
  	And the email should contain the CM User's username