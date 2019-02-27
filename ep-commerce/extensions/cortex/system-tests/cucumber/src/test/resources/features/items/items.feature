@Items

Feature: Items

  Background:
    Given I am logged in as a public shopper

  Scenario Outline: Item definition displays a display name
    When I view the item definition for item with code <SKU_WITH_DEFINED_ATTRIBUTES>
    Then the field display-name has value <DISPLAY_NAME>

    Examples:
      | SKU_WITH_DEFINED_ATTRIBUTES | DISPLAY_NAME |
      | tt0970179_sku               | Hugo         |

  Scenario Outline: Item definition displays a list of attributes
    When I view the item definition for item with code <SKU_WITH_DEFINED_ATTRIBUTES>
    Then the SKU attributes contain
      | name   | display name    | display value                                                |
      | A00001 | Plot Keyword    | Robot, Orphan, 1030s, Train Station, Mystery, إنسان آلي      |
      | A00002 | Release Date    | 2011                                                         |
      | A00003 | Storyline       | The "Invention of Hugo Cabret" concerns a 12-year-old orphan |
      | A00004 | Languages       | Anglais, Français, Italien, Allemand                         |
      | A00005 | Opening DayTime | 2011                                                         |
      | A00006 | Color           | True                                                         |
      | A00007 | Runtime         | 127                                                          |
      | A00009 | Viewer's Rating | 8.12                                                         |
      | A00013 | Format          | 35 mm, 1.85 : 1                                              |

    Examples:
      | SKU_WITH_DEFINED_ATTRIBUTES |
      | tt0970179_sku               |

  Scenario Outline: Item does not display attributes that have no value defined
    Given an item with code <ITEM> has an attribute with name <ATTRIBUTE> with no value defined
    When I view the item definition for item with code <ITEM>
    Then the details do not contain an element with name <ATTRIBUTE>

    Examples:
      | ITEM          | ATTRIBUTE |
      | tt0970179_sku | Tagline   |

  Scenario Outline: Item display name is based on scope locale
    Given item <ITEM> has a display name of <DISPLAY_NAME> in language <LANGUAGE>
    When I request the item definition of <ITEM> in language <LANGUAGE>
    Then the field display-name has value <DISPLAY_NAME>

    Examples:
      | LANGUAGE | ITEM          | DISPLAY_NAME          |
      | fr-ca    | tt0970179_sku | les aventures de Hugo |
      | en-ca    | tt0970179_sku | Hugo                  |

  Scenario Outline: Item displays attributes based on scope locale
    Given item <ITEM> has an attribute <ATTRIBUTE_NAME> with value <ATTRIBUTE_VALUE> in language <LANGUAGE>
    When I request the item definition of <ITEM> in language <LANGUAGE>
    Then the attribute with display-name equal to <ATTRIBUTE_NAME> has display-value equal to <ATTRIBUTE_VALUE>

    Examples:
      | LANGUAGE | ITEM          | ATTRIBUTE_NAME | ATTRIBUTE_VALUE                                                                                                                                                                      |
      | fr-ca    | tt0970179_sku | Storyline      | L'invention de "Hugo Cabret" concerne un orphelin de 12 ans qui vit dans les murs d'une gare de Paris en 1930 et un mystère impliquant le garçon, son père décédé et un robot.       |
      | en-ca    | tt0970179_sku | Storyline      | The "Invention of Hugo Cabret" concerns a 12-year-old orphan who lives in the walls of a Paris train station in 1930 and a mystery involving the boy, his late father and a robot. " |

  Scenario Outline: Item with no attributes associated with it will not display details field
    Given item <ITEM> has no attributes
    When I view the item definition for item with code <ITEM>
    Then the field details is an empty array

    Examples:
      | ITEM     |
      | t384lkef |