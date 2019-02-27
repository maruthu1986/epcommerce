/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.pricelistassignments.actions;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.core.CmSingletonUtil;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.event.EventType;
import com.elasticpath.cmclient.core.event.UIEvent;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareAction;
import com.elasticpath.cmclient.pricelistassignments.controller.PriceListAssignmentsSearchController;
import com.elasticpath.cmclient.pricelistassignments.model.PriceListAssigmentsSearchTabModel;
import com.elasticpath.cmclient.pricelistassignments.views.PriceListAssigmentsSearchView;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerMessages;
import com.elasticpath.common.dto.pricing.PriceListAssignmentsDTO;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.pricing.PriceListAssignment;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.changeset.ChangeSetMemberAction;
import com.elasticpath.service.pricing.PriceListAssignmentService;
import com.elasticpath.service.store.StoreService;

/**
 * 
 * Delete price list assignment.
 *
 */
public class DeletePriceListAssigment extends AbstractPolicyAwareAction {

	private static final Logger LOG = Logger.getLogger(DeletePriceListAssigment.class);
	
	private final PriceListAssigmentsSearchView view;

	private final PriceListAssignmentService priceListAssignmentService = ServiceLocator.getService(ContextIdNames.PRICE_LIST_ASSIGNMENT_SERVICE);
	private final StoreService storeService = ServiceLocator.getService(ContextIdNames.STORE_SERVICE);
	private final ChangeSetHelper changeSetHelper;
	
	/**
	 * The constructor.
	 * 
	 * @param view the results from search list view.
	 * @param text the tool tip text.
	 * @param imageDescriptor the image is shown at the title.
	 */	
	public DeletePriceListAssigment(final PriceListAssigmentsSearchView view,
			final String text, final ImageDescriptor imageDescriptor) {
		super(text, imageDescriptor);
		this.view = view;
		this.changeSetHelper = getChangeSetHelper();
	}

	@Override
	public void run() {
		
		PriceListAssignmentsDTO dto = view.getSelectedItem();
		
		Shell shell = view.getSite().getShell();
		
		String storeNames = getStoresNamesByCatalog(dto.getCatalogGuid());
		
		String msgBoxText = getMessageBoxText(storeNames, dto);
		
		final boolean confirmed = MessageDialog.openConfirm(shell,
				PriceListManagerMessages.get().ConfirmDeletePriceListAssignmentMsgBoxTitle,
				msgBoxText
				);
		
		if (confirmed) {
			PriceListAssignment assignment = priceListAssignmentService.findByGuid(dto.getGuid());

			changeSetHelper.addObjectToChangeSet(assignment, ChangeSetMemberAction.DELETE);
			priceListAssignmentService.delete(assignment);

			LOG.debug("Delete price list assignment operation completed. Name/guid " //$NON-NLS-1$ 
					+ dto.getName()
					+ '/'
					+ dto.getGuid()
					);
			this.fireEvent(EventType.DELETE);
		}
	}
	
	private String getMessageBoxText(final String storeNames, final PriceListAssignmentsDTO dto) {
		if (StringUtils.isBlank(storeNames)) {
			return
				NLS.bind(PriceListManagerMessages.get().ConfirmDeletePriceListAssignmentMsgBoxTextShort,
				new Object[] {
						dto.getName(),
						dto.getCatalogName()
					});
		}
		return
			NLS.bind(PriceListManagerMessages.get().ConfirmDeletePriceListAssignmentMsgBoxText,
			new Object[] {
				dto.getName(),
				dto.getCatalogName(),
				storeNames
			});
	}

	private String getStoresNamesByCatalog(final String catalogCode) {
		StringBuilder storeNames = new StringBuilder(); 
		Collection<Store> stores = storeService.findStoresWithCatalogCode(catalogCode);
		for (Store store : stores) {
			storeNames.append(store.getName());
			storeNames.append('\n');
		}
		return storeNames.toString();
	}

	/**
	 * fire event for any success operation.
	 * @param eventType event type for fire
	 */
	protected void fireEvent(final EventType eventType) {
		UIEvent<PriceListAssigmentsSearchTabModel> searchEvent =
				new UIEvent<>(new PriceListAssigmentsSearchTabModel(), eventType, false);
		CmSingletonUtil.getSessionInstance(PriceListAssignmentsSearchController.class).onEvent(searchEvent);
	}

	@Override
	public String getTargetIdentifier() {
		return "deletePriceListAssignmentAction"; //$NON-NLS-1$
	}

	@Override
	protected Object getDependentObject() {
		if (view == null) {
			return null;
		}
		if (view.getSelectedItem() != null) {
			return priceListAssignmentService.findByGuid(view.getSelectedItem().getGuid());
		}
		return null;
	}

}
