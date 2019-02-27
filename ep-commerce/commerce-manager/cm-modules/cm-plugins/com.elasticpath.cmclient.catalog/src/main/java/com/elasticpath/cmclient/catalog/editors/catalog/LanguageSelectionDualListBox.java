/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.editors.catalog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.core.EpUiException;
import com.elasticpath.cmclient.core.ui.framework.AbstractEpDualListBoxControl;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.DefaultValueRemovalForbiddenException;

/**
 * The language selection dual listbox.
 */
public final class LanguageSelectionDualListBox extends AbstractEpDualListBoxControl<Catalog> {

	/**
	 * Implementers can control removing of elements from the assigned list.
	 */
	public interface RemoveListener {

		/**
		 * Call back.
		 *
		 * @param list list of locales to be removed.
		 * @return if the specified locales can be removed.
		 */
		boolean tryToRemove(List<Locale> list);

	}

	private List<Locale> assignedSupportedLocales;

	private RemoveListener removeListener;

	/**
	 * Get a instance of the Language box.
	 * 
	 * @param parent the parent composite
	 * @param catalog the model catalog instance
	 * @param availableTitle the title for the Available listbox
	 * @param assignedTitle the title for the Assigned listbox
	 * @param showRemovalButtons to show removal buttons
	 * @return instance of LanguageSelectionDualListBox with the expected buttons enabled
	 */
	public static LanguageSelectionDualListBox getInstance(final IEpLayoutComposite parent, final Catalog catalog, final String availableTitle,
			final String assignedTitle, final boolean showRemovalButtons) {
		if (showRemovalButtons) {
			return new LanguageSelectionDualListBox(parent, catalog, availableTitle, assignedTitle, ALL_BUTTONS | MULTI_SELECTION);
		}
		return new LanguageSelectionDualListBox(parent, catalog, availableTitle, assignedTitle, MULTI_SELECTION | DISABLE_REMOVAL_BUTTONS);
	}

	/**
	 * Constructor. If catalog is being created, uidPk=0, then we allow removal+adding of language/currency If catalog is in edit mode, uidPk!=0, the
	 * we only allow adding ">" of language/currency
	 * 
	 * @param parent the parent composite
	 * @param catalog the model catalog instance
	 * @param availableTitle the title for the Available listbox
	 * @param assignedTitle the title for the Assigned listbox
	 * @param style the style of the list box
	 */
	private LanguageSelectionDualListBox(final IEpLayoutComposite parent, final Catalog catalog, final String availableTitle,
			final String assignedTitle, final int style) {
		super(parent, catalog, availableTitle, assignedTitle, style, parent.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true),
				EpState.EDITABLE);
	}
	
	@Override
	protected void customizeControls() {
		setSorter(new LocaleViewerSorter());
	}

	/**
	 * Validate the dual list box selection.
	 * 
	 * @return true if the selection valid
	 */
	public boolean validate() {
		return !getModel().getSupportedLocales().isEmpty();
	}

	@Override
	protected boolean assignToModel(final IStructuredSelection selection) {
		boolean success = false;
		
		for (final Iterator<Locale> it = selection.iterator(); it.hasNext();) {
			final Locale currLocale = it.next();
			assignedSupportedLocales.add(currLocale);
			success = true;
		}
		try {
			getModel().setSupportedLocales(assignedSupportedLocales);
		} catch (DefaultValueRemovalForbiddenException ex) {
			throw new EpUiException("Supported Locales does not contain default Locale", ex); //$NON-NLS-1$
		}
		
		return success;
	}

	@Override
	protected boolean removeFromModel(final IStructuredSelection selection) {
		boolean success = false;
		
		if (removeListener != null && !removeListener.tryToRemove(selection.toList())) {
			return success;
		}

		for (final Iterator<Locale> it = selection.iterator(); it.hasNext();) {
			assignedSupportedLocales.remove(it.next());
			success = true;
		}
		try {
			getModel().setSupportedLocales(assignedSupportedLocales);
		} catch (DefaultValueRemovalForbiddenException ex) {
			throw new EpUiException("Supported Locales does not contain default Locale", ex); //$NON-NLS-1$
		}
		
		return success;
	}

	@Override
	public Collection<Locale> getAssigned() {
		if (assignedSupportedLocales == null) {
			assignedSupportedLocales = new ArrayList<>(getModel().getSupportedLocales());
		}
		return assignedSupportedLocales;
	}

	@Override
	public Collection<Locale> getAvailable() {
		final List<Locale> locales = new ArrayList<>();
		// Available Locales are a list of all installed Locales
		final Locale[] availableLocales = Locale.getAvailableLocales();

		Collections.addAll(locales, availableLocales);
		locales.remove(Locale.ROOT);
		return locales;
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
			for (final Locale locale : getModel().getSupportedLocales()) {
				if (locale.equals(element)) {
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
			if (element instanceof Locale) {
				text = ((Locale) element).getDisplayName();
			}

			return text;
		}

		@Override
		public boolean isLabelProperty(final Object element, final String property) {
			return false;
		}
	}

	/**
	 * Sorter for viewing the locales in alphabetical order.
	 */
	public class LocaleViewerSorter extends ViewerSorter {

		@Override
		public int compare(final Viewer viewer, final Object obj1, final Object obj2) {
			Locale locale1 = (Locale) obj1;
			Locale locale2 = (Locale) obj2;
			return locale1.getDisplayName().compareTo(locale2.getDisplayName());
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