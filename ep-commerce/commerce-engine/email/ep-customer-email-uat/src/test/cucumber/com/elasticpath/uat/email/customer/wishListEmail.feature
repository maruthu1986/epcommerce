# language: en
@wishListEmail

Feature: Wish List Email
  In order to increase sales,
  As a Store Manager,
  I want customers to be able to email their wish list contents to a third party,
  to encourage them to make purchases on behalf of the customer.

  Background:
	Given email sending is enabled
	And the customer is shopping in Store "Test Store"
	And the Store supports the en_US locale
	And the customer's shopping locale is en_US

	Scenario: a customer emails the contents of their wish list to a third party.
	  Given the customer has items in their wish list
	  When the customer sends their wish list to the email address wishlist.recipient@example.org
	  Then the recipient should receive 1 email in their wishlist.recipient@example.org inbox
	  And the subject of the email should be "My Wish List"
	  And the email should contain the wish list sender name
	  And the email should contain the wish list items