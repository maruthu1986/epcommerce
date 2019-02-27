/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.helpers.store;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.osgi.util.NLS;

import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.domain.store.StoreState;

/**
 * Determines whether a Store State may be changed to "Open" or "Restricted access"
 * and contains the list of causes why State changing is currently unavailable.
 */
public class StoreStateValidator {
	// Contains causes of impossibility to change Store State
	private final List<String> causes = new ArrayList<String>();

	private StoreState storeState = StoreState.OPEN;

	private StoreEditorModel model;

	/**
	 * Checks whether Store State can be promoted to a given one.
	 *
	 * @param model store editor model containing the store to verify
	 * @param storeState isn't used currently because rules for "Open" and "Restricted access" are the same
	 * @return accessibility to change the state of store
	 */
	public boolean isStateAccessible(final StoreEditorModel model, final StoreState storeState) {
		this.storeState = storeState;
		this.model = model;
		verifyCatalog();
		verifyDefaultLocale();
		verifyDefaultCurrency();
		verifyUrl();
		verifyWarehouse();
		verifyContentEncoding();
		verifySenderEmail();
		verifySenderName();
		verifyAdminEmail();
		verifyStoreTheme();
		return causes.isEmpty();
	}


	/**
	 * Gets the message indicating impossibility to change Store State.
	 *
	 * @return error message containing all causes
	 */
	public String getErrorMessage() {
		final StringBuilder errorMessage = new StringBuilder(
			NLS.bind(CoreMessages.get().ValidationError_WarningMessage,
			model.getName(), CoreMessages.get().getMessage(storeState.getNameMessageKey())));
		for (String cause : causes) {
			errorMessage.append("\n - ").append(cause); //$NON-NLS-1$
		}
		return errorMessage.toString();
	}

	private void verifyAdminEmail() {
		if (StringUtils.isEmpty(model.getStoreAdminEmailAddress())) {
			causes.add(CoreMessages.get().ValidationError_MissingAdminEmail);
		}
	}

	private void verifySenderName() {
		if (StringUtils.isEmpty(model.getEmailSenderName())) {
			causes.add(CoreMessages.get().ValidationError_MissingEmailSenderName);
		}
	}

	private void verifySenderEmail() {
		if (StringUtils.isEmpty(model.getEmailSenderAddress())) {
			causes.add(CoreMessages.get().ValidationError_MissingEmailSenderAddress);
		}
	}

	private void verifyContentEncoding() {
		if (StringUtils.isEmpty(model.getContentEncoding())) {
			causes.add(CoreMessages.get().ValidationError_MissingContentEncoding);
		}
	}

	private void verifyWarehouse() {
		if (model.getWarehouse() == null) {
			causes.add(CoreMessages.get().ValidationError_NoWarehouseIsSelected);
		}
	}

	private void verifyUrl() {
		if (StringUtils.isEmpty(model.getUrl())) {
			causes.add(CoreMessages.get().ValidationError_MissingStoreUrl);
		}
	}

	private void verifyDefaultCurrency() {
		if (model.getDefaultCurrency() == null) {
			causes.add(CoreMessages.get().ValidationError_MissingDefaultCurrency);
		}
	}

	private void verifyDefaultLocale() {
		if (model.getDefaultLocale() == null) {
			causes.add(CoreMessages.get().ValidationError_MissingDefaultLocale);
		}
	}

	private void verifyCatalog() {
		if (model.getCatalog() == null) {
			causes.add(CoreMessages.get().ValidationError_NoCatalogIsSelected);
		}
	}

	private void verifyStoreTheme() {
		if (StringUtils.EMPTY.equals(model.getStoreThemeSetting())) {
			causes.add(CoreMessages.get().ValidationError_NoThemeIsSelected);
		}		
	}
}
