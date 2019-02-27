# language: en
@reliableEmailDelivery
Feature: Reliable Email Delivery
  As Operations,
  I want email delivery to be reliable,
  so that an outage on the email server does not cause email messages to be lost.

  Scenario: The SMTP server is accessible so the email is sent on the first attempt.
	Given the SMTP server is available
	When a message representing an email with recipient recipient@example.org is published to the ep.emails queue
	Then there should exist 1 email in the recipient@example.org inbox

  Scenario: A brief network outage causes the SMTP temporarily to be inaccessible, and the retry mechanism successfully delivers the email.
	Given the SMTP server is unable to deliver messages to recipient@example.org
	And the email sending service is configured to retry delivery every 20 seconds up to a maximum of 5 attempts
	When a message representing an email with recipient recipient@example.org is published to the ep.emails queue
	Then no email is delivered
	And when the SMTP server becomes available again within the retry window
	Then there should exist 1 email in the recipient@example.org inbox

  Scenario: SMTP server is inaccessible for long enough for all retries to fail, so messages are redirected to a Dead Letter Queue.
	Given the SMTP server is unable to deliver messages to recipient@example.org
	And the email sending service is configured to retry delivery every 1 seconds up to a maximum of 5 attempts
	When a message representing an email with recipient recipient@example.org is published to the ep.emails queue
	And all retries are exhausted
	Then no email is delivered
	And the message is delivered to the ep.emails.dlq queue

  # This scenario has not yet been implemented fully.
  Scenario: A malformed message is published to the email queue and is rejected.
	When a message that does not represent a valid email message is published to the ep.emails queue
	Then no email is delivered
#	And the message is delivered to the ep.emails.fatal queue
