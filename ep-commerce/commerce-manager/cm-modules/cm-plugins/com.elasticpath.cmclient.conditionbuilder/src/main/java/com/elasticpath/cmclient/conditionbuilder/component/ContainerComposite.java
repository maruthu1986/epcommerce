/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.conditionbuilder.component;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;


/**
 * ContainerComposite is a container composite.
 * @param <MA> model type
 * @param <A> adapter type
 */
public class ContainerComposite<MA, A> extends BaseComposite<MA, A> {

	private Composite container;

	/**
	 * Default Constructor.
	 * @param parent parent composite
	 * @param swtStyle SWT style
	 * @param model model
	 */
	public ContainerComposite(final Composite parent, final int swtStyle, final MA model) {
		super(parent, swtStyle, model);
	}

	/**
	 * Default Constructor.
	 * @param parent parent composite
	 * @param swtStyle SWT style
	 * @param model model
	 * @param columnsNumber number columns in layout
	 */
	public ContainerComposite(final Composite parent, final int swtStyle, 
			final MA model,
			final int columnsNumber) {
		super(parent, swtStyle, model, columnsNumber);
	}

	/**
	 * Create container composite.
	 * @param parent parent 
	 * @param style SWT style
	 */
	protected void createContainerComposite(final Composite parent, final int style) {
		// container composite
		container = new Composite(parent, style) {
			public void layout(final boolean changed) {
				// if no children, then we force invisible mode for this composite
				int heightHint = SWT.DEFAULT;
				if (this.getChildren().length == 0) {
					heightHint = 0;
				}
				GridData gridData = (GridData) this.getLayoutData();
				gridData.heightHint = heightHint;
				//
				super.layout(changed);
			}
		};

		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 2;
		layout.verticalSpacing = 2;

		GridData layoutData = new GridData(GridData.FILL, GridData.BEGINNING, true, false);
		layoutData.verticalIndent = 1;
		layoutData.horizontalIndent = 1;

		container.setBackground(parent.getBackground());
		container.setLayout(layout);
		container.setLayoutData(layoutData);
	}

	/**
	 * Get the container composite.
	 * @return the container
	 */
	public Composite getContainerComposite() {
		return container;
	}
}
