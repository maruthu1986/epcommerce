/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.shoppingcart.actions;

import java.util.Collection;
import java.util.Map;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.plugin.tax.domain.TaxDocument;
import com.elasticpath.plugin.tax.domain.TaxDocumentId;

/**
 * Container class for data required by setupCheckoutActions and reversibleCheckoutActions.
 */
public interface CheckoutActionContext {

	/**
	 * Gets the {@link Shopper}.
	 *
	 * @return the {@link Shopper}
	 * */
	Shopper getShopper();

	/**
	 * Gets the {@link ShoppingCart}.
	 *
	 * @return the {@link ShoppingCart}
	 * */
	ShoppingCart getShoppingCart();

	/**
	 * Returns the Shopping Cart Tax Snapshot.
	 *
	 * @return the Shopping Cart Tax Snapshot
	 */
	ShoppingCartTaxSnapshot getShoppingCartTaxSnapshot();

	/**
	 * Gets the {@link Customer}.  If you need to save the Customer, you need to know where it comes from.
	 *
	 * Look inside the implementation.
	 *
	 * @return the {@link Customer}
	 * */
	Customer getCustomer();

	/**
	 * Sets the order.
	 * @param order the order
	 * */
	void setOrder(Order order);

	/**
	 * Gets the order.
	 * @return the order
	 * */
	Order getOrder();

	/**
	 * Gets the order payment template.
	 * @return the order payment template
	 * */
	OrderPayment getOrderPaymentTemplate();

	/**
	 * Gets the is order exchange.
	 * @return the is order exchange
	 * */
	boolean isOrderExchange();

	/**
	 * Gets is Await Exchange Completion.
	 * @return is Await Exchange Completion
	 * */
	boolean isAwaitExchangeCompletion();

	/**
	 * Gets the order return exchange.
	 * @return order return exchange
	 * */
	OrderReturn getExchange();

	/**
	 * Sets the order payment list.
	 * @param orderPaymentList the order payment list
	 * */
	void setOrderPaymentList(Collection<OrderPayment> orderPaymentList);

	/**
	 * Gets order payment list.
	 * @return the order payment list
	 * */
	Collection<OrderPayment> getOrderPaymentList();

	/**
	 * Returns the Ip Address of the Customer that is doing this checkout.
	 *
	 * @return the Customer IP Address.
	 */
	String getCustomerIpAddress();

	/**
	 * Returns the Customer Session.
	 *
	 * @return the Customer Session
	 */
	CustomerSession getCustomerSession();

	/**
	 * Gets a map of tax document id and tax document.
	 *
	 * @return a map of tax document id and tax document
	 */
	Map<TaxDocumentId, TaxDocument> getTaxDocuments();

	/**
	 * Adds one tax document to the taxDocuments map.
	 *
	 * @param taxDocumentId the key of the taxDocuments map
	 * @param taxDocument the value of the taxDocuments map
	 */
	void addTaxDocument(TaxDocumentId taxDocumentId, TaxDocument taxDocument);

	/**
	 * Takes the transient orderPayments in the arguments and merges it
	 * with the internal list or orderPayments.
	 * @param transientOrderPayments the list of transient orderPayments.
	 */
	void preserveTransientOrderPayment(Collection<OrderPayment> transientOrderPayments);
}
