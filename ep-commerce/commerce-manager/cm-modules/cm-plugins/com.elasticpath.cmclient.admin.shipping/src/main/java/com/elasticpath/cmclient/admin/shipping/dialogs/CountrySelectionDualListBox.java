/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.admin.shipping.dialogs;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.core.ui.framework.AbstractEpDualListBoxControl;
import com.elasticpath.cmclient.core.ui.framework.Country;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;

/**
 * The country selection dual listbox.
 */
public class CountrySelectionDualListBox extends AbstractEpDualListBoxControl<List<Country>> {

	private final List<Country> allCountries;

	/**
	 * Constructor.
	 * 
	 * @param parent the parent composite
	 * @param selectedCountries the model object (the Collection)
	 * @param allCountries all possible countries
	 * @param availableTitle the title for the Available listbox
	 * @param assignedTitle the title for the Assigned listbox
	 */
	public CountrySelectionDualListBox(final IEpLayoutComposite parent, final List<Country> selectedCountries,
			final List<Country> allCountries, final String availableTitle, final String assignedTitle) {
		super(parent, selectedCountries, availableTitle, assignedTitle, ALL_BUTTONS | MULTI_SELECTION, parent.createLayoutData(IEpLayoutData.FILL,
				IEpLayoutData.FILL, true, true), EpState.EDITABLE);
		this.allCountries = allCountries;
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
		if (selection == null || selection.isEmpty()) {
			return false;
		}
		
		final List<Country> list = this.getModel();
		for (final Iterator<Country> it = selection.iterator(); it.hasNext();) {
			list.add(it.next());
		}
		
		return true;
	}

	@Override
	protected boolean removeFromModel(final IStructuredSelection selection) {
		if (selection == null || selection.isEmpty()) {
			return false;
		}
		
		final List<Country> list = this.getModel();
		for (final Iterator<Country> it = selection.iterator(); it.hasNext();) {
			list.remove(it.next());
		}
		return true;
	}

	@Override
	public Collection<Country> getAssigned() {
		return getModel();
	}

	@Override
	public Collection<Country> getAvailable() {
		return allCountries;
	}

	@Override
	public ViewerFilter getAvailableFilter() {
		return new AvailableCountryFilter();
	}

	@Override
	protected ILabelProvider getLabelProvider() {
		return new CountrySelectionLabelProvider();
	}

	/**
	 * Filters the AvailableListView so that it doesn't display any objects that are in the AssignedListView. Subclasses should override the Select
	 * method if they want to do any filtering.
	 */
	protected class AvailableCountryFilter extends ViewerFilter {
		@Override
		public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
			boolean sel = true;
			for (final Country country : CountrySelectionDualListBox.this.getAssigned()) {
				if (country.equals(element)) {
					sel = false;

					break;
				}
			}

			return sel;
		}
	}

	/**
	 * Label provider for CountryHelper listviewers.
	 */
	class CountrySelectionLabelProvider extends LabelProvider implements ILabelProvider {
		@Override
		public Image getImage(final Object element) {
			return null;
		}

		@Override
		public String getText(final Object element) {
			String text = null;
			if (element instanceof Country) {
				text = element.toString();
			}

			return text;
		}

		@Override
		public boolean isLabelProperty(final Object element, final String property) {
			return false;
		}
	}
}
