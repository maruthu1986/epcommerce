# Chapter 11: Reporting

You can generate various reports using Elastic Path Commerce. Reports can help to improve the decisions taken by management and front-line personnel in an organization. You can access reports from the main toolbar.

![](images/Ch11-01.png)

Elastic Path Commerce uses BIRT (Business Intelligence and Reporting Tools) technology to produce reports in a variety of formats, including HTML, CSV, and PDF. You can create and integrate custom reports to fit your specific needs. For more information, see the _Developer Guide_ at http://developers.elasticpath.com.

<!--## Customer Registration Report

The Customer Registration report is a summary of a store&#39;s customer registrations during a particular time frame. It is useful for gauging the effectiveness of promotions targeting new customers.

**Note:**         Anonymous registrations may also be included in the summary.

### Generating a Customer Registration Report

1. From the toolbar, click the **Reporting** button. The Reporting pane appears.

2. From the _Report Type_ list, select **Customer Registration**.

3. From the _Store_ list, select the store for which you want to generate the report.

4. Specify the date range for the report using the calendar in the From and To Date/Time fields. The _From Date/Time_ field is optional, but the _To Date/Time_ field is required.

5. From the _Report Format_ list, select the format to export the report to. For example, as a CSV, Microsoft Excel, or PDF file.

5. Click **Run Report**. A new pane containing the report appears on the right.

<!-- ## Gift Certificate Details Report

This report shows details about the gift certificates sold by a store in a given currency during a particular time frame.

### Generating a Gift Certificate Details Report

1. From the toolbar, click the **Reporting** button. The Reporting pane appears.

2. From the _Report Type_ list, select _Gift Certificate Details_.

3. From the _Store_ list, select the store for which you want to generate the report.

4. From the _Currency_ list, select the currency for which you want to generate the report.

5. Specify the purchase date range for the report using the calendar in the Purchased From and To Date/Time fields.  The _Purchased From Date/Time_ field is optional, but the _To Date/Time_ is required.

6. From the _Report Format_ list, select the format to export the report to. For example, as a CSV, Microsoft Excel, or PDF file.

7. Click **Run Report**. A new pane containing the report appears on the right.

## Gift Certificate Summary Report

The Gift Certificate Summary report is a summary of the gift certificates in a given currency that were sold by a store (or all stores) during a specific time frame. You can use it for calculating outstanding liabilities.

### Generating a Gift Certificate Summary Report

1. From the toolbar, click the **Reporting** button. The Reporting pane appears.

2. From the _Report Type_ list, select _Gift Certificate Summary_.

3. From the _Store_ list, select the store for which you want to generate the report, or select _All_ to generate a report for all stores.

4. From the _Currency_ list, select the currency for which you want to generate the report, or select _All_ to generate a report for all currencies.

5. Specify the purchase date range for the report using the calendar in the Purchased From and To Date/Time fields.  The _Purchased From Date/Time_ field is optional, but the _To Date/Time_ is required.

6. From the _Report Format_ list, select the format to export the report to. For example, as a CSV, Microsoft Excel, or PDF file.

7. Click **Run Report**. A new pane containing the report appears on the right.

<!-- -->
<!--## Low Stock Report

This report helps determine the products to restock. If a certain product&#39;s Quantity on Hand is less than its Re-Order Minimum amount, it is included in this report.

You need to specify the warehouse the report is for. You can also specify a brand for further filtering. This report is useful when a warehouse has to submit an order proposal to a supplier.

If you want to check the low stock levels for a specific product, you can enter the product's SKU number at the input screen.

### Generating a Low Stock Report

1. From the toolbar, click the **Reporting** button. The Reporting pane appears.

2. From the _Report Type_ list, select _Low Stock_.

3. From the _Store_ list, select the warehouse for which you want to generate the report.

4. If required, you can select a SKU Code and/or a brand to filter the results. Click the button next to the _SKU Code_ box to display the SKU code selector dialog box.

    ![](images/Ch12-05.png)

5. From the _Report Format_ list, select the format to export the report to. For example, as a CSV, Microsoft Excel, or PDF file.

7. Click **Run Report**. A new pane containing the report appears on the right.

## Order Details Report

This report includes the same set of report parameters as the Order Summary Report, but it also includes other details on the individual orders.

You need to specify the store and subset of orders the report is generated for.

### Generating an Order Details Report

1. From the toolbar, click the **Reporting** button. The Reporting pane appears.

2. From the _Report Type_ list, select _Order Details_.

3. Enter values in the fields to specify what data the report should be generated on. Use the calendar icon to select the report&#39;s date range.

4. From the _Report Format_ list, select the format to export the report to. For example, as a CSV, Microsoft Excel, or PDF file.

7. Click **Run Report**. A new pane containing the report appears on the right.
<!-- -->

## Order Summary Report

This report provides an overview of a store&#39;s performance (in sales) over a particular time frame.
You can specify a date range for the report to summarize the sales data over the specified time frame.
By default, the report encapsulates the sales data for just the current day.

You can also specify the currency for the report be generated in, the source of the orders, and the subset of orders (by their status) to include in the report.

### Generating an Order Summary Report

1. From the toolbar, click the **Reporting** button. The Reporting pane appears.

2. From the _Report Type_ list, select **Order Summary**.

3. From the _Store_ list, select the store whose sales data you want to generate.

4. Specify the date range for the report using the calendar in the From and To Date/Time fields.

5. From the _Currency_ list, select the currency for the report data to be generated in.

6. From the _Order Source_ list, specify whether to view orders generated from a particular store, or those from third-party web services.

7. (Optional) To exclude non-exchange orders, select the **Show Exchange Orders Only** option.

8. To include a subset of orders based on their status, select the appropriate options.

9. From the _Report Format_ list, select the format to export the report to. For example, as a CSV, Microsoft Excel, or PDF file.

10. Click **Run Report**. A new pane containing the report appears on the right.

## Shopping Cart Promotion Usage Report

The Shopping Cart Promotion Usage Report contains information about shopping cart promotions, including the number of orders that benefited from promotions, the total generated revenue, and the percentage of total orders. This can be useful for determining the overall effectiveness of promotions.

### Generating a Shopping Cart Promotion Usage Report

1. From the toolbar, click the **Reporting** button. The Reporting pane appears.

2. From the _Report Type_ list, select **Shopping Cart Promotion Usage**.

3. From the _Store_ list, select the store whose promotion data you want to generate, or select **All** to generate a report for all stores.

4. From the _Currency_ list, select the currency for the report data to be generated in, or select **All** to generate a report for all currencies.

5. Specify the purchase date range using the calendar in the Purchased From and To Date/Time fields. The  **Purchased From Date/Time** field is optional, but **To Date/Time** field is required.

6. To exclude non-coupon promotions, select the _Include only Promotions with Coupon_ option.

7. From the _Report Format_ list, select the format to export the report to. For example, as a CSV, Microsoft Excel, or PDF file.

8. Click **Run Report**. A new pane containing the report appears on the right.

<!--## Orders Awaiting Stock Allocation Report

This report provides a list of all products that are pre-ordered or back-ordered. You can use it for a variety of purposes, such as identifying the products to order.

You need to specify the store the report is generated for.
You can also filter the results by product status (preorder, backorder, or both).

If you want to check a specific product for its pre-order or back-order status, you can enter the corresponding SKU number at the input screen.

### Generating an Orders Awaiting Stock Allocation Report

1. From the toolbar, click the **Reporting** button. The Reporting pane appears.

2. From the _Report Type_ list, select _Orders Awaiting Stock Allocation._

3. From the _Store_ list, select the store for which you want to generate the report.

4. If required, you can also specify a SKU code to further filter the results in the report.

5. Select the Product Availability Rules you want the report generated for from the drop-down list.

6. From the _Report Format_ list, select the format to export the report to. For example, as a CSV, Microsoft Excel, or PDF file.

7. Click **Run Report**. A new pane containing the report appears on the right

<!-- -->

## Order Status Report

This report provides


### Generating an Order Status Report

1. From the toolbar, click the **Reporting** button. The Reporting pane appears.

2. From the _Report Type_ list, select **Order Status**.

3. Enter values in the fields to specify what data the report should be generated on.

4. From the _Report Format_ list, select the format to export the report to. For example, as a CSV, Microsoft Excel, or PDF file.

5. Click **Run Report**. A new pane containing the report appears on the right.

## Returns and Exchanges Report

This report provides a summary of all returns and exchanges during a particular time frame. You can use this report to monitor the volume of exchanges/refunds and the reasons for them.

You need to specify the warehouse the report is generated for. You can also filter the results by time frame, the RMA type (return, exchange, or both), or by the status of the refund/exchange. For example, completed, awaiting stock return, and so on.

### Generating a Returns and Exchanges Report

1. From the toolbar, click the **Reporting** button. The Reporting pane appears.

2. From the _Report Type_ list, select **Returns and Exchanges**.

3. Enter values in the fields to specify what data the report should be generated on. Again, you may click the calendar icon next to the Date fields to bring up a utility to help in selecting the range of dates for the report.

4. From the _Report Format_ list, select the format to export the report to. For example, as a CSV, Microsoft Excel, or PDF file.

5. Click **Run Report**. A new pane containing the report appears on the right.


<!--## Shopping Cart Promotion Details Report

The Shopping Cart Promotion Details Report contains information about orders where a specific shopping cart promotion was applied, including the order number, coupon code, order total, and customer email. This can be useful for determining the effectiveness of specific promotions.

### Generating a Shopping Cart Promotion Details Report

1. From the toolbar, click the **Reporting** button. The Reporting pane appears.

2. From the _Report Type_ list, select _Shopping Cart Promotion Details_.

3. From the _Store_ list, select the store for which you want to generate the report.

4. From the _Currency_ list, select the currency for which you want to generate the report, or select _All_ to generate a report for all currencies.

5.Specify the date range that contains orders you want to include in the report using the calendar in the Purchased From and To Date/Time fields. The _Purchased From Date/Time_ field is optional, but the _To Date/Time_ field is required.

6. From the _Promotion_ list, select the promotion for which you want to generate the report.

7. If you want to filter the results to include a specific coupon code, enter the code in the _Coupon Code_ box.

8. From the _Report Format_ list, select the format to export the report to. For example, as a CSV, Microsoft Excel, or PDF file.

9. Click **Run Report**. A new pane containing the report appears on the right.
<!-- -->
