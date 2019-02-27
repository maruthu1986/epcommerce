# language: en
@emailFileAttachments
Feature: Email File Attachments
  Emails should be able to include attachments.
  These can be required for customer communication, for example to include a .pdf receipt.
  It's also vital for emails to IT Operations personnel, who will be able to respond much quicker to issues if the notification email contains the
  relevant log files.

  Background:
	Given email sending is enabled

  Scenario: Emails may contain several attachment links
	Given an email message to be delivered
	And the email contains an attachment of file foo.txt
	And the email contains an attachment of file bar.txt
	When the email with recipient recipient@example.org is published to the ep.emails queue
    Then the recipient should receive 1 email in their recipient@example.org inbox
	And the email should contain an attachment with name foo.txt
	And the email should contain an attachment with name bar.txt

  Scenario: Attachments may be added as binaries
	Given an email message to be delivered
	And the email contains an attachment with the contents of file baz.png
	When the email with recipient recipient@example.org is published to the ep.emails queue
    Then the recipient should receive 1 email in their recipient@example.org inbox
	And the email should contain an attachment with the contents of file baz.png

  # @ignored because this scenario attempts to attach an attachment given a www URL; this is obviously not reliable
  @ignore
  Scenario: Attachments may be remote web URLs
	Given an email message to be delivered
	And the email contains an attachment of URL http://placehold.it/84x84
	When the email with recipient recipient@example.org is published to the ep.emails queue
    Then the recipient should receive 1 email in their recipient@example.org inbox
	And the email should contain an attachment with name 84x84