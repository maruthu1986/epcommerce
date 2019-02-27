#TODO: investigate way to test time fields
@Items

Feature: Items with components

  Background:
    Given I am logged in as a public shopper

  Scenario Outline: Item components represent bundle constituents
    Given a bundle item with code <PURCHASABLE_NESTED_BUNDLE> that has <NUMBER_OF_COMPONENTS> constituents
    When I look up an item with code <PURCHASABLE_NESTED_BUNDLE>
    Then the item has <NUMBER_OF_COMPONENTS> components

    Examples:
      | PURCHASABLE_NESTED_BUNDLE | NUMBER_OF_COMPONENTS |
      | mb_2893033                | 2                    |

  Scenario Outline: Bundle constituents can be bundles
    Given a nested bundle with code <PURCHASABLE_NESTED_BUNDLE> that has a bundle constituent with name <NESTED_BUNDLE>
    When I look up an item with code <PURCHASABLE_NESTED_BUNDLE>
    And I examine the nested bundle component <NESTED_BUNDLE>
    Then the nested bundle component has <NUMBER_OF_NESTED_COMPONENTS> components

    Examples:
      | PURCHASABLE_NESTED_BUNDLE | NESTED_BUNDLE | NUMBER_OF_NESTED_COMPONENTS |
      | mb_2893033                | New Movies    | 3                           |

  Scenario Outline: A bundle component that is not sold separately is restricted from being purchased from outside the bundle
    Given a nested bundle with code <PURCHASABLE_NESTED_BUNDLE> that has a bundle constituent with name <NESTED_BUNDLE>
    And nested bundle <NESTED_BUNDLE> has a component <NESTED_BUNDLE_COMPONENT> that is not sold separately
    And I look up an item with code <PURCHASABLE_NESTED_BUNDLE>
    When I attempt to add the nested bundle <NESTED_BUNDLE> component <NESTED_BUNDLE_COMPONENT> to cart
    Then I am prevented from adding the item to the cart

    Examples:
      | PURCHASABLE_NESTED_BUNDLE | NESTED_BUNDLE | NESTED_BUNDLE_COMPONENT |
      | mb_2893033                | New Movies    | The Social Network      |

  Scenario Outline: Bundle components refer to an underlying standalone item
    Given a bundle with code <PURCHASABLE_BUNDLE> that has a bundle constituent with name <STANDALONE_ITEM>
    When I look up an item with code <PURCHASABLE_BUNDLE>
    Then the component has a reference to the standalone item <STANDALONE_ITEM>

    Examples:
      | PURCHASABLE_BUNDLE | STANDALONE_ITEM    |
      | mb_6789012_sku     | The Social Network |

  Scenario Outline: Bundle with a selection rule of Select All add to Cart
    Given a bundle with a selection rule of Select All
    When lookup the bundle by <PURCHASABLE_BUNDLE> and add the <PURCHASABLE_BUNDLE> to cart
    And the <PURCHASABLE_BUNDLE> will be added to cart as a root LineItem
    Then all of the <PURCHASABLE_BUNDLE> constituents will be added to cart as DependentLineItems

    Examples:
      | PURCHASABLE_BUNDLE |
      | mb_6789012_sku     |

  Scenario Outline: Bundle is removed, as well as all of its DepedentLineItems
    Given a cart containing a <PURCHASABLE_BUNDLE> and its Constituents
    When regardless of the type of selection rule of the Bundle, remove the <PURCHASABLE_BUNDLE> from the Cart
    Then the Bundle is removed, as well as all of its DepedentLineItems

    Examples:
      | PURCHASABLE_BUNDLE                         |
      | bundleWithPhysicalAndDigitalComponents_sku |

  Scenario Outline: a non-Bundle Item will present empty DependentLineItems resource
    Given a non-Bundle Item, lookup the item by <SKU_CODE> and add the Item to Cart
    Then the item will be added to cart and will present a link to the DependentLineItems resource, but the resource will be empty

    Examples:
      | SKU_CODE  |
      | tt0373883 |
  Scenario Outline: Bundle in the Cart with Dependent Line Items that are NOT Bundles themselves, each of their DependentLineItems are empty
    Given a <PURCHASABLE_BUNDLE> in the Cart with Dependent Line Items that are NOT Bundles themselves (leaf items)
    When navigate to each of their DependentLineItems link
    Then the DependentLineItems link will be present, but will be empty on all leaf items

    Examples:
  | PURCHASABLE_BUNDLE                         |
  | bundleWithPhysicalAndDigitalComponents_sku |

  Scenario Outline: Bundle in the Cart with Dependent Line Items, all Order need infos solved and purchase complete
    Given a <PURCHASABLE_BUNDLE> in the Cart with Dependent Line Items
    When all Order need infos solved
    And complete the purchase with the <PURCHASABLE_BUNDLE> in the Cart
    Then purchase is completed successfully
    And  the LineItems structure in under the created Purchase is equivalent to the one in the Cart

    Examples:
      | PURCHASABLE_BUNDLE                         |
      | bundleWithPhysicalAndDigitalComponents_sku |