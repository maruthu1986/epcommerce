/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.editors.actions;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.store.Store;
import com.elasticpath.settings.SettingsService;

/**
 * Opens up an EP browser that automatically logs customer in to the store front.
 */
@SuppressWarnings("PMD") //TODO-RAP-M3
public class OpenEpBrowserContributionAction extends Action {
	
	private static final Logger LOG = Logger.getLogger(OpenEpBrowserContributionAction.class);
	
	private static final String SETTING_STOREFRONT_URL = "COMMERCE/STORE/storefrontUrl"; //$NON-NLS-1$

	private static final String DEFAULT_CSR_LOGIN_PATH = "/cmclient-signin.ep?createUser=true"; //$NON-NLS-1$

	private final IWorkbenchPartSite site;

	private final Customer customer;

	private final String queryURL;

	private final int style;

	private final Store store;

	/**
	 * Constructor.
	 * 
	 * @param store the store
	 * @param style browser style
	 * @param queryURL the url
	 * @param customer the customer
	 * @param site the IWorkbenchPartSite
	 */
	public OpenEpBrowserContributionAction(final Store store, final int style, final String queryURL, final Customer customer,
			final IWorkbenchPartSite site) {
		this.store = store;
		this.style = style;
		this.queryURL = queryURL;
		this.customer = customer;
		this.site = site;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param store the Store
	 * @param site the WorkbenchPartSite
	 */
	public OpenEpBrowserContributionAction(final Store store, final IWorkbenchPartSite site) {
		this.store = store;
		this.style = IWorkbenchBrowserSupport.AS_EDITOR | IWorkbenchBrowserSupport.NAVIGATION_BAR;
		this.site = site;
		this.customer = null;
		this.queryURL = DEFAULT_CSR_LOGIN_PATH;
		this.setText(this.store.getName()); 
	}

	// ---- DOCrun
	@Override
	public void run() {
		//RCPRAP: not sure about Browser yet
/*		final String selectedName = this.store.getName();
		final String selectedStoreUID = Long.toString(this.store.getUidPk());

		try {
			final URL url = new URL(getCsrLoginUrl());

			EpBrowserInput browserEditorInput = new EpBrowserInput(url, this.style, selectedStoreUID);
			browserEditorInput.setStoreUrl(getStoreUrl());
			browserEditorInput.setName(selectedName);

			if (this.customer == null) {
				browserEditorInput.setToolTipText(selectedName);
			} else {
				browserEditorInput.setToolTipText("Create Order - " + this.customer.getFullName()); //$NON-NLS-1$
			}

			final IEditorReference[] editorReferences = this.site.getPage().getEditorReferences();

			for (int i = 0; i < editorReferences.length; i++) {
				if (editorReferences[i].getId().equals(EpBrowserEditor.EPBROWSEREDITORID)) {
					this.site.getPage().closeEditor(editorReferences[i].getEditor(false), false);
				}
			}

			this.site.getPage().openEditor(browserEditorInput, EpBrowserEditor.EPBROWSEREDITORID);
		} catch (MalformedURLException e) {
			LOG.error("Malformed URL.", e); //$NON-NLS-1$
		} catch (PartInitException e) {
			LOG.error("Error opening EP browser", e); //$NON-NLS-1$
		}*/
	}
	// ---- DOCrun
	
	/**
	 * Constructs the CSR storefront login URL, for example https://localhost:8443/storefront/cmclient-signin.ep?createUser=true.
	 *
	 * @return The URL string.
	 */
	private String getCsrLoginUrl() {
		final String storeUrl = getStoreUrl();
		return storeUrl.concat(this.queryURL);
	}

	/**
	 * Gets the store URL value from the settings framework.
	 */
	private String getStoreUrl() {
		SettingsService service = ServiceLocator.getService("settingsService"); //$NON-NLS-1$
		//Get the URL and remove the trailing slash if it's there
		String storeUrl = service.getSettingValue(SETTING_STOREFRONT_URL, store.getCode()).getValue();
		return StringUtils.chomp(storeUrl, "/"); //$NON-NLS-1$
	}
}
