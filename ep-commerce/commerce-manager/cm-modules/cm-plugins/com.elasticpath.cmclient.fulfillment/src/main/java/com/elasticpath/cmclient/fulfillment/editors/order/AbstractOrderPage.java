/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.editors.order;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPage;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.AbstractCmClientFormSectionPart;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.fulfillment.FulfillmentImageRegistry;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.FulfillmentPermissions;
import com.elasticpath.cmclient.fulfillment.editors.actions.AddNoteContributionAction;
import com.elasticpath.cmclient.fulfillment.editors.actions.CreateRefundContributionAction;
import com.elasticpath.cmclient.fulfillment.editors.actions.ResendConfirmationEmailContributionAction;
import com.elasticpath.cmclient.fulfillment.editors.actions.UnlockOrderContributionAction;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderLock;
import com.elasticpath.service.order.OrderLockService;

/**
 * The abstract order editor page, provide the unified action bar functionality.
 */
@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod")
public abstract class AbstractOrderPage extends AbstractCmClientEditorPage {

	private final AbstractCmClientFormEditor editor;
	
	private Action unlockOrderAction;
	
	private Composite orderLockedComposite;
	
	private Label orderLockedByLabel;
	
	private Label lockerUserNameLabel;
	
	private OrderLockService orderLockService;
	
	/**
	 * Constructs the editor page.
	 * 
	 * @param editor The EP FormEditor
	 * @param partId the id for the editor page
	 * @param title the title of the editor page
	 */
	public AbstractOrderPage(final AbstractCmClientFormEditor editor, final String partId, final String title) {
		super(editor, partId, title);
		this.editor = editor;
	}

	@Override
	protected void addToolbarActions(final IToolBarManager toolBarManager) {

		final OrderEditor orderEditor = (OrderEditor) getEditor();
		final Order selectedOrder = orderEditor.getModel();

		final AuthorizationService authorizationService = AuthorizationService.getInstance();
		boolean isOrderLocked = orderEditor.isOrderLocked();

		EpState addNotePermission = EpState.READ_ONLY;
		EpState createRefundPermission = EpState.READ_ONLY;
		if (orderEditor.isAuthorizedAndNotLocked()) {
			addNotePermission = EpState.EDITABLE;
		}
		if (orderEditor.isAuthorizedAndAvailableForEdit()) {
			createRefundPermission = EpState.EDITABLE;
		}
		
		unlockOrderAction = new UnlockOrderContributionAction(this);
		toolBarManager.add(addToolbarActionItem(unlockOrderAction, isOrderLocked));
			
		//MSC-7890. If the user is not assigned the order's store, unlock button should be disabled
		//NTRN-1166, NTRN-723 unlock button should be disabled if user does not have edit order and unlock order permissions
		final boolean hasUnlockPermissions = authorizationService.isAuthorizedWithPermission(FulfillmentPermissions.ORDER_EDIT)
			&& authorizationService.isAuthorizedWithPermission(FulfillmentPermissions.ORDER_UNLOCK);
		if (!hasUnlockPermissions || !authorizationService.isAuthorizedForStore(selectedOrder.getStore())) {
			unlockOrderAction.setEnabled(false);
		}
		
		final Action addNoteAction = new AddNoteContributionAction(this);
		final Action resendConfirmationEmailContributionAction = new ResendConfirmationEmailContributionAction(this);
		final Action createRefundContributionAction = new CreateRefundContributionAction(this,
				FulfillmentMessages.get().OrderEditor_CreateRefund_ActionTitle);
		
		toolBarManager.add(addToolbarActionItem(addNoteAction, addNotePermission != EpState.READ_ONLY));
		toolBarManager.add(addToolbarActionItem(resendConfirmationEmailContributionAction, true));
		toolBarManager.add(addToolbarActionItem(createRefundContributionAction,
				createRefundPermission != EpState.READ_ONLY 
				&& authorizationService.isAuthorizedWithPermission(FulfillmentPermissions.CREATE_REFUND)
				&& selectedOrder.isRefundable()));

		setLockedOrderTitle(isOrderLocked);
	}
	
	/**
	 * Create toolbar action item.
	 *
	 * @param action to be added
	 * @return action item
	 */
	private ActionContributionItem addToolbarActionItem(final Action action, final boolean enabled) {
		action.setEnabled(enabled);
		final ActionContributionItem result = new ActionContributionItem(action);
		result.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		return result;
	}
	
	/**
	 * Enables order action.  
	 * 
	 * @param enabled enables order action if true
	 */
	public void setUnlockOrderActionEnabled(final boolean enabled) {
		if (unlockOrderAction != null) {
			unlockOrderAction.setEnabled(enabled);
		}
	}
	
	/**
	 * Sets title.
	 *
	 * @param isLocked true if order is locked
	 */
	public void setLockedOrderTitle(final boolean isLocked) {
		if (isLocked) {
			Image lockImage = FulfillmentImageRegistry.getImage(FulfillmentImageRegistry.LOCK_LOCKED);
			final ScrolledForm form = getManagedForm().getForm();
			form.setImage(lockImage);
		} else {
			Image unlockImage = FulfillmentImageRegistry.getImage(FulfillmentImageRegistry.LOCK_UNLOCKED);
			final ScrolledForm form = getManagedForm().getForm();
			form.setImage(unlockImage);
		}
	}
	
	@Override
	protected void createEditorContent(final IManagedForm managedForm) {
		final ScrolledForm form = managedForm.getForm();
		form.addDisposeListener(new DisposeListener() {

			/** called when com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor.refreshEditorPages releases control. */
			@Override
			public void widgetDisposed(final DisposeEvent disposeEvent) {
				for (IFormPart formPart : managedForm.getParts()) {
					AbstractCmClientFormSectionPart abstractFormPart = (AbstractCmClientFormSectionPart) formPart;
					abstractFormPart.sectionDisposed();
				}
				pageDisposed();			
			}
			
		});
		final FormToolkit toolkit = managedForm.getToolkit();
		final Composite body = form.getBody();
		body.setLayout(new TableWrapLayout());
		orderLockedComposite = toolkit.createComposite(body, SWT.NONE);
		final TableWrapData tableWrapDataComposite = new TableWrapData(TableWrapData.LEFT, TableWrapData.MIDDLE);
		tableWrapDataComposite.grabHorizontal = true;
		orderLockedComposite.setLayoutData(tableWrapDataComposite);
		final TableWrapLayout tableWrapLayout = new TableWrapLayout();
		final int leftMargin = 10;
		tableWrapLayout.leftMargin = leftMargin;
		final int numColumns = 3;
		tableWrapLayout.numColumns = numColumns;
		orderLockedComposite.setLayout(tableWrapLayout);
		toolkit.adapt(orderLockedComposite);
		super.createEditorContent(managedForm);
		orderLockedByLabel = toolkit.createLabel(orderLockedComposite, "Order Locked By:", SWT.NONE); //$NON-NLS-1$
		lockerUserNameLabel = toolkit.createLabel(orderLockedComposite, "", SWT.NONE);	//$NON-NLS-1$
		refreshOrderLockedByLabel();
	}
	
	/**
	 * Set visible or invisible "Order Locked By" label depending on orderLock. 
	 */
	public void refreshOrderLockedByLabel() {
		if (lockerUserNameLabel == null || orderLockedByLabel == null) {
			/** means that page hasn't yet been initialized. */
			return;
		}
		if (lockerUserNameLabel.isDisposed() || orderLockedByLabel.isDisposed()) {
			return;
		}
		Order order = (Order) editor.getModel();
		OrderLock orderLock = getOrderLockService().getOrderLock(order);
		if (orderLock == null) {
			orderLockedByLabel.setVisible(false);
			lockerUserNameLabel.setVisible(false);
		} else {
			orderLockedByLabel.setVisible(true);
			lockerUserNameLabel.setText(orderLock.getCmUser().getUserName());
			lockerUserNameLabel.setVisible(true);
			lockerUserNameLabel.getParent().pack();
		}
	}
	
	@Override
	public AbstractCmClientFormEditor getEditor() {
		return editor;
	}
	
	private OrderLockService getOrderLockService() {
		if (orderLockService == null) {
			orderLockService = ServiceLocator.getService(ContextIdNames.ORDER_LOCK_SERVICE);
		}
		return orderLockService;
	}
}
