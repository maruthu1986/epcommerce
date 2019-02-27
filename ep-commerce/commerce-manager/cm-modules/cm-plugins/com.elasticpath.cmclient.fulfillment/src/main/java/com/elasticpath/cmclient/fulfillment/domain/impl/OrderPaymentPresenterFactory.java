/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.fulfillment.domain.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.eclipse.osgi.util.NLS;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.util.DateTimeUtilFactory;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.FulfillmentPermissions;
import com.elasticpath.cmclient.fulfillment.domain.OrderPaymentPresenter;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.domain.store.Store;
import com.elasticpath.money.MoneyFormatter;
import com.elasticpath.plugin.payment.PaymentType;

/**
 * Wraps a core domain OrderPayment to provide display strings
 * for a commerce manager client.
 */
@SuppressWarnings({ "PMD.GodClass" })
public class OrderPaymentPresenterFactory {
	private static final Logger LOG = Logger.getLogger(OrderPaymentPresenterFactory.class);

	/**
	 * Retrieves an OrderPaymentPresenter appropriate for the given OrderPayment.
	 * This implementation switches on the OrderPayment's PaymentType enum.
	 * Calls {@link #getOrderPaymentCreditCardPresenter(OrderPayment)},
	 * {@link #getOrderPaymentGiftCertificatePresenter(OrderPayment)},
	 * {@link #getOrderPaymentTokenPresenter(OrderPayment)},
	 * {@link #getOrderPaymentPaypalExpressPresenter(OrderPayment)},
	 * {@link #getOrderPaymentReturnExchangePresenter(OrderPayment)}.
	 * @param orderPayment the orderpayment object we're presenting
	 * @return an OrderPaymentPresenter appropriate to the given OrderPayment, or null
	 * if one cannot be found.
	 */
	public OrderPaymentPresenter getOrderPaymentPresenter(final OrderPayment orderPayment) {
		if (orderPayment == null) {
			LOG.error("The given orderPayment was null"); //$NON-NLS-1$
			return null;
		}
		PaymentType paymentType = orderPayment.getPaymentMethod();
		OrderPaymentPresenter presenter = null;
		if (PaymentType.CREDITCARD_DIRECT_POST == paymentType) {
			return getOrderPaymentCreditCardPresenter(orderPayment);
		} else if (PaymentType.GIFT_CERTIFICATE == paymentType) {
			return getOrderPaymentGiftCertificatePresenter(orderPayment);
		} else if (PaymentType.PAYPAL_EXPRESS == paymentType) {
			return getOrderPaymentPaypalExpressPresenter(orderPayment);
		} else if (PaymentType.PAYMENT_TOKEN == paymentType) {
			return getOrderPaymentTokenPresenter(orderPayment);
		} else if (PaymentType.RETURN_AND_EXCHANGE == paymentType) {
			return getOrderPaymentReturnExchangePresenter(orderPayment);
		} else {
			if (LOG.isDebugEnabled()) {
				LOG.debug(orderPayment.getPaymentMethod() + " is not handled natively, looking for hooked presenter."); //$NON-NLS-1$
			}
			presenter = getAdditionalOrderPaymentPresenter(orderPayment);
		}
		return presenter;
	}

	/**
	 * Hook for additional order payment presenter classes. If an OrderPaymentType is
	 * added to the system that is not in the collection of OrderPaymentTypes that are
	 * handled by the internal classes of this class, this class can be extended and this
	 * method implemented to provide presenter classes for those new OrderPaymentTypes.
	 * @param orderPayment the OrderPayment object whose methods can be called in order to
	 * determine presentation strings.
	 * @return a list of additional OrderPaymentPresenter objects
	 */
	protected OrderPaymentPresenter getAdditionalOrderPaymentPresenter(final OrderPayment orderPayment) {
		return null; //hook
	}

	/**
	 * Gets an OrderPaymentPresenter for credit card OrderPayments. Can be overridden to supply something
	 * other than the default Presenter.
	 * @param orderPayment the order payment
	 * @return the presenter
	 */
	protected OrderPaymentPresenter getOrderPaymentCreditCardPresenter(final OrderPayment orderPayment) {
		return new OrderPaymentCreditCardPresenter(orderPayment);
	}

	/**
	 * Gets an OrderPaymentPresenter for Gift Certificate OrderPayments. Can be overridden to supply something
	 * other than the default Presenter.
	 * @param orderPayment the order payment
	 * @return the presenter
	 */
	protected OrderPaymentPresenter getOrderPaymentGiftCertificatePresenter(final OrderPayment orderPayment) {
		return new OrderPaymentGiftCertificatePresenter(orderPayment);
	}

	/**
	 * Gets an OrderPaymentPresenter for PaypalExpress OrderPayments. Can be overridden to supply something
	 * other than the default Presenter.
	 * @param orderPayment the order payment
	 * @return the presenter
	 */
	protected OrderPaymentPresenter getOrderPaymentPaypalExpressPresenter(final OrderPayment orderPayment) {
		return new OrderPaymentPaypalExpressPresenter(orderPayment);
	}

	/**
	 * Gets an OrderPaymentPresenter for Token OrderPayments. Can be overridden to supply something
	 * other than the default Presenter.
	 * @param orderPayment the order payment
	 * @return the presenter
	 */
	protected OrderPaymentPresenter getOrderPaymentTokenPresenter(final OrderPayment orderPayment) {
		return new OrderPaymentTokenPresenter(orderPayment);
	}

	/**
	 * Gets an OrderPaymentPresenter for Return / Exchange OrderPayments. Can be overridden to supply something
	 * other than the default Presenter.
	 * @param orderPayment the order payment
	 * @return the presenter
	 */
	protected OrderPaymentPresenter getOrderPaymentReturnExchangePresenter(final OrderPayment orderPayment) {
		return new OrderPaymentReturnExchangePresenter(orderPayment);
	}

	protected MoneyFormatter getMoneyFormatter() {
		return ServiceLocator.getService(ContextIdNames.MONEY_FORMATTER);
	}

	/**
	 * Provides default implementations of most OrderPaymentPresenter methods.
	 */
	protected abstract class AbstractOrderPaymentPresenter implements OrderPaymentPresenter {

		private final OrderPayment orderPayment;

		/**
		 * Constructor.
		 * @param orderPayment the object being presented
		 */
		protected AbstractOrderPaymentPresenter(final OrderPayment orderPayment) {
			this.orderPayment = orderPayment;
		}

		/**
		 * @return the OrderPayment being presented
		 */
		protected OrderPayment getOrderPayment() {
			return this.orderPayment;
		}

		/**
		 * Calls {@link DateTimeUtilFactory#getDateUtil()#formatAsDateTime()}.
		 * @return the date the OrderPayment was created, as a formatted string
		 */
		@Override
		public String getDisplayCreatedDate() {
			return DateTimeUtilFactory.getDateUtil().formatAsDateTime(orderPayment.getCreatedDate());
		}

		@Override
		public String getDisplayPaymentAmount() {
			return getMoneyFormatter().formatCurrency(orderPayment.getAmountMoney(), getLocale());
		}

		@Override
		public String getDisplayPaymentMethod() {
			return FulfillmentMessages.get().getLocalizedName(orderPayment.getPaymentMethod());
		}

		@Override
		public String getDisplayStatus() {
			return FulfillmentMessages.get().getLocalizedName(orderPayment.getStatus());
		}

		@Override
		public String getDisplayTransactionId() {
			return orderPayment.getReferenceId();
		}

		@Override
		public String getDisplayTransactionType() {
			return orderPayment.getTransactionType();
		}

		@Override
		public String getDisplayShipmentId() {
			OrderShipment shipment = orderPayment.getOrderShipment();
			String displayShipmentId;
			if (orderPayment.isPaymentForSubscriptions()) {
				displayShipmentId = FulfillmentMessages.get().Shipment_For_Subscriptions;
			} else if (shipment == null) {
				displayShipmentId = FulfillmentMessages.get().PaymentHistorySection_NotApplicable;
			} else {
				if (shipment.getOrderShipmentType() == ShipmentType.ELECTRONIC) {
					displayShipmentId = FulfillmentMessages.get().OrderPaymentHistorySection_ElectronicShipmentId;
				} else {
					displayShipmentId = shipment.getShipmentNumber();
				}
			}
			return displayShipmentId;
		}

		/**
		 * @return the order for which the order payment was created, or
		 * null if it is not available because it was not loaded from the
		 * persistence layer.
		 */
		protected Order getOrder() {
			return orderPayment.getOrder();
		}

		/**
		 * Gets the locale of the order if available.
		 * @return the order locale or null
		 */
		protected Locale getLocale() {
			if (getOrder() != null) {
				return getOrder().getLocale();
			}
			return null;
		}
	}

	/**
	 * Presenter for an OrderPayment using the type {@link PaymentType#CREDITCARD_DIRECT_POST}.
	 */
	protected class OrderPaymentCreditCardPresenter extends AbstractOrderPaymentPresenter {

		/**
		 * Constructor.
		 * @param orderPayment the object being presented.
		 */
		public OrderPaymentCreditCardPresenter(final OrderPayment orderPayment) {
			super(orderPayment);
		}

		/**
		 * Calls {@link #getCreditCardNumberString()}.
		 * @return the string description of the order payment, which may be a masked
		 * credit card number if the user is not authorized to see the full card number.
		 */
		@Override
		public String getDisplayPaymentDetails() {
			final List<String> bindings = new ArrayList<>();
			bindings.add(getCreditCardNumberString());
			final int numberOfObjectsToBind = 4;
			final Object[] elementsToBind = bindings.toArray(new Object[numberOfObjectsToBind]);
			return
				NLS.bind(FulfillmentMessages.get().RefundWizard_CardDescription,
				elementsToBind);
		}
		
		/**
		 * Gets the display string for the Credit Card Number.
		 * Calls {@link #isAuthorized()}.
		 * @return the OrderPayment's full credit card number if the user is authorized to see it, otherwise
		 * the masked credit card number.
		 */
		String getCreditCardNumberString() {
			return getOrderPayment().getDisplayValue();
		}
		
		@Override
		public String getDisplayTransactionId() {
			return getOrderPayment().getAuthorizationCode();
		}
		
		/** 
		 * Uses the AuthorizationService to determine whether the user is authorized to view
		 * credit card numbers in the store in which the current OrderPayment was created.
		 * Calls {@link #getStore()}.
		 * @return true if the current user is both authorized to view credit card numbers and
		 * has permissions for the store in which the current OrderPayment was created.
		 */
		protected boolean isAuthorized() {
			return AuthorizationService.getInstance().isAuthorizedWithPermission(FulfillmentPermissions.VIEW_FULL_CREDITCARD_NUMBER)
			&& AuthorizationService.getInstance().isAuthorizedForStore(getStore());
		}
		
		/**
		 * Calls {@link #getOrder()}.
		 * @return the order payment's Store, or null if it was not loaded from the persistence layer.
		 */
		protected Store getStore() {
			Order order = getOrder();
			if (order != null) {
				return order.getStore();
			}
			return null;
		}
	}
	
	/**
	 * Presenter for an OrderPayment using the type {@link PaymentType#GIFT_CERTIFICATE}.
	 */
	protected class OrderPaymentGiftCertificatePresenter extends AbstractOrderPaymentPresenter {
		
		/** 
		 * Constructor. 
		 * @param orderPayment the object being presented.
		 */
		public OrderPaymentGiftCertificatePresenter(final OrderPayment orderPayment) {
			super(orderPayment);
		}

		/** 
		 * Calls {@link #isAuthorized()}.
		 * @return the string description of the order payment, which may be a masked
		 * gift certificate number if the user is not authorized to see the full gift certificate number.
		 */
		@Override
		public String getDisplayPaymentDetails() {
			StringBuffer paymentString = new StringBuffer(FulfillmentMessages.get().PaymentType_GiftCertificate).append(' ');
			if (isAuthorized()) {
				paymentString = paymentString.append(getOrderPayment().getGiftCertificate().displayGiftCertificateCode());
			} else {
				paymentString = paymentString.append(getOrderPayment().getGiftCertificate().displayMaskedGiftCertificateCode());
			}
			return paymentString.toString();
		}
		
		/** 
		 * Uses the AuthorizationService to determine whether the user is authorized to view
		 * credit card numbers in the store in which the current OrderPayment was created.
		 * Calls {@link #getStore()}.
		 * @return true if the current user is both authorized to view credit card numbers and
		 * has permissions for the store in which the current OrderPayment was created.
		 */
		protected boolean isAuthorized() {
			return AuthorizationService.getInstance().isAuthorizedWithPermission(FulfillmentPermissions.VIEW_FULL_CREDITCARD_NUMBER)
			&& AuthorizationService.getInstance().isAuthorizedForStore(getStore());
		}
		
		/**
		 * Calls {@link #getOrder()}.
		 * @return the order payment's Store, or null if it was not loaded from the persistence layer.
		 */
		protected Store getStore() {
			Order order = getOrder();
			if (order != null) {
				return order.getStore();
			}
			return null;
		}
	}
	
	/**
	 * Presenter for an OrderPayment using the type {@link PaymentType#PAYPAL_EXPRESS}.
	 */
	protected class OrderPaymentPaypalExpressPresenter extends AbstractOrderPaymentPresenter {
		
		/** 
		 * Constructor. 
		 * @param orderPayment the object being presented.
		 */
		public OrderPaymentPaypalExpressPresenter(final OrderPayment orderPayment) {
			super(orderPayment);
		}

		/** 
		 * PaypalExpress payments are simply designated with the email address.
		 * @return the string description of the order payment
		 */
		@Override
		public String getDisplayPaymentDetails() {
			return getOrderPayment().getEmail();
		}
	}
	
	/**
	 * Presenter for an OrderPayment using the type {@link PaymentType#PAYMENT_TOKEN}.
	 */
	protected class OrderPaymentTokenPresenter extends AbstractOrderPaymentPresenter {
		/** 
		 * Constructor. 
		 * @param orderPayment the object being presented.
		 */
		public OrderPaymentTokenPresenter(final OrderPayment orderPayment) {
			super(orderPayment);
		}

		/** 
		 * Token payments are designated with the token display value.
		 * @return the string description of the order payment
		 */
		@Override
		public String getDisplayPaymentDetails() {
			String displayValue = getOrderPayment().getDisplayValue();
			if (displayValue == null) {
				return FulfillmentMessages.get().OrderPaymentHistorySection_PaymentTokenDetailsPlaceholder;
			} else {
				return displayValue;
			}
		}
	}
	
	/**
	 * Presenter for an OrderPayment using the type {@link PaymentType#RETURN_AND_EXCHANGE}.
	 */
	protected class OrderPaymentReturnExchangePresenter extends AbstractOrderPaymentPresenter {
		
		/** 
		 * Constructor. 
		 * @param orderPayment the object being presented.
		 */
		public OrderPaymentReturnExchangePresenter(final OrderPayment orderPayment) {
			super(orderPayment);
		}

		@Override
		public String getDisplayPaymentAmount() {
			Order order = getOrder();
			if (order != null && order.getDueToRMAMoney() != null) {
				return getMoneyFormatter().formatCurrency(order.getDueToRMAMoney(), order.getLocale());
			}
			return super.getDisplayPaymentAmount();
		}
		
		/** 
		 * Gets the details of the order payment. For returns and exchanges
		 * the details are dependent on the Order's status.
		 * Calls {@link #getOrderStatus()}.
		 * @return the string description of the order payment's details
		 */
		@Override
		public String getDisplayPaymentDetails() {
			OrderStatus orderStatus = getOrderStatus();
			if (orderStatus != null) {
				if (orderStatus == OrderStatus.AWAITING_EXCHANGE) {
					return FulfillmentMessages.get().Exchange_Pending_Payment_Details;
				} else if (
					orderStatus != OrderStatus.AWAITING_EXCHANGE
						&& orderStatus != OrderStatus.CANCELLED) {	
					return FulfillmentMessages.get().Exchange_Completed_Payment_Details;
				}
			}
			return null;
		}
		
		@Override
		public String getDisplayTransactionType() {
			String transactionTypeString = null;
			OrderStatus orderStatus = getOrderStatus();
			if (orderStatus != null) {
				if (orderStatus == OrderStatus.AWAITING_EXCHANGE) {
					transactionTypeString = OrderPayment.AUTHORIZATION_TRANSACTION;
				} else if (orderStatus != OrderStatus.AWAITING_EXCHANGE //NOPMD
						&& orderStatus != OrderStatus.CANCELLED) {	
					transactionTypeString = OrderPayment.CAPTURE_TRANSACTION;
				} else {
					transactionTypeString = OrderPayment.REVERSE_AUTHORIZATION;
				}
			}
			return transactionTypeString;
		}
		
		/**
		 * Gets the status of the OrderPayment's Order.
		 * Calls {@link #getOrder()}.
		 * @return the status of the OrderPayment's Order, or null if the Order was not loaded
		 * from the persistence layer.
		 */
		OrderStatus getOrderStatus() {
			Order order = getOrder();
			if (order != null) {
				return order.getStatus();
			}
			return null;
		}
	}
	
}
