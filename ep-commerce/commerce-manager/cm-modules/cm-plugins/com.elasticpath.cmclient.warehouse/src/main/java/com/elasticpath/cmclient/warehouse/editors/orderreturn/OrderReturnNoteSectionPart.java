/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.warehouse.editors.orderreturn;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPageSectionPart;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.helpers.Helper;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.ControlModificationListener;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.warehouse.WarehouseMessages;
import com.elasticpath.cmclient.warehouse.WarehousePermissions;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderReturnStatus;

/**
 * Represents the UI for viewing the notes.
 */
public class OrderReturnNoteSectionPart extends AbstractCmClientEditorPageSectionPart {

	private static final String FIELD_NAME_RETURN_COMMENT = "returnComment"; //$NON-NLS-1$

	private static final int NOTES_WIDTH = 75;

	private static final int NOTES_HEIGHT = 100;

	private final OrderReturn orderReturn;

	private final ControlModificationListener controlModificationListener;

	private IEpLayoutComposite noteComposite;

	private Text notesText;

	private final EpState editMode;

	/**
	 * Constructor.
	 * 
	 * @param formPage the FormPage
	 * @param editor the AbstractCmClientFormEditor
	 * @param editMode if this part is read only 
	 */
	public OrderReturnNoteSectionPart(final FormPage formPage, final AbstractCmClientFormEditor editor, final EpState editMode) {
		super(formPage, editor, ExpandableComposite.TITLE_BAR);
		this.orderReturn = (OrderReturn) editor.getModel();
		this.controlModificationListener = editor;

		if (editMode == EpState.EDITABLE && orderReturn.getReturnStatus() == OrderReturnStatus.AWAITING_STOCK_RETURN
				&& AuthorizationService.getInstance().isAuthorizedWithPermission(WarehousePermissions.WAREHOUSE_ORDER_RETURN_EDIT)) {
			this.editMode = EpState.EDITABLE;
		} else {
			this.editMode = EpState.READ_ONLY;
		}
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		final boolean hideDecorationOnFirstValidation = true;
		final EpControlBindingProvider binder = EpControlBindingProvider.getInstance();

		binder.bind(bindingContext, notesText, orderReturn, FIELD_NAME_RETURN_COMMENT, EpValidatorFactory.MAX_LENGTH_2000, null,
				hideDecorationOnFirstValidation);
	}

	@Override
	protected void createControls(final Composite client, final FormToolkit toolkit) {		
		noteComposite = CompositeFactory.createTableWrapLayoutComposite(client, 1, true);
		noteComposite.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB));

		notesText = noteComposite.addTextArea(true, false, editMode, null);
		
		Helper.fixText(notesText, NOTES_WIDTH, NOTES_HEIGHT);
	}

	@Override
	protected void populateControls() {
		notesText.setText(WarehouseMessages.get().getString(orderReturn.getReturnComment()));

		if (editMode == EpState.EDITABLE) {
			noteComposite.setControlModificationListener(this.controlModificationListener);
		}
	}

	@Override
	protected String getSectionDescription() {
		return WarehouseMessages.get().OrderReturnNoteSection_Description;
	}

	@Override
	protected String getSectionTitle() {
		return WarehouseMessages.get().OrderReturnNoteSection_Title;
	}

}
