/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.admin.stores.browser;

import java.net.URL;

//import org.eclipse.ui.internal.browser.WebBrowserEditorInput;

/**
 * EP Browser input.
 */
@SuppressWarnings("PMD") //TODO-RAP-M3
public class EpBrowserInput { // extends WebBrowserEditorInput {


	private String storeUrl;

	/**
	 *
	 * @param url the URL
	 * @param style the style
	 * @param browserId the browser id
	 */
	public EpBrowserInput(final URL url, final int style, final String browserId) {
		//	super(url, style, browserId);
	}

	/**
	 *
	 * @return the storeUrl the store URL string
	 */
	public String getStoreUrl() {
		return storeUrl;
	}

	/**
	 *
	 * @param storeUrl the storeUrl to set
	 */
	public void setStoreUrl(final String storeUrl) {
		this.storeUrl = storeUrl;
	}
}
