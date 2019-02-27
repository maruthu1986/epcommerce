/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment.views.order;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.util.DateTimeUtilFactory;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderAddress;
import com.elasticpath.money.MoneyFormatter;

/**
 * Label provider for order search result view. 
 */
public class OrderListViewLabelProvider extends LabelProvider implements ITableLabelProvider {
	
	// Column indices
	private static final int INDEX_ORDERNUMBER = 0;
	private static final int INDEX_STORE = 1;
	private static final int INDEX_CUSTOMERNAME = 2;
	private static final int INDEX_DATE = 3;
	private static final int INDEX_TOTAL = 4;
	private static final int INDEX_STATUS = 5;
	

	/**
	 * Get the image to put in each column.
	 * 
	 * @param element the row object
	 * @param columnIndex the column index
	 * @return the Image to put in the column
	 */
	@Override
	public Image getColumnImage(final Object element, final int columnIndex) {
		return null;
	}

	/**
	 * Get the text to put in each column.
	 * 
	 * @param element
	 *            the row object
	 * @param columnIndex
	 *            the column index
	 * @return the String to put in the column
	 */
	@Override
	public String getColumnText(final Object element, final int columnIndex) {
		if (!(element instanceof Order)) {
			return null;
		}
		final Order order = (Order) element;

		switch (columnIndex) {

			case INDEX_ORDERNUMBER:
				return order.getOrderNumber();
			case INDEX_STORE:
				return order.getStore().getName();
			case INDEX_CUSTOMERNAME:
				String customerName = StringUtils.EMPTY;
				final OrderAddress address = order.getBillingAddress();
				if (address != null) {
					customerName = address.getFullName();
				}
				return customerName;
			case INDEX_DATE:
				return DateTimeUtilFactory.getDateUtil().formatAsDateTime(order.getCreatedDate());
			case INDEX_TOTAL:
				return getMoneyFormatter().formatCurrency(order.getTotalMoney(), order.getLocale());
			case INDEX_STATUS:
				return FulfillmentMessages.get().getLocalizedName(order.getStatus());
			default:
				return ""; //$NON-NLS-1$
		}
	}


	public MoneyFormatter getMoneyFormatter() {
		return ServiceLocator.getService(ContextIdNames.MONEY_FORMATTER);
	}
}