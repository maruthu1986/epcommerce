/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.views; // NOPMD

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.PartInitException;

import com.elasticpath.cmclient.catalog.actions.AddExistingProductAction;
import com.elasticpath.cmclient.catalog.actions.AddLinkedCategoryAction;
import com.elasticpath.cmclient.catalog.actions.CreateCategoryAction;
import com.elasticpath.cmclient.catalog.actions.CreateSubCategoryAction;
import com.elasticpath.cmclient.catalog.actions.EditCatalogCategoryAction;
import com.elasticpath.cmclient.catalog.actions.ExcludeLinkedCategoryAction;
import com.elasticpath.cmclient.catalog.actions.IncludeLinkedCategoryAction;
import com.elasticpath.cmclient.catalog.actions.RefreshCatalogTreeAction;
import com.elasticpath.cmclient.catalog.actions.ReorderCategoryDownAction;
import com.elasticpath.cmclient.catalog.actions.ReorderCategoryUpAction;
import com.elasticpath.cmclient.catalog.actions.product.CreateProductAction;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.EpUiException;
import com.elasticpath.cmclient.core.event.ChangeSetMemberSelectionProvider;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.cmclient.core.helpers.CatalogListener;
import com.elasticpath.cmclient.core.helpers.CategoryListener;
import com.elasticpath.cmclient.core.helpers.EPTestUtilFactory;
import com.elasticpath.cmclient.core.helpers.TestIdUtil;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.service.CatalogEventService;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.views.AbstractCmClientView;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.service.catalog.CategoryLookup;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.search.query.ProductSearchCriteria;

/**
 * The <code>CatalogBrowseView</code> is used to browse catalogs and categories.
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.ExcessiveImports" })
public class CatalogBrowseView extends AbstractCmClientView implements ChangeSetMemberSelectionProvider {
	
	/**
	 * The input of the tree viewer.
	 */
	class CatalogInput {

		/**
		 * Gets all the catalogs from the data source.
		 * 
		 * @return an array of catalogs
		 */
		public Object[] getElements() {
			final CatalogService catalogService = ServiceLocator.getService(ContextIdNames.CATALOG_SERVICE);

			final List<Catalog> catalogList = catalogService.findAllCatalogs();
			AuthorizationService.getInstance().filterAuthorizedCatalogs(catalogList);
			Collections.sort(catalogList, new CatalogBrowseNameSorter());
			return catalogList.toArray(new Catalog[catalogList.size()]);
		}
	}

	/**
	 * SearchView ID specified in the plugin.xml file. It is the same as the class name
	 */
	public static final String VIEW_ID = CatalogBrowseView.class.getName();

	private static final Logger LOG = Logger.getLogger(CatalogBrowseView.class);

	private final Locale locale = CorePlugin.getDefault().getDefaultLocale();
	
	private TreeViewer treeViewer;

	private Action doubleClickAction;

	private IToolBarManager toolBarManager;
	
	private Action editAction;
	
	private Action createCategoryAction;
	
	private Action createSubCategoryAction;
	
	private Action createProductAction;

	private Action addExistingProductAction;
	
	private Action addLinkedCategoryAction;
	
	private Action reorderCategoryUpAction;
	
	private Action reorderCategoryDownAction;
	
	private Action excludeLinkedCategoryAction;
	
	private Action includeLinkedCategoryAction;
	
	private Map<AcceleratorCode, List<IAction>> actionsMap;

	private Action refreshCatalogTreeAction;
	
	/**
	 * The constructor.
	 */
	public CatalogBrowseView() {
		super();
	}

	@Override
	protected void createViewPartControl(final Composite parentComposite) {
		final IEpLayoutComposite parentEpComposite = CompositeFactory.createGridLayoutComposite(parentComposite, 1, false);

		ToolBar toolBar = new ToolBar(parentEpComposite.getSwtComposite(), SWT.FLAT | SWT.LEFT);
		this.toolBarManager = new ToolBarManager(toolBar);

		this.createTreeViewer(parentEpComposite.getSwtComposite());
		
		this.editAction = new EditCatalogCategoryAction();
		this.createCategoryAction = new CreateCategoryAction();
		this.createSubCategoryAction = new CreateSubCategoryAction();
		this.createProductAction = new CreateProductAction();
		this.addExistingProductAction = new AddExistingProductAction();
		this.addLinkedCategoryAction = new AddLinkedCategoryAction();
		this.reorderCategoryUpAction = new ReorderCategoryUpAction();
		this.reorderCategoryDownAction = new ReorderCategoryDownAction();
		this.excludeLinkedCategoryAction = new ExcludeLinkedCategoryAction();
		this.includeLinkedCategoryAction = new IncludeLinkedCategoryAction();
		this.refreshCatalogTreeAction = new RefreshCatalogTreeAction(this.treeViewer);

		this.initializeToolBar(this.toolBarManager);
		
		initActionsMap();
		hookKeyBoardActions();
	}

	/**
	 * <p>
	 * Initializes <code>actionsMap</code> with available actions.
	 * </p>
	 * <p>
	 * List is needed since the same key binding can be bound to several actions (e. g. both Create Category and Create Sub Category actions have
	 * Ctrl+Shift+C shortcut). In this case the first (and the only, otherwise there would be a conflict) enabled action is ran.
	 * </p>
	 */
	private void initActionsMap() {
		actionsMap = new HashMap<>();

		List<IAction> categoryList = new ArrayList<>(2);
		categoryList.add(createCategoryAction);
		categoryList.add(createSubCategoryAction);
		actionsMap.put(new AcceleratorCode('c', SWT.MOD1 | SWT.MOD2), categoryList);

		List<IAction> moveUpList = new ArrayList<>(1);
		moveUpList.add(reorderCategoryUpAction);
		actionsMap.put(new AcceleratorCode('u', SWT.MOD1 | SWT.MOD2), moveUpList);

		List<IAction> moveDownList = new ArrayList<>(1);
		moveDownList.add(reorderCategoryDownAction);
		actionsMap.put(new AcceleratorCode('d', SWT.MOD1 | SWT.MOD2), moveDownList);

		List<IAction> editList = new ArrayList<>(1);
		editList.add(editAction);
		actionsMap.put(new AcceleratorCode('o', SWT.MOD1 | SWT.MOD2), editList);

		List<IAction> linkedCategoryList = new ArrayList<>(1);
		linkedCategoryList.add(addLinkedCategoryAction);
		actionsMap.put(new AcceleratorCode('l', SWT.MOD1 | SWT.MOD2), linkedCategoryList);

		List<IAction> existingProductList = new ArrayList<>(1);
		existingProductList.add(addExistingProductAction);
		actionsMap.put(new AcceleratorCode('r', SWT.MOD1 | SWT.MOD2), existingProductList);

		List<IAction> createProductList = new ArrayList<>(1);
		createProductList.add(createProductAction);
		actionsMap.put(new AcceleratorCode('p', SWT.MOD1 | SWT.MOD2), createProductList);
	}

	/**
	 * Binds keyboard shortcuts to the <code>treeView</code>.
	 */
	private void hookKeyBoardActions() {
		treeViewer.getControl().addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(final KeyEvent event) {
				handleKeyReleased(event);
			}

		});
	}

	/**
	 * Handles key released action.
	 * 
	 * @param event key event
	 * @see KeyEvent
	 */
	protected void handleKeyReleased(final KeyEvent event) {
		List<IAction> actions = actionsMap.get(AcceleratorCode.retrieveAcceleratorCode(event));
		if (actions != null) {
			for (IAction action : actions) {
				if (action.isEnabled()) {
					action.run();
					break;
				}
			}
		}
	}

	/**
	 * Initializes the toolbar with the various catalog/category actions.
	 * 
	 * @param toolBarManager the IToolBarManager object
	 */
	private void initializeToolBar(final IToolBarManager toolBarManager) {
		// edit button
		toolBarManager.add(this.editAction);
		toolBarManager.add(new Separator());
		// category actions
		toolBarManager.add(this.createCategoryAction);
		toolBarManager.add(this.createSubCategoryAction);
		toolBarManager.add(new Separator());
		// linked category actions
		toolBarManager.add(this.addLinkedCategoryAction);
		toolBarManager.add(this.excludeLinkedCategoryAction);
		toolBarManager.add(this.includeLinkedCategoryAction);
		// refresh
		toolBarManager.add(new Separator());
		toolBarManager.add(this.refreshCatalogTreeAction);

		toolBarManager.update(true);

		if (toolBarManager instanceof ToolBarManager) {
			ToolBar toolBar = ((ToolBarManager) toolBarManager).getControl();
			EPTestUtilFactory.getInstance().getTestIdUtil().setId(toolBar, TestIdUtil.BROWSE_VIEW_TOOLBAR_ID);
		}
	}
	
	@Override
	public void setFocus() {
		this.treeViewer.getControl().setFocus();
	}

	@Override
	protected ProductSearchCriteria getModel() {
		// this method should not be called; get the model by calling ProductSearchTab.getModel() instead
		return null;
	}

	/**
	 * View content provider for the catalog tree.
	 */
	public class CatalogBrowseViewContentProvider implements IStructuredContentProvider, ITreeContentProvider, CategoryListener, CatalogListener {
		/**
		 * Default constructor.
		 */
		public CatalogBrowseViewContentProvider() {
			CatalogEventService.getInstance().addCategoryListener(this);
			CatalogEventService.getInstance().addCatalogListener(this);
		}

		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
			// Not used
		}

		@Override
		public void dispose() {
			// Not used
		}

		@Override
		public Object[] getElements(final Object parent) {
			return ((CatalogInput) parent).getElements();
		}

		@Override
		public Object getParent(final Object child) {
			if (child instanceof Category) {
				return getCategoryLookup().findParent((Category) child);
			}
			return null;
		}

		@Override
		public Object[] getChildren(final Object parent) {


			if (parent instanceof Catalog) {
				final List<Category> allRootCategories = getCategoryService().listRootCategories((Catalog) parent, false);
				return allRootCategories.toArray();
			
			} else if (parent instanceof Category) {
				List<Category> children = getCategoryLookup().findChildren((Category) parent);
				if (children != null) {
					return children.toArray();
				}
			}
			return new Object[0];
		}

		@Override
		public boolean hasChildren(final Object parent) {
			if (parent instanceof Catalog) {
				return (getCatalogCategoryCount((Catalog) parent) > 0);
			} else if (parent instanceof Category) {
				Category parentCategory = (Category) parent;
				return getCategoryService().hasSubCategories(parentCategory.getUidPk());
			}
			return false;
		}

		@Override
		public void categoryChanged(final ItemChangeEvent<Category> event) {
			if (event.getEventType().equals(ItemChangeEvent.EventType.ADD)) {
				treeViewer.add(event.getItem().getCatalog(), event.getItem());
				treeViewer.setSelection(null);
				treeViewer.refresh();
			} else if (event.getEventType().equals(ItemChangeEvent.EventType.REMOVE)) {
				treeViewer.remove(event.getItem());
				treeViewer.refresh();
			} else if (event.getEventType().equals(ItemChangeEvent.EventType.CHANGE)) {
				includeLinkedCategoryAction.setEnabled(false);
				excludeLinkedCategoryAction.setEnabled(false);
				treeViewer.setSelection(null);
				treeViewer.refresh();
			}
		}

		@Override
		public void categorySearchResultReturned(final SearchResultEvent<Category> event) {
			// do nothing.
		}

		@Override
		public void catalogChanged(final ItemChangeEvent<Catalog> event) {
			if (event.getEventType().equals(ItemChangeEvent.EventType.ADD)) {
				treeViewer.refresh();
			} else if (event.getEventType().equals(ItemChangeEvent.EventType.REMOVE)) {
				treeViewer.refresh();
			} else if (event.getEventType().equals(ItemChangeEvent.EventType.CHANGE)) {
				treeViewer.refresh();
			}
		}
	}

	/**
	 * Creates the tree viewer.
	 * 
	 * @param parent the parent composite
	 */
	public void createTreeViewer(final Composite parent) {
		this.treeViewer = new TreeViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
		this.treeViewer.setContentProvider(new CatalogBrowseViewContentProvider());
		this.treeViewer.setLabelProvider(new CatalogBrowseViewLabelProvider(this.locale));
		this.treeViewer.setSorter(new CatalogBrowseNameSorter());

		this.treeViewer.setInput(new CatalogInput());
		this.treeViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		treeViewer.setComparer(new IElementComparer() {
			@Override
			public boolean equals(final Object objectA, final Object objectB) { // NOPMD (implements interface)
				if (objectA instanceof Category && objectB instanceof Category) {
					return ((Category) objectA).getUidPk() == ((Category) objectB).getUidPk();
				}
				return objectA.equals(objectB);
			}

			@Override
			public int hashCode(final Object element) {
				return element.hashCode();
			}
		});

		CatalogBrowseView.this.getViewSite().setSelectionProvider(this.treeViewer);
		EPTestUtilFactory.getInstance().getTestIdUtil().setId(treeViewer.getTree(), TestIdUtil.CATALOG_BROWSE_TREE_ID);

		this.hookContextMenu();
		this.createDoubleClickAction();
		this.hookDoubleClickAction();
	}
	
	private void createDoubleClickAction() {
		this.doubleClickAction = new Action() {
			@Override
			public void run() {
				final ISelection selection = treeViewer.getSelection();
				
				// Double-clicking on a Catalog does nothing.
				if (!(((IStructuredSelection) selection).getFirstElement() instanceof Category)) {
					return;
				}
				
				final Category selectedCategory = (Category) ((IStructuredSelection) selection).getFirstElement();
				openBrowseProductListView(selectedCategory);
			}
		};
	}
	
	private void hookDoubleClickAction() {
		this.treeViewer.addDoubleClickListener(event -> doubleClickAction.run());
	}

	private void hookContextMenu() {
		final CatalogBrowsingMenuManager menuMgr = new CatalogBrowsingMenuManager("#PopupMenu"); //$NON-NLS-1$
		this.treeViewer.addSelectionChangedListener(menuMgr);
		final Menu menu = menuMgr.createContextMenu(this.treeViewer.getControl());
		this.treeViewer.getControl().setMenu(menu);
		CatalogBrowseView.this.getSite().registerContextMenu(menuMgr, this.treeViewer);
	}

	/**
	 * @param category the category
	 * @return the master catalog for the given category.
	 */
	Catalog getCategoryMasterCatalog(final Category category) {
		return ((CategoryService) (ServiceLocator.getService(ContextIdNames.CATEGORY_SERVICE)))
			.getMasterCatalog(category);
	}
	
	/**
	 * Helper method that opens a BrowseProductListView displaying products contained in the given category.
	 *
	 * @param category the category that contains the products to browse.
	 */
	private void openBrowseProductListView(final Category category) {
		try {
			final BrowseProductListView productListView = (BrowseProductListView) getSite().getPage().showView(
					BrowseProductListView.PART_ID);
			productListView.setBrowseTreeObject(category);
			productListView.searchProducts(category);
			productListView.setSelectedCategory(category);
			
			if (category.isLinked()) {
				productListView.showIncludedColumn();
			} else {
				productListView.hideIncludedColumn();
			}
			
			productListView.getDeleteProductAction().setEnabled(false);
			productListView.getIncludeProductAction().setEnabled(false);
			productListView.getExcludeProductAction().setEnabled(false);

		} catch (final PartInitException e) {
			// Log the error and throw an unchecked exception
			LOG.error(e.getStackTrace());
			throw new EpUiException("Fail to reopen product list view.", e); //$NON-NLS-1$
		}
	}

	private int getCatalogCategoryCount(final Catalog catalog) {
		return getCategoryService().getRootCategoryCount(catalog.getUidPk());
	}

	protected CategoryLookup getCategoryLookup() {
		return ServiceLocator.getService(ContextIdNames.CATEGORY_LOOKUP);
	}

	protected CategoryService getCategoryService() {
		return ServiceLocator.getService(ContextIdNames.CATEGORY_SERVICE);
	}

	/**
	 * Sorting is not yet implemented.
	 */
	private class CatalogBrowseNameSorter extends ViewerSorter implements Comparator<Object> {
		@Override
		public int compare(final Viewer viewer, final Object obj1, final Object obj2) {
			return compare(obj1, obj2);
		}

		@Override
		public int compare(final Object obj1, final Object obj2) {
			if ((obj1 instanceof Category) && (obj2 instanceof Category)) {
				return ((Category) obj1).compareTo((Category) obj2);
			} else if ((obj1 instanceof Catalog) && (obj2 instanceof Catalog)) {
				return ((Catalog) obj1).getName().compareTo(((Catalog) obj2).getName());
			}

			// Should never get here.
			return 0;
		}
	}
	
	/**
	 * The class <code>AcceleratorCode</code> represents the keyboard shortcut.
	 */
	private static class AcceleratorCode {
		private static final int HASH_CODE_MULTIPLIER = 37;

		private static final int INIT_HASH_CODE = 17;

		private int keyCode;

		private int stateMask;

		/**
		 * Constructs the instance of the <code>AcceleratorCode</code> with the given <code>keyCode</code> and <code>stateMask</code>.
		 * 
		 * @param keyCode key code
		 * @param stateMask state mask
		 * @see KeyEvent#keyCode
		 * @see KeyEvent#stateMask
		 */
		AcceleratorCode(final int keyCode, final int stateMask) {
			this.keyCode = keyCode;
			this.stateMask = stateMask;
		}

		/**
		 * Gets the <code>keyCode</code>.
		 * 
		 * @return <code>keyCode</code>
		 * @see KeyEvent#keyCode
		 */
		public int getKeyCode() {
			return keyCode;
		}

		/**
		 * Sets the <code>keyCode</code>.
		 * 
		 * @param keyCode key code
		 * @see KeyEvent#keyCode
		 */
		public void setKeyCode(final char keyCode) {
			this.keyCode = keyCode;
		}

		/**
		 * Gets the <code>stateMask</code>.
		 * 
		 * @return <code>stateMask</code>
		 * @see KeyEvent#stateMask
		 */
		public int getStateMask() {
			return stateMask;
		}

		/**
		 * Sets the <code>stateMask</code>.
		 * 
		 * @param stateMask state mask
		 * @see KeyEvent#stateMask
		 */
		public void setStateMask(final int stateMask) {
			this.stateMask = stateMask;
		}

		/**
		 * Constructs the instance of the <code>AcceleratorCode</code> from the given <code>KeyEvent</code>.
		 * 
		 * @param event key event
		 * @return <code>AcceleratorCode</code> constructed from the given <code>KeyEvent</code>
		 * @see KeyEvent
		 */
		private static AcceleratorCode retrieveAcceleratorCode(final KeyEvent event) {
			return new AcceleratorCode(event.keyCode, event.stateMask);
		}

		@Override
		public boolean equals(final Object obj) {
			if (!(obj instanceof AcceleratorCode)) {
				return false;
			}
			AcceleratorCode acceleratorCode = (AcceleratorCode) obj;
			return (acceleratorCode.getKeyCode() == this.getKeyCode() && acceleratorCode.getStateMask() == this.getStateMask());
		}

		// Implementation based on the Effective Java by Josh Bloch
		@Override
		public int hashCode() {
			int result = INIT_HASH_CODE;
			result = HASH_CODE_MULTIPLIER * result + getKeyCode();
			result = HASH_CODE_MULTIPLIER * result + getStateMask();
			return result;
		}
	}

	@Override
	public Object resolveObjectMember(final Object changeSetObjectSelection) {		
		return changeSetObjectSelection;
	}

	@Override
	protected String getPartId() {
		return VIEW_ID;
	}
}
