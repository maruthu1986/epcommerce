@Orders
Feature: Email address on order
  As a shopper,
  I want to be able to add an email address to my order on the fly,
  so that I can complete my purchase

  Background:
    Given I am logged in as a public shopper

  Scenario: Create an email for my order
    When I create an email for my order
    Then the email is created and selected for my order