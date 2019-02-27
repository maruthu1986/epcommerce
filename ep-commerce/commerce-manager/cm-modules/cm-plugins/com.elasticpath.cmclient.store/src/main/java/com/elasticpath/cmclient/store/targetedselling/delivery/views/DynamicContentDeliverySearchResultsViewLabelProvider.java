/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.targetedselling.delivery.views;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.conditionbuilder.wizard.conditions.handlers.ConditionHandler;
import com.elasticpath.cmclient.conditionbuilder.wizard.model.TimeConditionModelAdapter;
import com.elasticpath.cmclient.conditionbuilder.wizard.model.impl.StoresConditionModelAdapterImpl;
import com.elasticpath.cmclient.conditionbuilder.wizard.model.impl.TimeConditionModelAdapterImpl;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingImageRegistry;
import com.elasticpath.cmclient.store.targetedselling.delivery.wizard.model.DynamicContentDeliveryModelAdapter;
import com.elasticpath.commons.util.Utility;
import com.elasticpath.commons.util.impl.ConverterUtils;
import com.elasticpath.domain.contentspace.ContentSpace;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.StoreState;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.domain.LogicalOperator;
import com.elasticpath.tags.domain.TagDictionary;

/**
 * Label provider for DynamicContentDeliverySearchResultsView.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class DynamicContentDeliverySearchResultsViewLabelProvider extends LabelProvider implements ITableLabelProvider {

	private static final int COLUMN_IMAGE = 0;

	private static final int COLUMN_NAME = 1;

	private static final int COLUMN_DESCRIPTION = 2;

	private static final int COLUMN_STORE = 3;

	private static final int COLUMN_CONTENT_SPACE = 4;

	private static final int COLUMN_START_DATE = 5;

	private static final int COLUMN_END_DATE = 6;

	private final Utility utility;

	private List<Store> allStores;
	
	private final ConditionHandler conditionHandler = new ConditionHandler();

	/**
	 * Constructor.
	 * 
	 * @param utility EP Utility
	 */
	public DynamicContentDeliverySearchResultsViewLabelProvider(final Utility utility) {
		super();
		this.utility = utility;
	}

	@Override
	public Image getColumnImage(final Object object, final int index) {
		switch (index) {
			case COLUMN_IMAGE:
				return TargetedSellingImageRegistry.getImage(TargetedSellingImageRegistry.IMAGE_DYNAMIC_CONTENT_DELIVERY_LIST);
			default:
				return null;

		}
	}

	@Override
	public String getColumnText(final Object object, final int index) {

		DynamicContentDeliveryModelAdapter model = (DynamicContentDeliveryModelAdapter) object;
		StringBuilder stringBuilder;
		String result = ""; //$NON-NLS-1$

		// time conditional expression
		TimeConditionModelAdapter timeModelAdapter = null;
		if (index == COLUMN_START_DATE || index == COLUMN_END_DATE) {
			ConditionalExpression timeConditionalExpression =
					model.getSellingContext().getCondition(TagDictionary.DICTIONARY_TIME_GUID);
			if (timeConditionalExpression != null) {
				LogicalOperator timeLogicalOperator = conditionHandler.convertConditionExpressionStringToLogicalOperator(timeConditionalExpression);
				timeModelAdapter = new TimeConditionModelAdapterImpl(timeLogicalOperator);
			}
		}

		switch (index) {
			case COLUMN_NAME:
				result = model.getDynamicContentDelivery().getName();
				break;
			case COLUMN_DESCRIPTION:
				result = model.getDynamicContentDelivery().getDescription();
				break;
			case COLUMN_STORE:
				stringBuilder = new StringBuilder();
				Collection<Store> storesToShow = getStores(model);

				for (Store store : storesToShow) {
					if (store.getStoreState().equals(StoreState.OPEN) || store.getStoreState().equals(StoreState.RESTRICTED)) {
						if (stringBuilder.length() != 0) {
							stringBuilder.append(',');
						}
						stringBuilder.append(store.getName());
					}
				}

				result = stringBuilder.toString();
				break;
			case COLUMN_CONTENT_SPACE:
				stringBuilder = new StringBuilder();
				for (ContentSpace contentspace : model.getDynamicContentDelivery().getContentspaces()) {
					if (stringBuilder.length() != 0) {
						stringBuilder.append(',');
					}
					stringBuilder.append(contentspace.getTargetId());
				}
				result = stringBuilder.toString();
				break;
			case COLUMN_START_DATE:
				if (timeModelAdapter != null && timeModelAdapter.getStartDate() != null) {
					result = ConverterUtils.date2String(timeModelAdapter.getStartDate(), utility.getDefaultLocalizedDateFormat());
				}
				break;
			case COLUMN_END_DATE:
				if (timeModelAdapter != null && timeModelAdapter.getEndDate() != null) {
					result = ConverterUtils.date2String(timeModelAdapter.getEndDate(), utility.getDefaultLocalizedDateFormat());
				}
				break;
			default:
		}
		result = StringUtils.defaultString(result);
		return result.replace("\r\n", " ").replace("\t", " ");
	}

	/**
	 * Set list of all stores.
	 * 
	 * @param allStores list of all stores
	 */
	public void setAllStores(final List<Store> allStores) {
		this.allStores = allStores;
	}

	private Collection<Store> getStores(final DynamicContentDeliveryModelAdapter dynamicContentDeliveryWrapper) {
		
		ConditionalExpression conditionalExpression = 
			dynamicContentDeliveryWrapper.getSellingContext().getCondition(TagDictionary.DICTIONARY_STORES_GUID);

		if (conditionalExpression == null) {
			return allStores;
		}
		LogicalOperator logicalOperator = conditionHandler.convertConditionExpressionStringToLogicalOperator(conditionalExpression);

		return new StoresConditionModelAdapterImpl(logicalOperator).getStores();
	}

}
