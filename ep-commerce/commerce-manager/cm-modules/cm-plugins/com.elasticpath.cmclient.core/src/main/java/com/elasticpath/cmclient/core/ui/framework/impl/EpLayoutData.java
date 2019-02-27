/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.ui.framework.impl;

import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;

/**
 * Constructs new EP layout data.
 */
public class EpLayoutData implements IEpLayoutData {

	private final int horizontalAlignment;

	private final int verticalAlignment;

	private final boolean grabExcessHorizontalSpace;

	private final boolean grabExcessVerticalSpace;

	private final int horizontalSpan;

	private final int verticalSpan;

	private final AbstractEpLayoutComposite epLayoutComposite;

	/**
	 * Constructs the data.
	 * 
	 * @param epLayoutComposite the EP layout composite the current layout data is created from.
	 * @param horizontalAlignment alignment type
	 * @param verticalAlignment alignment type
	 * @param grabExcessHorizontalSpace grabs excess horizontal space
	 * @param grabExcessVerticalSpace grabs excess vertical space
	 * @param horizontalSpan columns span
	 * @param verticalSpan rows span
	 */
	public EpLayoutData(final AbstractEpLayoutComposite epLayoutComposite, final int horizontalAlignment, final int verticalAlignment,
			final boolean grabExcessHorizontalSpace, final boolean grabExcessVerticalSpace, final int horizontalSpan, final int verticalSpan) {
		this.epLayoutComposite = epLayoutComposite;
		this.horizontalAlignment = horizontalAlignment;
		this.verticalAlignment = verticalAlignment;
		this.grabExcessHorizontalSpace = grabExcessHorizontalSpace;
		this.grabExcessVerticalSpace = grabExcessVerticalSpace;
		this.horizontalSpan = horizontalSpan;
		this.verticalSpan = verticalSpan;
	}

	/**
	 * Constructs layout data.
	 * 
	 * @param epLayoutComposite the EP layout composite the current layout data is created from.
	 * @param horizontalAlignment alignment type
	 * @param verticalAlignment alignment type
	 * @param grabExcessHorizontalSpace grabs excess horizontal space
	 * @param grabExcessVerticalSpace grabs excess vertical space
	 */
	public EpLayoutData(final AbstractEpLayoutComposite epLayoutComposite, final int horizontalAlignment, final int verticalAlignment,
			final boolean grabExcessHorizontalSpace, final boolean grabExcessVerticalSpace) {
		this(epLayoutComposite, horizontalAlignment, verticalAlignment, grabExcessHorizontalSpace, grabExcessVerticalSpace, 1, 1);
	}

	/**
	 * Constructs layout data.
	 * 
	 * @param epLayoutComposite the EP layout composite the current layout data is created from.
	 * @param horizontalAlignment alignment type
	 * @param verticalAlignment alignment type
	 */
	public EpLayoutData(final AbstractEpLayoutComposite epLayoutComposite, final int horizontalAlignment, final int verticalAlignment) {
		this(epLayoutComposite, horizontalAlignment, verticalAlignment, false, false, 1, 1);
	}

	/**
	 * Constructs default layout data.
	 * 
	 * @param epLayoutComposite the EP layout composite the current layout data is created from.
	 */
	public EpLayoutData(final AbstractEpLayoutComposite epLayoutComposite) {
		this(epLayoutComposite, IEpLayoutData.FILL, IEpLayoutData.FILL, false, false, 1, 1);
	}

	/**
	 * Gets the horizontal alignment value.
	 * 
	 * @return the horizontalAlignment int
	 */
	protected int getHorizontalAlignment() {
		return this.horizontalAlignment;
	}

	/**
	 * Gets the vertical alignment value.
	 * 
	 * @return the verticalAlignment int
	 */
	protected int getVerticalAlignment() {
		return this.verticalAlignment;
	}

	/**
	 * Gets the boolean for taking the extra space.
	 * 
	 * @return the grabExcessHorizontalSpace boolean
	 */
	protected boolean isGrabExcessHorizontalSpace() {
		return this.grabExcessHorizontalSpace;
	}

	/**
	 * Gets the boolean for taking the extra space.
	 * 
	 * @return the grabExcessVerticalSpace boolean
	 */
	protected boolean isGrabExcessVerticalSpace() {
		return this.grabExcessVerticalSpace;
	}

	/**
	 * Gets the columns span.
	 * 
	 * @return the horizontalSpan int
	 */
	protected int getHorizontalSpan() {
		return this.horizontalSpan;
	}

	/**
	 * Gets the rows span.
	 * 
	 * @return the verticalSpan int
	 */
	protected int getVerticalSpan() {
		return this.verticalSpan;
	}

	/**
	 * Gets the native SWT layout data, adapted by the parent EP layout composite.
	 * 
	 * @return layout data object - <code>GridData</code> or <code>TableWrapData</code>
	 */
	public Object getSwtLayoutData() {
		return this.epLayoutComposite.adaptEpLayoutData(this);
	}

}
