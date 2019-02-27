# Appendix C: State Tables

[TOC]

## Automatic Order Unlocking

| **CSR Action** | **System Action** |
| --- | --- |
| CSR saves order | The system automatically unlocks the given order on completion of the save transaction. |
| CSR closes order editor (saves changes on prompt) | The system automatically unlocks the given order on completion of the save transaction. |
| CSR closes order editor (abandons changes on prompt) | The system automatically unlocks the given order. |
| CSR closes Elastic Path Commerce (with open orders and saves changes on prompt) | The system automatically unlocks each order on completion of the save transaction. |
| CSR closes Elastic Path Commerce (with open orders and abandons changes on prompt) | The system automatically unlocks each order. |

## Inventory Auditing

| Event | Description | Action |
| --- | --- | --- |
| New order for shippable SKU with &quot;In-Stock&quot; inventory | A new order is created in the store, Elastic Path Commerce, or via a Web Service | &quot;Allocated&quot; quantity is increased |
| Change in shipment SKU quantity before shipment is packed | A shipment is edited in Elastic Path Commerce | &quot;Allocated&quot; quantity may be reduced or increased accordingly |
| Removal of a shipment SKU before shipment is packed | A shipment is edited in Elastic Path Commerce | &quot;Allocated&quot; quantity is decreased |
| Addition of a new SKU to a shipment before shipment is packed | A shipment is edited in Elastic Path Commerce | &quot;Allocated&quot; quantity is increased |
| Order Cancellation | An order is canceled in the store, Elastic Path Commerce, or via a Web Service | &quot;Allocated&quot; quantity is decreased |
| Manual &quot;On Hand&quot; stock adjustment | Stock is adjusted in the Elastic Path Commerce warehouse | &quot;On Hand&quot; quantity may be reduced or increased |
| Stock upload (with no affected back-order or pre-order SKUs) | Stock is uploaded / received in the Elastic Path Commerce warehouse | &quot;On hand&quot; quantity is increased |
| Stock upload (with affected back-order or pre-order SKUs) | Stock is uploaded / received in the Elastic Path Commerce warehouse | &quot;Allocated&quot; quantity is increased &quot; and &quot;On hand&quot; quantity is increased |
| Shipment is completed (shipped) | Shipment is completed in Elastic Path Commerce or via a Web Service. | &quot;On Hand&quot; quantity is decreased &quot;Allocated&quot; quantity is decreased |

## Order Confirmation E-mail

| Inventory Status (at the time of receipt creation) | Sample SKU / Line item availability message |
| --- | --- |
| Unallocated (Always Available) | Usually ships in 1-2 days. |
| Allocated | Usually ships in 1-2 days. |
| Awaiting Allocation (Back Order) | On Backorder. Expected shipment date: 12th March 2008 |
| Awaiting Allocation (Pre Order) | On Preorder. Expected shipment date: 12th March 2008 |

## Order History

| Inventory Status (at the time of receipt creation) | SKU / Line item Availability Message (if shipment has shipped) | Sample SKU / Line item Availability Message (if shipment has not shipped) |
| --- | --- | --- |
| Unallocated (Always Available) | None | Usually ships in 1-2 days. |
| Allocated | None | Usually ships in 1-2 days. |
| Awaiting Allocation (Back Order) | None | On Backorder. Expected shipment date: 12th March 2008 |
| Awaiting Allocation (Pre Order) | None | On Preorder. Expected shipment date: 12th March 2008 |

## Order Receipt

| Inventory Status (at the time of receipt creation) | Sample SKU / Line item Availability Message |
| --- | --- |
| Unallocated (Always Available) | Usually ships in 1-2 days. |
| Allocated | Usually ships in 1-2 days. |
| Awaiting Allocation (Back Order) | On Backorder. Expected shipment date: 12th March 2008 |
| Awaiting Allocation (Pre Order) | On Preorder. Expected shipment date: 12th March 2008 |

## Product Availability Rules

You cannot change an _Always Available_ product to a different availability rule; conversely, you cannot change other availability rules to the Always Available rule.

Availability rules for _Available only if in Stock_, _Available on Pre Order_, and _Available on Back Order_ can change amongst each other.

> **Note**: If you need to change a product&#39;s availability to _Always Available_ from the other rules, we recommend you creating a new product and assigning it the _Always Available_ rule. Then, delete or discontinue using the previous product.

## Product Display Page Messages

| State # | Availability Rule | Stock Status | Availability Date | Displayed Message | Add to Cart Button |
| --- | --- | --- | --- | --- | --- |
| 1 | Available only if in stock | Out of stock | null | Out of stock | Disabled |
| 2 | Available only if in stock | Out of stock | 27th Mar 2007 | Out of stock (estimated back in stock date: March 27, 2007) | Disabled |
| 3 | Available only if in stock | In Stock | null | In Stock, x available | Enabled |
| 4 | Available on Pre Order | Out of stock | 27th Mar 2007 | Available for pre order (estimated release date: March 27, 2007) | Replaced with Preorder button -  Enabled |
| 5 | Available on Pre Order | In Stock | 27th Mar 2007 | In Stock, x available | Enabled |
| 6 | Available on Back Order | Out of stock | 27th Mar 2007 | Available for back order (estimated shipping date: March 27, 2007) | Enabled |
| 7 | Available on Back Order | Out of stock | null | Available for back order (will ship when available) | Enabled |
| 8 | Available on Back Order | In Stock | 27th Mar 2007 | In Stock, x available | Enabled |

## Returns and Exchanges

| Action | Physical Return | Action on return creation | Action on return of ALL SKUs in RMA |
| --- | --- | --- | --- |
| Refund | Yes | None | CSR is notified that refund can be given. |
| Refund | No | CSR can refund via original payment method or new CC | None |
| Exchange | Yes | None | CSR is notified that new &quot;exchange&quot; order can be created. |
| Exchange | No | New &quot;exchange&quot; order is created | None |
