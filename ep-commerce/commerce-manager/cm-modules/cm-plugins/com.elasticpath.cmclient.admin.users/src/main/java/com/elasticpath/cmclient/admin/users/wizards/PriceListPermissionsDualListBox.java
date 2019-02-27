/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.users.wizards;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.ui.framework.AbstractEpDualListBoxControl;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.common.pricing.service.PriceListService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cmuser.CmUser;

/**
 * The Price  List Permissions dual listbox.
 */
public class PriceListPermissionsDualListBox extends AbstractEpDualListBoxControl<CmUser> {
	
	private PriceListService priceListService;
	
	private Collection <PriceListDescriptorDTO> priceListDescriptors;
	
	private Map<String, PriceListDescriptorDTO> priceListDescriptorMap;

	/**
	 * Constructor.
	 * 
	 * @param parent the parent composite
	 * @param cmUser the model object (the CmUser)
	 * @param availableTitle the title for the Available listbox
	 * @param assignedTitle the title for the Assigned listbox
	 * @param data EP layout data
	 */
	public PriceListPermissionsDualListBox(final IEpLayoutComposite parent, final CmUser cmUser, final String availableTitle,
			final String assignedTitle, final IEpLayoutData data) {
		super(parent, cmUser, availableTitle, assignedTitle, ALL_BUTTONS | MULTI_SELECTION, data, EpState.EDITABLE);
	}

	@Override
	protected boolean assignToModel(final IStructuredSelection selection) {
		if (selection == null || selection.isEmpty()) {
			return false;
		}
		
		CmUser cmUser = getModel();
		for (Iterator< ? > it = selection.iterator(); it.hasNext();) {
			cmUser.addPriceList((String) it.next());
		}
		return true;
	}

	@Override
	protected boolean removeFromModel(final IStructuredSelection selection) {
		if (selection == null || selection.isEmpty()) {
			return false;
		}
		
		CmUser cmUser = getModel();
		for (Iterator< ? > it = selection.iterator(); it.hasNext();) {
			cmUser.removePriceList((String) it.next());
		}
		return true;
	}

	@Override
	public Collection<String> getAssigned() {
		return getModel().getPriceLists();
	}

	@Override
	public Collection<String> getAvailable() {
		List<String> guids = new ArrayList<>();
		for (PriceListDescriptorDTO dto : getPriceListDecriptorsDTO()) {
			guids.add(dto.getGuid());
		}
		return guids;
	}

	@Override
	public ViewerFilter getAvailableFilter() {
		return new PriceListPermissionsAvailableViewerFilter();
	}

	/**
	 * Filters the AvailableListView so that it doesn't display any objects that are in the AssignedListView.
	 */
	protected class PriceListPermissionsAvailableViewerFilter extends ViewerFilter {

		@Override
		public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
			final Collection<String> priceList = getAssigned();
			if (priceList == null || priceList.isEmpty()) {
				return true;
			}
			String priceListGuid = (String) element;
			for (String guid : priceList) {
				if (guid.equals(priceListGuid)) {
					return false;
				}
			}
			return true;
		}
	}

	/**
	 * Label provider for PriceListPermissions list viewers.
	 */
	class PriceListPermissionsLabelProvider extends LabelProvider {

		@Override
		public String getText(final Object element) {
			return getPriceListName((String) element);
		}

		@Override
		public boolean isLabelProperty(final Object element, final String property) {
			return false;
		}

	}

	@Override
	protected ILabelProvider getLabelProvider() {
		return new PriceListPermissionsLabelProvider();
	}
	
	/**
	 * @return the price list service
	 */
	private PriceListService getPriceListService() {
		if (this.priceListService == null) {
			this.priceListService = (PriceListService) ServiceLocator.getService(ContextIdNames.PRICE_LIST_CLIENT_SERVICE);
		}
		
		return this.priceListService;
	}

	
	private Collection <PriceListDescriptorDTO> getPriceListDecriptorsDTO() {
		if (priceListDescriptors == null) {
			priceListDescriptors = getPriceListService().getPriceListDescriptors(false); 
		}
		return priceListDescriptors;		
	}

	
	private String getPriceListName(final String priceListGuid) {
		
		if (priceListDescriptorMap == null) {
			priceListDescriptorMap = new HashMap<>();
			for (PriceListDescriptorDTO dto : getPriceListDecriptorsDTO()) {
				priceListDescriptorMap.put(dto.getGuid(), dto);
			}
		}
		
		PriceListDescriptorDTO dto = priceListDescriptorMap.get(priceListGuid);
		if (dto != null) {
			return dto.getName();			
		}		
		return priceListGuid;
		
	}
}
