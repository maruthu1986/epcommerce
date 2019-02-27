# Appendix D: Query Language

[TOC]

## Overview

The advanced search feature in Elastic Path relies on a special query language to perform sophisticated product searches. This appendix describes the query language, including supported search fields and operators.

## Query Format

A simple query consists of a single expression. An expression has the following form:

```
<field> <operator> <value>
```

Where:

- `<field>` is the field that contains the values you want to compare. A field represents a common characteristic of the object. For example, if you want to look for products of a specific brand, you would include the **BrandCode** field in your query. The supported fields are described in further in this appendix.
- `<operator>` is the operator you are using to perform the comparison.
- `<value>` is the literal value you want to compare to the field values.

For example, the following query matches the product whose code is 10030205:

```
ProductCode = '10030205'
```

In addition to searching for field values, you can also search for attribute values. To search for a value in an attribute, the expression has the following form:

```
AttributeName{<attribute_name>} <operator> <value>
```

where `<attribute_name> `is the name of a product attribute or product SKU attribute.

For example, the following query matches all products that have the Header / Model attribute set to MX:

```
AttributeName{Header / Model} = 'MX'
```

To search for a value in a product SKU attribute, use `SkuAttributeName`. For example, the following query matches all products that have the Header / Model SKU attribute set to MX:

```
SkuAttributeName{Header / Model} = 'MX'
```

## Case Sensitivity

The following are case-sensitive:

- Keywords, such as `AND`, `START`, and `AttributeName`
- Field names
- Attribute names
- Currency codes (must be in upper case letters)

The following are not case-sensitive:

- Literal string values (on the right side of the comparison)
- Language codes (when querying on localized fields)

## Data Types and Supported Operators

Every field and attribute has a data type. The data type determines the kind of data the field or attribute can contain. The following table shows the data types and the operators supported for each one.

| Data type | Supported operators | Notes | Examples |
| --- | --- | --- | --- |
| `DateTime` | `=`, `!=`, `<`, `=<`, `>`, `>=` | The `DateTime` format is `YYYY-MM-DDThh:mm:ss`. | `'2008-08-30T17:19:00&#39;&#39;2009-12-21T07:06:00'` |
| `Double` | `=`, `!=`, `<`, `=<`, `>`, `>=`  | The decimal point is optional. | `100.0020199` |
| `Integer` | `=`, `!=`, `<`, `=<`, `>`, `>=`  |   | `1` |
| `String` | `=`, `!=` | Strings must be surrounded by single quotes (`'`). If a string contains a single quote, it must be preceded by a backslash. | `'StringValue''Canon - Kit d\'accessoires pour appareil photo'` |

## Supported Fields

The following table shows the fields that can be used in product queries.

| Field name | Description | Data type | Localized |
| --- | --- | --- | --- |
| `BrandCode` | Brand code | `String` | No |
| `BrandName` | Brand name | `String` | Yes |
| `CatalogCode` | Catalog code | `String` | No |
| `CategoryCode` | Category code | `String` | No |
| `CategoryName` | Category name | `String` | Yes |
| `ProductStartDate` | Product availability start date | `Datetime` | No |
| `LastModifiedDate` | Product last modified date | `Datetime` | No |
| `ProductActive` | Product active | `String` | No |
| `ProductCode` | Product code | `String` | No |
| `ProductName` | Product name | `String` | Yes |
| `SkuCode` | Product SKU code | `String` | No |
| `StoreCode` | Store code | `String` | No |

## Localized Fields and Attributes

Some fields contain localized values. To include a localized field in your query, you must specify the language that you want to search. For example, the following query matches the product whose French name is Canon - Kit d&#39;accessoires pour appareil photo.

```
ProductName[fr] = 'Canon - Kit d\'accessoires pour appareil photo'
```

The value between the square brackets indicates the language. You can use either the two-letter language code or the full language name.

> **Note**: The apostrophe in d&#39;accessoires must be preceded by a backslash. Whenever a string contains this character, it must be escaped with a backslash, otherwise it is interpreted as the end of the string and causes a parsing error when the query is validated.

Attributes may also contain localized values. For example, the following query matches all products that have the English value of the Lens System / Type attribute set to Zoom lens.

```
AttributeName{Lens System / Type}[en] = 'Zoom lens'
```

## Combining Expressions

You can use `AND` or `OR` to combine multiple expressions.

For example, the following query uses `AND` to match all Kodak zoom lens items (based on the value of the Lens System / Type attribute).

```
AttributeName{Lens System / Type}[en] = 'Zoom lens' AND BrandName[en] = 'Kodak'
```

The following query uses `OR` to match all Pentax and Kodak products.

```
BrandName[en] = 'Pentax' OR BrandName[en] = 'Kodak'
```

You can use parentheses (the `(` and `)` characters) to set the order in which expressions are evaluated. Expressions in parentheses are evaluated first. You can nest expression groups.

## Limiting the Result Set Size

You can limit the number of results returned by adding `LIMIT <number>`, where `<number>` specifies the maximum number of items to include in the results. For example, to return the first ten Pentax products, execute the following:

```
BrandName[en] = 'Pentax' LIMIT 10
```

## Specifying the First Match

You can specify the position of the first match to return within the results by adding `START <number>`, where `<number>` is the position of the first match you want to return. For example, the following query returns the first ten matches starting at the twentieth match.

```
BrandName[en] = 'Pentax' LIMIT 10 START 20
```

Currently, it is not possible to sort results. This is primarily used in search queries executed by the Import-Export tool to split result sets into more manageable &quot;chunks&quot;. For example, the following query returns 274 matches for the Snap It Up master catalog:

```
BrandName[en] = 'Pentax'
```

You can split those matches into three separate result sets by executing the following three queries:

```
BrandName[en] = 'Pentax' LIMIT 100
```

```
BrandName[en] = 'Pentax' START 101 LIMIT 100
```

```
BrandName[en] = 'Pentax' START 201
```

The first query returns matches 1 to 100. The second returns 101 to 200. The third returns from 201 to the last match (274).
