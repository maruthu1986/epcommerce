/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.editors.catalog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.core.ui.framework.AbstractEpDualListBoxControl;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;

/**
 * The currency selection dual listbox.
 */
@SuppressWarnings({"PMD.DontUseFinalModifierForInterfaceParameters"})
public final class CurrencySelectionDualListBox extends AbstractEpDualListBoxControl<List<Currency>> {

	/**
	 * Implementers can control removing of elements from the assigned list.
	 */
	public interface RemoveListener {

		/**
		 * Call back.
		 *
		 * @param list list of currencies to be removed.
		 * @return if the specified currency can be removed.
		 */		
		boolean tryToRemove(final List<Currency> list);

	}

	private static final Logger LOG = Logger.getLogger(CurrencySelectionDualListBox.class);

	/**
	 * Constructor.
	 * 
	 * @param parent the parent composite
	 * @param availableCurrencies the model object (the Collection)
	 * @param availableTitle the title for the Available listbox
	 * @param assignedTitle the title for the Assigned listbox
	 * @param showRemoveButtons whether to hideRemoveButtons
	 * @return instance of CurrencySelectionDualListBox
	 */
	public static CurrencySelectionDualListBox getInstance(final IEpLayoutComposite parent, final List<Currency> availableCurrencies,
			final String availableTitle, final String assignedTitle, final boolean showRemoveButtons) {
		if (showRemoveButtons) {
			return new CurrencySelectionDualListBox(parent, availableCurrencies, availableTitle, assignedTitle, ALL_BUTTONS | MULTI_SELECTION);
		}
		return new CurrencySelectionDualListBox(parent, availableCurrencies, availableTitle, assignedTitle, MULTI_SELECTION
				| DISABLE_REMOVAL_BUTTONS);
	}

	private RemoveListener removeListener;

	/**
	 * Constructor.
	 * 
	 * @param parent the parent composite
	 * @param availableCurrencies the model object (the Collection)
	 * @param availableTitle the title for the Available listbox
	 * @param assignedTitle the title for the Assigned listbox
	 * @param style the style to apply
	 */
	private CurrencySelectionDualListBox(final IEpLayoutComposite parent, final List<Currency> availableCurrencies, final String availableTitle,
			final String assignedTitle, final int style) {
		super(parent, availableCurrencies, availableTitle, assignedTitle, style, parent.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL,
				true, true), EpState.EDITABLE);
	}

	/**
	 * Validate the dual list box selection.
	 * 
	 * @return true if the selection valid
	 */
	public boolean validate() {
		return !getModel().isEmpty();
	}

	@Override
	protected boolean assignToModel(final IStructuredSelection selection) {
		boolean success = false;
		final List<Currency> list = this.getModel();
		for (final Iterator<Currency> it = selection.iterator(); it.hasNext();) {
			success = list.add(it.next());
		}
		
		return success;
	}

	@Override
	protected boolean removeFromModel(final IStructuredSelection selection) {
		boolean success = false;

		if (removeListener != null && !removeListener.tryToRemove(selection.toList())) {
			return success;
		}

		final List<Currency> list = this.getModel();
		for (final Iterator<Currency> it = selection.iterator(); it.hasNext();) {
			success = list.remove(it.next());
		}
		
		return success;
	}

	@Override
	public Collection<Currency> getAssigned() {
		final List<Currency> assignedCurrencies = getModel();

		// Need to sort the array
		Collections.sort(assignedCurrencies, Comparator.comparing(Currency::toString));

		return assignedCurrencies;
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

		// Need to sort the array
		Collections.sort(availableCurrencies, Comparator.comparing(Currency::toString));

		return availableCurrencies;
	}

	@Override
	public ViewerFilter getAvailableFilter() {
		return new AvailableCountryFilter();
	}

	@Override
	protected ILabelProvider getLabelProvider() {
		return new CurrencySelectionLabelProvider();
	}

	/**
	 * Filters the AvailableListView so that it doesn't display any objects that are in the AssignedListView. Subclasses should override the Select
	 * method if they want to do any filtering.
	 */
	protected class AvailableCountryFilter extends ViewerFilter {
		@Override
		public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
			boolean select = true;
			for (final Currency currency : CurrencySelectionDualListBox.this.getAssigned()) {
				if (currency.equals(element)) {
					select = false;
					break;
				}
			}
			return select;
		}
	}

	/**
	 * Label provider for CountryHelper listviewers.
	 */
	class CurrencySelectionLabelProvider extends LabelProvider implements ILabelProvider {
		@Override
		public Image getImage(final Object element) {
			return null;
		}

		@Override
		public String getText(final Object element) {
			String text = null;
			if (element instanceof Currency) {
				text = element.toString();
			}

			return text;
		}

		@Override
		public boolean isLabelProperty(final Object element, final String property) {
			return false;
		}
	}

	/**
	 * Register items removal controller.
	 *
	 * @param removeListener removal controller. 
	 */
	public void registerRemoveListener(final RemoveListener removeListener) {
		this.removeListener = removeListener;
	}
}