/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.conditionbuilder.wizard.conditioncomposite;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;

import com.elasticpath.cmclient.conditionbuilder.component.TopLevelComposite;
import com.elasticpath.cmclient.conditionbuilder.impl.tag.ConditionBuilderFactoryImpl;
import com.elasticpath.cmclient.conditionbuilder.plugin.ConditionBuilderMessages;
import com.elasticpath.cmclient.conditionbuilder.wizard.model.ShopperConditionModelAdapter;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.tags.domain.LogicalOperator;
import com.elasticpath.tags.domain.LogicalOperatorType;

/**
 *  
 */
public class ShopperConditionComposite {

	private static final int HEIGHT_HINT_250 = 250;

	private final ScrolledComposite scrolledComposite; 
	
	/**
	 * Constructor.
	 *
	 * @param modelAdapter - data model.
	 * @param mainComposite - main composite on which this composite will be located.
	 * @param dictionary - this composite dictionary.
	 * @param dataBindingContext - data binding context
	 */
	public ShopperConditionComposite(final ShopperConditionModelAdapter modelAdapter,
			final Composite mainComposite, final String dictionary, final DataBindingContext dataBindingContext) {
		
		ConditionBuilderFactoryImpl conditionBuilderFactory = new ConditionBuilderFactoryImpl();
		conditionBuilderFactory.setLocale(CorePlugin.getDefault().getDefaultLocale());
		conditionBuilderFactory.setDataBindingContext(dataBindingContext);
		conditionBuilderFactory.setAddButtonText("ConditionBuilder_AddConditionButton"); 		 //$NON-NLS-1$
		conditionBuilderFactory.setConditionBuilderTitle("ConditionBuilder_Title"); //$NON-NLS-1$
		conditionBuilderFactory.setTagDictionary(dictionary);

		conditionBuilderFactory.getResourceAdapterFactory().setResourceAdapterForLogicalOperator(
            object -> {
				//TODO Shall we put OR  AND etc localization into DB ?
				return ConditionBuilderMessages.get().getMessage(object.getMessageKey());
			});
		
		GridData layoutData = new GridData(GridData.FILL, GridData.FILL, true, true);
		layoutData.heightHint = HEIGHT_HINT_250;
		Layout layout = new GridLayout();

		scrolledComposite = new ScrolledComposite(mainComposite, SWT.FLAT | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setLayoutData(layoutData);
		scrolledComposite.setLayout(layout);
		
		LogicalOperator logicalOperator = modelAdapter.getModel();
		
		final TopLevelComposite<LogicalOperator, LogicalOperatorType>  topLevelComposite = conditionBuilderFactory.createFullUiFromModel(
				scrolledComposite,
				SWT.FLAT, 
				logicalOperator);
		
		conditionBuilderFactory.setListenerForRefreshParentComposite(
            object -> {
				scrolledComposite.setRedraw(false);

				topLevelComposite.layout();
				Rectangle rect = scrolledComposite.getClientArea();
				scrolledComposite.setMinSize(topLevelComposite.computeSize(rect.width, SWT.DEFAULT));

				scrolledComposite.layout();
				scrolledComposite.setRedraw(true);
			});
		
		
		
		scrolledComposite.setContent(topLevelComposite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.addControlListener(new ControlAdapter() {
			public void controlResized(final ControlEvent event) {
				Rectangle rect = scrolledComposite.getClientArea();
				scrolledComposite.setMinSize(topLevelComposite.computeSize(rect.width, SWT.DEFAULT));
			}
		});
	}

}
