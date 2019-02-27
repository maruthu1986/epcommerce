# Chapter 8: Promotions/Shipping

[TOC]

## Overview

Promotions/Shipping enable e-commerce store managers to configure store-related aspects of Elastic Path Commerce, including promotions and shipping service levels. You can accessed it from the toolbar.

![](images/Ch08-01.png)

## Promotions

A promotion is a marketing tool used to increase sales. Promotions are store-specific. To create a promotion for a store, you must have the _Manage Promotions_ permission and must be assigned to that store. You cannot share promotions across multiple stores.

Elastic Path Commerce supports two types of promotions:

- Catalog promotions
- Shopping cart promotions

### Catalog Promotions

Catalog promotions are used to make specific products and categories more attractive to shoppers through incentives, such as lowered pricing on a particular brand. A catalog promotion grants all shoppers a discount on a specific product or a set of products. Catalog promotions are always visible to shoppers, so the price they see while browsing the site is the price they pay at checkout time (minus taxes and any shopping cart promotions that may apply).

### Shopping Cart Promotions

Shopping cart promotions are used to encourage shoppers to increase their order size by providing incentives, such as free shipping on orders over a certain dollar value. A shopping cart promotion grants a discount on a specific product or a set of products to shoppers who meet certain conditions. Shopping cart promotions are only applied after all conditions are met. So, a shopper only sees the price they pay for a promoted item when they view their shopping cart or when they check out (assuming all the promotion&#39;s conditions are met).

For example, a product has a list price of $200.00. There is a shopping cart promotion that grants a 10% discount on all orders over $50.00. When browsing the catalog, all shoppers see the product&#39;s $200.00 list price. If a shopper adds the product to their shopping cart and then views the cart&#39;s contents, the price in the cart is $180.00 (due to the 10% discount). If the same shopper goes back to browsing the frontend, the catalog still shows the list price of $200.

### Promotion Rules

Promotion rules define the behavior and effects of promotions. There are two types of promotion rules:

- Conditions
- Actions

#### Conditions

Conditions are the set of criteria that determine whether a shopper is eligible for a promotion, based on the current state of the shopping cart. Examples of conditions are:

- A cart containing a particular number of a certain product or SKU
- A cart subtotal greater than a specific amount

#### Actions

Actions specify the benefits that are granted by a promotion. They are the incentives used to encourage shoppers to purchase more, or to purchase specific items. Examples of actions are:

- Free shipping
- Discounts on specific products
- Free items
- Coupons for discounts on future purchases

The types of conditions and actions available depend on the promotion type.

There are many options available when configuring promotion rules. For detailed descriptions of the available options, see the _Promotion Shopper Segment Conditions and Rules_ section in the appendices.

### Shopper Segment

> **Note**: The shopper segment corresponds to the eligibility promotion rule that existed in Elastic Path Commerce versions prior to 6.2.1. It expands on the capabilities of eligibilities by providing many more options, including geo-location and browsing behavior. The shopper segment is a component of Elastic Path&#39;s targeted selling framework, which is also used to deliver price lists.

The shopper segment is similar to promotion rules. It specifies criteria that determine, based on information that is collected about the shopper, whether that shopper is eligible to receive a promotion. There are many options available when configuring the shopper segment. For detailed descriptions of the available options, see the _Promotion Shopper Segment Conditions and Rules_ section in the appendices.

### Activation Rules

Activation rules determine how eligible shoppers can apply promotions to their purchases. In the simplest case, a promotion is applied automatically if the customer meets all the conditions.

Some promotions need to be &quot;activated&quot; by a coupon code (also referred to as a promotion code). For example, to receive a discount on an item in the shopping cart, the shopper must enter a coupon code at checkout time. If your organization uses coupon codes, you need to configure certain rules to control how they can be used with each promotion.

When you create a promotion, you need to specify whether it gets activated automatically or by a coupon. There are two types of coupons:

- Public coupons
- Private coupons

#### Public Coupons

Public coupons can be used by any shopper who qualifies for a promotion that has an unredeemed coupon code associated to that promotion. Shoppers may or may not be able to re-use a previously redeemed coupon code, depending on how its usage options are configured.

There are three usage options for public coupon codes:

- **Unlimited**: Each coupon code can be used an unlimited number of times by all qualifying shoppers.

- **Limit per coupon code**: Each coupon code can be used a limited number of times. This limit applies to the total number of uses, but within that limit, any individual shopper could use the coupon any number of times. For example, if `PROMO1CODE1` has a limit of 10, then Shopper A can use it 2 times, Shopper B can use it 6 times, and Shoppers C and D can each use it once.

- **Limit per shopper**: Each coupon code can be used by an unlimited number of shoppers, but each shopper can only use it a limited number of times. For example, `PROMO2CODE1` has a limit of 5 uses per shopper, which means it can be used five times by Shopper A, five times by Shopper B, and so on.

  - **Note**: The usage count is tracked by the shopper&#39;s email address. A shopper can apply the same coupon code many times beyond the limit by using different email addresses. If limiting overall exposure is a concern, select the **Limited Usage Promotion** option in the first step of the wizard when creating the promotion and specify the maximum number of times the coupon code can be used.

#### Private Coupons

Private coupons can only be used by registered customers. Customers who placed orders but did not register (they created an order with only their email address) are not registered customers and, therefore, cannot use private coupons.

Private coupons can be configured for single, multiple, or unlimited use. They can also be configured to expire after a specified number of days from the date the customer qualified for the coupon.

> **Note:** The end date of a promotion always takes precedence over the expiry date of a coupon code. For example, a customer obtains a coupon on the final date in the promotion&#39;s date range. The coupon code is set to expire in 60 days. If the customer attempts to use the coupon on the following day, it is not accepted because the promotion has ended and is no longer active.

#### Coupon Codes

A promotion can have multiple coupon codes associated to it. You can manually add individual coupon codes when you create a promotion. You can also import coupon codes in bulk from CSV files after a promotion is created.

Unless you have special tracking requirements, it is not necessary to create a unique coupon code for each shopper who is eligible for a promotion. The frontend automatically determines if a coupon is already used by a particular shopper, and, based on the usage options, it determines whether the coupon code can be used again.

### Promotions Best Practices and Tips

- The number of active promotions can negatively influence the store performance. Try to minimize the number of active promotions and disable all expired promotions.

- Do not use the promotions to create permanent discounts. Instead, create sale prices in the appropriate price lists.

- Promotion rules are cached to improve performance in the frontend. As a result, there is a delay between when a promotion is created and when it applies in the store. The length of the delay depends on the store&#39;s configuration. By default, it may take up to an hour for the change to apply.

- Unless you have special tracking and reporting requirements (particularly for public coupons), it is not necessary to create a unique coupon code for each shopper you want to target with a promotion. The system automatically determines if a coupon is already used by a particular shopper, and, based on the coupon usage options, whether it can be used again.

- If you limit the public coupon usage to a specific number of times per shopper, be aware that the usage count is tracked by the shopper&#39;s email address. As such, a shopper can apply the same coupon code many times beyond the limit by using different email addresses. If limiting overall exposure is a concern, select the **Limited Usage Promotion** option in the first step of the wizard when creating the promotion, and specify a maximum number of times the coupon code can be used.

### Searching for a Promotion

1. On the toolbar, click the **Promotions/Shipping** button.

2. In the **Promotions** tab, select the filters for the  **Promotion State**, **Promotion Type**, **Catalog**, and **Store** as needed.

3. Enter a **Promotion Name**.

4. Set the sorting options to sort the search results by column name, and in ascending or descending order.

    > **Note**: You can sort the search results after the search is run by clicking the appropriate column header in the results list.

5. Click **Search**.

#### Sorting the Promotion Search Results

You can set the sorting criteria when you run a search, so that the search results are displayed by the selected column in ascending or descending order.

In addition, you can sort your search results by simply clicking on a column header to sort by that header.

In the example below, the search results are sorted by the **Type** column, as indicated by the grey, upward facing chevron to the right of the column label. Clicking a column header that is already selected for the sort order, toggles the sort order from ascending to descending order.

#### Changing the Search Results Pagination

By default, the search results display a list of 10 items per page. You can adjust this to show more than 10 items by changing the pagination setting:

1. Click the **admin** list in the top pane to display the **User Menu**.

2. Select **Change Pagination Settings**. The _Change Pagination Settings_ dialog box appears.

3. In the **Results Per Page** list, select the number of results you want to display per page.

4. Click **Save**.

### Creating a Shopping Cart Promotion

1. On the toolbar, click the **Promotions/Shipping** button.

2. On the top right pane toolbar, click the **Create Shopping Cart Promotion** ![](images/Ch08-02.png) button. The _Create Shopping Cart Promotion_ wizard appears.

    > **Note:** If the **Create Shopping Cart Promotion** button is not active, it may be because the Change Set feature is enabled. In this case, you must select a Change Set before adding the promotion. For more information, see the _Change Sets_ chapter.

3. Configure the promotion details using the following fields, and then click **Next**.

    | Field | Description |
    | --- | --- |
    | **Store** | The store to which the new promotion applies. |
    | **Promotion Name** | The name of the promotion. This is used within Elastic Path Commerce. It is not visible to customers. |
    | **Promotion Display Name** | The localized, public name of the promotion. This is the name that is displayed in the front-end. Select each supported language and enter the locale-specific promotion display name. |
    | **Description** | A description of the promotion. This is for your organization&#39;s internal use only and is not displayed in the frontend. |
    | **Enable in Store** | Whether the promotion will be enabled immediately in the store (subject to the expiration date/time). |
    | **Limited Usage Promotion** | Limits the number of times the promotion can be used if enabled. This is useful if you want to create a &quot;door crasher&quot;-style special. For example, only the first 100 customers who buy the product will receive the promotion at checkout. |
    | **Allowed Limit** | This field is only displayed when _Limited Usage Promotion_ is enabled. Sets the maximum number of times a promotion can be used. |

4. Configure the target shopper segment for the new promotion. Then, click **Next**.

    > **Note:** For detailed descriptions of the available shopper segment options, see _Promotion Shopper Segment Conditions and Rules_ in the appendices.

5. Specify the duration the promotion is in effect. Then, click **Next**.

    - If **All the time (effective immediately)** is selected and the **Enable in Store** option is selected in step 3, the promotion is effective immediately and remains effective until disabled.
    - If **Only within the following specific date range** is selected, you must set a start date/time for the promotion. If an end date/time is not set, the promotion remains effective until disabled.

6. Set the promotion rules (conditions and actions) for the promotion. Then, click **Next**.

    + To add a condition, click the add ![](images/Ch08-03.png) button.
    + To delete a condition, click the delete ![](images/Ch08-04.png) button.

    For detailed descriptions of the different promotion rules, see _Promotion Shopper Segment Conditions and Rules_ in the appendices.

7. Configure the activation rules for the promotion.

    + If you want the promotion to be applied automatically (as soon as the shopper qualifies for it), select **This promotion is not activated by coupons**.
    + If you want the promotion to be applied when the shopper enters a public coupon code, select **This promotion is activated by public coupons**. Then, configure the usage options and the coupon codes.
    + If you want the promotion to be applied when the shopper enters a private coupon code, select **This promotion is activated by private coupons**. Then, configure the usage options and the coupon codes.

    > **Note** :If you set an expiry date for the coupon codes and you have set a date range for the promotion, the date range always takes precedence in determining whether the promotion still applies. For example, a customer obtains a coupon on the final date in the promotion&#39;s date range. The coupon code is set to expire in 60 days. If the customer attempts to use the coupon on the following day, it is not accepted because the promotion is no longer active.

8. Click **Finish** to complete the creation of your new promotion.

### Creating a Catalog Promotion

1. On the toolbar, click the **Promotions/Shipping** button.

2. On the top right pane toolbar, click the **Create Catalog Promotion** ![](images/Ch08-05.png) button. The _Create Catalog Promotion_ wizard appears.

    > **Note**: If the Create Catalog Promotion button is not active, your Elastic Path Commerce system may be using the Change Set feature. In that case you must select a Change Set before adding the promotion. For more information, see the _Change Sets_ chapter.

3. Configure the promotion details using the following fields, and then click **Next**.

    | Field | Description |
    | --- | --- |
    | **Catalog** | The catalog in which the new promotion works in. This is a required field. <br/> <br/> <b>Note</b>: Only catalogs that have price lists associated to them are displayed in this list. |
    | **Promotion Name** | The name of the promotion. This is used within Elastic Path Commerce. It is not visible to customers. This is a required field. |
    | **Promotion Display Name** | The localized, public name of the promotion. This is the name that is displayed in the frontend. Select each supported language and enter the locale-specific promotion display name. |
    | **Description** | A description of what the promotion is used for, what it does, and other details as the creator deems fit. This is for internal use only; customers are not able to view this in the frontend. This is a required field. |
    | **Enable Date/Time** | When the promotion should become active. This is a required field. |
    | **Expiration Date/Time** | When the promotion should be deactivated. This field may be left blank if the promotion should be ongoing indefinitely. |

4. Set the promotional rules (conditions and actions), and click **Next**.

    + To add a condition, click the add ![](images/Ch08-03.png) button.
    + To delete a condition, click the delete ![](images/Ch08-04.png) button.

    For detailed descriptions of the different promotion rules, see _Promotion Shopper Segment Conditions and Rules_ in the appendices.

5. Click **Finish**.

### Editing a Promotion

1. On the toolbar, click the **Promotions/Shipping** button. The promotions are listed in the **Promotion Search Results** tab on the top right pane.

2. Search for the promotion that you want to edit, and double-click it. The **Summary** tab appears in the bottom pane.

3. Modify the information in the required tab(s).

    > **Note**: You cannot change the coupon types after the promotion is created.

4. On the toolbar, click **Save**.

### Importing Coupon Codes

> **Note**: To import coupon codes, you must have the _Manage Coupons_ permission.

> **Note**: Completed orders are not affected by changes in the coupon code status. If a customer redeems a coupon code and that code is disabled before checkout is complete, the promotion still applies.

By default, the status of imported coupons is set to _In Use_.

1. Create a CSV file containing the coupon codes you want to import.

    + For a public promotion, the file must consist of one column containing the coupon codes. The first row must contain only the following text: `couponCode`

    + For a private promotion, the file must consist of two columns - the first containing the coupon codes and the second containing the email addresses of the customers mapped to those codes. Multiple customers can be mapped to the same code. The email addresses must match the email addresses specified in the corresponding customer accounts. The first row must only contain the following text: `couponCode`,`emailAddress`

2. On the toolbar, click the **Promotions/Shipping** button.

3. Search for the promotion that you want to edit, and double-click it in the **Promotion Search Results** tab on the top right pane.

4. In the **Coupon Codes** tab, click **Import codes from a CSV file...**.

5. Click the **Browse** button and locate your CSV file. Then, click **Next**.

6. Click **Finish**.

### Enabling and Disabling Coupon Codes

> **Note**: To enable or disable coupon codes, you must have the _Manage Coupons_ permission.

1. On the toolbar, click the **Promotions/Shipping** button.

2. Search for the promotion that you want to edit, and double-click it in the **Promotion Search Results** tab on the top right pane.

3. In the **Coupon Codes** tab, select the coupon(s) that you want to modify. (Use the Shift and Ctrl keys to select multiple contiguous or non-contiguous coupon codes.)

4. Click **Edit**.

5. Change the status:

    + To enable the selected coupon code(s), set the status to **In Use**.
    + To disable the selected coupon code(s), set the status to **Suspended**.

6. Click **OK**.

7. On the toolbar, click **Save**.

## Tags

_Tags_ allow you to determine particular shoppers based on the information relating to their profile, browsing behavior, shopping cart, and Geo-Location. Elastic Path Commerce incorporates a tagging framework that is used to set and evaluate tags that you can use to segment _Shoppers_. The tagging data about a shopper is captured in a variety of ways, including:

- Information provided in a registered user&#39;s customer account
- Information entered by a CSR about a customer during a customer service call
- Target and Referring URLs and search terms captured when the user clicks a link to bring them to a store.

Information captured by the tags is stored in the user&#39;s session when they shop. Tag information is captured only when the shopper first visits a store during a browser session. For example, if a shopper visits your store from a Google link, and then goes to a different site that refers them back to your store, the Referring URL tag would only contain the original referring URL for Google.

Elastic Path Commerce is shipped with the most commonly used tags. More tags will be available in the future, and organizations can develop their own custom tags as required. The following tags are included with Elastic Path Commerce:

_Shopper_ tags are presented in Elastic Path Commerce in sub-groups for easy selection. The sub-groups include:

- Browse Behavior
- Customer Profile
- Geo Location (GEO IP)
- Shopping Cart

### Browse Behavior Tags

| **Tag name** | **Description** |
| --- | --- |
| have **searched in the store for phrase** (string) | This tag looks for search terms that the shopper has entered in the storefront **Search** field. For the search terms to be recorded, the shopper must actually press the **Search** button. <br/><br/> **Note**: The saved tag value is overwritten each time the shopper performs a new search. This tag has a limit of 50 characters. |
| have viewed **category** (string) | This tag evaluates the selected category value against those that the shopper has visited during their current session. This tag cannot be used to evaluate a shopper&#39;s navigation when the shopper has directly navigated to a product without passing through a category or a sub-category. |
| **landed on a store page with URL** (string) | The Target URL tag captures the URL that brought the shopper to a particular page in your storefront.  For example, if the complete URL is: <br/> -  [http://snapitup.elasticpath.com:8080/storefront/browse.ep?cID=100009&amp;filters=c90000003&amp;sorter=price-desc](http://snapitup.elasticpath.com:8080/storefront/browse.ep?cID=100009&amp;filters=c90000003&amp;sorter=price-desc) <br/> The value stored in the `TARGET_URL` tag is: <br/> - [http://snapitup.elasticpath.com:8080/storefront/browse.ep?cID=100009&amp;filters=c90000003&amp;sorter=price-desc](http://snapitup.elasticpath.com:8080/storefront/browse.ep?cID=100009&amp;filters=c90000003&amp;sorter=price-desc) <br/><br/> This tag has a limit of 50 characters. |
| **searched on the internet for phrase** (string) | The Search Terms tag is used to look for specific search terms a shopper entered into a search engine that resulted in them arriving at your store. Currently, this tag is designed to work with the Google, Yahoo, and MSN Live search engines.  Each search engine uses a different syntax for their searches. When it encounters this tag, Elastic Path Commerce looks for search terms within the originating URL. The tag can extract those search terms from URLs generated by Google, Yahoo, and MSN Live. This tag has a limit of 50 characters. |
| were **referred from a URL** (string) | This tag captures all or part of the URL of the referring site. For example: http://www.google.ca/aclk?sa=l&amp;ai=Cu3BuHnDnSebAJ56wsAPcq-GAC6WN\_nvZ5pr\_A8nYoP4ECAAQAVCyrdOV-\_\_\_\_\_8BYP3A-4DMA6ABp8Cv\_gPIAQGqBBtP0GD2nmBaPAvT5Q8HWz\_UpVlyLbZk9rI0Peg&amp;sig=AGiWqtzbc0FN-Ac8TwPg1073g\_yo6E\_aNg&amp;q=http://www.elasticpath.com<br/><br/> This tag has a limit of 50 characters. |

### Customer Profile Tags

| **Tag name** | **Description** |
| --- | --- |
| are of **age** (years)(integer) | This tag retrieves the shopper&#39;s age, as computed from their birth date, if that information is stored in their customer profile. The information from this tag is only retained when the shopper is logged into their account. |
| are of **gender** (string) | This tag references the shoppers&#39; Gender tag (M/F) if stored in their customer profile. Note that this tag is case-sensitive and must be entered in upper case.The information from this tag is only retained when the shopper is logged into their account. |

See your system administrator for further information.

| **Tag name** | **Description** |
| --- | --- |
| are browsing from a **time zone +/- offset from GMT** (string) | The shopper&#39;s time zone value is retrieved for this tag using a third party GEO IP service. The time zone is determined relative to GMT (Greenwich Mean Time).  <br/> <br/> **\*** _See the Proxy note below._ |
| are browsing from an internet **connection of type** (string) | This tag identifies the type of connection that the shopper is using to access the store. It uses a third-party GEO IP service to determine connection type. <br/><br/> Examples of connection types include: <br/> - Dial up <br/> - Cable<br/>  <br/>**\*** _See the Proxy note below._ |
| are browsing from an internet **IP routing of type** (integer) | This tag identifies the IP Routing Type of a shopper&#39;s connection to the store. Typical values returned for this could be _regional proxy_, _superproxy_, or _mobilegateway_. It is useful in determining the shopper&#39;s actual location relative to the IP address returned for them. |
| are browsing from an ISP with **second level domain** (string) | This tag retrieves the second level domain name from the shopper&#39;s connection to the store. The second level domain name is the portion of the internet domain name directly above a top level domain name. It is usually the name of the organization that the domain name represents. Examples of a second level domain are &quot;google&quot; and &quot;wikipedia&quot;. This tag has a limit of 50 characters. <br/><br/> **\*** _See the Proxy note below._ |
| are browsing from an ISP with **top level domain** (string) | This tag retrieves the top level domain name from the shopper&#39;s connection to the store. The top level domain name is the portion of the internet domain name following the dot. Examples of a top level domain are &quot;.com&quot;, &quot;.ca&quot;, &quot;.org&quot;. This tag has a limit of 50 characters. <br/><br/> **\*** _See the Proxy note below._ |
| are browsing from **city** (string) | This tag retrieves the shopper&#39;s city based on their IP address using a third-party GEO IP service. <br/> <br/> **\*** _See the Proxy note below._ |
| are browsing from **continent** (string) | This tag identifies the continent from where the shopper has connected. The identification is performed using a third-party GEO IP service, and is based on the shopper&#39;s IP address. <br/> <br/> **\*** _See the Proxy note below._   |
| are browsing from **country code** (string) | This tag identifies the country from where the shopper is connected to the storefront. The identification is performed using a third-party GEO IP service, and is based on the shopper&#39;s IP address. This tag has a limit of 2 characters. <br/></br> **\*** _See the Proxy note below._ |
| are browsing from **state** or **province** (string) | This tag uses a third party GEO IP service to retrieve the shopper&#39;s state or province.This tag has a limit of 50 characters. <br/><br/> **\*** _See the Proxy note below._ |
| are browsing from **zip/postal code** (string) | This tag uses a third party GEO IP service to retrieve the shopper's zip/postal code. This tag has a limit of 8 characters. <br/><br/> **\*** _See the Proxy note below._ |

**Note:** Some shoppers may be browsing the web behind a proxy. In such cases, the values stored in the shopper&#39;s geo-location tags reflect the location of the proxy server, which may be different from the location of the shopper.

### Shopping Cart Tags

| **Tag name** | **Description** |
| --- | --- |
| have a **cart subtotal** (decimal) | The value assigned to this tag is evaluated against the current sub-total of the customer&#39;s shopping cart. |
