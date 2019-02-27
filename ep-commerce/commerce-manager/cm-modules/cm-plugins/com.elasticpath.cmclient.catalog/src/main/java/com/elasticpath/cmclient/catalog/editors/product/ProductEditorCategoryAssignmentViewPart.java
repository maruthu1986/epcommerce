/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.editors.product;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.ui.dialog.CategoryFinderDialog;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.ui.framework.IEpViewPart;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.common.DefaultStatePolicyDelegateImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.policy.ui.PolicyTargetCompositeFactory;
import com.elasticpath.cmclient.core.dto.catalog.ProductModel;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;

/**
 * The class display the master catalog section of the Category Assignment page.
 */
@SuppressWarnings({ "PMD.GodClass" })
public class ProductEditorCategoryAssignmentViewPart extends DefaultStatePolicyDelegateImpl
	implements IEpViewPart, SelectionListener, ISelectionChangedListener {

	private static final String PRODUCT_CATEGORY_TABLE = "Product Category"; //$NON-NLS-1$
	private final Locale defaultLocale = CorePlugin.getDefault().getDefaultLocale();

	/**
	 * The LabelProvider class for the category table.
	 */
	public class CategoryLabelProvider extends LabelProvider implements
			ITableLabelProvider {

		private static final int CODE_COLUMN = 0;
		private static final int DISPLAY_NAME_COLUMN = 1;
		private static final int LINKED_COLUMN = 2;

		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			final Category category = (Category) element;
			switch (columnIndex) {
			case CODE_COLUMN:
				return category.getCode();
			case DISPLAY_NAME_COLUMN:
				return category.getDisplayName(defaultLocale);
			case LINKED_COLUMN:
				if (category.isLinked()) {
					return CatalogMessages.get().CategoryAssignmentPage_CategoryLinkedFlag_Yes;
				}
				return CatalogMessages.get().CategoryAssignmentPage_CategoryLinkedFlag_No;
			default:
				return CatalogMessages.EMPTY_STRING;
			}
		}
	}

	/**
	 * The view content provider for the attribute table.
	 */
	public class CategoryContentProvider implements IStructuredContentProvider {

		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput,
								 final Object newInput) {
			// No action required
		}

		@Override
		public void dispose() {
			// Nothing to dispose
		}

		/**
		 * Returns an array of elements to display.
		 *
		 * @param inputObj
		 *            the input object (A Product attribute array)
		 * @return an array of product AttributeValue
		 */
		@Override
		public Object[] getElements(final Object inputObj) {
			return (Object[]) inputObj;
		}
	}

	private IPolicyTargetLayoutComposite mainComposite;

	private IEpTableViewer categoryTableViewer;

	private Button addButton;

	private Button removeButton;

	private CCombo categoryCombo;
	private List<Category> categoriesInCombo;
	private final AbstractCmClientFormEditor editor;

	private final Catalog catalog;

	private PolicyActionContainer controlsContainer;

	private StatePolicy statePolicy;

	/**
	 * @param editor the page Editor object.
	 * @param catalog the catalog this view part is for
	 */
	public ProductEditorCategoryAssignmentViewPart(final Catalog catalog,
			final AbstractCmClientFormEditor editor) {
		this.catalog = catalog;
		this.editor = editor;
	}

	/**
	 * Utility method for getting categories for this catalog.
	 */
	private Set<Category> getCategories() {
		return getModel().getCategories(getCatalog());
	}

	/**
	 * Overrides to provide the controls in the section.
	 *
	 * @param parent
	 *            the parent composite that controls are to be added to
	 * @param data
	 *            the layout data
	 */
	@Override
	public void createControls(final IEpLayoutComposite parent, final IEpLayoutData data) {

		controlsContainer = addPolicyActionContainer("categoryAssignmentControls"); //$NON-NLS-1$
		controlsContainer.setPolicyDependent(this.catalog);

		this.mainComposite = PolicyTargetCompositeFactory.wrapLayoutComposite(parent.addGridLayoutComposite(1, false, data));

		final IEpLayoutData tableCompositeLayoutData =
			mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);

		final IPolicyTargetLayoutComposite upperComposite = mainComposite.addGridLayoutComposite(2, false, null, controlsContainer);
		final GridData gridData = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, true);
		upperComposite.setLayoutData(gridData);

		final IPolicyTargetLayoutComposite tableButtonComposite =
			mainComposite.addGridLayoutComposite(2, false, tableCompositeLayoutData, controlsContainer);

		upperComposite.addLabelBoldRequired(
				CatalogMessages.get().CategoryAssignmentPage_PrimaryCategory, null, controlsContainer);

		final IEpLayoutData comboData = upperComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING, false, false);
		categoryCombo = upperComposite.addComboBox(comboData, controlsContainer);

		final IEpLayoutData tableData = tableButtonComposite.createLayoutData(
				IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);

		final IPolicyTargetLayoutComposite tableComposite = tableButtonComposite
				.addGridLayoutComposite(1, false, tableData, controlsContainer);

		final IEpLayoutData tableLayoutData = mainComposite.createLayoutData(
				IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);

		this.categoryTableViewer = tableComposite.addTableViewer(false, tableLayoutData, controlsContainer, PRODUCT_CATEGORY_TABLE);

		final IPolicyTargetLayoutComposite buttonsComposite = tableButtonComposite
				.addGridLayoutComposite(1, false, null, controlsContainer);

		// create add button for Category finder dialog.
		String addButtonLabel = CatalogMessages.get().CategoryAssignmentPage_Button_Add;
		String removeButtonLabel = CatalogMessages.get().CategoryAssignmentPage_Button_Remove;

		if (!catalog.isMaster()) {
			addButtonLabel = CatalogMessages.get().CategoryAssignmentPage_Button_Add_Or_Include;
			removeButtonLabel = CatalogMessages.get().CategoryAssignmentPage_Button_Exclude;
		}

		addButton = buttonsComposite.addPushButton(
				addButtonLabel,
				CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_ADD),
				null, controlsContainer);

		removeButton = buttonsComposite.addPushButton(
				removeButtonLabel,
				CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_REMOVE),
				null, controlsContainer);
	}

	@Override
	public void populateControls() {
		final int codeColumnWidth = 140;
		final int nameColumnWidth = 190;
		final int linkedColumnWidth = 60;

		this.categoryTableViewer
				.setContentProvider(new CategoryContentProvider());
		this.categoryTableViewer.addTableColumn(
				CatalogMessages.get().CategoryAssignmentPage_CategoryCode,
				codeColumnWidth);
		this.categoryTableViewer.addTableColumn(
				CatalogMessages.get().CategoryAssignmentPage_CategoryName,
				nameColumnWidth);
		if (!getCatalog().isMaster()) {
			this.categoryTableViewer.addTableColumn(
					CatalogMessages.get().CategoryAssignmentPage_CategoryLinkedFlag,
					linkedColumnWidth);
		}
		this.categoryTableViewer.setLabelProvider(new CategoryLabelProvider());
		this.categoryTableViewer.setInput(getInput(getModel()));

		this.populateCategoriesToCombo();

		addButton.addSelectionListener(this);
		removeButton.addSelectionListener(this);
	}

	private void populateCategoriesToCombo() {
		this.categoriesInCombo = new ArrayList<>();
		final Category defaultCategory = getModel().getDefaultCategory(getCatalog());
		for (final Category category : getCategories()) {
			this.categoryCombo.add(category.getDisplayName(defaultLocale));
			if (defaultCategory != null && category.getUidPk() == defaultCategory.getUidPk()) {
				this.categoryCombo.select(this.categoryCombo.getItemCount() - 1);
			}
			this.categoriesInCombo.add(category);
		}
	}

	private Object[] getInput(final Product product) {
		final List<Category> categories = new ArrayList<>();
		final Set<Category> categorySet = product.getCategories();
		for (final Category category : categorySet) {
			if (category.getCatalog().getUidPk() == getCatalog().getUidPk()) {
				categories.add(category);
			}
		}
		return categories.toArray();
	}

	private Catalog getCatalog() {
		return catalog;
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent event) {
		// do nothing. no need to implement.
	}

	@Override
	public void widgetSelected(final SelectionEvent event) {
		if (event.getSource() == addButton) {
			addCategory();
		}

		if (event.getSource() == removeButton) {
			final Category category = getSelectedCategory();

			if (category != null && category.isLinked()) {
				excludeProductFromLinkedCategory(category);
			} else {
				removeCategory();
			}
		}
	}

	/**
	 * Excludes the active product from the selected linked category.
	 *
	 * @param category a linked category
	 */
	private void excludeProductFromLinkedCategory(final Category category) {
		final String message = CatalogMessages.get().CategoryAssignmentPage_RemoveConfirmMsg + category.getCode() + " - " //$NON-NLS-1$
									+ category.getDisplayName(defaultLocale);

		final boolean confirm = MessageDialog.openConfirm(getEditor().getSite().getShell(),
				CatalogMessages.get().CategoryAssignmentPage_RemoveConfirmTitle,
				message);

		if (confirm) {
			removeCategoryFromUI(category);
		}
	}

	private void primaryCategoryChanged() {
		final Category category = categoriesInCombo.get(this.categoryCombo
				.getSelectionIndex());
		getModel().setCategoryAsDefault(category);
		getEditor().controlModified();
	}

	private void removeCategory() {
		final Category category = getSelectedCategory();

		if (category != null) {
			if (getCatalog().isMaster() && category.getUidPk() == getDefaultCategoryUidPk()) {
				MessageDialog.openWarning(
								getEditor().getSite().getShell(),
								CatalogMessages.get().CategoryAssignmentPage_RemovePrimaryWarningTitle,
								CatalogMessages.get().CategoryAssignmentPage_RemovePrimaryWarningMsg);
				return;
			}

			final String message = CatalogMessages.get().CategoryAssignmentPage_RemoveConfirmMsg
					+ category.getCode() + " - " //$NON-NLS-1$
					+ category.getDisplayName(defaultLocale);

			final boolean confirm = MessageDialog.openConfirm(getEditor().getSite()
					.getShell(),
					CatalogMessages.get().CategoryAssignmentPage_RemoveConfirmTitle,
					message);

			if (confirm) {
				removeCategoryFromUI(category);
			}
		}
	}

	private void removeCategoryFromUI(final Category category) {
		getModel().removeCategory(category);
		removeFromCombox(category);
		refreshViewer();
	}

	/**
	 * @return the currently selected category in TableViewer, if nothing is selected returns null
	 */
	private Category getSelectedCategory() {
		final IStructuredSelection selection = (IStructuredSelection) categoryTableViewer.getSwtTableViewer().getSelection();

		if (selection == null || selection.isEmpty() || !(selection.getFirstElement() instanceof Category)) {
			return null;
		}

		return (Category) selection.getFirstElement();
	}

	// ---- DOCaddCategory
	private void addCategory() {
		final CategoryFinderDialog dialog = new CategoryFinderDialog(
				mainComposite.getSwtComposite().getShell(), this.getCatalog());
		final int result = dialog.open();
		if (result != Window.OK) {
			return;
		}
		final Category category = (Category) dialog.getSelectedObject();
		if (category != null) {
			getModel().addCategory(category);
			addToCombox(category);
			categoriesInCombo.add(category);
			refreshViewer();
		}
	}
	// ---- DOCaddCategory

	private void removeFromCombox(final Category assignmentRemoved) {
		categoryCombo.remove(assignmentRemoved.getDisplayName(defaultLocale));
		categoryCombo.redraw();
	}

	private void addToCombox(final Category addedAssignment) {
		categoryCombo.add(addedAssignment.getDisplayName(defaultLocale));
		categoryCombo.redraw();
	}

	@Override
	public void selectionChanged(final SelectionChangedEvent event) {
		if (event.getSelection() instanceof IStructuredSelection) {
			final Category category = (Category) ((IStructuredSelection) event.getSelection()).getFirstElement();
			setButtonState(category != null);

			if (category != null) {
				if (category.isLinked()) {
					removeButton.setText(CatalogMessages.get().CategoryAssignmentPage_Button_Exclude);
				} else {
					removeButton.setText(CatalogMessages.get().CategoryAssignmentPage_Button_Remove);
				}
			}
		}
	}

	@Override
	public void bindControls(final DataBindingContext bindingContext) {
		EpControlBindingProvider.getInstance().bind(bindingContext, categoryCombo,
				EpValidatorFactory.REQUIRED, null, new ObservableUpdateValueStrategy() {
					@Override
					protected IStatus doSet(final IObservableValue observableValue, final Object value) {
						primaryCategoryChanged();
						return Status.OK_STATUS;
					}
		}, true);

	}

	/**
	 * @return the section's model which is the current Product object.
	 */
	@Override
	public Product getModel() {
		return ((ProductModel) editor.getModel()).getProduct();
	}

	private void refreshViewer() {
		categoryTableViewer.setInput(getInput(getModel()));
		categoryTableViewer.getSwtTableViewer().refresh();

		mainComposite.getSwtComposite().redraw();
		getEditor().controlModified();
	}

	private AbstractCmClientFormEditor getEditor() {
		return editor;
	}

	/**
	 * Returns the current state of the editor.
	 *
	 * @return the current state of the editor
	 */
	protected EpState getEditorState() {
		if (statePolicy == null) {
			return EpState.READ_ONLY;
		}
		return statePolicy.determineState(controlsContainer);
	}

	@Override
	public void applyStatePolicy(final StatePolicy policy) {
		this.statePolicy = policy;
		super.applyStatePolicy(policy);
	}

	@Override
	public void refreshLayout() {
		if (!mainComposite.getSwtComposite().isDisposed()) {
			mainComposite.getSwtComposite().layout();
		}
	}

	/**
	 * Set the button state dependent on whether a row is selected or not.
	 *
	 * @param rowSelected true if a row is selected.
	 */
	protected void setButtonState(final boolean rowSelected) {
		final boolean buttonsEnabled = (getEditorState() == EpState.EDITABLE && rowSelected);
		if (!removeButton.isDisposed()) {
			removeButton.setEnabled(buttonsEnabled);
		}
	}

	/**
	 * Returns the UidPk of the default primary category.
	 *
	 * @return the UidPk of the default primary category.
	 */
	protected long getDefaultCategoryUidPk() {
		return getModel().getDefaultCategory(getCatalog()).getUidPk();
	}

}
