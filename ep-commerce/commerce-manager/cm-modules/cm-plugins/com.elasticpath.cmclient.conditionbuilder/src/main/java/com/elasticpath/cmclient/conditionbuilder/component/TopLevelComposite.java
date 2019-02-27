/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.conditionbuilder.component;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.elasticpath.cmclient.conditionbuilder.adapter.LogicalOperatorModelAdapter;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;

/**
 * TopLevelComposite.
 * @param <M> model adapter type
 * @param <LOT> logical operator type
 */
public class TopLevelComposite<M, LOT>
	extends ContainerComposite<LogicalOperatorModelAdapter<M, LOT>, LogicalOperatorModelAdapter<M, LOT>> {

	private static final int COLUMNS_4 = 4;
	private String addButtonText; //NOPMD
	private final EpControlFactory controlFactory = EpControlFactory.getInstance();

	/**
	 * Default constructor.
	 * @param parent parent composite
	 * @param style SWT style
	 * @param model model adapter
	 * @param addButtonText add button text
	 */
	public TopLevelComposite(final Composite parent, final int style, final LogicalOperatorModelAdapter<M, LOT> model, final String addButtonText) {
		super(parent, style, model);

		this.createContainerComposite(this, style);
		
		this.addButtonText = addButtonText;
		
		this.createControlsComposite(this, style);
	}

	private Composite createControlsComposite(final Composite parent, final int style) {

		GridLayout layout = new GridLayout(COLUMNS_4, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 1;
		layout.marginWidth = 1;

		GridData layoutData = new GridData(GridData.FILL, GridData.BEGINNING, true, false);
		layoutData.verticalIndent = 1;
		layoutData.horizontalIndent = 1;

		// composite
		Composite composite = new Composite(parent, style);
		composite.setBackground(parent.getBackground());
		composite.setLayout(layout);
		composite.setLayoutData(layoutData);

		Button addStatementBlock = controlFactory.createButton(composite, this.addButtonText, style, EpControlFactory.EpState.EDITABLE);
		addStatementBlock.setImage(CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_ADD));
		addStatementBlock.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(final SelectionEvent event) {
				//empty
			}
			public void widgetSelected(final SelectionEvent event) {
				TopLevelComposite.this.fireEventForAdd(TopLevelComposite.this.getModel());
			}
		});

		return composite;
	}
}
