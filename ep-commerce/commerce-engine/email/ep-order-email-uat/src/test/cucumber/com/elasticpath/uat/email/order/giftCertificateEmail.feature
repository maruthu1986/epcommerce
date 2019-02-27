# language: en
@giftCertificateEmail

Feature: Gift Certificate Email
  As a customer, when I purchase a gift certificate for somebody,
  I want the recipient to be sent an email containing all relevant details,
  so that they are informed that they have been given a gift,
  and to enable them to make use of the gift certificate.

  Background:
	Given email sending is enabled
	And I am shopping in Store "Test Store"
	And the Store supports the en_US locale
	And my shopping locale is en_US

  Scenario: A gift certificate email is sent to the intended gift certificate recipient after the gift certificate is purchased
	Given I have added a Gift Certificate for "Guy Recipient <recipient@example.org>" to my shopping cart
	When I successfully purchase my shopping cart contents
	Then the recipient should receive 1 email in their recipient@example.org inbox
	And the subject of the email should contain "Gift Certificate"
	And the email should contain the gift certificate code
	And the email should contain the gift certificate amount
	And the email should contain the gift certificate sender name
	And the email should contain the gift certificate recipient name