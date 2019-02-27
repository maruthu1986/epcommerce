@ZoomPerformance @notready
Feature: Zoom performance test

  Scenario Outline: Zoom with a big query on a cart with many items should not create DB overhead
    Given I have authenticated as a newly registered shopper
    And I add following items to the cart
      | sony_bt_sku                                                |
      | 20off_shipping_sku                                         |
      | physical_product_with_lineitem_promotion_sku               |
      | physicalItemFromBundleWithPhysicalAndDigitalComponents_sku |
      | digitalItemFromBundleWithPhysicalAndDigitalComponents_sku  |
      | FocUSsku                                                   |
      | t384lkef                                                   |
      | mk34abef                                                   |
      | motox_sku                                                  |
      | physical_sku                                               |
      | tt0373883                                                  |
      | tt0926084_sku                                              |
      | tt0984938                                                  |
      | mb_3456789_sku                                             |
      | mb_8901234_sku                                             |
      | tt0034583_sku                                              |
      | tt1483735                                                  |
      | tt0068646_sku                                              |
      | tt0050083_sku                                              |
      | tt0162661                                                  |
      | mb_8974323_sku                                             |
      | mb_1234567_sku                                             |
      | mb_5678901_sku                                             |
      | mb_2893033                                                 |
      | mb_4324324_sku                                             |
      | mb_9012345_sku                                             |
      | tt0970179_sku                                              |
      | transformers01_sku                                         |
      | transformers02_sku                                         |
      | transformers03_sku                                         |
      | transformers04_sku                                         |
      | gravity_sku                                                |
      | alien_sku                                                  |
    And I start measuring db queries
    When I zoom to cart with a query <LARGE_ZOOM>
    And I stop measuring db queries
    Then total number of DB calls should be close to 1730 calls with allowed deviation of 0.5 percent
    And number of DB calls for table TPRODUCTSKU should be close to 252 calls with allowed deviation of 0.2 percent

    # Large zoom query from client project that puts a strain on the backend.
    Examples:
      | LARGE_ZOOM                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
      | lineitems:element,lineitems:element:item:definition,lineitems:element:item:price,lineitems:element:item:code,lineitems:element:availability,lineitems:element:price,lineitems:element:total,lineitems:element:appliedpromotions,lineitems:element:appliedpromotions:element,lineitems:element:appliedpromotions:element:discountedproductselection:discountedlineitem:item:code,lineitems:element:satisfiedpromotions,lineitems:element:satisfiedpromotions:element,lineitems:element:satisfiedpromotions:element:discountedproductselection,lineitems:element:satisfiedpromotions:element:discountedproductselection:discountableitem:availability,lineitems:element:satisfiedpromotions:element:discountedproductselection:discountableitem:code,lineitems:element:satisfiedpromotions:element:discountedproductselection:discountableitem:definition,lineitems:element:satisfiedpromotions:element:discountedproductselection:discountableitem:price,lineitems:element:satisfiedpromotions:element:discountedproductselection:discountableitem:total,lineitems:element:satisfiedpromotions:element:discountedproductselection:discountableitem:appliedpromotions,lineitems:element:satisfiedpromotions:element:discountedproductselection:discountableitem:appliedpromotions:element,order,order:total,order:tax,order:error,appliedpromotions,appliedpromotions:element,discount |