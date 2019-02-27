/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.stores.browser;

import org.eclipse.swt.widgets.Composite;
//import org.eclipse.ui.internal.browser.BrowserViewer;

/**
 * A browser editor, used for logging onto the store front via the CSR login controller.
 */
@SuppressWarnings("PMD") //TODO-RAP-M3
public class EpBrowserEditor { // extends org.eclipse.ui.internal.browser.WebBrowserEditor {

	/**
	 * Ep browser id.
	 */
	public static final String EPSTOREBROWSEREDITORID = "com.elasticpath.cmclient.admin.stores.browser.EpBrowserEditor"; //$NON-NLS-1$

	/**
	 * Ep browser.
	 */
	private EpBrowser webBrowser;

	//	@Override

	/**
	 * .
	 *
	 * @param parent parent
	 */
	public void createPartControl(final Composite parent) {
/*		EpBrowserInput input = (EpBrowserInput) getWebBrowserEditorInput();

		int style = 0;
		if (input == null || input.isLocationBarLocal()) {
			style += BrowserViewer.LOCATION_BAR;
		}
		if (input == null || input.isToolbarLocal()) {
			style += BrowserViewer.BUTTON_BAR;
		}
		webBrowser = new EpBrowser(parent, style);
		webBrowser.getBrowser().clearSessions();
		webBrowser.getBrowser().addLocationListener(new LocationAdapter() {
			@Override
			public void changed(final LocationEvent event) {
				if (event.location.contains("http:")) { //$NON-NLS-1$
					webBrowser.getBrowser().removeLocationListener(this);
					try {
						webBrowser.getBrowser().setUrl(initialURL);
					} catch (Exception e) {
						throw new IllegalArgumentException("Malformed URL: " + initialURL //$NON-NLS-1$
								+ "\nOriginal Exception\n" + e); //$NON-NLS-1$
					}
				}
			}
		});

		removeCookie(input.getStoreUrl());
		webBrowser.setContainer(this);*/
	}

	private void removeCookie(final String baseUrl) {
//		String url = baseUrl + "/sign-out-customer-session.ep"; //$NON-NLS-1$
//		webBrowser.getBrowser().setUrl(url);
	}

}
