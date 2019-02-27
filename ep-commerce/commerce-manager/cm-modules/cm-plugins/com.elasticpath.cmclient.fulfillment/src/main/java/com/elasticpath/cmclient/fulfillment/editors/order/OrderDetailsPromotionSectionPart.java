/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.editors.order;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPageSectionPart;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableColumn;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.rules.AppliedCoupon;
import com.elasticpath.domain.rules.AppliedRule;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.service.rules.RuleService;

/**
 * Represents the UI of customer details address.
 */
public class OrderDetailsPromotionSectionPart extends AbstractCmClientEditorPageSectionPart implements SelectionListener {

	private static final int ORDER_PROMOTIONS_TABLE_HEIGHT = 200;

	private static final int COLUMN_WIDTH_PROMOTION_NAME = 120;

	private static final int COLUMN_WIDTH_DESCRIPTION = 444;

	private static final int COLUMN_WIDTH_PROMOTION_TYPE = 120;

	private static final int COLUMN_WIDTH_PROMOTION_COUPON = 120;
	
	private static final int COLUMN_WIDTH_PROMOTION_COUPON_USAGE = 60;

	private static final int COLUMN_WIDTH_PROMOTION_DISPLAY_NAME = 200;

	private static final String ORDER_PROMOTIONS_TABLE = "Order Promotions Table"; //$NON-NLS-1$

	private final Order order;

	private IEpTableViewer orderPromotionsTable;

	/**
	 * Constructor.
	 * 
	 * @param formPage the formpage
	 * @param editor the CmClientFormEditor that contains the form
	 */
	public OrderDetailsPromotionSectionPart(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor, ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
		this.order = (Order) editor.getModel();
	}

	private Rule retrieveRule(final AppliedRule appliedRule) {
		RuleService ruleService = ServiceLocator.getService(ContextIdNames.RULE_SERVICE);
		return ruleService.get(appliedRule.getRuleUid());
	}

	@Override
	protected void createControls(final Composite client, final FormToolkit toolkit) {
		final IEpLayoutComposite mainPane = CompositeFactory.createTableWrapLayoutComposite(client, 1, false);
		mainPane.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL));

		final IEpLayoutData tableData = mainPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);

		if (order.getAppliedRules().size() > 0) {
			orderPromotionsTable = mainPane.addTableViewer(false, EpState.READ_ONLY, tableData, ORDER_PROMOTIONS_TABLE);
			((TableWrapData) orderPromotionsTable.getSwtTable().getLayoutData()).maxHeight = ORDER_PROMOTIONS_TABLE_HEIGHT;
			orderPromotionsTable.getSwtTable().setLinesVisible(true);

			// PROMOTION TYPE COLUMN
			final IEpTableColumn promotionTypeColumn = orderPromotionsTable.addTableColumn(FulfillmentMessages.get().PromotionSection_PromotionType,
					COLUMN_WIDTH_PROMOTION_TYPE);
			promotionTypeColumn.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(final Object element) {
					final PromoDetail couponDetail = (PromoDetail) element;
					return couponDetail.getRuleType();
				}
			});
			
			// PROMOTION NAME COLUMN
			final IEpTableColumn promotionNameColumn = orderPromotionsTable.addTableColumn(FulfillmentMessages.get().PromotionSection_PromotionName,
					COLUMN_WIDTH_PROMOTION_NAME);
			promotionNameColumn.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(final Object element) {
					final PromoDetail couponDetail = (PromoDetail) element;
					return couponDetail.getRuleName();
				}
			});
			
			// PROMOTION DISPLAY NAME COLUMN
			final IEpTableColumn promotionDisplayNameColumn = orderPromotionsTable.addTableColumn(
					FulfillmentMessages.get().PromotionSection_PromotionDisplayName, COLUMN_WIDTH_PROMOTION_DISPLAY_NAME);
			promotionDisplayNameColumn.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(final Object element) {
					final PromoDetail couponDetail = (PromoDetail) element;
					return couponDetail.getPromoDisplayName();
				}
			});
			
			
			// PROMOTION DESCRIPTION COLUMN
			final IEpTableColumn promotionDescriptionColumn = orderPromotionsTable.addTableColumn(
					FulfillmentMessages.get().PromotionSection_PromotionDescription, COLUMN_WIDTH_DESCRIPTION);
			promotionDescriptionColumn.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(final Object element) {
					final PromoDetail couponDetail = (PromoDetail) element;
					return couponDetail.getRuleDescription();
				}
			});

			// PROMO/COUPON CODE COLUMN
			final IEpTableColumn couponCodeColumn = orderPromotionsTable.
					addTableColumn(FulfillmentMessages.get().PromotionSection_PromotionCouponCode, COLUMN_WIDTH_PROMOTION_COUPON);
			couponCodeColumn.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(final Object element) {
					final PromoDetail couponDetail = (PromoDetail) element;
					return couponDetail.getCouponCode();
				}
			});

			// COUPON CODE COLUMN
			final IEpTableColumn usageColumn = orderPromotionsTable.addTableColumn(FulfillmentMessages.get().PromotionSection_PromotionCouponUsage,
					COLUMN_WIDTH_PROMOTION_COUPON_USAGE);
			usageColumn.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(final Object element) {
					final PromoDetail couponDetail = (PromoDetail) element;
					return couponDetail.getUsageCount();
				}
			});
		}
	}

	@Override
	protected String getSectionDescription() {
		return FulfillmentMessages.get().PromotionSection_Description;
	}

	@Override
	protected String getSectionTitle() {
		return FulfillmentMessages.get().PromotionSection_Title;
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void populateControls() {
		if (order.getAppliedRules().size() > 0) {
			orderPromotionsTable.setContentProvider(new ArrayContentProvider());
			orderPromotionsTable.setInput(getDetails().toArray());
		}
	}

	private Collection<PromoDetail> getDetails() {
		Collection<PromoDetail> couponDetails = new ArrayList<>();
		for (AppliedRule appliedRule : this.order.getAppliedRules()) {
			Rule rule = retrieveRule(appliedRule);
			if (appliedRule.getAppliedCoupons().isEmpty()) {
				couponDetails.add(
						new PromoDetail(rule.getRuleSet().getName(), rule.getName(), rule.getDisplayName(order.getLocale()),
								rule.getDescription(), null, "") //$NON-NLS-1$
				);
			} else {
				for (AppliedCoupon couponCode : appliedRule.getAppliedCoupons()) {
					couponDetails.add(
						new PromoDetail(rule.getRuleSet().getName(), rule.getName(), rule.getDisplayName(order.getLocale()), 
								rule.getDescription(), couponCode.getCouponCode(), String.valueOf(couponCode.getUsageCount()))
					);
				}
			}
		}
		return couponDetails;
	}
	/**
	 * Not used.
	 * 
	 * @param event the event object
	 */
	@Override
	public void widgetDefaultSelected(final SelectionEvent event) {
		// do nothing
	}

	/**
	 * Invoked on selection event.
	 * 
	 * @param event the event object
	 */
	@Override
	public void widgetSelected(final SelectionEvent event) {
		// do nothing
	}
	
	@Override
	public void dispose() {
		getSection().dispose();
		super.dispose();
	}

	/**
	 * Promotion Detail helper class.
	 */
	private class PromoDetail {
		private final String couponCode;
		private final String usageCount;
		private final String ruleDescription;
		private final String ruleName;
		private final String promoDisplayName;
		private final String ruleType;
		
		PromoDetail(final String ruleType, final String ruleName, final String promoDisplayName, final String ruleDescription, 
				final String couponCode, final String usageCount) {
			this.ruleType = ruleType;
			this.ruleName = ruleName;
			this.promoDisplayName = promoDisplayName;
			this.ruleDescription = ruleDescription;
			this.couponCode = couponCode;
			this.usageCount = usageCount;
		}
		
		String getRuleType() {
			return ruleType;
		}

		String getRuleName() {
			return ruleName;
		}

		String getPromoDisplayName() {
			return promoDisplayName;
		}

		String getRuleDescription() {
			return ruleDescription;
		}

		String getCouponCode() {
			return couponCode;
		}

		String getUsageCount() {
			return usageCount;
		}
	}
}
