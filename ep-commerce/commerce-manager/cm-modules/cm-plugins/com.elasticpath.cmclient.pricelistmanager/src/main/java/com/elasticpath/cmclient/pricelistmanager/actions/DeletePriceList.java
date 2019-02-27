/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.pricelistmanager.actions;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.core.EpUiException;
import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.event.EventType;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.core.ui.util.EditorUtil;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareAction;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerMessages;
import com.elasticpath.cmclient.pricelistmanager.editors.PriceListEditor;
import com.elasticpath.cmclient.pricelistmanager.event.PriceListChangedEvent;
import com.elasticpath.cmclient.pricelistmanager.event.PricingEventService;
import com.elasticpath.cmclient.pricelistmanager.views.PriceListSearchResultsView;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.common.dto.pricing.PriceListAssignmentsDTO;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.common.pricing.service.BaseAmountFilter;
import com.elasticpath.common.pricing.service.PriceListAssignmentHelperService;
import com.elasticpath.common.pricing.service.PriceListService;
import com.elasticpath.common.pricing.service.impl.OfferInUseByPriceListException;
import com.elasticpath.common.pricing.service.impl.PriceListInUseByPLAException;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.changeset.ChangeSetObjectStatus;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.service.changeset.ChangeSetMemberAction;
import com.elasticpath.service.cmuser.CmUserService;

/**
 * Delete price list action.
 */
@SuppressWarnings({"PMD.PrematureDeclaration", "restriction"})
public class DeletePriceList extends AbstractPolicyAwareAction {

	private static final Logger LOG = Logger.getLogger(DeletePriceList.class.getName());

	private final PriceListSearchResultsView view;

	private final PriceListAssignmentHelperService priceListAssignmentHelperService = ServiceLocator.getService(ContextIdNames
		.PRICE_LIST_ASSIGNMENT_HELPER_SERVICE);
	private final PriceListService priceListService = ServiceLocator.getService(ContextIdNames.PRICE_LIST_CLIENT_SERVICE);
	private final BaseAmountFilter baseAmountFilter = ServiceLocator.getService(ContextIdNames.BASE_AMOUNT_FILTER);

	private final ChangeSetHelper changeSetHelper;

	/**
	 * The constructor.
	 * 
	 * @param view the results from search list view.
	 * @param text the tool tip text.
	 * @param imageDescriptor the image is shown at the title.
	 */
	public DeletePriceList(final PriceListSearchResultsView view, final String text, final ImageDescriptor imageDescriptor) {
		super(text, imageDescriptor);
		this.view = view;
		this.changeSetHelper = getChangeSetHelper();
	}

	@Override
	public void run() {
		final PriceListDescriptorDTO dto = view.getSelectedItem();

		boolean confirmed = confirmDelete(dto);

		if (confirmed) {

			CmUserService cmUserService = ServiceLocator.getService(ContextIdNames.CMUSER_SERVICE);
			cmUserService.removePriceListFromUsers(dto.getGuid());
			CmUser currentUser = LoginManager.getCmUser();
			currentUser.removePriceList(dto.getGuid());
			updateChangeSetStatusForBaseAmounts(dto);
			try {
				deletePriceList(dto);
			} catch (PriceListInUseByPLAException e) {
				String errorMessage = getErrorMessageForDenyDeletePriceListMsgBoxText(dto);

				MessageDialog.openInformation(view.getSite().getShell(), PriceListManagerMessages.get().DenyDeletePriceListMsgBoxTitle, errorMessage);
				return;
			} catch (OfferInUseByPriceListException e) {
				String errorMessage = getErrorMessageForDenyDeletePriceListOfferMsgBoxText(dto);

				MessageDialog.openInformation(view.getSite().getShell(), PriceListManagerMessages.get().DenyDeletePriceListMsgBoxTitle, errorMessage);
				return;
			}

			LOG.debug("Delete price list operation completed. Name/guid " //$NON-NLS-1$ 
					+ dto.getName() + '/' + dto.getGuid());

			PriceListChangedEvent plsEvent = new PriceListChangedEvent(dto, EventType.DELETE);
			PricingEventService.getInstance().fireChangedEvent(plsEvent);
		}
	}

	private String getErrorMessageForDenyDeletePriceListOfferMsgBoxText(final PriceListDescriptorDTO dto) {
		String message = PriceListManagerMessages.get().DenyDeletePriceListOfferMsgBoxText;
		return
			NLS.bind(message,
			new Object[]{dto.getName()});
	}

	// Price List <name> is currently used by the Site Optimization activity and
	// cannot be deleted. Please speak to the user who manages this activity in
	// your system.
	private String getErrorMessageForDenyDeletePriceListMsgBoxText(final PriceListDescriptorDTO dto) {
		String message = PriceListManagerMessages.get().DenyDeletePriceListMsgBoxText;
		List<PriceListAssignmentsDTO> assignments = priceListAssignmentHelperService.getPriceListAssignmentsDTOByPriceListGuid(dto.getGuid());
		return
			NLS.bind(message,
			new Object[]{dto.getName(), getPriceListAssignmentsNames(assignments)});
	}

	/**
	 * Confirm that the DTO is eligible for deletion and that the user intends it to be deleted. Pops confirmation and/or error dialogs.
	 *
	 * @param dto the DTO to confirm
	 * @return true if the DTO has not assignments and the user confirms it should be deleted.
	 */
	protected boolean confirmDelete(final PriceListDescriptorDTO dto) {
		Shell shell = view.getSite().getShell();

		// add get open editors part here
		if (checkForOpenEditorsAndDisplayWarning(dto)) {
			return false;
		}

		if (!checkForChangeSetsAndDisplayWarning(dto)) {
			return false;
		}

		String confirm =
			NLS.bind(PriceListManagerMessages.get().ConfirmDeletePriceListMsgBoxText,
			new Object[]{dto.getName()});

		return MessageDialog.openConfirm(shell, PriceListManagerMessages.get().ConfirmDeletePriceListMsgBoxTitle, confirm);
	}

	/**
	 * Verifies that all the base amounts are in the current change set.
	 *
	 * @param dto the price list descriptor
	 * @return true if none of the price list's base amounts are in another change set but the active
	 */
	private boolean checkForChangeSetsAndDisplayWarning(final PriceListDescriptorDTO dto) {
		if (!changeSetHelper.isChangeSetsEnabled()) {
			return true;
		}
		Collection<BaseAmountDTO> baseAmounts = getBaseAmounts(dto);
		for (BaseAmountDTO baseAmount : baseAmounts) {
			ChangeSetObjectStatus status = changeSetHelper.getChangeSetObjectStatus(baseAmount);
			if (status.isLocked() && !status.isMember(changeSetHelper.getActiveChangeSet().getGuid())) {
				MessageDialog.openWarning(null, PriceListManagerMessages.get().DeletePriceList_CanNotRemove,

						NLS.bind(PriceListManagerMessages.get().DeletePriceList_BaseAmounts,
						new Object[]{dto.getGuid(), dto.getName()}));
				return false;
			}
		}
		return true;
	}

	private Collection<BaseAmountDTO> getBaseAmounts(final PriceListDescriptorDTO dto) {
		baseAmountFilter.setPriceListDescriptorGuid(dto.getGuid());
		return priceListService.getBaseAmounts(baseAmountFilter);
	}

	/**
	 * Check whether the given dto is in a currently open price list editor.
	 *
	 * @param dto the dto to check
	 * @return true if it is in an open editor (also displays a dialog)
	 */
	private boolean checkForOpenEditorsAndDisplayWarning(final PriceListDescriptorDTO dto) {
		final IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

		for (IEditorReference editorRef : workbenchPage.getEditorReferences()) {
			try {
				if (EditorUtil.isSameEditor(editorRef, PriceListEditor.PART_ID) && EditorUtil.isSameEntity(dto.getGuid(), editorRef)) {
					MessageDialog.openWarning(null, PriceListManagerMessages.get().DeletePriceList_CanNotRemove,

							NLS.bind(PriceListManagerMessages.get().DeletePriceList_CloseEditor,
							new Object[]{dto.getName()}));
					return true;
				}
			} catch (PartInitException e) {
				LOG.error(e.getStackTrace());
				throw new EpUiException("Could not get price list editor input", e); //$NON-NLS-1$
			}
		}
		return false;
	}

	private void updateChangeSetStatusForBaseAmounts(final PriceListDescriptorDTO dto) {
		if (!changeSetHelper.isChangeSetsEnabled()) {
			return;
		}
		Collection<BaseAmountDTO> baseAmounts = getBaseAmounts(dto);
		for (BaseAmountDTO baseAmount : baseAmounts) {
			if (changeSetHelper.isMemberOfActiveChangeset(baseAmount)) {
				changeSetHelper.addObjectToChangeSet(baseAmount, ChangeSetMemberAction.DELETE);
			}
		}
	}

	/**
	 * Delete the price list for this DTO and add an event to the change set if enabled.
	 *
	 * @param dto the dto for the price list to delete
	 */
	protected void deletePriceList(final PriceListDescriptorDTO dto) {
		changeSetHelper.addObjectToChangeSet(dto, ChangeSetMemberAction.DELETE);
		priceListService.delete(dto);
	}

	private String getPriceListAssignmentsNames(final List<PriceListAssignmentsDTO> assignments) {
		StringBuilder plaNames = new StringBuilder();
		for (PriceListAssignmentsDTO dto : assignments) {
			plaNames.append(dto.getName());
			plaNames.append('\n');
		}
		return plaNames.toString();
	}

	@Override
	public String getTargetIdentifier() {
		return "deletePriceListAction"; //$NON-NLS-1$
	}

	@Override
	protected Object getDependentObject() {
		if (view == null) {
			return null;
		}
		return view.getSelectedItem();
	}

}
