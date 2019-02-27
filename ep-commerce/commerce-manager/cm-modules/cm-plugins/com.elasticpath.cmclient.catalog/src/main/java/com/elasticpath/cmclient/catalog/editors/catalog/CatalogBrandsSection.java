/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.editors.catalog;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.progress.IProgressService;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.dialogs.brand.BrandDialog;
import com.elasticpath.cmclient.catalog.editors.catalog.tablelabelprovider.CatalogBrandTableLabelProviderDecorator;
import com.elasticpath.cmclient.catalog.editors.catalog.tablelabelprovider.ChangeSetTableLabelProviderDecorator;
import com.elasticpath.cmclient.catalog.editors.catalog.tablelabelprovider.TableLabelProviderAdapter;
import com.elasticpath.cmclient.catalog.editors.model.CatalogModel;
import com.elasticpath.cmclient.core.EpUiException;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPage;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.event.ItemChangeEvent.EventType;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.core.service.CatalogEventService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.policy.PolicyPlugin;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEpTableSection;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.service.catalog.BrandService;
import com.elasticpath.service.changeset.ChangeSetMemberAction;

/**
 * Implements a section of the <code>CatalogCategoryPage</code> providing available brands within the catalog.
 */
@SuppressWarnings({ "PMD.GodClass" })
public class CatalogBrandsSection extends AbstractPolicyAwareEpTableSection<Brand> {

	private static final String CATALOG_BRANDS_TABLE = "Catalog Brands"; //$NON-NLS-1$
	private BrandService brandService;

	private final List<Brand> catalogBrands;

	private final ChangeSetHelper changeSetHelper = ServiceLocator.getService(ChangeSetHelper.BEAN_ID);

	private final ChangeSetColumnDecorator changeSetColumnDecorator = new ChangeSetColumnDecorator();

	private final CatalogBrandSectionObservable observable = new CatalogBrandSectionObservable();

	/**
	 * Default constructor.
	 *
	 * @param formPage the form page
	 * @param editor the editor
	 */
	public CatalogBrandsSection(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor);
		initializeServices();

		final List<Brand> readOnlyBrandList = brandService.findAllBrandsFromCatalog(getModel().getCatalog().getUidPk());
		catalogBrands = new ArrayList<>(readOnlyBrandList.size());
		catalogBrands.addAll(readOnlyBrandList);
		setTableItems(getModel().getBrandTableItems());
		if (formPage instanceof Observer) {
			observable.addObserver((Observer) formPage);
			observable.setChanged();
			observable.notifyObservers(readOnlyBrandList);
		}
	}

	/**
	 * Constructor used when you already have a list o brands to populate the table.
	 *
	 * @param formPage the form page
	 * @param editor the editor
	 * @param catalogBrands the list of brands
	 */
	public CatalogBrandsSection(final FormPage formPage, final AbstractCmClientFormEditor editor, final List<Brand> catalogBrands) {
		super(formPage, editor);
		this.catalogBrands = catalogBrands;
		setTableItems(getModel().getBrandTableItems());
		initializeServices();
		if (formPage instanceof Observer) {
			observable.addObserver((Observer) formPage);
		}
	}

	private void initializeServices() {
		brandService = ServiceLocator.getService(ContextIdNames.BRAND_SERVICE);
	}

	/**
	 * An observable subclass. Sub-classed to have access to setChanged().
	 *
	 */
	class CatalogBrandSectionObservable extends Observable {
		@Override
		@SuppressWarnings("PMD.UselessOverridingMethod")
		protected void setChanged() {
			super.setChanged();
		}
	}

	@Override
	protected String getSectionType() {
		return CatalogMessages.get().CatalogBrandsSection_ButtonText;
	}

	@Override
	protected void initializeTable(final IEpTableViewer table) {
		table.getSwtTableViewer().setComparator(new Comparator());

		addColumns(table);

		getViewer().setContentProvider(new TableContentProvider());

		addLabelProvider();

		refreshViewerInput();
	}

	private void addColumns(final IEpTableViewer table) {
		if (changeSetColumnDecorator.isDecoratable()) {
			changeSetColumnDecorator.addLockColumn(table);
			changeSetColumnDecorator.addActionColumn(table);
		}

		final String[] columnNames = { CatalogMessages.get().CatalogBrandsSection_TableBrandCodeColumn,
				CatalogMessages.get().CatalogBrandsSection_TableBrandNameColumn };
		final int[] columnWidths = { 100, 180, 120 };

		for (int i = 0; i < columnNames.length; ++i) {
			table.addTableColumn(columnNames[i], columnWidths[i]);
		}
	}

	private void addLabelProvider() {
		if (changeSetColumnDecorator.isDecoratable()) {
			getViewer().setLabelProvider(
					new CatalogBrandTableLabelProviderDecorator(
							new ChangeSetTableLabelProviderDecorator<>(
									new TableLabelProviderAdapter(), getModel().getBrandTableItems()),
							((AbstractCmClientEditorPage) getPage()).getSelectedLocale()));
		} else {
			getViewer().setLabelProvider(
					new CatalogBrandTableLabelProviderDecorator(new TableLabelProviderAdapter(),
							((AbstractCmClientEditorPage) getPage()).getSelectedLocale()));
		}
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		// nothing to bind
	}

	@Override
	protected EpState getEditorTableState() {
		return getStatePolicy().determineState(getEpTableSectionControlPane());
	}

	@Override
	public void commit(final boolean onSave) {
		if (onSave) {
			final Set<Brand> additionBrands = getAddedItems();
			for (final Brand brand : additionBrands) {
				brandService.add(brand);
				changeSetHelper.addObjectToChangeSet(brand, ChangeSetMemberAction.ADD);
				CatalogEventService.getInstance().notifyBrandChanged(new ItemChangeEvent<>(this, brand, EventType.ADD));
			}
			additionBrands.clear();

			final Set<Brand> modifiedBrands = getModifiedItems();
			for (final Brand brand : modifiedBrands) {
				final Brand updatedBrand = brandService.update(brand);
				catalogBrands.set(catalogBrands.indexOf(brand), updatedBrand);
				changeSetHelper.addObjectToChangeSet(brand, ChangeSetMemberAction.EDIT);
				CatalogEventService.getInstance().notifyBrandChanged(new ItemChangeEvent<>(this, updatedBrand, EventType.CHANGE));
			}
			modifiedBrands.clear();

			final Set<Brand> removedBrands = getRemovedItems();
			for (final Brand brand : removedBrands) {
				if (brand.isPersisted()) {
					changeSetHelper.addObjectToChangeSet(brand, ChangeSetMemberAction.DELETE);
					brandService.remove(brand);
					CatalogEventService.getInstance().notifyBrandChanged(new ItemChangeEvent<>(this, brand, EventType.REMOVE));
				}
			}
			removedBrands.clear();
			refreshViewerInput();
			super.commit(onSave);
		}

	}

	@Override
	public CatalogModel getModel() {
		return (CatalogModel) super.getModel();
	}

	@Override
	protected String getRemoveDialogTitle() {
		return CatalogMessages.get().CatalogBrandsSection_RemoveDialog_title;
	}

	@Override
	protected String getRemoveDialogDescription(final Brand item) {
		return
			NLS.bind(CatalogMessages.get().CatalogBrandsSection_RemoveDialog_description,
			getItemName(item));
	}

	@Override
	protected Brand addItemAction() {
		final CatalogBrandsPage page = (CatalogBrandsPage) getFormPage();
		final BrandDialog dialog = new BrandDialog(page.getSelectedLocale(), null, getModel());
		if (dialog.open() == Window.OK) {
			return dialog.getBrand();
		}
		return null;
	}

	@Override
	protected boolean editItemAction(final Brand object) {
		final CatalogBrandsPage page = (CatalogBrandsPage) getFormPage();
		final BrandDialog dialog = new BrandDialog(page.getSelectedLocale(), object, getModel());
		return dialog.open() == Window.OK;
	}

	@Override
	protected boolean removeItemAction(final Brand object) {
		final ParameterPasser passer = new ParameterPasser();
		passer.canRemove = false;
		final IProgressService progressService = PlatformUI.getWorkbench().getProgressService();
		try {
			// we can use the passer variable here because this is a blocking job
			progressService.busyCursorWhile(new IRunnableWithProgress() {
				@Override
				public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					if (brandService.isInUse(object.getUidPk())) {
						passer.canRemove = false;
					} else {
						passer.canRemove = true;
						updateChangeSetListsOnRemove(object);
					}
				}

				private void updateChangeSetListsOnRemove(final Brand object) {
					final Set<Brand> additionList = getAddedItems();
					if (isNewObjectAndInSet(object, additionList)) {
						additionList.remove(object);
						catalogBrands.remove(object);
					} else {
						getModel().getBrandTableItems().addRemovedItem(object);
					}
				}

				private boolean isNewObjectAndInSet(final Brand object, final Set<Brand> set) {
					return set.contains(object) && !object.isPersisted();
				}
			});
		} catch (final InvocationTargetException | InterruptedException e) {
			throw new EpUiException("Error removing: " + e.getMessage(), e); //$NON-NLS-1$
		}

		if (!passer.canRemove) {
			MessageDialog.openError(getPage().getSite().getShell(), CatalogMessages.get().CatalogBrandsSection_ErrorDialog_InUse_title,

					NLS.bind(CatalogMessages.get().CatalogBrandsSection_ErrorDialog_InUse_desc,
					getItemName(object)));
		}
		return passer.canRemove;
	}

	@Override
	protected String getItemName(final Brand brand) {
		final Locale selectedLocale = ((AbstractCmClientEditorPage) getPage()).getSelectedLocale();
		return String.format("%1$s - %2$s", brand.getCode(), brand.getDisplayName(selectedLocale, true)); //$NON-NLS-1$
	}

	@Override
	protected void addAddedItem(final Brand item) {
		super.addAddedItem(item);
		catalogBrands.add(item);
		markDirty();
		observable.setChanged();
		notifyParent();
	}

	@Override
	protected void addModifiedItem(final Brand item) {
		super.addModifiedItem(item);
		markDirty();
	}

	@Override
	protected void addRemovedItem(final Brand item) {
		super.addRemovedItem(item);
		if (!changeSetHelper.isChangeSetsEnabled()) {
			catalogBrands.remove(item);
		}
		markDirty();
	}

	/**
	 * Content provider for the table.
	 */
	private class TableContentProvider implements IStructuredContentProvider {

		@Override
		public Object[] getElements(final Object inputElement) {
			return catalogBrands.toArray();
		}

		@Override
		public void dispose() {
			// not used
		}

		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
			// not used
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		observable.deleteObservers();
		PolicyPlugin.getDefault().unregisterStatePolicyTarget(this);
	}

	private void notifyParent() {
		notifyParent(catalogBrands);
	}

	private void notifyParent(final List<Brand> catalogBrands) {
		observable.notifyObservers(catalogBrands);
	}

	/**
	 * Parameter holder class for passing a parameter between threads.
	 */
	private final class ParameterPasser {
		private boolean canRemove;
	}

	/**
	 * Brands specific sorter to sort on the brand code.
	 */
	private class Comparator extends ViewerComparator {
		@Override
		public int compare(final Viewer viewer, final Object object1, final Object object2) {
			final Brand brand1 = (Brand) object1;
			final Brand brand2 = (Brand) object2;
			return brand1.getCode().compareToIgnoreCase(brand2.getCode());
		}
	}

	@Override
	public void selectionChanged(final SelectionChangedEvent event) {
		final Brand brand = (Brand) ((IStructuredSelection) getViewer().getSelection()).getFirstElement();

		applyStatePolicyForBrand(getStatePolicy(), brand);
	}

	private void applyStatePolicyForBrand(final StatePolicy statePolicy, final Brand brand) {
		setStatePolicy(statePolicy);
		updatePoliciesWithDependentObject(brand);
		applyStatePolicy();
	}

	@Override
	public String getTargetIdentifier() {
		return "catalogBrandsSection"; //$NON-NLS-1$
	}

	@Override
	public void refresh() {
		// do nothing
	}

	@Override
	protected String getTableName() {
		return CATALOG_BRANDS_TABLE;
	}
}
