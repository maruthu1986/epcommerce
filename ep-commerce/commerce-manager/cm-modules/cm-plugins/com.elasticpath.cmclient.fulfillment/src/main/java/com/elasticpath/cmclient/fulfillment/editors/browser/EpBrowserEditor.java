/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment.editors.browser;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

// RCPRAP
//import org.eclipse.swt.browser.ProgressAdapter;

// RCPRAP
//import org.eclipse.ui.internal.browser.BrowserViewer;

/**
 * A browser editor, used for logging onto the store front via the CSR login controller. 
 */
@SuppressWarnings("PMD") //TODO-RAP-M3
public class EpBrowserEditor { // RCPRAP extends org.eclipse.ui.internal.browser.WebBrowserEditor {
	
	/**
	 * Ep browser id.
	 */
	public static final String EPBROWSEREDITORID = "com.elasticpath.cmclient.fulfillment.editors.browser.EpBrowserEditor"; //$NON-NLS-1$
//RCPRAP
/*	@Override
	public void createPartControl(final Composite parent) {
		EpBrowserInput input = (EpBrowserInput) getWebBrowserEditorInput();

		int style = 0;
		if (input == null || input.isLocationBarLocal()) {
			style += BrowserViewer.LOCATION_BAR;
		}
		if (input == null || input.isToolbarLocal()) {
			style += BrowserViewer.BUTTON_BAR;
		}
		webBrowser = new EpBrowser(parent, style);
		webBrowser.getBrowser().addProgressListener(new ProgressAdapter() {

			public void completed(final ProgressEvent event) {
				webBrowser.getBrowser().removeProgressListener(this);
				try {
					// inject an HTML form which submits itself
					webBrowser.getBrowser().setText(getHtml(initialURL));
				} catch (Exception e) {
					throw new IllegalArgumentException("Malformed URL: " + initialURL //$NON-NLS-1$
							+ "\nOriginal Exception\n" + e); //$NON-NLS-1$
				}
			}
		});

		removeCookie(input.getStoreUrl());

		webBrowser.setContainer(this);
	}*/

	/**
	 *
	 * @throws MalformedURLException
	 */
// RCPRAP
/*	private void removeCookie(final String baseUrl) {
		String url = baseUrl + "/sign-out-customer-session.ep"; //$NON-NLS-1$
		webBrowser.getBrowser().setUrl(url);
	}*/
	
	/**
	 * Parse the URL and query string to construct a FORM to POST.
	 * <p>
	 * Example initial URL: 
 	 * https://localhost:8443/storefront/cmclient-signin.ep?createUser=true&username=admin&password=123&custusername=123
	 *
	 * @return The FORM HTML with input parameters injected.
	 * @throws MalformedURLException If the URL and query string cannot be parsed.
	 */
	@SuppressWarnings("PMD.ConsecutiveLiteralAppends")
	private String getHtml(final String baseUrl) throws MalformedURLException {
		// parse the URL to build the form action
		URL url = new URL(baseUrl);

		StringBuilder actionBuffer = new StringBuilder(url.getProtocol());
		actionBuffer.append("://").append(url.getHost()); //$NON-NLS-1$
		
		if (url.getPort() != -1) {
			actionBuffer.append(':').append(url.getPort()); 
		}
		
		actionBuffer.append(url.getPath());
		
		// split query string into name/value pairs
		String[] nameValues = url.getQuery().split("&"); //$NON-NLS-1$
		Map<String, String> props = new HashMap<>();
		for (String nameValue : nameValues) {
			String[] split = nameValue.split("="); //$NON-NLS-1$
			props.put(split[0], split[1]);
		}
		
		// build the HTML
		StringBuilder html = new StringBuilder(
				"<html>\n<body onload='document.postform.submit();'>\n<form style='display: none;' name='postform' action='"); //$NON-NLS-1$
		html.append(actionBuffer);
		html.append("' method='post'>\n"); //$NON-NLS-1$
		
		// add the hidden form fields parsed from the query string
		for (String name : props.keySet()) {
			html.append("<input type='hidden' name='"); //$NON-NLS-1$
			html.append(name);
			html.append("' value='"); //$NON-NLS-1$
			html.append(props.get(name));
			html.append("'/>\n"); //$NON-NLS-1$
		}
		
		html.append("</form>\n</body>\n</html>\n"); //$NON-NLS-1$
		
		return html.toString();
	}

}
