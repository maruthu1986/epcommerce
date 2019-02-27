/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.store.promotions.event;

import java.util.ArrayList;
import java.util.List;

import com.elasticpath.cmclient.core.CmSingletonUtil;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.domain.rules.CouponUsage;
import com.elasticpath.domain.rules.Rule;

/**
 * Event service for sending notifications on occurring events.
 */
public final class PromotionsEventService {

	private final List<PromotionsEventListener> promotionEventListeners;

	private final List<CouponEventListener> couponEventListeners;

	/**
	 * Private constructor following the singleton pattern.
	 */
	private PromotionsEventService() {
		super();
		promotionEventListeners = new ArrayList<>();
		couponEventListeners = new ArrayList<>();
	}

	/**
	 * Gets a singleton instance of <code>PromotionsEventService</code>.
	 *
	 * @return singleton instance of <code>PromotionsEventService</code>
	 */
	public static PromotionsEventService getInstance() {
		return CmSingletonUtil.getSessionInstance(PromotionsEventService.class);
	}

	/**
	 * Registers a <code>PromotionsEventListener</code> listener.
	 *
	 * @param listener the promotions event listener
	 */
	public void registerPromotionsEventListener(final PromotionsEventListener listener) {
		if (!promotionEventListeners.contains(listener)) {
			promotionEventListeners.add(listener);
		}
	}

	/**
	 * Unregisters a <code>PromotionsEventListener</code> listener.
	 *
	 * @param listener the promotions event listener
	 */
	public void unregisterPromotionListener(final PromotionsEventListener listener) {
		if (promotionEventListeners.contains(listener)) {
			promotionEventListeners.remove(listener);
		}
	}

	/**
	 * Notifies all the listeners with a <code>SearchResultEvent</code> event.
	 *
	 * @param event the search result event
	 */
	public void firePromotionSearchResultEvent(final SearchResultEvent<Rule> event) {
		for (final PromotionsEventListener eventListener : promotionEventListeners) {
			eventListener.searchResultsUpdate(event);
		}
	}

	/**
	 * Notifies all the listeners with an <code>PromotionsChangeEvent</code> event.
	 *
	 * @param event the promotions change event
	 */
	public void firePromotionsChangeEvent(final PromotionsChangeEvent event) {
		for (final PromotionsEventListener eventListener : promotionEventListeners) {
			eventListener.promotionChanged(event);
		}
	}

	/**
	 * Registers a <code>CouponListener</code> listener.
	 *
	 * @param listener the coupon listener
	 */
	public void registerCouponEventListener(final CouponEventListener listener) {
		if (!couponEventListeners.contains(listener)) {
			couponEventListeners.add(listener);
		}
	}

	/**
	 * Unregisters a <code>CouponListener</code> listener.
	 *
	 * @param listener the coupon event listener
	 */
	public void unregisterCouponListener(final CouponEventListener listener) {
		if (couponEventListeners.contains(listener)) {
			couponEventListeners.remove(listener);
		}
	}

	/**
	 * Notifies all the listeners with a <code>SearchResultEvent</code> event.
	 *
	 * @param event the search result event
	 */
	public void fireCouponSearchResultEvent(final SearchResultEvent<CouponUsage> event) {
		for (final CouponEventListener eventListener : couponEventListeners) {
			eventListener.searchResultsUpdate(event);
		}
	}

	/**
	 * Notifies all the listeners with an <code>CouponChangeEvent</code> event.
	 *
	 * @param event the coupon change event
	 */
//	public void fireCouponChangeEvent(final CouponChangeEvent event) {
//		for (final CouponEventListener eventListener : couponEventListeners) {
//			eventListener.couponChanged(event);
//		}
//	}
}
