# language: en
@import
@inventory
Feature: Import inventory
  As Operations, I want to import inventory details from the file system

Scenario: Import inventory records
  Given A SKU with code invSku1 that exists in a warehouse with code MainWarehouse
  When I import inventory into MainWarehouse for invSku1 with on-hand quantity 3 and allocated quantity 2
  Then the inventory for invSku1 in warehouse MainWarehouse should show an on-hand quantity of 3 and an allocated quantity of 2


