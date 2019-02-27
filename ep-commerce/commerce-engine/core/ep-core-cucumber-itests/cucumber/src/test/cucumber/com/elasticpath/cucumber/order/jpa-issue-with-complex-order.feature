@Orders
Feature: Test a workaround for an OpenJPA bug

  There is an OpenJPA issue and a workaround for it that the scenario below attempts to test.

  The problem is that "parent" field of OrderSku entity (which is a recursive reference) doesn't get eagerly loaded by OpenJPA
  even though FetchGroup which is used with request is configured to load it eagerly with infinite recursion depth. The workaround
  is to load "parent" fields explicitly inside a transactional method by calling respective getter methods (getParent).

  Background:
    The order with number 10000 used below contains `galaxys2withphoneplan` SKU which consists of 4 items: one physical, one digital and
    two bundles (contents identical, but with different configuration options for them - selection rule and budle price adjustment).
    These latter two bundles contain 3 Frequency-based products with MONTHLY (nba, facebook and spotify)

    Given the Order number 10000 contains a complex bundle consisting physical, digital and bundles

  Scenario: Read the order from the DB and try to call getShoppingItems method that used to fail.
    When I read this order from the database
    Then the root shopping items count should be 1