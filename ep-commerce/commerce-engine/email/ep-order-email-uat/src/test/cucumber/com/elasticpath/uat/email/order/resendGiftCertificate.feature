# language: en
@resendGiftCertificateEmail

Feature: Resend Gift Certificate Email
  As a customer, when I purchase a gift certificate for somebody,
  I want to be able to have the gift certificate resent 
  in the event that the original email has been deleted or the original recipient email address is incorrect.

  Background:
	Given I am shopping in Store "Test Store"
	And the Store supports the en_US locale
	And my shopping locale is en_US
	And I have added a Gift Certificate for "Guy Recipient <wrongAddress@example.org>" to my shopping cart
	And I successfully purchase my shopping cart contents
	And email sending is enabled
	

  Scenario: A gift certificate email is resent to a new email address
	When the gift certificate email is resent to correctAddress@example.org
	Then the recipient should receive 1 email in their correctAddress@example.org inbox
	And the subject of the email should contain "Gift Certificate"
	And the email should contain the gift certificate code
	And the email should contain the gift certificate amount
	And the email should contain the gift certificate sender name
	And the email should contain the gift certificate recipient name