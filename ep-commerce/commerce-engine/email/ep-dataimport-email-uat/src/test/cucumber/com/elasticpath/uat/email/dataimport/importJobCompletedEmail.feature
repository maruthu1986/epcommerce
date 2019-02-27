# language: en
@importJobCompletedEmail

Feature: Import Job Completed Email
  As a CM User,
  I want to be notified by email when an import job I have initiated completes,
  So I am aware of the status of the import job.
  
  Background:
    Given email sending is enabled
    And I am an administrator of Store "Test Store"
    And I am a CM User with the email address cmuser@test.com
    And a file named "customer_insert.csv" exists in the import directory
    And an import job is created for "customer_insert.csv"
	
  Scenario: an import job completes successfully and the initiating CM User is notified by email
  	When the import job has completed
  	Then a user should receive 1 email in their cmuser@test.com inbox
  	And the subject of the email should be "Import Status Report"
  	And the email should list 2 total rows, 2 successfully imported rows, 0 failed rows, 0 left rows 