# language: en
@developer
@importexport
Feature: The Import Export (IE) CLI should work like other command line tools.

  Scenario: The IE CLI should return a successful result when a valid import is run
	Given an import configuration
	When I import the customers into the database using the IE CLI
	Then the CLI returns a successful response code

  Scenario: The IE CLI should return a successful result when a valid export is run
    Given an export configuration
    When I export the customers from the database using the IE CLI
    Then the CLI returns a successful response code