# language: en
@rmaEmail

Feature: RMA Email
  As a customer, when I return or exchange an item from a previous order
  I want to be sent an email with the RMA details
  so that I am able to send my item back to the manufacturer or retailer
  which is required for me to receive my refund or replacement item.

  Background:
	Given email sending is enabled
	And I am a customer of Store "Test Store"
	And the Store supports the en_US locale
	And my shopping locale is en_US
	And my email address customer@example.org has been registered in my user account

  Scenario: the customer returns an item and receives an RMA email
	Given I have previously made a purchase
	And the purchase has been completed and delivered
	And items must be sent back to the manufacturer or retailer for a return to be made
	When I initiate a return of at least one item in the purchase
	Then I should receive 1 email in my customer@example.org inbox
	And the subject of the email should be "RMA Receipt"
	And the email should contain the return date
	And the email should contain the RMA number
	And the email should contain my name
	And the email should contain the name(s) of the order item(s) to be returned
	And the email should contain the SKU code(s) of the order item(s) to be returned
	And the email should contain the quantities of each item(s) to be returned
	And the email should contain the mail-back address

  Scenario: the customer returns an item, but physical returns are not required. An RMA email is sent.
	Given I have previously made a purchase
	And the purchase has been completed and delivered
	And items need not be sent back to the manufacturer or retailer for a return to be made
	When I initiate a return of at least one item in the purchase
	Then I should receive 1 email(s) in my customer@example.org inbox
	And the subject of the email should be "RMA Receipt"
	And the email should contain the return date
	And the email should contain the RMA number
	And the email should contain my name
	And the email should contain the name(s) of the order item(s) to be returned
	And the email should contain the SKU code(s) of the order item(s) to be returned
	And the email should contain the quantities of each item(s) to be returned
	And the email should contain the mail-back address

  Scenario: the customer exchanges an item and receives an RMA email and an order confirmation email.
	Given I have previously made a purchase
	And the purchase has been completed and delivered
	And items must be sent back to the manufacturer or retailer for a return to be made
	When I initiate an exchange of at least one item in the purchase
	Then I should receive 2 email in my customer@example.org inbox
	And the subject of one of the emails should be "RMA Receipt"
	And the subject of one of the emails should be "Order Confirmation"
	And the "RMA Receipt" email should contain the return date
	And the "RMA Receipt" email should contain the RMA number
	And the "RMA Receipt" email should contain my name
	And the "RMA Receipt" email should contain the name(s) of the order item(s) to be returned
	And the "RMA Receipt" email should contain the SKU code(s) of the order item(s) to be returned
	And the "RMA Receipt" email should contain the quantities of each item(s) to be returned
	And the "RMA Receipt" email should contain the mail-back address

  Scenario: the customer exchanges an item, but physical returns are not required.  An RMA email and an exchange order confirmation email are sent.
	Given I have previously made a purchase
	And the purchase has been completed and delivered
	And items need not be sent back to the manufacturer or retailer for a return to be made
	When I initiate an exchange of at least one item in the purchase
	Then I should receive 2 email in my customer@example.org inbox
	And the subject of one of the emails should be "RMA Receipt"
	And the subject of one of the emails should be "Order Confirmation"
	And the "RMA Receipt" email should contain the return date
	And the "RMA Receipt" email should contain the RMA number
	And the "RMA Receipt" email should contain my name
	And the "RMA Receipt" email should contain the name(s) of the order item(s) to be returned
	And the "RMA Receipt" email should contain the SKU code(s) of the order item(s) to be returned
	And the "RMA Receipt" email should contain the quantities of each item(s) to be returned
	And the "RMA Receipt" email should contain the mail-back address