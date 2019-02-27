/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.shoppingcart;

/**
 * ExternalAuthUrls.
 */
public class ExternalAuthUrls {
	private String redirectUrl;
	private String finishUrl;
	private String cancelUrl;

	/**
	 * Returns the url that should be used as the post action if the gateway requires a redirect.
	 *
	 * @return the redirect url.
	 */
	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(final String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	/**
	 * Returns the url that the gateway should call if the direct post authentication is successful.
	 *
	 * @return the finish url.
	 */
	public String getFinishUrl() {
		return finishUrl;
	}

	public void setFinishUrl(final String finishUrl) {
		this.finishUrl = finishUrl;
	}

	/**
	 * Returns the url that the gateway should call if the direct post authentication fails.
	 *
	 * @return the cancel url.
	 */
	public String getCancelUrl() {
		return cancelUrl;
	}

	public void setCancelUrl(final String cancelUrl) {
		this.cancelUrl = cancelUrl;
	}
}
