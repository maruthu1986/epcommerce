# language: en
@orderShipmentConfirmationEmail

Feature: Order Shipment Confirmation Email
  So that I know when to expect my shipment to arrive,
  as a customer,
  I want to be sent an email containing all relevant details when a shipment ships

  Background:
	Given email sending is enabled
	And I am a customer of Store "Test Store"
	And the Store supports the en_US locale
	And my shopping locale is en_US
	And my email address customer@example.org has been registered in my user account

  Scenario: An order shipment confirmation email is sent after the shipment is completed
  	Given I have previously made a purchase
	When the shipment ships with tracking code "TRACK-001"
	Then I should receive 1 email(s) in my customer@example.org inbox
	And the subject of the email should be "Order Shipment Confirmation"
	And the email should contain the order number
	And the email should contain the shipment number
	And the email should contain the date the shipment shipped
	And the email should contain the shipment method
	And the email should contain the shipment tracking code "TRACK-001"
  	And the email should contain the order shipping address
	And the email should contain the shipment total