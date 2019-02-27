/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.stores.editors;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.elasticpath.cmclient.admin.stores.AdminStoresMessages;
import com.elasticpath.cmclient.admin.stores.AdminStoresPlugin;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;

/**
 * Represents the UI payment page.
 */
public class PaymentPage extends AbstractStorePage {

	/**
	 * Constructs the payment page.
	 * 
	 * @param editor the editor
	 * @param authorized whether the current user is authorized to edit the store
	 */
	public PaymentPage(final AbstractCmClientFormEditor editor, final boolean authorized) {
		super(editor, "paymentPage", AdminStoresMessages.get().Payments, authorized); //$NON-NLS-1$
	}

	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		final Composite body = managedForm.getForm().getBody();
		final IEpLayoutComposite creditCardSectionComposite = createCreditCardSectionComposite(body);

		new PaymentGatewaySection(editor, this.isEditable())
				.initialize(creditCardSectionComposite.getSwtComposite(), managedForm.getToolkit(), getEditor()
				.getDataBindingContext());
		new AlternativePaymentTypesSection(editor, this.isEditable()).initialize(body, managedForm.getToolkit(), getEditor().getDataBindingContext());
		new GiftCertificateSection(editor, this.isEditable()).initialize(body, managedForm.getToolkit(), getEditor().getDataBindingContext());

		getCustomPageData().put("isEditable", this.isEditable());
		getCustomPageData().put("body", body);
		addExtensionEditorSections(editor, managedForm, AdminStoresPlugin.PLUGIN_ID, this.getClass().getSimpleName());
	}

	private IEpLayoutComposite createCreditCardSectionComposite(final Composite body) {
		final IEpLayoutComposite creditCardSectionComposite = CompositeFactory.createTableWrapLayoutComposite(body, 1, false);
		final TableWrapData creditCardSectionWrapdata = new TableWrapData(TableWrapData.FILL, TableWrapData.FILL);
		creditCardSectionWrapdata.grabHorizontal = true;
		creditCardSectionWrapdata.rowspan = 2;
		creditCardSectionComposite.setLayoutData(creditCardSectionWrapdata);

		final TableWrapLayout creditCardSectionTableWrapLayout = new TableWrapLayout();
		creditCardSectionTableWrapLayout.topMargin = 0;
		creditCardSectionComposite.getSwtComposite().setLayout(creditCardSectionTableWrapLayout);
		return creditCardSectionComposite;
	}

	@Override
	protected int getFormColumnsCount() {
		return 2;
	}

	@Override
	protected String getFormTitle() {
		return AdminStoresMessages.get().Payments;
	}

	@Override
	public AbstractCmClientFormEditor getEditor() {
		return (AbstractCmClientFormEditor) super.getEditor();
	}
}
