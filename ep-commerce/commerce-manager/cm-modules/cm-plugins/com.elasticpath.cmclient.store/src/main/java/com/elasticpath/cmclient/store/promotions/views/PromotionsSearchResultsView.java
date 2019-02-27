/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.store.promotions.views;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;

import com.elasticpath.cmclient.conditionbuilder.wizard.conditions.handlers.ConditionHandler;
import com.elasticpath.cmclient.conditionbuilder.wizard.model.TimeConditionModelAdapter;
import com.elasticpath.cmclient.conditionbuilder.wizard.model.impl.TimeConditionModelAdapterImpl;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.event.ChangeSetMemberSelectionProvider;
import com.elasticpath.cmclient.core.util.DateTimeUtilFactory;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.cmclient.core.helpers.AbstractSearchRequestJob;
import com.elasticpath.cmclient.core.ui.framework.IEpTableColumn;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.views.AbstractSortListView;
import com.elasticpath.cmclient.store.StorePlugin;
import com.elasticpath.cmclient.store.promotions.PromotionsImageRegistry;
import com.elasticpath.cmclient.store.promotions.PromotionsMessages;
import com.elasticpath.cmclient.store.promotions.actions.OpenPromotionsEditorAction;
import com.elasticpath.cmclient.store.promotions.event.PromotionsChangeEvent;
import com.elasticpath.cmclient.store.promotions.event.PromotionsEventListener;
import com.elasticpath.cmclient.store.promotions.event.PromotionsEventService;
import com.elasticpath.cmclient.store.promotions.helpers.PromotionsSearchRequestJob;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleScenarios;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.search.query.SortBy;
import com.elasticpath.service.search.query.StandardSortBy;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.domain.LogicalOperator;
import com.elasticpath.tags.domain.TagDictionary;

/**
 * This view displays a list of promotions in a table format.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.GodClass" })
public class PromotionsSearchResultsView extends AbstractSortListView implements PromotionsEventListener, ChangeSetMemberSelectionProvider {

	/**
	 * Promotions search results view ID.
	 */
	public static final String ID_PROMOTIONS_SEARCH_RESULTS_VIEW =
		"com.elasticpath.cmclient.store.promotions.views.PromotionsSearchResultsView"; //$NON-NLS-1$

	private static final String PROMOTIONS_SEARCH_RESULTS_TABLE = "Promotions Search Results"; //$NON-NLS-1$

	private static final Logger LOG = Logger.getLogger(PromotionsSearchResultsView.class);

	// Table Column Constants
	private static final int COLUMN_IMAGE = 0;

	private static final int COLUMN_PROMOTIONNAME = 1;

	private static final int COLUMN_PROMOTIONTYPE = 2;
	private static final int COLUMN_ENABLED = 3;
	private static final int COLUMN_EXPIRED = 4;
	private static final int COLUMN_ACTIVEFROM = 5;
	private static final int COLUMN_ACTIVETO = 6;
	private static final int COLUMN_DESCRIPTION = 7;

	private PromotionsSearchRequestJob promotionsSearchRequestJob;
	
	private static final Object[] EMPTY_ARRAY = new Object[0];

	private Object[] objects;

	private final ConditionHandler conditionHandler = new ConditionHandler();

	/**
	 * Constructs a <code>PromotionsSearchResultsView</code>.
	 */
	public PromotionsSearchResultsView() {
		super(true, PROMOTIONS_SEARCH_RESULTS_TABLE);
	}

	@Override
	protected Object getModel() {
		return null;
	}

	/**
	 * This label provider returns the text that should appear in each column for a given <code>Rule</code> object. This also returns the icon that
	 * should appear in the first column.
	 */
	private final class PromotionsSearchResultsViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			final Rule rule = (Rule) element;
			if (columnIndex == COLUMN_IMAGE) {
				if (rule.getRuleSet().getScenario() == RuleScenarios.CART_SCENARIO) {
					return PromotionsImageRegistry.getImage(PromotionsImageRegistry.PROMOTION_SHOPPING_CART_SMALL);
				} else if (rule.getRuleSet().getScenario() == RuleScenarios.CATALOG_BROWSE_SCENARIO) {
					return PromotionsImageRegistry.getImage(PromotionsImageRegistry.PROMOTION_CATALOG_SMALL);
				}
				return null;
			}
			return null;
		}

		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			String result = ""; //$NON-NLS-1$
			final Rule rule = (Rule) element;

			switch (columnIndex) {
			case COLUMN_PROMOTIONNAME:
				result = rule.getName();
				break;
			case COLUMN_PROMOTIONTYPE:
				if (rule.getRuleSet().getScenario() == RuleScenarios.CART_SCENARIO) {
					result = PromotionsMessages.get().SearchView_Filters_ShoppingCartPromotionType;
				} else if (rule.getRuleSet().getScenario() == RuleScenarios.CATALOG_BROWSE_SCENARIO) {
					result = PromotionsMessages.get().SearchView_Filters_CatalogPromotionType;
				}			
				break;
			case COLUMN_ENABLED:
				if (rule.isEnabled()) {
					result = CoreMessages.get().YesNoForBoolean_true;
				} else {
					result = CoreMessages.get().YesNoForBoolean_false;
				}
				break;
			case COLUMN_EXPIRED:
				if (rule.isWithinDateRange()) {
					result = CoreMessages.get().YesNoForBoolean_false;
				} else {
					result = CoreMessages.get().YesNoForBoolean_true;
				}
				break;
			case COLUMN_ACTIVEFROM:
				if (rule.getStartDate() == null) {
					TimeConditionModelAdapter timeModelAdapter = this.getModelAdapter(rule.getSellingContext());
					if (timeModelAdapter != null && timeModelAdapter.getStartDate() != null) {
						result = DateTimeUtilFactory.getDateUtil().formatAsDate(timeModelAdapter.getStartDate());
					}
				} else {
					result = DateTimeUtilFactory.getDateUtil().formatAsDate(rule.getStartDate());
				}
				break;
			case COLUMN_ACTIVETO:
				if (rule.getEndDate() == null) {
					TimeConditionModelAdapter timeModelAdapter = this.getModelAdapter(rule.getSellingContext());
					if (timeModelAdapter != null && timeModelAdapter.getEndDate() != null) {
						result = DateTimeUtilFactory.getDateUtil().formatAsDate(timeModelAdapter.getEndDate());
					}
				} else {
					result = DateTimeUtilFactory.getDateUtil().formatAsDate(rule.getEndDate());
				}
				break;
			case COLUMN_DESCRIPTION:
				result = rule.getDescription();
				break;
				
			default:
			}
			return result;
		}
		private TimeConditionModelAdapter getModelAdapter(final SellingContext sellingContext) {
			if (sellingContext == null) {
				return null;
			}

			TimeConditionModelAdapter timeModelAdapter = null;
			ConditionalExpression timeConditionalExpression = sellingContext.getCondition(TagDictionary.DICTIONARY_TIME_GUID);
			if (timeConditionalExpression != null) {
				LogicalOperator timeLogicalOperator = 
					conditionHandler.convertConditionExpressionStringToLogicalOperator(timeConditionalExpression);
				timeModelAdapter = new TimeConditionModelAdapterImpl(timeLogicalOperator);
			}
			return timeModelAdapter;
		}
	}

	@Override
	public void searchResultsUpdate(final SearchResultEvent<Rule> event) {
		promotionsSearchRequestJob = (PromotionsSearchRequestJob) event.getSource();
		getViewer().getTable().getDisplay().syncExec(() -> {
			PromotionsSearchResultsView.this.objects = event.getItems().toArray();
			PromotionsSearchResultsView.this.setResultsCount(event.getTotalNumberFound());
			PromotionsSearchResultsView.this.getViewer().getTable().clearAll();

			if (event.getItems().isEmpty() && event.getStartIndex() <= 0) {
				PromotionsSearchResultsView.this.showMessage(CoreMessages.get().NoSearchResultsError);
			} else {
				PromotionsSearchResultsView.this.hideErrorMessage();
			}

			PromotionsSearchResultsView.this.setResultsStartIndex(event.getStartIndex());
			PromotionsSearchResultsView.this.refreshViewerInput();
			PromotionsSearchResultsView.this.updateNavigationComponents();
			PromotionsSearchResultsView.this.updateSortingOrder(promotionsSearchRequestJob.getSearchCriteria());
		});
	}

	@Override
	public void promotionChanged(final PromotionsChangeEvent event) {
		final Rule changedPromotionRule = event.getPromotionRule();
		for (final TableItem currTableItem : getViewer().getTable().getItems()) {
			final Rule currPromotionRule = (Rule) currTableItem.getData();
			if (currPromotionRule.getUidPk() == changedPromotionRule.getUidPk()) {
				currTableItem.setData(changedPromotionRule);
				getViewer().update(changedPromotionRule, null);
				break;
			}
		}
	}

	@Override
	protected Object[] getViewInput() {
		if (objects == null) {
			return new Object[0];
		}
		
		return objects.clone();
	}

	@Override
	protected ITableLabelProvider getViewLabelProvider() {
		return new PromotionsSearchResultsViewLabelProvider();
	}

	@Override
	protected void initializeTable(final IEpTableViewer epTableViewer) {
		final String[] columnNames = new String[] {
				"", //$NON-NLS-1$
				PromotionsMessages.get().SearchResultsView_TableColumnTitle_PromotionName,
				PromotionsMessages.get().SearchResultsView_TableColumnTitle_PromotionType,
				PromotionsMessages.get().SearchResultsView_TableColumnTitle_Enabled,
				PromotionsMessages.get().SearchResultsView_TableColumnTitle_Expired,
				PromotionsMessages.get().SearchResultsView_TableColumnTitle_ActiveFrom,
				PromotionsMessages.get().SearchResultsView_TableColumnTitle_ActiveTo,
				PromotionsMessages.get().SearchResultsView_TableColumnTitle_Description, };
		final int[] columnWidths = new int[] { 25, 250, 60, 60, 55, 110, 110, 350 };
		final SortBy[] sortBy = new SortBy[] { 
				null,
				StandardSortBy.PROMOTION_NAME, 
				StandardSortBy.PROMOTION_TYPE, 
				StandardSortBy.PROMOTION_STATE,
				null,
				StandardSortBy.PROMOTION_ENABLE_DATE, 
				StandardSortBy.PROMOTION_EXPIRATION_DATE,
				null };
		for (int i = 0; i < columnNames.length; i++) {
			IEpTableColumn tableColumn = epTableViewer.addTableColumn(columnNames[i], columnWidths[i]);
			registerTableColumn(tableColumn, sortBy[i]);
		}
		
		getSite().setSelectionProvider(epTableViewer.getSwtTableViewer());
	}

	@Override
	protected String getPluginId() {
		return StorePlugin.PLUGIN_ID;
	}

	@Override
	protected void initializeViewToolbar() {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Initialize the promotions search results view."); //$NON-NLS-1$
		}

		final OpenPromotionsEditorAction action = new OpenPromotionsEditorAction(getViewer(), this.getSite());
		addDoubleClickAction(action);
	}

	@Override
	protected void navigateFirst() {
		getViewer().setInput(EMPTY_ARRAY);
		super.navigateFirst();

		promotionsSearchRequestJob.executeSearchFromIndex(getShell(), getResultsStartIndex());
		refreshViewerInput();
	}
	
	private Shell getShell() {
		return getViewer().getControl().getShell();
	}

	@Override
	protected void navigateNext() {
		getViewer().setInput(EMPTY_ARRAY);
		super.navigateNext();

		promotionsSearchRequestJob.executeSearchFromIndex(getShell(), getResultsStartIndex());
		refreshViewerInput();
	}

	@Override
	protected void navigatePrevious() {
		getViewer().setInput(EMPTY_ARRAY);
		super.navigatePrevious();

		promotionsSearchRequestJob.executeSearchFromIndex(getShell(), getResultsStartIndex());
		refreshViewerInput();
	}

	@Override
	protected void navigateLast() {
		getViewer().setInput(EMPTY_ARRAY);
		super.navigateLast();

		promotionsSearchRequestJob.executeSearchFromIndex(getShell(), getResultsStartIndex());
		refreshViewerInput();
	}

	@Override
	protected void navigateTo(final int pageNumber) {
		getViewer().setInput(EMPTY_ARRAY);

		promotionsSearchRequestJob.executeSearchFromIndex(getShell(), getStartIndexByPageNumber(pageNumber, getResultsPaging()));
		refreshViewerInput();
	}
	
	@Override
	public void dispose() {
		super.dispose();
		PromotionsEventService.getInstance().unregisterPromotionListener(this);
	}

	@Override
	public void init(final IViewSite site) throws PartInitException {
		super.init(site);
		PromotionsEventService.getInstance().registerPromotionsEventListener(this);
	}
	
	@Override
	public AbstractSearchRequestJob < ? extends Persistable > getSearchRequestJob() {
		return promotionsSearchRequestJob;
	}

	@Override
	public Object resolveObjectMember(final Object changeSetObjectSelection) {
		return changeSetObjectSelection;
	}

	@Override
	public void couponSearchResultsUpdate(final SearchResultEvent<Coupon> event) {
		// do nothing as this view does not display coupons.		
	}

	@Override
	protected String getPartId() {
		return ID_PROMOTIONS_SEARCH_RESULTS_VIEW;
	}
}