/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.catalog.views;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;

import com.elasticpath.cmclient.catalog.actions.AddExistingProductAction;
import com.elasticpath.cmclient.catalog.actions.AddLinkedCategoryAction;
import com.elasticpath.cmclient.catalog.actions.CreateCategoryAction;
import com.elasticpath.cmclient.catalog.actions.CreateSubCategoryAction;
import com.elasticpath.cmclient.catalog.actions.DeleteCatalogCategoryAction;
import com.elasticpath.cmclient.catalog.actions.EditCatalogCategoryAction;
import com.elasticpath.cmclient.catalog.actions.ExcludeLinkedCategoryAction;
import com.elasticpath.cmclient.catalog.actions.IncludeLinkedCategoryAction;
import com.elasticpath.cmclient.catalog.actions.RemoveLinkedCategoryAction;
import com.elasticpath.cmclient.catalog.actions.ReorderCategoryDownAction;
import com.elasticpath.cmclient.catalog.actions.ReorderCategoryUpAction;
import com.elasticpath.cmclient.catalog.actions.product.CreateProductAction;
import com.elasticpath.cmclient.catalog.actions.product.CreateProductBundleAction;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;

/**
 * Context menu manager class for the CatalogBrowseView tree items.
 */
public class CatalogBrowsingMenuManager extends MenuManager implements ISelectionChangedListener  {

	private final EditCatalogCategoryAction editCatalogCategoryAction;
	private final CreateCategoryAction createCategoryAction;
	private final CreateSubCategoryAction createSubCategoryAction;
	private final CreateProductAction createProductAction;
	private final CreateProductBundleAction createProductBundleAction;
	private final AddExistingProductAction addExistingProductAction;
	private final AddLinkedCategoryAction addLinkedCategoryAction;
	private final ReorderCategoryUpAction reorderCategoryUpAction;
	private final ReorderCategoryDownAction reorderCategoryDownAction;
	private final DeleteCatalogCategoryAction deleteCatalogCategoryAction;
	private final RemoveLinkedCategoryAction removeLinkedCategoryAction;
	private final ExcludeLinkedCategoryAction excludeLinkedCategoryAction;
	private final IncludeLinkedCategoryAction includeLinkedCategoryAction;
	
	private Object currentElement;
	
	/**
	 * Creates a new MenuManager for the CatalogBrowseView's context menu.
	 * This menu manager has an id of null.
	 * @param text the text for the menu
	 */
	@SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
	public CatalogBrowsingMenuManager(final String text) {
		super(text);
		this.setRemoveAllWhenShown(true);
				
		editCatalogCategoryAction = new EditCatalogCategoryAction();
		createCategoryAction = new CreateCategoryAction();
		createSubCategoryAction = new CreateSubCategoryAction();
		createProductAction = new CreateProductAction();
		createProductBundleAction = new CreateProductBundleAction();
		addExistingProductAction = new AddExistingProductAction();
		addLinkedCategoryAction = new AddLinkedCategoryAction();
		reorderCategoryUpAction = new ReorderCategoryUpAction();
		reorderCategoryDownAction = new ReorderCategoryDownAction();
		deleteCatalogCategoryAction = new DeleteCatalogCategoryAction();
		removeLinkedCategoryAction = new RemoveLinkedCategoryAction();
		excludeLinkedCategoryAction = new ExcludeLinkedCategoryAction();
		includeLinkedCategoryAction = new IncludeLinkedCategoryAction();

		this.addMenuListener((IMenuListener) arg0 -> fillContextMenu());
	}

	/**
	 * Called when a selection has changed. Determines which
	 * menu items should be shown depending on the type of item selected,
	 * and adds the items to the menu.
	 * @param event the selection changed event
	 */
	@Override
	public void selectionChanged(final SelectionChangedEvent event) {
		if (!(event.getSelection() instanceof IStructuredSelection)) {
			return;
		}
		final IStructuredSelection structuredSelection = (IStructuredSelection) event.getSelection();
		currentElement = structuredSelection.getFirstElement();
	}
	
	/**
	 * Fills the context menu based on the currently-selected element.
	 */
	void fillContextMenu() {
		if (currentElement instanceof Catalog) {
			fillContextMenuForCatalog((Catalog) currentElement);
		} else if (currentElement instanceof Category) {
			// Category
			final Category selectedCategory = (Category) currentElement;
			
			if (selectedCategory.isVirtual() && !selectedCategory.isLinked()) {
				fillContextMenuForVirtualNonLinkedCategory(selectedCategory);
			} else if (selectedCategory.isVirtual() && selectedCategory.isLinked()) {
				fillContextMenuForVirtualLinkedCategory(selectedCategory);
			} else if (!selectedCategory.isVirtual()) {
				fillContextMenuForNonVirtualCategory(selectedCategory);
			}
		}
	}
	
	/**
	 * Fills the given context menu with menu items appropriate for the given non-virtual category and
	 * corresponding to the current user's permissions.
	 * 
	 * @param selectedCategory the non-virtual category
	 */
	void fillContextMenuForNonVirtualCategory(final Category selectedCategory) {
		// Non-virtual category
		add(editCatalogCategoryAction);
		add(deleteCatalogCategoryAction);
		add(new Separator());
		add(reorderCategoryUpAction);
		add(reorderCategoryDownAction);
		add(new Separator());
		add(createSubCategoryAction);
		add(createProductAction);
		add(createProductBundleAction);
	}

	/**
	 * Fills the given context menu with menu items appropriate for the given virtual linked category and
	 * corresponding to the current user's permissions.
	 * 
	 * @param selectedCategory the non-virtual category
	 */
	void fillContextMenuForVirtualLinkedCategory(final Category selectedCategory) {
		// Virtual, Linked category
		add(editCatalogCategoryAction);
		add(removeLinkedCategoryAction);
		add(new Separator());
		add(reorderCategoryUpAction);
		add(reorderCategoryDownAction);
		add(new Separator());
		
		if (selectedCategory.hasParent()) {
			// Inclusion/exclusion is not relevant for top-level linked categories
			add(excludeLinkedCategoryAction);
			add(includeLinkedCategoryAction);
		}
	}

	/**
	 * Fills the given context menu with menu items appropriate for the given virtual non-linked category and
	 * corresponding to the current user's permissions.
	 * 
	 * @param selectedCategory the non-virtual category
	 */
	void fillContextMenuForVirtualNonLinkedCategory(final Category selectedCategory) {
		// Virtual, non-linked category
		add(editCatalogCategoryAction);
		add(deleteCatalogCategoryAction);
		add(new Separator());
		add(reorderCategoryUpAction);
		add(reorderCategoryDownAction);
		add(new Separator());
		add(createSubCategoryAction);
		add(addExistingProductAction);
	}

	/**
	 * Fill the context menu with menu items appropriate for the given catalog.
	 * 
	 * @param catalog the catalog selected at time of right-mouse-click
	 */
	void fillContextMenuForCatalog(final Catalog catalog) {
		add(editCatalogCategoryAction);
		add(deleteCatalogCategoryAction);
		add(new Separator());
		add(createCategoryAction);
		
		if (!catalog.isMaster()) {
			// Virtual catalog
			add(addLinkedCategoryAction);
		}
	}
	
}
