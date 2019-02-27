/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 *  Delete named condition action.
 */
package com.elasticpath.cmclient.store.targetedselling.conditionalexpression.action;

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
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.event.EventType;
import com.elasticpath.cmclient.core.event.UIEvent;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.core.ui.util.EditorUtil;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingMessages;
import com.elasticpath.cmclient.store.targetedselling.conditionalexpression.editors.ConditionEditor;
import com.elasticpath.cmclient.store.targetedselling.conditionalexpression.model.ConditionalExpressionSearchTabModel;
import com.elasticpath.cmclient.store.targetedselling.conditionalexpression.model.impl.ConditionalExpressionModelImpl;
import com.elasticpath.cmclient.store.targetedselling.conditionalexpression.views.ConditionalExpressionSearchResultsView;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.domain.targetedselling.DynamicContentDelivery;
import com.elasticpath.service.changeset.ChangeSetMemberAction;
import com.elasticpath.service.sellingcontext.SellingContextService;
import com.elasticpath.service.targetedselling.DynamicContentDeliveryService;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.service.TagConditionService;


/**
 * DeleteConditionalExpressionAction.
 *
 */
@SuppressWarnings({ "PMD.PrematureDeclaration" })
public class DeleteConditionalExpressionAction extends BaseConditionalExpressionAction {
	
	/** The logger. */
	private static final Logger LOG = Logger.getLogger(DeleteConditionalExpressionAction.class);
	
	private final SellingContextService sellingContextService = ServiceLocator.getService(ContextIdNames.SELLING_CONTEXT_SERVICE);
	private final DynamicContentDeliveryService dynamicContentDeliveryService = ServiceLocator.getService(
		ContextIdNames.DYNAMIC_CONTENT_DELIVERY_SERVICE);
	private final TagConditionService tagConditionService = ServiceLocator.getService(ContextIdNames.TAG_CONDITION_SERVICE);
	private final ChangeSetHelper changeSetHelper = ServiceLocator.getService(ChangeSetHelper.BEAN_ID);


	/**
	 * default constructor for Delete action.
	 * @param view the view
	 * @param text the text
	 * @param imageDescriptor the icon
	 */
	public DeleteConditionalExpressionAction(
			final ConditionalExpressionSearchResultsView view, final String text, final ImageDescriptor imageDescriptor) {
		super(view, text, imageDescriptor);
	}
	
	@Override
	public void run() {
		LOG.debug("DeleteConditionalExpressionAction Action called."); //$NON-NLS-1$
		
		final ConditionalExpression expression = getView().getSelectedItem();
		final String namedConditionGuid = expression.getGuid();
		final List<SellingContext> sellingContext = getSellingContext(namedConditionGuid);
		final Shell shell = getView().getSite().getShell(); 
		
		if (this.checkForOpenEditor(expression)) {
			return;
		}
		
		if (isNamedConditionInUse(sellingContext)) {
			
			final String dcdNames = getDynamicContentDeliveryNames(sellingContext);

			MessageDialog.openWarning(
					shell,
				TargetedSellingMessages.get().SavedConditionInUse,

					NLS.bind(TargetedSellingMessages.get().SavedConditionInUseByContentDeliverys,
					new Object[]{
					expression.getName(),
					dcdNames})
			);


		} else  {
			if (MessageDialog.openConfirm(shell,
				TargetedSellingMessages.get().ConfirmDeleteSavedConditionMsgBoxTitle,

					NLS.bind(TargetedSellingMessages.get().ConfirmDeleteSavedConditionMsgBoxText,
					new Object[]{
					expression.getName()
				}))
			) {
				performDelete(expression);
				ConditionalExpressionEditorsCache.getInstance().closeConditionEditorByGuid(expression.getGuid());
			}
		}

	}

	private boolean checkForOpenEditor(final ConditionalExpression expression) {

		IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		for (IEditorReference editorRef : workbenchPage.getEditorReferences()) {
			try {
				if (EditorUtil.isSameEditor(editorRef, ConditionEditor.ID_EDITOR) && EditorUtil.isSameEntity(expression.getGuid(), editorRef)) {
					MessageDialog.openWarning(null,
							TargetedSellingMessages.get().DeleteSavedConditionMsgBoxTitle,

							NLS.bind(TargetedSellingMessages.get().DeleteSavedCondition_CloseEditor,
							new Object[]{expression.getName()}));
						return true;
				}
			} catch (final PartInitException e) {
				LOG.error(e.getStackTrace());
				throw new EpUiException("Could not get saved condition editor input", e); //$NON-NLS-1$
			}

		}
		return false;
	}

	/**
	 * Perform delete of given expression.
	 * @param expression  given expression.
	 */
	private void performDelete(final ConditionalExpression expression) {
		// delete from changeset
		changeSetHelper.addObjectToChangeSet(expression, ChangeSetMemberAction.DELETE);

		tagConditionService.delete(expression);

		UIEvent<ConditionalExpressionSearchTabModel> searchEvent = new UIEvent<>(new ConditionalExpressionModelImpl(), EventType.DELETE, false);
		this.fireEvent(searchEvent);
	}

	/**
	 * Get dynamic content names, that use given selling context list .
	 * @param sellingContext given selling context list.
	 * @return dynamic content names, delimited by new line.
	 */
	private String getDynamicContentDeliveryNames(final List<SellingContext> sellingContext) {
		StringBuilder stringBuilder = new StringBuilder();
		for (final SellingContext context : sellingContext) {
			stringBuilder.append(
					getDynamicContentDeliveryNames(context.getGuid())
					);
		}
		return stringBuilder.toString().substring(0, stringBuilder.length() - 2);
	}
	
	
	/**
	 * Get dynamic content names, that use given selling context guid .
	 * @param sellingContextGuid given selling context.
	 * @return dynamic content names, delimited by new line.
	 */
	private String getDynamicContentDeliveryNames(final String sellingContextGuid) {
		StringBuilder stringBuilder = new StringBuilder();
		List<DynamicContentDelivery> deliveries = dynamicContentDeliveryService.findBySellingContextGuid(sellingContextGuid);
		for (DynamicContentDelivery delivery : deliveries) {
			stringBuilder.append('"'); //$NON-NLS-1$
			stringBuilder.append(delivery.getName());
			stringBuilder.append("\", "); //$NON-NLS-1$
		}
		return stringBuilder.toString();
	}

	/**
	 * Get selling context by named condition.
	 * @param namedConditionGuid  named condition guid
	 * @return selling context 
	 */
	private List<SellingContext> getSellingContext(final String namedConditionGuid) {
		return sellingContextService.getByNamedConditionGuid(namedConditionGuid);
	}
	
	/**
	 * Check for usage of given named condition.	 
	 * @return true if given named condition guid used in any selling context.
	 */
	private boolean isNamedConditionInUse(final List<SellingContext> sellingContext) {
		return !sellingContext.isEmpty();
	}

	@Override
	public String getTargetIdentifier() {
		return "deleteConditionalExpression"; //$NON-NLS-1$
	}

}
