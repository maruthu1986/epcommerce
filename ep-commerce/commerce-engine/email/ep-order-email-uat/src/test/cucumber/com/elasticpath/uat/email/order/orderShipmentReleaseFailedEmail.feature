# language: en
@orderShipmentReleaseFailedEmail

Feature: Order Shipment Release Failed Email
  In order to be alerted of the problem with a customer's order so that I can investigate and remedy it,
  as the Store Administrator,
  I want to be sent an email when an order shipment release attempt fails.

  Background:
	Given email sending is enabled

  Scenario: an order shipment release fails, and an email is sent to the Store Administrator
	Given I am an administrator of Store "Test Store"
	And my email address store.admin@example.org is registered as the Store's Administrator email
	And a customer has previously made a purchase
	When releasing the shipment fails
	Then I should receive 1 email in my store.admin@example.org inbox
	And the subject of the email should be "Order Shipment Release Failure"
	And the email should contain the order number
	And the email should contain the shipment number