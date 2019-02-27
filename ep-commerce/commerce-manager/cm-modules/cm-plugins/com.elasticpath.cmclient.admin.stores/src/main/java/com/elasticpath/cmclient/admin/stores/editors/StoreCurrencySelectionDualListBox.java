/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.stores.editors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.admin.stores.AdminStoresMessages;
import com.elasticpath.cmclient.core.helpers.store.StoreEditorModel;
import com.elasticpath.cmclient.core.ui.framework.AbstractEpDualListBoxControl;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;

/**
 * The Language Selection dual listbox for Store.
 */
public class StoreCurrencySelectionDualListBox extends AbstractEpDualListBoxControl<StoreEditorModel> {

	private static final Logger LOG = Logger.getLogger(StoreCurrencySelectionDualListBox.class);

	private RemoveListener<Currency> removeListener;

	private List<Currency> assignedSupportedCurrencies;

	/**
	 * Constructor.
	 * 
	 * @param parentComposite the Composite that contains this thing
	 * @param model the model
	 * @param availableTitle the Available Title
	 * @param assignedTitle the Assigned Title
	 * @param editableState the editable state of the listbox
	 */
	public StoreCurrencySelectionDualListBox(final IEpLayoutComposite parentComposite,
			final StoreEditorModel model, final String availableTitle, final String assignedTitle, final EpState editableState) {
		super(parentComposite, model, availableTitle, assignedTitle, ALL_BUTTONS | MULTI_SELECTION, 
				parentComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true), editableState);
	}
	
	@Override
	protected void customizeControls() {
		setSorter(new ViewerSorter() {
			@Override
			public int compare(final Viewer viewer, final Object obj1, final Object obj2) {
				final Currency currency1 = (Currency) obj1;
				final Currency currency2 = (Currency) obj2;
				return currency1.getCurrencyCode().compareTo(currency2.getCurrencyCode());
			}
		});
	}

	/**
	 * Another Convenient Constructor.
	 * 
	 * @param parentComposite the Composite that contains this thing.
	 * @param model the model
	 * @param editableState the editable state of the listbox
	 */
	public StoreCurrencySelectionDualListBox(final IEpLayoutComposite parentComposite, final StoreEditorModel model, final EpState editableState) {
		this(parentComposite, model, AdminStoresMessages.get().StoreSelectionCurrencyAvailable,
				AdminStoresMessages.get().StoreSelectionCurrencyAssigned, editableState);
	}

	@Override
	protected boolean assignToModel(final IStructuredSelection selection) {
		if (selection == null || selection.isEmpty()) {
			return false;
		}
		
		for (final Iterator<Currency> it = selection.iterator(); it.hasNext();) {
			final Currency currLocale = it.next();
			assignedSupportedCurrencies.add(currLocale);
		}
		
		return true;
	}

	@Override
	protected boolean removeFromModel(final IStructuredSelection selection) {
		if (selection == null || selection.isEmpty()) {
			return false;
		}
		
		if (!getRemoveListener().tryToRemove(selection.toList())) {
			return false;
		}

		for (final Iterator<Currency> it = selection.iterator(); it.hasNext();) {
			assignedSupportedCurrencies.remove(it.next());
		}
		
		return true;
	}

	@Override
	public Collection<Currency> getAssigned() {
		if (assignedSupportedCurrencies == null) {
			assignedSupportedCurrencies = getModel().getSupportedCurrencies();
		}
		return assignedSupportedCurrencies;
	}

	@Override
	public Collection<Currency> getAvailable() {
		final List<Currency> availableCurrencies = new ArrayList<>();

		for (Locale currLocale : Locale.getAvailableLocales()) {
			try {
				if (!availableCurrencies.contains(Currency.getInstance(currLocale))) {
					availableCurrencies.add(Currency.getInstance(currLocale));
				}
			} catch (IllegalArgumentException illegalArgException) {
				// Country of the given locale is not a supported ISO 3166 country code; do nothing.
				LOG.debug("Country of the given locale is not a supported ISO 3166 country code"); //$NON-NLS-1$
			}
		}
		return availableCurrencies;
	}

	@Override
	public ViewerFilter getAvailableFilter() {
		return new ViewerFilter() {
			@Override
			public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
				for (final Currency currency : getAssigned()) {
					if (currency.equals(element)) {
						return false;
					}
				}
				return true;
			}
		};
	}

	@Override
	protected ILabelProvider getLabelProvider() {
		return new LabelProvider() {
			@Override
			public Image getImage(final Object element) {
				return null;
			}

			@Override
			public String getText(final Object element) {
				if (element instanceof Currency) {
					return ((Currency) element).getCurrencyCode();
				}
				return null;
			}

			@Override
			public boolean isLabelProperty(final Object element, final String property) {
				return false;
			}
		};
	}

	private RemoveListener<Currency> getRemoveListener() {
		if (removeListener == null) {
			removeListener = new DefaultRemoveListener();
		}
		return removeListener;
	}

	/**
	 * Register items removal controller.
	 * 
	 * @param removeListener removal controller.
	 */
	public void registerRemoveListener(final RemoveListener<Currency> removeListener) {
		this.removeListener = removeListener;
	}

	/**
	 * Represents default remove listener.
	 */
	private class DefaultRemoveListener implements RemoveListener<Currency> {

		@Override
		public boolean tryToRemove(final List<Currency> list) {
			return true;
		}
	}
}
