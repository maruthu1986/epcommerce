/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.store.targetedselling.delivery.wizard.duallistboxes;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.comparator.ContentSpaceComparator;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareDualListBox;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.store.targetedselling.delivery.wizard.tabletooltip.ContentSpaceTableTooltip;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.contentspace.ContentSpace;
import com.elasticpath.service.contentspace.ContentSpaceService;

/**
 * The country selection dual listbox.
 */
public class ContentSpaceSelectionDualListBox extends
		AbstractPolicyAwareDualListBox<List<ContentSpace>> {

	private static final int TOOLTIP_SHIFT = 10;
	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param container
	 * 			policy container           
	 * @param availableContentspaces
	 *            the model object (the Collection)
	 * @param availableTitle
	 *            the title for the Available listbox
	 * @param assignedTitle
	 *            the title for the Assigned listbox
	 * @param wizard 
	 * 			main wizard	
	 */
	public ContentSpaceSelectionDualListBox(
			final IPolicyTargetLayoutComposite parent,
			final PolicyActionContainer container,
			final List<ContentSpace> availableContentspaces,
			final String availableTitle, final String assignedTitle, final IWizard wizard) {
		super(parent, parent.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true), 
				container, availableContentspaces, availableTitle, assignedTitle, ALL_BUTTONS | MULTI_SELECTION);
		createControls();
		new ContentSpaceTableTooltip().addTableTooltip(getAvailableTableViewer().getTable(), wizard.getContainer(),
				TOOLTIP_SHIFT, TOOLTIP_SHIFT);
		new ContentSpaceTableTooltip().addTableTooltip(getAssignedTableViewer().getTable(), wizard.getContainer(),
				TOOLTIP_SHIFT, TOOLTIP_SHIFT);
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
		final List<ContentSpace> list = this.getModel();
		for (final Iterator<ContentSpace> it = selection.iterator(); it
				.hasNext();) {
			list.add(it.next());
		}

		return true;
	}

	@Override
	protected boolean removeFromModel(final IStructuredSelection selection) {
		if (selection == null || selection.isEmpty()) {
			return false;
		}
		final List<ContentSpace> list = this.getModel();
		for (final Iterator<ContentSpace> it = selection.iterator(); it
				.hasNext();) {
			list.remove(it.next());
		}
		return true;
	}

	@Override
	public Collection<ContentSpace> getAvailable() {
		return getAvailableContentspaces();
	}

	private List<ContentSpace> getAvailableContentspaces() {
		ContentSpaceService contentspaceService = ServiceLocator.getService(ContextIdNames.CONTENTSPACE_SERVICE);
		List<ContentSpace> contentSpaces = contentspaceService.findAll();
		Collections.sort(contentSpaces, new ContentSpaceComparator());
		return contentSpaces;
	}

	@Override
	public ViewerFilter getAvailableFilter() {
		return new AvailableContentspacesFilter();
	}

	@Override
	protected ILabelProvider getLabelProvider() {
		return new ContentSpacesSelectionLabelProvider();
	}

	/**
	 * Filters the AvailableListView so that it doesn't display any objects that
	 * are in the AssignedListView. Subclasses should override the Select method
	 * if they want to do any filtering.
	 */
	protected class AvailableContentspacesFilter extends
			ViewerFilter {
		@Override
		public boolean select(final Viewer viewer, final Object parentElement,
				final Object element) {
			boolean sel = true;
			for (ContentSpace assigned : ContentSpaceSelectionDualListBox.this
					.getAssigned()) {
				if (assigned.equals(element)) {
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
	class ContentSpacesSelectionLabelProvider extends LabelProvider
			implements ILabelProvider {
		@Override
		public Image getImage(final Object element) {
			return null;
		}

		@Override
		public String getText(final Object element) {
			String text = null;
			if (element instanceof ContentSpace) {
				text = ((ContentSpace) element).getTargetId();
			}

			return text;
		}

		@Override
		public boolean isLabelProperty(final Object element,
				final String property) {
			return false;
		}
	}

	@Override
	public Collection<ContentSpace> getAssigned() {
		List<ContentSpace> contentSpaces = getModel();
		Collections.sort(contentSpaces, new ContentSpaceComparator());
		return contentSpaces; 
	}

}