/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.editors.category;

import java.util.Collection;
import java.util.Locale;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.elasticpath.cmclient.catalog.CatalogImageRegistry;
import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.CatalogPermissions;
import com.elasticpath.cmclient.catalog.CatalogPlugin;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.EpUiException;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.editors.GuidEditorInput;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.service.CatalogEventService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareFormEditor;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.service.catalog.CategoryLookup;
import com.elasticpath.service.catalog.CategoryService;
/**
 * Implements a multi-page editor for displaying and editing categories.
 */
public class CategoryEditor extends AbstractPolicyAwareFormEditor {

	/**
	 * Editor ID.
	 */
	public static final String PART_ID = CategoryEditor.class.getName();

	private Category category;

	private CategoryLookup categoryLookup;
	private CategoryService categoryService;

	private PolicyActionContainer pageContainer;

	@Override
	public void initEditor(final IEditorSite site, final IEditorInput input) throws PartInitException {
		category = input.getAdapter(Category.class);
		categoryLookup = ServiceLocator.getService(ContextIdNames.CATEGORY_LOOKUP);
		categoryService = ServiceLocator.getService(ContextIdNames.CATEGORY_SERVICE);

		pageContainer = addPolicyActionContainer("categoryEditor"); //$NON-NLS-1$

		if (getCategory().isLinked()) {
			setEditorTitleImage(CatalogImageRegistry.CATEGORY_LINKED.createImage());
		}
	}

	@Override
	public Category getModel() {
		return category;
	}

	@Override
	protected void addPages() {
		try {
			addPage(new CategorySummaryPage(this), pageContainer);
			addPage(new CategoryAttributePage(this), pageContainer);
			addPage(new CategoryFeaturedProductsPage(this), pageContainer);
			addExtensionPages(getClass().getSimpleName(), CatalogPlugin.PLUGIN_ID);
		} catch (final PartInitException e) {
			// TODO: Find out what should be done in this case
			// Can't throw the PartInitException because it is checked
			// and the super-implementation doesn't check for it.
			// throwing an unchecked generic exception for now (bad)
			throw new EpUiException(e);
		}
	}

	@Override
	protected void saveModel(final IProgressMonitor monitor) {
		monitor.beginTask(CatalogMessages.get().CategoryEditor_Save_StatusBarMsg, 2);
		try {
			category = categoryService.saveOrUpdate(category);
			reloadModel();
			fireCategoryChangedEvent();
			monitor.worked(1);

			setPartName(category.getCode());
			refreshEditorPages();
		} finally {
			monitor.done();
		}
	}

	/**
	 * Let the listeners (e.g. Catalog browser) know of Category updates.
	 */
	private void fireCategoryChangedEvent() {
		final ItemChangeEvent<Category> event = new ItemChangeEvent<>(this, category, ItemChangeEvent.EventType.CHANGE);
		CatalogEventService.getInstance().notifyCategoryChanged(event);
	}

	@Override
	public void reloadModel() {
		category = categoryLookup.findByUid(category.getUidPk());
	}

	@Override
	public Collection<Locale> getSupportedLocales() {
		return getModel().getCatalog().getSupportedLocales();
	}

	@Override
	public Locale getDefaultLocale() {
		return getModel().getCatalog().getDefaultLocale();
	}

	/**
	 * @return {@link EpState#EDITABLE} if the category being edited is editable by the current user, {@link EpState#READ_ONLY} if not.
	 */
	protected EpState getAuthorizationState() {
		if (isAuthorizedToManageCategories() && isAuthorizedToEditContainingCatalog()) {
			return EpState.EDITABLE;
		}
		return EpState.READ_ONLY;
	}

	/**
	 * @return true if the current user has permission to manage categories.
	 */
	boolean isAuthorizedToManageCategories() {
		return AuthorizationService.getInstance().isAuthorizedWithPermission(CatalogPermissions.CATEGORY_MANAGE);
	}

	/**
	 * @return true if the current user has permissions to manage the current category's
	 * master category's catalog (since to modify this category is to modify the master).
	 */
	boolean isAuthorizedToEditContainingCatalog() {
		//Check that we're authorized to modify whatever catalog this category is in
		boolean authorized = AuthorizationService.getInstance().isAuthorizedForCatalog(getModel().getCatalog());
		//If the category's a linked category we must also be authorized to modify its master category's catalog
		if (getModel().isLinked()) {
			authorized = authorized && AuthorizationService.getInstance().isAuthorizedForCatalog(getMasterCategoryCatalog());
		}
		return authorized;
	}

	/**
	 * Get the current category's master category's catalog. This implementation
	 * goes straight to the CategoryService because it's unlikely that a linked
	 * category's master category and its master catalog will both be populated
	 * on the local object instance.
	 * @return the current category's master category's catalog
	 */
	Catalog getMasterCategoryCatalog() {
		return categoryService.getMasterCatalog(getModel());
	}

	@Override
	protected String getSaveOnCloseMessage() {
		return
			NLS.bind(CatalogMessages.get().CategoryEditor_OnSavePrompt,
			getEditorName());
	}

	@Override
	public void applyStatePolicy(final StatePolicy statePolicy) {
		statePolicy.init(getModel());
		super.applyStatePolicy(statePolicy);
	}

	@Override
	public String getTargetIdentifier() {
		return "categoryEditor"; //$NON-NLS-1$
	}

	@Override
	public String getEditorName() {
		return getCategory().getCode();
	}

	@Override
	public String getEditorToolTip() {
		final Locale locale = CorePlugin.getDefault().getDefaultLocale();
		return
			NLS.bind(CatalogMessages.get().CategoryEditor_Tooltip,
			getCategory().getCode(), getCategory().getDisplayName(locale));
	}

	/**
	 * Gets a category from persistent storage.
	 *
	 * @return the {@link Category}
	 */
	public Category getCategory() {
		if (category == null) {
			GuidEditorInput guidEditorInput = (GuidEditorInput) getEditorInput();
			String categoryGuid = guidEditorInput.getGuid();
			try {
				category = categoryLookup.findByGuid(categoryGuid);
			}	catch (Exception e) {
				throw new IllegalArgumentException(//NOPMD

						NLS.bind(CoreMessages.get().Given_Object_Not_Exist,
						new String[]{"Category", categoryGuid})); //$NON-NLS-1$
			}
		}
		return category;
	}
}
