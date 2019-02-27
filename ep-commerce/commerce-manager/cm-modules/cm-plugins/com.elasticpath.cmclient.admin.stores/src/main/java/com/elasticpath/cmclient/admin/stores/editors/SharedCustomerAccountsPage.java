/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.stores.editors;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.elasticpath.cmclient.admin.stores.AdminStoresMessages;
import com.elasticpath.cmclient.admin.stores.AdminStoresPlugin;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPageSectionPart;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.helpers.store.StoreEditorModel;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;

/**
 * Represents the UI of the Store Shared Customer Accounts Page.
 */
public class SharedCustomerAccountsPage extends AbstractStorePage {

	private SharedStoresSectionPart sharedStoresSectionPart;

	/**
	 * Creates SharedCustomerAccountsPage Instance.
	 * 
	 * @param editor <code>StoreEditor</code>
	 * @param authorized whether the current user is authorized to edit the current store
	 */
	public SharedCustomerAccountsPage(final AbstractCmClientFormEditor editor, final boolean authorized) {
		super(editor, "SharedCustomerAccountsPage", AdminStoresMessages.get().SharedCustomerAccounts, authorized); //$NON-NLS-1$
	}

	@Override
	protected String getFormTitle() {
		return AdminStoresMessages.get().SharedCustomerAccounts;
	}

	@Override
	protected int getFormColumnsCount() {
		return 1;
	}

	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		sharedStoresSectionPart = new SharedStoresSectionPart(this, editor, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED,
				this.isEditable());
		managedForm.addPart(sharedStoresSectionPart);
		addExtensionEditorSections(editor, managedForm, AdminStoresPlugin.PLUGIN_ID, this.getClass().getSimpleName());
	}

	/**
	 * Represents the shared store editor models section part.
	 */
	private final class SharedStoresSectionPart extends AbstractCmClientEditorPageSectionPart {
		private IEpLayoutComposite controlPane;

		private SharedCustomerAccountsDualListBox sharedCustomerAccountsSelectionBox;

		/**
		 * Constructs the shared stores section part.
		 * 
		 * @param formPage the form page.
		 * @param editor the editor
		 * @param style the style
		 * @param editable whether the section should be editable
		 */
		SharedStoresSectionPart(final FormPage formPage, final AbstractCmClientFormEditor editor, final int style,
				final boolean editable) {
			super(formPage, editor, style);
			this.getSection().setEnabled(editable);
		}

		@Override
		protected String getSectionTitle() {
			return AdminStoresMessages.get().CustomerAccountsAreSharedWithTheFollowingStores;
		}

		@Override
		protected void bindControls(final DataBindingContext bindingContext) {
			// empty
		}

		@Override
		protected void createControls(final Composite client, final FormToolkit toolkit) {
			controlPane = CompositeFactory.createTableWrapLayoutComposite(client, 2, false);

			sharedCustomerAccountsSelectionBox = new SharedCustomerAccountsDualListBox(controlPane, getStoreEditorModel());
			sharedCustomerAccountsSelectionBox.createControls();
			sharedCustomerAccountsSelectionBox.registerChangeListener(() -> getEditor().controlModified());

			controlPane.setControlModificationListener(getEditor());
		}

		@Override
		protected void populateControls() {
			// empty
		}

		private StoreEditorModel getStoreEditorModel() {
			return (StoreEditorModel) getEditor().getModel();
		}

		/**
		 * Gets the shared customer account duallist box.
		 * 
		 * @return the sharedCustomerAccountsSelectionBox
		 */
		public SharedCustomerAccountsDualListBox getSharedCustomerAccountsSelectionBox() {
			return sharedCustomerAccountsSelectionBox;
		}
	}

	/**
	 * Gets newly shared store editor models.
	 * 
	 * @return set of shared store editor models
	 */
	public Set<StoreEditorModel> getSharedStoreEditorModels() {
		if (sharedStoresSectionPart != null
			&& !sharedStoresSectionPart.getSharedCustomerAccountsSelectionBox().getAssigned().isEmpty()) {
			return new HashSet<>(
					sharedStoresSectionPart.getSharedCustomerAccountsSelectionBox().getAssigned());
		}
		return Collections.emptySet();
	}
}
