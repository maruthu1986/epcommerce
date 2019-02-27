# language: en
@checkout
Feature: Retrieve payment tokens for a customer
  In order to complete a purchase in a PCI compliant manner
  As a customer
  I want to be able to use one of my payment token to create a purchase

  Background:
    Given a customer with payment tokens and a default selected

  Scenario: Use a customer's default payment token to create a purchase
    Given an order is created with the default payment token
    When the order is submitted
    Then a purchase should be created from the order with the same payment token

  Scenario: Use a different payment token than the customer's default to create a purchase
    Given an order is created with one of the customer's tokens
    When the order is submitted
    Then a purchase should be created from the order with the same payment token




