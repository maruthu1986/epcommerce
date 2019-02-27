/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.store.targetedselling.conditionalexpression.editors;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.editors.GuidEditorInput;
import com.elasticpath.cmclient.core.event.ChangeSetMemberSelectionProvider;
import com.elasticpath.cmclient.core.event.EventType;
import com.elasticpath.cmclient.core.event.UIEvent;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareFormEditor;
import com.elasticpath.cmclient.store.StorePlugin;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingMessages;
import com.elasticpath.cmclient.store.targetedselling.conditionalexpression.model.ConditionalExpressionSearchTabModel;
import com.elasticpath.cmclient.store.targetedselling.conditionalexpression.model.impl.ConditionalExpressionModelImpl;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.service.changeset.ChangeSetMemberAction;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.service.TagConditionService;

/**
 * ConditionalExpression editor .
 *
 */
public class ConditionEditor extends AbstractPolicyAwareFormEditor implements ChangeSetMemberSelectionProvider {

	/**
	 * ID of the editor. It is the same as the class name.
	 */
	public static final String ID_EDITOR = ConditionEditor.class.getName();
	
	private static final int TOTAL_WORK_UNITS = 3;
	
	private TagConditionService conditionService;

	private ChangeSetHelper changeSetHelper;


	private ConditionalExpression model;
	
	@Override
	public void initEditor(final IEditorSite site, final IEditorInput input) throws PartInitException {
		conditionService = ServiceLocator.getService(ContextIdNames.TAG_CONDITION_SERVICE);
		changeSetHelper = ServiceLocator.getService(ChangeSetHelper.BEAN_ID);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
	 */
	@Override
	protected void setInput(final IEditorInput input) {
		super.setInput(input);
		GuidEditorInput guidEditorInput = (GuidEditorInput) input;
		this.model = this.getConditionalExpressionService().findByGuid(guidEditorInput.getGuid());
		if (this.model == null && input instanceof ConditionEditorInput) {
			this.model = ((ConditionEditorInput) input).getConditionalExpression();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#setInputWithNotify(org.eclipse.ui.IEditorInput)
	 */
	@Override
	protected void setInputWithNotify(final IEditorInput input) {
		this.setInput(input);
		super.setInputWithNotify(input);
	}

	private TagConditionService getConditionalExpressionService() {
		return ServiceLocator.getService(ContextIdNames.TAG_CONDITION_SERVICE);
	}

	@Override
	@SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
	protected void addPages() {
		PolicyActionContainer container = addPolicyActionContainer(getTargetIdentifier());
		try {
			addPage(new SummaryPage(this), container);
			addPage(new ConditionBuilderPage(this), container);
			addExtensionPages(getClass().getSimpleName(), StorePlugin.PLUGIN_ID);
		} catch (final PartInitException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Locale getDefaultLocale() {
		return null;
	}

	@Override
	public Collection<Locale> getSupportedLocales() {
		return Collections.emptyList();
	}

	@Override
	public void reloadModel() {
		this.setInput(this.getEditorInput());
	}

	@Override
	protected void saveModel(final IProgressMonitor monitor) {
		monitor.beginTask(TargetedSellingMessages.get().Saving_SavedCondition, TOTAL_WORK_UNITS);
		try {
			ConditionalExpression condition = getDependentObject();
			if (isInvalidConditionName(monitor, condition)) {
				return;
			}
			if (isDuplicateName(monitor, condition)) {
				return;
			}
			if (!setExpressionStringIfItIsValid(monitor)) {
				return;
			}
			monitor.worked(1);
			// try to save
			ConditionalExpression updatedCondition = conditionService.saveOrUpdate(condition);
			// add to changeset if it active
			changeSetHelper.addObjectToChangeSet(condition, ChangeSetMemberAction.ADD);
			monitor.worked(1);
			// do local tasks
			reloadModel();
			this.reinitStatePolicy();
			this.refreshEditorPages();
			updateInView(updatedCondition);
		} finally {
			monitor.done();
		}
	}

	/**
	 * Update or add ConditionalExpression in conditional expression list.
	 * 
	 * @param conditionalExpression ConditionalExpression
	 */
	private void updateInView(final ConditionalExpression conditionalExpression) {
		
		EventType eventType;
		if (conditionalExpression.isPersisted()) {
			eventType = EventType.UPDATE;
		} else {
			eventType = EventType.CREATE;
		}

		UIEvent<ConditionalExpressionSearchTabModel> event =
				new UIEvent<>(new ConditionalExpressionModelImpl(), eventType, false);

		StorePlugin.getDefault().getConditionalExpressionListController().onEvent(event);
	}
	
	/**
	 * checks if conditionCollection is not null and not empty, generates the expression string
	 * from it and sets it to condition. If anything goes wrong a warning message is generated.
	 * @param monitor the monitor
	 * @return true if conditional expression string is set to conditions successfully
	 */
	private boolean setExpressionStringIfItIsValid(final IProgressMonitor monitor) {
		String stringExpression = this.getDependentObject().getConditionString();
		boolean failed = StringUtils.isBlank(stringExpression);
		if (failed) {
			createWarningDialog(TargetedSellingMessages.get().ConditionalExpressionEditor_BlankExpression_Title,
					TargetedSellingMessages.get().ConditionalExpressionEditor_BlankExpression_Body);
			monitor.setCanceled(true);
		}
		return !failed;
	}

	/**
	 * checks if condition is not null and its name is not blank. Generates a warning dialog.
	 * @param monitor the monitor
	 * @param condition current expression being modified (must not be null)
	 * @return true is expression is null or its name is blank
	 */
	private boolean isInvalidConditionName(final IProgressMonitor monitor,
			final ConditionalExpression condition) {
		if (condition == null || StringUtils.isBlank(condition.getName())
				|| TargetedSellingMessages.get().NewSavedCondition.equals(condition.getName())) {
			createWarningDialog(TargetedSellingMessages.get().ConditionalExpressionEditor_InvalidName_Title,
					TargetedSellingMessages.get().ConditionalExpressionEditor_InvalidName_Body);
			monitor.setCanceled(true);
			return true;
		}
		return false;
	}
	
	/**
	 * checks if name is unique for this condition being created by retrieving a condition
	 * from persistence layer by name. if a condition is found it is checked to being 
	 * equal to current (to overcome editing without changing a name) by using equals
	 * method. Generates a warning dialog.
	 * @param monitor the monitor
	 * @param expression current expression being modified (must not be null)
	 * @return true is expression with the same name already exists
	 */
	private boolean isDuplicateName(final IProgressMonitor monitor, 
			final ConditionalExpression expression) {
		final ConditionalExpression persistedExpression = conditionService.findByName(expression.getName());
		if (persistedExpression != null && !persistedExpression.equals(expression)) {
			createWarningDialog(TargetedSellingMessages.get().ConditionalExpressionEditor_DuplicateName_Title,

					NLS.bind(TargetedSellingMessages.get().ConditionalExpressionEditor_DuplicateName_Body,
					expression.getName()));
			monitor.setCanceled(true);
			return true;
		}
		return false;
	}
	
	/**
	 * Create a warning dialog in the CM Client using the provided title and message.
	 * 
	 * @param title the title for the warning dialog
	 * @param message the body message to be used in the warning dialog
	 */
	private void createWarningDialog(final String title, final String message) {
		MessageDialog.openWarning(Display.getCurrent().getActiveShell(), title, message);
	}

	@Override
	protected String getSaveOnCloseMessage() {
		return
			NLS.bind(TargetedSellingMessages.get().ConditionalEditor_OnSavePrompt,
			getEditorName());
	}
	
	@Override
	public int promptToSaveOnClose() {
		int result = super.promptToSaveOnClose();
		if (result == NO) {
			reloadModel();
		}
		return result;
	}

	@Override
	public String getTargetIdentifier() {
		return "conditionBuilderEditor"; //$NON-NLS-1$
	}

	@Override
	public void applyStatePolicy(final StatePolicy statePolicy) {
		statePolicy.init(getModel());
		super.applyStatePolicy(statePolicy);
	}

	@Override
	public ConditionalExpression getDependentObject() {
		return this.model;
	}

	@Override
	public Object resolveObjectMember(final Object changeSetObjectSelection) {
		return changeSetObjectSelection;
	}

	@Override
	protected String getEditorName() {
		String tabName = TargetedSellingMessages.get().NewSavedCondition;
		if (this.model != null && StringUtils.isNotBlank(this.model.getName())) {
			tabName = this.model.getName();
		}
		return tabName;
	}

}
