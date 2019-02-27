/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment.editors.browser;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;

/**
 * Extends org.eclipse.ui.internal.browser.BrowserViewer, The purpose of this class is to redirect the busy indicator's target url
 * set by Eclipse folks to EP's home page.
 */
@SuppressWarnings("PMD") //TODO-RAP-M3
public class EpBrowser extends Browser {

	/**
	 * Constructor.
	 * 
	 * @param parent the composite
	 * @param style the style
	 */
	public EpBrowser(final Composite parent, final int style) {
		super(parent, style);

	}

	// RCPRAP: unclear about Browsers right now.
	@Override
	public boolean setUrl(final String url) {
		setURL(url, true);
		return true;
	}

	/**
	 * Implementation of the private method used by Eclipse with modification on target url.
	 * 
	 * @param url the url to navigate to
	 * @param browse boolean browse
	 */
	private void setURL(final String url, final boolean browse) {
		String targetUrl = url;

		// RCPRAP
		//Trace.trace(Trace.FINEST, "setURL: " + targetUrl + " " + browse); //$NON-NLS-1$ //$NON-NLS-2$
		if (targetUrl == null) {
			// RCPRAP
			//home();
			return;
		}

		// this is to overwrite the busy indicator target url hard coded in the super class
		// by the Eclipse folks
		if ("http://www.eclipse.org".equalsIgnoreCase(targetUrl)) { //$NON-NLS-1$
			targetUrl = "http://www.elasticpath.com"; //$NON-NLS-1$
		}

		if (browse) {
			navigate(targetUrl);
		}

		// RCPRAP
		//addToHistory(targetUrl);
		//updateHistory();
	}

	/**
	 * Implementation of the private method used by Eclipse.
	 * 
	 * @param url the target url to navigate to
	 * @return true if success, false if failure
	 */
	private boolean navigate(final String url) {
		// RCPRAP
/*		Trace.trace(Trace.FINER, "Navigate: " + url); //$NON-NLS-1$
		if (url != null && url.equals(getURL())) {
			refresh();
			return true;
		}
		if (browser != null) {
			return browser.setUrl(url);
		}
		return text.setUrl(url);*/
		return true;
	}

}
