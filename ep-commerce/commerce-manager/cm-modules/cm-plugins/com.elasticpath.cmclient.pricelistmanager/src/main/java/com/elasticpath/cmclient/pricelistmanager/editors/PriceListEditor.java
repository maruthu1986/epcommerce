/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.pricelistmanager.editors;

import java.util.Collection;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.springframework.validation.FieldError;

import com.elasticpath.cmclient.core.editors.GuidEditorInput;
import com.elasticpath.cmclient.core.event.ChangeSetMemberSelectionProvider;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.event.ItemChangeEvent.EventType;
import com.elasticpath.cmclient.core.helpers.BaseAmountChangedEventListener;
import com.elasticpath.cmclient.core.service.BaseAmountEventService;
import com.elasticpath.cmclient.core.ui.TableSelectionProvider;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareFormEditor;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerMessages;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerPlugin;
import com.elasticpath.cmclient.pricelistmanager.controller.PriceListEditorController;
import com.elasticpath.cmclient.pricelistmanager.controller.impl.PriceListEditorControllerImpl;
import com.elasticpath.cmclient.pricelistmanager.event.PriceListChangedEvent;
import com.elasticpath.cmclient.pricelistmanager.event.PricingEventService;
import com.elasticpath.common.dto.ChangeSetObjects;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.exceptions.BaseAmountInvalidException;
import com.elasticpath.domain.pricing.exceptions.DuplicateBaseAmountException;

/**
 * Multi-page editor that allows editing of price lists. One page allows editing of BaseAmounts and another page allows editing of
 * PriceListDescriptors as well as showing PLD summary information and attributes.
 */
public class PriceListEditor extends AbstractPolicyAwareFormEditor implements ChangeSetMemberSelectionProvider, BaseAmountChangedEventListener {
	private static final Logger LOG = Logger.getLogger(PriceListEditor.class);

	private PriceListEditorController controller;

	private PolicyActionContainer priceListEditorContainer;

	private final TableSelectionProvider baseAmountTableSelectionProvider = new TableSelectionProvider();

	/**
	 * Editor ID.
	 */
	public static final String PART_ID = PriceListEditor.class.getName();

	@Override
	public void initEditor(final IEditorSite site, final IEditorInput input) throws PartInitException {
		GuidEditorInput guidInput = (GuidEditorInput) input;
		controller = new PriceListEditorControllerImpl(guidInput.getGuid());
		controller.getBaseAmountsFilter().setLimit(0);  //not show anything till user press search

		// add the base amount table listener to the selection service as a selection provider
		// a selected base amount can then be added to a change set, see AddToChangeSetActionDelegate
		site.setSelectionProvider(baseAmountTableSelectionProvider);

		BaseAmountEventService.getInstance().addBaseAmountChangedEventListener(this);

	}

	@Override
	public void dispose() {
		BaseAmountEventService.getInstance().removeBaseAmountChangedEventListener(this);
		super.dispose();
	}

	@Override
	protected void addPages() {
		priceListEditorContainer = addPolicyActionContainer("priceListEditor"); //$NON-NLS-1$
		try {
			if (hasNoPriceListDescriptorName()) {
				addPage(new PriceListDescriptorEditorPage(this, controller), priceListEditorContainer);
				addPage(new BaseAmountEditorPage(this, controller, baseAmountTableSelectionProvider), priceListEditorContainer);
			} else {
				addPage(new BaseAmountEditorPage(this, controller, baseAmountTableSelectionProvider), priceListEditorContainer);
				addPage(new PriceListDescriptorEditorPage(this, controller), priceListEditorContainer);
			}
			getCustomData().put("priceListEditorContainer", priceListEditorContainer);
			addExtensionPages(getClass().getSimpleName(), PriceListManagerPlugin.PLUGIN_ID);
		} catch (PartInitException ex) {
			LOG.error("Unable to create PriceListEditor subpages: ", ex); //$NON-NLS-1$
		}
	}

	@Override
	public void doSaveAs() {
		// Not supported
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public Locale getDefaultLocale() {
		return null;
	}

	@Override
	public Collection<Locale> getSupportedLocales() {
		return null;
	}

	@Override
	public void reloadModel() {
		ChangeSetObjects<BaseAmountDTO> changeSet = this.controller.getModel().getChangeSet();
		boolean wasDirty = this.isDirty();

		controller.reloadModel();

		if (wasDirty) {
			controller.getModel().setChangeSet(changeSet);
		}
	}

	@Override
	protected void saveModel(final IProgressMonitor monitor) {
		try {

			if (!checkNameUnique()) {
				monitor.setCanceled(true);
				return;
			}
			controller.saveModel();

			final PriceListDescriptorDTO priceListDescriptor = controller.getPriceListDescriptor();

			// The parts are redrawn with the editor input's title+tooltip
			GuidEditorInput input = (GuidEditorInput) this.getEditorInput();
			input.setGuid(priceListDescriptor.getGuid());

			// update search results view after editing
			PricingEventService.getInstance().fireChangedEvent(new PriceListChangedEvent(priceListDescriptor));
		} catch (final BaseAmountInvalidException e) {
			String message = getMessage(e);
			MessageDialog.openError(Display.getCurrent().getActiveShell(), PriceListManagerMessages.get().PriceListEditorError_Title, message);
			monitor.setCanceled(true);
		} catch (DuplicateBaseAmountException dbae) {
			BaseAmount baseAmount = dbae.getBaseAmount();
			MessageDialog.openError(Display.getCurrent().getActiveShell(),
					PriceListManagerMessages.get().PriceListEditorError_Title,
						NLS.bind(PriceListManagerMessages.get().PriceListEditor_DuplicateBaseAmount,
						new Object[]{baseAmount.getObjectType(),
					baseAmount.getObjectGuid(), baseAmount.getQuantity().toString()}));
			monitor.setCanceled(true);
		}
		refreshEditorPages();
	}

	private boolean checkNameUnique() {
		if (controller.isPriceListNameUnique()) {
			return true;
		}
		MessageDialog.openError(Display.getCurrent().getActiveShell(), PriceListManagerMessages.get().PriceListEditorError_Title,
				PriceListManagerMessages.get().Price_List_name_unique);

		return false;
	}

	private String getMessage(final BaseAmountInvalidException exception) {

		StringBuffer result = new StringBuffer();

		FieldError fieldError;
		String message;
		for (Object error : exception.getErrors().getAllErrors()) {
			fieldError = (FieldError) error;
			message = PriceListManagerMessages.get().getMessage(fieldError.getCode().replace(".", "_")); //$NON-NLS-1$//$NON-NLS-2$
			result.append(message);
			result.append('\n');
		}
		return result.toString();
	}

	@Override
	public PriceListDescriptorDTO getModel() {
		return controller.getPriceListDescriptor();
	}

	@Override
	public String getEditorToolTip() {
		return
			NLS.bind(PriceListManagerMessages.get().PriceListEditorTooltip,
			new Object[]{getEditorName()});
	}

	@Override
	public String getTargetIdentifier() {
		return "priceListEditor"; //$NON-NLS-1$
	}

	@Override
	protected String getEditorName() {
		if (hasNoPriceListDescriptorName()) {
			return PriceListManagerMessages.get().PriceList_New;
		}
		return getModel().getName();
	}

	/**
	 * The name of a new price list descriptor is {@code null} until it gets set.
	 */
	private boolean hasNoPriceListDescriptorName() {
		return StringUtils.isEmpty(getModel().getName());
	}

	@Override
	public Object resolveObjectMember(final Object changeSetObjectSelection) {
		return changeSetObjectSelection;
	}

	@Override
	public void baseAmountChanged(final ItemChangeEvent<BaseAmountDTO> event) {
		if (isEventFromItSelf(event)) {
			return;
		}
		String priceListGuid = this.controller.getModel().getPriceListDescriptor().getGuid();
		if (priceListGuid == null || !priceListGuid.equals(event.getItem().getPriceListDescriptorGuid())) {
			return;
		}
		if (event.getEventType().equals(EventType.ADD) || isBaseAmountChanged(event.getItem())) {
			this.reloadModel();
			this.refreshEditorPages();
		}
	}

	private boolean isEventFromItSelf(final ItemChangeEvent<BaseAmountDTO> event) {
		if (event.getSource() instanceof PriceListEditorController) {
			PriceListEditorController from = (PriceListEditorController) event.getSource();
			if (from == this.controller) {
				return true;
			}
		}
		return false;
	}

	private boolean isBaseAmountChanged(final BaseAmountDTO changedBaseAmount) {
		Collection<BaseAmountDTO> baseAmounts = controller.getAllBaseAmounts();
		for (BaseAmountDTO baseAmountDTO : baseAmounts) {
			if (baseAmountDTO.getGuid().equals(changedBaseAmount.getGuid())) {
				return true;
			}
		}
		return false;
	}

}
