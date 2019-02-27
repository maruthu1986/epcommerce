# Chapter 6: Price List Manager

[TOC]

## Overview

The Price List Manager allows authorized users to work with:

- Price Lists
- Price List Assignments

## Price Lists

In Elastic Path Commerce, a _Price List_ is a set of pricing details that can be associated with products. Prices are stored in a price list, and a price list is associated to a catalog and a currency. The product code or SKU code links the price in a price list to the product or SKU in a catalog.

When a shopper views an item in a store, the system looks at the applicable price list assignments to determine the  price list to retrieve the product price.

![](images/Ch06-01.png)

### Price List Manager

You can create price lists and add products and SKUs to price lists using the Price List Manager. The Price List Manager contains the **Price List Summary** and **Prices** tabs.

In the **Prices** tab, you can add list and sale prices for products and SKUs. A single price entry for a product forms the default price for a certain quantity of the product. The required minimum quantity is &quot;1&quot;. Subsequent price entries for the same product apply to higher quantities, thereby, creating price tiers.

For example, the price for one t-shirt is entered on a price list as $7. A second price list entry for the t-shirt is listed at $5 with a quantity of &quot;3&quot;. This means that a customer pays $7 per t-shirt if they buy one or two t-shirts. However, if they buy three t-shirts, they pay only $5 per item.

### Price List Manager and the Product Pricing Tab

You can make changes to a product price or SKU price either in a price list or on the product&#39;s **Pricing** tab. If you change the price in one, the other gets updated automatically.

### Change Sets and Price Lists

If the Change Set feature is enabled in your Elastic Path Commerce deployment, you must select a change set before adding or removing prices in a price list.

### Viewing a Price List

1. On the toolbar, click the **Price List Manager** button.

    ![](images/Ch06-02.png)

2. On the **Price Lists** tab in the left pane, click **Search**.

3. In the top right pane, double-click the price list you want to view.

4. In the lower pane, perform any of the following actions:

    - To add a new price to the price list, click **Add Price**.

    - To edit an existing price on a price list, select an item and click **Edit Price**.

    - To delete an existing price on a price list, select an item and click **Delete Price**.

    - To open an item's SKU details in a new tab in the lower pane, select an item and click **Open Item**.  

5. On the toolbar, click **Save** or **Save All** to save your changes.

### Creating a Price List

1. On the toolbar, click the **Price List Manager** button.

2. From the top right pane toolbar, click **Create Price List**. The **New Price List** tab appears in the lower pane.

3. Enter a price list name, description, and currency code for the new price list. Ensure that the code is an ISO 4217 currency code. For example, USD for US dollars or GPB for Great British pounds.

4. On the toolbar, click the **Save** or **Save All** buttons.

5. Click the **Prices** tab.

6. Click **Add Price...**. The _Price Editor_ dialog box appears.

7. Enter the information in the fields as follows:

    |Field|Description|
    | --- | --- |    
    | **Type** | From the list, select if the price is for a SKU or a product. |
    | **Code** | Enter the SKU or product code for the item, if known. You can click the **Search** button to search for the product or SKU you want. |
    | **Quantity** | This value specifies the minimum quantity of the item that must be ordered for the list or sale price to take effect. Typically, every catalog item should have at least one price list entry with a quantity of &quot;1&quot; to provide a default price. **Note**: You can create additional price entries for the same SKU/Product using different quantity and price values. Doing this creates volume-discount (tiered) pricing for an item based on the quantity bought by a customer. |
    | **List Price** | Often this is the MSRP (Manufacturer&#39;s Suggested Retail Price). If this is the only price list entry for the product, this price is the default price. |
    | **Sale Price** | (Optional) In a typical store, this amount would be the &quot;our price&quot; selling price as opposed to the MSRP specified in the **List Price** field. |

8. Click **OK**.

### Adding Products/SKUs to a Price List

> **Note:** If the **Add Price** button is not active, your Elastic Path Commerce system may be using the Change Set feature. In that case you must select a Change Set before adding a product/SKU.

1. On the toolbar, click the **Price List Manager** button.

2. On the **Price Lists** tab in the left pane, click **Search**.

3. In the top right pane, double-click the price list you want to add a price to.

  The price list appears in the lower pane with the **Prices** tab displayed.

4. Click **Add Price...**.

5. Enter the information in the fields as follows:

    |Field|Description|
    | --- | --- |   
    | **Type** | From the list, select if the  price is for a _SKU_ or a _product_. |
    | **Code** | Enter the SKU or product code for the item, if known. You can click the **Search** button to search for the product or SKU you want. |
    | **Quantity** | This value specifies the minimum quantity of the item that must be ordered for the list or sale price to take effect. Typically, every catalog item should have at least one price list entry with a quantity of &quot;1&quot; to provide a default price. You can create additional price entries for the same SKU/Product using different quantity and price values. Doing this creates volume-discount (tiered) pricing for an item based on the quantity bought by a customer. |
    | **List Price** | Often this is the MSRP (Manufacturer&#39;s Suggested Retail Price). If this is the only price list entry for the product, this price is the default price. |
    | **Sale Price** | (Optional) In a typical store, this amount would be the &quot;our price&quot; selling price as opposed to the MSRP specified in the **List Price** field. |

6. Click **OK**.

### Editing Prices in a Price List

1. On the toolbar, click the **Price List Manager** button.

2. On the **Price Lists** tab in the left pane, click **Search**.

3. In the top right pane, double-click the price list you want to edit.

    The price list appears in the lower pane with the **Prices** tab displayed.

4. Locate the price you want to edit.

    - You can use the search boxes to find prices that match a specific tier, price range, or SKU/product code.

    - You can use the filter boxes to further filter the matches.

5. Select the product/SKU whose price you want to edit, and then click **Edit Price...**.

6. In the _Price Editor_ dialog box, make changes as required.

    > **Note**: Some fields are not editable after the price is created. The type and code are not editable. You cannot change the quantity either, because it would effectively change the price tier. (You can delete the price and recreate it with a different quantity.)

7. Click the **OK** button.

### Deleting a Price from a Price List

You can delete a price from a price list using the Price List Manager.

> **Note**: You can also delete a price from the price list from the **Pricing** tab of the product. If you delete a product/SKU from a catalog, all the price list entries for that product/SKU are automatically removed from the corresponding price list.

1. On the toolbar, click the **Price List Manager** button.

2. On the **Price Lists** tab in the left pane, click **Search**.

3. In the upper right pane, locate the price list that contains the price that you want to delete, and double-click it.

4. Select the product/SKU whose price you want to delete and then click **Delete Price...**.

5. Click **OK**.

### Deleting a Price List

1. On the toolbar, click the **Price List Manager** button.

2. On the **Price Lists** tab in the left pane, click **Search**.

3. In the upper right pane, locate the price list that you want to delete and select it.

4. On the top right pane toolbar, click **Delete Price List**.

5. Click **OK** in the confirmation dialog box to delete the price list.

    > **Note:** You cannot delete a price list that is being used by a Price List Assignment. To delete a price list that is associated to a Price List Assignment, you must either delete the Price List Assignment or associate it to a different price list.

### Importing a Price List

Many organizations create and maintain price lists in external applications. Elastic Path Commerce can import price list data from files in CSV (comma separated value) format. CSV files are supported by most popular spreadsheet applications, including Microsoft Excel.

> **Note:** Before you can import price list data, the destination price list must already exist in Elastic Path Commerce. For a new price list, simply create a price list without adding any prices and then perform the import.

#### CSV Import File Format

The CSV import file must contain column headings as well as the price data that you want to import. The structure of the CSV import file must meet the following requirements:

- The first row must contain column headings.
- The headings for columns 7 and 8 must be suffixed with the target price list name and currency in the following format:

```
_<priceListName>_<currencyCode>
```

For example, if the name of the price list in Elastic Path Commerce is &quot;My Price List&quot; and the currency is GBP, then the heading for column 7 would be the following:

```
listPrice_My Price List_GBP
```

- Column 1 is either **product** (if it&#39;s a product) or **SKU** (if it&#39;s a SKU) and is mandatory.
- Column 2 is the **product name** and is optional.
- Column 3 is the **product code** and is mandatory for single-SKU products.
- Column 4 is the **SKU code** and is mandatory for multi-SKU products.
- Column 5 is the **SKU configuration** and is optional.
- Column 6 is the **quantity** (maximum price tier quantity) and is mandatory.
- Column 7 is the **list price** and is mandatory.
- Column 8 is the **sale price** (if any) and is optional.

#### Running a Price List Import Job

> **Note:** You may experience problems importing and exporting price list data if your product codes or SKU codes are numeric values. Before importing or exporting a CSV file with numeric product/SKU codes, open the CSV file in a text editor and ensure that all product and SKU codes are enclosed in the text delimiter character. By default, this is a double quote `"`. For example: `"SKU123"`.

1. On the toolbar, click the **Price List Manager** button.

2. On the toolbar, click the **Run Price List Import Job** button ![](images/Ch06-03.png). The _Run Import_ wizard appears.

3. Complete the fields on this page of the wizard as follows:

    | Field | Description |
    | --- | --- |
    | **CSV Template File** | Click the **Search** icon to locate the CSV file containing the price list data you want to import. <br/><br/>  **Note:** Ensure that the file meets the format requirements described in _CSV Import File Format_ section above. |
    | **Column Delimiter** | Select the column delimiter used in the CSV import file. (Default is a comma, `,`.) |
    | **Text Delimiter** | Select the text delimiter used in the CSV import file. (Default is the double-quote, `"`.) |
    | **Preview data** | Select this option if you want to see a preview of the data before it is imported. This can be useful for detecting subtle errors in the data, such as data appearing in the incorrect column due to misplaced delimiters. |

4. Click **Next**. The price list file data is validated to ensure that there are no errors. If you selected the **Preview Data** option, the price list data is displayed in a spreadsheet format. Otherwise, a message appears indicating the validation results.

5. If validation is not successful, you need to correct the errors in the CSV file. Then, click back and select the file again. If it was successful, click **Finish**.

    The price list import job runs. When it is finished, verify that the new price data is imported successfully. To do this, open the target price list and check that the prices are added or updated.

### Exporting a Price List

From within Elastic Path Commerce, you can export price lists to comma-delimited (CSV) files that can be opened in Microsoft Excel or other spreadsheet applications.

1. On the toolbar, click the **Price List Manager** button.

2. On the **Price Lists** tab in the left pane, click **Search**. The search results appear in the **Price Lists** tab that opens in the top right pane.

3. In the top right pane, locate the price list that you want to export and select it.

4. On the top right pane toolbar, click **Export to CSV**.

The price list data is exported to a CSV file, which is then downloaded to your computer. Typically, this is saved to a location based on your web browser's settings. For example, your Downloads folder. The name of the file is the name of the price list.

Your computer attempts to open the file using the default associated application. For example, if Microsoft Excel is installed and you haven&#39;t changed the CSV file association, the price list opens in Microsoft Excel.

## Price List Assignments

Price List Assignments link price lists to products of a specific catalog. They determine the price list from which the product price is retrieved for the shoppers. However, price list assignments can do much more than simply provide default list and sale prices for products. This feature allows you to provide custom prices to the targeted shoppers, based on shopper characteristics, such as age, gender, geographic location, and searched terms.

For example, a used car reseller can create two different price list assignments. Each assignment would provide different prices to customers based on the customer&#39;s attributes, such as age. For example, customers who match a &quot;college student&quot; profile might receive deeper price discounts on economical vehicles, while customers matching &quot;55 years of age and older&quot; profiles would get preferred pricing on small luxury vehicles.

### Price List Stack

The price list stack is the set of price lists assigned to a shopper. For example, Price List A has prices for all products in a catalog and is assigned to all shoppers. Price List B has different prices for certain products and is only assigned to shoppers who meet specific conditions (as configured in the price list assignment). Price List B is configured with a higher priority than Price List A. When a shopper accesses the frontend, the system builds the shopper&#39;s price list stack by evaluating the price list assignment conditions of all the price lists. Some shoppers have a price list stack containing only Price List A. Other shoppers have both Price List A and B. When the shopper views a product, the system examines the price lists in the stack and retrieves the price from the price list with the highest priority. If the price list does not contain a price for that product, then the system descends the price list stack until it locates a price list that contains a price for the product.

### Considerations

Working with price list assignments involves assigning price lists to shoppers based on a set of conditions. The primary consideration is in deciding the conditions that apply to which people and when.

To determine the conditions under which a particular price list is shown, you need to ask the following questions:

- Who should have access to the price list? (Based on shopper information, such as age, gender, and geo-location.)
- When is the price list available? (The period of time that the price list is active.)
- Where is the price list available? (In which store or stores.)

After you have decided the price list to show a set of shoppers and you have decided on the conditions you want the content to display in, you are ready to set up a price list assignment.

![](images/Ch06-04.png)

### Creating a Price List Assignment

You can create a price list assignment from the **Price List Manager**.

1. On the toolbar, click the **Price List Manager** button.

2. On the toolbar, click the **Create Price List Assignment** button.

    ![](images/Ch06-05.png)

3. In the _Create Price List Assignment_ wizard, enter a **Name** and **Description** for the new price list assignment.

4. Click and drag the **Priority** slider to determine whether this price list assignment takes precedence over other applicable price list assignments. Click **Next**.

5. Select the price list to use to retrieve prices when the conditions are met for this price list assignment. Click **Next**.

6. Select the catalog to use for the selected price list. Click **Next**.

7. Set the conditions that determine whether or not the selected price list is used for a group of shoppers.

    + Select **All Shoppers** if this price list assignment should apply to all shoppers.

    + Select **Only Shoppers who match the following conditions** to set up a specific shopper segment who will see the price list.

8. If you selected **Only Shoppers who..**, click **add statement block** to add a new statement block.

9. In the **Shoppers who** section, click **Add Statement** and select a condition from the options listed.

10. Select an **operator** and a **value** for the condition (for example, &quot;is greater than&quot; and &quot;45&quot;).

11. Repeat steps 9 and 10 to add more condition statements as required. Click **Next** when you are done.

12. Set the time period during which the price list assignment is active.

    - Selecting **All the time** makes the assignment immediately active.

    - Selecting **Only within the following specific date range** to specify a date range for the assignment.

13. Click **Next**.

14. Select the stores where you want to use the price list. To select specific stores, select the **Assign Specific Stores** option.

15. Click the store(s) you want to use from those listed in the **Available Stores** list and use the **Move** button (represented by the symbol &gt;) to add them the **Assigned Store(s)** list.

16. Click **Finish**. The new Price List Assignment appears in the list panel.

### Modifying a Price List Assignment

You can change a price list assignment by opening it from a search results list and then updating the pages in the Price List Assignment wizard.

1. On the toolbar, click the **Price List Manager** button.

2. In the **Price List Search** pane, click the **Price List Assignments** tab, and click **Search** to return a list of all price list assignments in the system.

3. Double-click the price list assignment you want to change.

4. Make your changes. Click **Finish**.

### Deleting a Price List Assignment

You can delete a price list assignment even if it is currently being used.

1. On the toolbar, click the **Price List Manager** button.

2. In the **Price List Search** pane, click the **Price List Assignments** tab and click **Search** to return a list of all price list assignments in the system.

3. Double-click the price list assignment you want to delete.

4. On the top right pane toolbar, click **Delete Price List Assignment**. If a price list is currently in use, a message appears.

5. Click **OK**.

### Searching for a Price List Assignment

1. On the toolbar, click the **Price List Manager** button.

2. In the **Price List Search** pane, click the **Price List Assignments** tab.

3. Enter the **Name** of the price list assignment you want to view and select a **Catalog** from the list (optional).

4.  Click **Search** to return a list of matching price list assignments.

    > **Note**: To return a list of all price list assignments in the system, leave the **Price List Name** field blank and click **Search**.
