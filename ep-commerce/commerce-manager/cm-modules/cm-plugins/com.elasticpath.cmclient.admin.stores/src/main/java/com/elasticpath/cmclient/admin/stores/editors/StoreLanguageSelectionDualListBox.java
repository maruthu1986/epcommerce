/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.stores.editors;

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

import com.elasticpath.cmclient.admin.stores.AdminStoresMessages;
import com.elasticpath.cmclient.core.helpers.store.StoreEditorModel;
import com.elasticpath.cmclient.core.ui.framework.AbstractEpDualListBoxControl;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;

/**
 * The Language Selection dual listbox for Store.
 */
public class StoreLanguageSelectionDualListBox extends AbstractEpDualListBoxControl<StoreEditorModel> {
	
	private List<Locale> assignedSupportedLocales;
		
	private RemoveListener<Locale> removeListener;

	/**
	 * Constructor.
	 * 
	 * @param parentComposite the Composite that contains this thing
	 * @param model the model
	 * @param availableTitle the Available Title
	 * @param assignedTitle the Assigned Title
	 * @param editableState the editable state of the listbox
	 */
	public StoreLanguageSelectionDualListBox(final IEpLayoutComposite parentComposite,
			final StoreEditorModel model, final String availableTitle, final String assignedTitle, final EpState editableState) {
		super(parentComposite, model, availableTitle, assignedTitle, ALL_BUTTONS | MULTI_SELECTION, 
				parentComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true), editableState);

	}

	@Override
	protected void customizeControls() {
		setSorter(new ViewerSorter() {
			@Override
			public int compare(final Viewer viewer, final Object obj1, final Object obj2) {
				final Locale locale1 = (Locale) obj1;
				final Locale locale2 = (Locale) obj2;
				return locale1.getDisplayName().compareTo(locale2.getDisplayName());
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
	public StoreLanguageSelectionDualListBox(final IEpLayoutComposite parentComposite, final StoreEditorModel model, final EpState editableState) {
		this(parentComposite, model, AdminStoresMessages.get().StoreSelectionLanguageAvailable,
				AdminStoresMessages.get().StoreSelectionLanguageAssigned, editableState);
	}

	@Override
	protected boolean assignToModel(final IStructuredSelection selection) {
		if (selection == null || selection.isEmpty()) {
			return false;
		}
		
		for (final Iterator<Locale> it = selection.iterator(); it.hasNext();) {
			final Locale currLocale = it.next();
			assignedSupportedLocales.add(currLocale);
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

		for (final Iterator<Locale> it = selection.iterator(); it.hasNext();) {
			assignedSupportedLocales.remove(it.next());
		}
		
		return true;
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
		final Locale[] availableLocales = Locale.getAvailableLocales();
		Collections.addAll(locales, availableLocales);
		return locales;
	}

	@Override
	public ViewerFilter getAvailableFilter() {
		return new ViewerFilter() {
			@Override
			public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
				for (final Locale locale : getAssigned()) {
					if (locale.equals(element)) {
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
				if (element instanceof Locale) {
					return ((Locale) element).getDisplayName();
				}
				return null;
			}

			@Override
			public boolean isLabelProperty(final Object element, final String property) {
				return false;
			}
		};
	}
		
	private RemoveListener<Locale> getRemoveListener() {
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
	public void registerRemoveListener(final RemoveListener<Locale> removeListener) {
		this.removeListener = removeListener;
	}
	
	/**
	 * Represents default remove listener.
	 */
	private class DefaultRemoveListener implements RemoveListener<Locale> {

		@Override
		public boolean tryToRemove(final List<Locale> list) {
			return true;
		}	
	}
}
