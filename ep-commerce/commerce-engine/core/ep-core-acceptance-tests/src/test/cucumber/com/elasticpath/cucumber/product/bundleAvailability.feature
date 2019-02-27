Feature: Bundle availability
  In order bundles to work like products
  As a store manager
  I want the bundle availability to reflect the worst constituent

  Scenario: Bundle is out of stock when one of its constituents is out of stock
    Given product A with
      | availability | when in stock |
      | in stock     | no            |
    Given product B with
      | availability | for pre-order |
      | in stock     | yes           |
    When bundle X is created with product A and B
    Then bundle X is out of stock
