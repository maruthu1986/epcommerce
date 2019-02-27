/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.conditionbuilder.editor;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.forms.editor.FormPage;

import com.elasticpath.cmclient.conditionbuilder.adapter.BaseModelAdapter;
import com.elasticpath.cmclient.conditionbuilder.plugin.ConditionBuilderMessages;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.editors.CancelSaveException;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEditorPageSectionPart;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.service.InvalidConditionTreeException;

/**
 * This class holds common functionality for condition section parts.
 */
public abstract class AbstractConditionSectionPart extends AbstractPolicyAwareEditorPageSectionPart {

	private final String dictionaryGuid;

	/**
	 * Creates a new instance of {@link AbstractConditionSectionPart}.
	 * 
	 * @param formPage the {@link FormPage}
	 * @param editor an {@link AbstractCmClientFormEditor} parent
	 * @param style the style
	 * @param dictionaryGuid dictionary guid
	 */
	public AbstractConditionSectionPart(final FormPage formPage, final AbstractCmClientFormEditor editor, final int style, 
			final String dictionaryGuid) {
		super(formPage, editor, style);
		this.dictionaryGuid = dictionaryGuid;
	}

	/**
	 * @return tag dictionary guid
	 */
	protected String getDictionaryGuid() {
		return dictionaryGuid;
	}

	/**
	 * @return condition panel
	 */
	protected abstract AbstractConditionPanel<SellingContext> getConditionPanel();

	/**
	 * Shows warning dialog.
	 * 
	 * @param errorMessage message to display
	 */
	protected void showWarningDialog(final String errorMessage) {
		MessageDialog.openWarning(getManagedForm().getForm().getShell(), ConditionBuilderMessages.get().ShopperSectionPart_Error_Title, errorMessage);
	}

	/**
	 * Checks if the validation status is clear.
	 * 
	 * @param status status string to check
	 * @return true if status clear, otherwise false
	 */
	protected boolean isValidationErrorsStatusOk(final String status) {
		return status.length() == 0;
	}

	/**
	 * Applies some custom business rule to determine if the model is populated correctly.
	 * 
	 * @param modelAdapter the model adapter
	 * @return true if the model has errors, otherwise false
	 */
	protected abstract boolean isModelNotValid(final BaseModelAdapter<ConditionalExpression> modelAdapter);

	/**
	 * @return validation error status message string
	 */
	protected String getValidationErrorsStatus() {
		final char newLine = '\n';
		final StringBuilder message = new StringBuilder(StringUtils.EMPTY);

		for (IStatus status : (Collection<IStatus>) getConditionPanel().getActiveDataBindingContext().getValidationStatusMap().values()) {
			if (!status.isOK()) {
				message.append(status.getMessage());
				message.append(newLine);
			}
		}

		return message.toString();
	}

	@Override
	public void commit(final boolean onSave) {
		if (onSave) { // commit page changes only on save, no need to pester a user all the time
							// when she navigates the tabs

			String validationStatus = getValidationErrorsStatus();

			if (!isValidationErrorsStatusOk(validationStatus)) {
				showWarningDialog(validationStatus);
				throw new CancelSaveException(validationStatus);
			}

			BaseModelAdapter<ConditionalExpression> modelAdapter = null;

			try {
				modelAdapter = getConditionPanel().getModelAdapter();
			} catch (InvalidConditionTreeException e) {
				showWarningDialog(e.getLocalizedMessage());
				throw new CancelSaveException(e);
			}

			if (isModelNotValid(modelAdapter)) {
				showWarningDialog(ConditionBuilderMessages.get().ConditionRequired);
				throw new CancelSaveException(ConditionBuilderMessages.get().ConditionRequired);
			}

			ConditionalExpression expression = modelAdapter.getModel();
			getConditionPanel().getModelWrapper().getModel().setCondition(dictionaryGuid, expression);

			markStale();
			super.commit(onSave);
		}
	}

}
