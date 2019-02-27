@Validation
Feature: Validate Update Cart Item Modifier Fields


  Scenario Outline: Should be able to update cart with all valid fields
    Given I login as a public shopper
    When I look up an item with code <ITEMCODE>
    Then I go to add to cart form
    And I add the item to the cart with quantity <QTY> and configurable fields:
      | allFieldTypes.boolean           | true                         |
      | allFieldTypes.date              | 2016-08-18                   |
      | allFieldTypes.dateTime          | 2016-08-18T10:15:30+04:00    |
      | allFieldTypes.decimal           | 13.99                        |
      | allFieldTypes.email             | harry.potter@elasticpath.com |
      | allFieldTypes.integer           | 12                           |
      | allFieldTypes.multiSelectOption | multiValue1                  |
      | allFieldTypes.shortText         | hello                        |
      | allFieldTypes.singleOption      | singleValue1                 |
    When I update <ITEMCODE> in cart with Quantity: <QTY> and Configurable Fields:
      | allFieldTypes.boolean           | <BOOLEAN>       |
      | allFieldTypes.date              | <DATE>          |
      | allFieldTypes.dateTime          | <DATETIME>      |
      | allFieldTypes.decimal           | <DECIMAL>       |
      | allFieldTypes.email             | <EMAIL>         |
      | allFieldTypes.integer           | <INTEGER>       |
      | allFieldTypes.multiSelectOption | <MULTI_OPTION>  |
      | allFieldTypes.shortText         | <SHORT_TEXT>    |
      | allFieldTypes.singleOption      | <SINGLE_OPTION> |
    Then the cart lineitem with itemcode <ITEMCODE> has quantity <QTY> and configurable fields as:
      | allFieldTypes.boolean           | <BOOLEAN>       |
      | allFieldTypes.date              | <DATE>          |
      | allFieldTypes.dateTime          | <DATETIME>      |
      | allFieldTypes.decimal           | <DECIMAL>       |
      | allFieldTypes.email             | <EMAIL>         |
      | allFieldTypes.integer           | <INTEGER>       |
      | allFieldTypes.multiSelectOption | <MULTI_OPTION>  |
      | allFieldTypes.shortText         | <SHORT_TEXT>    |
      | allFieldTypes.singleOption      | <SINGLE_OPTION> |

    Examples:
      | ITEMCODE      | QTY | BOOLEAN | DATE       | DATETIME                  | DECIMAL | EMAIL                        | INTEGER | MULTI_OPTION             | SHORT_TEXT | SINGLE_OPTION |
      | sscpwaft      | 1   | true    | 1996-10-22 | 1996-10-22T03:12:31+04:00 | 13.99   | harry.potter@elasticpath.com | 12      | multiValue1              | hello      | singleValue1  |
      | mscpwaftblack | 2   | false   | 2008-03-25 | 2008-03-25T10:15:44Z      | 15      | harry.potter@elasticpath.com | 5       | multiValue2              | bye        | singleValue3  |
      | mscpwaftwhite | 2   | false   | 2016-07-25 | 2016-08-18T11:19:12Z      | 22      | harry.potter@elasticpath.com | 52      | multiValue2, multiValue3 | bye        | singleValue2  |


  Scenario Outline: Should not be able to update cart with empty required fields
    Given I login as a public shopper
    When I look up an item with code <ITEMCODE>
    Then I go to add to cart form
    And I add the item to the cart with quantity <QTY> and configurable fields:
      | allFieldTypes.boolean           | true                         |
      | allFieldTypes.date              | 2016-08-18                   |
      | allFieldTypes.dateTime          | 2016-08-18T10:15:30+04:00    |
      | allFieldTypes.decimal           | 13.99                        |
      | allFieldTypes.email             | harry.potter@elasticpath.com |
      | allFieldTypes.integer           | 12                           |
      | allFieldTypes.multiSelectOption | multiValue1                  |
      | allFieldTypes.shortText         | hello                        |
      | allFieldTypes.singleOption      | singleValue1                 |
    When I update <ITEMCODE> in cart with Quantity: <QTY> and Configurable Fields:
      | allFieldTypes.boolean           | <BOOLEAN>       |
      | allFieldTypes.date              | <DATE>          |
      | allFieldTypes.dateTime          | <DATETIME>      |
      | allFieldTypes.decimal           | <DECIMAL>       |
      | allFieldTypes.email             | <EMAIL>         |
      | allFieldTypes.integer           | <INTEGER>       |
      | allFieldTypes.multiSelectOption | <MULTI_OPTION>  |
      | allFieldTypes.shortText         | <SHORT_TEXT>    |
      | allFieldTypes.singleOption      | <SINGLE_OPTION> |
    Then the HTTP status is bad request
    And Structured error message contains:
      | 'allFieldTypes.date' value is required.              |
      | 'allFieldTypes.email' value is required.             |
      | 'allFieldTypes.multiSelectOption' value is required. |
      | 'allFieldTypes.integer' value is required.           |
      | 'allFieldTypes.shortText' value is required.         |

    Examples:
      | ITEMCODE      | QTY | BOOLEAN | DATE | DATETIME                  | DECIMAL | EMAIL | INTEGER | MULTI_OPTION | SHORT_TEXT | SINGLE_OPTION |
      | sscpwaft      | 1   | false   |      | 2015-06-18T10:15:30Z      | 23.55   |       |         |              |            | singleValue3  |
      | mscpwaftwhite | 1   | true    |      | 2016-08-18T10:15:30+04:00 |         |       |         |              |            | singleValue1  |


  Scenario Outline: Should not be able to update cart with invalid boolean field value
    Given I login as a public shopper
    When I look up an item with code <ITEMCODE>
    Then I go to add to cart form
    And I add the item to the cart with quantity <QTY> and configurable fields:
      | allFieldTypes.boolean           | true                         |
      | allFieldTypes.date              | 2016-08-18                   |
      | allFieldTypes.dateTime          | 2016-08-18T10:15:30+04:00    |
      | allFieldTypes.decimal           | 13.99                        |
      | allFieldTypes.email             | harry.potter@elasticpath.com |
      | allFieldTypes.integer           | 12                           |
      | allFieldTypes.multiSelectOption | multiValue1                  |
      | allFieldTypes.shortText         | hello                        |
      | allFieldTypes.singleOption      | singleValue1                 |
    When I update <ITEMCODE> in cart with Quantity: <QTY> and Configurable Fields:
      | allFieldTypes.boolean           | <BOOLEAN>       |
      | allFieldTypes.date              | <DATE>          |
      | allFieldTypes.dateTime          | <DATETIME>      |
      | allFieldTypes.decimal           | <DECIMAL>       |
      | allFieldTypes.email             | <EMAIL>         |
      | allFieldTypes.integer           | <INTEGER>       |
      | allFieldTypes.multiSelectOption | <MULTI_OPTION>  |
      | allFieldTypes.shortText         | <SHORT_TEXT>    |
      | allFieldTypes.singleOption      | <SINGLE_OPTION> |
    Then the HTTP status is bad request
    And Structured error message contains:
      | 'allFieldTypes.boolean' value '<BOOLEAN>' must be a boolean (true or false). |

    Examples:
      | ITEMCODE      | QTY | BOOLEAN | DATE       | DATETIME                  | DECIMAL | EMAIL                        | INTEGER | MULTI_OPTION | SHORT_TEXT | SINGLE_OPTION |
      | sscpwaft      | 1   | TRUE    | 2016-08-18 | 2016-08-18T10:15:30+04:00 | 13.99   | harry.potter@elasticpath.com | 12      | multiValue1  | hello      | singleValue1  |
      | mscpwaftwhite | 1   | 1       | 2016-07-25 | 2016-01-11T12:34:30+07:00 | 23.34   | harry.potter@elasticpath.com | 23      | multiValue3  | Thanks     | singleValue2  |


  Scenario Outline: Should not be able to update cart with invalid date field value
    Given I login as a public shopper
    When I look up an item with code <ITEMCODE>
    Then I go to add to cart form
    And I add the item to the cart with quantity <QTY> and configurable fields:
      | allFieldTypes.boolean           | true                         |
      | allFieldTypes.date              | 2016-08-18                   |
      | allFieldTypes.dateTime          | 2016-08-18T10:15:30+04:00    |
      | allFieldTypes.decimal           | 13.99                        |
      | allFieldTypes.email             | harry.potter@elasticpath.com |
      | allFieldTypes.integer           | 12                           |
      | allFieldTypes.multiSelectOption | multiValue1                  |
      | allFieldTypes.shortText         | hello                        |
      | allFieldTypes.singleOption      | singleValue1                 |
    When I update <ITEMCODE> in cart with Quantity: <QTY> and Configurable Fields:
      | allFieldTypes.boolean           | <BOOLEAN>       |
      | allFieldTypes.date              | <DATE>          |
      | allFieldTypes.dateTime          | <DATETIME>      |
      | allFieldTypes.decimal           | <DECIMAL>       |
      | allFieldTypes.email             | <EMAIL>         |
      | allFieldTypes.integer           | <INTEGER>       |
      | allFieldTypes.multiSelectOption | <MULTI_OPTION>  |
      | allFieldTypes.shortText         | <SHORT_TEXT>    |
      | allFieldTypes.singleOption      | <SINGLE_OPTION> |
    Then the HTTP status is bad request
    And Structured error message contains:
      | 'allFieldTypes.date' value '<DATE>' must be in ISO8601 date format (YYYY-MM-DD). |

    Examples:
      | ITEMCODE      | QTY | BOOLEAN | DATE       | DATETIME                  | DECIMAL | EMAIL                        | INTEGER | MULTI_OPTION | SHORT_TEXT | SINGLE_OPTION |
      | sscpwaft      | 1   | true    | 2016-12    | 2016-08-18T10:15:30+04:00 | 13.99   | harry.potter@elasticpath.com | 12      | multiValue1  | hello      | singleValue1  |
      | mscpwaftblack | 2   | false   | 2016-07-45 | 2016-08-18T10:15:30Z      | 15      | harry.potter@elasticpath.com | 5       | multiValue2  | bye        | singleValue3  |
      | mscpwaftwhite | 2   | false   | 07-22-2016 | 2016-08-18T10:15:30Z      | 15      | harry.potter@elasticpath.com | 5       | multiValue2  | bye        | singleValue3  |


  Scenario Outline: Should not be able to update cart with invalid datetime field value
    Given I login as a public shopper
    When I look up an item with code <ITEMCODE>
    Then I go to add to cart form
    And I add the item to the cart with quantity <QTY> and configurable fields:
      | allFieldTypes.boolean           | true                         |
      | allFieldTypes.date              | 2016-08-18                   |
      | allFieldTypes.dateTime          | 2016-08-18T10:15:30+04:00    |
      | allFieldTypes.decimal           | 13.99                        |
      | allFieldTypes.email             | harry.potter@elasticpath.com |
      | allFieldTypes.integer           | 12                           |
      | allFieldTypes.multiSelectOption | multiValue1                  |
      | allFieldTypes.shortText         | hello                        |
      | allFieldTypes.singleOption      | singleValue1                 |
    When I update <ITEMCODE> in cart with Quantity: <QTY> and Configurable Fields:
      | allFieldTypes.boolean           | <BOOLEAN>       |
      | allFieldTypes.date              | <DATE>          |
      | allFieldTypes.dateTime          | <DATETIME>      |
      | allFieldTypes.decimal           | <DECIMAL>       |
      | allFieldTypes.email             | <EMAIL>         |
      | allFieldTypes.integer           | <INTEGER>       |
      | allFieldTypes.multiSelectOption | <MULTI_OPTION>  |
      | allFieldTypes.shortText         | <SHORT_TEXT>    |
      | allFieldTypes.singleOption      | <SINGLE_OPTION> |
    Then the HTTP status is bad request
    And Structured error message contains:
      | 'allFieldTypes.dateTime' value '<DATETIME>' must be in ISO8601 date time format (YYYY-MM-DDThh:mm:ssTZD). |

    Examples:
      | ITEMCODE      | QTY | BOOLEAN | DATE       | DATETIME            | DECIMAL | EMAIL                        | INTEGER | MULTI_OPTION | SHORT_TEXT | SINGLE_OPTION |
      | sscpwaft      | 1   | true    | 2016-08-18 | 2016-08-18          | 13.99   | harry.potter@elasticpath.com | 12      | multiValue1  | hello      | singleValue1  |
      | mscpwaftwhite | 2   | false   | 2016-07-25 | 2016-08-18T10:15:30 | 15      | harry.potter@elasticpath.com | 5       | multiValue2  | bye        | singleValue3  |


  Scenario Outline: Should not be able to update cart with invalid decimal field value
    Given I login as a public shopper
    When I look up an item with code <ITEMCODE>
    Then I go to add to cart form
    And I add the item to the cart with quantity <QTY> and configurable fields:
      | allFieldTypes.boolean           | true                         |
      | allFieldTypes.date              | 2016-08-18                   |
      | allFieldTypes.dateTime          | 2016-08-18T10:15:30+04:00    |
      | allFieldTypes.decimal           | 13.99                        |
      | allFieldTypes.email             | harry.potter@elasticpath.com |
      | allFieldTypes.integer           | 12                           |
      | allFieldTypes.multiSelectOption | multiValue1                  |
      | allFieldTypes.shortText         | hello                        |
      | allFieldTypes.singleOption      | singleValue1                 |
    When I update <ITEMCODE> in cart with Quantity: <QTY> and Configurable Fields:
      | allFieldTypes.boolean           | <BOOLEAN>       |
      | allFieldTypes.date              | <DATE>          |
      | allFieldTypes.dateTime          | <DATETIME>      |
      | allFieldTypes.decimal           | <DECIMAL>       |
      | allFieldTypes.email             | <EMAIL>         |
      | allFieldTypes.integer           | <INTEGER>       |
      | allFieldTypes.multiSelectOption | <MULTI_OPTION>  |
      | allFieldTypes.shortText         | <SHORT_TEXT>    |
      | allFieldTypes.singleOption      | <SINGLE_OPTION> |
    Then the HTTP status is bad request
    And Structured error message contains:
      | 'allFieldTypes.decimal' value '<DECIMAL>' must be a decimal. |

    Examples:
      | ITEMCODE      | QTY | BOOLEAN | DATE       | DATETIME                  | DECIMAL | EMAIL                        | INTEGER | MULTI_OPTION | SHORT_TEXT | SINGLE_OPTION |
      | sscpwaft      | 1   | true    | 2016-08-18 | 2016-08-18T10:15:30+04:00 | hello   | harry.potter@elasticpath.com | 12      | multiValue1  | hello      | singleValue1  |
      | mscpwaftblack | 2   | false   | 2016-07-25 | 2016-08-18T10:15:30Z      | 15ABC   | harry.potter@elasticpath.com | 5       | multiValue2  | bye        | singleValue3  |


  Scenario Outline: Should not be able to update cart with invalid email field value
    Given I login as a public shopper
    When I look up an item with code <ITEMCODE>
    Then I go to add to cart form
    And I add the item to the cart with quantity <QTY> and configurable fields:
      | allFieldTypes.boolean           | true                         |
      | allFieldTypes.date              | 2016-08-18                   |
      | allFieldTypes.dateTime          | 2016-08-18T10:15:30+04:00    |
      | allFieldTypes.decimal           | 13.99                        |
      | allFieldTypes.email             | harry.potter@elasticpath.com |
      | allFieldTypes.integer           | 12                           |
      | allFieldTypes.multiSelectOption | multiValue1                  |
      | allFieldTypes.shortText         | hello                        |
      | allFieldTypes.singleOption      | singleValue1                 |
    When I update <ITEMCODE> in cart with Quantity: <QTY> and Configurable Fields:
      | allFieldTypes.boolean           | <BOOLEAN>       |
      | allFieldTypes.date              | <DATE>          |
      | allFieldTypes.dateTime          | <DATETIME>      |
      | allFieldTypes.decimal           | <DECIMAL>       |
      | allFieldTypes.email             | <EMAIL>         |
      | allFieldTypes.integer           | <INTEGER>       |
      | allFieldTypes.multiSelectOption | <MULTI_OPTION>  |
      | allFieldTypes.shortText         | <SHORT_TEXT>    |
      | allFieldTypes.singleOption      | <SINGLE_OPTION> |
    Then the HTTP status is bad request
    And Structured error message contains:
      | 'allFieldTypes.email' value '<EMAIL>' must be a valid email format. |

    Examples:
      | ITEMCODE      | QTY | BOOLEAN | DATE       | DATETIME                  | DECIMAL | EMAIL                        | INTEGER | MULTI_OPTION | SHORT_TEXT | SINGLE_OPTION |
      | sscpwaft      | 1   | true    | 2016-08-18 | 2016-08-18T10:15:30+04:00 | 13.99   | harry.potter                 | 12      | multiValue1  | hello      | singleValue1  |
      | mscpwaftwhite | 2   | false   | 2016-07-25 | 2016-08-18T10:15:30Z      | 15      | harry.potter#elasticpath.com | 5       | multiValue2  | bye        | singleValue3  |


  Scenario Outline: Should not be able to update cart with invalid integer field value
    Given I login as a public shopper
    When I look up an item with code <ITEMCODE>
    Then I go to add to cart form
    And I add the item to the cart with quantity <QTY> and configurable fields:
      | allFieldTypes.boolean           | true                         |
      | allFieldTypes.date              | 2016-08-18                   |
      | allFieldTypes.dateTime          | 2016-08-18T10:15:30+04:00    |
      | allFieldTypes.decimal           | 13.99                        |
      | allFieldTypes.email             | harry.potter@elasticpath.com |
      | allFieldTypes.integer           | 12                           |
      | allFieldTypes.multiSelectOption | multiValue1                  |
      | allFieldTypes.shortText         | hello                        |
      | allFieldTypes.singleOption      | singleValue1                 |
    When I update <ITEMCODE> in cart with Quantity: <QTY> and Configurable Fields:
      | allFieldTypes.boolean           | <BOOLEAN>       |
      | allFieldTypes.date              | <DATE>          |
      | allFieldTypes.dateTime          | <DATETIME>      |
      | allFieldTypes.decimal           | <DECIMAL>       |
      | allFieldTypes.email             | <EMAIL>         |
      | allFieldTypes.integer           | <INTEGER>       |
      | allFieldTypes.multiSelectOption | <MULTI_OPTION>  |
      | allFieldTypes.shortText         | <SHORT_TEXT>    |
      | allFieldTypes.singleOption      | <SINGLE_OPTION> |
    Then the HTTP status is bad request
    And Structured error message contains:
      | 'allFieldTypes.integer' value '<INTEGER>' must be an integer. |

    Examples:
      | ITEMCODE      | QTY | BOOLEAN | DATE       | DATETIME                  | DECIMAL | EMAIL                        | INTEGER | MULTI_OPTION | SHORT_TEXT | SINGLE_OPTION |
      | sscpwaft      | 1   | true    | 2016-08-18 | 2016-08-18T10:15:30+04:00 | 13.99   | harry.potter@elasticpath.com | 12ABC   | multiValue1  | hello      | singleValue1  |
      | mscpwaftblack | 2   | false   | 2016-07-25 | 2016-08-18T10:15:30Z      | 15      | harry.potter@elasticpath.com | 5.95    | multiValue2  | bye        | singleValue3  |


  Scenario Outline: Should not be able to update cart with invalid multi-option field value
    Given I login as a public shopper
    When I look up an item with code <ITEMCODE>
    Then I go to add to cart form
    And I add the item to the cart with quantity <QTY> and configurable fields:
      | allFieldTypes.boolean           | true                         |
      | allFieldTypes.date              | 2016-08-18                   |
      | allFieldTypes.dateTime          | 2016-08-18T10:15:30+04:00    |
      | allFieldTypes.decimal           | 13.99                        |
      | allFieldTypes.email             | harry.potter@elasticpath.com |
      | allFieldTypes.integer           | 12                           |
      | allFieldTypes.multiSelectOption | multiValue1                  |
      | allFieldTypes.shortText         | hello                        |
      | allFieldTypes.singleOption      | singleValue1                 |
    When I update <ITEMCODE> in cart with Quantity: <QTY> and Configurable Fields:
      | allFieldTypes.boolean           | <BOOLEAN>       |
      | allFieldTypes.date              | <DATE>          |
      | allFieldTypes.dateTime          | <DATETIME>      |
      | allFieldTypes.decimal           | <DECIMAL>       |
      | allFieldTypes.email             | <EMAIL>         |
      | allFieldTypes.integer           | <INTEGER>       |
      | allFieldTypes.multiSelectOption | <MULTI_OPTION>  |
      | allFieldTypes.shortText         | <SHORT_TEXT>    |
      | allFieldTypes.singleOption      | <SINGLE_OPTION> |
    Then the HTTP status is bad request
    And Structured error message contains:
      | 'allFieldTypes.multiSelectOption' value '<MULTI_OPTION>' must match a valid option. |

    Examples:
      | ITEMCODE      | QTY | BOOLEAN | DATE       | DATETIME                  | DECIMAL | EMAIL                        | INTEGER | MULTI_OPTION       | SHORT_TEXT | SINGLE_OPTION |
      | sscpwaft      | 1   | true    | 2016-08-18 | 2016-08-18T10:15:30+04:00 | 13.99   | harry.potter@elasticpath.com | 12      | hello              | hello      | singleValue1  |
      | mscpwaftblack | 2   | false   | 2016-07-25 | 2016-08-18T10:15:30Z      | 15      | harry.potter@elasticpath.com | 5       | multiValue1, hello | bye        | singleValue3  |


  Scenario Outline: Should not be able to update cart with field value too large
    Given I login as a public shopper
    When I look up an item with code <ITEMCODE>
    Then I go to add to cart form
    And I add the item to the cart with quantity <QTY> and configurable fields:
      | allFieldTypes.boolean           | true                         |
      | allFieldTypes.date              | 2016-08-18                   |
      | allFieldTypes.dateTime          | 2016-08-18T10:15:30+04:00    |
      | allFieldTypes.decimal           | 13.99                        |
      | allFieldTypes.email             | harry.potter@elasticpath.com |
      | allFieldTypes.integer           | 12                           |
      | allFieldTypes.multiSelectOption | multiValue1                  |
      | allFieldTypes.shortText         | hello                        |
      | allFieldTypes.singleOption      | singleValue1                 |
    When I update <ITEMCODE> in cart with Quantity: <QTY> and Configurable Fields:
      | allFieldTypes.boolean           | <BOOLEAN>       |
      | allFieldTypes.date              | <DATE>          |
      | allFieldTypes.dateTime          | <DATETIME>      |
      | allFieldTypes.decimal           | <DECIMAL>       |
      | allFieldTypes.email             | <EMAIL>         |
      | allFieldTypes.integer           | <INTEGER>       |
      | allFieldTypes.multiSelectOption | <MULTI_OPTION>  |
      | allFieldTypes.shortText         | <SHORT_TEXT>    |
      | allFieldTypes.singleOption      | <SINGLE_OPTION> |
    Then the HTTP status is bad request
    And Structured error message contains:
      | 'allFieldTypes.shortText' value must contain between 0 and 10 characters. |

    Examples:
      | ITEMCODE      | QTY | BOOLEAN | DATE       | DATETIME                  | DECIMAL | EMAIL                        | INTEGER | MULTI_OPTION | SHORT_TEXT       | SINGLE_OPTION |
      | sscpwaft      | 1   | true    | 2016-08-18 | 2016-08-18T10:15:30+04:00 | 13.99   | harry.potter@elasticpath.com | 12      | multiValue1  | hellooo world!   | singleValue1  |
      | mscpwaftblack | 2   | false   | 2016-07-25 | 2016-08-18T10:15:30Z      | 15      | harry.potter@elasticpath.com | 5       | multiValue2  | this is too long | singleValue3  |


  Scenario Outline: Should not be able to update cart with invalid single-option field value
    Given I login as a public shopper
    When I look up an item with code <ITEMCODE>
    Then I go to add to cart form
    And I add the item to the cart with quantity <QTY> and configurable fields:
      | allFieldTypes.boolean           | true                         |
      | allFieldTypes.date              | 2016-08-18                   |
      | allFieldTypes.dateTime          | 2016-08-18T10:15:30+04:00    |
      | allFieldTypes.decimal           | 13.99                        |
      | allFieldTypes.email             | harry.potter@elasticpath.com |
      | allFieldTypes.integer           | 12                           |
      | allFieldTypes.multiSelectOption | multiValue1                  |
      | allFieldTypes.shortText         | hello                        |
      | allFieldTypes.singleOption      | singleValue1                 |
    When I update <ITEMCODE> in cart with Quantity: <QTY> and Configurable Fields:
      | allFieldTypes.boolean           | <BOOLEAN>       |
      | allFieldTypes.date              | <DATE>          |
      | allFieldTypes.dateTime          | <DATETIME>      |
      | allFieldTypes.decimal           | <DECIMAL>       |
      | allFieldTypes.email             | <EMAIL>         |
      | allFieldTypes.integer           | <INTEGER>       |
      | allFieldTypes.multiSelectOption | <MULTI_OPTION>  |
      | allFieldTypes.shortText         | <SHORT_TEXT>    |
      | allFieldTypes.singleOption      | <SINGLE_OPTION> |
    Then the HTTP status is bad request
    And Structured error message contains:
      | 'allFieldTypes.singleOption' value '<SINGLE_OPTION>' must match a valid option. |

    Examples:
      | ITEMCODE      | QTY | BOOLEAN | DATE       | DATETIME                  | DECIMAL | EMAIL                        | INTEGER | MULTI_OPTION | SHORT_TEXT | SINGLE_OPTION |
      | sscpwaft      | 1   | true    | 2016-08-18 | 2016-08-18T10:15:30+04:00 | 13.99   | harry.potter@elasticpath.com | 12      | multiValue1  | hello      | hello         |
      | mscpwaftblack | 2   | false   | 2016-07-25 | 2016-08-18T10:15:30Z      | 15      | harry.potter@elasticpath.com | 5       | multiValue2  | bye        | multiValue2   |


  Scenario Outline: Should not be able to update cart with all fields with invalid values.
    Given I login as a public shopper
    When I look up an item with code <ITEMCODE>
    Then I go to add to cart form
    And I add the item to the cart with quantity <QTY> and configurable fields:
      | allFieldTypes.boolean           | true                         |
      | allFieldTypes.date              | 2016-08-18                   |
      | allFieldTypes.dateTime          | 2016-08-18T10:15:30+04:00    |
      | allFieldTypes.decimal           | 13.99                        |
      | allFieldTypes.email             | harry.potter@elasticpath.com |
      | allFieldTypes.integer           | 12                           |
      | allFieldTypes.multiSelectOption | multiValue1                  |
      | allFieldTypes.shortText         | hello                        |
      | allFieldTypes.singleOption      | singleValue1                 |
    When I update <ITEMCODE> in cart with Quantity: <QTY> and Configurable Fields:
      | allFieldTypes.boolean           | <BOOLEAN>       |
      | allFieldTypes.date              | <DATE>          |
      | allFieldTypes.dateTime          | <DATETIME>      |
      | allFieldTypes.decimal           | <DECIMAL>       |
      | allFieldTypes.email             | <EMAIL>         |
      | allFieldTypes.integer           | <INTEGER>       |
      | allFieldTypes.multiSelectOption | <MULTI_OPTION>  |
      | allFieldTypes.shortText         | <SHORT_TEXT>    |
      | allFieldTypes.singleOption      | <SINGLE_OPTION> |
    Then the HTTP status is bad request
    And Structured error message contains:
      | 'allFieldTypes.boolean' value '<BOOLEAN>' must be a boolean (true or false).                              |
      | 'allFieldTypes.date' value '<DATE>' must be in ISO8601 date format (YYYY-MM-DD).                          |
      | 'allFieldTypes.dateTime' value '<DATETIME>' must be in ISO8601 date time format (YYYY-MM-DDThh:mm:ssTZD). |
      | 'allFieldTypes.decimal' value '<DECIMAL>' must be a decimal.                                              |
      | 'allFieldTypes.email' value '<EMAIL>' must be a valid email format.                                       |
      | 'allFieldTypes.integer' value '<INTEGER>' must be an integer.                                             |
      | 'allFieldTypes.multiSelectOption' value '<MULTI_OPTION>' must match a valid option.                       |
      | 'allFieldTypes.shortText' value must contain between 0 and 10 characters.                                 |
      | 'allFieldTypes.singleOption' value '<SINGLE_OPTION>' must match a valid option.                           |

    Examples:
      | ITEMCODE      | QTY | BOOLEAN | DATE       | DATETIME            | DECIMAL | EMAIL                    | INTEGER | MULTI_OPTION | SHORT_TEXT       | SINGLE_OPTION |
      | sscpwaft      | 1   | TRUE    | 2016-08-45 | 2016-08-18          | 13.TT   | harry.potter@elasticpath | 12.AB   | hello        | hellooo world!   | bye           |
      | mscpwaftblack | 2   | 1       | 2016-07    | 2016-08-18T10:15:30 | ABC     | harry.potter             | 5CC     | singleValue1 | this is too long | multiValue2   |